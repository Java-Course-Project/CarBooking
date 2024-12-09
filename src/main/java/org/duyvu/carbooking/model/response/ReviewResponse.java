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
public class ReviewResponse {
	private Long id;

	@JsonProperty("ride_transaction_id")
	private Long rideTransactionId;

	private Integer rate;

	private String comment;
}
