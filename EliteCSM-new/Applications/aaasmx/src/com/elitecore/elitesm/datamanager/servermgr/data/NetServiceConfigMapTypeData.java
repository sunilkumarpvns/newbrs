package com.elitecore.elitesm.datamanager.servermgr.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class NetServiceConfigMapTypeData extends BaseData implements INetServiceConfigMapTypeData{
	private String netConfigMapTypeId;
	private String name;
	private String description;
	private String alias;
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
	public String getNetConfigMapTypeId() {
		return netConfigMapTypeId;
	}
	public void setNetConfigMapTypeId(String netConfigMapTypeId) {
		this.netConfigMapTypeId = netConfigMapTypeId;
	}
}
