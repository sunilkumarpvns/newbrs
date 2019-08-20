package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver.data;

import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlType(propOrder = {"serviceId","enabled"})
@ValidObject
public class ServiceDetailsData implements Validator{
	
	private static List<String> servicesTypeAliasName;	
	
	static{
		NetServerBLManager blManager = new NetServerBLManager();
		try{
			servicesTypeAliasName = blManager.getListOfServicesAliasNames();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@NotEmpty(message="Service Identifier must be specified for Service")
	private String serviceId;
	@NotEmpty(message="Service Enable value must be specified(In service list)")
	private String enabled;

	public ServiceDetailsData(){}
	
	@XmlElement(name = "service-id")
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	@XmlElement(name = "enabled")
	@Pattern(regexp=RestValidationMessages.REGEX_TRUE_FALSE,message="Invalid value of enabled paramter of Service list(true/false only)")
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled.toLowerCase();
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		if(Strings.isNullOrBlank(serviceId) == false){
			if(servicesTypeAliasName.contains(serviceId) == false){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Service Identifier "+serviceId + " is not valid");
			}
		}
		return isValid;
	}
}
