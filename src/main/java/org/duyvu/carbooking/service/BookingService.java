package org.duyvu.carbooking.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.duyvu.carbooking.exception.TimeoutException;
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

import static org.duyvu.carbooking.model.AssignationInfo.AssignationStatus.CONFIRMED;
import static org.duyvu.carbooking.model.AssignationInfo.AssignationStatus.DENIED;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
	private final CustomerService customerService;

	private final RideTransactionService rideTransactionService;

	private final MessageTransfer messageTransfer;

	private final DriverService driverService;

	private final DistributedUtils distributedUtils;

	private static final Duration TIMEOUT = Duration.ofSeconds(60);

	@Transactional(rollbackFor = Exception.class)
	public Long book(BookingRequest bookingRequest) throws InterruptedException {
		// make sure that current customer is free to book
		Long customerId = customerService.findIdBy(bookingRequest.getCustomerId(), CustomerStatus.NOT_BOOKED);
		customerService.updateStatus(customerId, CustomerStatus.BOOKED);

		int priority = 0;

		Instant startTime = Instant.now();

		while (Duration.between(startTime, Instant.now()).compareTo(TIMEOUT) < 0) {
			UUID key = UUID.randomUUID();
			Message<BookingInfo> message = new Message<>(BookingRequestToBookingInfoMapper.INSTANCE.map(bookingRequest),
														 Instant.now(), key, priority);
			messageTransfer.send(MessageTransfer.Topic.CUSTOMER_BOOKING, message);

			// Wait for message to return
			Message<BookingInfo> requestMessage =
					messageTransfer.receive(MessageTransfer.Topic.CUSTOMER_BOOKING, "x_custom_id = '%s'".formatted(key),
											Duration.ofSeconds(10));
			if (requestMessage != null) {
				AssignationInfo assignationInfo = handleBookingRequest(requestMessage);
				if (assignationInfo != null) {
					if (assignationInfo.getAssignationStatus().equals(CONFIRMED)) {
						driverService.updateStatus(assignationInfo.getDriverId(), DriverStatus.ASSIGNED);
						rideTransactionService.updateStatus(assignationInfo.getRideTransactionId(), RideTransactionStatus.ASSIGNED);
						customerService.updateStatus(customerId, CustomerStatus.DRIVER_ASSIGNED);
						return assignationInfo.getRideTransactionId();
					}
				}
			}
		}

		throw new TimeoutException("No driver found for this ride");
	}

	private AssignationInfo handleBookingRequest(Message<BookingInfo> message) throws InterruptedException {
		Long id = driverService.findShortestAvailableDriver(message.getData().getStartLocation());
		if (id == null) {
			return null;
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
		log.debug("Waiting for driver {} to confirm", id);
		distributedUtils.set("Booking-%s".formatted(id), false);
		distributedUtils.wait("Booking-%s".formatted(id), Duration.ofSeconds(10));

		log.debug("Driver {} confirmed {}", id, distributedUtils.get("Booking-%s".formatted(id)));
		AssignationInfo.AssignationStatus assignationStatus =
				// if driver status = WAIT_FOR_CONFIRMATION then driver not confirmed or denied
				distributedUtils.get("Booking-%s".formatted(id)) ? CONFIRMED : DENIED;
		log.debug("Driver confirmation status {}", assignationStatus);

		return AssignationInfo.builder()
							  .assignationStatus(assignationStatus)
							  .driverId(id)
							  .rideTransactionId(rideTransactionId)
							  .build();

	}

}
