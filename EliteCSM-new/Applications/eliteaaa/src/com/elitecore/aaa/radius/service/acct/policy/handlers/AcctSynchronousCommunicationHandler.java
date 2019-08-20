package com.elitecore.aaa.radius.service.acct.policy.handlers;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.AccountingChainHandler;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.SynchronousCommunicationHandlerData;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AcctSynchronousCommunicationHandler extends AccountingChainHandler {
	private static final String MODULE = "ACCT-PROXY-HNDLR";
	private final RadAcctServiceContext serviceContext;
	private final SynchronousCommunicationHandlerData data;

	public AcctSynchronousCommunicationHandler(RadAcctServiceContext serviceContext, SynchronousCommunicationHandlerData data) {
		this.serviceContext = serviceContext;
		this.data = data;
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Initializing Accounting Proxy Communication handler for policy: " + data.getPolicyName());
		}
		AcctExternalCommunicationHandlerFactory factory = new AcctExternalCommunicationHandlerFactory(serviceContext);
		for (ExternalCommunicationEntryData commEntry : data.getProxyCommunicatioEntries()) {
			RadAcctServiceHandler externalHandler = factory.createHandler(commEntry);
			AcctFilteredHandler filterHandler = new AcctFilteredHandler(commEntry.getRuleset(), externalHandler);
			filterHandler.init();
			addHandler(filterHandler);
		}
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Authentication Proxy Communication handler for policy: " + data.getPolicyName());
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}
}
