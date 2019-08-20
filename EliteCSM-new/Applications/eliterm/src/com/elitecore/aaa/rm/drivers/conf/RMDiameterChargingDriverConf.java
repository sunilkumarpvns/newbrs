package com.elitecore.aaa.rm.drivers.conf;

import com.elitecore.aaa.core.drivers.DriverConfiguration;

public interface RMDiameterChargingDriverConf extends DriverConfiguration {
	
	public String getTranslationMappingName();
	
	public String getDisconnectUrl();

}
