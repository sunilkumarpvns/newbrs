package com.elitecore.elitesm.ws.ytl.cal.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "subscriber-validation-detail")
public class SubscriberValidationDetail {

	private List<ValidationData> validationData = new ArrayList<ValidationData>();
	
	private Map<String, Map<String, List<String>>> validatorMap = new HashMap<String, Map<String,List<String>>>();
	
	@XmlElement(name = "validation-data")
	public List<ValidationData> getValidationData() {
		return validationData;
	}
	
	@XmlTransient
	public Map<String, Map<String, List<String>>> getValidatorMap() {
		return validatorMap;
	}
	
	public void postRead() {
		Map<String, Map<String, List<String>>> validatorMap = new HashMap<String, Map<String,List<String>>>();
		for (ValidationData data : validationData) {
			Map<String, List<String>> realmToProfileSet = new HashMap<String,List<String>>();
			realmToProfileSet.put(data.getRealmName(), data.getProfileSets());
			validatorMap.put(data.getOrganizationName(), realmToProfileSet);
		}
		this.validatorMap = validatorMap;
	}
}
