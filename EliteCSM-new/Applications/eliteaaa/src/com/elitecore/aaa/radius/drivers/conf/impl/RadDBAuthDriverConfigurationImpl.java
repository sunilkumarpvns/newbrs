package com.elitecore.aaa.radius.drivers.conf.impl;



import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.impl.DBAuthDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

public class RadDBAuthDriverConfigurationImpl extends DBAuthDriverConfigurationImpl {

	@XmlTransient
	@Override
	public DriverTypes getDriverType() {		
		return DriverTypes.RAD_OPENDB_AUTH_DRIVER;
	}
}


