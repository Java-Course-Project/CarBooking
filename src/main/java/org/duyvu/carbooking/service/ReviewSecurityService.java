package org.duyvu.carbooking.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.entity.Review;
import org.duyvu.carbooking.entity.RideTransaction;
import org.duyvu.carbooking.model.UserType;
import org.duyvu.carbooking.repository.ReviewRepository;
import org.duyvu.carbooking.repository.RideTransactionRepository;
import org.duyvu.carbooking.utils.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings({"unused", "DuplicatedCode"})
// used in spring el
public class ReviewSecurityService {
	private final ReviewRepository reviewRepository;

	private final RideTransactionRepository rideTransactionRepository;

	private final JwtUtils jwtUtils;

	private boolean hasRoleAdmin(Authentication auth) {
		for (GrantedAuthority authority : auth.getAuthorities()) {
			if (authority.getAuthority().equals("ROLE_ADMIN")) {
				return true;
			}
		}
		return false;
	}

	public boolean hasReadPermission(Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (hasRoleAdmin(auth)) {
			return true;
		}

		String token = (String) auth.getCredentials();
		UserType userType = UserType.from(jwtUtils.extractRole(token));
		Long targetId = jwtUtils.extractId(token);

		Review review = reviewRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Review not found"));

		return switch (userType) {
			case ADMIN -> true;
			case DRIVER -> targetId.equals(review.getRideTransaction().getDriver().getId());
			case CUSTOMER -> targetId.equals(review.getRideTransaction().getCustomer().getId());
		};
	}

	public boolean hasWritePermission(Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (hasRoleAdmin(auth)) {
			return true;
		}

		String token = (String) auth.getCredentials();
		UserType userType = UserType.from(jwtUtils.extractRole(token));
		Long targetId = jwtUtils.extractId(token);

		RideTransaction rideTransaction =
				rideTransactionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("RideTransaction not found"));

		return switch (userType) {
			case ADMIN -> true;
			case DRIVER -> targetId.equals(rideTransaction.getDriver().getId());
			case CUSTOMER -> targetId.equals(rideTransaction.getCustomer().getId());
		};
	}
}
