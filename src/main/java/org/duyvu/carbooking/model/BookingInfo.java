package org.duyvu.carbooking.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingInfo {
	private Long customerId;

	private Coordinate startLocation;

	private Coordinate destinationLocation;

	private Long transportationTypeId;

	private List<Long> blacklistDriverIds;
}
