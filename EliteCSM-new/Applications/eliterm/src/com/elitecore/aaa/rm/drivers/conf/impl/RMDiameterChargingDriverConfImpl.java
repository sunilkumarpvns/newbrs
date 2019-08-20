package com.elitecore.aaa.rm.drivers.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.rm.drivers.conf.RMDiameterChargingDriverConf;

@XmlType(propOrder = {})
public class RMDiameterChargingDriverConfImpl implements RMDiameterChargingDriverConf {
	
	private String driverInstanceId;
	private String driverName;
	private String translatorMappingName = "";
	private String disconnectUrl = "";

	@Override
	@XmlElement(name ="translation-mapping-name",type = String.class)
	public String getTranslationMappingName() {
		return this.translatorMappingName;
	}

	@Override
	@XmlElement(name="id",type = String.class)
	public String getDriverInstanceId() {
		return this.driverInstanceId;
	}

	@Override
	@XmlElement(name ="driver-name",type = String.class)
	public String getDriverName() {
		return this.driverName;
	}

	@Override
	@XmlTransient
	public DriverTypes getDriverType() {
		return DriverTypes.RM_DIAMETER_DRIVER;
	}
	
	@Override
	@XmlElement(name ="disconnect-url",type = String.class)
	public String getDisconnectUrl() {		
		return this.disconnectUrl;
	}


	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public void setTranslationMappingName(String translatorMappingName) {
		this.translatorMappingName = translatorMappingName;
	}

	public void setDisconnectUrl(String disconnectUrl) {
		this.disconnectUrl = disconnectUrl;
	}

}
