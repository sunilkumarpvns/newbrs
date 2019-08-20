package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.impl.HSSAuthDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

public class DiameterHSSAuthDriverConfImpl extends HSSAuthDriverConfigurationImpl {

	@Override
	@XmlTransient
	public DriverTypes getDriverType() {
		return DriverTypes.DIA_HSS_AUTH_DRIVER;
	}

}
