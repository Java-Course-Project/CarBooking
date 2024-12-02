package org.duyvu.carbooking.model;

import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum UserType {
	CUSTOMER("CUSTOMER"), DRIVER("DRIVER"), ADMIN("ADMIN");

	private final String value;

	UserType(String value) {
		this.value = value;
	}

	public static UserType from(@NotNull String value) {
		return Arrays.stream(UserType.values()).filter(e -> e.value.equals(value.replace("ROLE_", ""))).findFirst()
					 .orElseThrow(() -> new IllegalArgumentException("Invalid user type: " + value));
	}
}
