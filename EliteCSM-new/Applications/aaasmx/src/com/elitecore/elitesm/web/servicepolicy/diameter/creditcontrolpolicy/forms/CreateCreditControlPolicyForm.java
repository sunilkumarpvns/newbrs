/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   CreateCcpolicyForm.java                 		
 * ModualName CreditControlPolicy    			      		
 * Created on 13 April, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.servicepolicy.diameter.creditcontrolpolicy.forms;

import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CCResponseAttributes;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateCreditControlPolicyForm extends BaseWebForm{
   
	private static final long serialVersionUID = 1L;
	private java.lang.Long policyId;
    private String name;
    private String description;
    private String ruleSet;
    private String sessionManagement = "false";
    private java.lang.Long orderNumber;
    private String status;
    private String action;
    private String script;
    private Set<CCResponseAttributes>  ccResponseAttributesSet;
    private List<ScriptInstanceData> driverScriptList;
    
    /*Pre Post Plugin for CC Policy*/
    private String prePluginsList;
    private String postPluginList;
    
    private String defaultResponseBehaviorArgument;
    private String defaultResponseBehaviour;

	public java.lang.Long getPolicyId(){
        return policyId;
    }

	public void setPolicyId(java.lang.Long policyId) {
		this.policyId = policyId;
	}


    public String getName(){
        return name;
    }

	public void setName(String name) {
		this.name = name;
	}


    public String getDescription(){
        return description;
    }

	public void setDescription(String description) {
		this.description = description;
	}


    public String getRuleSet(){
        return ruleSet;
    }

	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}


    public java.lang.Long getOrderNumber(){
        return orderNumber;
    }

	public void setOrderNumber(java.lang.Long orderNumber) {
		this.orderNumber = orderNumber;
	}


    public String getStatus(){
        return status;
    }

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public Set<CCResponseAttributes> getCcResponseAttributesSet() {
		return ccResponseAttributesSet;
	}

	public void setCcResponseAttributesSet(Set<CCResponseAttributes> ccResponseAttributesSet) {
		this.ccResponseAttributesSet = ccResponseAttributesSet;
	}
	
	public String getSessionManagement() {
		return sessionManagement;
	}

	public void setSessionManagement(String sessionManagement) {
		this.sessionManagement = sessionManagement;
	}

	public String getPrePluginsList() {
		return prePluginsList;
	}

	public void setPrePluginsList(String prePluginsList) {
		this.prePluginsList = prePluginsList;
	}

	public String getPostPluginList() {
		return postPluginList;
	}

	public void setPostPluginList(String postPluginList) {
		this.postPluginList = postPluginList;
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
