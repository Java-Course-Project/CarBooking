package org.duyvu.carbooking.configuration.generator;

import org.duyvu.carbooking.entity.BaseUser;
import org.duyvu.carbooking.entity.Fare;
import org.duyvu.carbooking.entity.TransportationType;
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
		if (entity instanceof BaseUser user) {
			return user.getId() == null;
		} else if (entity instanceof Fare fare) {
			return fare.getId() == null;
		} else if (entity instanceof TransportationType transportationType) {
			return transportationType.getId() == null;
		}
		return super.generatedOnExecution(entity, session);
	}

	@Override
	public boolean allowAssignedIdentifiers() {
		return true;
	}

	@Override
	public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
		if (owner instanceof BaseUser user) {
			return user.getId();
		} else if (owner instanceof Fare fare) {
			return fare.getId();
		} else if (owner instanceof TransportationType transportationType) {
			return transportationType.getId();
		}

		throw new RuntimeException("Unknown owner type: " + owner);
	}
}