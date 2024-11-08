package org.duyvu.carbooking.repository;

import java.util.UUID;
import org.duyvu.carbooking.entity.PriceUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PriceUnitRepository extends JpaRepository<PriceUnit, UUID>, JpaSpecificationExecutor<PriceUnit> {
}