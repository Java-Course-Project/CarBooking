package org.duyvu.carbooking.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReviewRequest {
	@JsonProperty("ride_transaction_id")
	@NotNull
	private Long rideTransactionId;

	@NotNull
	@Min(0)
	@Max(5)
	private Integer rate;

	private String comment;
}
