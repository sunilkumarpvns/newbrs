package com.elitecore.aaa.radius.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.impl.MAPGWAuthDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

public class RadMAPGWAuthDriverConfigurationImpl extends MAPGWAuthDriverConfigurationImpl{
	
	@XmlTransient
	@Override
	public DriverTypes getDriverType() {
		return DriverTypes.RAD_MAPGW_AUTH_DRIVER;
	}
}
