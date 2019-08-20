package com.elitecore.aaa.diameter.service.application.listener;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterAsyncRequestExecutor;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;

/**
 * 
 * @author khushbu.chauhan
 *
 */
public class AAAServerApplicationTest {

	private static final String ANY_SESSION_ID = "SESSION_ID";
	@Rule public ExpectedException exception =  ExpectedException.none();
	private DiameterSession diameterSession ;
	private DiameterRequest diameterRequest ;
	private ApplicationRequest request;
	private ApplicationResponse response;
	@Mock private DiameterAsyncRequestExecutor asyncRequestEcecutor;
	
	/*
	 * Due to the change in sendDiamterAnswer method in Application listener, if stack context is not passed , it throws null pointer exception.
	 * So dummy diameter stack context is passed. Note: sendDiameterAnswer method may throw run time exception.
	 */ 
	
	@Spy private AAAServerApplication aaaServerApplication = new AAAServerApplicationStub(new DummyStackContext(null) {
		
		@Override
		public DiameterPeerCommunicator getPeerCommunicator(String hostIdentity) {
			return null;
		};
	}, null);
	
	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		diameterSession = new DiameterSession(ANY_SESSION_ID, null);
		diameterRequest = new DiameterRequest();
		diameterRequest.setRequestingHost("eliteaaa.elitecore.com");
		request = new ApplicationRequest(diameterRequest);
		response = new ApplicationResponse(diameterRequest);
		
	}
	
	@Test
	public void doesPostResponseProcessingAfterProcessingApplicationRequest() {
		aaaServerApplication.processApplicationRequest(diameterSession, diameterRequest);
		
		verifyPostResponseProcessingExecution();
	}

	
	@Test
	public void doesPostResponseProcessingAfterResumingApplicationRequest() {
		aaaServerApplication.resumeApplicationRequest(diameterSession, request, response , asyncRequestEcecutor);
		
		verifyPostResponseProcessingExecution();
	}
	
	private void verifyPostResponseProcessingExecution() {
		verify(aaaServerApplication, times(1)).postResponseProcessing(Mockito.any(ApplicationRequest.class),
				Mockito.any(ApplicationResponse.class), Mockito.any(ISession.class));
	}
	
	@Test
	public void swallowsExceptionThrownByPostResponseProcessingWhileProcessingApplicationRequest() {
		aaaServerApplication.processApplicationRequest(diameterSession, diameterRequest);
	}

	@Test
	public void swallowsExceptionThrownByPostResponseProcessingWhileResumingApplicationRequest() {
		aaaServerApplication.resumeApplicationRequest(diameterSession, request, response, asyncRequestEcecutor);
	}
	
	private class AAAServerApplicationStub extends AAAServerApplication {
		
		public AAAServerApplicationStub(IStackContext stackContext,
				DiameterServiceContext serviceContext) {
			super(stackContext, serviceContext);
		}
		
		protected void postResponseProcessing(ApplicationRequest appRequest, ApplicationResponse appResponse)  {
			throw new RuntimeException("Exception in some handler");
		} 
		
		@Override
		protected void handleApplicationRequest(DiameterSession session,
				ApplicationRequest applicationRequest,
				ApplicationResponse applicationResponse) {
		}
		
		@Override
		protected void resumeApplicationRequest(DiameterSession session,
				ApplicationRequest request, ApplicationResponse response) {
		}
		
		@Override
		public String getApplicationIdentifier() {
			return null;
		}
		
		@Override
		protected @Nullable SessionReleaseIndiactor createSessionReleaseIndicator(
				@Nonnull ApplicationEnum applicationEnum) {
			return null;
		}

		@Override
		protected void finalPreResponseProcessing(ApplicationRequest applicationRequest,
				ApplicationResponse applicationResponse, ISession session) {
		}
	}
}

