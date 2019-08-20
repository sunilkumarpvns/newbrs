package com.elitecore.elitesm.web.driver.radius.forms;

import com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateRadiusDCDriverRealmsForm extends BaseWebForm{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String action;
	private DiameterChargingDriverData diameterChargingDriverData;
	private String driverInstanceName;
	private String driverDesp;
	private String driverInstanceId;
	
	public String getDriverInstanceId() {
		return driverInstanceId;
	}

	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
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

	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public DiameterChargingDriverData getDiameterChargingDriverData() {
		return diameterChargingDriverData;
	}
	
	public void setDiameterChargingDriverData(
			DiameterChargingDriverData diameterChargingDriverData) {
		this.diameterChargingDriverData = diameterChargingDriverData;
	}
}
