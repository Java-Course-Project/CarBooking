package org.duyvu.carbooking.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.duyvu.carbooking.model.DriverStatus;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class DriverResponse extends BaseUserResponse {
	private Long transportationTypeId;

	private String driverLicense;

	private DriverStatus driverStatus;
}
