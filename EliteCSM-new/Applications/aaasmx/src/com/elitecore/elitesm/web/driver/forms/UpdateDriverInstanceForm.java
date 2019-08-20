package com.elitecore.elitesm.web.driver.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateDriverInstanceForm extends BaseWebForm{
	
	private String driverInstanceId;
	private String name;
	private String description;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
