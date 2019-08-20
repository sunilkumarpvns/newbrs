package com.elitecore.elitesm.datamanager.core.system.accessgroup.data;

import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IActionData;

public interface IGroupActionRelData {
	public String getActionId();
	public void setActionId(String actionId);
	public String getGroupId();
	public void setGroupId(String groupId);
	public IActionData getActionData();
	public void setActionData(IActionData actionData);
}
