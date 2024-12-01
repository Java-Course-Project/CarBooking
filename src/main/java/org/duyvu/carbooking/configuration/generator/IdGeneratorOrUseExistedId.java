package org.duyvu.carbooking.configuration.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.hibernate.annotations.IdGeneratorType;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@IdGeneratorType(UserIdGenerator.class)
@Retention(RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface IdGeneratorOrUseExistedId {

}
