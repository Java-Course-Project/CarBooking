package org.duyvu.carbooking.controller;

import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.model.request.CustomerRequest;
import org.duyvu.carbooking.model.response.CustomerResponse;
import org.duyvu.carbooking.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {
	private final CustomerService customerService;

	@GetMapping("/{customer_id}")
	@PreAuthorize("hasRole('ADMIN') OR (hasRole('CUSTOMER') AND authentication.credentials == id)")
	public ResponseEntity<CustomerResponse> findById(@PathVariable("customer_id") Long id) {
		return ResponseEntity.ok(customerService.findBy(id));
	}

	@GetMapping("")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Page<CustomerResponse>> findAll(Pageable pageable) {
		return ResponseEntity.ok(customerService.findAll(pageable));
	}

	@PostMapping("")
	@PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
	public ResponseEntity<Long> save(CustomerRequest request) {
		return ResponseEntity.ok(customerService.save(request));
	}

	@PutMapping("/{customer_id}")
	@PreAuthorize("hasRole('ADMIN') OR (hasRole('CUSTOMER') AND authentication.credentials == id)")
	public ResponseEntity<Long> update(@PathVariable("customer_id") Long id, CustomerRequest request) {
		return ResponseEntity.ok(customerService.update(id, request));
	}
}