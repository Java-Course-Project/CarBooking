package org.duyvu.carbooking.configuration.generator;

import org.duyvu.carbooking.entity.BaseUser;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.id.IdentityGenerator;

public class UserIdGenerator extends IdentityGenerator implements BeforeExecutionGenerator {
	@Override
	public boolean generatedOnExecution() {
		return super.generatedOnExecution();
	}

	@Override
	public boolean generatedOnExecution(Object entity, SharedSessionContractImplementor session) {
		if (entity instanceof BaseUser userEntity) {
			return userEntity.getId() == null;
		}
		return super.generatedOnExecution(entity, session);
	}

	@Override
	public boolean allowAssignedIdentifiers() {
		return true;
	}

	@Override
	public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
		if (!(owner instanceof BaseUser userEntity)) {
			throw new RuntimeException("It's not BaseUser !!");
		}

		return userEntity.getId();
	}
}