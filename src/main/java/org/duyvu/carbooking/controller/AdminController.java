package org.duyvu.carbooking.controller;

import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.model.AdminResponse;
import org.duyvu.carbooking.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

}
