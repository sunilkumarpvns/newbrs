package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateNasServicePolicyBasicForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String ruleSet;
	private String nasPolicyId;
	private String action;
	private Integer requestType;
	private String sessionManagement;
	private String auditUId;
	private String defaultResponseBehaviour;
	private String defaultResponseBehaviourArgument;
	
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
	public String getNasPolicyId() {
		return nasPolicyId;
	}
	public void setNasPolicyId(String nasPolicyId) {
		this.nasPolicyId = nasPolicyId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public Integer getRequestType() {
		return requestType;
	}
	public void setRequestType(Integer requestType) {
		this.requestType = requestType;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public String getSessionManagement() {
		return sessionManagement;
	}
	public void setSessionManagement(String sessionManagement) {
		this.sessionManagement = sessionManagement;
	}
	public String getDefaultResponseBehaviour() {
		return defaultResponseBehaviour;
	}
	public void setDefaultResponseBehaviour(String defaultResponseBehaviour) {
		this.defaultResponseBehaviour = defaultResponseBehaviour;
	}
	public String getDefaultResponseBehaviourArgument() {
		return defaultResponseBehaviourArgument;
	}
	public void setDefaultResponseBehaviourArgument(String defaultResponseBehaviourArgument) {
		this.defaultResponseBehaviourArgument = defaultResponseBehaviourArgument;
	}
	
}
