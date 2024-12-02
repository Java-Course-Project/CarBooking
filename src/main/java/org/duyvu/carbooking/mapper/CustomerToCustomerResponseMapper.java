package org.duyvu.carbooking.mapper;

import org.duyvu.carbooking.entity.Customer;
import org.duyvu.carbooking.model.response.CustomerResponse;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper()
public interface CustomerToCustomerResponseMapper extends Mapper<Customer, CustomerResponse> {
	CustomerToCustomerResponseMapper INSTANCE = Mappers.getMapper(CustomerToCustomerResponseMapper.class);
}