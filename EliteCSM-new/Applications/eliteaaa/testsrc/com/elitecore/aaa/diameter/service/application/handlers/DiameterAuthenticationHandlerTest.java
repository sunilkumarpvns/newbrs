package com.elitecore.aaa.diameter.service.application.handlers;


import static com.elitecore.diameterapi.diameter.DiameterMatchers.DiameterResponseMatcher.containsAttribute;
import static com.elitecore.diameterapi.diameter.DiameterMatchers.PacketMatchers.hasResultCode;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.authprotocol.exception.InvalidPasswordException;
import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.constant.AuthMethods;
import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthenticationHandlerData;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepository;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepositoryDetails;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class DiameterAuthenticationHandlerTest {

	private static final String TEST_USER = "Test";
	private DiameterAuthenticationHandlerData data;
	private DiameterRequest diameterRequest;
	private ApplicationRequest request;
	private ApplicationResponse response;
	private DiameterAuthenticationHandlerStub authenticationHandler;

	@Mock private DiameterAuthMethodHandler papHandler;
	@Mock private DiameterAuthMethodHandler chapHandler;
	@Mock private DiameterAuthMethodHandler eapHandler;
	@Mock private DiameterServiceContext diameterServiceContext;
	@Mock private AAAServerConfiguration aaaServerConfiguration;
	@Mock private DiameterSubscriberProfileRepository accountInfoProvider;
	
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();

	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		data = new DiameterAuthenticationHandlerData();
		data.setPolicyName("tmpPolicy");
		data.setSubscriberProfileRepositoryDetails(new DiameterSubscriberProfileRepositoryDetails());
		createRequestAndResponse();
		createHandler(data);
	}

	@Test
	public void isAlwaysEligible() throws Exception {
		assertTrue(authenticationHandler.isEligible(request, response));
	}

	@Test
	public void resultCodeIsDiameterUnableToComplyWhenUnsupportedAuthenticationMethodConfigured() throws Exception {
		data.getAutheMethodHandlerTypes().add(AuthMethods.PAP);
		createHandler(data);

		when(papHandler.isEligible(request)).thenReturn(false);

		authenticationHandler.handleRequest(request, response, null);

		assertThat(response.getDiameterAnswer(), hasResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY));
	}

	@Test
	public void resultCodeIsDiameterSuccessWhenRequestContainSessionTerminationCommandCode() throws Exception {
		data.setSubscriberProfileRepositoryDetails(new DiameterSubscriberProfileRepositoryDetails());
		data.getAutheMethodHandlerTypes().add(AuthMethods.PAP);
		createHandler(data);

		diameterRequest.setCommandCode(CommandCode.SESSION_TERMINATION.getCode());

		authenticationHandler.handleRequest(request, response, null);

		assertThat(response.getDiameterAnswer(), hasResultCode(ResultCode.DIAMETER_SUCCESS));
	}

	@Test
	public void hintsSystemToSkipFurtherProcessingOnSessionTerminationRequest () throws Exception {
		data.setSubscriberProfileRepositoryDetails(new DiameterSubscriberProfileRepositoryDetails());
		data.getAutheMethodHandlerTypes().add(AuthMethods.PAP);
		createHandler(data);

		diameterRequest.setCommandCode(CommandCode.SESSION_TERMINATION.getCode());

		authenticationHandler.handleRequest(request, response, null);

		assertFalse(response.isFurtherProcessingRequired());
	}

	@Test
	public void updatesResultCodeToDiameterAuthenticationRejectedWhenInvalidPasswordExceptionIsThrownByMethod() throws Exception {
		data.getAutheMethodHandlerTypes().add(AuthMethods.PAP);
		createHandler(data);
		
		when(papHandler.isEligible(request)).thenReturn(true);
		doThrow(InvalidPasswordException.class).when(papHandler).handleRequest(request, response, accountInfoProvider);

		authenticationHandler.handleRequest(request, response, null);

		assertThat(response.getDiameterAnswer(), hasResultCode(ResultCode.DIAMETER_AUTHENTICATION_REJECTED));
	}

	@Test
	public void updatesResultCodeToDiameterUserUnknownWhenAuthenticationFailedExceptionIsThrown() throws Exception  {
		data.getAutheMethodHandlerTypes().add(AuthMethods.PAP);
		createHandler(data);
		
		when(papHandler.isEligible(request)).thenReturn(true);
		doThrow(AuthenticationFailedException.class).when(papHandler).handleRequest(request, response, accountInfoProvider);

		authenticationHandler.handleRequest(request, response, null);

		assertThat(response.getDiameterAnswer(), hasResultCode(ResultCode.DIAMETER_USER_UNKNOWN));
	}

	@Test
	public void defaultResponseBehaviorIsApplicableIfSprIsDead() throws Exception {
		data.getAutheMethodHandlerTypes().add(AuthMethods.PAP);
		createHandler(data);

		assertTrue(authenticationHandler.isResponseBehaviorApplicable());
	}

	@Test
	public void defaultResponseBehaviorIsNotApplicableIfSprIsAlive() throws Exception {
		data.getAutheMethodHandlerTypes().add(AuthMethods.PAP);
		createHandler(data);

		when(accountInfoProvider.isAlive()).thenReturn(true);

		assertFalse(authenticationHandler.isResponseBehaviorApplicable());
	}

	public class MethodHandlerCreation {

		private DiameterAuthenticationHandler authenticationHandler;

		@Test
		public void createsAppropriateHandlerForPAP () throws Exception {
			data.getSubscriberProfileRepositoryDetails().getUpdateIdentity().setIsTrimPassword(false);
			authenticationHandler = new DiameterAuthenticationHandler(diameterServiceContext, data);

			assertThat(authenticationHandler.createHandler(AuthMethods.PAP), 
					is(instanceOf(DiameterPAPMethodHandler.class)));
		}

		@Test
		public void createsAppropriateHandlerForCHAP () throws Exception {
			authenticationHandler = new DiameterAuthenticationHandler(diameterServiceContext, data);

			assertThat(authenticationHandler.createHandler(AuthMethods.CHAP), 
					is(instanceOf(DiameterCHAPMethodHandler.class)));
		}

		@Test
		public void createsAppropriateHandlerForEAP () throws Exception {
			authenticationHandler = new DiameterAuthenticationHandler(diameterServiceContext, data);

			assertThat(authenticationHandler.createHandler(AuthMethods.EAP), 
					is(instanceOf(DiameterEapHandler.class)));
		}

		@Test
		public void returnsNullIfMethodIsUnsupported () throws Exception {
			authenticationHandler = new DiameterAuthenticationHandler(diameterServiceContext, data);

			assertThat(authenticationHandler.createHandler(AuthMethods.PROXY), 
					is(nullValue()));
		}
	}

	@Test
	public void initializesMethodHandlers() throws Exception {
		data.getAutheMethodHandlerTypes().add(AuthMethods.PAP);
		data.getAutheMethodHandlerTypes().add(AuthMethods.EAP);

		DiameterAuthenticationHandlerStub authMethodHandlerStub = new DiameterAuthenticationHandlerStub(diameterServiceContext, data);
		authMethodHandlerStub.init();

		verify(papHandler).init();
		verify(eapHandler).init();
	}

	@Test
	public void authMethodHandlersAreExecutedInConfiguredOrder () throws Exception {

		data.getAutheMethodHandlerTypes().add(AuthMethods.PAP);
		data.getAutheMethodHandlerTypes().add(AuthMethods.CHAP);
		data.getAutheMethodHandlerTypes().add(AuthMethods.EAP);
		createHandler(data);

		when(papHandler.isEligible(request)).thenReturn(false);
		when(chapHandler.isEligible(request)).thenReturn(false);
		when(eapHandler.isEligible(request)).thenReturn(true);

		authenticationHandler.handleRequest(request, response, null);

		InOrder order = inOrder(papHandler, chapHandler, eapHandler);
		order.verify(papHandler).isEligible(request);
		order.verify(chapHandler).isEligible(request);
		order.verify(eapHandler).isEligible(request);

		verify(eapHandler).handleRequest(request, response, accountInfoProvider);
	}

	@Test
	public void onlyFirstEligibleHandlerIsExecuted () throws Exception {

		data.getAutheMethodHandlerTypes().add(AuthMethods.PAP);
		data.getAutheMethodHandlerTypes().add(AuthMethods.CHAP);
		data.getAutheMethodHandlerTypes().add(AuthMethods.EAP);
		createHandler(data);

		when(papHandler.isEligible(request)).thenReturn(true);
		when(chapHandler.isEligible(request)).thenReturn(true);
		when(eapHandler.isEligible(request)).thenReturn(true);

		authenticationHandler.handleRequest(request, response, null);

		verify(papHandler).handleRequest(request, response, accountInfoProvider);
		verify(eapHandler, only()).init();
		verify(chapHandler, only()).init();
	}
	
	
	public class UsernameResponseAttribute {
		
		@Before
		public void setup() throws Exception {
			when(papHandler.isEligible(request)).thenReturn(true);
			
			authenticationHandler = new DiameterAuthenticationHandlerStub(diameterServiceContext, data);
			data.getAutheMethodHandlerTypes().add(AuthMethods.PAP);
			data.getSubscriberProfileRepositoryDetails().setUserName("AutenticatedUsername");
			createHandler(data);
		}
		
		public class OnAuthenticationSuccess {

			@Before
			public void setup() throws Exception {
				request.setParameter(AAAServerConstants.CUI_KEY, "eliteaaa");
				response.getDiameterAnswer().addAvp(DiameterAVPConstants.RESULT_CODE, "2001");
			}
			
			@Test
			public void willNotBeAddedInResponseIfHandlerIsAlreadyInvokedForThisRequest() throws Exception {
				authenticationHandler.handleRequest(request, response, null);

				authenticationHandler.handleRequest(request, response, null);

				assertThat(response.getDiameterAnswer().getAVPList(DiameterAVPConstants.USER_NAME).size(), 
						is(equalTo(1)));
			}
			
			@Test
			public void willNotBeAddedInResponseWhenUsernameParameterIsNone() throws Exception {
				data.getSubscriberProfileRepositoryDetails().setUserName("None");

				authenticationHandler.handleRequest(request, response, null);
				
				assertThat(response.getDiameterAnswer().getAVP(DiameterAVPConstants.USER_NAME), 
						is(nullValue()));
			}
			
			@Test
			public void unstrippedIdentityIsAddedInConfiguredResponseAttributesWhenConfiguredUsernameParameterIsRequest() throws Exception {
				data.getSubscriberProfileRepositoryDetails().setUserName("Request");
				
				data.getSubscriberProfileRepositoryDetails().getUserNameResponseAttributeList().add("0:31");

				request.setParameter(AAAServerConstants.UNSTRIPPED_CUI, "eliteaaa");

				authenticationHandler.handleRequest(request, response, null);
				
				assertThat(response.getDiameterAnswer(), 
						containsAttribute("0:31", "eliteaaa"));
			}

			@Test
			public void authenticatedIdentityIsAddedInConfiguredResponseAttributeIfConfiguredUsernameParameterIsAutenticatedUsername() throws Exception {
				data.getSubscriberProfileRepositoryDetails().setUserName("AutenticatedUsername");
				
				data.getSubscriberProfileRepositoryDetails().getUserNameResponseAttributeList().add("0:31");

				authenticationHandler.handleRequest(request, response, null);

				assertThat(response.getDiameterAnswer(),
						containsAttribute("0:31", "eliteaaa"));
			}
			
			@Test
			public void willNotBeAddedInResponseWhenUsernameParameterIsNull() throws Exception {
				data.getSubscriberProfileRepositoryDetails().setUserName(null);

				authenticationHandler.handleRequest(request, response, null);
				
				assertThat(response.getDiameterAnswer().getAVP(DiameterAVPConstants.USER_NAME), 
						is(nullValue()));
			}
			
			@Test
			public void addsAllConfiguredResponseAttributes() throws Exception {
				data.getSubscriberProfileRepositoryDetails().getUserNameResponseAttributeList().add("0:31");
				data.getSubscriberProfileRepositoryDetails().getUserNameResponseAttributeList().add("0:25");

				authenticationHandler.handleRequest(request, response, null);

				assertThat(response.getDiameterAnswer(),
						allOf(containsAttribute("0:31", "eliteaaa"), 
								containsAttribute("0:25", "eliteaaa")));
			}
			
			@Test
			public void skipsAnyUnknownResponseAvpsConfigured() throws Exception {
				String UNKNOWN_AVP = "0:233";
				data.getSubscriberProfileRepositoryDetails().getUserNameResponseAttributeList().add(UNKNOWN_AVP);
				data.getSubscriberProfileRepositoryDetails().getUserNameResponseAttributeList().add("0:25");
				
				authenticationHandler.handleRequest(request, response, null);

				DiameterAssertion.assertThat(response.getDiameterAnswer()).doesNotContainAVP(UNKNOWN_AVP);
				assertThat(response.getDiameterAnswer(),
						containsAttribute("0:25", "eliteaaa"));
			}

			@Test
			public void addsStandardUsernameAvpIfNotPresentAndResponseAttributesAreNotConfigured() throws Exception {
				data.getSubscriberProfileRepositoryDetails().setUserName("AutenticatedUsername");
				
				authenticationHandler.handleRequest(request, response, null);

				assertThat(response.getDiameterAnswer(),
						containsAttribute("0:1", "eliteaaa"));
			}

			@Test
			public void updateStandardUsernameAvpIfPresentAndResponseAttributesAreNotConfigured() throws Exception {
				IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.USER_NAME);
				avp.setStringValue("test");
				response.addAVP(avp);
				
				authenticationHandler.handleRequest(request, response, null);

				assertThat(response.getDiameterAnswer(),
						containsAttribute("0:1", "eliteaaa"));
			}
		}
		
		public class OnAuthenticationFailure {
		
			@Test
			public void authenticatedIdentityWillNotBeAddedInResponse() throws Exception {
				doThrow(AuthenticationFailedException.class).when(papHandler).handleRequest(request, response, accountInfoProvider);
				authenticationHandler.handleRequest(request, response, null);
				
				DiameterAssertion.assertThat(response.getDiameterAnswer())
					.doesNotContainAVP(DiameterAVPConstants.USER_NAME);
			}
		}
	}
	
	private void createHandler(DiameterAuthenticationHandlerData data) throws Exception{
		authenticationHandler = new DiameterAuthenticationHandlerStub(diameterServiceContext, data);
		authenticationHandler.init();
		authenticationHandler.setSubscriberProfileRepository(accountInfoProvider);
	}

	private void createRequestAndResponse() {
		diameterRequest =  new DiameterRequest();
		request =  new ApplicationRequest(diameterRequest);
		response =  new ApplicationResponse(diameterRequest);
		response.setCommandCode(ResultCode.DIAMETER_SUCCESS.code);
		diameterRequest.addAvp(DiameterAVPConstants.USER_NAME, TEST_USER);
	}

	private class DiameterAuthenticationHandlerStub extends DiameterAuthenticationHandler {

		public DiameterAuthenticationHandlerStub(
				DiameterServiceContext context,
				DiameterAuthenticationHandlerData data) {
			super(context, data);
		}

		@Override
		DiameterAuthMethodHandler createHandler(int methodType) {

			if (methodType == AuthMethods.PAP) {
				return papHandler;
			} else if (methodType == AuthMethods.CHAP) {
				return chapHandler;
			} else {
				return eapHandler; 
			}
		}
	}
}
