package com.elitecore.aaa.core.radius.service.auth.handlers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

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
import com.elitecore.aaa.radius.service.auth.handlers.RadPAPHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthenticationHandlerData;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService.RadiusAuthRequestImpl;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService.RadiusAuthResponseImpl;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.aaa.radius.subscriber.RadiusSubscriberProfileRepository;
import com.elitecore.aaa.radius.subscriber.conf.RadiusSubscriberProfileRepositoryDetails;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class RadPAPHandlerTest {

	private static final boolean TRIM_PASSWORD_DISABLED = false;
	private static final String DUMMY_ID = "0";
	private final static String PASSWORD = "0x0b86b7a5bf2ac68e94ee9f2bf75b9601";
	private AuthenticationHandlerData authenticationHandlerData;
	private RadPAPHandler radPAPHandler;
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
		initAuthenticationHandler(TRIM_PASSWORD_DISABLED);
		createAndInitPAPHandler();
		createRequestAndResponse();
	}

	@Test
	public void isNotEligibleForExecutionWhenPasswordAttributeIsMissingInRequestPacket() throws Exception {
		assertFalse(radPAPHandler.isEligible(request));
	}

	@Test
	public void isEligibleForExecutionWhenPasswordAttributeIsPresentInRequestPacket() throws Exception {
		requestWithPassword(PASSWORD);
		assertTrue(radPAPHandler.isEligible(request));
	}

	@Test
	public void throwsUserNotfoundExceptionWhenSubscriberProfileNotFound() throws AuthenticationFailedException {
		exception.expect(UserNotFoundException.class);
		radPAPHandler.handleRequest(request, response, accountInfoProvider);
	}

	public class SubscriberProfileFound {

		private AccountData accountData;

		@Before
		public void setUp() throws Exception {
			accountData =  new AccountData();
			Mockito.when(accountInfoProvider.getAccountData(Mockito.any(RadiusAuthRequestImpl.class),Mockito.any(RadiusAuthResponseImpl.class))).thenReturn(accountData);

			when(clientData.getClientIp()).thenReturn("127.0.0.1");
			when(clientData.getSharedSecret(anyInt())).thenReturn("secret");

			when(radAuthServiceContext.getAuthConfiguration()).thenReturn(radAuthConfiguration);
			when(radAuthServiceContext.getServerContext()).thenReturn(serverContext);
			when(serverContext.getServerConfiguration()).thenReturn(aaaServerConfiguration);

			when(aaaServerConfiguration.getDriverConfigurationProvider()).thenReturn(driverConfigurationProvider);
			when(driverConfigurationProvider.getDriverConfiguration(DUMMY_ID)).thenReturn(driverConfiguration);
			when(driverConfiguration.getDriverInstanceId()).thenReturn(DUMMY_ID);


			request.setParameter(AAAServerConstants.DRIVER_INSTANCE_ID, DUMMY_ID);
			response.setClientData(clientData);
		}

		@Test
		public void fetchesSubscriberProfileFromAccountInfoProvider() throws Exception {

			subscriberProfileWithPassword("test", "0");
			requestWithPassword(PASSWORD);

			radPAPHandler.handleRequest(request, response, accountInfoProvider);

			Mockito.verify(accountInfoProvider, Mockito.times(1)).getAccountData(request, response);
		}

		public class PasswordCheckEnabled {

			private static final boolean TRIM_PASSWORD_ENABLED = true;

			@Test
			public void replayWithAuthenticationSuccessWhenPasswordMatch() throws Exception {
				subscriberProfileWithPassword("test", "0");

				requestWithPassword(PASSWORD);

				radPAPHandler.handleRequest(request, response, accountInfoProvider);
				assertEquals(AuthReplyMessageConstant.AUTHENTICATION_SUCCESS, response.getResponseMessege());

			}

			@Test
			public void throwsAuthenticationFailedExceptionWhenEncryptionTypeIsNonIntegralValue() throws Exception {
				String password = "test";
				subscriberProfileWithPassword(password, "invalid-ecryption-type");
				requestWithPassword(PASSWORD);

				try {
					radPAPHandler.handleRequest(request, response, accountInfoProvider);
					fail("It should have thrown exception, but didn't");
				} catch (AuthenticationFailedException e) {
					assertThat(e.getMessage(), is(equalTo("Authentication failed due to invalid encryption type.")));
				}
			}

			@Test
			public void throwsInvalidPasswordExceptionWhenPasswordDoesnotMatch() throws Exception {
				subscriberProfileWithPassword("test1", "0");
				requestWithPassword(PASSWORD);

				try {
					radPAPHandler.handleRequest(request, response, accountInfoProvider);
					fail("It should have thrown exception, but didn't");
				} catch (InvalidPasswordException e) {
					assertThat(e.getMessage(), is(equalTo("Authentication Failed due to Invalid Password")));
				}
			}

			@Test
			public void throwsAuthenticationFailedExceptionIfUnsupportedEncryptionTypeIsConfiguredInSubscriberProfile() throws Exception {
				subscriberProfileWithPassword("test1", "23");
				requestWithPassword(PASSWORD);

				try {
					radPAPHandler.handleRequest(request, response, accountInfoProvider);
					fail("It should have thrown exception, but didn't");
				} catch (AuthenticationFailedException e) {
					assertThat(e.getMessage(), is(equalTo("Authentication failed due to unsupported encryption.")));
				}
			}

			@Test
			public void trimsRequestPasswordIfTrimPasswordIsEnabled() throws Exception {

				initAuthenticationHandler(TRIM_PASSWORD_ENABLED);
				createAndInitPAPHandler();
				subscriberProfileWithPassword("test", "0");
				requestWithPassword(PASSWORD+ "   ");

				radPAPHandler.handleRequest(request, response, accountInfoProvider);
			}
			
			@Test
			public void throwsExceptionWhenPasswordIsNULL() throws Exception {
				subscriberProfileWithPassword(null, "0");
				requestWithPassword(PASSWORD);

				exception.expect(InvalidPasswordException.class);
				exception.expectMessage("Authentication Failed due to Invalid Password");
				radPAPHandler.handleRequest(request, response, accountInfoProvider);

			}
		}

		public class PasswordCheckDisabled {

			@Before
			public void setUp() {
				accountData.setPasswordCheck("NO");
			}

			@Test
			public void doesNotValidatePassword() throws Exception {
				subscriberProfileWithPassword("test1", "0");
				requestWithPassword(PASSWORD);

				radPAPHandler.handleRequest(request, response, accountInfoProvider);
			}

			@Test
			public void setsResultCodeToSuccess() throws Exception {
				subscriberProfileWithPassword("test1", "0");
				requestWithPassword(PASSWORD);

				radPAPHandler.handleRequest(request, response, accountInfoProvider);

				assertEquals(AuthReplyMessageConstant.AUTHENTICATION_SUCCESS, response.getResponseMessege());
			}
		}
		
		private void subscriberProfileWithPassword(String pwd, String encryptionType) {
			accountData.setPassword(pwd);
			accountData.setEncryptionType(encryptionType);
		}
	}

	public void createAndInitPAPHandler() throws InitializationFailedException{
		radPAPHandler = new RadPAPHandler(radAuthServiceContext,authenticationHandlerData);
		radPAPHandler.init();
	}

	private void requestWithPassword(String password) throws Exception {
		request = new RadAuthRequestBuilder().addAttribute(RadiusAttributeConstants.USER_PASSWORD_STR, PASSWORD)
				.packetType(1).build();
	}

	private void initAuthenticationHandler(boolean pwd) {
		authenticationHandlerData = new AuthenticationHandlerData();
		authenticationHandlerData.setSubscriberProfileRepositoryDetails(new RadiusSubscriberProfileRepositoryDetails());
		authenticationHandlerData.getUserProfileRepoDetails().getUpdateIdentity().setIsTrimPassword(pwd);
	}

	private void createRequestAndResponse() throws UnknownHostException {
		request = new RadAuthRequestBuilder().build();
		response = new RadAuthRequestBuilder().buildResponse();
	}
}
