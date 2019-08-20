package com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data;

import java.util.Set;

public interface IBISModelData {
	public String getBusinessModelId();
	public void setBusinessModelId(String businessModelId);
	public String getAlias();
	public void setAlias(String alias);
	public String getName();
	public void setName(String name);
	public String getSystemGenerated();
	public void setSystemGenerated(String systemGenerated);
	public String getStatus();
	public void setStatus(String status);
	public String getDescription() ;
	public void setDescription(String description);
	public String getFreezeProfile();
	public void setFreezeProfile(String freezeProfile);
	public Set getBusinessModelModuleRel();
	public void setBusinessModelModuleRel(Set businessModelModuleRel);
}
