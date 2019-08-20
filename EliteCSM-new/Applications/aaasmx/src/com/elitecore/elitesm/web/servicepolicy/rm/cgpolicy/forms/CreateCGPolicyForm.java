package com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateCGPolicyForm extends BaseWebForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private java.lang.Long policyId;
    private String name;
    private String description;
    private String ruleSet;
    private java.lang.Long orderNumber;
    private String status;   
    private String action;
    private String script;
    private List<ScriptInstanceData> driverScriptList;
    
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

	public List<ScriptInstanceData> getDriverScriptList() {
		return driverScriptList;
	}

	public void setDriverScriptList(List<ScriptInstanceData> driverScriptList) {
		this.driverScriptList = driverScriptList;
	}
	
 }
