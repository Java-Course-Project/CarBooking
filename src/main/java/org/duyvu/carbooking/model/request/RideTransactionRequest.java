package org.duyvu.carbooking.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
	@JsonProperty("customer_id")
	private Long customerId;

	@JsonProperty("driver_id")
	private Long driverId;

	@JsonProperty("start_location")
	private Coordinate startLocation;

	@JsonProperty("destination_location")
	private Coordinate destinationLocation;
}
