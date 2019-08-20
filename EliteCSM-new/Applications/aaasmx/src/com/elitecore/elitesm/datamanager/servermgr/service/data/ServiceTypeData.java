package com.elitecore.elitesm.datamanager.servermgr.service.data;

import java.util.Set;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class ServiceTypeData  extends BaseData implements IServiceTypeData{
	
	private long serviceTypeId;
	private String name;
	private String displayName;
	private String alias;
	private long serialNo;
	private String description;
	private String status;
	private Set driverTypeSet;
	
	
	public long getServiceTypeId() {
		return serviceTypeId;
	}
	public void setServiceTypeId(long serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public long getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(long serialNo) {
		this.serialNo = serialNo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Set getDriverTypeSet() {
		return driverTypeSet;
	}
	public void setDriverTypeSet(Set driverTypeList) {
		this.driverTypeSet = driverTypeList;
	}
	
}
