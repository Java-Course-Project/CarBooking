package org.duyvu.carbooking.mapper;

import org.duyvu.carbooking.entity.RideTransaction;
import org.duyvu.carbooking.model.request.RideTransactionRequest;
import org.locationtech.jts.geom.GeometryFactory;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(uses = CoordinateToPointMapper.class)
public interface RideTransactionRequestToRideTransactionMapper extends Mapper<RideTransactionRequest, RideTransaction> {
	RideTransactionRequestToRideTransactionMapper INSTANCE = Mappers.getMapper(RideTransactionRequestToRideTransactionMapper.class);

	@Mapping(target = "rideTransactionStatus", ignore = true)
	@Mapping(target = "startTime", ignore = true)
	@Mapping(target = "price", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "endTime", ignore = true)
	@Mapping(target = "driver",
			 expression = "java(org.duyvu.carbooking.entity.Driver.builder().id(rideTransactionRequest.getDriverId()).build())")
	@Mapping(target = "customer",
			 expression = "java(org.duyvu.carbooking.entity.Customer.builder().id(rideTransactionRequest.getCustomerId()).build())")
	RideTransaction map(RideTransactionRequest rideTransactionRequest, GeometryFactory factory);
}