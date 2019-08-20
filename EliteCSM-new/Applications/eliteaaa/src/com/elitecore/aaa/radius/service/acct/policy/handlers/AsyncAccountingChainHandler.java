package com.elitecore.aaa.radius.service.acct.policy.handlers;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.AsyncRadServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.AsyncRadiusChainHandler;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.core.commons.InitializationFailedException;

public class AsyncAccountingChainHandler 
extends AsyncRadiusChainHandler<RadAcctRequest, RadAcctResponse, 
AsyncRadServiceHandler<RadAcctRequest, RadAcctResponse>> 
implements RadAcctServiceHandler {

	public AsyncAccountingChainHandler() {
		super();
	}
	
	public AsyncAccountingChainHandler(ProcessingStrategy processingStrategy) {
		super(processingStrategy);
	}
	
	@Override
	public void init() throws InitializationFailedException {
		
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		
	}

	@Override
	protected RadiusRequestExecutor<RadAcctRequest, RadAcctResponse> getExecutor(RadAcctRequest request) {
		return request.getExecutor();
	}
}

