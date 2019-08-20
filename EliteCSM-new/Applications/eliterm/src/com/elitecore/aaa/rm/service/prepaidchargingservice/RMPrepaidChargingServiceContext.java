package com.elitecore.aaa.rm.service.prepaidchargingservice;

import com.elitecore.aaa.radius.service.RadServiceContext;
import com.elitecore.aaa.rm.conf.RMPrepaidChargingServiceConfiguration;

public interface RMPrepaidChargingServiceContext extends RadServiceContext<RMPrepaidChargingServiceRequest, RMPrepaidChargingServiceResponse> {
	
	public RMPrepaidChargingServiceConfiguration getRMPrepaidChargingServiceConfiguration();
}
