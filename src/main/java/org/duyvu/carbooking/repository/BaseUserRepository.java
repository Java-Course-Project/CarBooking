package org.duyvu.carbooking.repository;

import java.util.Optional;
import org.duyvu.carbooking.entity.BaseUser;
import org.duyvu.carbooking.model.UserType;

public interface BaseUserRepository {
	Optional<? extends BaseUser> findByUsername(String username, UserType userType);
}
