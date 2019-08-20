package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.ConcurrencyHandlerData;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

/**
 * Performs concurrent login checks for the request received. Helpful when need to 
 * restrict some user to limited number of logins. 
 * 
 * @author narendra.pathai
 *
 */
public class ConcurrencyHandler implements RadAuthServiceHandler {
	private static final String MODULE = "CONC-HNDLR";
	private final RadAuthServiceContext serviceContext;
	private final ConcurrencyHandlerData data;
	private ConcurrencySessionManager sessionManager;

	public ConcurrencyHandler(RadAuthServiceContext serviceContext, ConcurrencyHandlerData concurrencyHandlerData) {
		this.serviceContext = serviceContext;
		this.data = concurrencyHandlerData;
	}

	@Override
	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		return true;
	}

	@Override
	public void handleRequest(RadAuthRequest request, RadAuthResponse response, ISession session) {
		sessionManager.handleAuthenticationRequest(request, response, serviceContext);
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing Concurrency handler for policy: " + data.getPolicyName());
		}
		
		Optional<ConcurrencySessionManager> optionalSessionManager = serviceContext.getServerContext().getLocalSessionManager(data.getSessionManagerId());
		if(optionalSessionManager.isPresent() == false) {
			throw new InitializationFailedException("Session Manager with id: " + data.getSessionManagerId() + " not found.");
		}
		sessionManager = optionalSessionManager.get();
		
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Concurrency handler for policy: " + data.getPolicyName());
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}

}
