package com.elitecore.aaa.radius.service.acct.policy.handlers;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.AccountingChainHandler;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.CdrGenerationHandlerData;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.CdrHandlerEntryData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AcctCdrGenerationHandler extends AccountingChainHandler {
	private static final String MODULE = "ACCT-CDR-GEN-HNDLR";
	private final RadAcctServiceContext serviceContext;
	private final CdrGenerationHandlerData data;

	public AcctCdrGenerationHandler(RadAcctServiceContext serviceContext,
			CdrGenerationHandlerData cdrGenerationHandlerData) {
		this.serviceContext = serviceContext;
		this.data = cdrGenerationHandlerData;
	}

	@Override
	public boolean isEligible(RadAcctRequest request, RadAcctResponse response) {
		return true;
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Initializing CDR Generation handler for policy: " + data.getPolicyName());
		}
		super.init();
		for (CdrHandlerEntryData entry : data.getCdrHandlers()) {
			RadAcctServiceHandler cdrHandler = entry.createHandler(serviceContext);
			AcctFilteredHandler filteredHandler = new AcctFilteredHandler(entry.getRuleset(), cdrHandler);
			filteredHandler.init();
			addHandler(filteredHandler);
		}
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Successfully initialized CDR Generation handler for policy: " + data.getPolicyName());
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {

	}

	@Override
	protected RadiusRequestExecutor<RadAcctRequest, RadAcctResponse> getExecutor(RadAcctRequest request) {
		return request.getExecutor();
	}
}
