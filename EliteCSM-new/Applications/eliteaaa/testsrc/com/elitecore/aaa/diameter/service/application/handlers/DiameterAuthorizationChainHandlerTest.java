package com.elitecore.aaa.diameter.service.application.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.SubscriberProfileRepositoryAware;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepository;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

public class DiameterAuthorizationChainHandlerTest {

	@Mock private DiameterSubscriberProfileRepository spr;
	private DiameterAuthorizationChainHandler authorizationChainHandler  = new DiameterAuthorizationChainHandler();
	private SPRAwareHandler handler1, handler2;
	private SPRUnAwareHandler handler3, handler4;

	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}


	@Test 
	public void setsSubsciberProfileRepositoryToTheHandlersWhichAreAwareOfSubscriberProbfileRepository() { 
		createChainHandler();
		
		authorizationChainHandler.setSubscriberProfileRepository(spr);

		Mockito.verify(handler1,Mockito.times(1)).setSubscriberProfileRepository(spr);
		Mockito.verify(handler2,Mockito.times(1)).setSubscriberProfileRepository(spr);
		Mockito.verifyZeroInteractions(handler3, handler4);
	}


	private void createChainHandler() {
		handler1 = 	Mockito.spy(new SPRAwareHandler());
		handler2 =  Mockito.spy(new SPRAwareHandler());
		handler3 =  Mockito.spy(new SPRUnAwareHandler());
		handler4 =  Mockito.spy(new SPRUnAwareHandler());
		authorizationChainHandler.addHandler(handler1);
		authorizationChainHandler.addHandler(handler2);
		authorizationChainHandler.addHandler(handler3);
		authorizationChainHandler.addHandler(handler4);
	}

	private class SPRAwareHandler implements SubscriberProfileRepositoryAware, DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> {

		@Override
		public void setSubscriberProfileRepository(
				DiameterSubscriberProfileRepository spr) {
		}

		@Override
		public void init() throws InitializationFailedException {
		}

		@Override
		public boolean isEligible(ApplicationRequest request,
				ApplicationResponse response) {
			return true;
		}

		@Override
		public void handleRequest(ApplicationRequest request,
				ApplicationResponse response, ISession session) {
		}

		@Override
		public boolean isResponseBehaviorApplicable() {
			return false;
		}

		@Override
		public void reInit() throws InitializationFailedException {
		}
	}

	private class SPRUnAwareHandler implements DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> {

		@Override
		public void init() throws InitializationFailedException {
		}

		@Override
		public boolean isEligible(ApplicationRequest request,
				ApplicationResponse response) {
			return true;
		}

		@Override
		public void handleRequest(ApplicationRequest request,
				ApplicationResponse response, ISession session) {
		}

		@Override
		public boolean isResponseBehaviorApplicable() {
			return false;
		}

		@Override
		public void reInit() throws InitializationFailedException {
		}
	}

}
