package com.elitecore.netvertex.service.pcrf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.NotificationRecipient;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.util.MockBasePackage;
import com.elitecore.corenetvertex.pm.util.MockQuotaTopUp;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.corenetvertex.spr.DummySubscriptionProvider;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.util.Maps;
import com.elitecore.netvertex.core.util.Maps.Entry;
import com.elitecore.netvertex.pm.util.MockProductOffer;
import com.elitecore.netvertex.service.notification.QuotaNotificationScheme;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class QuotaNotificationProcessorTest {

    public static final String SUBSCRIBER_ID = "subscriberId";
    public static final String DATA_PACKAGE = "dataPackage";
    public static final String PRODUCT_OFFER_ID = "productOfferId";
    public static final String PRODUCT_OFFER_NAME = "productOfferName";
    public static final String PACKAGE_ID = UUID.randomUUID().toString();
    public static final String SUBSCRIPTION_ID = UUID.randomUUID().toString();
    private QuotaNotificationProcessor processor;
    private DummyNetvertexServerContextImpl serverContext;
    private PCRFRequestImpl request;
    private PCRFResponseImpl response;
    private DummyPolicyRepository policyRepository;
    private MockBasePackage basePackage;
    private String quotaProfileId;
    private static final String TOP_UP_NAME = "TopUpName";
    private QuotaNotificationScheme scheme;

    @Before
    public void setUp() {
        basePackage = MockBasePackage.create("basePackageId", DATA_PACKAGE);
        request = new PCRFRequestImpl();
        response = new PCRFResponseImpl();
        request.setSPRInfo(new SPRInfoImpl());
        request.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, SUBSCRIBER_ID);
        request.setAttribute(PCRFKeyConstants.SUB_PRODUCT_OFFER.val, PRODUCT_OFFER_NAME);
        serverContext = spy(new DummyNetvertexServerContextImpl());
        processor = new QuotaNotificationProcessor(serverContext);
        policyRepository = spy(new DummyPolicyRepository());
        policyRepository.addBasePackage(basePackage);

        PolicyManager policyManager = mock(PolicyManager.class);

        PolicyManager.setInstance(policyManager);
        when(policyManager.getPkgDataById(basePackage.getId())).thenReturn(null);

        MockProductOffer productOffer  = MockProductOffer.create(policyRepository, PRODUCT_OFFER_ID, PRODUCT_OFFER_NAME);
        productOffer.addDataPackage(basePackage);
        policyRepository.addProductOffer(productOffer);
        serverContext.setPolicyRepository(policyRepository);
        List<NonMonetoryBalance> previousNonMonetoryBalanceList = new ArrayList<>();
        List<NonMonetoryBalance> currentNonMonetoryBalanceList = new ArrayList<>();
        previousNonMonetoryBalanceList.add(createNonMonetaryBalanceBuilder(SUBSCRIPTION_ID).withBillingCycleVolumeBalance(100, 80).build());
        currentNonMonetoryBalanceList.add(createNonMonetaryBalanceBuilder(SUBSCRIPTION_ID).withBillingCycleVolumeBalance(100, 70).build());
        SubscriberNonMonitoryBalance previousBalance = new SubscriberNonMonitoryBalance(previousNonMonetoryBalanceList);
        SubscriberNonMonitoryBalance currentBalance = new SubscriberNonMonitoryBalance(currentNonMonetoryBalanceList);
        response.setCurrentNonMonetoryBalance(previousBalance);
        response.setPreviousNonMonetoryBalance(currentBalance);
        Subscription subscription = new Subscription.SubscriptionBuilder().withId(SUBSCRIPTION_ID).withPackageId(PACKAGE_ID).withType(SubscriptionType.TOP_UP).build();
        LinkedHashMap<String, Subscription> subscriptions = Maps.newLinkedHashMap(Entry.newEntry(subscription.getId(), subscription));
        response.setSubscriptions(subscriptions);
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        DummySubscriptionProvider subscriptionProvider = new DummySubscriptionProvider();
        subscriptionProvider.setSubscriptions(subscriptions);
        sprInfo.setSubscriptionProvider(subscriptionProvider);
        request.setSPRInfo(sprInfo);

        MockQuotaTopUp quotaTopUp = MockQuotaTopUp.create(PACKAGE_ID, TOP_UP_NAME);
        scheme = mock(QuotaNotificationScheme.class);
        doReturn(scheme).when(quotaTopUp).getQuotaNotificationScheme();
        policyRepository.addQuotaTopUp(quotaTopUp);
    }

    private NonMonetoryBalance.NonMonetaryBalanceBuilder createNonMonetaryBalanceBuilder(String subcriptionId) {
        return new NonMonetoryBalance.NonMonetaryBalanceBuilder("id",
                CommonConstants.ALL_SERVICE_IDENTIFIER,
                "packageId",
                CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER,
                "subscriberIdentity",
                subcriptionId,
                0,
                quotaProfileId,
                ResetBalanceStatus.NOT_RESET, null, null).withBillingCycleResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1));
    }

    public class NotificationGenerationSkipsWhen {

        @Test
        public void subscriberIdNotFoundFromPCRFRequest() {
            request.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, null);
            processor.process(request, response);
            verify(serverContext, never()).sendNotification(any(Template.class), any(Template.class), same(response), eq(null), any(NotificationRecipient.class));
        }

        @Test
        public void basePackageNameNotFoundFromPCRFRequest() {
            request.setAttribute(PCRFKeyConstants.SUB_DATA_PACKAGE.val, null);
            processor.process(request, response);
            verify(serverContext, never()).sendNotification(any(Template.class), any(Template.class), same(response), eq(null), any(NotificationRecipient.class));
        }

        @Test
        public void basePackageNotFoundFromPolicyRepository() {
            doReturn(null).when(policyRepository).getBasePackageDataByName(DATA_PACKAGE);
            processor.process(request, response);
            verify(serverContext, never()).sendNotification(any(Template.class), any(Template.class), same(response), eq(null), any(NotificationRecipient.class));
        }

        @Test
        public void basePackageIsInFailedStatus() {
            basePackage.setPolicyStatus(PolicyStatus.FAILURE);
            processor.process(request, response);
            verify(serverContext, never()).sendNotification(any(Template.class), any(Template.class), same(response), eq(null), any(NotificationRecipient.class));
        }

        @Test
        public void currentBalanceIsNotFound() {
            response.setCurrentNonMonetoryBalance(null);
            processor.process(request, response);
            verify(serverContext, never()).sendNotification(any(Template.class), any(Template.class), same(response), eq(null), any(NotificationRecipient.class));
        }

        @Test
        public void previousBalanceIsNotFound() {
            response.setPreviousNonMonetoryBalance(null);
            processor.process(request, response);
            verify(serverContext, never()).sendNotification(any(Template.class), any(Template.class), same(response), eq(null), any(NotificationRecipient.class));
        }

        @Test
        public void subscriptionIsNullFromPCRFResponse() {
            response.setSubscriptions(null);
            processor.process(request, response);
            verify(serverContext, never()).sendNotification(any(Template.class), any(Template.class), same(response), eq(null), any(NotificationRecipient.class));
        }

        @Test
        public void subscriptionIsEmptyFromPCRFResponse() {
            response.setSubscriptions(Maps.newLinkedHashMap());
            processor.process(request, response);
            verify(serverContext, never()).sendNotification(any(Template.class), any(Template.class), same(response), eq(null), any(NotificationRecipient.class));
        }

        @Test
        public void subscriptionBalanceNotFound() {
            response.getCurrentNonMonetoryBalance().getPackageBalances().remove(SUBSCRIPTION_ID);
            processor.process(request, response);
            verify(serverContext, never()).sendNotification(any(Template.class), any(Template.class), same(response), eq(null), any(NotificationRecipient.class));
        }
    }

    public class NotificationGenerationSuccess {

        @Before
        public void setUp() {
            doNothing().when(scheme).queueEligibleEvents(same(response.getPreviousNonMonetoryBalance().getPackageBalance(SUBSCRIPTION_ID)),
                    same(response.getCurrentNonMonetoryBalance().getPackageBalance(SUBSCRIPTION_ID)), any(NotificationQueue.class));
        }

        @Test
        public void test() {
            processor.process(request, response);
            verify(scheme, times(1)).queueEligibleEvents(eq(Arrays.asList(response.getPreviousNonMonetoryBalance().getPackageBalance(SUBSCRIPTION_ID))),
                    eq(Arrays.asList(response.getCurrentNonMonetoryBalance().getPackageBalance(SUBSCRIPTION_ID))), any(NotificationQueue.class));
        }
    }
}