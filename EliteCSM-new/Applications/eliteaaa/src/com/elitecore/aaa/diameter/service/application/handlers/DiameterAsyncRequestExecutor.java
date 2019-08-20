package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

public interface DiameterAsyncRequestExecutor {
	void handleServiceRequest(ApplicationRequest serviceRequest, ApplicationResponse serviceResponse,
			ISession session);
}
