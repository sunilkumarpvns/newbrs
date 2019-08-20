package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterBroadcastCommunicationEntryData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterBroadcastCommunicationHandlerData;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;

public class DiaToRadBroadcastHandler extends DiameterBroadcastHandler {

	private static final String MODULE = "DIA-TO-RAD-BRDCST-HNDLR";
	private ITranslationAgent translationAgent;
	private DiameterServiceContext context;

	public DiaToRadBroadcastHandler(DiameterServiceContext context,
			DiameterBroadcastCommunicationHandlerData diameterAsynchronousCommunicationHandlerData,
			ITranslationAgent translationAgent) {
		super(context, diameterAsynchronousCommunicationHandlerData);
		this.context = context;
		this.translationAgent = translationAgent;
	}

	@Override
	protected DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> newProxyCommunicationHandler(
			DiameterBroadcastCommunicationEntryData entryData) {
		return new DiaToRadProxyCommunicationHandler(entryData, context, translationAgent);
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
	protected void processOnNoHandlerEligible(ApplicationRequest request, ApplicationResponse response) {
		// DO NOTHING. THIS FEATURE IS PRESENT IN DIAMETER BROADCASTING HANDLER ONLY.
	}
}
