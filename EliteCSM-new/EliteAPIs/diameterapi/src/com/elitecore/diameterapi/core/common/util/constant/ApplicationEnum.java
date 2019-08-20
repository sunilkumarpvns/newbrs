package com.elitecore.diameterapi.core.common.util.constant;

import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.mibs.constants.ServiceTypes;

public interface ApplicationEnum {

	public long getApplicationId();
	
	public long getVendorId();
	
	public Application getApplication();
	
	public ServiceTypes getApplicationType();

}
