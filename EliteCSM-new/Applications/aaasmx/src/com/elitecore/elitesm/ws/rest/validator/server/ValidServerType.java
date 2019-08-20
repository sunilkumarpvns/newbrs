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
 * <p> <b> @ValidServerType </b> validates value is valid or not</br>
 * For example</br>
 * <p>In REST there is possibility user may enter wrong Server Type, In such a cases 
 * we are not allowed user to create server instance.</p>
 * <p>Server Instance has following possible values are: </p></br>
 * <server-type>EliteCSM Server</server-type></br>
 * <server-type>Resource Manager</server-type></br> 
 * 
 * @author nayana.rathod
 */

@Target({ METHOD,FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ServerTypeValidator.class)
@Documented
public @interface ValidServerType {
	String message() default "Invalid Server Type";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
}
