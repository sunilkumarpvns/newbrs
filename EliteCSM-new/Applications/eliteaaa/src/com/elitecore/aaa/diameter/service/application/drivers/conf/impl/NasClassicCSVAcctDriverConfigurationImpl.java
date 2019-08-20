package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.impl.ClassicCSVAcctDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

public class NasClassicCSVAcctDriverConfigurationImpl extends ClassicCSVAcctDriverConfigurationImpl{

	@XmlTransient
	@Override
	public DriverTypes getDriverType() {		
		return DriverTypes.NAS_CLASSIC_CSV_ACCT_DRIVER;
	}

}
