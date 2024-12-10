package org.duyvu.carbooking.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {
	private Coordinate startLocation;

	private Coordinate destinationLocation;

	private Long transportationTypeId;

	private double price;

	private Long driverId;
}
