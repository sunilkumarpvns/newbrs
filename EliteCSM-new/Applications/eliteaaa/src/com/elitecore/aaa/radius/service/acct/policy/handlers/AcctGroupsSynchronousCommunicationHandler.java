package com.elitecore.aaa.radius.service.acct.policy.handlers;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.AccountingChainHandler;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctGroupProxyHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.GroupExternalCommunicationEntryData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.GroupSynchronousCommunicationHandlerData;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;

public class AcctGroupsSynchronousCommunicationHandler extends AccountingChainHandler {

	private static final String MODULE = "ACCT-GROUP-SYNC-PROXY-HNDLR";

	private RadAcctServiceContext serviceContext;
	private GroupSynchronousCommunicationHandlerData data;

	public AcctGroupsSynchronousCommunicationHandler(RadAcctServiceContext serviceContext,
			GroupSynchronousCommunicationHandlerData data) {
		this.serviceContext = serviceContext;
		this.data = data;
	}

	@Override
	public void init() throws InitializationFailedException {

		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Initializing stateful accounting proxy communication "
					+ "handler for policy: " + data.getPolicyName());
		}

		for (GroupExternalCommunicationEntryData commEntry : data.getExternalCommunicationEntries()) {
			commEntry.setRadESIGroupData(serviceContext.getServerContext().getServerConfiguration().getRadiusESIGroupConfigurable()
					.getESIGroupByName(commEntry.getRadiusEsiGroupName()));

			RadAcctGroupProxyHandler radProxyHandler = new RadAcctGroupProxyHandler(serviceContext, commEntry);

			AcctFilteredHandler filterHandler = new AcctFilteredHandler(commEntry.getRuleset(), radProxyHandler);
			filterHandler.init();
			addHandler(filterHandler);
		}

		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Successfully initialized stateful accounting proxy communication "
					+ "handler for policy: " + data.getPolicyName());
		}
	}
}
