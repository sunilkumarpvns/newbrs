package com.elitecore.aaa.core.drivers;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.diameterapi.core.common.TranslationFailedException;

public interface IEliteCrestelRatingDriver  extends ESCommunicator {
	public void handleRequest(ApplicationRequest request,ApplicationResponse response) throws TranslationFailedException;
		
}
