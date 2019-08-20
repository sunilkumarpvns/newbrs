package com.elitecore.elitesm.datamanager.core.system.profilemanagement.data;

import java.util.Set;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class BISModuleData extends BaseData implements IBISModuleData{
	private String businessModuleId;
	private String alias;
	private String name;
	private String systemGenerated;
	private String status;
	private String description;
	private String freezeProfile;
	private IBISModuleTypeData bisModuleType;
	private Set bisModuleSubBisModuleRel;
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getBusinessModuleId() {
		return businessModuleId;
	}
	public void setBusinessModuleId(String businessModuleId) {
		this.businessModuleId = businessModuleId;
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
	public IBISModuleTypeData getBisModuleType() {
		return bisModuleType;
	}
	public void setBisModuleType(IBISModuleTypeData bisModuleType) {
		this.bisModuleType = bisModuleType;
	}
	public Set getBisModuleSubBisModuleRel() {
		return bisModuleSubBisModuleRel;
	}
	public void setBisModuleSubBisModuleRel(Set bisModuleSubBisModuleRel) {
		this.bisModuleSubBisModuleRel = bisModuleSubBisModuleRel;
	}
}
