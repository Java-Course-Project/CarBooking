package org.duyvu.carbooking.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.entity.User;
import org.duyvu.carbooking.model.LoginRequest;
import org.duyvu.carbooking.model.Token;
import org.duyvu.carbooking.repository.UserRepository;
import org.duyvu.carbooking.utils.JwtUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final JwtUtils jwtUtils;

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	public Token login(LoginRequest loginRequest) {
		User user = userRepository.findByName(loginRequest.getUsername()).orElseThrow(() -> new EntityNotFoundException("User not found"));
		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new BadCredentialsException("Wrong password");
		}

		return Token.builder().accessToken(
							jwtUtils.generateAccessToken(loginRequest.getUsername(), Map.of(JwtUtils.ClaimAttribute.ROLE,
																							user.getRole().name())))
					.refreshToken(jwtUtils.generateRefreshToken(loginRequest.getUsername())).build();
	}

	public Token refresh(@NotNull String refreshToken) {
		if (!jwtUtils.isValidateToken(refreshToken)) {
			throw new BadCredentialsException("Invalid refresh token");
		}

		String username = jwtUtils.extractUsername(refreshToken);
		User user = userRepository.findByName(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
		return Token.builder()
					.accessToken(jwtUtils.generateAccessToken(username, Map.of(JwtUtils.ClaimAttribute.ROLE, user.getRole().name())))
					.refreshToken(refreshToken).build();
	}
}
