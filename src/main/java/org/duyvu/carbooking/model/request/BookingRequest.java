package org.duyvu.carbooking.model.request;

import lombok.Data;
import org.locationtech.jts.geom.Coordinate;

@Data
public class BookingRequest {
	private Long customerId;
	private Coordinate startLocation;
	private Coordinate destinationLocation;
	private Long transportationTypeId;
}
