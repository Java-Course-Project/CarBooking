package org.duyvu.carbooking.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class AdminResponse extends BaseUserResponse {
	@JsonProperty("company_number")
	private String companyNumber;
}
