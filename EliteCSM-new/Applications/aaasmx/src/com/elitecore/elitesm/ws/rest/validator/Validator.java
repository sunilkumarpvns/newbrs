package com.elitecore.elitesm.ws.rest.validator;

import javax.validation.ConstraintValidatorContext;

public interface Validator {
	boolean validate(ConstraintValidatorContext context);
}
