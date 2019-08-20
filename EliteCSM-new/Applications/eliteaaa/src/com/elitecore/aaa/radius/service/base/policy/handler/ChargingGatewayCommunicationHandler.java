package com.elitecore.aaa.radius.service.base.policy.handler;

import com.elitecore.aaa.radius.service.RadServiceContext;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * This is a support class for all handlers that need to communicate with Charging Gateway
 * type of external systems.
 * 
 * @author narendra.pathai
 *
 * @param <T> type of request packet
 * @param <V> type of response packet
 */
public abstract class ChargingGatewayCommunicationHandler<T extends RadServiceRequest, V extends RadServiceResponse> extends RMCommunicationHandler<T, V> {
	public ChargingGatewayCommunicationHandler(RadServiceContext<T, V> serviceContext, ExternalCommunicationEntryData data) {
		super(serviceContext, data);
	}

	@Override
	public boolean isEligible(T request, V response) {
		return true;
	}
	
	@Override
	public void init() throws InitializationFailedException {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(getModule(), "Initializing Charging Communication handler for policy: " + getData().getPolicyName());
		}
		super.init();
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(getModule(), "Successfully initialized Charging Communication handler for policy: " + getData().getPolicyName());
		}
	}
}
