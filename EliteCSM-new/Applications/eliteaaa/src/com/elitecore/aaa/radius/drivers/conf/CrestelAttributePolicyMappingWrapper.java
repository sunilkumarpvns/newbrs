package com.elitecore.aaa.radius.drivers.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.core.commons.InitializationFailedException;

public class CrestelAttributePolicyMappingWrapper {

	
private static final long serialVersionUID = 1L;
	
	private String policy;
	private List<Map<String,Object>> requestParameterMappingList;
	private List<Map<String,Object>> responseParameterMappingList;
	private String callMethod;
	private String mappingId;
	private String checkItem;
	private List<CheckedMapping> responseCheckParameterMappingList;
	
	
	
	public CrestelAttributePolicyMappingWrapper() {
		super();
		this.responseCheckParameterMappingList = new ArrayList<CheckedMapping>();
	}
	
	public String getPolicies() {
		return policy;
	}
	public void setPolicies(String policy) {
		this.policy = policy;
	}
	public List<Map<String, Object>> getRequestParameterMappingList() {
		return requestParameterMappingList;
	}
	public void setRequestParameterMappingList(
			List<Map<String, Object>> requestParameterMappingList) {
		this.requestParameterMappingList = requestParameterMappingList;
	}
	public List<Map<String, Object>> getResponseParameterMappingList() {
		return responseParameterMappingList;
	}
	public void setResponseParameterMappingList(
			List<Map<String, Object>> responseParameterMappingList) {
		this.responseParameterMappingList = responseParameterMappingList;
	}
	public String getCallMethod() {
		return callMethod;
	}
	public void setCallMethod(String callMethod) {
		this.callMethod = callMethod;
	}
	public String getMappingId() {
		return mappingId;
	}
	public void setMappingId(String mappingId) {
		this.mappingId = mappingId;
	}
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	
	public String getCheckItem() {
		return checkItem;
	}

	public void setCheckItem(String checkItem) {
		this.checkItem = checkItem;
	}

	public void addCheckedMapping(List<Map<String,String>> checkValueMap,List<Map<String,Object>> checkAttributeMap) throws InitializationFailedException{
		if(checkAttributeMap==null || checkValueMap==null || checkAttributeMap.isEmpty() || checkValueMap.isEmpty()){
			throw new InitializationFailedException("Invalid Configuration for Checked Mapping List");
		}
		CheckedMapping checkedMapping = new CheckedMapping(checkValueMap, checkAttributeMap);
		responseCheckParameterMappingList.add(checkedMapping);
	}
	
	public void setResponseCheckParameterMappingList(
			List<CheckedMapping> responseCheckParameterMappingList) {
		this.responseCheckParameterMappingList = responseCheckParameterMappingList;
	}

	public List<CheckedMapping> getCheckedMapping(){
		return responseCheckParameterMappingList;
	}
	
	public class CheckedMapping {
		List<Map<String,String>> checkValueMap;
		List<Map<String,Object>> checkAttributeMap;
		public CheckedMapping(List<Map<String, String>> checkValueMap, List<Map<String, Object>> checkAttributeMap) {
			super();
			this.checkValueMap = checkValueMap;
			this.checkAttributeMap = checkAttributeMap;
		}
		public List<Map<String, String>> getCheckValueMap() {
			return checkValueMap;
		}
		public void setCheckValueMap(List<Map<String, String>> checkValueMap) {
			this.checkValueMap = checkValueMap;
		}
		public List<Map<String, Object>> getCheckAttributeMapping() {
			return checkAttributeMap;
		}
		public void setCheckAttributeMap(List<Map<String, Object>> checkAttributeMap) {
			this.checkAttributeMap = checkAttributeMap;
		}
		
	}
}
