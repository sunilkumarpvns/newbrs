package com.elitecore.aaa.rm.service.ippool.handlers;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.rm.service.handlers.RMServiceHandler;

public interface RMIPPoolServiceHandler extends RMServiceHandler {

	public boolean isEligible (RadAuthRequest request, RadAuthResponse response);
	public void handleIPAllocateRequest(RadServiceRequest request, RadServiceResponse response);
	public void handleIPUpdateOrReleaseRequest(RadServiceRequest request,RadServiceResponse response,String iRequestType);
	
}
