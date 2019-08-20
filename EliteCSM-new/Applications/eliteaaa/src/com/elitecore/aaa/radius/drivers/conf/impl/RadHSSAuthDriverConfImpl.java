package com.elitecore.aaa.radius.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.impl.HSSAuthDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

public class RadHSSAuthDriverConfImpl extends HSSAuthDriverConfigurationImpl {

	@Override
	@XmlTransient
	public DriverTypes getDriverType() {
		return DriverTypes.RAD_HSS_AUTH_DRIVER;
	}

}
