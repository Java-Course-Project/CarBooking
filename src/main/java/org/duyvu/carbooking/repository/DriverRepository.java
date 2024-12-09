package org.duyvu.carbooking.repository;

import java.time.Instant;
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

	@Query("""
			SELECT tmp.id AS id
			FROM (
				  SELECT d.id AS id
				  FROM Driver d
				  LEFT JOIN (
					  SELECT rt.driver.id AS driverId,
							 (5 - COALESCE(AVG(r.rate), 0)) AS ratePoint
					  FROM Review r
					  LEFT JOIN RideTransaction rt
					  ON r.rideTransaction.id = rt.id
					  GROUP BY rt.driver.id
				  ) rate
				  ON d.id = rate.driverId
				  WHERE d.driverStatus = 'NOT_BOOKED'
				  GROUP BY d.id
				  ORDER BY
					  MIN(ST_Distance(d.location, :startLocation) + COALESCE(rate.ratePoint, 0)),
					  d.id
				  LIMIT 1
			  ) tmp
	""")
	@Lock(PESSIMISTIC_WRITE)
	Optional<Long> findShortestAvailableDriverId(@Param("startLocation") Point startLocation);

	@Modifying
	@Query("UPDATE Driver d SET d.driverStatus = 'OFFLINE' WHERE d.driverStatus = 'NOT_BOOKED' AND d.lastUpdated < :timeout")
	int setInactiveDriversToOffline(Instant timeout);
}
