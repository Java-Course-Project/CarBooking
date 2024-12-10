package org.duyvu.carbooking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.model.request.BookingRequest;
import org.duyvu.carbooking.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("bookings")
public class BookingController {
	private final BookingService bookingService;

	@PostMapping("")
	@PreAuthorize("""
		hasRole('ADMIN') OR (hasRole('CUSTOMER') AND @jwtUtils.extractId(authentication.credentials).equals(#bookingRequest.customerId))
	""")
	public ResponseEntity<Long> book(@RequestBody @Valid BookingRequest bookingRequest) {
		return ResponseEntity.ok(bookingService.book(bookingRequest));
	}
}
