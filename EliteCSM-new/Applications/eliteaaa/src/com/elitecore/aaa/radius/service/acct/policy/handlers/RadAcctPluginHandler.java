package com.elitecore.aaa.radius.service.acct.policy.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elitecore.aaa.radius.plugins.core.RadPluginRequestHandler;
import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.AccountingChainHandler;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.PluginEntryData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadPluginHandlerData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.plugins.data.PluginMode;
import com.elitecore.core.commons.plugins.data.ServicePolicyFlow;
import com.elitecore.core.commons.plugins.data.ServiceTypeConstants;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

/**
 * 
 * @author narendra.pathai
 *
 */
public class RadAcctPluginHandler extends AccountingChainHandler {
	private static final String MODULE = "ACCT-PLGN-HNDLR";
	private final RadAcctServiceContext serviceContext;
	private final RadPluginHandlerData data;

	public RadAcctPluginHandler(RadAcctServiceContext serviceContext, RadPluginHandlerData radPluginHandlerData) {
		this.serviceContext = serviceContext;
		this.data = radPluginHandlerData;
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Initializing Accounting Plugin handler for policy: " + data.getPolicyName());
		}
		List<PluginEntryData> pluginEntries = data.getPluginEntries();
		for (int index=0; index < pluginEntries.size(); index++) {
			PluginEntryData pluginData = pluginEntries.get(index);
			PluginExecutionHandler executionHandler = new PluginExecutionHandler(pluginData, index);
			AcctFilteredHandler filteredHandler = new AcctFilteredHandler(pluginData.getRuleset(), executionHandler);
			filteredHandler.init();
			addHandler(filteredHandler);
		}
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Accounting Plugin handler for policy: " + data.getPolicyName());
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}
	
	@Override
	public boolean isEligible(RadAcctRequest request, RadAcctResponse response) {
		return true;
	}
	
	@Override
	protected RadiusRequestExecutor<RadAcctRequest, RadAcctResponse> getExecutor(RadAcctRequest request) {
		return request.getExecutor();
	}

	/**
	 * Adapter on the {@link RadPluginRequestHandler} which adapts the plugin request
	 * handler interface into {@link RadAcctServiceHandler} form, so that it can be used
	 * in service handler hierarchy.
	 * 
	 * @author narendra.pathai
	 *
	 */
	class PluginExecutionHandler implements RadAcctServiceHandler {

		private final PluginEntryData entryData;
		private RadPluginRequestHandler pluginRequestHandler;
		private final int index;
		
		public PluginExecutionHandler(PluginEntryData entryData, int index) {
			this.entryData = entryData;
			this.index = index;
		}
		
		@Override
		public void init() throws InitializationFailedException {
			if(entryData.isOnResponse() == false) {
				pluginRequestHandler = serviceContext.createPluginRequestHandler(getPluginEntryDetails(PluginMode.PRE), Collections.<PluginEntryDetail> emptyList());
			} else {
				pluginRequestHandler = serviceContext.createPluginRequestHandler(Collections.<PluginEntryDetail> emptyList(), getPluginEntryDetails(PluginMode.POST));
			}
		}

		private List<PluginEntryDetail> getPluginEntryDetails(PluginMode mode) {
			List<PluginEntryDetail> prePlugin = new ArrayList<PluginEntryDetail>();
			PluginEntryDetail data = new PluginEntryDetail();
			data.setPluginName(entryData.getPluginName());
			data.setPluginArgument(entryData.getPluginArgument());
			data.setCallerId(getCallerId(mode));
			prePlugin.add(data);
			return prePlugin;
		}

		private PluginCallerIdentity getCallerId(PluginMode mode) {
			return PluginCallerIdentity.createAndGetIdentity(ServiceTypeConstants.RAD_ACCT, mode, index, this.entryData.getPluginName())
					.setServicePolicyName(data.getPolicyName()).setServicePolicyFlow(ServicePolicyFlow.ACCT_FLOW)
					.setPluginHandlerName(data.getHandlerName()).getId();
		}
		
		@Override
		public void reInit() throws InitializationFailedException {
			
		}

		@Override
		public boolean isEligible(RadAcctRequest request, RadAcctResponse response) {
			return true;
		}

		@Override
		public void handleRequest(RadAcctRequest request, RadAcctResponse response, ISession session) {
			if(entryData.isOnResponse() == false) {
				pluginRequestHandler.handlePreRequest(request, response, session);
			} else {
				pluginRequestHandler.handlePostRequest(request, response, session);
			}
		}

		@Override
		public boolean isResponseBehaviorApplicable() {
			return false;
		}
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}
}