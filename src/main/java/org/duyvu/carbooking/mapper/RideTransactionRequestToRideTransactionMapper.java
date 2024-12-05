package org.duyvu.carbooking.mapper;

import org.duyvu.carbooking.entity.RideTransaction;
import org.duyvu.carbooking.model.request.RideTransactionRequest;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper()
public interface RideTransactionRequestToRideTransactionMapper extends Mapper<RideTransactionRequest, RideTransaction> {
	RideTransactionRequestToRideTransactionMapper INSTANCE = Mappers.getMapper(RideTransactionRequestToRideTransactionMapper.class);

	@Mapping(target = "rideTransactionStatus", ignore = true)
	@Mapping(target = "startTime", ignore = true)
	@Mapping(target = "price", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "endTime", ignore = true)
	@Mapping(target = "driver",
			 expression = "java(org.duyvu.carbooking.entity.Driver.builder().id(Math.toIntExact(driverRequest.getTransportationTypeId())).build())")
	@Mapping(target = "customer",
			 expression = "java(org.duyvu.carbooking.entity.Customer.builder().id(Math.toIntExact(driverRequest.getTransportationTypeId())).build())")
	@Override
	RideTransaction map(RideTransactionRequest rideTransactionRequest);
}