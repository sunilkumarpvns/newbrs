package com.elitecore.elitesm.ws.rest.validator.esi;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validates IP:PORT(both Ipv4 and Ipv6),
 * In Ipv6 value must be in format [Ipv6]:PORT
 * @chirag.i.prajapati
 *
 */
@Target( { METHOD,FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IpAndPortValidator.class)
@Documented
public @interface ValidateIPPort {
	String message() default "Invalid IP Address or Port";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
