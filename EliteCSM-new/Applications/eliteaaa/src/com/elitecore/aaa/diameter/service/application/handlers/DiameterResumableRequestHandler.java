package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;

public interface DiameterResumableRequestHandler<T extends ApplicationRequest, V extends ApplicationResponse>
extends DiameterApplicationHandler<T, V> {
	void resume(ApplicationRequest diameterRequest, ApplicationResponse diameterAnswer) throws Exception;
}
