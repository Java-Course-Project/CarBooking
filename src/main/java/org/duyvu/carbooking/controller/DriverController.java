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
	@PreAuthorize("hasRole('ADMIN') OR (hasRole('DRIVER') AND authentication.credentials == id)")
	public ResponseEntity<DriverResponse> findById(@PathVariable("driver_id") Long id) {
		return ResponseEntity.ok(driverService.findById(id));
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
	@PreAuthorize("hasRole('ADMIN') OR (hasRole('DRIVER') AND authentication.credentials == id)")
	public ResponseEntity<Long> update(@PathVariable("driver_id") Long id, @RequestBody @Valid DriverRequest request) {
		return ResponseEntity.ok(driverService.update(id, request));
	}

	@PatchMapping("/{driver_id}/location")
	@PreAuthorize("hasRole('ADMIN') OR (hasRole('DRIVER') AND authentication.credentials == id)")
	public ResponseEntity<Long> updateLocation(@PathVariable("driver_id") Long id, @RequestBody @Valid Coordinate coordinate) {
		return ResponseEntity.ok(driverService.updateLocation(id, coordinate));
	}
}