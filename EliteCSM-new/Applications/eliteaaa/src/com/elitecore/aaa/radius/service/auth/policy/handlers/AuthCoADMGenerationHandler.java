package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.AuthenticationChainHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.FirstCoADMFilterSelectedStrategy;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CoADMGenerationHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CoADMHandlerEntryData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.core.commons.InitializationFailedException;

public class AuthCoADMGenerationHandler extends AuthenticationChainHandler {

	private RadAuthServiceContext serviceContext;
	private CoADMGenerationHandlerData data;
	
	public AuthCoADMGenerationHandler(RadAuthServiceContext serviceContext,
			CoADMGenerationHandlerData coADMGenerationHandlerData) {
		super(new FirstCoADMFilterSelectedStrategy());
		this.serviceContext = serviceContext;
		this.data = coADMGenerationHandlerData;
	}

	@Override
	public void init() throws InitializationFailedException {
		for (CoADMHandlerEntryData entryData : data.getEntries()) {
			RadAuthServiceHandler handler = entryData.createHandler(serviceContext);
			AuthFilteredHandler filter = new AuthFilteredHandler(entryData.getRuleset(), handler);
			filter.init();
			addHandler(filter);
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}

	@Override
	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		return true;
	}

	@Override
	protected RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> getExecutor(RadAuthRequest request) {
		return request.getExecutor();
	}
}
