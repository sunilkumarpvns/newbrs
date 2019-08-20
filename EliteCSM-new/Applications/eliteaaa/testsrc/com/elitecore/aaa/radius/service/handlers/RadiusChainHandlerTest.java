package com.elitecore.aaa.radius.service.handlers;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService.RadiusAuthRequestImpl;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService.RadiusAuthResponseImpl;
import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.util.url.SocketDetail;

/**
 * 
 * @author narendra.pathai
 *
 */
public class RadiusChainHandlerTest {

	private RadAuthRequest request;
	private RadAuthResponse response;
	private RadiusChainHandler<RadAuthRequest, RadAuthResponse, RadServiceHandler<RadAuthRequest, RadAuthResponse>> chainHandler;

	@Before
	public void setUp() throws UnknownHostException {
		chainHandler = new RadiusChainHandler<RadAuthRequest, RadAuthResponse, RadServiceHandler<RadAuthRequest, RadAuthResponse>>() {

			@Override
			public void init() throws InitializationFailedException {
				
			}

			@Override
			public void reInit() throws InitializationFailedException {
				
			}

			@Override
			protected RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> getExecutor(
					RadAuthRequest request) {
				return request.getExecutor();
			}
			
		};
		
		request = new RadiusAuthRequestImpl(new byte[] {}, InetAddress.getLocalHost(), 0, null, new SocketDetail(InetAddress.getLocalHost().getHostAddress(), 0));
		response = new RadiusAuthResponseImpl(request.getAuthenticator(), 0, null);
		request.setExecutor(new RadiusRequestExecutor<RadAuthRequest, RadAuthResponse>(chainHandler, request, response));
		
	}

	@Test
	public void callsOnlyEligibleHandlersInOrderOfAddition() {
		RadServiceHandler<RadAuthRequest, RadAuthResponse> eligibleHandler1 = aHandler().eligible().buildSpied();
		chainHandler.addHandler(eligibleHandler1);
		RadServiceHandler<RadAuthRequest, RadAuthResponse> nonEligibleHandler1 = aHandler().nonEligible().buildSpied();
		chainHandler.addHandler(nonEligibleHandler1);
		RadServiceHandler<RadAuthRequest, RadAuthResponse> eligibleHandler2 = aHandler().eligible().buildSpied();
		chainHandler.addHandler(eligibleHandler2);
		RadServiceHandler<RadAuthRequest, RadAuthResponse> nonEligibleHandler2 = aHandler().nonEligible().buildSpied();
		chainHandler.addHandler(nonEligibleHandler2);

		chainHandler.handleRequest(request, response, ISession.NO_SESSION);

		InOrder order = inOrder(eligibleHandler1, eligibleHandler2);
		order.verify(eligibleHandler1).handleRequest(request, response, ISession.NO_SESSION);
		order.verify(eligibleHandler2).handleRequest(request, response, ISession.NO_SESSION);

		verify(nonEligibleHandler1, never()).handleRequest(request, response, ISession.NO_SESSION);
		verify(nonEligibleHandler2, never()).handleRequest(request, response, ISession.NO_SESSION);
	}

	@Test
	public void skipsProcessingOfSubsequentHandlersIfAnyHandlerSuspendsProcessing() {		
		RadServiceHandler<RadAuthRequest, RadAuthResponse> nonSuspendingHandler1 = aHandler().eligible().nonSuspending().buildSpied();
		chainHandler.addHandler(nonSuspendingHandler1);
		RadServiceHandler<RadAuthRequest, RadAuthResponse> suspendingHandler1 = aHandler().eligible().suspending().buildSpied();
		chainHandler.addHandler(suspendingHandler1);
		RadServiceHandler<RadAuthRequest, RadAuthResponse> nonSuspendingHandler2 = aHandler().eligible().nonSuspending().buildSpied();
		chainHandler.addHandler(nonSuspendingHandler2);
		RadServiceHandler<RadAuthRequest, RadAuthResponse> suspendingHandler2 = aHandler().eligible().suspending().buildSpied();
		chainHandler.addHandler(suspendingHandler2);

		chainHandler.handleRequest(request, response, ISession.NO_SESSION);

		verify(nonSuspendingHandler1).handleRequest(request, response, ISession.NO_SESSION);
		verify(suspendingHandler1).handleRequest(request, response, ISession.NO_SESSION);

		verifyZeroInteractions(nonSuspendingHandler2, suspendingHandler2);
	}
	
	@Test
	public void resumesProcessingStartingFromHandlerThatComesAfterTheHandlerThatSuspendedProcessing() {
		RadServiceHandler<RadAuthRequest, RadAuthResponse> nonSuspendingHandler1 = aHandler().eligible().nonSuspending().buildSpied();
		chainHandler.addHandler(nonSuspendingHandler1);
		RadServiceHandler<RadAuthRequest, RadAuthResponse> suspendingHandler1 = aHandler().eligible().suspending().buildSpied();
		chainHandler.addHandler(suspendingHandler1);
		RadServiceHandler<RadAuthRequest, RadAuthResponse> nonSuspendingHandler2 = aHandler().eligible().nonSuspending().buildSpied();
		chainHandler.addHandler(nonSuspendingHandler2);
		RadServiceHandler<RadAuthRequest, RadAuthResponse> suspendingHandler2 = aHandler().eligible().suspending().buildSpied();
		chainHandler.addHandler(suspendingHandler2);
		
		chainHandler.handleRequest(request, response, ISession.NO_SESSION);
		
		response.setFurtherProcessingRequired(true);
		chainHandler.resumeRequest(request, response, ISession.NO_SESSION);

		verify(nonSuspendingHandler1).handleRequest(request, response, ISession.NO_SESSION);
		verify(suspendingHandler1).handleRequest(request, response, ISession.NO_SESSION);
		verify(nonSuspendingHandler2).handleRequest(request, response, ISession.NO_SESSION);
		verify(suspendingHandler2).handleRequest(request, response, ISession.NO_SESSION);
	}

	private RadiusFakeHandlerBuilder aHandler() {
		return new RadiusFakeHandlerBuilder();
	}

	public static class RadiusFakeHandlerBuilder {

		private FakeHandler fakeHandler = new FakeHandler();

		public RadiusFakeHandlerBuilder eligible() {
			fakeHandler.isEligible = true;
			return this;
		}

		public RadiusFakeHandlerBuilder suspending() {
			fakeHandler.isSuspending = true;
			return this;
		}

		public RadiusFakeHandlerBuilder nonEligible() {
			fakeHandler.isEligible = false;
			return this;
		}

		public RadiusFakeHandlerBuilder nonSuspending() {
			fakeHandler.isSuspending = false;
			return this;
		}

		public FakeHandler build() {
			return fakeHandler;
		}

		public FakeHandler buildSpied() {
			return spy(fakeHandler);
		}
	}

	public static class FakeHandler implements RadServiceHandler<RadAuthRequest, RadAuthResponse> {
		public boolean isSuspending;
		public boolean isEligible;

		@Override
		public void init() throws InitializationFailedException {

		}

		@Override
		public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
			return isEligible;
		}

		@Override
		public void handleRequest(RadAuthRequest request, RadAuthResponse response, ISession session) {
			if (isSuspending) {
				RadiusProcessHelper.onExternalCommunication(request, response);
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
