package org.duyvu.carbooking.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.entity.Customer_;
import org.duyvu.carbooking.entity.Driver_;
import org.duyvu.carbooking.entity.Review;
import org.duyvu.carbooking.entity.Review_;
import org.duyvu.carbooking.entity.RideTransaction_;
import org.duyvu.carbooking.exception.UnsupportedValue;
import org.duyvu.carbooking.mapper.ReviewRequestToReviewMapper;
import org.duyvu.carbooking.mapper.ReviewToReviewResponseMapper;
import org.duyvu.carbooking.model.UserType;
import org.duyvu.carbooking.model.request.ReviewRequest;
import org.duyvu.carbooking.model.response.ReviewResponse;
import org.duyvu.carbooking.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewRepository reviewRepository;

	public Page<ReviewResponse> findAll(Long targetId, UserType userType, Pageable pageable) {
		Specification<Review> spec = (root, query, builder) -> builder.conjunction();
		switch (userType) {
			case CUSTOMER -> spec =
					spec.and((root, query, builder)
									 -> builder.equal(root.get(Review_.RIDE_TRANSACTION).get(RideTransaction_.CUSTOMER).get(Customer_.ID),
													  targetId));
			case DRIVER -> spec =
					spec.and((root, query, builder)
									 -> builder.equal(root.get(Review_.RIDE_TRANSACTION).get(RideTransaction_.DRIVER).get(Driver_.ID),
													  targetId));
			case ADMIN -> throw new UnsupportedValue("Not supported user type %s".formatted(userType));
		}

		return reviewRepository.findAll(spec, pageable).map(ReviewToReviewResponseMapper.INSTANCE::map);
	}

	public ReviewResponse findById(Long id) {
		return ReviewToReviewResponseMapper.INSTANCE.map(
				reviewRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Review not found")));
	}

	@Transactional(rollbackFor = Exception.class)
	public Long save(ReviewRequest request) {
		return reviewRepository.save(ReviewRequestToReviewMapper.INSTANCE.map(request)).getId();
	}
}
