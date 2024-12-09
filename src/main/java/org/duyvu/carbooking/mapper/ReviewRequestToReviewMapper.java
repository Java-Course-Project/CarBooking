package org.duyvu.carbooking.mapper;

import org.duyvu.carbooking.entity.Review;
import org.duyvu.carbooking.model.request.ReviewRequest;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper()
public interface ReviewRequestToReviewMapper extends Mapper<ReviewRequest, Review> {
	ReviewRequestToReviewMapper INSTANCE = Mappers.getMapper(ReviewRequestToReviewMapper.class);

	@Mapping(target = "rideTransaction",
			 expression = "java(org.duyvu.carbooking.entity.RideTransaction.builder().id(reviewRequest.getRideTransactionId()).build())")
	@Mapping(target = "id", ignore = true)
	@Override
	Review map(ReviewRequest reviewRequest);
}
