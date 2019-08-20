package com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data;

import java.io.Serializable;

import com.elitecore.netvertexsm.datamanager.core.base.data.BaseData;

public class BISModelModuleRelData extends BaseData implements IBISModelModuleRelData,Serializable{
	private String businessModelId;
	private String businessModuleId;
	private String status;
	private IBISModuleData bisModule;
	public String getBusinessModelId() {
		return businessModelId;
	}
	public void setBusinessModelId(String businessModelId) {
		this.businessModelId = businessModelId;
	}
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
	public IBISModuleData getBisModule() {
		return bisModule;
	}
	public void setBisModule(IBISModuleData bisModule) {
		this.bisModule = bisModule;
	}
}
