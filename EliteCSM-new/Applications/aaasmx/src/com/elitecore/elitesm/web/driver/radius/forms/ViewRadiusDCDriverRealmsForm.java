package com.elitecore.elitesm.web.driver.radius.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewRadiusDCDriverRealmsForm extends BaseWebForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String driverInstanceName;
	private String driverDesp;
	
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
}
