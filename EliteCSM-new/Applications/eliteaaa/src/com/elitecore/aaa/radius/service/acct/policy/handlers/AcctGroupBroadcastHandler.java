package com.elitecore.aaa.radius.service.acct.policy.handlers;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctGroupProxyHandler;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.AsyncGroupCommunicationEntryData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.GroupBroadcastCommunicationHandlerData;
import com.elitecore.aaa.radius.service.handlers.RadiusChainHandler;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

/**
 * 
 * @author soniya.patel
 *
 */
public class AcctGroupBroadcastHandler implements RadAcctServiceHandler {
	private static final String MODULE = "STATEFUL-ACCT-BROADCAST-HNDLR";

	private final RadAcctServiceContext serviceContext;
	private final GroupBroadcastCommunicationHandlerData data;
	private AsyncAccountingChainHandler waitForResponseChain;
	private AsyncAccountingChainHandler noWaitForResponseChain;

	public AcctGroupBroadcastHandler(RadAcctServiceContext serviceContext, GroupBroadcastCommunicationHandlerData groupBroadcastCommunicationHandlerData) {
		this.serviceContext = serviceContext;
		this.data = groupBroadcastCommunicationHandlerData;
		waitForResponseChain = new AsyncAccountingChainHandler(new RadiusChainHandler.ContinueProcessingStrategy());
		noWaitForResponseChain = new AsyncAccountingChainHandler(new RadiusChainHandler.ContinueProcessingStrategy());
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing Broadcast handler for policy: " + data.getPolicyName());
		}

		for (AsyncGroupCommunicationEntryData commEntry : data.getProxyCommunicationEntries()) {
			commEntry.setRadESIGroupData(serviceContext.getServerContext().getServerConfiguration().getRadiusESIGroupConfigurable()
					.getESIGroupByName(commEntry.getRadiusEsiGroupName()));
			RadAcctServiceHandler externalHandler = new RadAcctGroupProxyHandler(serviceContext, commEntry);
			if (commEntry.isWait()) {
				AcctWaitBroadcastFilteredHandler filterHandler = new AcctWaitBroadcastFilteredHandler(commEntry.getRuleset(), externalHandler);
				filterHandler.init();
				waitForResponseChain.addHandler(filterHandler);
			} else {
				AcctFilteredHandler filterHandler = new AcctFilteredHandler(commEntry.getRuleset(), externalHandler);
				filterHandler.init();
				noWaitForResponseChain.addHandler(filterHandler);
			}
		}

		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Stateful Broadcast handler for policy: " + data.getPolicyName());
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
		//  no-op
	}

	@Override
	public boolean isEligible(RadAcctRequest request, RadAcctResponse response) {
		return true;
	}

	@Override
	public void handleRequest(RadAcctRequest request, RadAcctResponse response, ISession session) {		
		noWaitForResponseChain.handleAsyncRequest(request, response, session, 
				new AcctNoWaitForResponseBroadcastResponseListener());

		waitForResponseChain.handleAsyncRequest(request, response, session, 
				new AcctWaitForResponseBroadcastResponseListener(serviceContext,
						request, response));
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return waitForResponseChain.isResponseBehaviorApplicable();
	}
	
}
