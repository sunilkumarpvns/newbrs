package com.elitecore.aaa.radius.drivers.conf.impl;


import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.impl.DbAcctDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;


@XmlType(propOrder={})
public class RadDBAcctDriverConfigurationImpl extends DbAcctDriverConfigurationImpl {
	
	@XmlTransient
	@Override
	public DriverTypes getDriverType() {		
		return DriverTypes.RAD_OPENDB_ACCT_DRIVER;
	}
	
}


