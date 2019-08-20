package com.elitecore.netvertexsm.datamanager.gateway.profile.data;

import java.util.List;

public class DefaultAttributeMappingData {

	private long ID;
	private String parameterUsageType;
	private String policyKey;
	private String diameterAttribute;
	private String defaultValue;
	private String valueMapping = null;
	
	private List<DefaultValueMappingData> defaultValueMapList;
	
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public List<DefaultValueMappingData> getDefaultValueMapList() {
		return defaultValueMapList;
	}
	public void setDefaultValueMapList(
			List<DefaultValueMappingData> defaultValueMapList) {
		this.defaultValueMapList = defaultValueMapList;
	}
	public long getID() {
		return ID;
	}
	public void setID(long ID) {
		this.ID = ID;
	}
	public String getParameterUsageType() {
		return parameterUsageType;
	}
	public void setParameterUsageType(String parameterUsageType) {
		this.parameterUsageType = parameterUsageType;
	}
	public String getPolicyKey() {
		return policyKey;
	}
	public void setPolicyKey(String policyKey) {
		this.policyKey = policyKey;
	}
	public String getDiameterAttribute() {
		return diameterAttribute;
	}
	public void setDiameterAttribute(String diameterAttribute) {
		this.diameterAttribute = diameterAttribute;
	}
	public String getValueMapping() {
		return valueMapping;
	}
	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}
	
	
}