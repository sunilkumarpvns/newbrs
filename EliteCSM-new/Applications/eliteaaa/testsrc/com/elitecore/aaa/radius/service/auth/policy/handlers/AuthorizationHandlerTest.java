package com.elitecore.aaa.radius.service.auth.policy.handlers;

import static com.elitecore.aaa.EliteAAAMatchers.RadServiceResponseMatchers.acceptMessage;
import static com.elitecore.aaa.EliteAAAMatchers.RadServiceResponseMatchers.containsAttribute;
import static com.elitecore.aaa.EliteAAAMatchers.RadServiceResponseMatchers.rejectMessage;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.EliteAAAMatchers;
import com.elitecore.aaa.core.conf.impl.DummyAAAServerConfigurationImpl;
import com.elitecore.aaa.core.conf.impl.RadiusPolicyDetail;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.data.ClientTypeConstant;
import com.elitecore.aaa.core.policies.accesspolicy.AccessDeniedException;
import com.elitecore.aaa.core.policies.accesspolicy.AccessPolicyManager;
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.drivers.RadDBAuthDirver;
import com.elitecore.aaa.radius.policies.radiuspolicy.InMemoryRadiusPolicyManager;
import com.elitecore.aaa.radius.policies.radiuspolicy.RadiusPolicyManager;
import com.elitecore.aaa.radius.policies.radiuspolicy.RadiusPolicyManagerSpy;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyData;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthorizationHandlerData;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.aaa.radius.subscriber.RadiusSubscriberProfileRepository;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.UpdateAccountDataFailedException;
import com.elitecore.core.serverx.policies.ManagerInitialzationException;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class AuthorizationHandlerTest {

	private static final String ANY_NON_ACTIVE_STATUS_TOKEN = "any";
	private static final long THREE_DAYS_IN_MILLIS = TimeUnit.DAYS.toMillis(3);
	private static final long TWO_DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(2);

	private RadAuthRequest request;
	private RadAuthResponse response;
	private AuthorizationHandler handler;
	private RadiusPolicyDetail radiusPolicyDetail;
	private RadiusPolicyManagerSpy policyManagerSpy;
	private TimeSource timeSource;
	private AccountData accountData = new AccountData();
	private AuthorizationHandlerData authorizationHandlerData = new AuthorizationHandlerData();
	private AccessPolicyManager accessPolicyManager = spy(AccessPolicyManager.getInstance());
	private DummyAAAServerConfigurationImpl dummyAAAServerConfiguration = new DummyAAAServerConfigurationImpl();

	@Mock private RadDBAuthDirver  baseAuthDriver;
	@Mock private RadAuthServiceContext radAuthServiceContext;
	@Mock private RadiusSubscriberProfileRepository spr;
	@Mock private AAAServerContext serverContext;
	@Mock private RadClientData radClientData;

	@Rule public ExpectedException expectedException = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() {
		RadiusDictionaryTestHarness.getInstance();
	}

	@Before
	public void setup() throws ManagerInitialzationException {
		MockitoAnnotations.initMocks(this);
		RadAuthRequestBuilder radAuthRequestBuilder = new RadAuthRequestBuilder();
		request = radAuthRequestBuilder.buildRequest();
		response = radAuthRequestBuilder.buildResponse();
		request.setAccountData(accountData);
		response.setClientData(radClientData);

		RadiusPolicyManager policyManager = new InMemoryRadiusPolicyManager(RadiusConstants.RADIUS_AUTHORIZATION_POLICY);
		policyManager.initCache(serverContext, false);
		policyManagerSpy = RadiusPolicyManagerSpy.create(policyManager);

		RadiusServicePolicyData radiusServicePolicyData = new RadiusServicePolicyData();
		radiusServicePolicyData.setName("TEST_POLICY");
		authorizationHandlerData.setRadiusServicePolicyData(radiusServicePolicyData);

		radiusPolicyDetail = new RadiusPolicyDetail();
		radiusPolicyDetail.setRejectOnRejectItemNotFound(false);
		radiusPolicyDetail.setRejectOnCheckItemNotFound(true);
		radiusPolicyDetail.setAcceptOnPolicyOnFound(false);

		authorizationHandlerData.setRadiusPolicy(radiusPolicyDetail);

		timeSource = new FixedTimeSource(THREE_DAYS_IN_MILLIS);

		handler = new AuthorizationHandler(radAuthServiceContext, authorizationHandlerData,
				policyManagerSpy.spiedPolicyManager, accessPolicyManager, timeSource);

		handler.setSubscriberProfileRepository(spr);


		when(radAuthServiceContext.getServerContext()).thenReturn(serverContext);
		when(serverContext.getServerConfiguration()).thenReturn(dummyAAAServerConfiguration);
	}

	@Test
	public void isAlwaysEligible() {
		assertTrue(handler.isEligible(request, response));
	}

	@Test
	public void alwaysInitalizesSuccessfully() {
		assertNotNull("handler object should be non null",handler);
		assertNotNull("Policy name present in handler should not be null", authorizationHandlerData.getPolicyName());
		try {
			handler.init();
		} catch (InitializationFailedException e) {
			fail("No exception should have been thrown.");
		}
	}

	@Test
	public void givenAccountProviderIsNotAliveDefaultResponseBehaviorIsApplicable() {
		Mockito.when(spr.isAlive()).thenReturn(false);

		assertThat(handler.isResponseBehaviorApplicable(), is(true));
	}

	@Test
	public void givenAccountProviderIsAliveDefaultBehaviorIsNotApplied() {
		Mockito.when(spr.isAlive()).thenReturn(true);

		assertThat(handler.isResponseBehaviorApplicable(), is(false));
	}

	public class AccountDataLocation {

		@Test
		public void doesNotFetchAccountDataFromSPRIfAlreadyLocated() {
			request.setAccountData(new AccountData());

			requestIsReceived();

			verify(spr, never()).getAccountData(request, response);
		}

		@Test
		public void triesToFetchTheSameFromDiameterSubscriberProfileRepository() {
			request.setAccountData(null);

			requestIsReceived();

			verify(spr,times(1)).getAccountData(request, response);
		}

		@Test
		public void authorizationFailsIfAccountDataIsNotFound() {
			request.setAccountData(null);

			requestIsReceived();

			verifyAccessRejectWithReplyMessage(AuthReplyMessageConstant.USER_NOT_FOUND);		
		}
	}

	public class BasicAuthorizationChecks {

		@Test
		public void subscriberAuthorizationFailsIfCreditLimitIsExceeded() {
			accountData.setCreditLimitCheckRequired(true);
			accountData.setCreditLimit(0);

			requestIsReceived();

			verifyAccessRejectWithReplyMessage(AuthReplyMessageConstant.CREDIT_LIMIT_EXCEEDED);
		}

		public class SubscriberAccountActiveMarkeres {

			@Test
			public void canBeDefinedActiveStatusToken() {
				assertEquals(CommonConstants.STATUS_ACTIVE, "ACTIVE");

				accountData.setAccountStatus(CommonConstants.STATUS_ACTIVE);

				requestIsReceived();

				verifyAccessAccept();
			}

			@Test
			public void canBeCaseInsensitiveActiveStatusToken() {
				accountData.setAccountStatus(CommonConstants.STATUS_ACTIVE.toLowerCase());

				requestIsReceived();

				verifyAccessAccept();
			}

			@Test
			public void canBeUppercaseLetterA() {
				accountData.setAccountStatus("A");

				requestIsReceived();

				verifyAccessAccept();
			}

			@Test 
			public void canBeLowercaseLetterA() {
				accountData.setAccountStatus("a");

				requestIsReceived();

				verifyAccessAccept();
			}
		}


		@Test
		public void subscriberAuthorizationFailsIfAccountIsInactive() {
			accountData.setAccountStatus(ANY_NON_ACTIVE_STATUS_TOKEN);

			requestIsReceived();

			verifyAccessRejectWithReplyMessage(AuthReplyMessageConstant.ACCOUNT_NOT_ACTIVE);
		}

		public class AccountIsActive {

			@Before
			public void setUp() {
				accountData.setAccountStatus(CommonConstants.STATUS_ACTIVE);
			}

			@Test
			public void creditLimitCheckSucceedsIfCreditLimitIsNotPresent()  {
				givenCreditLimitIsNotConfigured();

				requestIsReceived();

				verifyAccessAccept();
			}

			@Test
			public void creditLimitCheckSucceedsIfCreditLimitIsNotExceeded() {
				accountData.setCreditLimitCheckRequired(true);
				accountData.setCreditLimit(1);

				requestIsReceived();

				verifyAccessAccept();
			}

			public class AccountExpiryChecks {
				public class AccountIsExpired {

					@Test
					public void subscriberAuthorizationFailsIfAccountExpired() {
						accountData.setExpiryDate(expiredDate());

						requestIsReceived();

						verifyAccessRejectWithReplyMessage(AuthReplyMessageConstant.ACCOUNT_EXPIRED);
					}

					public class GracePolicyIsConfigured {

						private static final String GRACEPOLICYONE = "graceOne";
						private static final String GRACEPOLICYTWO = "graceTwo";

						@Before
						public void setup() throws ManagerInitialzationException {
							accountData.setExpiryDate(expiredDate());

							dummyAAAServerConfiguration.setGracePolicy(GRACEPOLICYONE, new int[] {1, 2});
							dummyAAAServerConfiguration.setGracePolicy(GRACEPOLICYTWO, new int[] {2, 3});
						}

						public class AtProfileLevel {

							@Test
							public void authorizationSuccedsIfGraceIsSuccessfullyApplied() {
								accountData.setGracePolicy(GRACEPOLICYONE);

								requestIsReceived();

								verifyAccessAccept();
							}

							@Test
							public void graceTypeAttributeWithSelectedSlotIsAddedInRequestOnSuccessfulGrace() {
								accountData.setGracePolicy(GRACEPOLICYONE);

								requestIsReceived();

								verifyAccessAccept();

								assertThat(request,EliteAAAMatchers.RadServiceRequestMatchers.
										containsAttribute("" + RadiusConstants.ELITECORE_VENDOR_ID +
												":" + RadiusAttributeConstants.ELITE_GRACE_TYPE, "1"));

							}

							@Test
							public void authorizationFailsIfNumberOfDaysSinceExpiryExccedsGivenSlots() {
								accountData.setGracePolicy(GRACEPOLICYONE);

								accountData.setExpiryDate(expiryDateExceedingAnyGraceSlot());


								requestIsReceived();

								verifyAccessRejectWithReplyMessage(AuthReplyMessageConstant.ACCOUNT_EXPIRED);
							}
						}

						public class AtHandlerLevel {

							@Test
							public void authorizationSuccedsIfGracePolicyIsSuccessfullyApplied() {
								handlerWithGracePolicy(GRACEPOLICYONE);

								requestIsReceived();

								verifyAccessAccept();
							}

							@Test
							public void graceTypeAttributeWithSelectedSlotIsAddedInRequestOnSuccessfulGrace() {
								handlerWithGracePolicy(GRACEPOLICYONE);

								requestIsReceived();

								verifyAccessAccept();

								assertThat(request,EliteAAAMatchers.RadServiceRequestMatchers.
										containsAttribute("" + RadiusConstants.ELITECORE_VENDOR_ID +
												":" + RadiusAttributeConstants.ELITE_GRACE_TYPE, "1"));
							}

							@Test
							public void authorizationFailsIfNumberOfDaysSinceExpiryExccedsGivenSlots() {
								handlerWithGracePolicy(GRACEPOLICYONE);

								accountData.setExpiryDate(expiryDateExceedingAnyGraceSlot());

								requestIsReceived();

								verifyAccessRejectWithReplyMessage(AuthReplyMessageConstant.ACCOUNT_EXPIRED);
							}
						}

						/* 
						 * The account in the following test case is expired by two days,
						 * Grace period values for 
						 * 							gracePolicyOne = 1,2
						 * 							gracePolicyTwo = 2,3
						 * 
						 * Both the policies are bound and both will be successful as number of
						 * expired days is within their slot just on different indexes.
						 * 
						 * If gracePolicyOne is applied, then EC_GRACE_TYPE = 2
						 * If gracePolicyTwo is applied, then EC_GRACE_TYPE = 1
						 * 
						 * as preference is given to grace policy bound in account data first, which 
						 * is gracePolicyTwo in this test case, the AVP EC_GRACE_TYPE will have value = 1
						 * 
						 * */

						@Test 
						public void gracePolicyAtSubscriberProfileLevelHasHigherPriorityThanHandlerLevel() {
							accountData.setExpiryDate(sinceTwoDays());

							handlerWithGracePolicy(GRACEPOLICYONE);
							accountData.setGracePolicy(GRACEPOLICYTWO);

							requestIsReceived();

							verifyAccessAccept();

							assertThat(request, EliteAAAMatchers.RadServiceRequestMatchers.
									containsAttribute("" + RadiusConstants.ELITECORE_VENDOR_ID +
											":" + RadiusAttributeConstants.ELITE_GRACE_TYPE, "1"));
						}
					}
				}

				@Test
				public void expiryCheckSucceedsIfAccountIsNotExpired() {
					accountData.setExpiryDate(validDate());

					requestIsReceived();

					verifyAccessAccept();
				}

				@Test
				public void expiryCheckSucceedsIfExpiryDateIsNotRead() {
					accountData.setExpiryDate((Date)null);

					requestIsReceived();

					verifyAccessAccept();
				}
			}
			private void givenCreditLimitIsNotConfigured() {
				accountData.setCreditLimit(0);
				accountData.setCreditLimitCheckRequired(false);
			}

			private Date expiredDate() {
				return new Date(timeSource.currentTimeInMillis() - 10000);
			}

			private Date sinceTwoDays() {
				return new Date(timeSource.currentTimeInMillis() - TWO_DAY_IN_MILLIS);
			}

			private Date expiryDateExceedingAnyGraceSlot() {
				return new Date(timeSource.currentTimeInMillis() - THREE_DAYS_IN_MILLIS);
			}

			private Date validDate() {
				return new Date(timeSource.currentTimeInMillis() + 1000000);
			}

			private void handlerWithGracePolicy(String policyName) {
				authorizationHandlerData.setGracePolicy(policyName);
			}
		}
	}


	public class AccountLevelPolicyChecks {


		private static final String VALUE = "SOME_VALUE";
		private static final String SAME_VALUE = "SOME_VALUE";
		private static final String DIFFERERNT_VALUE = "DIFFERENT_VALUE";

		@Before
		public void setup() {
			givenBasicAuthorizationChecksSucceed();
		}

		public class CallingStationIdIsConfiguredInSubscriberProfile {

			@Before
			public void setup() {
				accountData.setCallingStationId(VALUE);
			}

			@Test
			public void authorizationFailsIfCallingStationIdInRequestHasDifferentValue() throws InvalidAttributeIdException {
				request = new RadAuthRequestBuilder()
						.addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, DIFFERERNT_VALUE)
						.buildRequest();

				request.setAccountData(accountData);

				requestIsReceived();

				verifyAccessRejectWithReplyMessage(AuthReplyMessageConstant.INVALID_CALLING_STATION_ID);
			}

			@Test
			public void checkSucceedsIfRequestHasCallingStationIdWithSameValue() throws InvalidAttributeIdException {
				request = new RadAuthRequestBuilder()
						.addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, SAME_VALUE)
						.buildRequest();

				request.setAccountData(accountData);

				requestIsReceived();

				verifyAccessAccept();
			}

			@Test
			public void checkSucceedsIfCallingStationIdIsNotPresentInRequestAndRejectOnCheckItemNotFoundIsDisabled() {
				authorizationHandlerData.getRadiusPolicy().setRejectOnCheckItemNotFound(false);

				requestIsReceived();

				verifyAccessAccept();
			}

			@Test
			public void authorizationFailsIfCallingStationIdIsNotPresentInRequestAndRejectOnCheckItemNotFoundIsEnabled() {
				authorizationHandlerData.getRadiusPolicy().setRejectOnCheckItemNotFound(true);

				requestIsReceived();

				verifyAccessRejectWithReplyMessage(AuthReplyMessageConstant.CALLING_STATION_ID_NOT_FOUND);
			}
		}

		public class CalledStationIdIsConfiguredInSubscriberProfile {

			@Before
			public void setup() {
				accountData.setCalledStationId(VALUE);
			}

			@Test
			public void authorizationFailsIfCalledStationIdInRequestHasDifferentValue() throws InvalidAttributeIdException {
				request = new RadAuthRequestBuilder()
						.addAttribute(RadiusAttributeConstants.CALLED_STATION_ID_STR, DIFFERERNT_VALUE)
						.buildRequest();

				request.setAccountData(accountData);

				requestIsReceived();

				verifyAccessRejectWithReplyMessage(AuthReplyMessageConstant.INVALID_CALLED_STATION_ID);
			}

			@Test
			public void checkSucceedsIfRequestHasCalledStationIdWithSameValue() throws InvalidAttributeIdException {
				request = new RadAuthRequestBuilder()
						.addAttribute(RadiusAttributeConstants.CALLED_STATION_ID_STR, SAME_VALUE)
						.buildRequest();

				request.setAccountData(accountData);

				requestIsReceived();

				verifyAccessAccept();
			}

			@Test
			public void checkSucceedsIfCalledStationIdIsNotPresentInRequestAndRejectOnCheckItemNotFoundIsDisabled() {
				authorizationHandlerData.getRadiusPolicy().setRejectOnCheckItemNotFound(false);

				requestIsReceived();

				verifyAccessAccept();
			}

			@Test
			public void authorizationFailsIfCalledStationIdIsNotPresentInRequestAndRejectOnCheckItemNotFoundIsEnabled() {
				authorizationHandlerData.getRadiusPolicy().setRejectOnCheckItemNotFound(true);

				requestIsReceived();

				verifyAccessRejectWithReplyMessage(AuthReplyMessageConstant.CALLED_STATION_ID_NOT_FOUND);
			}
		}

		public class ServiceTypeIsMappedInConfiguredSubscriberProfile {

			private final String ASYNC = "1";
			private final String SYNC = "2";

			@Before
			public void setup() {
				accountData.setServiceType(ASYNC);
			}

			@Test
			public void giveNasPortTypeIsNotPresentInRequestAndRejectOnCheckItemNotFoundIsEnabledAuthorizationFails() {
				assertTrue(authorizationHandlerData.getRadiusPolicy().isRejectOnCheckItemNotFound());

				assertThat(request.getRadiusAttribute(RadiusAttributeConstants.NAS_PORT_TYPE), nullValue());

				requestIsReceived();

				verifyAccessRejectWithReplyMessage(AuthReplyMessageConstant.NAS_PORT_TYPE_NOT_FOUND);
			}

			@Test
			public void checkSuccedsIfNasPortTypeIsNotPresentInRequestAndRejectOnCheckItemNotFoundIsDisabled() {
				authorizationHandlerData.getRadiusPolicy().setRejectOnCheckItemNotFound(false);

				assertThat(request.getRadiusAttribute(RadiusAttributeConstants.NAS_PORT_TYPE), nullValue());

				requestIsReceived();

				verifyAccessAccept();
			}

			@Test
			public void authorizationFailsIfNasPortTypeInRequestHasDifferentValue() throws InvalidAttributeIdException {
				request = new RadAuthRequestBuilder()
						.addAttribute(RadiusAttributeConstants.NAS_PORT_TYPE_STR, SYNC)
						.build();

				request.setAccountData(accountData);

				requestIsReceived();

				verifyAccessRejectWithReplyMessage(AuthReplyMessageConstant.INVALID_NAS_PORT_TYPE);
			}

			@Test
			public void checkSucceedsIfNasPortTypeInRequestHasSameValue() throws InvalidAttributeIdException {
				request = new RadAuthRequestBuilder()
						.addAttribute(RadiusAttributeConstants.NAS_PORT_TYPE_STR, ASYNC)
						.build();

				request.setAccountData(accountData);

				requestIsReceived();

				verifyAccessAccept();
			}

			@Test
			public void dictionaryValueCanBeConfiguredAsServiceType() throws InvalidAttributeIdException {
				accountData.setServiceType(RadiusAttributeValuesConstants.VIRTUAL_STR);

				request = new RadAuthRequestBuilder()
						.addAttribute(RadiusAttributeConstants.NAS_PORT_TYPE_STR,
								RadiusAttributeValuesConstants.VIRTUAL_STR)
						.build();

				request.setAccountData(accountData);

				requestIsReceived();

				verifyAccessAccept();
			}
		}

		@Test
		public void givenVendorTypeIsPortalAccountLevelPolicyChecksAreSkipped() throws InvalidAttributeIdException {
			accountData.setCalledStationId(VALUE);
			accountData.setCallingStationId(VALUE);
			when(radClientData.getVendorType()).thenReturn(ClientTypeConstant.PORTAL.typeId);

			request = new RadAuthRequestBuilder()
					.addAttribute(RadiusAttributeConstants.CALLED_STATION_ID_STR, DIFFERERNT_VALUE)
					.addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, DIFFERERNT_VALUE)
					.buildRequest();


			request.setAccountData(accountData);

			requestIsReceived();

			verifyAccessAccept();
		}
	}

	public class RadiusPolicy {

		@Before
		public void setup() {
			givenBasicAuthorizationChecksSucceed();
		}

		public class SubscriberProfileLevelCheckItems {

			@Before
			public void setup() {
				accountData.setCheckItem("0:25=\"addedFromTest\"");
			}

			@Test
			public void authorizationFailsIfRequestDoesNotContainTheCheckItems() {
				requestIsReceived();

				verifyAccessRejectWithReplyMessage("Policy Failed");
			}

			@Test
			public void authorizationSuccedsIfRequestDoesNotContainCheckItemsAndRejectOnCheckItemNotFoundIsDisabled() {
				authorizationHandlerData.getRadiusPolicy().setRejectOnCheckItemNotFound(false);

				requestIsReceived();

				verifyAccessAccept();
			}

			@Test
			public void authorizationSucceedsIfCheckItemsArePresentInTheRequestAndCheckItemSucceeds() throws InvalidAttributeIdException {
				request = new RadAuthRequestBuilder()
						.addAttribute(RadiusAttributeConstants.CLASS_STR, "addedFromTest")
						.buildRequest();

				request.setAccountData(accountData);

				requestIsReceived();

				verifyAccessAccept();
			}

			@Test 
			public void authorizationFailsIfValueOfCheckItemPresentInRequestDoesNotMatch() throws InvalidAttributeIdException {
				request = new RadAuthRequestBuilder()
						.addAttribute(RadiusAttributeConstants.CLASS_STR, "someRandomValue")
						.buildRequest();

				request.setAccountData(accountData);

				requestIsReceived();

				verifyAccessRejectWithReplyMessage("Policy Failed");
			}
		}

		public class SubscriberProfileLevelRejectItems {

			@Before
			public void setup() {
				accountData.setRejectItem("0:25=\"rejectItemValue\"");
			}

			@Test
			public void authorizationFailsIfRejectItemExpressionMatches() throws InvalidAttributeIdException {
				request = new RadAuthRequestBuilder()
						.addAttribute(RadiusAttributeConstants.CLASS_STR, "rejectItemValue")
						.buildRequest();

				request.setAccountData(accountData);


				requestIsReceived();

				verifyAccessRejectWithReplyMessage("Policy Failed");
			}

			@Test
			public void authorizationSucceedsIfRejectItemIsNotPresentInRequestAndRejectOnRejectItemNotFoundIsDisabled() {
				authorizationHandlerData.getRadiusPolicy().setRejectOnRejectItemNotFound(false);

				requestIsReceived();

				verifyAccessAccept();
			}

			@Test
			public void authorizationFailsIfRejectItemIsNotPresentInRequestAndRejectOnRejectItemNotFoundIsEnabled() {
				authorizationHandlerData.getRadiusPolicy().setRejectOnRejectItemNotFound(true);

				requestIsReceived();

				verifyAccessRejectWithReplyMessage("Policy Failed");
			}
		}

		@Test
		public void nameOfAppliedAuthorizationPoliciesIsAddedInResponsePacket() throws ParserException, PolicyFailedException {
			accountData.setAuthorizationPolicy("someAuthorizationPolicy");

			policyManagerSpy.simulatePolicyApplicationResult("(someAuthorizationPolicy)", "someAuthorizationPolicy");

			requestIsReceived();

			verifyAccessAccept();

			assertThat(response, EliteAAAMatchers.RadServiceResponseMatchers.
					containsAttribute(RadiusConstants.ELITECORE_VENDOR_ID +
							":" + RadiusAttributeConstants.ELITE_SATISFIED_POLICIES, "someAuthorizationPolicy"));
		}

		@Test
		public void authorizationFailsGivenAuthorizationPolicyFails() throws ParserException, PolicyFailedException {
			accountData.setAuthorizationPolicy("someAuthorizationPolicy");

			policyManagerSpy.simulateFailedPolicy("someAuthorizationPolicy");

			requestIsReceived();

			verifyAccessRejectWithReplyMessage("Policy Failed");
		}

		@Test
		public void authorizationFailsGivenParsingOfAuthorizationPolicyFails() throws ParserException, PolicyFailedException {
			accountData.setAuthorizationPolicy("someAuthorizationPolicy");

			policyManagerSpy.simulateParsingFailureOf("someAuthorizationPolicy");

			requestIsReceived();

			verifyAccessRejectWithReplyMessage("some message");
		}

		@Test
		public void givenAPolicyGroupIsConfiguredAllAppliedAuthorizationPoliciesAreAddedInResponsePacket() throws ParserException, PolicyFailedException {
			accountData.setAuthorizationPolicyGroup("authorizationPolicyGroup");

			doReturn("policyOne && policyTwo").when(policyManagerSpy.spiedPolicyManager).getExpressionFrom("authorizationPolicyGroup");

			policyManagerSpy.simulatePolicyApplicationResult("( policyOne && policyTwo ) ", "policyOne","&&","policyTwo");

			requestIsReceived();

			verifyAccessAccept();

			assertThat(response, EliteAAAMatchers.RadServiceResponseMatchers.
					containsAttribute(RadiusConstants.ELITECORE_VENDOR_ID +
							":" + RadiusAttributeConstants.ELITE_SATISFIED_POLICIES, "policyOne,&&,policyTwo"));
		}

		@Test
		public void nameOfAdditionalPoliciesIsAddedInResponsePacket() throws ParserException, PolicyFailedException {
			accountData.setAdditionalPolicy("someAdditionalPolicy");

			policyManagerSpy.simulatePolicyApplicationResult("(someAdditionalPolicy)", "someAdditionalPolicy");

			requestIsReceived();

			verifyAccessAccept();

			assertThat(response, EliteAAAMatchers.RadServiceResponseMatchers.
					containsAttribute(RadiusConstants.ELITECORE_VENDOR_ID +
							":" + RadiusAttributeConstants.ELITE_SATISFIED_POLICIES, "*,someAdditionalPolicy"));
		}

		@Test
		public void authorizaionSucceedsEvenIfAdditionalPolicyFails() throws ParserException, PolicyFailedException {
			accountData.setAdditionalPolicy("someAdditionalPolicy");

			policyManagerSpy.simulateFailedPolicy("someAdditionalPolicy");

			requestIsReceived();

			verifyAccessAccept();
		}
	}

	public class DynamicCheckItem {

		private static final String ANY_ID = "0";

		@Before
		public void setup() throws InvalidAttributeIdException {
			givenBasicAuthorizationChecksSucceed();

			accountData.setDynamicCheckItems("0:31=[*]");

			request = new RadAuthRequestBuilder()
					.addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, "machineOne")
					.buildRequest();

			request.setAccountData(accountData);
			request.setParameter(AAAServerConstants.DYNAMIC_CHECK_ITEM_DRIVER_INSTANCE_ID, ANY_ID);
		}

		@Test
		public void givenDynamicCheckItemIsConfiguredCallIsMadeToUpdateAccountData() throws UpdateAccountDataFailedException, InvalidAttributeIdException {

			when(radAuthServiceContext.getDriver(ANY_ID)).thenReturn(baseAuthDriver);

			requestIsReceived();

			verify(baseAuthDriver, times(1)).setDynamicCheck(anyString(), anyString());
		}

		@Test
		public void evenIfCallMadeToUpdataDynamicCheckItemInAccountDataFailsAuthorizationSucceds() throws UpdateAccountDataFailedException {

			doReturn(baseAuthDriver).when(
					radAuthServiceContext).getDriver(ANY_ID);

			doThrow(UpdateAccountDataFailedException.class).when(
					baseAuthDriver).setDynamicCheck(anyString(), anyString());

			requestIsReceived();

			verifyAccessAccept();
		}

		@Test
		public void  authorizationFailsAfterConfiguredNumberOfWildCardRequestsHasBeenAccepted() throws UpdateAccountDataFailedException {
			doReturn(baseAuthDriver).when(
					radAuthServiceContext).getDriver(ANY_ID);

			requestIsReceived();

			verify(baseAuthDriver, times(1)).setDynamicCheck(anyString(), anyString());

			verifyAccessAccept();

			IRadiusAttribute radiusAttribute = request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR);
			radiusAttribute.setStringValue("machineTwo");

			requestIsReceived();

			verify(baseAuthDriver, times(1)).setDynamicCheck(anyString(), anyString());

			verifyAccessRejectWithReplyMessage("Policy Failed");

		}

		@Test
		public void givenDynamicCheckItemsConfiguredInSubscriberProfileAreInWrongFormatDynamicCheckAvpIsIgnored() throws UpdateAccountDataFailedException {
			accountData.setDynamicCheckItems("0:31 \"machineone\"");


			request = new RadAuthRequestBuilder().buildRequest();
			request.setAccountData(accountData);
			request.setParameter(AAAServerConstants.DYNAMIC_CHECK_ITEM_DRIVER_INSTANCE_ID, ANY_ID);

			doReturn(baseAuthDriver).when(
					radAuthServiceContext).getDriver(ANY_ID);

			requestIsReceived();

			verify(baseAuthDriver, never()).setDynamicCheck(anyString(), anyString());		
		}
	}

	public class AccessPolicyIsConfiguredInSubscriberProfile {

		@Before
		public void setup() {
			givenBasicAuthorizationChecksSucceed();
			accountData.setAccessPolicy("someAccessPolicy");
		}

		@Test
		public void authorizationFailsIfAccessDeniedExceptionIsThrownWhileApplyingAccessPolicy() throws AccessDeniedException {
			AccessDeniedException accessDeniedException = new AccessDeniedException("some message");
			doThrow(accessDeniedException).when(accessPolicyManager).checkAccessPolicy(
					"someAccessPolicy", false);
			requestIsReceived();

			verifyAccessRejectWithReplyMessage("Access denied by Access Policy Manager. Reason: " 
					+ accessDeniedException.getMessage() );
		}

		@Test
		public void authorizationSucceedsIfAccessPolicyIsSuccessfulyApplied() throws AccessDeniedException {
			doReturn(2000l).when(accessPolicyManager).checkAccessPolicy("someAccessPolicy", false);

			requestIsReceived();

			verifyAccessAccept();
		}

		public class GivenSessionTimeoutIsPresentInResponse {

			@Before
			public void setup() throws InvalidAttributeIdException {
				response = new RadAuthRequestBuilder()
						.addAttribute(RadiusAttributeConstants.SESSION_TIMEOUT_STR, "1000")
						.buildResponse();
				response.setClientData(radClientData);

			}

			@Test
			public void ifSessionTimeoutCalculatedByAccessPolicyIsSmallerItIsUpdated() throws AccessDeniedException {

				response.getRadiusAttribute(RadiusAttributeConstants.SESSION_TIMEOUT).setLongValue(1000l);

				doReturn(500l).when(accessPolicyManager).checkAccessPolicy("someAccessPolicy", false);

				requestIsReceived();

				verifyAccessAccept();

				assertThat(response, EliteAAAMatchers.RadServiceResponseMatchers.
						containsAttribute(RadiusAttributeConstants.SESSION_TIMEOUT_STR, "500"));
			}

			@Test
			public void givenSessionTimeoutCalculatedByAccessPolicyIsLargerItIsNotUpdated() throws AccessDeniedException {
				response.getRadiusAttribute(true, RadiusAttributeConstants.SESSION_TIMEOUT).setLongValue(1000l);

				doReturn(2000l).when(accessPolicyManager).checkAccessPolicy("someAccessPolicy", false);

				requestIsReceived();

				verifyAccessAccept();

				assertThat(response, EliteAAAMatchers.RadServiceResponseMatchers.
						containsAttribute(RadiusAttributeConstants.SESSION_TIMEOUT_STR, "1000"));
			}
		}

		@Test
		public void givenSessionTimeoutAvpIsNotPresentInResponseItIsAdded() throws AccessDeniedException {

			assertEquals(null, response.getRadiusAttribute(RadiusAttributeConstants.SESSION_TIMEOUT_STR));

			doReturn(500l).when(accessPolicyManager).checkAccessPolicy("someAccessPolicy", false);

			requestIsReceived();

			verifyAccessAccept();

			assertThat(response, EliteAAAMatchers.RadServiceResponseMatchers.
					containsAttribute(RadiusAttributeConstants.SESSION_TIMEOUT_STR, "500"));
		}

	}


	public class ClientPolicy {

		private final String CLIENT_POLICY = "CLIENT_POLICY";

		@Before
		public void setup() {
			givenBasicAuthorizationChecksSucceed();	

			when(radClientData.getClientPolicy()).thenReturn(CLIENT_POLICY);
		}

		@Test
		public void ifAppliedSuccefullyAreAddedInRequestAsAParameter() throws Exception {

			policyManagerSpy.simulatePolicyWithPortalCheckItemApplicationResult(CLIENT_POLICY, CLIENT_POLICY);

			requestIsReceived();

			verifyAccessAccept();

			List<String> satisfiedClientPolicies = (List<String>) request.getParameter(AAAServerConstants.SATISFIED_CLIENT_POLICIES);

			assertEquals(CLIENT_POLICY , satisfiedClientPolicies.get(0));
		}

		@Test
		public void authorizationFailsGivenClientPolicyFails() throws Exception {

			policyManagerSpy.simualteFailureOfPortalCheckItemPolicy(CLIENT_POLICY);

			requestIsReceived();

			verifyAccessRejectWithReplyMessage("Policy Failed");
		}

		@Test
		public void authorizationFailsGivenClientPolicyParsingFails() throws Exception {
			policyManagerSpy.simualteParsingFailureOfPortalCheckItemPolicy(CLIENT_POLICY);

			requestIsReceived();

			verifyAccessRejectWithReplyMessage("some message");
		}
	}

	public class HandlerLevelSessionTimeOut {

		private final int POSITIVE_SESSION_TIMEOUT = 1000;
		private final String POSITIVE_SESSION_TIMEOUT_STR = String.valueOf(POSITIVE_SESSION_TIMEOUT);
		private final int NEGATIVE_SESSION_TIMEOUT = -1;

		@Before
		public void setup() {
			givenBasicAuthorizationChecksSucceed();
		}

		@Test
		public void givenPositiveValueIsConfiguredItIsAddedInResponse() {

			authorizationHandlerData.setDefaultSessionTimeout(POSITIVE_SESSION_TIMEOUT);

			assertThat(response, not(containsAttribute(RadiusAttributeConstants.SESSION_TIMEOUT_STR)));

			requestIsReceived();

			assertThat(response, containsAttribute(
					RadiusAttributeConstants.SESSION_TIMEOUT_STR,
					POSITIVE_SESSION_TIMEOUT_STR));
		}

		@Test
		public void givenSessionTimeOutIsZeroNoSessionTimeIsAddedInResponse() {
			authorizationHandlerData.setDefaultSessionTimeout(0);

			requestIsReceived();

			assertThat(response, not(containsAttribute(RadiusAttributeConstants.SESSION_TIMEOUT_STR)));
		}

		@Test
		public void hintsSystemNotToSendSessionTimeOutIfValueOfDefaultSessionTimeOutIsNegative() {
			authorizationHandlerData.setDefaultSessionTimeout(NEGATIVE_SESSION_TIMEOUT);

			requestIsReceived();

			assertThat(response, not(containsAttribute(RadiusAttributeConstants.SESSION_TIMEOUT_STR)));

			assertEquals(AAAServerConstants.SESSION_TIMEOUT_DISABLED,
					request.getParameter(AAAServerConstants.SESSION_TIMEOUT));

		}

		// FIXME Report to QA, Remove this only after a bug is raised
		@Test
		public void doesNotUpdateSessionTimeoutToBeMinimumOfSessionTimeoutAndAccountValidity() {

			accountData.setExpiryDate(new Date(smallerTimeValue()));

			authorizationHandlerData.setDefaultSessionTimeout(largerTimeValue());

			requestIsReceived();

			assertThat(response, containsAttribute(
					RadiusAttributeConstants.SESSION_TIMEOUT_STR,
					largerTimeValueStr())
					);
		}

		private int smallerTimeValue() {
			return (int) timeSource.currentTimeInMillis();
		}

		private int largerTimeValue() {
			return (int) timeSource.currentTimeInMillis() + 1000;
		}

		private String largerTimeValueStr() {
			return (int) timeSource.currentTimeInMillis() + 1000 + "";
		}

	}

	public class ServiceTypeIsAuthorizeOnly {

		@Before
		public void setup() throws InvalidAttributeIdException {
			request = new RadAuthRequestBuilder()
					.addAttribute(
							RadiusAttributeConstants.SERVICE_TYPE_STR,
							RadiusAttributeValuesConstants.AUTHORIZE_ONLY_STR)
					.build();

			request.setAccountData(accountData);


			givenBasicAuthorizationChecksSucceed();

		}

		@Test
		public void serviceTypeIsAddedInResponse() {
			requestIsReceived();

			assertThat(response, containsAttribute(
					RadiusAttributeConstants.SERVICE_TYPE_STR,
					RadiusAttributeValuesConstants.AUTHORIZE_ONLY_STR)
					);
		}

		@Test
		public void givenStateAttributeIsPresentInRequestItIsCopiedInResponse() throws InvalidAttributeIdException {
			request = new RadAuthRequestBuilder()
					.addAttribute(
							RadiusAttributeConstants.SERVICE_TYPE_STR,
							RadiusAttributeValuesConstants.AUTHORIZE_ONLY_STR)
					.addAttribute(RadiusAttributeConstants.STATE_STR, "some value")
					.build();

			request.setAccountData(accountData);

			requestIsReceived();

			assertThat(response, containsAttribute(
					RadiusAttributeConstants.STATE_STR, "some value")
					);
		}
	}

	private void verifyAccessAccept() {
		assertThat(response, acceptMessage());
	}

	private void verifyAccessRejectWithReplyMessage(String replyMessage) {

		assertThat(response, rejectMessage());
		assertThat(response, containsAttribute(
				RadiusAttributeConstants.REPLY_MESSAGE_STR,
				replyMessage));
	}

	private void requestIsReceived() {
		handler.handleRequest(request, response, null);
	}

	private void givenBasicAuthorizationChecksSucceed() {
		accountData.setCreditLimit(100);
		accountData.setCreditLimitCheckRequired(true);

		accountData.setExpiryDate(validDate());
		accountData.setExpiryDateCheckRequired(true);

		accountData.setAccountStatus(CommonConstants.STATUS_ACTIVE);
	}

	private Date validDate() {
		return new Date(timeSource.currentTimeInMillis() + 1000000);
	}
}
