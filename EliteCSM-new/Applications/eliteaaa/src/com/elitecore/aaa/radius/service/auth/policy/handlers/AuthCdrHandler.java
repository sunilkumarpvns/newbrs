package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.CdrHandlerEntryData;
import com.elitecore.aaa.radius.service.base.policy.handler.CdrHandler;

/**
 * 
 * @author kuldeep.panchal
 * @author narendra.pathai
 *
 */
public class AuthCdrHandler extends CdrHandler<RadAuthRequest, RadAuthResponse>
implements RadAuthServiceHandler {
	private static final String MODULE = "AUTH-CDR-HANDLER";
	
	public AuthCdrHandler(RadAuthServiceContext serviceContext, CdrHandlerEntryData cdrHandlerEntryData) {
		super(serviceContext, cdrHandlerEntryData);
	}

	@Override
	public String getModuleName() {
		return MODULE;
	}
}
