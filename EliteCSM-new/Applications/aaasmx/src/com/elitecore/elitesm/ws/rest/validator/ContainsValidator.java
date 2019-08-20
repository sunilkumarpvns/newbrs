package com.elitecore.elitesm.ws.rest.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

/**
 * Validates to property annotated with <b>@Contains</b>
 * @author animesh christie
 */
public class ContainsValidator implements ConstraintValidator<Contains, String>{

	Set<String> allowedValues;
	boolean isIgnoreCase;
	boolean isNullable;
	String nullMessage;
	String invalidMessage;
	
	@Override
	public void initialize(Contains contains) {
		isIgnoreCase = contains.isIgnoreCase();
		isNullable = contains.isNullable();
		nullMessage = contains.nullMessage();
		invalidMessage = contains.invalidMessage();
		
		if(isIgnoreCase == false){
			allowedValues = new HashSet<String>(Arrays.asList(contains.allowedValues()));
		} else {
			for(String value : contains.allowedValues()){
				allowedValues.add(value.toUpperCase());
			}
		}
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		boolean isValid = true;

		if (isNullable == false) {
			if (Strings.isNullOrEmpty(value)) {
				isValid = false;
				RestUtitlity.setValidationMessage(context, nullMessage);
			} else {
				isValid = validateValue(value,context);
			}
		} else {
			if (Strings.isNullOrEmpty(value) == false) {
				isValid = validateValue(value,context);
			}
		}

		return isValid;
	}
	
	/**
	 * Validates that value is preset in allowed values list or not
	 * @param value Value that has to be validated
	 * @param context ConstraintValidatorContext context for updating validation messages
	 * @return boolean true if value is allowed otherwise false 
	 */
	private boolean validateValue(String value, ConstraintValidatorContext context){
		boolean isValid = true;
		if(isIgnoreCase){
			value = value.toUpperCase();
		}
		
		if (allowedValues.contains(value) == false){
			isValid = false;
			RestUtitlity.setValidationMessage(context, invalidMessage);
		}
		
		return isValid;
	}
}
