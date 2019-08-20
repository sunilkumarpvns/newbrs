package com.elitecore.elitesm.ws.rest.validator;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.PropertyUtils;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
/**
 * Contains code that validates the class annotated with <b>@SingleDriver</b>
 * allows only and at least one driver value in Service Driver
*@author Tejas.p.Shah
*	
*/
public class SingleDriverValidator  implements ConstraintValidator<SingleDriver,  Object>{

	@Override
	public void initialize(SingleDriver arg0) {
	}

	@Override
	public boolean isValid(Object driverInstance, ConstraintValidatorContext context) {
		boolean isValid = true;
		String [] propertyNames = {"dbdetail", "userFileDetail","csvset","dbacctset","mapGatewaySet",
				"detaillocalset","crestelRatingSet","diameterChargingDriverSet","webServiceAuthDriverSet",
				"crestelChargingSet","httpAuthFieldMapSet","hssDetail","ldapdetail"};
		int count = 0;
		for(String property : propertyNames){
			if(Collectionz.isNullOrEmpty(getDriver(driverInstance, property)) == false){
				count ++ ;
				if(count > 1){
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Specify driver type should be same");
					break;
				}
			}
		}
		if(count == 0){
			isValid = false;
		}
		return isValid;
	}
	
	private Set<?> getDriver(Object object, String propertyName){

		Set<?> driverSet = null;
		try {
			driverSet = (Set<?>) PropertyUtils.getProperty(object,propertyName ); 
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		 return driverSet;
	}

}
