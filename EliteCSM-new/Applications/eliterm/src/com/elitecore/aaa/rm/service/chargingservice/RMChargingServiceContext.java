package com.elitecore.aaa.rm.service.chargingservice;

import com.elitecore.aaa.radius.service.RadServiceContext;
import com.elitecore.aaa.rm.conf.RMChargingServiceConfiguration;

public interface RMChargingServiceContext extends RadServiceContext<RMChargingRequest, RMChargingResponse> {
	public RMChargingServiceConfiguration getChargingConfiguration();
}
