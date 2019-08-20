package com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data;

import com.elitecore.netvertexsm.datamanager.core.base.data.BaseData;

public class ActionTypeData extends BaseData implements IActionTypeData{
	private String actionTypeId;
	private String alias;
	private String name;
	private String description;
	private String systemGenerated;
	public String getActionTypeId() {
		return actionTypeId;
	}
	public void setActionTypeId(String actionTypeId) {
		this.actionTypeId = actionTypeId;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
}
