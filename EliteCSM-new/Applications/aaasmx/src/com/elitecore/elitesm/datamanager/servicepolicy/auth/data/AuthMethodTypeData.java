package com.elitecore.elitesm.datamanager.servicepolicy.auth.data;

import java.io.Serializable;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class AuthMethodTypeData extends BaseData implements  Serializable{

	private static final long serialVersionUID = 1L;
	private Long authMethodTypeId;
	private String name;
	private String description;
	private String status;
	private Integer serialNumber;
	private String alias;
	
	public long getAuthMethodTypeId() {
		return authMethodTypeId;
	}
	public void setAuthMethodTypeId(long authMethodTypeId) {
		this.authMethodTypeId = authMethodTypeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public Integer getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(Integer serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	

}
