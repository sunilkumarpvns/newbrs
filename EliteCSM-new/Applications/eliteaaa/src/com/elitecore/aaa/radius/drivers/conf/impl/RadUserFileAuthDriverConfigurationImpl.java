package com.elitecore.aaa.radius.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.impl.UserFileAuthDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

@XmlType(propOrder = {})
public class RadUserFileAuthDriverConfigurationImpl extends UserFileAuthDriverConfigurationImpl  {

	@XmlTransient
	@Override
	public DriverTypes getDriverType() {		
		return DriverTypes.RAD_USERFILE_AUTH_DRIVER;
	}
	
}
