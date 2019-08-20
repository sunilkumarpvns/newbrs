package com.elitecore.elitesm.datamanager.servermgr.drivers.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
@XmlRootElement(name = "driver-type-data")
public class DriverTypeData extends BaseData implements IDriverTypeData,Serializable{
	private long driverTypeId;
	private long serviceTypeId;
	private String name;
	private long serialNo;
	private String displayName;
	private String alias;
	private String description;
	private String status;

	@XmlElement(name = "driver-type-id")
	public long getDriverTypeId() {
		return driverTypeId;
	}
	public void setDriverTypeId(long driverTypeId) {
		this.driverTypeId = driverTypeId;
	}
	@XmlElement(name = "service-type-id")
	public long getServiceTypeId() {
		return serviceTypeId;
	}
	public void setServiceTypeId(long serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlElement(name = "serial-no")
	public long getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(long serialNo) {
		this.serialNo = serialNo;
	}
	@XmlElement(name = "display-name")
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	@XmlTransient
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	@XmlElement(name = "discription")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@XmlElement(name = "status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}


