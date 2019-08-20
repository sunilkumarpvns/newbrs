package com.elitecore.elitesm.ws.rest.validator;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
*
*@author Tejas.p.Shah
*	
*/
@Target( { TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SingleDriverValidator.class)
@Documented
public @interface SingleDriver {

	String message() default "At least one driver detail required";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}

