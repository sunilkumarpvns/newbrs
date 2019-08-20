package com.elitecore.elitesm.ws.rest.validator.server;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

/**
 * Contains code that validates the property annotated with <b>@ValidServiceType</b>
 * Allows only valid Server Type(RAD-AUTH, RAD-ACCT, RAD-DYNAUTH, DIAMETER-EAP, DIAMETER-NAS, DIAMETER-CC, 3GPP AAA SERVER)
 * @author nayana.rathod
 */

public class ServiceTypeValidator implements ConstraintValidator<ValidServiceType, List<String>>{

	private static final String RESPONSE_ERROR_MESSAGE = "Service Type is invalid, Kindly verify configured services list.";

	@Override
	public void initialize(ValidServiceType arg0) {
	}

	@Override
	public boolean isValid(List<String> serviceTypeList, ConstraintValidatorContext context) {
		
		boolean isValid = true;
		String errorMsg = "";
		
		try{
			
			if( Collectionz.isNullOrEmpty(serviceTypeList) == false){
				for( String service : serviceTypeList ){
					if( Strings.isNullOrEmpty(service) == false ){
						if( service.equalsIgnoreCase("Invalid") ){
							errorMsg = RESPONSE_ERROR_MESSAGE;
							isValid = false;
							break;
						}else{
							NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
							INetServiceTypeData netServiceTypeData = netServiceBLManager.getNetServiceType(service.trim());
							if( netServiceTypeData == null ){
								errorMsg = RESPONSE_ERROR_MESSAGE;
								isValid = false;
								break;
							}
						}
					}
				}
			}
		} catch (DataManagerException e) {
			errorMsg = RESPONSE_ERROR_MESSAGE;
			isValid = false;
			e.printStackTrace();
		} catch (Exception e) {
			errorMsg = RESPONSE_ERROR_MESSAGE;
			isValid = false;
			e.printStackTrace();
		}
		
		if( isValid == false ){
			RestUtitlity.setValidationMessage(context, errorMsg);
		}
		
		return isValid;
	}
}
