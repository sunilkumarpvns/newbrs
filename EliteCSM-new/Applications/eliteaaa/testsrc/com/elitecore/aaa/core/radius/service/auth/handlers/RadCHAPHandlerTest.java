package com.elitecore.aaa.core.radius.service.auth.handlers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.authprotocol.exception.InvalidPasswordException;
import com.elitecore.aaa.core.authprotocol.exception.UserNotFoundException;
import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.conf.RadAuthConfiguration;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.handlers.RadCHAPHandler;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService.RadiusAuthRequestImpl;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService.RadiusAuthResponseImpl;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.aaa.radius.subscriber.RadiusSubscriberProfileRepository;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class RadCHAPHandlerTest {

	private static final String CHAP_CHELLANGE = "0xf3a39e4beb451fab769d9da1beb5cece";
	private static final String CHAP_PASSWORD = "0x1ec70061a192a6a3543a2b44cf31198bfc";
	
	private RadCHAPHandler radCHAPHandler;
	private RadAuthRequest request;
	private RadAuthResponse response;

	@Mock private RadAuthServiceContext radAuthServiceContext; 
	@Mock private RadiusSubscriberProfileRepository accountInfoProvider;
	@Mock private RadClientData clientData;
	@Mock private DriverConfigurationProvider driverConfigurationProvider;
	@Mock private DriverConfiguration driverConfiguration;
	@Mock private AAAServerConfiguration aaaServerConfiguration;
	@Mock private RadAuthConfiguration radAuthConfiguration;
	@Mock private AAAServerContext serverContext;

	@Rule public ExpectedException exception = ExpectedException.none();
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();

	@BeforeClass
	public static void setUpBeforeClass() {
		RadiusDictionaryTestHarness.getInstance();
	}

	@Before
	public void setup() throws Exception{
		MockitoAnnotations.initMocks(this);
		createCHAPHandler();
		createRequestAndResponse();
	}

	public class Eligibility {

		@Test
		public void isNotEligibleForExecutionWhenCHAP_PASSWORDIsMissingInRequestPacket() throws Exception {
			assertFalse("Chap-Password must not be present", radCHAPHandler.isEligible(request));
		}

		@Test
		public void isEligibleForExecutionWhenCHAP_PASSWORDIsPresentInRequestPacket() throws Exception {
			requestWithCHAPPassword();
			assertTrue("Chap-Password must be present", radCHAPHandler.isEligible(request));
		}
		
		public class SubscriberProfileIsFound {

			private static final String ENCRYPTION_TYPE_PLAIN = "0";
			private AccountData accountData;

			@Before
			public void setUp() {
				accountData =  new AccountData();
				Mockito.when(accountInfoProvider.getAccountData(Mockito.any(RadiusAuthRequestImpl.class),Mockito.any(RadiusAuthResponseImpl.class))).thenReturn(accountData);
			}

			@Test
			public void fetchesSubscriberProfileFromAccountInfoProvider() throws Exception {

				subscriberProfileWithPassword("kkd", ENCRYPTION_TYPE_PLAIN);

				requestWithCHAPPassword();

				radCHAPHandler.handleRequest(request, response, accountInfoProvider);

				Mockito.verify(accountInfoProvider, Mockito.times(1)).getAccountData(request, response);
			}
			
			@Test
			public void throwsAuthenticationFailedExceptionWhen_CHAPCHALLANGEDoesNotSet() throws Exception {

				subscriberProfileWithPassword("kkd", ENCRYPTION_TYPE_PLAIN);

				request = new RadAuthRequestBuilder().addAttribute(RadiusAttributeConstants.CHAP_PASSWORD_STR,CHAP_PASSWORD)
						.packetType(1).build();	

				exception.expect(InvalidPasswordException.class);
				radCHAPHandler.handleRequest(request, response, accountInfoProvider);

			}
			
			public class PasswordCheckEnabled {
				
				private static final String PASSWORD = "kkd";

				@Test
				public void replyWithAuthenticationSuccessWhenPasswordMatch() throws Exception {
					subscriberProfileWithPassword(PASSWORD, ENCRYPTION_TYPE_PLAIN);
					requestWithCHAPPassword();
					
					radCHAPHandler.handleRequest(request, response, accountInfoProvider);
					assertEquals(AuthReplyMessageConstant.AUTHENTICATION_SUCCESS, response.getResponseMessege());
				}
				
				@Test
				public void throwsInvalidPasswordExceptionWhenPasswordsDontMatch() throws Exception {
					subscriberProfileWithPassword("incorrect password", ENCRYPTION_TYPE_PLAIN);
					requestWithCHAPPassword();
							
					try {
						radCHAPHandler.handleRequest(request, response, accountInfoProvider);
						fail("It should have thrown exception but didn't");
					} catch (InvalidPasswordException e) {
						assertThat(e.getMessage(), is(equalTo("Authentication Failed due to Invalid Password")));
					}
				}
				
				@Test
				public void throwsAuthenticationFailedExceptionWhenEncryptionTypeIsNonIntegralValue() throws Exception {
					String password = PASSWORD;
					subscriberProfileWithPassword(password, "invalid-encryption-type");
					requestWithCHAPPassword();
					
					try {
						radCHAPHandler.handleRequest(request, response, accountInfoProvider);
						fail("It should have thrown exception but didn't");
					} catch (AuthenticationFailedException e) {
						assertThat(e.getMessage(), is(equalTo("Authentication failed due to invalid encryption type.")));
					}
				}

				@Test
				public void throwsAuthenticationFailedExceptionIfUnsupportedEncryptionTypeIsConfiguredInSubscriberProfile() throws Exception {
					subscriberProfileWithPassword(PASSWORD, "23");
					requestWithCHAPPassword();
					
					try {
						radCHAPHandler.handleRequest(request, response, accountInfoProvider);
						fail("It should have thrown exception but didn't");
					} catch (AuthenticationFailedException e) {
						assertTrue(e.getCause() instanceof NoSuchEncryptionException);
						assertThat(e.getMessage(), is(equalTo("Authentication failed due to unsupported encryption.")));
					}
				}
				
				@Test
				public void throwsDecryptionNotSupportedExceptionIfPasswordInRequestPacketIsNotAsPerEncryptionType() throws Exception {
					subscriberProfileWithPassword(PASSWORD, String.valueOf(PasswordEncryption.BASE16));
					requestWithCHAPPassword();
					
					try {
						radCHAPHandler.handleRequest(request, response, accountInfoProvider);
						fail("It should have thrown exception but didn't");
					} catch (AuthenticationFailedException e) {
						assertTrue(e.getCause() instanceof DecryptionNotSupportedException);
						assertThat(e.getMessage(), is(equalTo("Authentication failed due to improper password encryption format.")));
					}
				}
				
				@Test
				public void throwsDecryptionFailedExceptionIfPasswordInRequestPacketIsNotAsPerEncryptionType() throws Exception {
					subscriberProfileWithPassword(PASSWORD, String.valueOf(PasswordEncryption.AES_CRYPT));
					requestWithCHAPPassword();
					
					try {
						radCHAPHandler.handleRequest(request, response, accountInfoProvider);
						fail("It should have thrown exception but didn't");
					} catch (AuthenticationFailedException e) {
						assertTrue(e.getCause() instanceof DecryptionFailedException);
						assertThat(e.getMessage(), is(equalTo("Authentication failed due to exception in decryption")));
					}
				}
			}
			
			public class PasswordCheckDisabled {
				
				private final static String INVALID_PASSWORD = "INVALID-PASSWORD";
				
				@Before
				public void setUp() {
					accountData.setPasswordCheck("NO");
				}
				
				@Test
				public void canBeDisabledUsing_NO_Keyword() throws Exception {
					subscriberProfileWithPassword(INVALID_PASSWORD, ENCRYPTION_TYPE_PLAIN);
					requestWithCHAPPassword();
					try {
						radCHAPHandler.handleRequest(request, response, accountInfoProvider);
					} catch (InvalidPasswordException e) {
						assertThat(e.getMessage(), is(equalTo("Authentication failed due to Invalid password")));
					}
				}
				
				@Test
				public void canBeDisabledUsing_NO_Keyword_IgnoringCase() throws Exception {
					accountData.setPasswordCheck("No");
					
					subscriberProfileWithPassword(INVALID_PASSWORD, ENCRYPTION_TYPE_PLAIN);
					requestWithCHAPPassword();
					
					try {
						radCHAPHandler.handleRequest(request, response, accountInfoProvider);
					} catch (InvalidPasswordException e) {
						assertThat(e.getMessage(), is(equalTo("Authentication failed due to Invalid password")));
					}
				}
				
				@Test
				public void doesNotValidatePassword() throws Exception {
					subscriberProfileWithPassword(INVALID_PASSWORD, ENCRYPTION_TYPE_PLAIN);
					requestWithCHAPPassword();
					
					try {
						radCHAPHandler.handleRequest(request, response, accountInfoProvider);
					} catch (InvalidPasswordException e) {
						assertThat(e.getMessage(), is(equalTo("Authentication failed due to Invalid password")));
					}
				}
			}

			private void subscriberProfileWithPassword(String pwd, String encryptionType) {
				accountData.setPassword(pwd);
				accountData.setEncryptionType(encryptionType);
			}
		}
	}
	
	@Test
	public void throwsUserNotfoundExceptionWhenSubscriberProfileNotFound() throws AuthenticationFailedException {
		exception.expect(UserNotFoundException.class);
		radCHAPHandler.handleRequest(request, response, accountInfoProvider);
	}
	
	private void requestWithCHAPPassword() throws InvalidAttributeIdException {
		request = new RadAuthRequestBuilder().addAttribute(RadiusAttributeConstants.CHAP_PASSWORD_STR,CHAP_PASSWORD)
				.addAttribute(RadiusAttributeConstants.CHAP_CHALLENGE_STR,CHAP_CHELLANGE)
				.packetType(1).build();	
	}

	private void createCHAPHandler() {
		radCHAPHandler = new RadCHAPHandler(radAuthServiceContext);
	}

	private void createRequestAndResponse() throws UnknownHostException {
		request = new RadAuthRequestBuilder().build();
		response = new RadAuthRequestBuilder().buildResponse();
	}
}