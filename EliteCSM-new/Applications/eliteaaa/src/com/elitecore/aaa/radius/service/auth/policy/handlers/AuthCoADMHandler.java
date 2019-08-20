package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.CoADMHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CoADMHandlerEntryData;

public class AuthCoADMHandler extends CoADMHandler<RadAuthRequest, RadAuthResponse> 
implements RadAuthServiceHandler {

	private static final String MODULE = "AUTH-COA-DM-HNDLR";
	
	public AuthCoADMHandler(RadAuthServiceContext serviceContext, 
			CoADMHandlerEntryData data) {
		super(serviceContext, data);
	}

	public String getModuleName() {
		return MODULE;
	}
}
