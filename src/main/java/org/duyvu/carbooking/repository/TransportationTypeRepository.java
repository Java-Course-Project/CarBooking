package org.duyvu.carbooking.repository;

import org.duyvu.carbooking.entity.TransportationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransportationTypeRepository extends JpaRepository<TransportationType, Integer>,
		JpaSpecificationExecutor<TransportationType> {
}