package org.duyvu.carbooking.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.duyvu.carbooking.entity.Gender;
import org.duyvu.carbooking.entity.Role;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserRequest {
	@NotNull
	@Size(max = 258)
	private String credential;

	@Size(max = 258)
	private String name;

	private OffsetDateTime dob;

	private Gender gender;

	private String password;

	private Role role;
}