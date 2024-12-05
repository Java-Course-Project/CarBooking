package org.duyvu.carbooking.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Builder
public class RideTransactionRequest {
	private Long customerId;

	private Long driverId;

	private Coordinate startLocation;

	private Coordinate destinationLocation;
}
