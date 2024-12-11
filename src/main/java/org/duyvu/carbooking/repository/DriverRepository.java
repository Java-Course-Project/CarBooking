package org.duyvu.carbooking.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.duyvu.carbooking.entity.Driver;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

public interface DriverRepository extends JpaRepository<Driver, Long>, JpaSpecificationExecutor<Driver> {
	@Query("SELECT r FROM Driver r WHERE r.id = :id")
	@Lock(PESSIMISTIC_WRITE)
	Optional<Driver> findByIdThenLock(@Param("id") Long id);

	@Query(value = """
			SELECT d.id AS id
			FROM driver d
			LEFT JOIN (
				SELECT rt.driver_id AS driverId,
					(5 - COALESCE(AVG(r.rate), 0)) AS ratePoint
				FROM review r
				LEFT JOIN ride_transaction rt
				ON r.ride_transaction_id = rt.id
				GROUP BY rt.driver_id
			) rate
			ON d.id = rate.driverId
			WHERE d.driver_status = 'NOT_BOOKED'
				AND d.id NOT IN (:driverIds)
			GROUP BY d.id
			ORDER BY
				MIN(ST_Distance(d.location, :startLocation) + COALESCE(rate.ratePoint, 0)),
				d.id
			LIMIT 1 FOR UPDATE SKIP LOCKED
			""", nativeQuery = true)
	Optional<Long> findShortestAvailableDriverId(@Param("startLocation") Point startLocation, @Param("driverIds") List<Long> driverIds);

	@Modifying
	@Query("UPDATE Driver d SET d.driverStatus = 'OFFLINE' WHERE d.driverStatus = 'NOT_BOOKED' AND d.lastUpdated < :timeout")
	int setInactiveDriversToOffline(Instant timeout);
}
