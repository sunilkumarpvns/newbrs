package com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms;

import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPResponseAttributes;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateEAPPolicyForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	private java.lang.Long policyId;
    private String name;
    private String description;
    private String ruleSet="0:1=*";
    private String sessionManagement = "false";
    private java.lang.Long orderNumber;
    private String status;
    private int caseSensitiveUserIdentity;
	private String multipleUserIdentity="0:1";
	private String stripUserIdentity="false";
	private String realmPattern="suffix";
	private String realmSeparator="@";
	private String trimUserIdentity="false";
	private String trimPassword="false";
    private String eapConfigId;
    private String action;
    private List<EAPConfigData> eapConfigurationList;
    private Integer requestType = 3;
	private String rejectOnCheckItemNotFound = "false";
	private String rejectOnRejectItemNotFound = "false";
	private String actionOnPolicyNotFound = "false";
	private String script;
	private Set<EAPResponseAttributes> eapResponseAttributesSet;
	private List<GracepolicyData> gracePolicyList;
	private String wimax;
	private String gracePolicy;
	private String anonymousProfileIdentity;
	private String diameterConcurrency;
	private String additionalDiameterConcurrency;
	private List<DiameterConcurrencyData> diameterConcurrencyDataList;
	private String postPluginList;
	private String prePluginsList;
	private Long defaultSessionTimeout = 600L;
	private String cui;
	private String cuiResponseAttributes;
	private String advancedCuiExpression;
	private String defaultResponseBehaviorArgument;
    private String defaultResponseBehaviour;
    private List<ScriptInstanceData> driverScriptList;
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public java.lang.Long getPolicyId() {
		return policyId;
	}
	
	public void setPolicyId(java.lang.Long policyId) {
		this.policyId = policyId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getRuleSet() {
		return ruleSet;
	}
	
	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}
	
	public java.lang.Long getOrderNumber() {
		return orderNumber;
	}
	
	public void setOrderNumber(java.lang.Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public int getCaseSensitiveUserIdentity() {
		return caseSensitiveUserIdentity;
	}
	
	public void setCaseSensitiveUserIdentity(int caseSensitiveUserIdentity) {
		this.caseSensitiveUserIdentity = caseSensitiveUserIdentity;
	}
	
	public String getMultipleUserIdentity() {
		return multipleUserIdentity;
	}
	
	public void setMultipleUserIdentity(String multipleUserIdentity) {
		this.multipleUserIdentity = multipleUserIdentity;
	}
	
	public String getStripUserIdentity() {
		return stripUserIdentity;
	}
	
	public void setStripUserIdentity(String stripUserIdentity) {
		this.stripUserIdentity = stripUserIdentity;
	}
	
	public String getRealmPattern() {
		return realmPattern;
	}
	
	public void setRealmPattern(String realmPattern) {
		this.realmPattern = realmPattern;
	}
	
	public String getRealmSeparator() {
		return realmSeparator;
	}
	
	public void setRealmSeparator(String realmSeparator) {
		this.realmSeparator = realmSeparator;
	}
	
	public String getTrimUserIdentity() {
		return trimUserIdentity;
	}
	
	public void setTrimUserIdentity(String trimUserIdentity) {
		this.trimUserIdentity = trimUserIdentity;
	}
	
	public String getTrimPassword() {
		return trimPassword;
	}
	
	public void setTrimPassword(String trimPassword) {
		this.trimPassword = trimPassword;
	}
	
	public String getEapConfigId() {
		return eapConfigId;
	}
	
	public void setEapConfigId(String eapConfigId) {
		this.eapConfigId = eapConfigId;
	}
	
	public List<EAPConfigData> getEapConfigurationList() {
		return eapConfigurationList;
	}
	
	public void setEapConfigurationList(List<EAPConfigData> eapConfigurationList) {
		this.eapConfigurationList = eapConfigurationList;
	}

	public Integer getRequestType() {
		return requestType;
	}

	public void setRequestType(Integer requestType) {
		this.requestType = requestType;
	}

	public String getRejectOnCheckItemNotFound() {
		return rejectOnCheckItemNotFound;
	}

	public void setRejectOnCheckItemNotFound(String rejectOnCheckItemNotFound) {
		this.rejectOnCheckItemNotFound = rejectOnCheckItemNotFound;
	}

	public String getRejectOnRejectItemNotFound() {
		return rejectOnRejectItemNotFound;
	}
	
	public void setRejectOnRejectItemNotFound(String rejectOnRejectItemNotFound) {
		this.rejectOnRejectItemNotFound = rejectOnRejectItemNotFound;
	}

	public String getActionOnPolicyNotFound() {
		return actionOnPolicyNotFound;
	}
	
	public void setActionOnPolicyNotFound(String actionOnPolicyNotFound) {
		this.actionOnPolicyNotFound = actionOnPolicyNotFound;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public Set<EAPResponseAttributes> getEapResponseAttributesSet() {
		return eapResponseAttributesSet;
	}

	public void setEapResponseAttributesSet(Set<EAPResponseAttributes> eapResponseAttributesSet) {
		this.eapResponseAttributesSet = eapResponseAttributesSet;
	}

	public List<GracepolicyData> getGracePolicyList() {
		return gracePolicyList;
	}

	public void setGracePolicyList(List<GracepolicyData> gracePolicyList) {
		this.gracePolicyList = gracePolicyList;
	}

	public String getWimax() {
		return wimax;
	}

	public void setWimax(String wimax) {
		this.wimax = wimax;
	}

	public String getGracePolicy() {
		return gracePolicy;
	}

	public void setGracePolicy(String gracePolicy) {
		this.gracePolicy = gracePolicy;
	}

	public String getAnonymousProfileIdentity() {
		return anonymousProfileIdentity;
	}

	public void setAnonymousProfileIdentity(String anonymousProfileIdentity) {
		this.anonymousProfileIdentity = anonymousProfileIdentity;
	}

	public String getSessionManagement() {
		return sessionManagement;
	}

	public void setSessionManagement(String sessionManagement) {
		this.sessionManagement = sessionManagement;
	}

	public String getDiameterConcurrency() {
		return diameterConcurrency;
	}

	public void setDiameterConcurrency(String diameterConcurrency) {
		this.diameterConcurrency = diameterConcurrency;
	}

	public String getAdditionalDiameterConcurrency() {
		return additionalDiameterConcurrency;
	}

	public void setAdditionalDiameterConcurrency(
			String additionalDiameterConcurrency) {
		this.additionalDiameterConcurrency = additionalDiameterConcurrency;
	}

	public List<DiameterConcurrencyData> getDiameterConcurrencyDataList() {
		return diameterConcurrencyDataList;
	}

	public void setDiameterConcurrencyDataList(
			List<DiameterConcurrencyData> diameterConcurrencyDataList) {
		this.diameterConcurrencyDataList = diameterConcurrencyDataList;
	}

	public String getPostPluginList() {
		return postPluginList;
	}

	public void setPostPluginList(String postPluginList) {
		this.postPluginList = postPluginList;
	}

	public String getPrePluginsList() {
		return prePluginsList;
	}

	public void setPrePluginsList(String prePluginsList) {
		this.prePluginsList = prePluginsList;
	}

	public Long getDefaultSessionTimeout() {
		return defaultSessionTimeout;
	}

	public void setDefaultSessionTimeout(Long defaultSessionTimeout) {
		this.defaultSessionTimeout = defaultSessionTimeout;
	}

	public String getCui() {
		return cui;
	}

	public void setCui(String cui) {
		this.cui = cui;
	}

	public String getCuiResponseAttributes() {
		return cuiResponseAttributes;
	}

	public void setCuiResponseAttributes(String cuiResponseAttributes) {
		this.cuiResponseAttributes = cuiResponseAttributes;
	}

	public String getAdvancedCuiExpression() {
		return advancedCuiExpression;
	}

	public void setAdvancedCuiExpression(String advancedCuiExpression) {
		this.advancedCuiExpression = advancedCuiExpression;
	}
	public String getDefaultResponseBehaviorArgument() {
		return defaultResponseBehaviorArgument;
	}

	public void setDefaultResponseBehaviorArgument(String defaultResponseBehaviorArgument) {
		this.defaultResponseBehaviorArgument = defaultResponseBehaviorArgument;
	}

	public String getDefaultResponseBehaviour() {
		return defaultResponseBehaviour;
	}

	public void setDefaultResponseBehaviour(String defaultResponseBehaviour) {
		this.defaultResponseBehaviour = defaultResponseBehaviour;
	}

	public List<ScriptInstanceData> getDriverScriptList() {
		return driverScriptList;
	}

	public void setDriverScriptList(List<ScriptInstanceData> driverScriptList) {
		this.driverScriptList = driverScriptList;
	}
}
