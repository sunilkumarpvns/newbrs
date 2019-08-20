package com.elitecore.aaa.radius.service.acct.handlers;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.handlers.RadiusChainHandler;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AccountingChainHandler 
extends RadiusChainHandler<RadAcctRequest, RadAcctResponse, RadAcctServiceHandler>
implements RadAcctServiceHandler {

	public AccountingChainHandler() {}
	
	public AccountingChainHandler(ProcessingStrategy processingStrategy) {
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
