package com.elitecore.netvertexsm.datamanager.gateway.profile.data;

public class PCCRuleMappingData {
	private long pccRuleMapId;
	private String attribute;
	private String policyKey;
	private String defaultValue;
	private String valueMapping;
	private String type;
	private Long profileId;
	private Long ruleMappingId;
	
	public Long getRuleMappingId() {
		return ruleMappingId;
	}
	public void setRuleMappingId(Long mappingRelId) {
		this.ruleMappingId = mappingRelId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getPccRuleMapId() {
		return pccRuleMapId;
	}
	public void setPccRuleMapId(long pccRuleMapId) {
		this.pccRuleMapId = pccRuleMapId;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getPolicyKey() {
		return policyKey;
	}
	public void setPolicyKey(String policyKey) {
		this.policyKey = policyKey;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getValueMapping() {
		return valueMapping;
	}
	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}
	public Long getProfileId() {
		return profileId;
	}
	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}
}
