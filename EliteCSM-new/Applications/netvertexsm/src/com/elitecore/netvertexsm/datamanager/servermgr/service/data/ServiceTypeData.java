package com.elitecore.netvertexsm.datamanager.servermgr.service.data;

import java.util.Set;

public class ServiceTypeData  implements IServiceTypeData{
	
	private long serviceTypeId;
	private String name;
	private String displayName;
	private String alias;
	private long serialNo;
	private String description;
	private char status;
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
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public Set getDriverTypeSet() {
		return driverTypeSet;
	}
	public void setDriverTypeSet(Set driverTypeList) {
		this.driverTypeSet = driverTypeList;
	}
	
}
