package com.elitecore.aaa.diameter.service.application.handlers;

import java.util.Iterator;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

public class DiameterAsyncChainHandler extends DiameterChainHandler<DiameterAsyncApplicationHandler<ApplicationRequest,ApplicationResponse>> 
implements DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> {
	
	public DiameterAsyncChainHandler() {
		super(new ContinueProcessingStrategy());
	}
	
	@Override
	public void handleAsyncRequest(ApplicationRequest request, ApplicationResponse response,
			ISession session, DiameterBroadcastResponseListener listener) {
		process(request, response, session,  iterator(), listener);
	}

	private void process(ApplicationRequest request, ApplicationResponse response, 
			ISession session, Iterator<DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse>> iterator,
			DiameterBroadcastResponseListener listener) {
		boolean anyHandlerExecuted = false;
		
		while (iterator.hasNext()) {
			DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> next = iterator.next();
			if (next.isEligible(request, response) == false) {
				continue;
			}
			next.handleAsyncRequest(request, response, session, listener);
			anyHandlerExecuted = true;
		}
		
		if (anyHandlerExecuted == false) {
			processOnNoHandlerEligible(request, response);
		}
	}

	@Override
	public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
		throw new UnsupportedOperationException("Handling request in sync is not supported. Use handleAsyncRequest() instead.");
	}
	
	@Override
	public void resumeRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
		throw new UnsupportedOperationException("Resuming request in sync is not supported.");
	}
}
