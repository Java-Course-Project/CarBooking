package org.duyvu.carbooking.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.duyvu.carbooking.entity.Customer;
import org.duyvu.carbooking.entity.Driver;
import org.duyvu.carbooking.entity.RideTransaction;
import org.duyvu.carbooking.exception.InvalidStateException;
import org.duyvu.carbooking.mapper.CoordinateToPointMapper;
import org.duyvu.carbooking.mapper.DriverRequestToDriverMapper;
import org.duyvu.carbooking.mapper.DriverToDriverResponseMapper;
import org.duyvu.carbooking.model.CustomerStatus;
import org.duyvu.carbooking.model.DriverStatus;
import org.duyvu.carbooking.model.RideTransactionStatus;
import org.duyvu.carbooking.model.request.DriverRequest;
import org.duyvu.carbooking.model.response.DriverResponse;
import org.duyvu.carbooking.repository.CustomerRepository;
import org.duyvu.carbooking.repository.DriverRepository;
import org.duyvu.carbooking.repository.RideTransactionRepository;
import org.duyvu.carbooking.utils.distributed.DistributedLock;
import org.duyvu.carbooking.utils.distributed.DistributedObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverService {
	private final DriverRepository driverRepository;

	private final RideTransactionRepository rideTransactionRepository;

	private final CustomerRepository customerRepository;

	private final PasswordEncoder passwordEncoder;

	private final GeometryFactory factory;

	private final DistributedLock distributedLock;

	private final DistributedObject distributedObject;

	public Page<DriverResponse> findAll(Pageable pageable) {
		return driverRepository.findAll(pageable).map(DriverToDriverResponseMapper.INSTANCE::map);
	}

	public DriverResponse findById(Long id) {
		return DriverToDriverResponseMapper.INSTANCE.map(
				driverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Driver not found")));
	}

	@Transactional
	public Long save(DriverRequest request) {
		Driver driver = DriverRequestToDriverMapper.INSTANCE.map(request);
		driver.setPassword(passwordEncoder.encode(request.getPassword()));
		driver.setDriverStatus(DriverStatus.NOT_BOOKED);
		return driverRepository.save(driver).getId();
	}

	@Transactional
	public Long update(Long id, DriverRequest request) {
		if (driverRepository.existsById(id)) {
			throw new EntityNotFoundException("DriverService not found");
		}
		Driver driver = driverRepository.findByIdThenLock(id).orElseThrow(() -> new EntityNotFoundException("Driver not found"));
		driver.setEmail(request.getEmail());
		driver.setGender(request.getGender());
		driver.setDob(request.getDob());
		driver.setCitizenIdentificationNumber(request.getCitizenIdentificationNumber());
		driver.setUsername(request.getUsername());
		driver.setPassword(passwordEncoder.encode(request.getPassword()));

		driver.setDriverLicense(request.getDriverLicense());
		driver.setDriverStatus(driver.getDriverStatus());

		return driverRepository.save(driver).getId();
	}

	@Transactional
	public Long updateLocation(Long id, Coordinate location) {
		Driver driver = driverRepository.findByIdThenLock(id).orElseThrow(() -> new EntityNotFoundException("Driver not found"));
		driver.setLocation(CoordinateToPointMapper.INSTANCE.map(location, factory));
		if (DriverStatus.OFFLINE.equals(driver.getDriverStatus())) {
			driver.setDriverStatus(DriverStatus.NOT_BOOKED);
		}
		return driverRepository.save(driver).getId();
	}

	@Transactional
	public Long updateStatus(Long id, DriverStatus status) {
		Driver driver = driverRepository.findByIdThenLock(id).orElseThrow(() -> new EntityNotFoundException("Driver not found"));
		driver.setDriverStatus(status);
		return driverRepository.save(driver).getId();
	}

	@Transactional
	public Long findShortestAvailableDriver(Coordinate startLocation) {
		return driverRepository.findShortestAvailableDriverId(CoordinateToPointMapper.INSTANCE.map(startLocation, factory)).orElse(null);
	}

	@Transactional
	public Long confirmWaitingRideTransaction(Long id, boolean isConfirmed) {
		// Can't direct update driver here because of other process is holding lock to driver
		log.debug("Driver {} confirm {}", id, isConfirmed);
		distributedObject.set("Booking-%s".formatted(id), isConfirmed, Duration.ofSeconds(20));
		distributedLock.await("Booking-%s".formatted(id));
		return id;
	}

	@Transactional
	public Long pickRide(Long driverId) {
		if (!DriverStatus.ASSIGNED.equals(driverRepository.findByIdThenLock(driverId).map(Driver::getDriverStatus).orElse(null))) {
			throw new InvalidStateException("Driver is not assigned to any rides");
		}

		RideTransaction rideTransaction =
				rideTransactionRepository.findByDriverIdAndStatusThenLock(driverId, RideTransactionStatus.ASSIGNED).orElseThrow(
						() -> new EntityNotFoundException("Ride Transaction not found"));
		if (!RideTransactionStatus.ASSIGNED.equals(rideTransaction.getRideTransactionStatus())) {
			throw new InvalidStateException("Ride is not assigned to any driver");
		}
		rideTransaction.setRideTransactionStatus(RideTransactionStatus.ON_THE_WAY);

		Customer customer = customerRepository.findByIdThenLock(rideTransaction.getCustomer().getId())
											  .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
		if (!CustomerStatus.DRIVER_ASSIGNED.equals(customer.getCustomerStatus())) {
			throw new InvalidStateException("Customer is not assigned to any driver");
		}
		customer.setCustomerStatus(CustomerStatus.ON_THE_WAY);

		rideTransactionRepository.save(rideTransaction);
		customerRepository.save(customer);
		return updateStatus(driverId, DriverStatus.ON_THE_WAY);
	}

	@Transactional
	public Long finishRide(Long driverId) {
		if (!DriverStatus.ON_THE_WAY.equals(driverRepository.findByIdThenLock(driverId).map(Driver::getDriverStatus).orElse(null))) {
			throw new InvalidStateException("Driver is not assigned to any rides");
		}

		RideTransaction rideTransaction =
				rideTransactionRepository.findByDriverIdAndStatusThenLock(driverId, RideTransactionStatus.ON_THE_WAY).orElseThrow(
						() -> new EntityNotFoundException("Ride Transaction not found"));
		if (!RideTransactionStatus.ON_THE_WAY.equals(rideTransaction.getRideTransactionStatus())) {
			throw new InvalidStateException("Ride is not assigned to any driver");
		}

		Customer customer = customerRepository.findByIdThenLock(rideTransaction.getCustomer().getId())
											  .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
		if (!CustomerStatus.ON_THE_WAY.equals(customer.getCustomerStatus())) {
			throw new InvalidStateException("Customer is not assigned to any driver");
		}
		customer.setCustomerStatus(CustomerStatus.NOT_BOOKED);

		rideTransaction.setRideTransactionStatus(RideTransactionStatus.FINISHED);
		rideTransactionRepository.save(rideTransaction);
		customerRepository.save(customer);
		return updateStatus(driverId, DriverStatus.NOT_BOOKED);
	}

	@Scheduled(fixedDelay = 60000L)
	@Transactional
	public void setInactiveDriversToOffline() {
		final Duration timeout = Duration.ofSeconds(3600);
		final Instant inactiveTimeout = Instant.now().minus(timeout);

		int updated = driverRepository.setInactiveDriversToOffline(inactiveTimeout);
		log.debug("Updated {} drivers to offline", updated);
	}
}
