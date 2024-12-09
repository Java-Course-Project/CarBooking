package org.duyvu.carbooking.repository;

import java.util.Optional;
import org.duyvu.carbooking.entity.RideTransaction;
import org.duyvu.carbooking.model.RideTransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

public interface RideTransactionRepository extends JpaRepository<RideTransaction, Long>, JpaSpecificationExecutor<RideTransaction> {
	@Query("SELECT r FROM RideTransaction r WHERE r.driver.id = :driverId AND r.rideTransactionStatus = :status")
	Optional<RideTransaction> findCurrentWaitingTransaction(Long driverId, RideTransactionStatus status);

	@Query("SELECT r FROM RideTransaction r WHERE r.driver.id = :driverId AND r.rideTransactionStatus = :status")
	@Lock(PESSIMISTIC_WRITE)
	Optional<RideTransaction> findByDriverIdAndStatusThenLock(@Param("driverId") Long driverId,
															  @Param("status") RideTransactionStatus status);
}
