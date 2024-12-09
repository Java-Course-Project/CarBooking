package org.duyvu.carbooking.controller;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.model.UserType;
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
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ride-transactions")
public class RideTransactionController {
	private final RideTransactionService rideTransactionService;

	@GetMapping("/{ride-transaction-id}")
	@PostAuthorize("""
		hasRole('ADMIN') OR
		((hasRole('CUSTOMER') AND @jwtUtils.extractId(authentication.credentials).equals(returnObject.body.customerId))
			OR (hasRole('DRIVER') AND @jwtUtils.extractId(authentication.credentials).equals(returnObject.body.driverId)))
	""")
	public ResponseEntity<RideTransactionResponse> findById(@PathVariable("ride-transaction-id") Long id) {
		return ResponseEntity.ok(rideTransactionService.findById(id));
	}

	@GetMapping("")
	@PreAuthorize("""
		hasRole('ADMIN') OR ((hasRole(#userType.getValue()) AND @jwtUtils.extractId(authentication.credentials).equals(#targetId)))
	""")
	public ResponseEntity<Page<RideTransactionResponse>> findAll(@RequestParam(value = "target-id") Long targetId,
																 @RequestParam(value = "user-type") UserType userType,
																 Pageable pageable) {
		return ResponseEntity.ok(rideTransactionService.findAll(targetId, userType, pageable));
	}

	@GetMapping("/current-waiting-transaction")
	@PreAuthorize("hasRole('ADMIN') OR (hasRole('DRIVER') AND @jwtUtils.extractId(authentication.credentials).equals(#driverId))")
	public DeferredResult<ResponseEntity<RideTransactionResponse>> findCurrentWaitingTransaction(@RequestParam("driver-id") Long driverId) {
		final long timeout = 30L;
		DeferredResult<ResponseEntity<RideTransactionResponse>> deferredResult =
				new DeferredResult<>(timeout * 1000, ResponseEntity.noContent());

		CompletableFuture.runAsync(() -> {
			try {
				deferredResult.setResult(ResponseEntity.ok(rideTransactionService.findCurrentWaitingTransaction(driverId, timeout)));
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
		deferredResult.onError(deferredResult::setErrorResult);
		return deferredResult;
	}

}