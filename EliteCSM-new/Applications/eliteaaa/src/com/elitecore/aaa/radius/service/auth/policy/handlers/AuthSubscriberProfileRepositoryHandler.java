package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.core.subscriber.SubscriberProfileRepositoryAware;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.SubscriberProfileRepositoryHandler;
import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class AuthSubscriberProfileRepositoryHandler 
extends SubscriberProfileRepositoryHandler<RadAuthRequest, RadAuthResponse> 
implements RadAuthServiceHandler, SubscriberProfileRepositoryAware {

	private static final String MODULE = "AUTH-USER-PROFILE-HNDLR";

	@Override
	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		return true;
	}

	@Override
	protected void actionOnProfileNotFound(RadAuthRequest request, RadAuthResponse response) {
		RadiusProcessHelper.rejectResponse(response, 
				AuthReplyMessageConstant.USER_NOT_FOUND);

		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Subscriber profile not found, " 
					+ "sending Access-Reject");
		}
	}

	@Override
	protected String getModule() {
		return MODULE;
	}
}
