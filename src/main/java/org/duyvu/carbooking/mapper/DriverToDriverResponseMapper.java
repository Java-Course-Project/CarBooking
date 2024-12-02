package org.duyvu.carbooking.mapper;

import org.duyvu.carbooking.entity.Driver;
import org.duyvu.carbooking.model.response.DriverResponse;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper()
public interface DriverToDriverResponseMapper extends Mapper<Driver, DriverResponse> {
	DriverToDriverResponseMapper INSTANCE = Mappers.getMapper(DriverToDriverResponseMapper.class);
}