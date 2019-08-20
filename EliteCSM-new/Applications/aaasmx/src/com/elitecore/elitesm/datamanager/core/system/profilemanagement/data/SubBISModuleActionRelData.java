package com.elitecore.elitesm.datamanager.core.system.profilemanagement.data;

import java.io.Serializable;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class SubBISModuleActionRelData extends BaseData implements ISubBISModuleActionRelData,Serializable{
	private String actionId;
	private String subBusinessModuleId;
	private String status;
	private IActionData actionData;
	private ISubBISModuleData subBISModuleData;
	public String getActionId() {
		return actionId;
	}
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSubBusinessModuleId() {
		return subBusinessModuleId;
	}
	public void setSubBusinessModuleId(String subBusinessModuleId) {
		this.subBusinessModuleId = subBusinessModuleId;
	}
	public IActionData getActionData() {
		return actionData;
	}
	public void setActionData(IActionData actionData) {
		this.actionData = actionData;
	}
	public ISubBISModuleData getSubBISModuleData() {
		return subBISModuleData;
	}
	public void setSubBISModuleData(ISubBISModuleData subBISModuleData) {
		this.subBISModuleData = subBISModuleData;
	}
}
