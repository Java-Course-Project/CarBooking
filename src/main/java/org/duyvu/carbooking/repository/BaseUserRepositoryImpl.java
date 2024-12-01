package org.duyvu.carbooking.repository;

import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.entity.BaseUser;
import org.duyvu.carbooking.model.UserType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BaseUserRepositoryImpl implements BaseUserRepository {
	private final EntityManager em;

	private <T extends BaseUser> JpaSpecificationExecutor<T> getRepository(@NotNull UserType userType) {
		JpaRepositoryFactory factory = new JpaRepositoryFactory(em);
		return switch (userType) {
			case ADMIN -> (JpaSpecificationExecutor<T>) factory.getRepository(AdminRepository.class);
			case DRIVER -> (JpaSpecificationExecutor<T>) factory.getRepository(DriverRepository.class);
			case CUSTOMER -> (JpaSpecificationExecutor<T>) factory.getRepository(CustomerRepository.class);
		};
	}

	@Override
	public Optional<BaseUser> findByUsername(String username, UserType userType) {
		Specification<BaseUser> specification =
				(root, query, criteriaBuilder)
						-> criteriaBuilder.equal(root.get("username"), username);

		return getRepository(userType).findOne(specification);
	}
}
