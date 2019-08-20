package com.elitecore.aaa.rm.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.radius.drivers.conf.impl.RadClassicCSVAcctConfigurationImpl;
import com.elitecore.aaa.rm.drivers.conf.RMRadClassicCSVAcctDriverConf;

public class RMRadClassicCSVAcctDriverConfImpl extends RadClassicCSVAcctConfigurationImpl 
implements RMRadClassicCSVAcctDriverConf {
	
	@XmlTransient
	@Override
	public DriverTypes getDriverType() {		
		return DriverTypes.RM_CLASSIC_CSV_ACCT_DRIVER;
	}
}
