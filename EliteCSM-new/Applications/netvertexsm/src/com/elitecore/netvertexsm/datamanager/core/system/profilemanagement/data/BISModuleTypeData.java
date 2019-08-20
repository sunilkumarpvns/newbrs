
package com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data;

import java.sql.Timestamp;

import com.elitecore.netvertexsm.datamanager.core.base.data.BaseData;

public class BISModuleTypeData extends BaseData implements IBISModuleTypeData{
	private String bisModuleTypeId;
	private String name;
	private String alias;
	private String description;
	private String systemGenerated;
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
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
}
