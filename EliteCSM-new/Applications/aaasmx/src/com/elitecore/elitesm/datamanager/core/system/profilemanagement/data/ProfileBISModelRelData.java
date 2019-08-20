package com.elitecore.elitesm.datamanager.core.system.profilemanagement.data;

import java.io.Serializable;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class ProfileBISModelRelData extends BaseData implements IProfileBISModelRelData,Serializable{
	private String configurationProfileId;
	private String businessModelId;
	private String status;
	public String getBusinessModelId() {
		return businessModelId;
	}
	public void setBusinessModelId(String businessModelId) {
		this.businessModelId = businessModelId;
	}
	public String getConfigurationProfileId() {
		return configurationProfileId;
	}
	public void setConfigurationProfileId(String configurationProfileId) {
		this.configurationProfileId = configurationProfileId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
