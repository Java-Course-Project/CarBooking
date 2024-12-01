package org.duyvu.carbooking.model;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum UserType {
	CUSTOMER("CUSTOMER"), DRIVER("DRIVER"), ADMIN("ADMIN");

	private final String value;

	UserType(String value) {
		this.value = value;
	}

	public static UserType from(String value) {
		return Arrays.stream(UserType.values()).filter(e -> e.value.equals(value)).findFirst()
					 .orElseThrow(() -> new IllegalArgumentException("Invalid user type: " + value));
	}
}
