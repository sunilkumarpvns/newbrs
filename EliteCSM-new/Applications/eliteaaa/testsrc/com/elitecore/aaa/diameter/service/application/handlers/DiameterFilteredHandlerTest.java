package com.elitecore.aaa.diameter.service.application.handlers;


import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static com.elitecore.core.serverx.sessionx.inmemory.ISession.NO_SESSION;
import static org.hamcrest.CoreMatchers.sameInstance;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class DiameterFilteredHandlerTest {
	
	private static final String VALID_RULESET = "0:1=\"expectedUser\"";
	private static final String INVALID_RULESET = "0:1=";

	
	private DiameterRequest diameterRequest;
	private ApplicationRequest request;
	private ApplicationResponse response;
	private DiameterFilteredHandler filteredHandler;
	
	@Mock private DiameterAsyncApplicationHandler<ApplicationRequest, ApplicationResponse> handler;
	
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	@BeforeClass
	public static void setupBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		diameterRequest = new DiameterRequest();
		request = new ApplicationRequest(diameterRequest);
		response = new ApplicationResponse(diameterRequest);
	}
	
	@Test
	public void handlerBeingWrappedCanNotBeNull() {
		
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("handler is null");
		
		filteredHandler = createFilterHandlerWith(VALID_RULESET, null);
	}
	
	@Test
	public void failsToInitializeIfRulesetIsInvalid() throws InitializationFailedException {
		filteredHandler = createFilterHandlerWith(INVALID_RULESET, handler);
		expectedException.expect(InitializationFailedException.class);
		filteredHandler.init();
	}
	
	@Test
	public void failsToInitializeIfWrappedHandlerFailsToInitialize() throws InitializationFailedException {
		filteredHandler = createFilterHandlerWith(VALID_RULESET, handler);
		InitializationFailedException exception = new InitializationFailedException("Some error");
		doThrow(exception).when(handler).init();
		
		expectedException.expect(sameInstance(exception));
		filteredHandler.init();
	}
	
	@Test
	public void responseBehaviorIsApplicableIfResponseBehaviorOfWrappedHandlerIsApplicable() {
		filteredHandler = createFilterHandlerWith(VALID_RULESET, handler);
		
		when(handler.isResponseBehaviorApplicable()).thenReturn(true);
		
		assertTrue(filteredHandler.isResponseBehaviorApplicable());
	}
	
	public class Eligibility {
		
		@Test
		public void isNotEligibleWhenWrappedHandlerIsNotEligible() throws InitializationFailedException {
			when(handler.isEligible(request, response)).thenReturn(false);
			
			request.getDiameterRequest().addAvp(DiameterAVPConstants.USER_NAME, "expectedUser");
			
			filteredHandler = createFilterHandlerWith(VALID_RULESET, handler);
			
			filteredHandler.init();
			
			assertFalse(filteredHandler.isEligible(request, response));
		}
		
		public class WrappedHandlerIsEligibleToBeApplied {
			
			@Before
			public void setup() {
				when(handler.isEligible(request, response)).thenReturn(true);
			}
			
			@Test
			public void isEligibleTobeAppliedWhenRulesetSatisfies() throws InitializationFailedException {
				request.getDiameterRequest().addAvp(DiameterAVPConstants.USER_NAME, "expectedUser");
				
				filteredHandler = createFilterHandlerWith(VALID_RULESET, handler);
				
				filteredHandler.init();
				
				assertTrue(filteredHandler.isEligible(request, response));
			}
			
			
			@Test 
			public void isEligibleIfRulesetIsEmpty() throws InitializationFailedException {
				
				filteredHandler = createFilterHandlerWith("", handler);
				
				filteredHandler.init();
				
				assertTrue(filteredHandler.isEligible(request, response));
			}
			
			@Test 
			public void isEligibleIfRulesetIsNull() throws InitializationFailedException {
				
				filteredHandler = createFilterHandlerWith(null, handler);
				
				filteredHandler.init();
				
				assertTrue(filteredHandler.isEligible(request, response));
			}
			
			@Test
			public void isNotEligibleWhenRulesetIsNotSatisfied() throws InitializationFailedException {
				request.getDiameterRequest().addAvp(DiameterAVPConstants.USER_NAME, "someOtherUser");
				
				filteredHandler = createFilterHandlerWith(VALID_RULESET, handler);
				
				filteredHandler.init();
				
				assertFalse(filteredHandler.isEligible(request, response));
			}
		}
	}
	
	@Test
	public void callsWrappedHandlerToHandleRequest() {
		filteredHandler = createFilterHandlerWith(VALID_RULESET, handler);
		
		filteredHandler.handleRequest(request, response, NO_SESSION);
		verify(handler).handleRequest(request, response, NO_SESSION);
		
		filteredHandler.handleAsyncRequest(request, response, NO_SESSION, null);
		verify(handler).handleRequest(request, response, NO_SESSION);
	}
	
	private DiameterFilteredHandler createFilterHandlerWith(String ruleset,
			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler) {
		return new DiameterFilteredHandler(ruleset, handler);
	}
}
