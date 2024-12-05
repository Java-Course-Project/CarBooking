package org.duyvu.carbooking.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.mapper.BookingRequestToBookingInfoMapper;
import org.duyvu.carbooking.message.MessageTransfer;
import org.duyvu.carbooking.model.AssignationInfo;
import org.duyvu.carbooking.model.BookingInfo;
import org.duyvu.carbooking.model.CustomerStatus;
import org.duyvu.carbooking.model.Message;
import org.duyvu.carbooking.model.request.BookingRequest;
import org.duyvu.carbooking.model.request.RideTransactionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {
	private final CustomerService customerService;

	private final RideTransactionService rideTransactionService;

	private final MessageTransfer messageTransfer;

	private static final Duration TIMEOUT = Duration.ofSeconds(30);

	@SuppressWarnings("unchecked")
	@Transactional
	public Long book(BookingRequest bookingRequest) throws InterruptedException, TimeoutException {
		// make sure that current customer is free to book
		Long customerId = customerService.findIdBy(bookingRequest.getCustomerId(), CustomerStatus.NOT_BOOKED);
		customerService.updateStatus(customerId, CustomerStatus.BOOKED);

		List<Long> blacklistDriverIds = new ArrayList<>();
		int priority = 0;

		Instant startTime = Instant.now();
		Message<AssignationInfo> responseMessage = null;
		while (Duration.between(startTime, Instant.now()).compareTo(TIMEOUT) < 0) {
			Message<BookingInfo> message = new Message<>(BookingRequestToBookingInfoMapper.INSTANCE.map(bookingRequest, blacklistDriverIds),
														 Instant.now(), Map.of("id", UUID.randomUUID().toString()), priority);
			messageTransfer.send("CUSTOMER_BOOKING", message);

			responseMessage
					= (Message<AssignationInfo>) messageTransfer.receive("DRIVER_CONFIRMATION",
																		 s -> s.equals("id") ? message.getHeaders().get("id") : "", 2);

			if (responseMessage.getData() != null) {
				if (responseMessage.getData().getAssignationStatus().equals(AssignationInfo.AssignationStatus.DENIED)) {
					blacklistDriverIds.add(responseMessage.getData().getDriverId());
				}
				if (responseMessage.getData().getAssignationStatus().equals(AssignationInfo.AssignationStatus.CONFIRMED)) {
					break;
				}
			}
		}

		if (responseMessage == null) {
			throw new TimeoutException("No driver found for this ride");
		}

		customerService.updateStatus(customerId, CustomerStatus.DRIVER_ASSIGNED);
		return rideTransactionService.save(RideTransactionRequest.builder()
																 .driverId(responseMessage.getData().getDriverId())
																 .customerId(customerId)
																 .startLocation(bookingRequest.getStartLocation())
																 .destinationLocation(bookingRequest.getDestinationLocation())
																 .build());
	}

}
