package org.duyvu.carbooking.mapper;

import java.util.List;
import org.duyvu.carbooking.model.BookingInfo;
import org.duyvu.carbooking.model.request.BookingRequest;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper()
public interface BookingRequestToBookingInfoMapper extends Mapper<BookingRequest, BookingInfo> {
	BookingRequestToBookingInfoMapper INSTANCE = Mappers.getMapper(BookingRequestToBookingInfoMapper.class);

	BookingInfo map(BookingRequest bookingRequest, List<Long> blacklistDriverIds);
}