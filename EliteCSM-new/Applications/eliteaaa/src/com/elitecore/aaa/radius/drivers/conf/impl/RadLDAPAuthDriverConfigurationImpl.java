package com.elitecore.aaa.radius.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.impl.LDAPAuthDriverConfigurationImpl;
import com.elitecore.aaa.core.constant.DriverTypes;

@XmlType(propOrder = {})
public class RadLDAPAuthDriverConfigurationImpl extends LDAPAuthDriverConfigurationImpl{

	@XmlTransient
	@Override
	public DriverTypes getDriverType() {		
		return DriverTypes.RAD_LDAP_AUTH_DRIVER;
	}
}
