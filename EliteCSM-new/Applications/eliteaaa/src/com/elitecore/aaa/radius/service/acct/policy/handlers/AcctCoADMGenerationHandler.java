package com.elitecore.aaa.radius.service.acct.policy.handlers;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.AccountingChainHandler;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.FirstCoADMFilterSelectedStrategy;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CoADMGenerationHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CoADMHandlerEntryData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * The handler for the Accounting CoA/DM generation that creates the handler for all the entries configured in one 
 * group and also create the filter for the rule set provided.
 * 
 * @author kuldeep.panchal
 *
 */
public class AcctCoADMGenerationHandler extends AccountingChainHandler {
	
	private RadAcctServiceContext serviceContext;
	private CoADMGenerationHandlerData data;

	public AcctCoADMGenerationHandler(RadAcctServiceContext serviceContext,
			CoADMGenerationHandlerData coADMGenerationHandlerData) {
		super(new FirstCoADMFilterSelectedStrategy());
		this.serviceContext = serviceContext;
		this.data = coADMGenerationHandlerData;
	}

	@Override
	public void init() throws InitializationFailedException {
		for (CoADMHandlerEntryData entryData : data.getEntries()) {
			RadAcctServiceHandler handler = entryData.createHandler(serviceContext);
			AcctFilteredHandler filter = new AcctFilteredHandler(entryData.getRuleset(), handler);
			filter.init();
			addHandler(filter);
		}
	}

	@Override
	public boolean isEligible(RadAcctRequest request, RadAcctResponse response) {
		return true;
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		
	}

	@Override
	protected RadiusRequestExecutor<RadAcctRequest, RadAcctResponse> getExecutor(
			RadAcctRequest request) {
		return request.getExecutor();
	}
}
