package com.elitecore.aaa.diameter.service.application.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.conf.impl.DummyAAAServerConfigurationImpl;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.policies.accesspolicy.AccessDeniedException;
import com.elitecore.aaa.core.policies.accesspolicy.AccessPolicyManager;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.diameter.policies.diameterpolicy.DiameterPolicyManager;
import com.elitecore.aaa.diameter.policies.diameterpolicy.DiameterPolicyManagerSpy;
import com.elitecore.aaa.diameter.policies.diameterpolicy.InMemoryDiameterPolicyManager;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.DiameterServiceContextStub;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.drivers.DiameterDbDriver;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthorizationHandlerData;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepository;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepositoryDetails;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.drivers.UpdateAccountDataFailedException;
import com.elitecore.core.serverx.policies.ManagerInitialzationException;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class DiameterAuthorizationHandlerTest {
	private static final String ANY_NON_ACTIVE_STATUS_TOKEN = "any";

	private static final long THREE_DAYS_IN_MILLIS = TimeUnit.DAYS.toMillis(3);
	private static final long TWO_DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(2);

	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	
	@Mock private DiameterDbDriver baseAuthDriver;
	@Mock private AAAServerContext aaaServerContext;
	private DiameterAuthorizationHandler<ApplicationRequest, ApplicationResponse> handler;
	private DiameterAuthorizationHandlerData handlerData = new DiameterAuthorizationHandlerData();
	private DiameterServiceContextStub diameterServiceContextStub = spy(new DiameterServiceContextStub());
	private DiameterPolicyManagerSpy policyManagerSpy;
	private DummyAAAServerConfigurationImpl dummyAAAServerConfiguration = new DummyAAAServerConfigurationImpl();
	private AccessPolicyManager accessPolicyManager = spy(AccessPolicyManager.getInstance());

	private DiameterSubscriberProfileRepositoryDetails diameterSubscriberProfileRepositoryDetails = new DiameterSubscriberProfileRepositoryDetails();
		
	private DiameterSubscriberProfileRepositoryStub accountInfoProvider;

	private ApplicationResponse response;
	private ApplicationRequest request;
	private AccountData accountData;

	@BeforeClass
	public static void setUpBeforeClass() throws ManagerInitialzationException {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() throws ManagerInitialzationException {
		MockitoAnnotations.initMocks(this);
		DiameterPolicyManager policyManager = new InMemoryDiameterPolicyManager(DiameterPolicyManager.DIAMETER_AUTHORIZATION_POLICY);
		policyManager.initCache(aaaServerContext, false);
		policyManagerSpy = DiameterPolicyManagerSpy.create(policyManager);

		DiameterRequest diameterRequest = new DiameterRequest();
		request = new ApplicationRequest(diameterRequest);
		response = new ApplicationResponse(diameterRequest);

		accountData = new AccountData();
		diameterSubscriberProfileRepositoryDetails.setAnonymousProfileIdentity("anonymous");
		accountInfoProvider = spy(new DiameterSubscriberProfileRepositoryStub(
				diameterServiceContextStub, diameterSubscriberProfileRepositoryDetails));
		request.setAccountData(accountData);
		handler = new DiameterAuthorizationHandler<ApplicationRequest, ApplicationResponse>(diameterServiceContextStub,
				handlerData, policyManagerSpy.spiedPolicyManager, accessPolicyManager);
		handler.setSubscriberProfileRepository(accountInfoProvider);
		when(diameterServiceContextStub.getServerContext()).thenReturn(aaaServerContext);
		when(aaaServerContext.getServerConfiguration()).thenReturn(dummyAAAServerConfiguration);
	}
	
	@Test
	public void authorizationHandlerIsAlwaysEligible() {
		assertTrue(handler.isEligible(request, response));
	}
	
	@Test
	public void givenDiameterSPRIsNotAliveResponseBehaviorIsApplicable() {
		when(accountInfoProvider.isAlive()).thenReturn(false);
		
		assertTrue(handler.isResponseBehaviorApplicable());
	}
	
	@Test
	public void givenDiameterSPRIsAliveIsResponseBehaviorIsNotApplicable() {
		when(accountInfoProvider.isAlive()).thenReturn(true);
		
		assertFalse(handler.isResponseBehaviorApplicable());
	}
	
	public class AccountDataLocation {
		
		@Test
		public void doesNotFetchAccountDataFromSPRIfAlreadyLocated() {
			request.setAccountData(new AccountData());
			
			requestIsReceived();
			
			verify(accountInfoProvider, never()).getAccountData(request, response);
		}
		
		@Test
		public void triesToFetchTheSameFromDiameterSubscriberProfileRepository() {
			request.setAccountData(null);
			
			requestIsReceived();
			
			verify(accountInfoProvider,times(1)).getAccountData(request, response);
		}
		
		@Test
		public void triesToFetchAnonymousAccountDataIfNotFoundInSPR() {
			request.setAccountData(null);
			
			requestIsReceived();
			
			verify(accountInfoProvider,times(1)).getAnonymousAccountData(request);
		}
		
		@Test
		public void authorizationFailsIfAccountDataIsNotFound() {
			request.setAccountData(null);
			
			requestIsReceived();
			
			verifySubscriberAuthorizationFailsWithMessage(DiameterErrorMessageConstants.USER_NOT_FOUND);
		}
	}
	
	public class BasicAuthorizationChecks {

		@Test
		public void subscriberAuthorizationFailsIfCreditLimitIsExceeded() {
			accountData.setCreditLimitCheckRequired(true);
			accountData.setCreditLimit(0);

			requestIsReceived();

			verifySubscriberAuthorizationFailsWithMessage(DiameterErrorMessageConstants.CREDIT_LIMIT_EXCEEDED);
		}

		public class SubscriberAccountActiveMarkers {

			@Test
			public void canBeDefinedActiveStatusToken() {
				assertEquals(CommonConstants.STATUS_ACTIVE, "ACTIVE");

				accountData.setAccountStatus(CommonConstants.STATUS_ACTIVE);

				requestIsReceived();

				subscriberAuthorizationSucceeds();
			}

			@Test
			public void canBeCaseInsensitiveActiveStatusToken() {
				accountData.setAccountStatus(CommonConstants.STATUS_ACTIVE.toLowerCase());

				requestIsReceived();

				subscriberAuthorizationSucceeds();
			}

			@Test
			public void canBeUppercaseLetterA() {
				accountData.setAccountStatus("A");

				requestIsReceived();

				subscriberAuthorizationSucceeds();
			}

			@Test 
			public void canBeLowercaseLetterA() {
				accountData.setAccountStatus("a");

				requestIsReceived();

				subscriberAuthorizationSucceeds();
			}
		}

		@Test
		public void subscriberAuthorizationFailsIfAccountIsInactive() {
			accountData.setAccountStatus(ANY_NON_ACTIVE_STATUS_TOKEN);

			requestIsReceived();

			verifySubscriberAuthorizationFailsWithMessage(DiameterErrorMessageConstants.ACCOUNT_NOT_ACTIVE);
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

				subscriberAuthorizationSucceeds();
			}

			@Test
			public void creditLimitCheckSucceedsIfCreditLimitIsNotExceeded() {
				accountData.setCreditLimitCheckRequired(true);
				accountData.setCreditLimit(1);

				requestIsReceived();

				subscriberAuthorizationSucceeds();
			}
			
			public class AccountExpiryChecks {
				public class AccountIsExpired {
					
					@Test
					public void subscriberAuthorizationFailsIfAccountExpired() {
						accountData.setExpiryDate(expiredDate());
						
						requestIsReceived();
						
						verifySubscriberAuthorizationFailsWithMessage(DiameterErrorMessageConstants.ACCOUNT_EXPIRED);
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
								
								when(diameterServiceContextStub.getServerContext()).thenReturn(aaaServerContext);

								requestIsReceived();

								subscriberAuthorizationSucceeds();
							}

							@Test
							public void graceTypeAttributeWithSelectedSlotIsAddedInRequestOnSuccessfulGrace() {
								accountData.setGracePolicy(GRACEPOLICYONE);
								when(diameterServiceContextStub.getServerContext()).thenReturn(aaaServerContext);

								requestIsReceived();

								subscriberAuthorizationSucceeds();

								DiameterAssertion.assertThat(request.getDiameterRequest()).containsAVP(
										DiameterAVPConstants.EC_GRACE_TYPE, "1");
							}
							
							@Test
							public void authorizationFailsIfNumberOfDaysSinceExpiryExccedsGivenSlots() {
								accountData.setGracePolicy(GRACEPOLICYONE);

								accountData.setExpiryDate(expiryDateExceedingAnyGraceSlot());

								when(diameterServiceContextStub.getServerContext()).thenReturn(aaaServerContext);

								requestIsReceived();

								verifySubscriberAuthorizationFailsWithMessage(DiameterErrorMessageConstants.ACCOUNT_EXPIRED);
							}
						}
						
						public class AtHandlerLevel {
							
							@Test
							public void authorizationSuccedsIfGracePolicyIsSuccessfullyApplied() {
								handlerWithGracePolicy(GRACEPOLICYONE);

								when(diameterServiceContextStub.getServerContext()).thenReturn(aaaServerContext);

								requestIsReceived();

								subscriberAuthorizationSucceeds();
							}

							@Test
							public void graceTypeAttributeWithSelectedSlotIsAddedInRequestOnSuccessfulGrace() {
								handlerWithGracePolicy(GRACEPOLICYONE);

								when(diameterServiceContextStub.getServerContext()).thenReturn(aaaServerContext);

								requestIsReceived();

								subscriberAuthorizationSucceeds();

								DiameterAssertion.assertThat(request.getDiameterRequest()).containsAVP(
										DiameterAVPConstants.EC_GRACE_TYPE, "1");
							}
							
							@Test
							public void authorizationFailsIfNumberOfDaysSinceExpiryExccedsGivenSlots() {
								handlerWithGracePolicy(GRACEPOLICYONE);

								accountData.setExpiryDate(expiryDateExceedingAnyGraceSlot());

								when(diameterServiceContextStub.getServerContext()).thenReturn(aaaServerContext);

								requestIsReceived();

								verifySubscriberAuthorizationFailsWithMessage(DiameterErrorMessageConstants.ACCOUNT_EXPIRED);
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

							subscriberAuthorizationSucceeds();

							DiameterAssertion.assertThat(request.getDiameterRequest()).containsAVP(
									DiameterAVPConstants.EC_GRACE_TYPE, "1");
						}
					}
				}

				@Test
				public void expiryCheckSucceedsIfAccountIsNotExpired() {
					accountData.setExpiryDate(validDate());

					requestIsReceived();

					subscriberAuthorizationSucceeds();
				}

				@Test
				public void expiryCheckSucceedsIfExpiryDateIsNotRead() {
					accountData.setExpiryDate((Date)null);

					requestIsReceived();

					subscriberAuthorizationSucceeds();
				}
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
			public void authorizationFailsIfCallingStationIdInRequestHasDifferentValue() {
				request.getDiameterRequest().addAvp(DiameterAVPConstants.CALLING_STATION_ID, DIFFERERNT_VALUE);
				
				requestIsReceived();
				
				verifySubscriberAuthorizationFailsWithMessage(DiameterErrorMessageConstants.INVALID_CALLING_STATION_ID);
			}
			
			@Test
			public void checkSucceedsIfRequestHasCallingStationIdWithSameValue() {
				request.getDiameterRequest().addAvp(DiameterAVPConstants.CALLING_STATION_ID, SAME_VALUE);
				
				requestIsReceived();
				
				subscriberAuthorizationSucceeds();
			}
			
			@Test
			public void checkSucceedsIfCallingStationIdIsNotPresentInRequestAndRejectOnCheckItemNotFoundIsDisabled() {
				handlerData.setRejectOnCheckItemNotFound(false);
				
				requestIsReceived();
				
				subscriberAuthorizationSucceeds();
			}
			
			@Test
			public void authorizationFailsIfCallingStationIdIsNotPresentInRequestAndRejectOnCheckItemNotFoundIsEnabled() {
				handlerData.setRejectOnCheckItemNotFound(true);
				
				requestIsReceived();
				
				verifySubscriberAuthorizationFailsWithMessage(DiameterErrorMessageConstants.CALLING_STATION_ID_NOT_FOUND);
			} 
		}
		
		public class CalledStationIdIsConfiguredInSubscriberProfile {
			
			@Before
			public void setup() {
				accountData.setCalledStationId(VALUE);
			}
			
			@Test
			public void authorizationFailsIfCalledStationIdInRequestHasDifferentValue() {
				request.getDiameterRequest().addAvp(DiameterAVPConstants.CALLED_STATION_ID, DIFFERERNT_VALUE);
				
				requestIsReceived();
				
				verifySubscriberAuthorizationFailsWithMessage(DiameterErrorMessageConstants.INVALID_CALLED_STATION_ID);
			}
			
			@Test
			public void checkSucceedsIfRequestHasCalledStationIdWithSameValue() {
				request.getDiameterRequest().addAvp(DiameterAVPConstants.CALLED_STATION_ID, SAME_VALUE);
				
				requestIsReceived();
				
				subscriberAuthorizationSucceeds();
			}
			
			@Test
			public void checkSucceedsIfCalledStationIdIsNotPresentInRequestAndRejectOnCheckItemNotFoundIsDisabled() {
				handlerData.setRejectOnCheckItemNotFound(false);
				
				requestIsReceived();
				
				subscriberAuthorizationSucceeds();
			}
			
			@Test
			public void authorizationFailsIfCalledStationIdIsNotPresentInRequestAndRejectOnCheckItemNotFoundIsEnabled() {
				handlerData.setRejectOnCheckItemNotFound(true);
				
				requestIsReceived();
				
				verifySubscriberAuthorizationFailsWithMessage(DiameterErrorMessageConstants.CALLED_STATION_ID_NOT_FOUND);
			}
		}
	}
	
	public class DiameterPolicy {
		
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
				
				verifySubscriberAuthorizationFailsWithMessage("Policy Failed");
			}
			
			@Test
			public void authorizationSuccedsIfRequestDoesNotContainCheckItemsAndRejectOnCheckItemNotFoundIsDisabled() {
				handlerData.setRejectOnCheckItemNotFound(false);
				
				requestIsReceived();
				
				subscriberAuthorizationSucceeds();
			}
			
			@Test
			public void authorizationSucceedsIfCheckItemsArePresentInTheRequestAndCheckItemSucceeds() {
				request.getDiameterRequest().addAvp(DiameterAVPConstants.CLASS, "addedFromTest");
				
				requestIsReceived();
				
				subscriberAuthorizationSucceeds();
			}
			
			@Test 
			public void authorizationFailsIfValueOfCheckItemPresentInRequestDoesNotMatch() {
				request.getDiameterRequest().addAvp(DiameterAVPConstants.CLASS, "someRandomValue");
				
				requestIsReceived();
				
				verifySubscriberAuthorizationFailsWithMessage("Policy Failed");
			}
		}
		
		public class SubscriberProfileLevelRejectItems {
			
			@Before
			public void setup() {
				accountData.setRejectItem("0:25=\"rejectItemValue\"");
			}
			
			@Test
			public void authorizationFailsIfRejectItemExpressionMatches() {
				request.getDiameterRequest().addAvp(DiameterAVPConstants.CLASS, "rejectItemValue");
				
				requestIsReceived();
				
				verifySubscriberAuthorizationFailsWithMessage("Policy Failed");
			}
			
			@Test
			public void authorizationSucceedsIfRejectItemIsNotPresentInRequestAndRejectOnRejectItemNotFoundIsDisabled() {
				handlerData.setRejectOnRejectItemNotFound(false);

				requestIsReceived();
				
				subscriberAuthorizationSucceeds();
			}
			
			@Test
			public void authorizationFailsIfRejectItemIsNotPresentInRequestAndRejectOnRejectItemNotFoundIsEnabled() {
				handlerData.setRejectOnRejectItemNotFound(true);

				requestIsReceived();
				
				verifySubscriberAuthorizationFailsWithMessage("Policy Failed");
			}
		}
		
		@Test
		public void nameOfAppliedAuthorizationPoliciesIsAddedInResponsePacket() throws ParserException, PolicyFailedException {
			accountData.setAuthorizationPolicy("someAuthorizationPolicy");
			
			policyManagerSpy.simulatePolicyApplicationResult("(someAuthorizationPolicy)", "someAuthorizationPolicy");
			
			requestIsReceived();
			
			DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(
					DiameterAVPConstants.EC_SATISFIED_POLICIES, "someAuthorizationPolicy");
		}
		
		@Test
		public void authorizationFailsGivenAuthorizationPolicyFails() throws ParserException, PolicyFailedException {
			accountData.setAuthorizationPolicy("someAuthorizationPolicy");
			
			policyManagerSpy.simulateFailedPolicy("someAuthorizationPolicy");
			
			requestIsReceived();
			
			verifySubscriberAuthorizationFailsWithMessage("Policy Failed");
		}
		
		@Test
		public void authorizationFailsGivenParsingOfAuthorizationPolicyFails() throws ParserException, PolicyFailedException {
			accountData.setAuthorizationPolicy("someAuthorizationPolicy");
			
			policyManagerSpy.simulateParsingFailureOf("someAuthorizationPolicy");
			
			requestIsReceived();
			
			verifySubscriberAuthorizationFailsWithMessage("some message");
		}
		
		@Test
		public void givenAPolicyGroupIsConfiguredAllAppliedAuthorizationPoliciesAreAddedInResponsePacket() throws ParserException, PolicyFailedException {
			accountData.setAuthorizationPolicyGroup("authorizationPolicyGroup");
			
			doReturn("policyOne && policyTwo").when(policyManagerSpy.spiedPolicyManager).getExpressionFrom("authorizationPolicyGroup");
			
			policyManagerSpy.simulatePolicyApplicationResult("( policyOne && policyTwo ) ", "policyOne","&&","policyTwo");
			
			requestIsReceived();
			
			DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(
					DiameterAVPConstants.EC_SATISFIED_POLICIES, "policyOne,&&,policyTwo");
		}
		
		@Test
		public void nameOfAdditionalPoliciesIsAddedInResponsePacket() throws ParserException, PolicyFailedException {
			accountData.setAdditionalPolicy("someAdditionalPolicy");
			
			policyManagerSpy.simulatePolicyApplicationResult("(someAdditionalPolicy)", "someAdditionalPolicy");
			
			requestIsReceived();
			
			DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(
					DiameterAVPConstants.EC_SATISFIED_POLICIES, "*,someAdditionalPolicy");
		}
		
		@Test
		public void authorizaionSucceedsEvenIfAdditionalPolicyFails() throws ParserException, PolicyFailedException {
			accountData.setAdditionalPolicy("someAdditionalPolicy");
			
			policyManagerSpy.simulateFailedPolicy("someAdditionalPolicy");

			requestIsReceived();
			
			subscriberAuthorizationSucceeds();
		}
	}

	public class DynamicCheckItem {
		
		private static final String ANY_ID = "0";

		@Before
		public void setup() {
			givenBasicAuthorizationChecksSucceed();
			request.setParameter(AAAServerConstants.DYNAMIC_CHECK_ITEM_DRIVER_INSTANCE_ID, ANY_ID);
			accountData.setDynamicCheckItems("0:31=[*]");
		}

		@Test
		public void givenDynamicCheckItemIsConfiguredCallIsMadeToUpdateAccountData() throws UpdateAccountDataFailedException {
			request.getDiameterRequest().addAvp(DiameterAVPConstants.CALLING_STATION_ID, "machineOne");
			
			doReturn(baseAuthDriver).when(
					diameterServiceContextStub).getDriver(ANY_ID);
			
			requestIsReceived();
	
			verify(baseAuthDriver, times(1)).setDynamicCheck(anyString(), anyString());
		}
		
		@Test
		public void evenIfCallMadeToUpdataDynamicCheckItemInAccountDataFailsAuthorizationSucceds() throws UpdateAccountDataFailedException {
			request.getDiameterRequest().addAvp(DiameterAVPConstants.CALLING_STATION_ID, "machineOne");
			
			doReturn(baseAuthDriver).when(
					diameterServiceContextStub).getDriver(ANY_ID);
			
			doThrow(UpdateAccountDataFailedException.class).when(
					baseAuthDriver).setDynamicCheck(anyString(), anyString());
			
			requestIsReceived();
	
			subscriberAuthorizationSucceeds();
		}
		
		@Test
		public void  authorizationFailsAfterConfiguredNumberOfWildCardRequestsHasBeenAccepted() throws UpdateAccountDataFailedException {
			request.getDiameterRequest().addAvp(DiameterAVPConstants.CALLING_STATION_ID, "machineOne");
			
			doReturn(baseAuthDriver).when(
					diameterServiceContextStub).getDriver(ANY_ID);
			
			requestIsReceived();
	
			verify(baseAuthDriver, times(1)).setDynamicCheck(anyString(), anyString());
			
			subscriberAuthorizationSucceeds();
			
			IDiameterAVP avp = request.getAVP(DiameterAVPConstants.CALLING_STATION_ID);
			avp.setStringValue("machineTwo");
			
			requestIsReceived();
			
			verify(baseAuthDriver, times(1)).setDynamicCheck(anyString(), anyString());
			
			verifySubscriberAuthorizationFailsWithMessage("Policy Failed");
			
	
		}
		
		@Test
		public void givenDynamicCheckItemsConfiguredInSubscriberProfileAreInWrongFormatDynamicCheckAvpIsIgnored() throws UpdateAccountDataFailedException {
			accountData.setDynamicCheckItems("0:31 \"machineone\"");
			
			DiameterAssertion.assertThat(request.getDiameterRequest()).doesNotContainAVP(DiameterAVPConstants.CALLING_STATION_ID);
		
			doReturn(baseAuthDriver).when(
					diameterServiceContextStub).getDriver(ANY_ID);
			
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
			doThrow(AccessDeniedException.class).when(accessPolicyManager).checkAccessPolicy(
					"someAccessPolicy", false);
			
			requestIsReceived();
			
			DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_AUTHORIZATION_REJECTED);
		}
		
		@Test
		public void authorizationSucceedsIfAccessPolicyIsSuccessfulyApplied() throws AccessDeniedException {
			doReturn(2000l).when(accessPolicyManager).checkAccessPolicy("someAccessPolicy", false);
			
			requestIsReceived();
			
			subscriberAuthorizationSucceeds();
		}
		
		@Test
		public void givenSessionTimeoutCalculatedByAccessPolicyIsSmallerItIsUpdated() throws AccessDeniedException {
			IDiameterAVP sessionTimeout = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SESSION_TIMEOUT);
			sessionTimeout.setInteger(1000);
			response.addAVP(sessionTimeout);
			
			doReturn(500l).when(accessPolicyManager).checkAccessPolicy("someAccessPolicy", false);
			
			requestIsReceived();
			
			subscriberAuthorizationSucceeds();
			
			DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(DiameterAVPConstants.SESSION_TIMEOUT, "500");
		}
		
		@Test
		public void givenSessionTimeoutCalculatedByAccessPolicyIsLargerItIsNotUpdated() throws AccessDeniedException {
			IDiameterAVP sessionTimeout = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SESSION_TIMEOUT);
			sessionTimeout.setInteger(1000);
			response.addAVP(sessionTimeout);
			
			doReturn(2000l).when(accessPolicyManager).checkAccessPolicy("someAccessPolicy", false);
			
			requestIsReceived();
			
			subscriberAuthorizationSucceeds();
			
			DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(DiameterAVPConstants.SESSION_TIMEOUT, "1000");
		}
		
		@Test
		public void givenSessionTimeoutAvpIsNotPresentInResponseItIsAdded() throws AccessDeniedException {
			DiameterAssertion.assertThat(response.getDiameterAnswer()).doesNotContainAVP(DiameterAVPConstants.SESSION_TIMEOUT);
			doReturn(500l).when(accessPolicyManager).checkAccessPolicy("someAccessPolicy", false);
			
			requestIsReceived();
			
			subscriberAuthorizationSucceeds();
			
			DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(DiameterAVPConstants.SESSION_TIMEOUT, "500");
		}
		
	}
	
	public class AddDiameterSuccessAvp {
		
		@Before
		public void setup() {
			givenBasicAuthorizationChecksSucceed();
		}
		
		@Test
		public void givenResultCodeAvpIsNotPresentInResponseItIsAddedWithDefaultValueOfDiameterSuccess() {
		 DiameterAssertion.assertThat(response.getDiameterAnswer()).doesNotContainAVP(DiameterAVPConstants.RESULT_CODE);
		 
		 requestIsReceived();
		 
		 subscriberAuthorizationSucceeds();
		}
	}
	
	public class DefaultSessionTimeout {
		
		@Before
		public void setup() {
			givenBasicAuthorizationChecksSucceed();
		}
		
		@Test
		public void hintsSystemToDisableSessionTimeoutIfNegativeValueIsConfigured() {
			handlerData.setDefaultSessionTimeoutInSeconds(-20l);
			
			requestIsReceived();
			
			assertEquals(request.getParameter(AAAServerConstants.SESSION_TIMEOUT), AAAServerConstants.SESSION_TIMEOUT_DISABLED);
		}
		
		@Test
		public void addsSessionTimeoutInResponseIfPositiveValueIsConfigured() {
			handlerData.setDefaultSessionTimeoutInSeconds(100);
			
			requestIsReceived();
			
			DiameterAssertion.assertThat(response.getDiameterAnswer()).containsAVP(DiameterAVPConstants.SESSION_TIMEOUT, "100");
		}
		
		@Test
		public void doesNotAddSessionTimeoutInResponseIfZeroIsConfigured() {
			handlerData.setDefaultSessionTimeoutInSeconds(0);
			
			requestIsReceived();
			
			DiameterAssertion.assertThat(response.getDiameterAnswer()).doesNotContainAVP(DiameterAVPConstants.SESSION_TIMEOUT);
		}
	}
	
	private void givenBasicAuthorizationChecksSucceed() {
		accountData.setCreditLimit(100);
		accountData.setCreditLimitCheckRequired(true);
		
		accountData.setExpiryDate(validDate());
		accountData.setExpiryDateCheckRequired(true);
		
		accountData.setAccountStatus(CommonConstants.STATUS_ACTIVE);
	}

	private void givenCreditLimitIsNotConfigured() {
		accountData.setCreditLimit(0);
		accountData.setCreditLimitCheckRequired(false);
	}

	private Date expiredDate() {
		return new Date(System.currentTimeMillis() - 10000);
	}

	private Date sinceTwoDays() {
		return new Date(System.currentTimeMillis() - TWO_DAY_IN_MILLIS);
	}

	private Date expiryDateExceedingAnyGraceSlot() {
		return new Date(System.currentTimeMillis() - THREE_DAYS_IN_MILLIS);
	}

	private Date validDate() {
		return new Date(System.currentTimeMillis() + 1000000);
	}

	private void requestIsReceived() {
		handler.handleRequest(request, response, null);
	}

	private void verifySubscriberAuthorizationFailsWithMessage(String reason) {
		DiameterAssertion.assertThat(response.getDiameterAnswer())
		.hasResultCode(ResultCode.DIAMETER_AUTHORIZATION_REJECTED)
		.containsAVP(DiameterAVPConstants.ERROR_MESSAGE, reason);
	}

	private void subscriberAuthorizationSucceeds() {
		DiameterAssertion.assertThat(response.getDiameterAnswer())
		.hasResultCode(ResultCode.DIAMETER_SUCCESS);
	}

	private void handlerWithGracePolicy(String policyName) {
		handlerData.setGracePolicy(policyName);
	}

	private class DiameterSubscriberProfileRepositoryStub extends DiameterSubscriberProfileRepository {

		public DiameterSubscriberProfileRepositoryStub(DiameterServiceContext serviceContext,
				DiameterSubscriberProfileRepositoryDetails data) {
			super(serviceContext, data);
		}
		
		
		@Override
		public AccountData getAccountData(ApplicationRequest request, ApplicationResponse response) {
			return null;
		}
	}
}
