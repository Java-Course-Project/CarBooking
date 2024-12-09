package org.duyvu.carbooking.mapper;

import org.duyvu.carbooking.entity.Review;
import org.duyvu.carbooking.model.response.ReviewResponse;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper()
public interface ReviewToReviewResponseMapper extends Mapper<Review, ReviewResponse> {
	ReviewToReviewResponseMapper INSTANCE = Mappers.getMapper(ReviewToReviewResponseMapper.class);

    @Mapping(target = "rideTransactionId", expression = "java(review.getRideTransaction().getId())")
    @Override
    ReviewResponse map(Review review);
}
