package com.elitecore.aaa.radius.service.auth.policy.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elitecore.aaa.radius.plugins.core.RadPluginRequestHandler;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.AuthenticationChainHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.PluginEntryData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadPluginHandlerData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.commons.logging.LogLevel;
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
public class RadAuthPluginHandler extends AuthenticationChainHandler {
	private static final String MODULE = "AUTH-PLGN-HNDLR";
	private final RadAuthServiceContext serviceContext;
	private final RadPluginHandlerData data;

	public RadAuthPluginHandler(RadAuthServiceContext serviceContext, RadPluginHandlerData radPluginHandlerData) {
		this.serviceContext = serviceContext;
		this.data = radPluginHandlerData;
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing Authentication Plugin handler for policy: " + data.getPolicyName());
		}
		List<PluginEntryData> pluginEntries = data.getPluginEntries();
		for(int index=0; index < pluginEntries.size(); index++) {
			PluginEntryData pluginData = pluginEntries.get(index);
			PluginExecutionHandler executionHandler = new PluginExecutionHandler(pluginData, index);
			AuthFilteredHandler filteredHandler = new AuthFilteredHandler(pluginData.getRuleset(), executionHandler);
			filteredHandler.init();
			addHandler(filteredHandler);
		}
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Authentication Plugin handler for policy: " + data.getPolicyName());
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}
	
	@Override
	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		return true;
	}
	
	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}

	@Override
	protected RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> getExecutor(RadAuthRequest request) {
		return request.getExecutor();
	}

	class PluginExecutionHandler implements RadAuthServiceHandler {

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
			List<PluginEntryDetail> postPlugin = new ArrayList<PluginEntryDetail>();
			PluginEntryDetail data = new PluginEntryDetail();
			data.setPluginName(entryData.getPluginName());
			data.setPluginArgument(entryData.getPluginArgument());
			data.setCallerId(getCallerId(mode));
			postPlugin.add(data);
			return postPlugin;
		}

		private PluginCallerIdentity getCallerId(PluginMode mode) {
			return PluginCallerIdentity.createAndGetIdentity(ServiceTypeConstants.RAD_AUTH, mode, index, this.entryData.getPluginName())
					.setServicePolicyName(data.getPolicyName()).setServicePolicyFlow(ServicePolicyFlow.AUTH_FLOW)
					.setPluginHandlerName(data.getHandlerName()).getId();
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
}