package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.impl.UserFileAuthDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

public class DiameterUserFileDriverConfigurationImpl extends UserFileAuthDriverConfigurationImpl {
	
	@XmlTransient
	@Override
	public DriverTypes getDriverType() {		
		return DriverTypes.DIAMETER_USERFILE_DRIVER;
	}

}
