package org.duyvu.carbooking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.model.request.AdminRequest;
import org.duyvu.carbooking.model.response.AdminResponse;
import org.duyvu.carbooking.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins")
public class AdminController {
	private final AdminService adminService;

	@GetMapping("/{admin_id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<AdminResponse> findById(@PathVariable("admin_id") Long id) {
		return ResponseEntity.ok(adminService.findById(id));
	}

	@GetMapping("")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Page<AdminResponse>> findAll(Pageable pageable) {
		return ResponseEntity.ok(adminService.findAll(pageable));
	}

	@PostMapping("")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Long> save(@RequestBody @Valid AdminRequest adminRequest) {
		return ResponseEntity.ok(adminService.save(adminRequest));
	}

	@PutMapping("/{admin_id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Long> update(@PathVariable("admin_id") Long id, @RequestBody @Valid AdminRequest adminRequest) {
		return ResponseEntity.ok(adminService.update(id, adminRequest));
	}

}
