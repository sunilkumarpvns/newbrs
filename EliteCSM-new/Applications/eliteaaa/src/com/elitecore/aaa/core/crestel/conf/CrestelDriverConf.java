package com.elitecore.aaa.core.crestel.conf;

import java.util.Hashtable;

import com.elitecore.aaa.core.drivers.DriverConfiguration;

public interface CrestelDriverConf extends DriverConfiguration{
	
	public Hashtable<String,String> getJndiPropertyMap();
	
	public String getTranslationMappingName();

}
