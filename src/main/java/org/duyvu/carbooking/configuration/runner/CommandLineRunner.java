package org.duyvu.carbooking.configuration.runner;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.entity.Role;
import org.duyvu.carbooking.model.UserRequest;
import org.duyvu.carbooking.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandLineRunner implements org.springframework.boot.CommandLineRunner {
	private static final String SUPER_ADMIN_USERNAME = "SuperAdmin";

	private static final String SUPER_ADMIN_PASSWORD = "This is a long string";

	private static final String SUPER_ADMIN_CREDENTIAL = "085911840";

	private final PasswordEncoder passwordEncoder;

	private final UserService userService;

	@Override
	public void run(String... args) {
		if (!userService.isExistByUsername(SUPER_ADMIN_USERNAME)) {
			UserRequest request = UserRequest.builder()
											 .dob(OffsetDateTime.of(2000, 1, 1, 0, 0, 1, 0, ZoneOffset.UTC))
											 .name(SUPER_ADMIN_USERNAME)
											 .password(passwordEncoder.encode(SUPER_ADMIN_PASSWORD))
											 .credential(SUPER_ADMIN_CREDENTIAL)
											 .role(Role.ROLE_ADMIN)
											 .build();
			userService.save(request);
		}
	}
}
