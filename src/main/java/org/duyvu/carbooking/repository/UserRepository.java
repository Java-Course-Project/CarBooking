package org.duyvu.carbooking.repository;

import java.util.Optional;
import java.util.UUID;
import org.duyvu.carbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
	Optional<User> findByName(String username);

	boolean existsByName(String username);
}