package com.elitecore.elitesm.ws.rest.validator.server;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * <p> <b> @ValidServiceType </b> validates the value is valid or not</br>
 * For example</br>
 * <p>In REST there is possibility user may enter wrong Service Type, In such a cases 
 * It will not allowed user to add services.</p>
 * <p>Services are : </p></br>
 * <server-type>RAD-AUTH</server-type></br>
 * <server-type>RAD-ACCT</server-type></br> 
 * <server-type>RAD-DYNAUTH</server-type></br> 
 * <server-type>DIAMETER-EAP</server-type></br> 
 * <server-type>DIAMETER-NAS</server-type></br> 
 * <server-type>DIAMETER-CC</server-type></br> 
 * <server-type>3GPP-AAA-SERVER</server-type></br> 
 * @author nayana.rathod
 */

@Target({ METHOD,FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ServiceTypeValidator.class)
@Documented
public @interface ValidServiceType {
	String message() default "Invalid Service Type";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
}
