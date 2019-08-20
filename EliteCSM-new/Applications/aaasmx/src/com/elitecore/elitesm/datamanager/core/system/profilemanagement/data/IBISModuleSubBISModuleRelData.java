package com.elitecore.elitesm.datamanager.core.system.profilemanagement.data;

public interface IBISModuleSubBISModuleRelData {
	public String getBusinessModuleId() ;
	public void setBusinessModuleId(String businessModuleId);
	public String getSubBusinessModuleId();
	public void setSubBusinessModuleId(String subBusinessModuleId);
	public String getStatus();
	public void setStatus(String status);
	public ISubBISModuleData getSubBisModule();
	public void setSubBisModule(ISubBISModuleData subBisModule);
	public IBISModuleData getBisModule();
	public void setBisModule(IBISModuleData bisModule);
}
