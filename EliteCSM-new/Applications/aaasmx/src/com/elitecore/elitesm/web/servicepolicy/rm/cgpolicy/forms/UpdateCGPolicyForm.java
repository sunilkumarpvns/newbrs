package com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyDriverRelationData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateCGPolicyForm extends BaseWebForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private java.lang.String policyId;
    private String name;
    private String description;
    private String ruleSet;
    private java.lang.Long orderNumber;
    private String status;
    private String action;
    private String[] selecteddriverIds;
    private String script;
    private List<CGPolicyDriverRelationData> driversList;
    private String auditUId;
    private List<ScriptInstanceData> driverScriptList;
    
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
	
	public List<CGPolicyDriverRelationData> getDriversList() {
		return driversList;
	}
	
	public void setDriversList(List<CGPolicyDriverRelationData> driversList) {
		this.driversList = driversList;
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

	public List<ScriptInstanceData> getDriverScriptList() {
		return driverScriptList;
	}

	public void setDriverScriptList(List<ScriptInstanceData> driverScriptList) {
		this.driverScriptList = driverScriptList;
	}
	
}
