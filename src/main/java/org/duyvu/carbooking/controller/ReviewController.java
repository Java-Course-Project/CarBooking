package org.duyvu.carbooking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.model.UserType;
import org.duyvu.carbooking.model.request.ReviewRequest;
import org.duyvu.carbooking.model.response.ReviewResponse;
import org.duyvu.carbooking.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
// For this controller, security must be done manually as customer id is not available in the response
public class ReviewController {
	private final ReviewService reviewService;

	@GetMapping("/{review-id}")
	@PreAuthorize("@reviewSecurityService.hasReadPermission(#id)")
	public ResponseEntity<ReviewResponse> findById(@PathVariable("review-id") Long id) {
		return ResponseEntity.ok(reviewService.findById(id));
	}

	@GetMapping("")
	@PreAuthorize("""
		hasRole('ADMIN') OR ((hasRole(#userType.getValue()) AND @jwtUtils.extractId(authentication.credentials).equals(#targetId)))
	""")
	public ResponseEntity<Page<ReviewResponse>> findAll(@RequestParam(value = "target-id") Long targetId,
														@RequestParam(value = "user-type") UserType userType,
														Pageable pageable) {
		return ResponseEntity.ok(reviewService.findAll(targetId, userType, pageable));
	}

	@PostMapping("")
	@PreAuthorize("@reviewSecurityService.hasReadPermission(#request.rideTransactionId)")
	public ResponseEntity<Long> save(@Valid @RequestBody ReviewRequest request) {
		return ResponseEntity.ok(reviewService.save(request));
	}

}
