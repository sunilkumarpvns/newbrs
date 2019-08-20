package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.impl.DetailLocalAcctDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

public class NasDetailLocalAcctConfigurationImpl extends DetailLocalAcctDriverConfigurationImpl{
	
	@XmlTransient
	@Override
	public DriverTypes getDriverType() {		
		return DriverTypes.NAS_DETAIL_LOCAL_ACCT_DRIVER;
	}
	
}
