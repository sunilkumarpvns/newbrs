package com.elitecore.elitesm.web.driver.radius.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateRadiusUserFileAuthDriverForm extends BaseWebForm{
	
	private long userFileDriverId;
	private String fileLocations="data/userfiles";
	private String expiryDateFormat = "MM/dd/yyyy";
	private long driverInstanceId;
	private String action;
	
	///request Parameters
	
	private String driverInstanceName;
	private String driverDesp;
	private String driverRelatedId;
	
	
	
	public long getUserFileDriverId() {
		return userFileDriverId;
	}
	public void setUserFileDriverId(long userFileDriverId) {
		this.userFileDriverId = userFileDriverId;
	}
	public String getFileLocations() {
		return fileLocations;
	}
	public void setFileLocations(String fileLocations) {
		this.fileLocations = fileLocations;
	}
	public long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(long driverInstanceId) {
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
	public String getDriverDesp() {
		return driverDesp;
	}
	public void setDriverDesp(String driverDesp) {
		this.driverDesp = driverDesp;
	}
	public String getDriverRelatedId() {
		return driverRelatedId;
	}
	public void setDriverRelatedId(String driverRelatedId) {
		this.driverRelatedId = driverRelatedId;
	}
	public String getExpiryDateFormat() {
		return expiryDateFormat;
	}
	public void setExpiryDateFormat(String expiryDateFormat) {
		this.expiryDateFormat = expiryDateFormat;
	}
	
}
