package com.elitecore.aaa.diameter.service.application.handlers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
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
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacketBuilder;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class DiameterCHAPMethodHandlerTest {
	
	private static final String CHAP_IDENT = DiameterAVPConstants.CHAP_AUTH + "." + DiameterAVPConstants.CHAP_IDENT;
	private static final String CHAP_RESPONSE = DiameterAVPConstants.CHAP_AUTH + "." + DiameterAVPConstants.CHAP_RESPONSE;
	private static final String FAILED_AVP = DiameterAVPConstants.FAILED_AVP + ".";
	private static final String ANY = "ANY";
	
	private DiameterAuthenticationHandlerData diameterAuthenticationHandlerData;
	private DiameterCHAPMethodHandler diameterCHAPMethodHandler;
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
		createHandlerObject();
	}
	
	public class Eligibility {
		
		@Test
		public void isNotEligibleForExecutionWhenCHAP_AUTH_CHAP_CHALLENGE_AND_CHAP_IDENT_AreMissingInRequestPacket() throws AuthenticationFailedException, InitializationFailedException {
			assertFalse(diameterCHAPMethodHandler.isEligible(request));
		}
		
		@Test
		public void isNotEligibleForExecutionWhenOnlyCHAP_AUTHIsPresentInRequestPacket() throws AuthenticationFailedException, InitializationFailedException {
			DiameterRequest request = DiameterPacketBuilder.localRequestBuilder()
					.addAVP(DiameterAVPConstants.CHAP_AUTH, ANY)
					.build();
					
			assertFalse(diameterCHAPMethodHandler.isEligible(new ApplicationRequest(request)));
		}
		
		@Test
		public void isNotEligibleForExecutionWhenOnlyCHAP_IDENTIsPresentInRequestPacket() throws AuthenticationFailedException, InitializationFailedException {
			DiameterRequest request = DiameterPacketBuilder.localRequestBuilder()
					.addAVP(CHAP_IDENT, ANY)
					.build();
					
			assertFalse(diameterCHAPMethodHandler.isEligible(new ApplicationRequest(request)));
		}
		
		@Test
		public void isNotEligibleForExecutionWhenOnlyCHAP_CHALLENGEIsPresentInRequestPacket() throws AuthenticationFailedException, InitializationFailedException {
			DiameterRequest request = DiameterPacketBuilder.localRequestBuilder()
					.addAVP(DiameterAVPConstants.CHAP_CHALLENGE, ANY)
					.build();
					
			assertFalse(diameterCHAPMethodHandler.isEligible(new ApplicationRequest(request)));
		}
		
		@Test
		public void isEligibleForExecutionWhenCHAP_AUTH_CHAP_CHALLENGE_AND_CHAP_IDENT_ArePresentInRequestPacket() throws AuthenticationFailedException, InitializationFailedException {
			requestWith(ANY, ANY, ANY);
			
			assertTrue(diameterCHAPMethodHandler.isEligible(request));
		}
	}
	
	@Test
	public void throwsUserNotfoundExceptionWhenSubscriberProfileAndAnonymousProfileIsNotFound() throws AuthenticationFailedException {
		exception.expect(UserNotFoundException.class);

		diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
	}
	

	public class SubscriberProfileIsFound {
		
		private static final String ENCRYPTION_TYPE_PLAIN = "0";
		private static final String CHAP_CHALLENGE = "0x30313233343536373839616263646566";
		private static final String CHAP_IDENTITY = "0x00000030";
		private static final String CHAP_RESPONSE = "0xcd30119f7f56cb0965c14e6638a9a45c";
		private static final String PASSWORD = "kkd";
		
		private AccountData accountData;
		
		@Before
		public void setUp() {
			accountData =  new AccountData();
			Mockito.when(accountInfoProvider.getAccountData(request, response)).thenReturn(accountData);
		}
		
		@Test
		public void fetchesSubscriberProfileFromAccountInfoProvider() throws AuthenticationFailedException {
			
			subscriberProfileWithPassword(PASSWORD, ENCRYPTION_TYPE_PLAIN);
			
			requestWith(CHAP_RESPONSE, CHAP_IDENTITY, CHAP_CHALLENGE);
			
			diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
			
			Mockito.verify(accountInfoProvider, Mockito.times(1)).getAccountData(request, response);
		}
		
		@Test
		public void fetchesAnonymousSubscriberProfileIfMainSubscriberProfileIsNotFound() throws AuthenticationFailedException {
			subscriberProfileWithPassword(PASSWORD, ENCRYPTION_TYPE_PLAIN);
			
			requestWith(CHAP_RESPONSE, CHAP_IDENTITY, CHAP_CHALLENGE);
			
			Mockito.when(accountInfoProvider.getAccountData(request, response)).thenReturn(null);
			Mockito.when(accountInfoProvider.getAnonymousAccountData(request)).thenReturn(accountData);
			
			diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
			
			InOrder inOrder = Mockito.inOrder(accountInfoProvider);
			inOrder.verify(accountInfoProvider).getAccountData(request, response);
			inOrder.verify(accountInfoProvider).getAnonymousAccountData(request);
		}

		
		public class PasswordCheckEnabled {
			
			@Test
			public void setsResultCodeToSuccessWhenPasswordMatch() throws AuthenticationFailedException {
				subscriberProfileWithPassword(PASSWORD, ENCRYPTION_TYPE_PLAIN);
				requestWith(CHAP_RESPONSE, CHAP_IDENTITY, CHAP_CHALLENGE);
				
				diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
				
				resultCodeIsSuccess();
			}
			
			@Test
			public void setsAccountDataInRequestIfAuthenticationSucceeds() throws AuthenticationFailedException {
				subscriberProfileWithPassword(PASSWORD, ENCRYPTION_TYPE_PLAIN);
				requestWith(CHAP_RESPONSE, CHAP_IDENTITY, CHAP_CHALLENGE);
				
				diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
				
				assertThat(request.getAccountData(), is(accountData));
			}
			
			@Test
			public void throwsAuthenticationFailedExceptionIfCHAP_ResponseIsNotPresentInRequestPacket() {
				try {
					diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
					fail("It should have thrown exception but didn't");
				} catch (AuthenticationFailedException e) {
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD)));
					assertThat(request.getAccountData(), is(nullValue()));
				}
			}
			
			@Test
			public void throwsInvalidPasswordExceptionIfCHAP_IdentIsEmptyString() throws AuthenticationFailedException {
				requestWith(ANY, "", ANY);
				try {
					diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
					fail("It should have thrown exception but didn't");
				} catch (InvalidPasswordException e) {
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_TO_INVALID_AVP)));
					assertThat(request.getAccountData(), is(nullValue()));
				}
			}
			
			@Test
			public void addedFailedAvpInResponseIfCHAP_IdentIsEmptyStringAndDoesNotSetAccountDataInRequest() throws AuthenticationFailedException{
				requestWith(ANY, "", ANY);
				try {
					diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
					fail("It should have thrown exception but didn't");
				} catch (InvalidPasswordException e) {
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_TO_INVALID_AVP)));
					DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(FAILED_AVP + DiameterAVPConstants.CHAP_IDENT);
					assertThat(request.getAccountData(), is(nullValue()));
				}
			}
			
			@Test
			public void throwsAuthenticationFailedExceptionWhenEncryptionTypeIsNonIntegralValueAndDoesNotSetAccountDataInRequest() throws AuthenticationFailedException {
				String password = PASSWORD;
				subscriberProfileWithPassword(password, "invalid-encryption-type");
				requestWith(CHAP_RESPONSE, CHAP_IDENTITY, CHAP_CHALLENGE);
				
				try {
					diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
					fail("It should have thrown exception but didn't");
				} catch (AuthenticationFailedException e) {
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_TO_INVALID_ENCRYPTION_TYPE)));
					assertThat(request.getAccountData(), is(nullValue()));
				}
			}
			
			@Test
			public void throwsInvalidPasswordExceptionWhenPasswordsDontMatchAndDoesNotSetAccountDataInRequest() throws AuthenticationFailedException {
				subscriberProfileWithPassword("incorrect password", ENCRYPTION_TYPE_PLAIN);
				requestWith(CHAP_RESPONSE, CHAP_IDENTITY, CHAP_CHALLENGE);
						
				try {
					diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
					fail("It should have thrown exception but didn't");
				} catch (InvalidPasswordException e) {
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD)));
					assertThat(request.getAccountData(), is(nullValue()));
				}
			}
			
			@Test
			public void throwsAuthenticationFailedExceptionIfUnsupportedEncryptionTypeIsConfiguredInSubscriberProfileAndDoesNotSetAccountDataInRequest() throws AuthenticationFailedException {
				subscriberProfileWithPassword(ANY, "23");
				requestWith(CHAP_RESPONSE, CHAP_IDENTITY, CHAP_CHALLENGE);
				
				try {
					diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
					fail("It should have thrown exception but didn't");
				} catch (AuthenticationFailedException e) {
					assertTrue(e.getCause() instanceof NoSuchEncryptionException);
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_TO_UNSUPPORTED_ENCRYPTION)));
					assertThat(request.getAccountData(), is(nullValue()));
				}
			}
			
			@Test
			public void throwsDecryptionNotSupportedExceptionIfPasswordInRequestPacketIsNotAsPerEncryptionTypeAndDoesNotSetAccountDataInRequest() throws AuthenticationFailedException {
				subscriberProfileWithPassword(PASSWORD, PasswordEncryption.BASE16 + "");
				requestWith(CHAP_RESPONSE, CHAP_IDENTITY, CHAP_CHALLENGE);
				
				try {
					diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
					fail("It should have thrown exception but didn't");
				} catch (AuthenticationFailedException e) {
					assertTrue(e.getCause() instanceof DecryptionNotSupportedException);
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_TO_IMPROPER_PASSWORD_ENCRYPTION_FORMAT)));
					assertThat(request.getAccountData(), is(nullValue()));
				}
			}
			
			@Test
			public void throwsDecryptionFailedExceptionIfPasswordInRequestPacketIsNotAsPerEncryptionTypeAndDoesNotSetAccountDataInRequest() throws AuthenticationFailedException {
				subscriberProfileWithPassword(PASSWORD, PasswordEncryption.AES_CRYPT + "");
				requestWith(CHAP_RESPONSE, CHAP_IDENTITY, CHAP_CHALLENGE);
				
				try {
					diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
					fail("It should have thrown exception but didn't");
				} catch (AuthenticationFailedException e) {
					assertTrue(e.getCause() instanceof DecryptionFailedException);
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_TO_EXCEPTION_IN_DECRYPTION)));
					assertThat(request.getAccountData(), is(nullValue()));
				}
			}
			
		}
		
		public class PasswordCheckDisabled {
			
			@Before
			public void setUp() {
				accountData.setPasswordCheck("NO");
			}
			
			@Test
			public void canBeDisabledUsing_NO_Keyword() throws AuthenticationFailedException {
				subscriberProfileWithPassword(ANY, ENCRYPTION_TYPE_PLAIN);
				requestWith(CHAP_RESPONSE, CHAP_IDENTITY, CHAP_CHALLENGE);
				try {
					diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
				} catch (InvalidPasswordException e) {
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD)));
					assertThat(request.getAccountData(), is(nullValue()));
				}
			}
			
			@Test
			public void canBeDisabledUsing_NO_Keyword_IgnoringCase() throws AuthenticationFailedException {
				accountData.setPasswordCheck("No");
				
				subscriberProfileWithPassword(ANY, ENCRYPTION_TYPE_PLAIN);
				requestWith(CHAP_RESPONSE, CHAP_IDENTITY, CHAP_CHALLENGE);
				
				try {
					diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
				} catch (InvalidPasswordException e) {
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD)));
					assertThat(request.getAccountData(), is(nullValue()));
				}
			}
			
			@Test
			public void canBeDisabledUsing_FALSE_Keyword() throws AuthenticationFailedException {
				accountData.setPasswordCheck("FALSE");

				subscriberProfileWithPassword(ANY, ENCRYPTION_TYPE_PLAIN);
				requestWith(CHAP_RESPONSE, CHAP_IDENTITY, CHAP_CHALLENGE);

				try {
					diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
				} catch (InvalidPasswordException e) {
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD)));
					assertThat(request.getAccountData(), is(nullValue()));
				}			
			}
			
			@Test
			public void canBeDisabledUsing_FALSE_Keyword_IgnoringCase() throws AuthenticationFailedException {
				accountData.setPasswordCheck("False");
				
				subscriberProfileWithPassword(ANY, ENCRYPTION_TYPE_PLAIN);
				requestWith(CHAP_RESPONSE, CHAP_IDENTITY, CHAP_CHALLENGE);
				
				try {
					diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
				} catch (InvalidPasswordException e) {
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD)));
					assertThat(request.getAccountData(), is(nullValue()));
				}
			}
			
			
			@Test
			public void doesNotValidatePassword() throws AuthenticationFailedException {
				subscriberProfileWithPassword(ANY, ENCRYPTION_TYPE_PLAIN);
				requestWith(CHAP_RESPONSE, CHAP_IDENTITY, CHAP_CHALLENGE);
				
				try {
					diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
				} catch (InvalidPasswordException e) {
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD)));
					assertThat(request.getAccountData(), is(nullValue()));
				}
			}
			
			@Test
			public void setsResultCodeToSuccess() throws AuthenticationFailedException {
				subscriberProfileWithPassword(ANY, ENCRYPTION_TYPE_PLAIN);
				requestWith(CHAP_RESPONSE, CHAP_IDENTITY, CHAP_CHALLENGE);
				
				try {
					diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
				} catch (InvalidPasswordException e) {
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD)));
					assertThat(request.getAccountData(), is(nullValue()));
				}
				
				//resultCodeIsSuccess();
			}
			
			@Test
			public void setsAccountDataInRequest() throws AuthenticationFailedException {
				subscriberProfileWithPassword(ANY, ENCRYPTION_TYPE_PLAIN);
				requestWith(CHAP_RESPONSE, CHAP_IDENTITY, CHAP_CHALLENGE);
				
				try {
					diameterCHAPMethodHandler.handleRequest(request, response, accountInfoProvider);
				} catch (InvalidPasswordException e) {
					assertThat(e.getMessage(), is(equalTo(DiameterErrorMessageConstants.AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD)));
					assertThat(request.getAccountData(), is(nullValue()));
				}
			
				//assertThat(request.getAccountData(), is(accountData));
			}
		}

		private void subscriberProfileWithPassword(String pwd, String encryptionType) {
			accountData.setPassword(pwd);
			accountData.setEncryptionType(encryptionType);
		}
	}
	
	private void resultCodeIsSuccess() {
		DiameterAssertion.assertThat(response.getDiameterAnswer())
		.hasResultCode(ResultCode.DIAMETER_SUCCESS);
	}
	
	
	private void requestWith(String chapResponse, String chapIdent, String chapChallenge) {
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.SESSION_ID, request.getDiameterRequest(), "123");
		DiameterUtility.addOrReplaceAvp(CHAP_IDENT, request.getDiameterRequest(), chapIdent);
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.CHAP_CHALLENGE, request.getDiameterRequest(), chapChallenge);
		DiameterUtility.addOrReplaceAvp(CHAP_RESPONSE, request.getDiameterRequest(), chapResponse);
	}

	private void createRequestAndResponse() {
		diameterRequest =  new DiameterRequest();
		request =  new ApplicationRequest(diameterRequest);
		response =  new ApplicationResponse(diameterRequest);
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE_STR, 
				response.getDiameterAnswer(), ResultCode.DIAMETER_AUTHENTICATION_REJECTED.code + "");
	}
	
	private void createHandlerObject() throws InitializationFailedException {
		diameterAuthenticationHandlerData.setSubscriberProfileRepositoryDetails( new DiameterSubscriberProfileRepositoryDetails() );
		diameterCHAPMethodHandler = new DiameterCHAPMethodHandler(diameterServiceContext, diameterAuthenticationHandlerData);
		diameterCHAPMethodHandler.init();
	}
	
}
