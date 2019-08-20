package com.elitecore.aaa.radius.drivers.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.core.commons.configuration.LoadConfigurationException;


public class CrestelAttributePolicyMappingDictator {

public static final String MODULE = "POLICY_BASED_MAPPING_DICTATOR";
	
	private Map<String,List<CrestelAttributePolicyMappingWrapper>> policiesMap = new HashMap<String, List<CrestelAttributePolicyMappingWrapper>>();
//	private List<Map<String, Object>> responseErrorConfigList;
	private Map<String,CrestelAttributePolicyMappingWrapper> attributePolicyMappingConfiguration = new HashMap<String, CrestelAttributePolicyMappingWrapper>();
	
	
	public CrestelAttributePolicyMappingDictator() {
		
	}

	public void  addConfiguration(String requestType,String id,String policies,String callMethod,String checkItemName) throws LoadConfigurationException{
		
		
		CrestelAttributePolicyMappingWrapper attributePolicyMappingWraper = attributePolicyMappingConfiguration.get(id);
//		CrestelAttributePolicyMappingWrapper attributePolicyMappingWraper = new CrestelAttributePolicyMappingWrapper();
		
		List<CrestelAttributePolicyMappingWrapper> configuration = (List<CrestelAttributePolicyMappingWrapper>) policiesMap.get(requestType);
		if(attributePolicyMappingWraper!=null){
				attributePolicyMappingWraper.setPolicies(policies);
				attributePolicyMappingWraper.setMappingId(id);
				attributePolicyMappingWraper.setCheckItem(checkItemName);
				if(id != null) {
					attributePolicyMappingWraper.setRequestParameterMappingList(attributePolicyMappingConfiguration.get(id).getRequestParameterMappingList());
					attributePolicyMappingWraper.setResponseParameterMappingList(attributePolicyMappingConfiguration.get(id).getResponseParameterMappingList());
					attributePolicyMappingWraper.setResponseCheckParameterMappingList(attributePolicyMappingConfiguration.get(id).getCheckedMapping());
					attributePolicyMappingWraper.setCallMethod(callMethod);					
				}

			if(configuration==null){
				configuration  = new ArrayList<CrestelAttributePolicyMappingWrapper>();
				configuration.add(attributePolicyMappingWraper);
				policiesMap.put(requestType, configuration);
			}else{
				configuration.add(attributePolicyMappingWraper);
			}
		}else{
			throw new LoadConfigurationException("No Configuration Found for the Configured Policy Id : "+id);
		}
	}
	
	public List<CrestelAttributePolicyMappingWrapper> getMappingPolicy(String requestType){
		return policiesMap.get(requestType);
	}
	
//	public List<Map<String, Object>> getResponseErrorConfigList() {
//		return responseErrorConfigList;
//	}
//
//	public void setResponseErrorConfigList(
//			List<Map<String, Object>> responseErrorConfigList) {
//		this.responseErrorConfigList = responseErrorConfigList;
//	}
	
	public CrestelAttributePolicyMappingWrapper addAttributePolicyMappingConfiguration(String identifier,CrestelAttributePolicyMappingWrapper config){
		if(!attributePolicyMappingConfiguration.containsKey(identifier)){
//			getLogger().info(MODULE, "Configuration added to the key : "+identifier);
			return attributePolicyMappingConfiguration.put(identifier, config);
		}else{
//			getLogger().warn(MODULE, "Configuration allready exist, so Configuration not added to the key : "+identifier);
			return config;
		}
	}

	public List<CrestelAttributePolicyMappingWrapper> getCommunicationConfiguration(String requestType){
		return policiesMap.get(requestType);
	}
	

}
