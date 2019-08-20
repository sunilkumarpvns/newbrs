package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.AuthenticationChainHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthGroupProxyHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.GroupExternalCommunicationEntryData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.GroupSynchronousCommunicationHandlerData;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;

public class AuthGroupsSynchronousCommunicationHandler extends AuthenticationChainHandler {

	private static final String MODULE = "AUTH-GROUP-SYNC-PROXY-HNDLR";

	private final GroupSynchronousCommunicationHandlerData data;
	private final RadAuthServiceContext serviceContext;


	public AuthGroupsSynchronousCommunicationHandler(RadAuthServiceContext serviceContext,
			GroupSynchronousCommunicationHandlerData statefulSynchronousCommunicationHandlerData) {
		this.serviceContext = serviceContext;
		this.data = statefulSynchronousCommunicationHandlerData;
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Initializing authentication proxy communication handler for policy: " + data.getPolicyName());
		}

		for (GroupExternalCommunicationEntryData commEntry : data.getExternalCommunicationEntries()) {
			commEntry.setRadESIGroupData(serviceContext.getServerContext().getServerConfiguration().getRadiusESIGroupConfigurable()
					.getESIGroupByName(commEntry.getRadiusEsiGroupName()));

			RadAuthGroupProxyHandler radProxyHandler = new RadAuthGroupProxyHandler(serviceContext, commEntry);

			AuthFilteredHandler filterHandler = new AuthFilteredHandler(commEntry.getRuleset(), radProxyHandler);
			filterHandler.init();
			addHandler(filterHandler);
		}
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Successfully initialized authentication proxy communication "
					+ "handler for policy: " + data.getPolicyName());
		}
	}

}
