package com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms;

import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyAuthDriverRelationData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPResponseAttributes;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateEAPPolicyForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	private java.lang.String policyId;
    private String name;
    private String description;
    private String ruleSet;
    private String sessionManagement;
    private java.lang.Long orderNumber;
    private String status;
    private int caseSensitiveUserIdentity;
	private String multipleUserIdentity="0:1";
	private boolean stripUserIdentity=false;
	private String realmPattern="suffix";
	private String realmSeparator="@";
	private boolean trimUserIdentity=false;
	private boolean trimPassword=false;
    private String eapConfigId;
    private String action;
    private List<EAPConfigData> eapConfigurationList;
    private String[] selecteddriverIds;
    private String[] selectedAdditionalDriverIds;
    private List<EAPPolicyAuthDriverRelationData> driversList;
    private Integer requestType = 3;
    private String rejectOnCheckItemNotFound = "false";
	private String rejectOnRejectItemNotFound = "false";
	private String actionOnPolicyNotFound = "false";
	private List<EAPPolicyAdditionalDriverRelData> additionalDriverRelDataList;
	private String script;
	private String auditUId;
	private Set<EAPResponseAttributes> eapResponseSet;
	private List<GracepolicyData> gracePolicyList;
	private String wimax;
	private String gracePolicy;
	private String anonymousProfileIdentity;
	private List<DiameterConcurrencyData> diameterConcurrencyDataList;
	private String diameterConcurrency;
	private String additionalDiameterConcurrency;
	private String postPluginList;
	private String prePluginsList;
	private Long defaultSessionTimeout;
	private String cui;
	private String cuiResponseAttributes;
	private String advancedCuiExpression;
	private String defaultResponseBehavior;
	private String defaultResponseBehaviorArgument;
	private List<ScriptInstanceData> driverScriptList;
	
	public boolean isStripUserIdentity() {
		return stripUserIdentity;
	}

	public void setStripUserIdentity(boolean stripUserIdentity) {
		this.stripUserIdentity = stripUserIdentity;
	}

	public boolean isTrimUserIdentity() {
		return trimUserIdentity;
	}

	public void setTrimUserIdentity(boolean trimUserIdentity) {
		this.trimUserIdentity = trimUserIdentity;
	}

	public boolean isTrimPassword() {
		return trimPassword;
	}

	public void setTrimPassword(boolean trimPassword) {
		this.trimPassword = trimPassword;
	}

	public List<EAPConfigData> getEapConfigurationList() {
		return eapConfigurationList;
	}

	public void setEapConfigurationList(List<EAPConfigData> eapConfigurationList) {
		this.eapConfigurationList = eapConfigurationList;
	}

	public java.lang.String getPolicyId() {
		return policyId;
	}
	
	public void setPolicyId(java.lang.String policyId) {
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
	
	public String getEapConfigId() {
		return eapConfigId;
	}
	
	public void setEapConfigId(String eapConfigId) {
		this.eapConfigId = eapConfigId;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public String[] getSelecteddriverIds() {
		return selecteddriverIds;
	}
	
	public void setSelecteddriverIds(String[] selecteddriverIds) {
		this.selecteddriverIds = selecteddriverIds;
	}
	
	public String[] getSelectedAdditionalDriverIds() {
		return selectedAdditionalDriverIds;
	}

	public void setSelectedAdditionalDriverIds(String[] selectedAdditionalDriverIds) {
		this.selectedAdditionalDriverIds = selectedAdditionalDriverIds;
	}

	public List<EAPPolicyAuthDriverRelationData> getDriversList() {
		return driversList;
	}
	
	public void setDriversList(List<EAPPolicyAuthDriverRelationData> driversList) {
		this.driversList = driversList;
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

	public List<EAPPolicyAdditionalDriverRelData> getAdditionalDriverRelDataList() {
		return additionalDriverRelDataList;
	}

	public void setAdditionalDriverRelDataList(
			List<EAPPolicyAdditionalDriverRelData> additionalDriverRelDataList) {
		this.additionalDriverRelDataList = additionalDriverRelDataList;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getAuditUId() {
		return auditUId;
	}

	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}

	public Set<EAPResponseAttributes> getEapResponseSet() {
		return eapResponseSet;
	}

	public void setEapResponseSet(Set<EAPResponseAttributes> eapResponseSet) {
		this.eapResponseSet = eapResponseSet;
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

	public List<DiameterConcurrencyData> getDiameterConcurrencyDataList() {
		return diameterConcurrencyDataList;
	}

	public void setDiameterConcurrencyDataList(
			List<DiameterConcurrencyData> diameterConcurrencyDataList) {
		this.diameterConcurrencyDataList = diameterConcurrencyDataList;
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

	public String getDefaultResponseBehavior() {
		return defaultResponseBehavior;
	}

	public void setDefaultResponseBehavior(String defaultResponseBehavior) {
		this.defaultResponseBehavior = defaultResponseBehavior;
	}

	public String getDefaultResponseBehaviorArgument() {
		return defaultResponseBehaviorArgument;
	}

	public void setDefaultResponseBehaviorArgument(String defaultResponseBehaviorArgument) {
		this.defaultResponseBehaviorArgument = defaultResponseBehaviorArgument;
	}

	public List<ScriptInstanceData> getDriverScriptList() {
		return driverScriptList;
	}

	public void setDriverScriptList(List<ScriptInstanceData> driverScriptList) {
		this.driverScriptList = driverScriptList;
	}
	
}
