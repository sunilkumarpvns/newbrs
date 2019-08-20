package com.elitecore.elitesm.datamanager.core.system.profilemanagement.data;

import java.util.Set;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class BISModelData extends BaseData implements IBISModelData {
	private String businessModelId;
	private String alias;
	private String name;
	private String systemGenerated;
	private String status;
	private String description;
	private String freezeProfile;
	private Set businessModelModuleRel; 
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getBusinessModelId() {
		return businessModelId;
	}
	public void setBusinessModelId(String businessModelId) {
		this.businessModelId = businessModelId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFreezeProfile() {
		return freezeProfile;
	}
	public void setFreezeProfile(String freezeProfile) {
		this.freezeProfile = freezeProfile;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	public Set getBusinessModelModuleRel() {
		return businessModelModuleRel;
	}
	public void setBusinessModelModuleRel(Set businessModelModuleRel) {
		this.businessModelModuleRel = businessModelModuleRel;
	}
}
