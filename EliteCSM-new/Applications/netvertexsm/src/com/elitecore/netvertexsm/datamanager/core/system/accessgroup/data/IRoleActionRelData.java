package com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data;

import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IActionData;

public interface IRoleActionRelData {
	public String getActionId();
	public void setActionId(String actionId);
	public long getRoleId();
	public void setRoleId(long roleId);
	public IActionData getActionData();
	public void setActionData(IActionData actionData);
}
