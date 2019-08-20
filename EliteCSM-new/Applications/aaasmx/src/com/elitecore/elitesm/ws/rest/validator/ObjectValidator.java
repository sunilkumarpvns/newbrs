package com.elitecore.elitesm.ws.rest.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

/**
 * <p> <b> @ValidObject </b> Validates value of all class property at class level by implementing Validator Interface.</br>
 * @author vijayrajsinh
 */
public class ObjectValidator implements ConstraintValidator<ValidObject, Object> {

	@Override
	public void initialize(ValidObject arg0) {
	}

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext context) {
		boolean isValid = true;

		try {
			isValid = ((Validator) obj).validate(context);
		} catch (ClassCastException e) {
			e.printStackTrace();
			isValid = false;
			RestUtitlity.setValidationMessage(context,"validator implementation not found for class: " + obj.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			isValid = false;
		}
		return isValid;
	}
}
