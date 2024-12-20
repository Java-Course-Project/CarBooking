package org.duyvu.carbooking.repository;

import org.duyvu.carbooking.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReviewRepository extends JpaRepository<Review, Long> , JpaSpecificationExecutor<Review> {
  }