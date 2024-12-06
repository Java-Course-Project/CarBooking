package org.duyvu.carbooking.controller;

import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.model.response.RideTransactionResponse;
import org.duyvu.carbooking.service.RideTransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ride-transactions")
public class RideTransactionController {
	private final RideTransactionService rideTransactionService;

	@GetMapping("/{ride-transaction-id}")
    @PostAuthorize("hasRole('ADMIN') OR (hasRole('CUSTOMER') " +
                   "AND (authentication.credentials == returnObject.body.customerId OR authentication.credentials == returnObject.body" +
                   ".driverId))")
	public ResponseEntity<RideTransactionResponse> findById(@PathVariable("ride-transaction-id") Long id) {
		return ResponseEntity.ok(rideTransactionService.findById(id));
	}

	@GetMapping("")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Page<RideTransactionResponse>> findAll(Pageable pageable) {
		return ResponseEntity.ok(rideTransactionService.findAll(pageable));
	}

	@GetMapping("/current-waiting-transaction")
	@PreAuthorize("hasRole('ADMIN') OR (hasRole('DRIVER') AND authentication.credentials == driverId)")
	// TODO: do long polling
	public ResponseEntity<RideTransactionResponse> findCurrentWaitingTransaction(@RequestParam("driver-id") Long driverId) {
		return ResponseEntity.ok(rideTransactionService.findCurrentWaitingTransaction(driverId));
	}

}