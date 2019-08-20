package com.elitecore.aaa.radius.service.auth.handlers;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.handlers.RadiusChainHandler;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AuthenticationChainHandler 
extends RadiusChainHandler<RadAuthRequest, RadAuthResponse, RadAuthServiceHandler>
implements RadAuthServiceHandler {

	public AuthenticationChainHandler() {}
	
	public AuthenticationChainHandler(ProcessingStrategy processingStrategy) {
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
