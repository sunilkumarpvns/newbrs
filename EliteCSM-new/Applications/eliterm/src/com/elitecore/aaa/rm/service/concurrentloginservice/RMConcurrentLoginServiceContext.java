package com.elitecore.aaa.rm.service.concurrentloginservice;

import com.elitecore.aaa.radius.service.RadServiceContext;
import com.elitecore.aaa.rm.conf.RMConcurrentLoginServiceConfiguration;

public interface RMConcurrentLoginServiceContext extends RadServiceContext<RadConcLoginRequest, RadConcLoginResponse> {

	public RMConcurrentLoginServiceConfiguration getRMConcurrentLoginServiceConfiguration();
}
