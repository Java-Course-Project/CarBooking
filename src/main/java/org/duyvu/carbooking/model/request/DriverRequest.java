package org.duyvu.carbooking.model.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DriverRequest extends BaseUserRequest {
	private Long transportationTypeId;

	private String driverLicense;
}
