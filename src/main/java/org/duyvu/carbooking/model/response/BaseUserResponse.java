package org.duyvu.carbooking.model.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.duyvu.carbooking.model.Gender;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseUserResponse {
	private Long id;

	@Size(max = 32)
	@NotNull
	private String username;

	@Size(max = 128)
	@NotNull
	private String email;

	@Size(max = 32)
	@NotNull
	private String citizenIdentificationNumber;

	@NotNull
	private OffsetDateTime dob;

	@NotNull
	private Gender gender;

}
