package org.duyvu.carbooking.mapper;

import org.duyvu.carbooking.entity.Driver;
import org.duyvu.carbooking.model.request.DriverRequest;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper
public interface DriverRequestToDriverMapper extends Mapper<DriverRequest, Driver> {
	DriverRequestToDriverMapper INSTANCE = Mappers.getMapper(DriverRequestToDriverMapper.class);

	@Mapping(target = "transportationType",
			 expression = "java(org.duyvu.carbooking.entity.TransportationType.builder().id(Math.toIntExact(driverRequest.getTransportationTypeId())).build())")
	@Mapping(target = "location", ignore = true)
	@Mapping(target = "lastUpdated", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "driverStatus", ignore = true)
	@Mapping(target = "authorities", ignore = true)
	@Override
	Driver map(DriverRequest driverRequest);
}