package org.duyvu.carbooking.service;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.duyvu.carbooking.mapper.BookingRequestToBookingInfoMapper;
import org.duyvu.carbooking.message.MessageTransfer;
import org.duyvu.carbooking.model.AssignationInfo;
import org.duyvu.carbooking.model.BookingInfo;
import org.duyvu.carbooking.model.CustomerStatus;
import org.duyvu.carbooking.model.DriverStatus;
import org.duyvu.carbooking.model.Message;
import org.duyvu.carbooking.model.RideTransactionStatus;
import org.duyvu.carbooking.model.request.BookingRequest;
import org.duyvu.carbooking.model.request.RideTransactionRequest;
import org.duyvu.carbooking.utils.locking.DistributedUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static org.duyvu.carbooking.model.AssignationInfo.AssignationStatus.CONFIRMED;
import static org.duyvu.carbooking.model.AssignationInfo.AssignationStatus.DENIED;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
	private final CustomerService customerService;

	private final RideTransactionService rideTransactionService;

	private final MessageTransfer messageTransfer;

	private final TransactionTemplate transactionTemplate;

	private final DriverService driverService;

	private final DistributedUtils distributedUtils;

	private static final Duration TIMEOUT = Duration.ofSeconds(30);

	@PostConstruct
	public void init() {
		startListenerThread();
	}

	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public Long book(BookingRequest bookingRequest) throws InterruptedException, TimeoutException {
		// make sure that current customer is free to book
		Long customerId = customerService.findIdBy(bookingRequest.getCustomerId(), CustomerStatus.NOT_BOOKED);
		customerService.updateStatus(customerId, CustomerStatus.BOOKED);

		List<Long> blacklistDriverIds = new ArrayList<>();
		int priority = 0;

		Instant startTime = Instant.now();
		Message<AssignationInfo> responseMessage = null;
		Long rideTransactionId = null;

		while (Duration.between(startTime, Instant.now()).compareTo(TIMEOUT) < 0) {
			Message<BookingInfo> message = new Message<>(BookingRequestToBookingInfoMapper.INSTANCE.map(bookingRequest, blacklistDriverIds),
														 Instant.now(), Map.of("id", UUID.randomUUID().toString()), priority);
			messageTransfer.send("CUSTOMER_BOOKING", message);

			responseMessage
					= (Message<AssignationInfo>) messageTransfer.receive("DRIVER_CONFIRMATION",
																		 s -> s.equals("id") ? message.getHeaders().get("id") : "", 2);

			if (responseMessage != null && responseMessage.getData() != null) {
				rideTransactionId = responseMessage.getData().getRideTransactionId();
				if (responseMessage.getData().getAssignationStatus().equals(AssignationInfo.AssignationStatus.DENIED)) {
					blacklistDriverIds.add(responseMessage.getData().getDriverId());
				}
			}
		}

		if (responseMessage == null) {
			throw new TimeoutException("No driver found for this ride");
		}

		customerService.updateStatus(customerId, CustomerStatus.DRIVER_ASSIGNED);
		return rideTransactionId;
	}

	@SuppressWarnings("unchecked")
	private void startListenerThread() {
		new Thread(() -> {
			final ExecutorService executor = Executors.newFixedThreadPool(100);
			while (true) {
				try {
					Message<BookingInfo> message = (Message<BookingInfo>) messageTransfer.receive("CUSTOMER_BOOKING");
					if (message != null) {
						executor.submit(() -> handleBookingRequest(message));
					}
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}).start();
	}

	@SneakyThrows
	private void handleBookingRequest(Message<BookingInfo> message) {
		transactionTemplate.executeWithoutResult((status -> {
			try {
				Long id = driverService.findShortestAvailableDriver(message.getData().getStartLocation());
				if (id == null) {
					return;
				}
				log.debug("Finding driver {}", id);
				driverService.updateStatus(id, DriverStatus.WAIT_FOR_CONFIRMATION);

				Long rideTransactionId = rideTransactionService.save(RideTransactionRequest.builder()
																						   .driverId(id)
																						   .customerId(message.getData().getCustomerId())
																						   .startLocation(
																								   message.getData().getStartLocation())
																						   .destinationLocation(
																								   message.getData()
																										  .getDestinationLocation())
																						   .build());
				rideTransactionService.updateStatus(rideTransactionId, RideTransactionStatus.WAIT_FOR_CONFIRMATION);

				// Wait for confirmation from driver.
				distributedUtils.set("Booking-%s".formatted(id), false);
				distributedUtils.lock("Booking-%s".formatted(id), 10);

				AssignationInfo.AssignationStatus assignationStatus =
						// if driver status = WAIT_FOR_CONFIRMATION then driver not confirmed or denied
						distributedUtils.get("Booking-%s".formatted(id)) ? CONFIRMED : DENIED;
				log.debug("Driver confirmation status {}", assignationStatus);
				if (assignationStatus.equals(CONFIRMED)) {
					driverService.updateStatus(id, DriverStatus.ASSIGNED);
					rideTransactionService.updateStatus(rideTransactionId, RideTransactionStatus.ASSIGNED);
				}

				Message<AssignationInfo> msg = new Message<>(AssignationInfo.builder()
																			.assignationStatus(assignationStatus)
																			.driverId(id)
																			.rideTransactionId(rideTransactionId)
																			.build(),
															 Instant.now(), Map.of("id", message.getHeaders().get("id")), 0);

				messageTransfer.send("DRIVER_CONFIRMATION", msg);
			} catch (Exception e) {
				log.error("", e);
				status.setRollbackOnly();
			}
		}));
	}

}
