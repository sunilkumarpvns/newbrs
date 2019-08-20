package com.elitecore.aaa.diameter.service.application.handlers;

import java.util.Iterator;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

/**
 * FIXME javadoc
 * FIXME This handler can be called ShortCircuitChainHandler. Which would be more apt.
 * @author narendra.pathai
 *
 */
public class DiameterIfElseHandler extends DiameterChainHandler<DiameterFilteredHandler>
implements DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> {

	@Override
	public void resumeRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
		/*
		 * This handler will never be resumed
		 */
	}

	@Override
	public void init() throws InitializationFailedException {
		// no-op
	}

	@Override
	public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
		return true;
	}

	@Override
	public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
		// if the processing is already completed then no point in handling request
		if (shouldContinue(request, response) == false) {
			return;
		}
		
		boolean isAnyHandlerEligible = false; 
		Iterator<DiameterFilteredHandler> iterator = iterator();
		while (iterator.hasNext()) {
			DiameterFilteredHandler next = iterator.next();
			if (next.isEligible(request, response) == false) {
				continue;
			}
			isAnyHandlerEligible = true;
			next.handleRequest(request, response, session);
			// One of the filter is satisfied, so no other filter should be checked.
			break;
		}
		
		if (isAnyHandlerEligible == false) {
			processOnNoHandlerEligible(request, response);
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}
	
	@Override
	public boolean isResponseBehaviorApplicable() {
		Iterator<DiameterFilteredHandler> iterator = iterator();
		while (iterator.hasNext()) {
			if (iterator.next().isResponseBehaviorApplicable() == false) {
				return false;
			}
		}
		return true;
	}

}
