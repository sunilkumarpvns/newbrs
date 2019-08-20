package com.elitecore.elitesm.datamanager.servermgr.alert.data;

import java.util.Set;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class AlertTypeData extends BaseData{
	private String alertTypeId;
	private String parentId;
	private String name;
	private String alias;
	private String enabled;
	private String type;
	private Set<AlertTypeData> nestedChildDetailList;
	
	
	public String getAlertTypeId() {
		return alertTypeId;
	}
	
	public void setAlertTypeId(String alertTypeId) {
		this.alertTypeId = alertTypeId;
	}
	
	public String getParentId() {
		return parentId;
	}
	
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getEnabled() {
		return enabled;
	}
	
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public Set<AlertTypeData> getNestedChildDetailList() {
		return nestedChildDetailList;
	}

	public void setNestedChildDetailList(Set<AlertTypeData> nestedChildDetailList) {
		this.nestedChildDetailList = nestedChildDetailList;
	}
	
	
}
