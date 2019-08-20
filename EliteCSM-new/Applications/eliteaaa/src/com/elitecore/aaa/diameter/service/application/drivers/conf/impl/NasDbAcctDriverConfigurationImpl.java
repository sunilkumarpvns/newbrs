package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.impl.DbAcctDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

public class NasDbAcctDriverConfigurationImpl extends DbAcctDriverConfigurationImpl{

	@XmlTransient
	@Override
	public DriverTypes getDriverType() {		
		return DriverTypes.NAS_OPENDB_ACCT_DRIVER;
	}

}