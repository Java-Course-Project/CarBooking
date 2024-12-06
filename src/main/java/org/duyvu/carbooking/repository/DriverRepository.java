package org.duyvu.carbooking.repository;

import java.util.Optional;
import org.duyvu.carbooking.entity.Driver;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

public interface DriverRepository extends JpaRepository<Driver, Long>, JpaSpecificationExecutor<Driver> {
	@Query("SELECT r FROM Driver r WHERE r.id = :id")
	@Lock(PESSIMISTIC_WRITE)
	Optional<Driver> findByIdThenLock(@Param("id") Long id);

	@Query("""
	SELECT d.id FROM Driver d
	LEFT JOIN (
		SELECT rt.driver.id AS driverId, COALESCE(AVG(r.rate), 0) AS ratePoint
		FROM Review r
		LEFT JOIN RideTransaction rt ON r.rideTransaction.id = rt.id
		GROUP BY driverId
	 ) rate
	 ON d.id = rate.driverId
	 WHERE d.driverStatus = 'NOT_BOOKED' ORDER BY ST_Distance(d.location, ST_GeomFromText('POINT(2 2)')) + rate.ratePoint LIMIT 1
	""")
	@Lock(PESSIMISTIC_WRITE)
	Optional<Long> findShortestAvailableDriverId(@Param("startLocation") Point startLocation);
}