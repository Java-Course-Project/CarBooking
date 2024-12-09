package org.duyvu.carbooking.mapper;

import java.time.LocalDate;
import java.time.LocalTime;
import org.duyvu.carbooking.entity.Fare;
import org.duyvu.carbooking.entity.TransportationType;
import org.duyvu.carbooking.model.response.TransportationTypeResponse;
import org.duyvu.carbooking.service.RideTransactionService;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper()
public interface TransportationTypeToTransportationTypeResponseMapper extends Mapper<TransportationType, TransportationTypeResponse> {
	TransportationTypeToTransportationTypeResponseMapper INSTANCE =
			Mappers.getMapper(TransportationTypeToTransportationTypeResponseMapper.class);

	@Mapping(target = "pricePerUnit", source = "fare", qualifiedByName = "mapFareToPricePerUnit")
	@Override
	TransportationTypeResponse map(TransportationType transportationType);

	@Named("mapFareToPricePerUnit")
	default double mapFareToPricePerUnit(Fare fare) {
		double price = fare.getPrice();
		price *= RideTransactionService.SpecialDayTime.HOLIDAYS.contains(LocalDate.now().withYear(0)) ? fare.getHolidayRate() : fare.getNormalDayRate();
		price *= RideTransactionService.SpecialDayTime.RUSH_HOURS.contains(LocalTime.now().withMinute(0).withSecond(0).withNano(0))
				 ? fare.getRushHourRate() : fare.getNormalHourRate();
		return price;
	}
}
