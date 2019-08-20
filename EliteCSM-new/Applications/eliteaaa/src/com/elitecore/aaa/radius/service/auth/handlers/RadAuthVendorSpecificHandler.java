package com.elitecore.aaa.radius.service.auth.handlers;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.handlers.RadVendorSpecificHandler;

public interface RadAuthVendorSpecificHandler extends RadVendorSpecificHandler {
	public boolean isEligible (RadAuthRequest request);
	public void handleRequest(RadAuthRequest request,RadAuthResponse response);

}
