package org.duyvu.carbooking.service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.entity.Admin;
import org.duyvu.carbooking.model.Gender;
import org.duyvu.carbooking.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final AdminRepository adminRepository;

	private final PasswordEncoder passwordEncoder;

	@PostConstruct
	public void initDefaultAdmin() {
		final Long superAdminId = 1L;
		if (!adminRepository.existsById(superAdminId)) {
			final Admin superAdmin = Admin.builder()
										  .companyNumber("123456789")
										  .id(superAdminId)
										  .dob(OffsetDateTime.of(LocalDate.of(2001, 5, 30).atStartOfDay(), ZoneOffset.UTC))
										  .citizenIdentificationNumber("085911840")
										  .email("ThisIsAnEmail@gmail.com")
										  .gender(Gender.MALE)
										  .password(passwordEncoder.encode("specialPassword"))
										  .username("SuperAdmin")
										  .build();

			adminRepository.save(superAdmin);
		}
	}

}
