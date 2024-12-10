package org.duyvu.carbooking.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.entity.Customer_;
import org.duyvu.carbooking.entity.Driver_;
import org.duyvu.carbooking.entity.Fare;
import org.duyvu.carbooking.entity.RideTransaction;
import org.duyvu.carbooking.entity.RideTransaction_;
import org.duyvu.carbooking.exception.UnsupportedValue;
import org.duyvu.carbooking.mapper.RideTransactionRequestToRideTransactionMapper;
import org.duyvu.carbooking.mapper.RideTransactionToRideTransactionResponseMapper;
import org.duyvu.carbooking.model.RideTransactionStatus;
import org.duyvu.carbooking.model.UserType;
import org.duyvu.carbooking.model.request.RideTransactionRequest;
import org.duyvu.carbooking.model.response.RideTransactionResponse;
import org.duyvu.carbooking.repository.DriverRepository;
import org.duyvu.carbooking.repository.FareRepository;
import org.duyvu.carbooking.repository.RideTransactionRepository;
import org.duyvu.carbooking.utils.distributed.DistributedObject;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RideTransactionService {
	private final RideTransactionRepository rideTransactionRepository;

	private final DriverRepository driverRepository;

	private final FareRepository fareRepository;

	private final GeometryFactory geometryFactory;

	private final DistributedObject distributedObject;

	public static class SpecialDayTime {
		public static final Set<LocalDate> HOLIDAYS
				// Using an arbitrary year
				= Set.of(LocalDate.of(0, 1, 1),
						 LocalDate.of(0, 2, 10),
						 LocalDate.of(0, 2, 11),
						 LocalDate.of(0, 2, 12),
						 LocalDate.of(0, 2, 13),
						 LocalDate.of(0, 2, 14),
						 LocalDate.of(0, 2, 15),
						 LocalDate.of(0, 4, 30),
						 LocalDate.of(0, 5, 1),
						 LocalDate.of(0, 9, 2),
						 LocalDate.of(0, 9, 3));

		public static final Set<LocalTime> RUSH_HOURS
				= Set.of(LocalTime.of(17, 0), LocalTime.of(18, 0), LocalTime.of(19, 0));
	}

	private double calculatePrice(Integer transactionTypeId, Point startLocation, Point endLocation) {
		Fare fare = fareRepository.findByTransportationTypeId(transactionTypeId).orElseThrow(EntityNotFoundException::new);
		double price = fare.getPrice();
		price *= SpecialDayTime.HOLIDAYS.contains(LocalDate.now().withYear(0)) ? fare.getHolidayRate() : fare.getNormalDayRate();
		price *= SpecialDayTime.RUSH_HOURS.contains(LocalTime.now().withMinute(0).withSecond(0).withNano(0))
				 ? fare.getRushHourRate() : fare.getNormalHourRate();

		return price * (startLocation.distance(endLocation));
	}

	public Page<RideTransactionResponse> findAll(Long targetId, UserType userType, Pageable pageable) {
		Specification<RideTransaction> spec = (root, query, builder) -> builder.conjunction();
		switch (userType) {
			case CUSTOMER -> spec =
					spec.and((root, query, builder)
									 -> builder.equal(root.get(RideTransaction_.CUSTOMER).get(Customer_.ID), targetId));
			case DRIVER -> spec =
					spec.and((root, query, builder)
									 -> builder.equal(root.get(RideTransaction_.DRIVER).get(Driver_.ID), targetId));
			case ADMIN -> throw new UnsupportedValue("Not supported user type %s".formatted(userType));
		}
		return rideTransactionRepository.findAll(spec, pageable).map(RideTransactionToRideTransactionResponseMapper.INSTANCE::map);
	}

	public RideTransactionResponse findById(Long id) {
		return RideTransactionToRideTransactionResponseMapper.INSTANCE.map(
				rideTransactionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ride Transaction not found")));
	}

	@Transactional
	public Long save(RideTransactionRequest request) {
		RideTransaction rideTransaction = RideTransactionRequestToRideTransactionMapper.INSTANCE.map(request, geometryFactory);
		rideTransaction.setRideTransactionStatus(RideTransactionStatus.ASSIGNED);
		rideTransaction.setPrice(calculatePrice(
				Objects.requireNonNull(driverRepository.findById(request.getDriverId()).orElse(null)).getTransportationType().getId(),
				rideTransaction.getStartLocation(), rideTransaction.getDestinationLocation()));

		return rideTransactionRepository.save(rideTransaction).getId();
	}

	@SuppressWarnings("UnusedReturnValue")
	@Transactional
	public Long updateStatus(Long id, RideTransactionStatus status) {
		RideTransaction rideTransaction = rideTransactionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
				"Transaction not found"));

		rideTransaction.setRideTransactionStatus(status);
		return rideTransactionRepository.save(rideTransaction).getId();
	}

	public RideTransactionResponse findCurrentWaitingTransaction(Long driverId, Duration timeout) {
		Instant startTime = Instant.now();
		while (Duration.between(startTime, Instant.now()).compareTo(timeout) < 0) {
			// Can't select from db because ride transaction is in middle of transaction - changes can't be seen
			RideTransactionResponse response = distributedObject.get("Booking-ride-transaction-%s".formatted(driverId));
			if (response != null) {
				return response;
			}
		}
		return null;
	}
}
