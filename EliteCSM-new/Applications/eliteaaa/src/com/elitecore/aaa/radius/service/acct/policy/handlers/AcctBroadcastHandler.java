package com.elitecore.aaa.radius.service.acct.policy.handlers;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.ExternalCommunicationHandler.CommunicatorExceptionPolicy;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.AsyncCommunicationEntryData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.BroadcastCommunicationHandlerData;
import com.elitecore.aaa.radius.service.handlers.RadiusChainHandler;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AcctBroadcastHandler implements RadAcctServiceHandler {
	private static final String MODULE = "ACCT-BROADCAST-HNDLR";

	private final RadAcctServiceContext serviceContext;
	private final BroadcastCommunicationHandlerData data;
	private AsyncAccountingChainHandler waitForResponseChain;
	private AsyncAccountingChainHandler noWaitForResponseChain;

	public AcctBroadcastHandler(RadAcctServiceContext serviceContext, BroadcastCommunicationHandlerData data) {
		this.serviceContext = serviceContext;
		this.data = data;
		waitForResponseChain = new AsyncAccountingChainHandler(new RadiusChainHandler.ContinueProcessingStrategy());
		noWaitForResponseChain = new AsyncAccountingChainHandler(new RadiusChainHandler.ContinueProcessingStrategy());
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing Broadcast handler for policy: " + data.getPolicyName());
		}
		
		AcctExternalCommunicationHandlerFactory factory = new AcctExternalCommunicationHandlerFactory(serviceContext);
		for (AsyncCommunicationEntryData commEntry : data.getProxyCommunicationEntries()) {
			try {
				RadAcctServiceHandler externalHandler = factory.createHandler(commEntry, CommunicatorExceptionPolicy.CONTINUE);
				if (commEntry.isWait()) {
					AcctWaitBroadcastFilteredHandler filterHandler = new AcctWaitBroadcastFilteredHandler(commEntry.getRuleset(), externalHandler);
					filterHandler.init();
					waitForResponseChain.addHandler(filterHandler);
				} else {
					AcctFilteredHandler filterHandler = new AcctFilteredHandler(commEntry.getRuleset(), externalHandler);
					filterHandler.init();
					noWaitForResponseChain.addHandler(filterHandler);
				}
			} catch(InitializationFailedException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Error in adding Broadcast communicator for policy: " + data.getPolicyName()
							+ "Reason: " + e.getMessage() + ", skipping that communicator");
				}
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Broadcast handler for policy: " + data.getPolicyName());
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {

	}

	@Override
	public boolean isEligible(RadAcctRequest request, RadAcctResponse response) {
		return true;
	}

	@Override
	public void handleRequest(RadAcctRequest request, RadAcctResponse response, ISession session) {
		noWaitForResponseChain.handleAsyncRequest(request, response, session, new AcctNoWaitForResponseBroadcastResponseListener());

		waitForResponseChain.handleAsyncRequest(request, response, session, new AcctWaitForResponseBroadcastResponseListener(serviceContext, request, response));
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return waitForResponseChain.isResponseBehaviorApplicable();
	}
	
}
