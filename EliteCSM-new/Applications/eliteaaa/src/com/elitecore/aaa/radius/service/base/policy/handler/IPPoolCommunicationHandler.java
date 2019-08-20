package com.elitecore.aaa.radius.service.base.policy.handler;

import javax.annotation.Nonnull;

import com.elitecore.aaa.radius.service.RadServiceContext;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * 
 * @author narendra.pathai
 *
 * @param <T>
 * @param <V>
 */
public abstract class IPPoolCommunicationHandler<T extends RadServiceRequest, V extends RadServiceResponse> extends RMCommunicationHandler<T, V> {

	public IPPoolCommunicationHandler(@Nonnull RadServiceContext<T, V> serviceContext, @Nonnull ExternalCommunicationEntryData data) {
		super(serviceContext, data);
	}

	@Override
	public boolean isEligible(T request, V response) {
		boolean isPortalClient = getServiceContext().getServerContext().getServerConfiguration().getRadClientConfiguration().isPortalTypeClient(request.getClientIp());
		if(isPortalClient) {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(getModule(), "Request received from Portal, Skipping IP Pool Communication.");
			}
			return false;
		}
		return true;
	}
	
	@Override
	public void init() throws InitializationFailedException {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(getModule(), "Initializing IP Pool Communication handler for policy: " + getData().getPolicyName());
		}
		super.init();
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(getModule(), "Successfully initialized IP Pool Communication handler for policy: " + getData().getPolicyName());
		}
	}
}
