package com.elitecore.aaa.radius.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.impl.ClassicCSVAcctDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

@XmlType(propOrder={})
public class RadClassicCSVAcctConfigurationImpl extends ClassicCSVAcctDriverConfigurationImpl{
	
	@XmlTransient
	@Override
	public DriverTypes getDriverType() {		
		return DriverTypes.RAD_CLASSIC_CSV_ACCT_DRIVER;
	}
}
