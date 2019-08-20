package com.elitecore.aaa.core.drivers;

import com.elitecore.aaa.core.constant.DriverTypes;


public interface DriverConfiguration {
	
	public String getDriverInstanceId();
	
	public DriverTypes getDriverType();
	
	public String getDriverName();
	
}
