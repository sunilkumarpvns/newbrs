package com.elitecore.aaa.radius.service.base.policy.handler;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.subscriber.SubscriberProfileRepositoryAware;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.handlers.RadServiceHandler;
import com.elitecore.aaa.radius.subscriber.RadiusSubscriberProfileRepository;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

public abstract class SubscriberProfileRepositoryHandler<T extends RadServiceRequest, V extends RadServiceResponse> 
implements RadServiceHandler<T, V>, SubscriberProfileRepositoryAware {

	private RadiusSubscriberProfileRepository spr;

	@Override
	public void setSubscriberProfileRepository(
			RadiusSubscriberProfileRepository spr) {
		this.spr = spr;
	}
	
	@Override
	public void init() throws InitializationFailedException {
		// no-op
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		// no-op
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return spr.isAlive() == false;
	}
	
	public final void handleRequest(T request, V response, ISession session) {
		AccountData accountData = request.getAccountData();
		if (accountData == null) {
			accountData = spr.getAccountData(request, response);
		}
		
		if (accountData == null) {
			accountData = spr.getAnonymousAccountData(request);
			if (accountData == null) {
				actionOnProfileNotFound(request, response);
				return;
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(getModule(), 
							" User Not found further processing by " +
							"Anonymous User Profile : " + request.getAccountData());
				}
			}
		}
		
		request.setAccountData(accountData);
	}

	protected abstract String getModule();
	protected abstract void actionOnProfileNotFound(T request, V response);
}
