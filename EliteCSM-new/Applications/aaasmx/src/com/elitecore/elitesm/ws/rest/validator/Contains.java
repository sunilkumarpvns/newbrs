package com.elitecore.elitesm.ws.rest.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validates the value is in the allowed values list or not
 * @author animesh christie
 */
@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ContainsValidator.class)
@Documented
public @interface Contains {
	String message() default "Valid value must be specified";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String nullMessage() default "Value must be specified";
	String invalidMessage() default "Invalid value specified";
	
	String[] allowedValues();
	boolean isNullable() default false;
	boolean isIgnoreCase() default false;
}