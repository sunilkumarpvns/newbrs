package com.elitecore.elitesm.ws.rest.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Contains code that validates the property annotated with <b>@IsNumeric</b>
 * allows only numeric value in Integral data type(used in REST)
 * @author chirag.i.prajapati
 */

public class NumberValidator implements ConstraintValidator<IsNumeric, Object>{
	
	/**
	 * this field is use when you annotated property in which value may arrive or not, in that
	 * case whether you want to ignore this validation or not .</br>
	 * default false means if required property's value not found it will return false and 
	 * show user given validation message
	 */
	boolean isAllowBlank = false;
	
	@Override
	public void initialize(IsNumeric arg0) {
		isAllowBlank = arg0.isAllowBlank();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext arg1) {
		if (value == null) {
            return isAllowBlank;
        }
		return true;
	}

}
