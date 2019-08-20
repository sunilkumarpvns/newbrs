package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.AuthenticationChainHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthPostResponseHandlerData;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthServicePolicyHandlerData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AuthPostResponseHandler extends AuthenticationChainHandler {
	private static final String MODULE = "AUTH-POST-RES-HNDLR";

	private final AuthPostResponseHandlerData data;
	private final RadAuthServiceContext serviceContext;

	public AuthPostResponseHandler(RadAuthServiceContext serviceContext, AuthPostResponseHandlerData authPostResponseHandlerData) {
		this.serviceContext = serviceContext;
		this.data = authPostResponseHandlerData;
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing Authentication Post Response handler for policy: " + data.getPolicyName());
		}
		for (AuthServicePolicyHandlerData handlerData : data.getHandlersData()) {
			try {
				RadAuthServiceHandler handler = handlerData.createHandler(serviceContext);
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
			LogManager.getLogger().info(MODULE, "Successfully initialized Authentication Post Response handler for policy: " + data.getPolicyName());
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
	protected RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> getExecutor(RadAuthRequest request) {
		return request.getExecutor();
	}

}
