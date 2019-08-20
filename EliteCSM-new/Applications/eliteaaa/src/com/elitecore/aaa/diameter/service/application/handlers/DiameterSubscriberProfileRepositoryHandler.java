package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.SubscriberProfileRepositoryAware;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepository;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

/**
 * 
 * @author narendra.pathai
 *
 */
public class DiameterSubscriberProfileRepositoryHandler 
implements DiameterApplicationHandler<ApplicationRequest, ApplicationResponse>,
SubscriberProfileRepositoryAware {

	private static final String MODULE = "DIA-PROFILE-LOOKUP-HNDLR";
	private DiameterSubscriberProfileRepository spr;

	@Override
	public void init() throws InitializationFailedException {
		LogManager.getLogger().info(MODULE, "Diameter profile lookup handler initialized successfully.");
	}

	@Override
	public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
		return true;
	}

	@Override
	public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
		AccountData accountData = request.getAccountData();
		if (accountData == null) {
			accountData = spr.getAccountData(request, response);
		}
		
		if (accountData == null) {
			accountData = spr.getAnonymousAccountData(request);
			if (accountData == null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, 
							"Subscriber profile could not be located.");
				}
				return;
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, 
							" User Not found further processing by " +
							"Anonymous User Profile : " + request.getAccountData());
				}
			}
		}
		
		request.setAccountData(accountData);
	
	}

	@Override
	public void reInit() throws InitializationFailedException {

	}

	@Override
	public void setSubscriberProfileRepository(DiameterSubscriberProfileRepository spr) {
		this.spr = spr;
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return spr.isAlive() == false;
	}

}
