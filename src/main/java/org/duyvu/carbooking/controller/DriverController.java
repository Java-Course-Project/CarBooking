package org.duyvu.carbooking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.model.request.DriverRequest;
import org.duyvu.carbooking.model.response.DriverResponse;
import org.duyvu.carbooking.service.DriverService;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/drivers")
public class DriverController {
	private final DriverService driverService;

	@GetMapping("/{driver_id}")
	@PreAuthorize("hasRole('ADMIN') OR (hasRole('DRIVER') AND @jwtUtils.extractId(authentication.credentials).equals(#driverId))")
	public ResponseEntity<DriverResponse> findById(@PathVariable("driver_id") Long driverId) {
		return ResponseEntity.ok(driverService.findById(driverId));
	}

	@GetMapping("")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Page<DriverResponse>> findAll(Pageable pageable) {
		return ResponseEntity.ok(driverService.findAll(pageable));
	}

	@PostMapping("")
	@PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
	public ResponseEntity<Long> save(@RequestBody @Valid DriverRequest request) {
		return ResponseEntity.ok(driverService.save(request));
	}

	@PutMapping("/{driver_id}")
	@PreAuthorize("hasRole('ADMIN') OR (hasRole('DRIVER') AND @jwtUtils.extractId(authentication.credentials).equals(#driverId))")
	public ResponseEntity<Long> update(@PathVariable("driver_id") Long driverId, @RequestBody @Valid DriverRequest request) {
		return ResponseEntity.ok(driverService.update(driverId, request));
	}

	@PatchMapping("/{driver_id}/location")
	@PreAuthorize("hasRole('ADMIN') OR (hasRole('DRIVER') AND @jwtUtils.extractId(authentication.credentials).equals(#driverId))")
	public ResponseEntity<Long> updateLocation(@PathVariable("driver_id") Long driverId, @RequestBody @Valid Coordinate coordinate) {
		return ResponseEntity.ok(driverService.updateLocation(driverId, coordinate));
	}

	@PostMapping("/{driver_id}/confirm")
	@PreAuthorize("hasRole('ADMIN') OR (hasRole('DRIVER') AND @jwtUtils.extractId(authentication.credentials).equals(#driverId))")
	public ResponseEntity<Long> confirmWaitingRideTransaction(@PathVariable("driver_id") Long driverId,
															  @RequestBody boolean isConfirmed) throws InterruptedException {
		return ResponseEntity.ok(driverService.confirmWaitingRideTransaction(driverId, isConfirmed));
	}

	@PostMapping("/{driver_id}/pick")
	@PreAuthorize("hasRole('ADMIN') OR (hasRole('DRIVER') AND @jwtUtils.extractId(authentication.credentials).equals(#driverId))")
	public ResponseEntity<Long> pickRide(@PathVariable("driver_id") Long driverId) {
		return ResponseEntity.ok(driverService.pickRide(driverId));
	}

	@PostMapping("/{driver_id}/finish")
	@PreAuthorize("hasRole('ADMIN') OR (hasRole('DRIVER') AND @jwtUtils.extractId(authentication.credentials).equals(#driverId))")
	public ResponseEntity<Long> finishRide(@PathVariable("driver_id") Long driverId) {
		return ResponseEntity.ok(driverService.finishRide(driverId));
	}

}
