package org.duyvu.carbooking.mapper;

import org.duyvu.carbooking.entity.Driver;
import org.duyvu.carbooking.model.request.DriverRequest;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper()
public interface DriverRequestToDriverMapper extends Mapper<DriverRequest, Driver> {
	DriverRequestToDriverMapper INSTANCE = Mappers.getMapper(DriverRequestToDriverMapper.class);
}