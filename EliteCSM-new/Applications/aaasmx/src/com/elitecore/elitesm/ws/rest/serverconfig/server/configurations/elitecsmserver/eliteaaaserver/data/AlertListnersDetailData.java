package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver.data;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlType(propOrder = {})
@ValidObject
public class AlertListnersDetailData implements Validator{

	private List<String> alertListnersList;
	
	public AlertListnersDetailData(){
		alertListnersList = new ArrayList<String>();
	}
	
	@XmlElement(name = "alert-listener")
	@Size(min=1,message="Atleast one Alert Listener must be specified")
	public List<String> getAlertListnersList() {
		return alertListnersList;
	}

	public void setAlertListnersList(List<String> alertListnersList) {
		this.alertListnersList = alertListnersList;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		if(Collectionz.isNullOrEmpty(alertListnersList) == false){
			LinkedList<String> alertConfigurationNames = new LinkedList<String>();
			for(String alertConfigurationName : alertListnersList){
				if(Strings.isNullOrBlank(alertConfigurationName) == false){
					if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(alertConfigurationName) == false){
						AlertListenerBLManager alertListenerBLManager = new AlertListenerBLManager();
						try {
							AlertListenerData alertListenerData = alertListenerBLManager.getAlertListenerDataByName(alertConfigurationName);
							alertConfigurationNames.add(alertConfigurationName);
						} catch (DataManagerException e) {
							isValid = false;
							RestUtitlity.setValidationMessage(context, "Configured "+alertConfigurationName + " Alert Listener does not exists");
							break;
						}
					}else{
						alertConfigurationNames.add("");
					}
				}
			}
			if(isValid){
				alertListnersList.clear();
				alertListnersList.addAll(alertConfigurationNames);
			}
		}
		return isValid;
	} 
}

