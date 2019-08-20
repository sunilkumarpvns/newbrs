package com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class WebMethodKeyData extends BaseData{

	private String id;
	private String name;
	private String value;
	private Integer serialNumber;
	private String status;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Integer getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(Integer serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	

}
