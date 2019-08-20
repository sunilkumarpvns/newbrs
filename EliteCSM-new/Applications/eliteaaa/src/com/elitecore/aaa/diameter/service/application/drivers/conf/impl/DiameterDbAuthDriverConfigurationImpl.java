package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.impl.DBAuthDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

public class DiameterDbAuthDriverConfigurationImpl extends DBAuthDriverConfigurationImpl  {

	@XmlTransient
	@Override
	public DriverTypes getDriverType() {		
		return DriverTypes.DIAMETER_DB_DRIVER;
	}
}