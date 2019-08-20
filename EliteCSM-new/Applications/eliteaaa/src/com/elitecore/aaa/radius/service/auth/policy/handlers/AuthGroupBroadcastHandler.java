package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthGroupProxyHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
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
public class AuthGroupBroadcastHandler implements RadAuthServiceHandler {
	private static final String MODULE = "STATEFUL-AUTH-BROADCAST-HNDLR";

	private final RadAuthServiceContext serviceContext;
	private final GroupBroadcastCommunicationHandlerData data;
	private AsyncAuthenticationChainHandler waitForResponseChain;
	private AsyncAuthenticationChainHandler noWaitForResponseChain;

	public AuthGroupBroadcastHandler(RadAuthServiceContext serviceContext, GroupBroadcastCommunicationHandlerData data) {
		this.serviceContext = serviceContext;
		this.data = data;
		waitForResponseChain = new AsyncAuthenticationChainHandler(new RadiusChainHandler.ContinueProcessingStrategy());
		noWaitForResponseChain = new AsyncAuthenticationChainHandler(new RadiusChainHandler.ContinueProcessingStrategy());
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing Broadcast handler for policy: " + data.getPolicyName());
		}

		for(AsyncGroupCommunicationEntryData commEntry : data.getProxyCommunicationEntries()) {
			commEntry.setRadESIGroupData(serviceContext.getServerContext().getServerConfiguration().getRadiusESIGroupConfigurable()
					.getESIGroupByName(commEntry.getRadiusEsiGroupName()));
			RadAuthServiceHandler externalHandler = new RadAuthGroupProxyHandler(this.serviceContext, commEntry);
			if (commEntry.isWait()) {
				AuthWaitBroadcastFilteredHandler filterHandler = new AuthWaitBroadcastFilteredHandler(commEntry.getRuleset(), externalHandler);
				filterHandler.init();
				waitForResponseChain.addHandler(filterHandler);
			} else {
				AuthFilteredHandler filterHandler = new AuthFilteredHandler(commEntry.getRuleset(), externalHandler);
				filterHandler.init();
				noWaitForResponseChain.addHandler(filterHandler);
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
	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		return true;
	}

	@Override
	public void handleRequest(RadAuthRequest request, RadAuthResponse response, ISession session) {
		noWaitForResponseChain.handleAsyncRequest(request, response, 
				session, new AuthNoWaitForResponseBroadcastResponseListener());

		waitForResponseChain.handleAsyncRequest(request, response, 
				session, new AuthWaitForResponseBroadcastResponseListener(serviceContext, 
						request, response));
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return waitForResponseChain.isResponseBehaviorApplicable(); 
	}

}
