package org.duyvu.carbooking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssignationInfo {
	public enum AssignationStatus {
		CONFIRMED,
		DENIED
	}

	private Long driverId;

	private AssignationStatus assignationStatus;
}
