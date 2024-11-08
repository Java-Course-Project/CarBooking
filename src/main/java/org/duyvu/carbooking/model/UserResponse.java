package org.duyvu.carbooking.model;

import java.time.OffsetDateTime;
import java.util.UUID;
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
public class UserResponse {
	private UUID id;

	private String credential;

	private String username;

	private OffsetDateTime dob;

	private Gender gender;

	private Role role;
}