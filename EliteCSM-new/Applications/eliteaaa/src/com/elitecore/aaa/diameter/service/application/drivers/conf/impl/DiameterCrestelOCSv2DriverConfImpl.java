package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.crestel.conf.impl.CrestelDriverConfImpl;

public class DiameterCrestelOCSv2DriverConfImpl extends CrestelDriverConfImpl {

	@XmlTransient
	@Override
	public DriverTypes getDriverType() {
		return DriverTypes.DIAMETER_CRESTEL_OCSv2_DRIVER;
	}

}
