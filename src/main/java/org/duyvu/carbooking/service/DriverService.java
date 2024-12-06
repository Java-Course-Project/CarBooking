package org.duyvu.carbooking.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.entity.Driver;
import org.duyvu.carbooking.mapper.CoordinateToPointMapper;
import org.duyvu.carbooking.mapper.DriverRequestToDriverMapper;
import org.duyvu.carbooking.mapper.DriverToDriverResponseMapper;
import org.duyvu.carbooking.model.DriverStatus;
import org.duyvu.carbooking.model.request.DriverRequest;
import org.duyvu.carbooking.model.response.DriverResponse;
import org.duyvu.carbooking.repository.DriverRepository;
import org.duyvu.carbooking.utils.locking.DistributedUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriverService {
	private final DriverRepository driverRepository;

	private final PasswordEncoder passwordEncoder;

	private final GeometryFactory factory;

	private final DistributedUtils distributedUtils;

	public Page<DriverResponse> findAll(Pageable pageable) {
		return driverRepository.findAll(pageable).map(DriverToDriverResponseMapper.INSTANCE::map);
	}

	public DriverResponse findById(Long id) {
		if (!driverRepository.existsById(id)) {
			throw new EntityNotFoundException("Driver not found");
		}

		return DriverToDriverResponseMapper.INSTANCE.map(
				driverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Driver not found")));
	}

	@Transactional
	public Long save(DriverRequest request) {
		Driver driver = DriverRequestToDriverMapper.INSTANCE.map(request);
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
		distributedUtils.set("Booking-%s".formatted(id), isConfirmed);
		distributedUtils.unlock("Booking-%s".formatted(id));
		return id;
	}
}