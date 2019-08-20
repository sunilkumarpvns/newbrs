package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.AuthenticationChainHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.CdrGenerationHandlerData;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.CdrHandlerEntryData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AuthCdrGenerationHandler extends AuthenticationChainHandler {

	private static final String MODULE = "AUTH-CDR-GEN-HNDLR";
	private final RadAuthServiceContext serviceContext;
	private final CdrGenerationHandlerData data;

	public AuthCdrGenerationHandler(RadAuthServiceContext serviceContext, CdrGenerationHandlerData cdrGenerationHandlerData) {
		this.serviceContext = serviceContext;
		this.data = cdrGenerationHandlerData;
	}

	@Override
	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		return true;
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing CDR Generation handler for policy: " + data.getPolicyName());
		}
		super.init();
		for (CdrHandlerEntryData entry : data.getCdrHandlers()) {
			RadAuthServiceHandler cdrHandler = entry.createHandler(serviceContext);
			AuthFilteredHandler filteredHandler = new AuthFilteredHandler(entry.getRuleset(), cdrHandler);
			filteredHandler.init();
			addHandler(filteredHandler);
		}
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Successfully initialized CDR Generation handler for policy: " + data.getPolicyName());
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {

	}

	@Override
	protected RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> getExecutor(RadAuthRequest request) {
		return request.getExecutor();
	}

}
