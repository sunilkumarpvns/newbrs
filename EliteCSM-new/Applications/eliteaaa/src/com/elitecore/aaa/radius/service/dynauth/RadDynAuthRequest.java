package com.elitecore.aaa.radius.service.dynauth;

import com.elitecore.aaa.radius.policies.servicepolicy.DynAuthServicePolicy;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;

public interface RadDynAuthRequest extends RadServiceRequest {
	public DynAuthServicePolicy getServicePolicy();
	public RadiusRequestExecutor<RadDynAuthRequest, RadDynAuthResponse> getExecutor();
	public void setExecutor(RadiusRequestExecutor<RadDynAuthRequest, RadDynAuthResponse> executor);
}
