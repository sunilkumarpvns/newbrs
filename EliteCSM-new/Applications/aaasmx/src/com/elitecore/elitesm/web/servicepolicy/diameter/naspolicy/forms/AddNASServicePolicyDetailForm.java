package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class AddNASServicePolicyDetailForm extends BaseWebForm{


	private static final long serialVersionUID = 1L;	

	private String authPrePluginsList;
	private String acctPrePluginsList;
	
	private String authPostPluginsList;
	private String acctPostPluginsList;
	private String authScript;
	private String acctScript;
	
	private String authPrePluginJson;
	private String authPostPluginJson;
	private String acctPrePluginJson;
	private String acctPostPluginJson;
	
	private	String defaultResponseBehaviourParameter;
	private String defaultResponseBehaviourType;
	
	public String getAuthPrePluginsList() {
		return authPrePluginsList;
	}
	public void setAuthPrePluginsList(String authPrePluginsList) {
		this.authPrePluginsList = authPrePluginsList;
	}
	public String getAcctPrePluginsList() {
		return acctPrePluginsList;
	}
	public void setAcctPrePluginsList(String acctPrePluginsList) {
		this.acctPrePluginsList = acctPrePluginsList;
	}
	public String getAuthPostPluginsList() {
		return authPostPluginsList;
	}
	public void setAuthPostPluginsList(String authPostPluginsList) {
		this.authPostPluginsList = authPostPluginsList;
	}
	public String getAcctPostPluginsList() {
		return acctPostPluginsList;
	}
	public void setAcctPostPluginsList(String acctPostPluginsList) {
		this.acctPostPluginsList = acctPostPluginsList;
	}
	public String getAuthScript() {
		return authScript;
	}
	public void setAuthScript(String authScript) {
		this.authScript = authScript;
	}
	public String getAcctScript() {
		return acctScript;
	}
	public void setAcctScript(String acctScript) {
		this.acctScript = acctScript;
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
	public String getAcctPrePluginJson() {
		return acctPrePluginJson;
	}
	public void setAcctPrePluginJson(String acctPrePluginJson) {
		this.acctPrePluginJson = acctPrePluginJson;
	}
	public String getAcctPostPluginJson() {
		return acctPostPluginJson;
	}
	public void setAcctPostPluginJson(String acctPostPluginJson) {
		this.acctPostPluginJson = acctPostPluginJson;
	}
	public String getDefaultResponseBehaviourParameter() {
		return defaultResponseBehaviourParameter;
	}
	public void setDefaultResponseBehaviourParameter(String defaultResponseBehaviourParameter) {
		this.defaultResponseBehaviourParameter = defaultResponseBehaviourParameter;
	}
	public String getDefaultResponseBehaviourType() {
		return defaultResponseBehaviourType;
	}
	public void setDefaultResponseBehaviourType(String defaultResponseBehaviourType) {
		this.defaultResponseBehaviourType = defaultResponseBehaviourType;
	}
	
	
	
}
