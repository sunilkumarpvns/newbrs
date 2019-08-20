package com.elitecore.aaa.diameter.service.application.handlers;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterPluginHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.PluginEntryData;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.plugins.data.PluginMode;
import com.elitecore.core.commons.plugins.data.ServiceTypeConstants;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.plugins.DiameterPluginManager;

public class DiameterPluginHandler extends DiameterChainHandler<DiameterApplicationHandler<ApplicationRequest, ApplicationResponse>> {

	private static final String MODULE = "PLUGIN-HNDLR";
	private DiameterPluginHandlerData data;
	private final DiameterServiceContext context;

	public DiameterPluginHandler(DiameterServiceContext context,
			DiameterPluginHandlerData data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public void init() throws InitializationFailedException {
		List<PluginEntryData> pluginEntries = data.getPluginEntries();
		for (int index = 0; index < pluginEntries.size(); index++) {
			PluginEntryData pluginEntry = pluginEntries.get(index);
			PluginRequestApplicationHandler pluginRequestHandler = new PluginRequestApplicationHandler(pluginEntry, index);
			DiameterFilteredHandler filteredHandler = new DiameterFilteredHandler(pluginEntry.getRuleset(), pluginRequestHandler);
			filteredHandler.init();
			addHandler(filteredHandler);
		}
	}
	
	@Override
	public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Handling request for policy: " 
					+ data.getPolicyName());
		}
		super.handleRequest(request, response, session);
	}

	class PluginRequestApplicationHandler implements DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> {
		
		private DiameterPluginManager pluginManager;
		private final PluginEntryData data;
		private final int index;
		
		public PluginRequestApplicationHandler(final PluginEntryData data, final int index) {
			pluginManager = new DiameterPluginManager(context.getServerContext().getDiameterPluginManager().getNameToPluginMap());
			this.data = data;
			this.index = index;
		}
		
		@Override
		public void init() throws InitializationFailedException {
			if (data.isOnResponse() == false) {
				List<PluginEntryDetail> prePlugins = getPluginEntryDetails(PluginMode.OUT);
				pluginManager.registerInPlugins(prePlugins);
			} else {
				List<PluginEntryDetail> postPlugins = getPluginEntryDetails(PluginMode.OUT);
				pluginManager.registerOutPlugins(postPlugins);
			}
			
		}

		private List<PluginEntryDetail> getPluginEntryDetails(PluginMode mode) {
			List<PluginEntryDetail> plugins = new ArrayList<PluginEntryDetail>();
			PluginEntryDetail pluginData = new PluginEntryDetail();
			pluginData.setPluginName(data.getPluginName());
			pluginData.setPluginArgument(data.getPluginArgument());
			pluginData.setCallerId(getCallerId(mode));
			plugins.add(pluginData);
			return plugins;
		}

		private PluginCallerIdentity getCallerId(PluginMode mode) {
				return PluginCallerIdentity.createAndGetIdentity(ServiceTypeConstants.DIA_3GPP, mode, index, data.getPluginName())
						.setServicePolicyName(DiameterPluginHandler.this.data.getPolicyName())
						.setPluginHandlerName(DiameterPluginHandler.this.data.getHandlerName()).getId();
		}

		@Override
		public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
			return true;
		}

		@Override
		public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
			try	{
				if (data.isOnResponse() == false) {
					pluginManager.applyInPlugins(request.getDiameterRequest(), response.getDiameterAnswer(), session);
				} else {
					pluginManager.applyOutPlugins(request.getDiameterRequest(), response.getDiameterAnswer(), session);
				}
			} catch (Exception e){
				LogManager.getLogger().error(MODULE, "Unable to apply plugin: "+ data.getPluginName() + ", Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}

		}

		@Override
		public void reInit() throws InitializationFailedException {
			
		}
		
		@Override
		public boolean isResponseBehaviorApplicable() {
			return false;
		}
	}
}
