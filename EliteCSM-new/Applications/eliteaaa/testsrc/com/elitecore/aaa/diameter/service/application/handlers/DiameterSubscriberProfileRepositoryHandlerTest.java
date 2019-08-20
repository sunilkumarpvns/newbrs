package com.elitecore.aaa.diameter.service.application.handlers;


import static com.elitecore.core.serverx.sessionx.inmemory.ISession.NO_SESSION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepository;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class DiameterSubscriberProfileRepositoryHandlerTest {

	private DiameterRequest diameterRequest;
	private ApplicationRequest request;
	private ApplicationResponse response;
	private DiameterSubscriberProfileRepositoryHandler handler;
	
	@Mock private DiameterSubscriberProfileRepository spr;
	@Mock private AccountData accountData;
	@Mock private AccountData anonymusAccountData;
	
	@Before
	public void setup() throws InitializationFailedException {
		MockitoAnnotations.initMocks(this);
		diameterRequest = new DiameterRequest();
		request = new ApplicationRequest(diameterRequest);
		response = new ApplicationResponse(diameterRequest);
		
		handler = new DiameterSubscriberProfileRepositoryHandler();
		handler.setSubscriberProfileRepository(spr);
		handler.init();
	}
	
	
	@Test
	public void isAlwaysEligible() {
		assertTrue(handler.isEligible(request, response));
	}

	@Test
	public void requestIsEligibleForDefaultResponseBehaviorIfSprIsNotAlive() {
		when(spr.isAlive()).thenReturn(false);
		
		assertTrue(handler.isResponseBehaviorApplicable());
	}
	
	@Test
	public void requestIsNotEligibleForDefaultResponseBehaviorIfSprIsAlive() {
		when(spr.isAlive()).thenReturn(true);
		
		assertFalse(handler.isResponseBehaviorApplicable());
	}

	public class AccountDataIsPresentInRequest {
		
		@Before
		public void setup() {
			request.setAccountData(accountData);
		}
		
		@Test
		public void sprIsNotAccessedAtAll() {
			
			handler.handleRequest(request, response, NO_SESSION);
			
			verifyZeroInteractions(spr);
		}
	}
	
	public class AccountDataIsNotPresentInRequest {
		
		private static final String ANON_PROFILE_NAME = "anonUser";
		private static final String USER_NAME = "someName";

		@Before
		public void setup() {
			request.setAccountData(null);
		}
		
		@Test
		public void fetchesAndSetsAccountDataFromSPR() {
			when(spr.getAccountData(request, response)).thenReturn(accountData);
			when(accountData.getUserName()).thenReturn(USER_NAME);
			
			handler.handleRequest(request, response, NO_SESSION);
			
			verify(spr).getAccountData(request, response);
			assertEquals(accountData, request.getAccountData());
			assertEquals(accountData.getUserName(), request.getAccountData().getUserName());
		}
		
		@Test
		public void fetchesAndSetsAnonymousProfileDataGivenAccountDataIsNotFoundUsingSpr() {
			when(spr.getAccountData(request, response)).thenReturn(null);
			when(spr.getAnonymousAccountData(request)).thenReturn(anonymusAccountData);
			when(anonymusAccountData.getUserName()).thenReturn(ANON_PROFILE_NAME);
			
			handler.handleRequest(request, response, NO_SESSION);
			
			verify(spr).getAccountData(request, response);
			verify(spr).getAnonymousAccountData(request);
			assertEquals(anonymusAccountData.getUserName(), request.getAccountData().getUserName());
		}
		
		@Test
		public void accountDataIsNotSetIfAccountDataCanNotBeLocatedUsingSpr() {
			when(spr.getAccountData(request, response)).thenReturn(null);
			when(spr.getAnonymousAccountData(request)).thenReturn(null);
			
			handler.handleRequest(request, response, NO_SESSION);
			
			verify(spr).getAccountData(request, response);
			verify(spr).getAnonymousAccountData(request);
			assertNull(request.getAccountData());
		}
	}
}
