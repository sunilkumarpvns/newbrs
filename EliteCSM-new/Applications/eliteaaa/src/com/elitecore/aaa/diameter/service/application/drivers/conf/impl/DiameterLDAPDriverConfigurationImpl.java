package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.impl.LDAPAuthDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

public class DiameterLDAPDriverConfigurationImpl extends LDAPAuthDriverConfigurationImpl {

	@XmlTransient
	@Override
	public DriverTypes getDriverType() {		
		return DriverTypes.DIAMETER_LDAP_DRIVER;
	}

}
