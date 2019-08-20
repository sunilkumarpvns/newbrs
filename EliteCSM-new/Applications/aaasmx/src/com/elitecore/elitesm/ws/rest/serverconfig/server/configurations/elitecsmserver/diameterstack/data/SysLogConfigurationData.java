package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data;

import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.alert.AlertListenerBLManager;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlType(propOrder = {"hostIpAddress","facility"})
@ValidObject
public class SysLogConfigurationData  implements Validator{
	private String hostIpAddress;
	
	@NotEmpty(message="Facility(In Sys Log Configuration) value must be specified ")
	private String facility;
	
	public SysLogConfigurationData() {
	}
	
	@XmlElement(name = "facility")
	public String getFacility() {
		return facility;
	}
	
	public void setFacility(String facility){
		this.facility = facility.toUpperCase();
	}
	
	@XmlElement(name = "address",type =String.class)
	public String getHostIpAddress() {
		return hostIpAddress;
	}
	
	public void setHostIpAddress(String hostIpAddress){
		this.hostIpAddress = hostIpAddress;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context){
		boolean isValid = true;
		AlertListenerBLManager alertListenerBLManager =new AlertListenerBLManager();
		if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(facility) == false && Strings.isNullOrBlank(facility) == false){
			try{
				List<String> sysLogNames = alertListenerBLManager.getSysLogNames();
				if(sysLogNames.contains(facility) == false){
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Invalid value of Facility(Sys Log Configuration)");
				}
				
			}catch(Exception e){
				isValid = false;
			}
		}
		
		return isValid;
	}
	
}
