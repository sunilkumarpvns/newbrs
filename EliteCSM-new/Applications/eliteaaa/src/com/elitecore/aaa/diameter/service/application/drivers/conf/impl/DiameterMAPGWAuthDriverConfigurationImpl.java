package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.impl.MAPGWAuthDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

public class DiameterMAPGWAuthDriverConfigurationImpl extends MAPGWAuthDriverConfigurationImpl {

	@XmlTransient
	@Override
	public DriverTypes getDriverType() {
		return DriverTypes.DIAMETER_MAPGW_AUTH_DRIVER;
	}

	
}
