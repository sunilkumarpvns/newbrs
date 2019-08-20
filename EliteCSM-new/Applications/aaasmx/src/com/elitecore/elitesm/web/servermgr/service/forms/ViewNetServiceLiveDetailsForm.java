package com.elitecore.elitesm.web.servermgr.service.forms;

import java.util.List;

import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class ViewNetServiceLiveDetailsForm extends BaseDictionaryForm{
	private String name;
	private String netServiceId;
	private String netServiceType;
	private String description;
	private String version;
	private String action;
//	private List driverDetails;
	private String errorCode;
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
/*	public List getDriverDetails() {
		return driverDetails;
	}
	public void setDriverDetails(List driverDetails) {
		this.driverDetails = driverDetails;
	}*/
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNetServiceId() {
		return netServiceId;
	}
	public void setNetServiceId(String netServiceId) {
		this.netServiceId = netServiceId;
	}
	public String getNetServiceType() {
		return netServiceType;
	}
	public void setNetServiceType(String netServiceType) {
		this.netServiceType = netServiceType;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
