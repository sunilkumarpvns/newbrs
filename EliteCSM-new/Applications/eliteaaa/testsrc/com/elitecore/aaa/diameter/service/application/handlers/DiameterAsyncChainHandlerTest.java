package com.elitecore.aaa.diameter.service.application.handlers;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

/**
 *
 * @author vicky.singh
 * @author narendra.pathai
 *
 */
public class DiameterAsyncChainHandlerTest {

	private ApplicationRequest request;
	private ApplicationResponse response;
	private DiameterAsyncChainHandler chainHandler;
	private DiameterBroadcastResponseListener fakeListener;

	@Before
	public void setUp() {
		chainHandler = spy(new DiameterAsyncChainHandler());
		DiameterRequest diameterRequest = new DiameterRequest();
		request = new ApplicationRequest(diameterRequest);
		response = new ApplicationResponse(diameterRequest);
		fakeListener = mock(DiameterBroadcastResponseListener.class);
		request.setExecutor(new RadiusRequestExecutor<ApplicationRequest, ApplicationResponse>(chainHandler, request, response));
	}

	@Test
	public void callsOnlyEligibleHandlersInOrderOfAddition() {
		DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> eligibleHandler1 = aHandler().eligible().buildSpied();
		chainHandler.addHandler(eligibleHandler1);
		DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> nonEligibleHandler1 = aHandler().nonEligible().buildSpied();
		chainHandler.addHandler(nonEligibleHandler1);
		DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> eligibleHandler2 = aHandler().eligible().buildSpied();
		chainHandler.addHandler(eligibleHandler2);
		DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> nonEligibleHandler2 = aHandler().nonEligible().buildSpied();
		chainHandler.addHandler(nonEligibleHandler2);

		chainHandler.handleAsyncRequest(request, response, null, fakeListener);

		InOrder order = inOrder(eligibleHandler1, eligibleHandler2);
		order.verify(eligibleHandler1).handleAsyncRequest(request, response, null, fakeListener);
		order.verify(eligibleHandler2).handleAsyncRequest(request, response, null, fakeListener);

		verify(nonEligibleHandler1, never()).handleAsyncRequest(request, response, null, fakeListener);
		verify(nonEligibleHandler2, never()).handleAsyncRequest(request, response, null, fakeListener);
	}

	@Test
	public void completesProcessingOfAllHandlersEvenIfAnyHandlerSuspendsProcessing() {		
		DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> nonSuspendingHandler1 = aHandler().eligible().nonSuspending().buildSpied();
		chainHandler.addHandler(nonSuspendingHandler1);
		DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> suspendingHandler1 = aHandler().eligible().suspending().buildSpied();
		chainHandler.addHandler(suspendingHandler1);
		DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> nonSuspendingHandler2 = aHandler().eligible().nonSuspending().buildSpied();
		chainHandler.addHandler(nonSuspendingHandler2);
		DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> suspendingHandler2 = aHandler().eligible().suspending().buildSpied();
		chainHandler.addHandler(suspendingHandler2);

		chainHandler.handleAsyncRequest(request, response, null, fakeListener);

		verify(nonSuspendingHandler1).handleAsyncRequest(request, response, null, fakeListener);
		verify(suspendingHandler1).handleAsyncRequest(request, response, null, fakeListener);
		verify(nonSuspendingHandler2).handleAsyncRequest(request, response, null, fakeListener);
		verify(suspendingHandler2).handleAsyncRequest(request, response, null, fakeListener);
	}

	@Test
	public void givenAChainWithAnEligibleHandlerProcessOnNoHandlerEligibleIsNotCalled() {
		chainWith(aHandler().eligible());

		chainHandler.handleAsyncRequest(request, response, null, fakeListener);

		Mockito.verify(chainHandler, Mockito.never()).processOnNoHandlerEligible(request, response);
	}

	@Test 
	public void processOnNoHandlerEligibleIsCalledIfNoHandlerInTheChainIsEligible () {
		chainWith(aHandler().nonEligible(), aHandler().nonEligible());

		chainHandler.handleAsyncRequest(request, response, null, fakeListener);

		Mockito.verify(chainHandler).processOnNoHandlerEligible(request, response);
	}

	@Test
	public void processOnNoHandlerEligibleIsCalledIfChainHasNoHandlers() {
		chainWith(noHandlers());

		chainHandler.handleAsyncRequest(request, response, null, fakeListener);

		Mockito.verify(chainHandler).processOnNoHandlerEligible(request, response);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void resumptionOfAsyncChainIsUnsupported() {
		chainWith(aHandler().eligible(), aHandler().nonEligible());
		
		chainHandler.resumeRequest(request, response, null);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void syncExecutionOfAsyncChainIsUnsupported() {
		chainWith(aHandler().eligible(), aHandler().nonEligible());
		
		chainHandler.handleRequest(request, response, null);
	}

	private DiameterFakeAsyncHandlerBuilder[] noHandlers() {
		return new DiameterFakeAsyncHandlerBuilder[] {};
	}

	private void chainWith(DiameterFakeAsyncHandlerBuilder... handlers) {
		for (DiameterFakeAsyncHandlerBuilder handler : handlers) {
			chainHandler.addHandler(handler.build());
		}
	}

	private DiameterFakeAsyncHandlerBuilder aHandler() {
		return new DiameterFakeAsyncHandlerBuilder();
	}

	public static class DiameterFakeAsyncHandlerBuilder {

		private FakeAsyncHandler fakeHandler = new FakeAsyncHandler();

		public DiameterFakeAsyncHandlerBuilder eligible() {
			fakeHandler.isEligible = true;
			return this;
		}

		public DiameterFakeAsyncHandlerBuilder suspending() {
			fakeHandler.isSuspending = true;
			return this;
		}

		public DiameterFakeAsyncHandlerBuilder nonEligible() {
			fakeHandler.isEligible = false;
			return this;
		}

		public DiameterFakeAsyncHandlerBuilder nonSuspending() {
			fakeHandler.isSuspending = false;
			return this;
		}

		public FakeAsyncHandler build() {
			return fakeHandler;
		}

		public FakeAsyncHandler buildSpied() {
			return spy(fakeHandler);
		}
	}

	public static class FakeAsyncHandler implements DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> {
		public boolean isSuspending;
		public boolean isEligible;

		@Override
		public void init() throws InitializationFailedException {

		}

		@Override
		public boolean isEligible(ApplicationRequest request, ApplicationResponse response) {
			return isEligible;
		}

		@Override
		public void handleRequest(ApplicationRequest request, ApplicationResponse response, ISession session) {
			fail("Should never be invoked");
		}

		@Override
		public boolean isResponseBehaviorApplicable() {
			return false;
		}

		@Override
		public void reInit() throws InitializationFailedException {

		}

		@Override
		public void handleAsyncRequest(ApplicationRequest request, ApplicationResponse response,
				ISession session, DiameterBroadcastResponseListener listener) {
			if (isSuspending) {
				DiameterProcessHelper.onExternalCommunication(request, response);
			}
		}
	}
}