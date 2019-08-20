package com.elitecore.elitesm.datamanager.core.system.profilemanagement.data;

public interface ISubBISModuleData {
	public String getSubBusinessModuleId();
	public void setSubBusinessModuleId(String subBusinessModuleId);
	public String getAlias();
	public void setAlias(String alias);
	public String getName();
	public void setName(String name);
	public String getSystemGenerated();
	public void setSystemGenerated(String systemGenerated);
	public String getStatus();
	public void setStatus(String status);
	public String getSubBisModuleTypeId();
	public void setSubBisModuleTypeId(String subBisModuleTypeId);
	public String getDescription();
	public void setDescription(String description);
	public ISubBISModuleTypeData getSubBISModuleType();
	public void setSubBISModuleType(ISubBISModuleTypeData subBISModuleType);
	public String getFreezeProfile();
	public void setFreezeProfile(String freezeProfile);
}
