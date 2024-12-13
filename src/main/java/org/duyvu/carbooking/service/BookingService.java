package org.duyvu.carbooking.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.duyvu.carbooking.exception.TimeoutException;
import org.duyvu.carbooking.mapper.BookingRequestToBookingInfoMapper;
import org.duyvu.carbooking.mapper.CoordinateToPointMapper;
import org.duyvu.carbooking.message.MessageTransfer;
import org.duyvu.carbooking.model.AssignationInfo;
import org.duyvu.carbooking.model.BookingInfo;
import org.duyvu.carbooking.model.CustomerStatus;
import org.duyvu.carbooking.model.DriverStatus;
import org.duyvu.carbooking.model.Message;
import org.duyvu.carbooking.model.RideTransactionStatus;
import org.duyvu.carbooking.model.request.BookingRequest;
import org.duyvu.carbooking.model.request.RideTransactionRequest;
import org.duyvu.carbooking.model.response.BookingResponse;
import org.duyvu.carbooking.utils.distributed.DistributedLock;
import org.duyvu.carbooking.utils.distributed.DistributedObject;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
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

	private final PlatformTransactionManager transactionManager;

	private final MessageTransfer messageTransfer;

	private final GeometryFactory geometryFactory;

	private final DriverService driverService;

	private final DistributedLock distributedLock;

	private final DistributedObject distributedObject;

	private static final Duration TIMEOUT = Duration.ofSeconds(60);

	@Transactional(rollbackFor = Exception.class)
	public Long book(BookingRequest bookingRequest) {
		// make sure that current customer is free to book
		Long customerId = customerService.findIdBy(bookingRequest.getCustomerId(), CustomerStatus.NOT_BOOKED);
		customerService.updateStatus(customerId, CustomerStatus.BOOKED);

		int priority = 0;

		// an invalid driver id so that the query is not null
		final List<Long> blacklistDriverIds = new ArrayList<>(List.of(-1L));

		Instant startTime = Instant.now();

		while (Duration.between(startTime, Instant.now()).compareTo(TIMEOUT) < 0) {
			UUID key = UUID.randomUUID();
			Message<BookingInfo> message = new Message<>(BookingRequestToBookingInfoMapper.INSTANCE.map(bookingRequest, blacklistDriverIds),
														 Instant.now(), key, ++priority);
			messageTransfer.send(MessageTransfer.Topic.CUSTOMER_BOOKING, message);

			// Wait for message to return
			Message<BookingInfo> requestMessage =
					messageTransfer.receive(MessageTransfer.Topic.CUSTOMER_BOOKING, "x_custom_id = '%s'".formatted(key),
											Duration.ofSeconds(10));
			if (requestMessage != null) {
				AssignationInfo assignationInfo = handleBookingRequest(requestMessage);
				if (assignationInfo != null) {
					distributedLock.await("Booking-Response-%s".formatted(assignationInfo.getDriverId()));
					blacklistDriverIds.add(assignationInfo.getDriverId());
					if (CONFIRMED.equals(assignationInfo.getAssignationStatus())) {
						driverService.updateStatus(assignationInfo.getDriverId(), DriverStatus.ASSIGNED);
						Long rideTransactionId = rideTransactionService.save(RideTransactionRequest.builder()
																								   .driverId(assignationInfo.getDriverId())
																								   .customerId(
																										   message.getData()
																												  .getCustomerId())
																								   .startLocation(
																										   message.getData()
																												  .getStartLocation())
																								   .destinationLocation(
																										   message.getData()
																												  .getDestinationLocation())
																								   .build());

						rideTransactionService.updateStatus(rideTransactionId, RideTransactionStatus.ASSIGNED);
						customerService.updateStatus(customerId, CustomerStatus.DRIVER_ASSIGNED);
						return rideTransactionId;
					}
				}
			}
		}

		throw new TimeoutException("No driver found for this ride");
	}

	private AssignationInfo handleBookingRequest(Message<BookingInfo> message) {
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setName("handleBookingRequest");
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		return transactionTemplate.execute((status) -> {
			Long id = driverService.findShortestAvailableDriver(message.getData().getStartLocation(), message.getData()
																											 .getBlacklistDriverIds());
			if (id == null) {
				return null;
			}

			log.debug("Finding driver {}", id);
			driverService.updateStatus(id, DriverStatus.ASSIGNED);
			Duration timeout = Duration.ofSeconds(10);

			distributedObject.set("Booking-ride-transaction-%s".formatted(id),
								  BookingResponse.builder()
												 .price(rideTransactionService.calculatePrice(
														 message.getData().getTransportationTypeId().intValue(),
														 CoordinateToPointMapper.INSTANCE.map(
																 message.getData().getStartLocation(), geometryFactory),
														 CoordinateToPointMapper.INSTANCE.map(message.getData().getDestinationLocation(),
																							  geometryFactory)))
												 .startLocation(message.getData().getStartLocation())
												 .destinationLocation(message.getData().getDestinationLocation())
												 .driverId(id)
												 .build()
					, timeout.multipliedBy(2));

			// Wait for confirmation from driver.
			log.debug("Waiting for driver {} to confirm", id);
			distributedObject.set("Booking-%s".formatted(id), false, timeout.multipliedBy(2));
			try {
				distributedLock.wait("Booking-%s".formatted(id), timeout);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			AssignationInfo.AssignationStatus assignationStatus =
					// if driver status = WAIT_FOR_CONFIRMATION then driver not confirmed or denied
					Boolean.TRUE.equals(distributedObject.get("Booking-%s".formatted(id))) ? CONFIRMED : DENIED;
			log.debug("Driver {} confirmation status {}", id, assignationStatus);

			distributedObject.delete("Booking-ride-transaction-%s".formatted(id));
			distributedObject.delete("Booking-%s".formatted(id));
			distributedLock.delete("Booking-%s".formatted(id));

			if (assignationStatus.equals(DENIED)) {
				status.setRollbackOnly();
				return null;
			}

			return AssignationInfo.builder()
								  .assignationStatus(assignationStatus)
								  .driverId(id)
								  .build();
		});
	}

}
