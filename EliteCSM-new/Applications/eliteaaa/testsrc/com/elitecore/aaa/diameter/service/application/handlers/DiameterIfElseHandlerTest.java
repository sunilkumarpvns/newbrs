package com.elitecore.aaa.diameter.service.application.handlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

public class DiameterIfElseHandlerTest {
	
	private static final String ANY_RULESET = "";
	
	private FakeApplicationHandler[] handlers = new FakeApplicationHandler[] {
			aHandler(),
			aHandler(),
			aHandler()
	};
	
	private DiameterIfElseHandler ifElseHandler = new DiameterIfElseHandler();
	
	@Before
	public void setUp() {
		for (FakeApplicationHandler handler : handlers) {
			ifElseHandler.addHandler(new DiameterFilteredHandler(ANY_RULESET, handler));
		}
	}
	
	@Test
	public void defaultResponseBehaviorIsNotApplicableUnlessAllHandlersConfirmTheSame() {
		responseBehaviorApplicableFor(aRandomHandler());
		
		assertFalse(ifElseHandler.isResponseBehaviorApplicable());
	}
	
	@Test
	public void defaultResponseBehaviorIsApplicableIfAllHandlersConfirmTheSame() {
		responseBehaviorApplicableFor(handlers);
		
		assertTrue(ifElseHandler.isResponseBehaviorApplicable());
	}

	private void responseBehaviorApplicableFor(FakeApplicationHandler... handlers) {
		for (FakeApplicationHandler handler : handlers) {
			handler.responseBehaviorApplicable = true;
		}
	}

	private static FakeApplicationHandler aHandler() {
		return new FakeApplicationHandler();
	}

	private FakeApplicationHandler aRandomHandler() {
		return handlers[new Random().nextInt(handlers.length)];
	}
	
	private static class FakeApplicationHandler implements DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> {
		public boolean responseBehaviorApplicable = false;
		
		@Override
		public void init() throws InitializationFailedException {
			
		}

		@Override
		public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
			return true;
		}

		@Override
		public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
			
		}

		@Override
		public boolean isResponseBehaviorApplicable() {
			return responseBehaviorApplicable;
		}

		@Override
		public void reInit() throws InitializationFailedException {
			
		}
	}
}
