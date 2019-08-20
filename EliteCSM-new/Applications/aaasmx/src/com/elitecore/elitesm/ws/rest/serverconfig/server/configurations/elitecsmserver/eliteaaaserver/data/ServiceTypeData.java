package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlType(propOrder = {})
@ValidObject
public class ServiceTypeData implements Validator{

	private List<ServiceDetailsData> configuredServiceList;
	
	public ServiceTypeData(){
		configuredServiceList = new ArrayList<ServiceDetailsData>();
	}
	
	@XmlElement(name = "service")
	@Valid
	public List<ServiceDetailsData> getConfiguredServiceList() {
		return configuredServiceList;
	}

	public void setConfiguredServiceList(List<ServiceDetailsData> configuredServiceList) {
		this.configuredServiceList = configuredServiceList;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		Set<String> serviceIdentifiers = new HashSet<String>();
		if(Collectionz.isNullOrEmpty(configuredServiceList) == false){
			for(ServiceDetailsData serviceDetailsData : configuredServiceList){
				String serviceIdentifier = serviceDetailsData.getServiceId();
				boolean isDuplicate = serviceIdentifiers.add(serviceDetailsData.getServiceId());
				if(isDuplicate == false){
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Duplicate Service Identifier( "+serviceIdentifier+" ) found");
					break;
				}
				
			}
		}
		return isValid;
	}

}
