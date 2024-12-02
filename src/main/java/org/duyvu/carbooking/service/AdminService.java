package org.duyvu.carbooking.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.entity.Admin;
import org.duyvu.carbooking.mapper.AdminRequestToAdminMapper;
import org.duyvu.carbooking.mapper.AdminToAdminResponseMapper;
import org.duyvu.carbooking.model.request.AdminRequest;
import org.duyvu.carbooking.model.response.AdminResponse;
import org.duyvu.carbooking.model.Gender;
import org.duyvu.carbooking.repository.AdminRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	public AdminResponse findById(Long id) {
		final Admin admin = adminRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Admin not found"));

		return AdminToAdminResponseMapper.INSTANCE.map(admin);
	}

	public Page<AdminResponse> findAll(Pageable pageable) {
		return adminRepository.findAll(pageable).map(AdminToAdminResponseMapper.INSTANCE::map);
	}

	public Long save(@NotNull @Valid AdminRequest adminRequest) {
		final Admin admin = AdminRequestToAdminMapper.INSTANCE.map(adminRequest);
		return adminRepository.save(admin).getId();
	}

	public Long update(@NotNull Long id, @NotNull @Valid AdminRequest adminRequest) {
		if (!adminRepository.existsById(id)) {
			throw new EntityNotFoundException("Admin not found");
		}

		final Admin admin = adminRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Admin not found"));
		admin.setCompanyNumber(adminRequest.getCompanyNumber());
		admin.setEmail(adminRequest.getEmail());
		admin.setGender(adminRequest.getGender());
		admin.setDob(adminRequest.getDob());
		admin.setCitizenIdentificationNumber(adminRequest.getCitizenIdentificationNumber());
		admin.setUsername(adminRequest.getUsername());
		admin.setPassword(passwordEncoder.encode(admin.getPassword()));
		return adminRepository.save(admin).getId();
	}
}
