package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterExternalCommunicationEntryData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterSynchronousCommunicationHandlerData;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;

/*
 * TODO
 * 1) Translation failure decision
 * 2) Which session to provide in translation while response translation
 */
public class DiaToRadProxyHandler extends DiameterChainHandler<DiameterApplicationHandler<ApplicationRequest,ApplicationResponse>> {

	private static final String MODULE = "DIA-TO-RAD-PROXY-HNDLR";
	private final DiameterSynchronousCommunicationHandlerData data;
	private final DiameterServiceContext serviceContext;
	private final ITranslationAgent translationAgent;
	
	public DiaToRadProxyHandler(DiameterServiceContext serviceContext, 
			DiameterSynchronousCommunicationHandlerData data,
			ITranslationAgent agent) {
		this.serviceContext = serviceContext;
		this.data = data;
		this.translationAgent = agent;
	}
	
	@Override
	public void init() throws InitializationFailedException {
		for (DiameterExternalCommunicationEntryData entry : data.getEntries()) {
			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> proxyCommunicationHandler 
				= new DiaToRadProxyCommunicationHandler(entry, serviceContext, translationAgent);
			DiameterFilteredHandler filteredHandler = new DiameterFilteredHandler(entry.getRuleset(), proxyCommunicationHandler);
			filteredHandler.init();
			addHandler(filteredHandler);
		}
	}

	@Override
	public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
		return true;
	}
	
	@Override
	public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Handling request for policy: "
					+ data.getPolicyName());
		}
		super.handleRequest(request, response, session);
	}

	@Override
	public void resumeRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Resuming request handling for policy: " 
					+ data.getPolicyName());
		}
		super.resumeRequest(request, response, session);
	}
	@Override
	public void reInit() throws InitializationFailedException {
		
	}
}