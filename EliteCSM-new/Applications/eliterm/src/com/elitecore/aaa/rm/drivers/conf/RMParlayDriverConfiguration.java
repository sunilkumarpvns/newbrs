package com.elitecore.aaa.rm.drivers.conf;

import com.elitecore.aaa.core.drivers.DriverConfiguration;

public interface RMParlayDriverConfiguration extends DriverConfiguration {
	
	public String getWebServiceAddress();
	
	public String getSessionManagerServiceName();
	
	public String getParlayServiceName();
	
	public String getUserName();
	
	public String getPassword();
	
	public String getTranslationMappingName();

}
