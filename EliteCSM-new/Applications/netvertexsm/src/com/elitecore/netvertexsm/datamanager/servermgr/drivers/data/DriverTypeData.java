package com.elitecore.netvertexsm.datamanager.servermgr.drivers.data;

import java.io.Serializable;

public class DriverTypeData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private long driverTypeId;
	private Long serviceTypeId;
	private String name;
	private Long serialNo;
	private String displayName;
	private String alias;
	private String description;
	private String status;
	
	public long getDriverTypeId() {
		return driverTypeId;
	}
	public void setDriverTypeId(long driverTypeId) {
		this.driverTypeId = driverTypeId;
	}
	public Long getServiceTypeId() {
		return serviceTypeId;
	}
	public void setServiceTypeId(Long serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(Long serialNo) {
		this.serialNo = serialNo;
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
	
}
