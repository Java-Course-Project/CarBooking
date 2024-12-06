package org.duyvu.carbooking.mapper;

import org.duyvu.carbooking.entity.RideTransaction;
import org.duyvu.carbooking.model.response.RideTransactionResponse;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(uses = PointToCoordinateMapper.class)
public interface RideTransactionToRideTransactionResponseMapper extends Mapper<RideTransaction, RideTransactionResponse> {
	RideTransactionToRideTransactionResponseMapper INSTANCE = Mappers.getMapper(RideTransactionToRideTransactionResponseMapper.class);

    @Mapping(target = "driverId",
             expression = "java(rideTransaction.getCustomer().getId())")
    @Mapping(target = "customerId", expression = "java(rideTransaction.getDriver().getId())")
    @Override
    RideTransactionResponse map(RideTransaction rideTransaction);
}