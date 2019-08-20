package com.elitecore.aaa.diameter.service.application.handlers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.authprotocol.exception.InvalidPasswordException;
import com.elitecore.aaa.core.authprotocol.exception.UserNotFoundException;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthenticationHandlerData;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepository;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepositoryDetails;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class DiameterPAPMethodHandlerTest {

	private static final boolean TRIM_PASSWORD_DISABLED = false;
	private DiameterAuthenticationHandlerData diameterAuthenticationHandlerData;
	private DiameterPAPMethodHandler diameterPAPMethodHandler;
	private DiameterRequest diameterRequest;
	private ApplicationRequest request;
	private ApplicationResponse response;

	@Mock private DiameterSubscriberProfileRepository accountInfoProvider;
	@Mock private DiameterServiceContext diameterServiceContext;

	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();

	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setup() throws InitializationFailedException{
		diameterAuthenticationHandlerData = new DiameterAuthenticationHandlerData();
		MockitoAnnotations.initMocks(this);
		createRequestAndResponse();
		createHandlerObject(TRIM_PASSWORD_DISABLED);
	}
	
	@Test
	public void isNotEligibleForExecutionWhenPasswordAttributeIsMissingInRequestPacket() throws AuthenticationFailedException, InitializationFailedException {
		assertFalse(diameterPAPMethodHandler.isEligible(request));
	}
	
	@Test
	public void isEligibleForExecutionWhenPasswordAttributeIsPresentInRequestPacket() throws AuthenticationFailedException, InitializationFailedException {
		requestWithPassword("any");
		assertTrue(diameterPAPMethodHandler.isEligible(request));
	}
	
	@Test
	public void throwsUserNotfoundExceptionWhenSubscriberProfileAndAnonymousProfileIsNotFound() throws AuthenticationFailedException {
		requestWithPassword("test");
		exception.expect(UserNotFoundException.class);
		
		diameterPAPMethodHandler.handleRequest(request, response, accountInfoProvider);
	}
	
	
	public class SubscriberProfileIsFound {
		
		private AccountData accountData;
		
		@Before
		public void setUp() {
			accountData =  new AccountData();
			Mockito.when(accountInfoProvider.getAccountData(request, response)).thenReturn(accountData);
		}
		
		@Test
		public void fetchesSubscriberProfileFromAccountInfoProvider() throws AuthenticationFailedException {
			
			subscriberProfileWithPassword("test", "0");
			requestWithPassword("test");
			
			diameterPAPMethodHandler.handleRequest(request, response, accountInfoProvider);
			
			Mockito.verify(accountInfoProvider, Mockito.times(1)).getAccountData(request, response);
		}
		
		@Test
		public void fetchesAnonymousSubscriberProfileIfMainSubscriberProfileIsNotFound() throws AuthenticationFailedException {
			subscriberProfileWithPassword("test", "0");
			requestWithPassword("test");
			
			Mockito.when(accountInfoProvider.getAccountData(request, response)).thenReturn(null);
			Mockito.when(accountInfoProvider.getAnonymousAccountData(request)).thenReturn(accountData);
			
			diameterPAPMethodHandler.handleRequest(request, response, accountInfoProvider);
			
			InOrder inOrder = Mockito.inOrder(accountInfoProvider);
			inOrder.verify(accountInfoProvider).getAccountData(request, response);
			inOrder.verify(accountInfoProvider).getAnonymousAccountData(request);
		}
		
		public class PasswordCheckEnabled {
			
			private static final boolean TRIM_PASSWORD_ENABLED = true;

			@Test
			public void setsResultCodeToSuccessWhenPasswordMatch() throws AuthenticationFailedException {
				subscriberProfileWithPassword("test", "0");
				
				requestWithPassword("test");
				
				diameterPAPMethodHandler.handleRequest(request, response, accountInfoProvider);
				
				resultCodeIsSuccess();
			}
			
			@Test
			public void setsAccountDataInRequestIfAuthenticationSucceeds() throws AuthenticationFailedException {
				subscriberProfileWithPassword("test", "0");
				
				requestWithPassword("test");
				
				diameterPAPMethodHandler.handleRequest(request, response, accountInfoProvider);
				
				assertThat(request.getAccountData(), is(accountData));
			}

			@Test
			public void throwsAuthenticationFailedExceptionWhenEncryptionTypeIsNonIntegralValueAndDoesNotSetAccountDataInRequest() throws AuthenticationFailedException {
				String password = "test";
				subscriberProfileWithPassword(password, "invalid-ecryption-type");
				requestWithPassword(password);
				
				try {
					diameterPAPMethodHandler.handleRequest(request, response, accountInfoProvider);
					fail("It should have thrown exception but didn't");
				} catch (AuthenticationFailedException e) {
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_TO_INVALID_ENCRYPTION_TYPE)));
					assertThat(request.getAccountData(), is(nullValue()));
				}
			}

			@Test
			public void throwsInvalidPasswordExceptionWhenPasswordsDontMatchAndDoesNotSetAccountDataInRequest() throws AuthenticationFailedException {
				subscriberProfileWithPassword("test", "0");
				requestWithPassword("test1");
						
				try {
					diameterPAPMethodHandler.handleRequest(request, response, accountInfoProvider);
					fail("It should have thrown exception but didn't");
				} catch (InvalidPasswordException e) {
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD)));
					assertThat(request.getAccountData(), is(nullValue()));
				}
			}

			@Test
			public void throwsAuthenticationFailedExceptionIfUnsupportedEncryptionTypeIsConfiguredInSubscriberProfileAndDoesNotSetAccountDataInRequest() throws AuthenticationFailedException {
				subscriberProfileWithPassword("test", "23");
				requestWithPassword("test1");
				
				try {
					diameterPAPMethodHandler.handleRequest(request, response, accountInfoProvider);
					fail("It should have thrown exception but didn't");
				} catch (AuthenticationFailedException e) {
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_TO_UNSUPPORTED_ENCRYPTION)));
					assertThat(request.getAccountData(), is(nullValue()));
				}
			}
			
			@Test
			public void trimsRequestPasswordIfTrimPasswordIsEnabled() throws AuthenticationFailedException, InitializationFailedException {

				createHandlerObject(TRIM_PASSWORD_ENABLED);
				
				subscriberProfileWithPassword("test", "0");
				requestWithPassword(" test ");
				
				diameterPAPMethodHandler.handleRequest(request, response, accountInfoProvider);
			}
		}
		
		public class PasswordCheckDisabled {
			
			@Before
			public void setUp() {
				accountData.setPasswordCheck("NO");
			}
			
			@Test
			public void doesNotValidatePassword() throws AuthenticationFailedException {
				subscriberProfileWithPassword("test", "0");
				requestWithPassword("test1");
				
				diameterPAPMethodHandler.handleRequest(request, response, accountInfoProvider);
			}
			
			@Test
			public void setsResultCodeToSuccess() throws AuthenticationFailedException {
				subscriberProfileWithPassword("test", "0");
				requestWithPassword("test1");
				
				diameterPAPMethodHandler.handleRequest(request, response, accountInfoProvider);
				
				resultCodeIsSuccess();
			}
			
			@Test
			public void setsAccountDataInRequest() throws AuthenticationFailedException {
				subscriberProfileWithPassword("test", "0");
				requestWithPassword("test1");
				
				diameterPAPMethodHandler.handleRequest(request, response, accountInfoProvider);
			
				assertThat(request.getAccountData(), is(accountData));
			}
		}

		private void subscriberProfileWithPassword(String pwd, String encryptionType) {
			accountData.setPassword(pwd);
			accountData.setEncryptionType(encryptionType);
		}
	}
	
	private void createHandlerObject(boolean isTrimPassword) throws InitializationFailedException {
		diameterAuthenticationHandlerData.setSubscriberProfileRepositoryDetails( new DiameterSubscriberProfileRepositoryDetails() );
		diameterAuthenticationHandlerData.getSubscriberProfileRepositoryDetails().getUpdateIdentity().setIsTrimPassword(isTrimPassword);
		diameterPAPMethodHandler = new DiameterPAPMethodHandler(diameterServiceContext, diameterAuthenticationHandlerData);
		diameterPAPMethodHandler.init();
	}


	private void requestWithPassword(String pwd) {
		diameterRequest.addAvp(DiameterAVPConstants.USER_PASSWORD, pwd);
	}

	private void createRequestAndResponse() {
		diameterRequest =  new DiameterRequest();
		request =  new ApplicationRequest(diameterRequest);
		response =  new ApplicationResponse(diameterRequest);
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE_STR, 
				response.getDiameterAnswer(), ResultCode.DIAMETER_AUTHENTICATION_REJECTED.code + "");
	}

	private void resultCodeIsSuccess() {
		DiameterAssertion.assertThat(response.getDiameterAnswer())
		.hasResultCode(ResultCode.DIAMETER_SUCCESS);
	}

}
