package com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data;

public interface IActionData {
	public String getActionId();
	public void setActionId(String actionId);
	public String getName();
	public void setName(String name);
	public String getAlias();
	public void setAlias(String alias) ;
	public String getDescription();
	public void setDescription(String description);
	public String getActionTypeId();
	public void setActionTypeId(String actionTypeId);
	public String getParentActionId();
	public void setParentActionId(String parentActionId);
	public String getActionLevel();
	public void setActionLevel(String actionLevel);
	public String getSystemGenerated();
	public void setSystemGenerated(String systemGenerated);
	public String getScreenId();
	public void setScreenId(String screenId);
	public String getStatus();
	public void setStatus(String status);
	public String getFreezeProfile() ;
	public void setFreezeProfile(String freezeProfile);
}
