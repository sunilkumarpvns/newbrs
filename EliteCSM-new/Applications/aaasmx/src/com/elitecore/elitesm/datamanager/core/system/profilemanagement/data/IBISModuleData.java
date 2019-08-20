package com.elitecore.elitesm.datamanager.core.system.profilemanagement.data;

import java.util.Set;

public interface IBISModuleData {
	public String getBusinessModuleId() ;
	public void setBusinessModuleId(String businessModuleId);
	public String getAlias();
	public void setAlias(String alias);
	public String getName();
	public void setName(String name);
	public String getSystemGenerated();
	public void setSystemGenerated(String systemGenerated);
	public String getStatus();
	public void setStatus(String status);
	public String getDescription();
	public void setDescription(String description);
	public String getFreezeProfile();
	public void setFreezeProfile(String freezeProfile);
	public IBISModuleTypeData getBisModuleType();
	public void setBisModuleType(IBISModuleTypeData bisModuleType);
	public Set getBisModuleSubBisModuleRel();
	public void setBisModuleSubBisModuleRel(Set bisModuleSubBisModuleRel);
}
