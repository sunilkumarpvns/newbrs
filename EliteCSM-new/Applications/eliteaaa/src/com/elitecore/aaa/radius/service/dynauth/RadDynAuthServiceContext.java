package com.elitecore.aaa.radius.service.dynauth;

import com.elitecore.aaa.radius.conf.RadDynAuthConfiguration;
import com.elitecore.aaa.radius.service.RadServiceContext;

public interface RadDynAuthServiceContext extends RadServiceContext<RadDynAuthRequest, RadDynAuthResponse> {
	public RadDynAuthConfiguration getDynAuthConfiguration();
}
