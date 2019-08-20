package com.elitecore.elitesm.datamanager.core.system.profilemanagement.data;

import java.sql.Timestamp;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class SubBISModuleTypeData extends BaseData implements ISubBISModuleTypeData {
	private String subBisModuleTypeId;
	private String name;
	private String alias;
	private String description;
	private String systemGenerated;
	private String bisModuleTypeId;
	private Timestamp createDate;
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getBisModuleTypeId() {
		return bisModuleTypeId;
	}
	public void setBisModuleTypeId(String bisModuleTypeId) {
		this.bisModuleTypeId = bisModuleTypeId;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
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
	public String getSubBisModuleTypeId() {
		return subBisModuleTypeId;
	}
	public void setSubBisModuleTypeId(String subBisModuleTypeId) {
		this.subBisModuleTypeId = subBisModuleTypeId;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
}
