package org.duyvu.carbooking.repository;

import java.util.Optional;
import org.duyvu.carbooking.entity.Fare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FareRepository extends JpaRepository<Fare, Integer>, JpaSpecificationExecutor<Fare> {
	Optional<Fare> findByTransportationTypeId(Integer transportationTypeId);
}
