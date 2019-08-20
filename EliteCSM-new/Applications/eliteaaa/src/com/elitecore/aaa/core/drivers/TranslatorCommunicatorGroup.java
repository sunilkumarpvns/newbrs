package com.elitecore.aaa.core.drivers;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.systemx.esix.ESCommunicatorGroup;
import com.elitecore.diameterapi.core.common.TranslationFailedException;

public interface TranslatorCommunicatorGroup extends ESCommunicatorGroup<IEliteCrestelRatingDriver> {
	
	public void translate(ApplicationRequest request,ApplicationResponse response) throws TranslationFailedException;

}
