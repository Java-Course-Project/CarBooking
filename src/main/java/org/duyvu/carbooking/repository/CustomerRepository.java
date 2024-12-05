package org.duyvu.carbooking.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.duyvu.carbooking.entity.Customer;
import org.duyvu.carbooking.model.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
	@Query("SELECT c FROM Customer c WHERE c.id = :id AND c.customerStatus = :status")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Customer> findByIdAndStatusThenLock(Long id, CustomerStatus status);

	@Query("SELECT c FROM Customer c WHERE c.id = :id")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Customer> findByIdThenLock(Long id);
}