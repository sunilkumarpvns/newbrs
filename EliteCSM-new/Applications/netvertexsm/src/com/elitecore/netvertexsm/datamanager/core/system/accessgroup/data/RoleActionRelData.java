package com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data;

import java.io.Serializable;

import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IActionData;

public class RoleActionRelData implements IRoleActionRelData, Serializable {
	private String actionId;
	private long roleId;
	private IActionData actionData;
	public String getActionId() {
		return actionId;
	}
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	public IActionData getActionData() {
		return actionData;
	}
	public void setActionData(IActionData actionData) {
		this.actionData = actionData;
	}
}
