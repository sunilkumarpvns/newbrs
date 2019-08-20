package com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data;

import java.io.Serializable;

import com.elitecore.netvertexsm.datamanager.core.base.data.BaseData;

public class BISModuleSubBISModuleRelData extends BaseData implements IBISModuleSubBISModuleRelData,Serializable{
	private String businessModuleId;
	private String subBusinessModuleId;
	private String status;
	private ISubBISModuleData subBisModule; 
	private IBISModuleData bisModule;
	public String getBusinessModuleId() {
		return businessModuleId;
	}
	public void setBusinessModuleId(String businessModuleId) {
		this.businessModuleId = businessModuleId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSubBusinessModuleId() {
		return subBusinessModuleId;
	}
	public void setSubBusinessModuleId(String subBusinessModuleId) {
		this.subBusinessModuleId = subBusinessModuleId;
	}
	public ISubBISModuleData getSubBisModule() {
		return subBisModule;
	}
	public void setSubBisModule(ISubBISModuleData subBisModule) {
		this.subBisModule = subBisModule;
	}
	public IBISModuleData getBisModule() {
		return bisModule;
	}
	public void setBisModule(IBISModuleData bisModule) {
		this.bisModule = bisModule;
	}
}
