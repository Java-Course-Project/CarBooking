package org.duyvu.carbooking.mapper;

import org.duyvu.carbooking.entity.Customer;
import org.duyvu.carbooking.model.request.CustomerRequest;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper()
public interface CustomerRequestToCustomerMapper extends Mapper<CustomerRequest, Customer> {
	CustomerRequestToCustomerMapper INSTANCE = Mappers.getMapper(CustomerRequestToCustomerMapper.class);
}