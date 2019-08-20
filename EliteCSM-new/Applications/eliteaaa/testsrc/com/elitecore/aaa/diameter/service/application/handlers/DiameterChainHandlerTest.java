package com.elitecore.aaa.diameter.service.application.handlers;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

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
public class DiameterChainHandlerTest {

	private ApplicationRequest request;
	private ApplicationResponse response;
	private DiameterChainHandler<DiameterApplicationHandler<ApplicationRequest,ApplicationResponse>> chainHandler;

	@Before
	public void setUp() {
		chainHandler = spy(new DiameterChainHandler<DiameterApplicationHandler<ApplicationRequest,ApplicationResponse>>());
		DiameterRequest diameterRequest = new DiameterRequest();
		request = new ApplicationRequest(diameterRequest);
		response = new ApplicationResponse(diameterRequest);

		request.setExecutor(new RadiusRequestExecutor<ApplicationRequest, ApplicationResponse>(chainHandler, request, response));
	}

	@Test
	public void callsOnlyEligibleHandlersInOrderOfAddition() {
		DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> eligibleHandler1 = aHandler().eligible().buildSpied();
		chainHandler.addHandler(eligibleHandler1);
		DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> nonEligibleHandler1 = aHandler().nonEligible().buildSpied();
		chainHandler.addHandler(nonEligibleHandler1);
		DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> eligibleHandler2 = aHandler().eligible().buildSpied();
		chainHandler.addHandler(eligibleHandler2);
		DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> nonEligibleHandler2 = aHandler().nonEligible().buildSpied();
		chainHandler.addHandler(nonEligibleHandler2);

		chainHandler.handleRequest(request, response, null);

		InOrder order = inOrder(eligibleHandler1, eligibleHandler2);
		order.verify(eligibleHandler1).handleRequest(request, response, null);
		order.verify(eligibleHandler2).handleRequest(request, response, null);

		verify(nonEligibleHandler1, never()).handleRequest(request, response, null);
		verify(nonEligibleHandler2, never()).handleRequest(request, response, null);
	}

	@Test
	public void skipsProcessingOfSubsequentHandlersIfAnyHandlerSuspendsProcessing() {		
		DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> nonSuspendingHandler1 = aHandler().eligible().nonSuspending().buildSpied();
		chainHandler.addHandler(nonSuspendingHandler1);
		DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> suspendingHandler1 = aHandler().eligible().suspending().buildSpied();
		chainHandler.addHandler(suspendingHandler1);
		DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> nonSuspendingHandler2 = aHandler().eligible().nonSuspending().buildSpied();
		chainHandler.addHandler(nonSuspendingHandler2);
		DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> suspendingHandler2 = aHandler().eligible().suspending().buildSpied();
		chainHandler.addHandler(suspendingHandler2);

		chainHandler.handleRequest(request, response, null);

		verify(nonSuspendingHandler1).handleRequest(request, response, null);
		verify(suspendingHandler1).handleRequest(request, response, null);

		verifyZeroInteractions(nonSuspendingHandler2, suspendingHandler2);
	}
	
	@Test
	public void resumesProcessingStartingFromTheHandlerThatComesAfterTheOneThatSuspendedProcessing() {
		DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> nonSuspendingHandler1 = aHandler().eligible().nonSuspending().buildSpied();
		chainHandler.addHandler(nonSuspendingHandler1);
		DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> suspendingHandler1 = aHandler().eligible().suspending().buildSpied();
		chainHandler.addHandler(suspendingHandler1);
		DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> nonSuspendingHandler2 = aHandler().eligible().nonSuspending().buildSpied();
		chainHandler.addHandler(nonSuspendingHandler2);
		DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> suspendingHandler2 = aHandler().eligible().suspending().buildSpied();
		chainHandler.addHandler(suspendingHandler2);
		
		chainHandler.handleRequest(request, response, null);
		
		response.setFurtherProcessingRequired(true);
		chainHandler.resumeRequest(request, response, null);

		verify(nonSuspendingHandler1).handleRequest(request, response, null);
		verify(suspendingHandler1).handleRequest(request, response, null);
		verify(nonSuspendingHandler2).handleRequest(request, response, null);
		verify(suspendingHandler2).handleRequest(request, response, null);
	}
	
	@Test
	public void givenAChainWithEligibleExternalCommunicationHandlerOnResumingRequestExecutionProcessOnNoHandlerEligibleIsNotCalled() {
		chainWith(aHandler().eligible().suspending(), aHandler().nonEligible());
		
		chainHandler.handleRequest(request, response, null);
		
		response.setFurtherProcessingRequired(true);
		chainHandler.resumeRequest(request, response, null);
		
		Mockito.verify(chainHandler, Mockito.never()).processOnNoHandlerEligible(request, response);
	}
	
	@Test
	public void givenAChainWithAnEligibleSingleExternalCommunicationHandlerOnResumingRequestExecutionProcessOnNoHandlerEligibleIsNotCalled() {
		chainWith(aHandler().eligible().suspending());
		
		chainHandler.handleRequest(request, response, null);
		
		response.setFurtherProcessingRequired(true);
		chainHandler.resumeRequest(request, response, null);
		
		Mockito.verify(chainHandler, Mockito.never()).processOnNoHandlerEligible(request, response);
	}
	
	@Test
	public void givenAChainWithAnEligibleHandlerProcessOnNoHandlerEligibleIsNotCalled() {
		chainWith(aHandler().eligible());
		
		chainHandler.handleRequest(request, response, null);
		
		Mockito.verify(chainHandler, Mockito.never()).processOnNoHandlerEligible(request, response);
	}
	
	@Test 
	public void processOnNoHandlerEligibleIsCalledIfNoHandlerInTheChainIsEligible () {
		chainWith(aHandler().nonEligible(), aHandler().nonEligible());
		
		chainHandler.handleRequest(request, response, null);
		
		Mockito.verify(chainHandler).processOnNoHandlerEligible(request, response);
	}
	
	@Test
	public void processOnNoHandlerEligibleIsCalledIfChainHasNoHandlers() {
		chainWith(noHandlers());

		chainHandler.handleRequest(request, response, null);
		
		Mockito.verify(chainHandler).processOnNoHandlerEligible(request, response);
	}

	private DiameterFakeHandlerBuilder[] noHandlers() {
		return new DiameterFakeHandlerBuilder[] {};
	}

	private void chainWith(DiameterFakeHandlerBuilder... handlers) {
		for (DiameterFakeHandlerBuilder handler : handlers) {
			chainHandler.addHandler(handler.build());
		}
	}

	private DiameterFakeHandlerBuilder aHandler() {
		return new DiameterFakeHandlerBuilder();
	}

	public static class DiameterFakeHandlerBuilder {

		private FakeApplication fakeApplication = new FakeApplication();

		public DiameterFakeHandlerBuilder eligible() {
			fakeApplication.isEligible = true;
			return this;
		}

		public DiameterFakeHandlerBuilder suspending() {
			fakeApplication.isSuspending = true;
			return this;
		}

		public DiameterFakeHandlerBuilder nonEligible() {
			fakeApplication.isEligible = false;
			return this;
		}

		public DiameterFakeHandlerBuilder nonSuspending() {
			fakeApplication.isSuspending = false;
			return this;
		}

		public FakeApplication build() {
			return fakeApplication;
		}

		public FakeApplication buildSpied() {
			return spy(fakeApplication);
		}
	}

	public static class FakeApplication implements DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> {
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
			if (isSuspending) {
				DiameterProcessHelper.onExternalCommunication(request, response);
			}
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