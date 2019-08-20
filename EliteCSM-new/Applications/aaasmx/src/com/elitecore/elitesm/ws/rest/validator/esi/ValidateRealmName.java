package com.elitecore.elitesm.ws.rest.validator.esi;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


/**
 * Validates Realm Name if ESI Type is RAD Auth or RAD Acct for REST API and ignores Realm Name in other scenario(eg: GUI)
 * @author chirag.i.prajapati
 */
@Target( { TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RealmNameValidator.class)
@Documented
public @interface ValidateRealmName {
	String message() default "Realm Names must be specified";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String esiTypeField();
	String realmNameField();
}