package org.duyvu.carbooking.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequest {
	@NotNull
	@Size(min = 1, max = 256)
	private String username;

	@NotNull
	@Size(min = 1, max = 256)
	private String password;
}
