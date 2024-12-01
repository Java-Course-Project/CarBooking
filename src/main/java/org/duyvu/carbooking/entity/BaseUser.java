package org.duyvu.carbooking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Collection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.duyvu.carbooking.configuration.generator.IdGeneratorOrUseExistedId;
import org.duyvu.carbooking.model.Gender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@MappedSuperclass
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SuperBuilder
public abstract class BaseUser implements UserDetails {
	@Id
	@IdGeneratorOrUseExistedId
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 32)
	@NotNull
	@Column(name = "username", nullable = false, length = 32)
	private String username;

	@Size(max = 128)
	@NotNull
	@Column(name = "email", nullable = false, length = 128)
	private String email;

	@Size(max = 1024)
	@NotNull
	@Column(name = "password", nullable = false, length = 1024)
	private String password;

	@Size(max = 32)
	@NotNull
	@Column(name = "citizen_identification_number", nullable = false, length = 32)
	private String citizenIdentificationNumber;

	@NotNull
	@Column(name = "dob", nullable = false)
	private OffsetDateTime dob;

	@NotNull
	@Column(name = "gender", nullable = false)
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Override
	public abstract Collection<? extends GrantedAuthority> getAuthorities();

	@Override
	public String getUsername() {
		return username;
	}
}
