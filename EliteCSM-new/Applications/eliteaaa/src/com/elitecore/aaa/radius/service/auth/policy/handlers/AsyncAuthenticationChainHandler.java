package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.AsyncRadServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.AsyncRadiusChainHandler;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AsyncAuthenticationChainHandler 
extends AsyncRadiusChainHandler<RadAuthRequest, RadAuthResponse,
AsyncRadServiceHandler<RadAuthRequest, RadAuthResponse>> 
implements RadAuthServiceHandler {

	public AsyncAuthenticationChainHandler() {
		super();
	}
	
	public AsyncAuthenticationChainHandler(ProcessingStrategy processingStrategy) {
		super(processingStrategy);
	}
	
	@Override
	public void init() throws InitializationFailedException {
		
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		
	}

	@Override
	protected RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> getExecutor(RadAuthRequest request) {
		return request.getExecutor();
	}
}
