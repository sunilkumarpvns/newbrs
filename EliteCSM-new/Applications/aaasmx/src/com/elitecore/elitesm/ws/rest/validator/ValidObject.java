package com.elitecore.elitesm.ws.rest.validator;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ObjectValidator.class)
@Documented
public @interface ValidObject {

	String message() default "object value is invalid";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
