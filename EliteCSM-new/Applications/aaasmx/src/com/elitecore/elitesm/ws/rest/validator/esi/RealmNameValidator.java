package com.elitecore.elitesm.ws.rest.validator.esi;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

import com.elitecore.commons.base.Strings;

/**
 * Conatins code that validates the property annotated with <b>@ValidateRealmName</b>
 * @author chirag.i.prajapati
 */

public class RealmNameValidator implements ConstraintValidator<ValidateRealmName, Object>{

	String extednedRadiusTypeField = null;
	String realmNameField = null;
	
	@Override
	public void initialize(ValidateRealmName arg0) {
		extednedRadiusTypeField = arg0.esiTypeField();
		realmNameField = arg0.realmNameField();
	}

	@Override
	public boolean isValid(Object arg0, ConstraintValidatorContext arg1) {
		boolean isValid = true;
		try {
			Object extednedRadiusTypeValue = BeanUtils.getProperty(arg0, extednedRadiusTypeField);
			Object realmNameValue = BeanUtils.getProperty(arg0, realmNameField);
			
			if(Strings.isNullOrBlank(String.valueOf(realmNameValue)) && (((String)extednedRadiusTypeValue).equals("1") || ((String)extednedRadiusTypeValue.toString()).equals("2") )){
				isValid = false;
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return isValid;
	}
}
