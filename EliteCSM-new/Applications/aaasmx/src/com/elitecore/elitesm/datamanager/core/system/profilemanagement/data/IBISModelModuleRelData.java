package com.elitecore.elitesm.datamanager.core.system.profilemanagement.data;

public interface IBISModelModuleRelData {
	public String getBusinessModelId();
	public void setBusinessModelId(String businessModelId);
	public String getBusinessModuleId();
	public void setBusinessModuleId(String businessModuleId);
	public String getStatus();
	public void setStatus(String status);
	public IBISModuleData getBisModule();
	public void setBisModule(IBISModuleData bisModule);
}
