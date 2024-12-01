package org.duyvu.carbooking.repository;

import org.duyvu.carbooking.entity.RideTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RideTransactionRepository extends JpaRepository<RideTransaction, Long>, JpaSpecificationExecutor<RideTransaction> {
}