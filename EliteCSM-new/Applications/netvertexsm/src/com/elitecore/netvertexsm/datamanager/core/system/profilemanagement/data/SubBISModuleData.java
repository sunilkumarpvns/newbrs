package com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data;

import com.elitecore.netvertexsm.datamanager.core.base.data.BaseData;

public class SubBISModuleData extends BaseData implements ISubBISModuleData{
	private String subBusinessModuleId;
	private String alias;
	private String name;
	private String systemGenerated;
	private String status;
	private String subBisModuleTypeId;
	private String description;
	private String freezeProfile;
	private ISubBISModuleTypeData subBISModuleType;
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getSubBisModuleTypeId() {
		return subBisModuleTypeId;
	}
	public void setSubBisModuleTypeId(String subBisModuleTypeId) {
		this.subBisModuleTypeId = subBisModuleTypeId;
	}
	public String getSubBusinessModuleId() {
		return subBusinessModuleId;
	}
	public void setSubBusinessModuleId(String subBusinessModuleId) {
		this.subBusinessModuleId = subBusinessModuleId;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	public ISubBISModuleTypeData getSubBISModuleType() {
		return subBISModuleType;
	}
	public void setSubBISModuleType(ISubBISModuleTypeData subBISModuleType) {
		this.subBISModuleType = subBISModuleType;
	}
	public String getFreezeProfile() {
		return freezeProfile;
	}
	public void setFreezeProfile(String freezeProfile) {
		this.freezeProfile = freezeProfile;
	}
}
