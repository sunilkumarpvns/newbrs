package com.elitecore.elitesm.ws.rest.validator.server;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

/**
 * Contains code that validates the property annotated with <b>@ValidServerType</b>
 * Allows only valid Server Type(Resource Manager, EliteCSM Server)
 * @author nayana.rathod
 */

public class ServerTypeValidator implements ConstraintValidator<ValidServerType, Object>{

	private static final String INVALID_SERVER_TYPE = "Invalid Server Type(Valid Server Type are Resource Manager , EliteCSM Server)";

	@Override
	public void initialize(ValidServerType arg0) {
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		
		boolean isValid = true;
		String msg = "";
		
		try {
			
			if (Strings.isNullOrEmpty((String) value)) {
				 msg = "ServerType is a compulsory field Please enter required data in this field";
			     isValid = false;
			} else if (RestValidationMessages.INVALID.equals(value)) {
				 msg = INVALID_SERVER_TYPE;
				 isValid = false;
			} else {
				
				try {
					NetServerBLManager netServerBLManager = new NetServerBLManager();
					INetServerTypeData netServerTypeData = netServerBLManager.getNetServerType((String) value);
					
					if( netServerTypeData == null ){
						msg = INVALID_SERVER_TYPE;
						isValid = false;
					}
					
				} catch (DataManagerException dme) {
					dme.printStackTrace();
					msg = INVALID_SERVER_TYPE;
					isValid = false;
				}
				
			}
		} catch ( Exception e ){
			e.printStackTrace();
			msg = INVALID_SERVER_TYPE;
			isValid = false;
		}
		
		if( isValid == false ){
			RestUtitlity.setValidationMessage(context, msg);
		}
		
		return isValid;
		
	}
}
