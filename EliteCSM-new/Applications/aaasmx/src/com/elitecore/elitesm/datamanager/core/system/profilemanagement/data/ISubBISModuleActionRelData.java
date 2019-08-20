package com.elitecore.elitesm.datamanager.core.system.profilemanagement.data;

public interface ISubBISModuleActionRelData {
	public String getActionId();
	public void setActionId(String actionId);
	public String getSubBusinessModuleId();
	public void setSubBusinessModuleId(String subBusinessModuleId);
	public String getStatus();
	public void setStatus(String status);
	public IActionData getActionData();
	public void setActionData(IActionData actionData);
	public ISubBISModuleData getSubBISModuleData();
	public void setSubBISModuleData(ISubBISModuleData subBISModuleData);
}
