package org.duyvu.carbooking.service;

import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.entity.BaseUser;
import org.duyvu.carbooking.model.UserType;
import org.duyvu.carbooking.repository.BaseUserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BaseUserService implements UserDetailsService {
	private static final String SEPARATOR = "\\|";
	private final BaseUserRepository baseUserRepository;

	private String extractActualUsernameFrom(String username) {
		return username.split(SEPARATOR, 2)[0];
	}

	private UserType extractUserTypeFrom(String username) {
		return UserType.from(username.split(SEPARATOR, 2)[1]);
	}

	@Override
	// username in format (UserType|Username)
	public BaseUser loadUserByUsername(String username) throws UsernameNotFoundException {
		return baseUserRepository.findByUsername(extractActualUsernameFrom(username), extractUserTypeFrom(username))
								 .orElseThrow(() -> new BadCredentialsException("Username not exist"));
	}

}
