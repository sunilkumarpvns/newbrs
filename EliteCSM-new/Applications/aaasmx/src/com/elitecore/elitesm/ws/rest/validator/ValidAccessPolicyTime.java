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
 * It validate time for Access Policy Detail.
 * 
 * @author Shekhar Vyas
 *
 */

@Target( { TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidAccessPolicyTimeValidator.class)
@Documented
public @interface ValidAccessPolicyTime {
	
	String message() default "Invalid time for timeslap";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
