package com.elitecore.aaa.radius.service.auth.handlers;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.subscriber.SubscriberProfileRepositoryAware;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.subscriber.RadiusSubscriberProfileRepository;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class BlackListServiceHandler implements RadAuthServiceHandler, SubscriberProfileRepositoryAware {
	
	private static final String MODULE = null;
	private RadAuthServiceContext radAuthServiceContext;
	private BWMode bwMode;
	private RadiusSubscriberProfileRepository accountInfoProvider;
	
	public BlackListServiceHandler(RadAuthServiceContext radAuthServiceContext,BWMode bwMode) {
		this.radAuthServiceContext = radAuthServiceContext;
		this.bwMode = bwMode;
	}

	@Override
	public void init() throws InitializationFailedException {
		
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
		AccountData accountData = request.getAccountData();
		if(accountData == null){
			accountData = accountInfoProvider.getAccountData(request,response);
			if(accountData == null) {
				accountData = accountInfoProvider.getAnonymousAccountData(request);
				if(accountData == null) {
					response.setFurtherProcessingRequired(false);					
					response.setResponseMessage(AuthReplyMessageConstant.USER_NOT_FOUND);				
					response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
					return;
				}
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, " User Not found further processing by Anonymous User Profile : " + request.getAccountData());
				}
			}
		}
		
		if(radAuthServiceContext.isBlockedUser(request, bwMode)){
			response.setResponseMessage(AuthReplyMessageConstant.ACCOUNT_IS_BLACKLISTED);
			response.setFurtherProcessingRequired(false);			
			response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);			
			return;
		}
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return this.accountInfoProvider.isAlive() == false;
	}

	@Override
	public void setSubscriberProfileRepository(RadiusSubscriberProfileRepository spr) {
		this.accountInfoProvider = spr;
	}

}
