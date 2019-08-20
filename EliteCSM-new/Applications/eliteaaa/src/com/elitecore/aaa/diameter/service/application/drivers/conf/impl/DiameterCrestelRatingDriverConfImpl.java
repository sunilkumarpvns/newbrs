package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.crestel.conf.impl.CrestelDriverConfImpl;

public class DiameterCrestelRatingDriverConfImpl extends CrestelDriverConfImpl{

	@XmlTransient
	@Override
	public DriverTypes getDriverType() {
		return DriverTypes.CRESTEL_RATING_DRIVER;
	}
}
