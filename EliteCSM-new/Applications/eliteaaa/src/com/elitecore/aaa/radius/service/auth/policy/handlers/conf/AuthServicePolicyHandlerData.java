package com.elitecore.aaa.radius.service.auth.policy.handlers.conf;

import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ServicePolicyHandlerData;

public interface AuthServicePolicyHandlerData extends ServicePolicyHandlerData {
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext);
}
