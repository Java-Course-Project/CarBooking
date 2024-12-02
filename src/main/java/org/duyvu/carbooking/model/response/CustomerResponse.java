package org.duyvu.carbooking.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.duyvu.carbooking.model.CustomerStatus;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class CustomerResponse extends BaseUserResponse {
	private CustomerStatus customerStatus;
}
