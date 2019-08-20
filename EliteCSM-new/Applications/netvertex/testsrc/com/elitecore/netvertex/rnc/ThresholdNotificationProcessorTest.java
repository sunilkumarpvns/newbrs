package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.constants.NotificationRecipient;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.corenetvertex.spr.MockSubscriptionProvider;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.pm.util.MockProductOffer;
import com.elitecore.netvertex.pm.util.MockRnCPackage;
import com.elitecore.netvertex.rnc.ThresholdNotificationProcessor.NotificationQueueImpl;
import com.elitecore.netvertex.service.notification.ThresholdNotificationScheme;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class ThresholdNotificationProcessorTest {

	public static final String SUBSCRIBER_ID = "subscriberId";
	public static final String RNC_PACKAGE_NAME = "dataPackage";
	public static final String RNC_PACKAGE_ID = "rncPackageId";
	public static final String PRODUCT_OFFER_ID = "productOfferId";
	public static final String PRODUCT_OFFER_NAME = "productOfferName";

	private ThresholdNotificationProcessor processor;
	private DummyNetvertexServerContextImpl serverContext;
	private PCRFRequestImpl request;
	private PCRFResponseImpl response;
	private DummyPolicyRepository policyRepository;
	private MockProductOffer productOffer;
	private MockRnCPackage rnCPackage;
	private String smsService = "SMS";
	private String smsServiceId = "SMSId";
	private RnCNonMonetaryBalance currentBalance;
	private RnCNonMonetaryBalance previousBalance;
	private ThresholdNotificationScheme scheme;
	private SPRInfoImpl sprInfo;
	private MockSubscriptionProvider subscriptionProvider;

	@Before
	public void setUp() {
		subscriptionProvider = new MockSubscriptionProvider();
		sprInfo = new SPRInfoImpl();
		sprInfo.setSubscriptionProvider(subscriptionProvider);
		this.serverContext = spy(new DummyNetvertexServerContextImpl());
		this.processor = new ThresholdNotificationProcessor(serverContext);
		request = new PCRFRequestImpl();
		response = new PCRFResponseImpl();
		scheme = spy(new ThresholdNotificationScheme(Collections.emptyList()));
		rnCPackage = MockRnCPackage.createBase(RNC_PACKAGE_ID, RNC_PACKAGE_NAME);
		rnCPackage.setNotificationScheme(scheme);
		policyRepository = spy(new DummyPolicyRepository());
		policyRepository.addRnCPackage(rnCPackage);
		productOffer = MockProductOffer.create(policyRepository, PRODUCT_OFFER_ID, PRODUCT_OFFER_NAME);
		productOffer.addRnCPackage(smsService, smsServiceId, rnCPackage);
		policyRepository.addProductOffer(productOffer);
		serverContext.setPolicyRepository(policyRepository);
		setRequiredAttriubtesInRequestResponse();
	}

	private void setRequiredAttriubtesInRequestResponse() {
		request.setSPRInfo(sprInfo);
		request.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, SUBSCRIBER_ID);
		request.setAttribute(PCRFKeyConstants.SUB_PRODUCT_OFFER.val, PRODUCT_OFFER_NAME);
		request.setAttribute(PCRFKeyConstants.REQUEST_TYPE.val, PCRFKeyValueConstants.REQUEST_TYPE_EVENT_REQUEST.val);
		request.setAttribute(PCRFKeyConstants.REQUESTED_ACTION.val, PCRFKeyValueConstants.REQUESTED_ACTION_DIRECT_DEBITING.val);
		response.setAttribute(PCRFKeyConstants.RESULT_CODE.val, PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val);
		response.setAttribute(PCRFKeyConstants.CS_SERVICE.val, smsServiceId);
		String balanceId = UUID.randomUUID().toString();
		currentBalance = new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(balanceId,
				rnCPackage.getId(),
				productOffer.getId(),
				SUBSCRIBER_ID,
				null,
				"rateCardId",
				ResetBalanceStatus.NOT_RESET,
				"0",
				ChargingType.EVENT)
				.withBillingCycleTimeBalance(100, 70)
				.withBillingCycleResetTime(System.currentTimeMillis()+100000)
				.build();
		previousBalance = new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(balanceId,
				rnCPackage.getId(),
				productOffer.getId(),
				SUBSCRIBER_ID,
				null,
				"rateCardId",
				ResetBalanceStatus.NOT_RESET,
				"0",
				ChargingType.EVENT)
				.withBillingCycleTimeBalance(100, 80).build();
		SubscriberRnCNonMonetaryBalance currentSubscriberBalance = new SubscriberRnCNonMonetaryBalance(Arrays.asList(currentBalance));
		SubscriberRnCNonMonetaryBalance previousSubscriberBalance = new SubscriberRnCNonMonetaryBalance(Arrays.asList(previousBalance));
		response.setCurrentRnCNonMonetaryBalance(currentSubscriberBalance);
		response.setPreviousRnCNonMonetaryBalance(previousSubscriberBalance);
	}

	public class NotificationGenerationSkipsWhen {

		@Test
		public void subscriberIdNotFoundFromPCRFRequest() {
			request.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, null);
			processor.process(request, response);
			verify(scheme, never()).queueEligibleEvents(same(response.getPreviousRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)),
					same(response.getCurrentRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)), any(NotificationQueueImpl.class));
		}

		@Test
		public void resultCodeIsNotSuccessNotFoundFromPCRFRequest() {
			response.setAttribute(PCRFKeyConstants.RESULT_CODE.val, PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
			processor.process(request, response);
			verify(scheme, never()).queueEligibleEvents(same(response.getPreviousRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)),
					same(response.getCurrentRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)), any(NotificationQueueImpl.class));
		}

		@Test
		public void productOfferNameNotFoundFromPCRFRequest() {
			request.setAttribute(PCRFKeyConstants.SUB_PRODUCT_OFFER.val, null);
			processor.process(request, response);
			verify(scheme, never()).queueEligibleEvents(same(response.getPreviousRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)),
					same(response.getCurrentRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)), any(NotificationQueueImpl.class));
		}

		@Test
		public void productOfferNotFoundFromPolicyRepository() {
			policyRepository.flushProductOffers();
			processor.process(request, response);
			verify(scheme, never()).queueEligibleEvents(same(response.getPreviousRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)),
					same(response.getCurrentRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)), any(NotificationQueueImpl.class));
		}

		@Test
		public void productOfferIsInFailedStatus() {
			productOffer.setPolicyStatus(PolicyStatus.FAILURE);
			processor.process(request, response);
			verify(scheme, never()).queueEligibleEvents(same(response.getPreviousRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)),
					same(response.getCurrentRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)), any(NotificationQueueImpl.class));
		}

		@Test
		public void requestedServiceIsNotMatchWithConfiguredServiceInProductOffer() {
			response.setAttribute(PCRFKeyConstants.CS_SERVICE.val, "VOICE");
			processor.process(request, response);
			verify(scheme, never()).queueEligibleEvents(same(response.getPreviousRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)),
					same(response.getCurrentRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)), any(NotificationQueueImpl.class));
		}

		@Test
		public void rncPackageIsInFailedStatus() {
			rnCPackage.setPolicyStatus(PolicyStatus.FAILURE);
			processor.process(request, response);
			verify(scheme, never()).queueEligibleEvents(same(response.getPreviousRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)),
					same(response.getCurrentRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)), any(NotificationQueueImpl.class));
		}

		@Test
		public void onEventRequestIfRequestedActionIsNotDirectDebit() {
			request.setAttribute(PCRFKeyConstants.REQUESTED_ACTION.val, PCRFKeyValueConstants.REQUESTED_ACTION_REFUND_ACCOUNT.val);
			processor.process(request, response);
			verify(scheme, never()).queueEligibleEvents(same(response.getPreviousRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)),
					same(response.getCurrentRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)), any(NotificationQueueImpl.class));
		}

		@Test
		public void currentBalanceIsNotFound() {
			response.setCurrentRnCNonMonetaryBalance(null);
			processor.process(request, response);
			verify(serverContext, never()).sendNotification(any(Template.class), any(Template.class), same(response), eq(null), any(NotificationRecipient.class));
		}

		@Test
		public void notificationSchemeNotConfigured() {
			rnCPackage.setNotificationScheme(null);
			processor.process(request, response);
			verify(scheme, never()).queueEligibleEvents(same(response.getPreviousRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)),
					same(response.getCurrentRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)), any(NotificationQueueImpl.class));
		}
	}

	public class BasePackageNotificationGenerationSuccessWhen {

		@Before
		public void setUp() {
			doNothing().when(scheme).queueEligibleEvents(same(response.getPreviousRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)),
					same(response.getCurrentRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)), any(NotificationQueueImpl.class));
		}

		@Test
		public void basePackage() {
			processor.process(request, response);
			verify(scheme, times(1)).queueEligibleEvents(same(response.getPreviousRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)),
					same(response.getCurrentRnCNonMonetaryBalance().getPackageBalance(RNC_PACKAGE_ID)), any(NotificationQueueImpl.class));
		}
	}

	public class AddOnPackageNotificationGenerationSuccessWhen {
		private static final String ADDON_PACKAGE_ID1 = "ADDON_PACKAGE_ID1";
		private static final String ADDON_PACKAGE_NAME1 = "ADDON_PACKAGE_NAME1";
		private static final String ADDON_PRODUCT_OFFER_ID1 = "ADDON_PRODUCT_OFFER_ID1";
		private static final String ADDON_PRODUCT_OFFER_NAME1 = "ADDON_PRODUCT_OFFER_NAME1";
		private final String SUBSCRIPTION_ID1 = UUID.randomUUID().toString();
		private final String SUBSCRIPTION_ID2 = UUID.randomUUID().toString();
		private MockRnCPackage addOn;
		private MockProductOffer addOnOffer;

		@Before
		public void setUp() {
			doNothing().when(scheme).queueEligibleEvents(same(response.getPreviousRnCNonMonetaryBalance().getPackageBalance(SUBSCRIPTION_ID1)),
					same(response.getCurrentRnCNonMonetaryBalance().getPackageBalance(SUBSCRIPTION_ID1)), any(NotificationQueueImpl.class));

			addOn = MockRnCPackage.createNonMonetaryAddOn(ADDON_PACKAGE_ID1, ADDON_PACKAGE_NAME1);
			addOn.eventchargingType();



			addOn.setNotificationScheme(scheme);
			policyRepository.addRnCPackage(addOn);
			addOnOffer = MockProductOffer.createAddOn(policyRepository, ADDON_PRODUCT_OFFER_ID1, ADDON_PRODUCT_OFFER_NAME1);
			addOnOffer.addRnCPackage(smsService, smsServiceId, addOn);
			policyRepository.addProductOffer(addOnOffer);
		}

		private RnCNonMonetaryBalance createBalance(String balanceId, String SUBSCRIPTION_ID1) {
			return new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(balanceId,
					addOn.getId(),
					addOnOffer.getId(),
					SUBSCRIBER_ID,
					SUBSCRIPTION_ID1,
					"rateCardId",
					ResetBalanceStatus.NOT_RESET,
					"0",
					ChargingType.EVENT)
					.withBillingCycleTimeBalance(100, 70)
					.withBillingCycleResetTime(System.currentTimeMillis()+100000)
					.build();
		}

		private void addSubscription(String subscriptionId) {
			Subscription subscription1 = new Subscription.SubscriptionBuilder()
					.withType(SubscriptionType.RO_ADDON)
					.withId(subscriptionId)
					.withPackageId(ADDON_PACKAGE_ID1)
					.withProductOfferId(ADDON_PRODUCT_OFFER_ID1)
					.build();
			subscriptionProvider.addSubscription(subscription1);
		}

		@Test
		public void singleSubscription() {
			addSubscription(SUBSCRIPTION_ID1);
			setUpSingleSubscription(SUBSCRIPTION_ID1);
			processor.process(request, response);
			verify(scheme, times(1)).queueEligibleEvents(same(response.getPreviousRnCNonMonetaryBalance().getPackageBalance(SUBSCRIPTION_ID1)),
					same(response.getCurrentRnCNonMonetaryBalance().getPackageBalance(SUBSCRIPTION_ID1)), any(NotificationQueueImpl.class));
		}

		private void setUpSingleSubscription(String subscriptionId) {
			String balanceId = UUID.randomUUID().toString();

			currentBalance = createBalance(balanceId, subscriptionId);
			previousBalance = createBalance(balanceId, subscriptionId);
			SubscriberRnCNonMonetaryBalance currentSubscriberBalance = new SubscriberRnCNonMonetaryBalance(Arrays.asList(currentBalance));
			SubscriberRnCNonMonetaryBalance previousSubscriberBalance = new SubscriberRnCNonMonetaryBalance(Arrays.asList(previousBalance));
			response.setCurrentRnCNonMonetaryBalance(currentSubscriberBalance);
			response.setPreviousRnCNonMonetaryBalance(previousSubscriberBalance);
		}

		@Test
		public void multipleSubscription() {
			setUpMultipleSubscription();
			processor.process(request, response);

			verify(scheme, times(1)).queueEligibleEvents(eq(Arrays.asList(response.getPreviousRnCNonMonetaryBalance().getPackageBalance(SUBSCRIPTION_ID1),
					response.getPreviousRnCNonMonetaryBalance().getPackageBalance(SUBSCRIPTION_ID2))),
					eq(Arrays.asList(response.getCurrentRnCNonMonetaryBalance().getPackageBalance(SUBSCRIPTION_ID1),
							response.getCurrentRnCNonMonetaryBalance().getPackageBalance(SUBSCRIPTION_ID2))), any(NotificationQueueImpl.class));

		}

		private void setUpMultipleSubscription() {
			addSubscription(SUBSCRIPTION_ID1);
			addSubscription(SUBSCRIPTION_ID2);
			String balanceId = UUID.randomUUID().toString();

			RnCNonMonetaryBalance currentBalance1 = createBalance(balanceId, SUBSCRIPTION_ID1);
			RnCNonMonetaryBalance previousBalance1 = createBalance(balanceId, SUBSCRIPTION_ID1);
			RnCNonMonetaryBalance currentBalance2 = createBalance(balanceId, SUBSCRIPTION_ID2);
			RnCNonMonetaryBalance previousBalance2 = createBalance(balanceId, SUBSCRIPTION_ID2);
			SubscriberRnCNonMonetaryBalance currentSubscriberBalance = new SubscriberRnCNonMonetaryBalance(Arrays.asList(currentBalance1, currentBalance2));
			SubscriberRnCNonMonetaryBalance previousSubscriberBalance = new SubscriberRnCNonMonetaryBalance(Arrays.asList(previousBalance1, previousBalance2));
			response.setCurrentRnCNonMonetaryBalance(currentSubscriberBalance);
			response.setPreviousRnCNonMonetaryBalance(previousSubscriberBalance);
		}
	}

}