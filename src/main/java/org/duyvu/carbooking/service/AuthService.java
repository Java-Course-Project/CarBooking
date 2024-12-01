package org.duyvu.carbooking.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.entity.BaseUser;
import org.duyvu.carbooking.model.LoginRequest;
import org.duyvu.carbooking.model.Token;
import org.duyvu.carbooking.model.UserType;
import org.duyvu.carbooking.repository.BaseUserRepository;
import org.duyvu.carbooking.utils.JwtUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final JwtUtils jwtUtils;

	private final PasswordEncoder passwordEncoder;

	private final BaseUserRepository baseUserRepository;

	public Token login(LoginRequest loginRequest) {
		BaseUser user
				= baseUserRepository.findByUsername(loginRequest.getUsername(), loginRequest.getUserType())
									.orElseThrow(() -> new EntityNotFoundException("Username not found"));
		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new BadCredentialsException("Wrong password");
		}

		return Token.builder().accessToken(
							jwtUtils.generateAccessToken(loginRequest.getUsername(),
														 Map.of(JwtUtils.ClaimAttribute.ROLE, user.getAuthorities())))
					.refreshToken(jwtUtils.generateRefreshToken(loginRequest.getUsername())).build();
	}

	public Token refresh(@NotNull String refreshToken) {
		if (!jwtUtils.isValidateToken(refreshToken)) {
			throw new BadCredentialsException("Invalid refresh token");
		}

		String username = jwtUtils.extractUsername(refreshToken);
		UserType userType = UserType.from(jwtUtils.extractRole(refreshToken));

		BaseUser user =
				baseUserRepository.findByUsername(username, userType).orElseThrow(() -> new EntityNotFoundException("Username not found"));
		return Token.builder()
					.accessToken(jwtUtils.generateAccessToken(username, Map.of(JwtUtils.ClaimAttribute.ROLE, user)))
					.refreshToken(refreshToken).build();
	}
}
