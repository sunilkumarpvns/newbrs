package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.AuthenticationChainHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.SynchronousCommunicationHandlerData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AuthSynchronousCommunicationHandler extends AuthenticationChainHandler {
	private static final String MODULE = "AUTH-PROXY-HNDLR";
	
	private final SynchronousCommunicationHandlerData data;
	private final RadAuthServiceContext serviceContext;

	public AuthSynchronousCommunicationHandler(RadAuthServiceContext serviceContext, SynchronousCommunicationHandlerData proxyHandlerData) {
		this.serviceContext = serviceContext;
		this.data = proxyHandlerData;
	}

	@Override
	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		return true;
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing Authentication Proxy Communication handler for policy: " + data.getPolicyName());
		}
		AuthExternalCommunicationHandlerFactory factory = new AuthExternalCommunicationHandlerFactory(serviceContext);
		for (ExternalCommunicationEntryData commEntry : data.getProxyCommunicatioEntries()) {
			RadAuthServiceHandler externalHandler = factory.createHandler(commEntry);
			AuthFilteredHandler filterHandler = new AuthFilteredHandler(commEntry.getRuleset(), externalHandler);
			filterHandler.init();
			addHandler(filterHandler);
		}
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Authentication Proxy Communication handler for policy: " + data.getPolicyName());
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}
}
