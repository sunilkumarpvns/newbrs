package com.elitecore.aaa.rm.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.crestel.conf.impl.CrestelDriverConfImpl;

public class RMCrestelChargingDriverConfImpl extends CrestelDriverConfImpl{

	@XmlTransient
	@Override
	public DriverTypes getDriverType() {
		return DriverTypes.RM_CRESTEL_CHARGING_DRIVER;
	}

}
