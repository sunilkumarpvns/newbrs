package com.elitecore.aaa.radius.service.acct.policy.handlers;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.AccountingChainHandler;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.acct.policy.handlers.conf.AcctPostResponseHandlerData;
import com.elitecore.aaa.radius.service.acct.policy.handlers.conf.AcctServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AcctPostResponseHandler extends AccountingChainHandler {
	private static final String MODULE = "ACCT-POST-RES-HNDLR";

	private final AcctPostResponseHandlerData data;
	private final RadAcctServiceContext serviceContext;

	public AcctPostResponseHandler(RadAcctServiceContext serviceContext, AcctPostResponseHandlerData authPostResponseHandlerData) {
		this.serviceContext = serviceContext;
		this.data = authPostResponseHandlerData;
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing Accounting Post Response handler for policy: " + data.getPolicyName());
		}
		for (AcctServicePolicyHandlerData handlerData : data.getHandlersData()) {
			try {
				RadAcctServiceHandler handler = handlerData.createHandler(serviceContext);
				handler.init();
				addHandler(handler);
			} catch(Exception e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "Problem in initializing post response handler, Reason: " + e.getMessage());
				}
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Accounting Post Response handler for policy: " + data.getPolicyName());
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}

	@Override
	protected RadiusRequestExecutor<RadAcctRequest, RadAcctResponse> getExecutor(RadAcctRequest request) {
		return request.getExecutor();
	}

}

