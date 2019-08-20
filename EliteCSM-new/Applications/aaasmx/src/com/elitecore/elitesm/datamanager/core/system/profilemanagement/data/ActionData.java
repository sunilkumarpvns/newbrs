package com.elitecore.elitesm.datamanager.core.system.profilemanagement.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class ActionData extends BaseData implements IActionData{
	private String actionId;
	private String name;
	private String alias;
	private String description;
	private String actionTypeId;
	private String parentActionId;
	private Long actionLevel;
	private String systemGenerated;
	private String screenId;
	private String status;
	private String freezeProfile;
	public String getActionId() {
		return actionId;
	}
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	public Long getActionLevel() {
		return actionLevel;
	}
	public void setActionLevel(Long actionLevel) {
		this.actionLevel = actionLevel;
	}
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
	public String getParentActionId() {
		return parentActionId;
	}
	public void setParentActionId(String parentActionId) {
		this.parentActionId = parentActionId;
	}
	public String getScreenId() {
		return screenId;
	}
	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFreezeProfile() {
		return freezeProfile;
	}
	public void setFreezeProfile(String freezeProfile) {
		this.freezeProfile = freezeProfile;
	}
}
