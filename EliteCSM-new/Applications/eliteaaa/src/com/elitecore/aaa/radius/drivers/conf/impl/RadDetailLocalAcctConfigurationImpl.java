package com.elitecore.aaa.radius.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.impl.DetailLocalAcctDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

@XmlType(propOrder={})
public class RadDetailLocalAcctConfigurationImpl extends DetailLocalAcctDriverConfigurationImpl{

	@XmlTransient
	@Override
	public DriverTypes getDriverType() {		
		return DriverTypes.RAD_DETAIL_LOCAL_ACCT_DRIVER;
	}
}
