package com.elitecore.aaa.rm.service.chargingservice;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.rm.policies.RMChargingPolicy;

public interface RMChargingRequest extends RadServiceRequest{
	public RMChargingPolicy getServicePolicy();
}
