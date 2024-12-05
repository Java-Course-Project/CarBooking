package org.duyvu.carbooking.service.listener;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.duyvu.carbooking.message.MessageTransfer;
import org.duyvu.carbooking.model.AssignationInfo;
import org.duyvu.carbooking.model.BookingInfo;
import org.duyvu.carbooking.model.DriverStatus;
import org.duyvu.carbooking.model.Message;
import org.duyvu.carbooking.service.DriverService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import static org.duyvu.carbooking.model.AssignationInfo.AssignationStatus.CONFIRMED;
import static org.duyvu.carbooking.model.AssignationInfo.AssignationStatus.DENIED;

@Component
@Slf4j
@RequiredArgsConstructor
public class DriverListener {
	private final ExecutorService executor = Executors.newFixedThreadPool(100);

	private final MessageTransfer messageTransfer;

	private final TransactionTemplate transactionTemplate;

	private final DriverService driverService;

	@SuppressWarnings("unchecked")
	private void startListenerThread() {
		new Thread(() -> {
			while (true) {
				try {
					Message<BookingInfo> message = (Message<BookingInfo>) messageTransfer.receive("CUSTOMER_BOOKING");
					executor.submit(() -> handleBooking(message));
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}).start();
	}

	@SneakyThrows
	private void handleBooking(Message<BookingInfo> message) {
		Long driverId = transactionTemplate.execute((status -> {
			Long id = driverService.findShortestAvailableDriver(message.getData().getStartLocation());
			if (id == null) {
				return null;
			}
			return driverService.updateStatus(id, DriverStatus.WAIT_FOR_CONFIRMATION);
		}));

		if (driverId != null) {
			// Wait for confirmation from driver.
			// TODO: could use an await system for not wasting time (using redis lock - try redisson)
			Thread.sleep(5000L);
		}

		transactionTemplate.executeWithoutResult((status -> {
			AssignationInfo.AssignationStatus assignationStatus =
					// if driver status = WAIT_FOR_CONFIRMATION then driver not confirmed or denied
					DriverStatus.WAIT_FOR_CONFIRMATION.equals(driverService.findById(driverId).getDriverStatus()) ? DENIED : CONFIRMED;
			if (assignationStatus.equals(CONFIRMED)) {
				driverService.updateStatus(driverId, DriverStatus.ASSIGNED);
			}

			Message<AssignationInfo> msg = new Message<>(AssignationInfo.builder()
																		.assignationStatus(assignationStatus)
																		.driverId(driverId)
																		.build(),
														 Instant.now(), Map.of("id", message.getHeaders().get("id")), 0);

			messageTransfer.send("DRIVER_CONFIRMATION", msg);
		}));
	}

	@PostConstruct
	public void init() {
		startListenerThread();
	}
}
