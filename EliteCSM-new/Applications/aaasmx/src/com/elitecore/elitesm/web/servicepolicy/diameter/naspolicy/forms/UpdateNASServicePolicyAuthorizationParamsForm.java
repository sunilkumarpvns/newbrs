package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateNASServicePolicyAuthorizationParamsForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	
	private String nasPolicyId;
	private String rejectOnCheckItemNotFound;
	private String rejectOnRejectItemNotFound;
	private String actionOnPolicyNotFound;
	private String auditUId;
	private String name;
	private String wimax;
	private String gracePolicy;
	private List<GracepolicyData> gracePolicyList;
	private List<DiameterConcurrencyData> diameterConcurrencyDataList;
	private String diameterConcurrency;
	private String additionalDiameterConcurrency;
	private Long defaultSessionTimeout;
	
	private String action;
	private String authPrePluginJson;
	private String authPostPluginJson;
	
	public String getNasPolicyId() {
		return nasPolicyId;
	}
	public void setNasPolicyId(String nasPolicyId) {
		this.nasPolicyId = nasPolicyId;
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
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public List<GracepolicyData> getGracePolicyList() {
		return gracePolicyList;
	}
	public void setGracePolicyList(List<GracepolicyData> gracePolicyList) {
		this.gracePolicyList = gracePolicyList;
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
	public Long getDefaultSessionTimeout() {
		return defaultSessionTimeout;
	}
	public void setDefaultSessionTimeout(Long defaultSessionTimeout) {
		this.defaultSessionTimeout = defaultSessionTimeout;
	}
	public String getAuthPrePluginJson() {
		return authPrePluginJson;
	}
	public void setAuthPrePluginJson(String authPrePluginJson) {
		this.authPrePluginJson = authPrePluginJson;
	}
	public String getAuthPostPluginJson() {
		return authPostPluginJson;
	}
	public void setAuthPostPluginJson(String authPostPluginJson) {
		this.authPostPluginJson = authPostPluginJson;
	}
}
