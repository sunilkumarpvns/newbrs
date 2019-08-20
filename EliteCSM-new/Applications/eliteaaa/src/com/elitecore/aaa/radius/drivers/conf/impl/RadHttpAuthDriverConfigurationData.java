package com.elitecore.aaa.radius.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.impl.HttpAuthDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

public class RadHttpAuthDriverConfigurationData  extends HttpAuthDriverConfigurationImpl{

	@XmlTransient
	@Override
	public DriverTypes getDriverType() {
		return DriverTypes.RAD_HTTP_AUTH_DRIVER;
	}
}
