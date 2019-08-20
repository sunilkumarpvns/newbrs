package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

public interface DiameterAsyncApplicationHandler<T extends ApplicationRequest, V extends ApplicationResponse> 
extends DiameterApplicationHandler<T, V>{

	public void handleAsyncRequest(ApplicationRequest request, ApplicationResponse response, ISession session, DiameterBroadcastResponseListener listener);
}
