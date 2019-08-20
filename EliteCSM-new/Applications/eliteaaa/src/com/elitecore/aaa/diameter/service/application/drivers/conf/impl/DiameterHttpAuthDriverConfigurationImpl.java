package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.impl.HttpAuthDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

public class DiameterHttpAuthDriverConfigurationImpl extends HttpAuthDriverConfigurationImpl {
	
	@XmlTransient
	@Override
	public DriverTypes getDriverType() {
		return DriverTypes.DIAMETER_HTTP_AUTH_DRIVER;
	}
	
}
