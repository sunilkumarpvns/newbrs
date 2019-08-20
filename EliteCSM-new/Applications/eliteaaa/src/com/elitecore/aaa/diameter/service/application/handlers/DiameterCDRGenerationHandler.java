package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterCDRGenerationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterCDRHandlerEntryData;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * 
 * @author narendra.pathai
 *
 */
public class DiameterCDRGenerationHandler extends DiameterChainHandler<DiameterApplicationHandler<ApplicationRequest, ApplicationResponse>> {

	private DiameterServiceContext context;
	private DiameterCDRGenerationHandlerData data;

	public DiameterCDRGenerationHandler(DiameterServiceContext context,
			DiameterCDRGenerationHandlerData data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public void init() throws InitializationFailedException {
		for (DiameterCDRHandlerEntryData entryData : data.getEntries()) {
			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> cdrHandler = 
					entryData.createHandler(context);
			
			DiameterFilteredHandler filteredHandler = new DiameterFilteredHandler(entryData.getRuleset(), cdrHandler);
			filteredHandler.init();
			addHandler(filteredHandler);
		}
	}

	@Override
	public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
		return true;
	}
}
