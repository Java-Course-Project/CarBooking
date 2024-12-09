package org.duyvu.carbooking.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransportationTypeResponse {
	private Integer id;

	private String type;

	@JsonProperty("price_per_unit")
	private double pricePerUnit;
}
