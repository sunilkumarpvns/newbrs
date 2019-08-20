package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterApplicationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterPostResponseHandlerData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

/**
 * 
 * @author narendra.pathai
 *
 */
public class DiameterPostResponseHandler extends DiameterChainHandler<DiameterApplicationHandler<ApplicationRequest, ApplicationResponse>> {

	private static final String MODULE = "DIA-POST-RES-HNDLR";
	private DiameterServiceContext context;
	private DiameterPostResponseHandlerData data;

	public DiameterPostResponseHandler(DiameterServiceContext context,
			DiameterPostResponseHandlerData diameterPostResponseHandlerData) {
		this.context = context;
		this.data = diameterPostResponseHandlerData;
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Initializing Diameter Post Response handler for policy: " + data.getPolicyName());
		}
		
		for (DiameterApplicationHandlerData handlerData : data.getHandlersData()) {
			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = 
					handlerData.createHandler(context);
			handler.init();
			addHandler(handler);
		}
	}
	
	@Override
	public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Handling post response flow for Diameter TGPP policy: " + data.getPolicyName());
		}
		
		super.handleRequest(request, response, session);
	}

	@Override
	public void reInit() throws InitializationFailedException {

	}

}
