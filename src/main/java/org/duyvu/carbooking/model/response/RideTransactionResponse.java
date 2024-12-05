package org.duyvu.carbooking.model.response;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.duyvu.carbooking.model.RideTransactionStatus;
import org.locationtech.jts.geom.Coordinate;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class RideTransactionResponse {
	private Long id;

	private Double price;

	private Long customerId;

	private Instant startTime;

	private Long driverId;

	private Instant endTime;

	private Coordinate startLocation;

	private Coordinate destinationLocation;

	private RideTransactionStatus rideTransactionStatus;
}
