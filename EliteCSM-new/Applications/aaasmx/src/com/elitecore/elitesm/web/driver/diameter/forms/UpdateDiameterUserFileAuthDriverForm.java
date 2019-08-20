package com.elitecore.elitesm.web.driver.diameter.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateDiameterUserFileAuthDriverForm extends BaseWebForm{
	
	// driver instance related properties
	private String driverInstanceName;
	private String driverInstanceDesc;
	
	// user file driver details
	private String userFileDriverId;
	private String fileLocations;
	private String expiryDateFormat;
	private String driverInstanceId;
	private String action;
	private String auditUId;
	
	public String getUserFileDriverId() {
		return userFileDriverId;
	}
	public void setUserFileDriverId(String userFileDriverId) {
		this.userFileDriverId = userFileDriverId;
	}
	public String getFileLocations() {
		return fileLocations;
	}
	public void setFileLocations(String fileLocations) {
		this.fileLocations = fileLocations;
	}
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDriverInstanceName() {
		return driverInstanceName;
	}
	public void setDriverInstanceName(String driverInstanceName) {
		this.driverInstanceName = driverInstanceName;
	}
	public String getDriverInstanceDesc() {
		return driverInstanceDesc;
	}
	public void setDriverInstanceDesc(String driverInstanceDesc) {
		this.driverInstanceDesc = driverInstanceDesc;
	}
	public String getExpiryDateFormat() {
		return expiryDateFormat;
	}
	public void setExpiryDateFormat(String expiryDateFormat) {
		this.expiryDateFormat = expiryDateFormat;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
	

}
