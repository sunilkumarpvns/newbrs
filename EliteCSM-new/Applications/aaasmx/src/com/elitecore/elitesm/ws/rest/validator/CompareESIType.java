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
 * It validate ESI Type for Radius ESI Group.
 * 
 * @author Shekhar Vyas
 *
 */
@Target( { TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CompareESITypeValidator.class)
@Documented
public @interface CompareESIType {
	
	String message() default "Type mis-match of ESI. ";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
