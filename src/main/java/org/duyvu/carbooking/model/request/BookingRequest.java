package org.duyvu.carbooking.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
	@JsonProperty("customer_id")
	private Long customerId;

	@JsonProperty("start_location")
	private Coordinate startLocation;

	@JsonProperty("destination_location")
	private Coordinate destinationLocation;

	@JsonProperty("transportation_type_id")
	private Long transportationTypeId;
}
