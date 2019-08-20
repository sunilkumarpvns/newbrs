package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;

public interface AuthServicePolicyHandlerData extends ServicePolicyHandlerData {
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext);
}
