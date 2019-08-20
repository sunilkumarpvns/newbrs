package com.elitecore.elitesm.ws.rest.validator;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.PropertyUtils;

import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

/**
 * 
 * It validate primary esi and secondary esi property of Radius Policy Group annotated with @CompareESIType. <br> 
 * It validate primary esi type and secondary esi type must be same. 
 * 
 * @author Shekhar Vyas
 *
 */

public class CompareESITypeValidator implements ConstraintValidator<CompareESIType, Object>  {

	@Override
	public void initialize(CompareESIType arg0) {
		
	}

	@Override
	public boolean isValid(Object arg0, ConstraintValidatorContext context) {

		boolean isValid = true;

		StringBuilder message = new StringBuilder("");

		try {
			
			Object primaryESINameObj = PropertyUtils.getProperty(arg0, "primaryESIName");
			
			Object secondaryESINameObj = null;
			
			try {
				secondaryESINameObj = PropertyUtils.getProperty(arg0, "secondaryESIName");
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
			if (secondaryESINameObj != null) {
				
				ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager = new ExternalSystemInterfaceBLManager();
				
				String primaryESINameStr = String.valueOf(primaryESINameObj);
				String secondaryESINameStr = String.valueOf(secondaryESINameObj);
				
				Long primaryESITypeId = externalSystemInterfaceBLManager.getExternalSystemInterfaceInstanceDataByName(primaryESINameStr).getEsiTypeId();
				Long secondaryESITypeId = externalSystemInterfaceBLManager.getExternalSystemInterfaceInstanceDataByName(secondaryESINameStr).getEsiTypeId();
				
				if (primaryESITypeId != secondaryESITypeId) {
					
					isValid = false;
					
					message.append("Primary ESI and Secondary ESI must be same type. ");
					
				}
				
			}

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (DataManagerException e) {
			e.printStackTrace();
		} finally {

			if (isValid == false) {

				RestUtitlity.setValidationMessage(context, message.toString());

			}

		}

		return isValid;
	}


}
