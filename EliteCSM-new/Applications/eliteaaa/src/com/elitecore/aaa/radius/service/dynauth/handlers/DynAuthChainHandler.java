package com.elitecore.aaa.radius.service.dynauth.handlers;

import com.elitecore.aaa.radius.service.dynauth.RadDynAuthRequest;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthResponse;
import com.elitecore.aaa.radius.service.handlers.RadiusChainHandler;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.core.commons.InitializationFailedException;

public class DynAuthChainHandler extends RadiusChainHandler<RadDynAuthRequest, RadDynAuthResponse, RadDynAuthServiceHandler> implements RadDynAuthServiceHandler {

	public DynAuthChainHandler() {

	}
	
	public DynAuthChainHandler(ProcessingStrategy processingStrategy) {
		super(processingStrategy);
	}
	
	@Override
	public void init() throws InitializationFailedException {
		
	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}

	@Override
	protected RadiusRequestExecutor<RadDynAuthRequest, RadDynAuthResponse> getExecutor(RadDynAuthRequest request) {
		return request.getExecutor();
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}

}
