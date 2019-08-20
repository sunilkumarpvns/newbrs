package com.elitecore.netvertex.pm.quota;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pd.topup.TopUpType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.util.MockQuotaTopUp;
import com.elitecore.corenetvertex.pm.util.PolicyProvider;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.RnCQuotaProfileFactory;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.elitecore.netvertex.pm.quota.RncTopUpQuotaProfileDetailTestUtil.createMonetaryBalance;
import static com.elitecore.netvertex.pm.quota.RncTopUpQuotaProfileDetailTestUtil.createNonMonetaryBalance;
import static com.elitecore.netvertex.pm.quota.RncTopUpQuotaProfileDetailTestUtil.createRnCQuotaDetail;
import static com.elitecore.netvertex.pm.quota.RncTopUpQuotaProfileDetailTestUtil.createTopUp;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class RncTopUpQuotaProfileDetailMultipleSubscriptionTest {

    public static final String INR = "INR";
    private static final String UNKNOWN = "UNKNOWN";
    private RnCTopUpQuotaProfileDetail rnCTopUpQuotaProfileDetail;
    private PCRFRequest request;
    private PCRFResponse response;
    private QuotaReservation quotaReservation;
    private MockBasePackage basePackage;
    private GyPolicyContextImpl gyPolicyContext;
    private ExecutionContext executionContext;
    private PolicyProvider policyRepository;

    private SPRInfoImpl sprInfo;
    private String baseQuotaProfileId;
    private long baseRGId = 101;
    private String pccProfileName = UUID.randomUUID().toString() + "_name";
    private final List<String> validApplicablePCCProfiles = Arrays.asList(pccProfileName);
    private final List<String> inValidApplicablePCCProfiles = Arrays.asList(pccProfileName + UNKNOWN);


    @Before
    public void setUp() {

        String subscriberID = "subscriberID";
        baseQuotaProfileId = UUID.randomUUID().toString();

        sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberID);
        request = new PCRFRequestImpl();
        request.setSPRInfo(sprInfo);
        response = new PCRFResponseImpl();
        basePackage = MockBasePackage.create(UUID.randomUUID().toString(), UUID.randomUUID().toString() + "name");

        this.rnCTopUpQuotaProfileDetail = spy(new RnCQuotaProfileFactory(baseQuotaProfileId,
                UUID.randomUUID().toString())
                .withRGId(baseRGId).withPCCProfileName(pccProfileName)
                .randomBalanceWithRate().createTopUpQuotaProfileDetail());
        NonMonetoryBalance nonMonetoryBalance = createNonMonetaryBalance(sprInfo, rnCTopUpQuotaProfileDetail, basePackage.getId(), null);
        SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(Arrays.asList(nonMonetoryBalance));
        response.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);

        SubscriberMonetaryBalance subscriberMonetaryBalance = spy(new SubscriberMonetaryBalance(new FixedTimeSource(System.currentTimeMillis())));
        MonetaryBalance monetaryBalance = spy(createMonetaryBalance(subscriberID));
        subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
        response.setCurrentMonetaryBalance(subscriberMonetaryBalance);

        this.policyRepository = spy(new PolicyProvider());
        when(policyRepository.getSliceConfiguration()).thenReturn(new DummyPolicyRepository().getSliceConfiguration());
        this.quotaReservation = new QuotaReservation();
        this.executionContext = spy(new ExecutionContext(request, response, mock(CacheAwareDDFTable.class), INR));
        this.gyPolicyContext = spy(new GyPolicyContextImpl(request, response, basePackage, executionContext, policyRepository, 600, null));
    }

    private QuotaProfile createQuotaProfile(SPRInfoImpl sprInfo, String packageId, String subscriptionId, String quotaProfileId, long rgId) {
        return new QuotaProfile("QuotaProfile",
                "PkgName",
                quotaProfileId, BalanceLevel.HSQ, 2, RenewalIntervalUnit.MONTH,
                QuotaProfileType.RnC_BASED, Arrays.asList(createQuotaProfileProfileDetail(sprInfo, packageId, subscriptionId, quotaProfileId, rgId)), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());
    }




    private Map<String, QuotaProfileDetail> createQuotaProfileProfileDetail(SPRInfoImpl sprInfo, String packageId, String subscriptionId, String quotaProfileId, long rgId) {
        RnCQuotaProfileDetail rnCQuotaProfileDetail = spy(new RnCQuotaProfileFactory(quotaProfileId, UUID.randomUUID().toString())
                .randomBalanceWithRate().withRGId(rgId).create());
        Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();
        fupLevelServiceWiseQuotaProfileDetails.put(CommonConstants.ALL_SERVICE_ID, rnCQuotaProfileDetail);

        NonMonetoryBalance nonMonetoryBalance = createNonMonetaryBalance(sprInfo, rnCQuotaProfileDetail, packageId, subscriptionId);
        response.getCurrentNonMonetoryBalance().addBalance(nonMonetoryBalance);
        return fupLevelServiceWiseQuotaProfileDetails;
    }



    private NonMonetoryBalance createZeroNonMonetaryBalance(SPRInfo sprInfo, RnCQuotaProfileDetail rnCQuotaProfileDetail, String packageId, String subscriptionId) {

        long billingCycleTotalVolume = nextLong(2, 1000);
        long billingCycleTotalTime = nextLong(2, 1000);
        return new NonMonetoryBalance.NonMonetaryBalanceBuilder(
                UUID.randomUUID().toString(), rnCQuotaProfileDetail.getDataServiceType().getServiceIdentifier(),
                packageId,
                rnCQuotaProfileDetail.getRatingGroup().getIdentifier(),
                sprInfo.getSubscriberIdentity(),
                subscriptionId,
                0,
                rnCQuotaProfileDetail.getQuotaProfileId(), ResetBalanceStatus.NOT_RESET, null, null)
                .withBillingCycleVolumeBalance(0, 0).
                        withBillingCycleTimeBalance(0, 0)
                .withDailyUsage(0, 0)
                .withWeeklyUsage(0, 0)
                .withDailyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .withWeeklyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(6))
                .withBillingCycleResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .build();

    }


    public class WhenNoApplicablePCCProfileConfiguredInTopUp {
        private RnCQuotaProfileDetail quotaProfileDetail1;
        private RnCQuotaProfileDetail quotaProfileDetail2;
        private RnCQuotaProfileDetail quotaProfileDetail3;
        private MockQuotaTopUp quotaTopUp1;
        private MockQuotaTopUp quotaTopUp2;
        private MockQuotaTopUp quotaTopUp3;
        private List<Subscription> subscriptions;
        private String quotaProfileId1;
        private String quotaProfileId2;
        private String quotaProfileId3;

        @Before
        public void setUp() {
            quotaProfileDetail1 = spy(createRnCQuotaDetail(quotaProfileId1));
            quotaProfileDetail2 = spy(createRnCQuotaDetail(quotaProfileId2));
            quotaProfileDetail3 = spy(createRnCQuotaDetail(quotaProfileId3));

            quotaTopUp1 = createTopUp(TopUpType.TOP_UP);
            quotaTopUp1.setQuotaProfiles(createQuotaProfile(quotaProfileDetail1, quotaProfileId1, quotaTopUp1.getId()));
            quotaTopUp2 = createTopUp(TopUpType.TOP_UP);
            quotaTopUp2.setQuotaProfiles(createQuotaProfile(quotaProfileDetail2, quotaProfileId2, quotaTopUp2.getId()));
            quotaTopUp3 = createTopUp(TopUpType.TOP_UP);
            quotaTopUp3.setQuotaProfiles(createQuotaProfile(quotaProfileDetail3, quotaProfileId3, quotaTopUp3.getId()));

            policyRepository.addTopUp(quotaTopUp1, quotaTopUp2, quotaTopUp3);

            subscriptions = new ArrayList<>();
            subscriptions.add(createSubscription(quotaTopUp1.getId(), quotaTopUp1.getId() + "x"));
            subscriptions.add(createSubscription(quotaTopUp2.getId(), quotaTopUp2.getId() + "x"));
            subscriptions.add(createSubscription(quotaTopUp3.getId(), quotaTopUp3.getId() + "x"));
        }

        private QuotaProfile createQuotaProfile(RnCQuotaProfileDetail quotaProfileDetail1, String quotaProfileId1, String packageId) {
            Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();
            fupLevelServiceWiseQuotaProfileDetails.put(CommonConstants.ALL_SERVICE_ID, quotaProfileDetail1);

            NonMonetoryBalance nonMonetoryBalance = createNonMonetaryBalance(sprInfo, quotaProfileDetail1, packageId, packageId + "x");
            response.getCurrentNonMonetoryBalance().addBalance(nonMonetoryBalance);

            return new QuotaProfile("QuotaProfile",
                    "PkgName",
                    quotaProfileId1, BalanceLevel.HSQ, 2, RenewalIntervalUnit.MONTH,
                    QuotaProfileType.RnC_BASED, Arrays.asList(fupLevelServiceWiseQuotaProfileDetails), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());
        }

        private RnCQuotaProfileDetail createRnCQuotaDetail(String quotaProfileId1) {
            return new RnCQuotaProfileFactory(quotaProfileId1, UUID.randomUUID().toString()).randomBalanceWithRate().create();
        }

        @Test
        public void applyTopUpShouldSkipProcessingWhenOneSubsctiptionIsApplied() {
            Subscription subscription1 = subscriptions.get(0);
            Subscription subscription2 = subscriptions.get(1);
            doReturn(false).when(quotaProfileDetail1).apply(gyPolicyContext, subscription1.getPackageId(), subscription1, quotaReservation);

            assertTrue(rnCTopUpQuotaProfileDetail.applyTopUps(gyPolicyContext, quotaReservation, subscriptions));
            assertTrue(quotaReservation.get().size() == 1);
            assertEquals(quotaProfileId2, getQuotaProfileIdFromReservation());
            verify(quotaProfileDetail1, times(1)).apply(gyPolicyContext, quotaTopUp1.getId(), subscription1, quotaReservation);
            verify(quotaProfileDetail2, times(1)).apply(gyPolicyContext, quotaTopUp2.getId(), subscription2, quotaReservation);
            verify(quotaProfileDetail3, times(0)).apply(gyPolicyContext, quotaTopUp3.getId(), subscriptions.get(2), quotaReservation);
        }

        @Test
        public void applyTopUpShouldReturnFalseWhenAllSubscritionNotApplied() {
            Subscription subscription1 = subscriptions.get(0);
            Subscription subscription2 = subscriptions.get(1);
            Subscription subscription3 = subscriptions.get(2);
            doReturn(false).when(quotaProfileDetail1).apply(gyPolicyContext, subscription1.getPackageId(), subscription1, quotaReservation);
            doReturn(false).when(quotaProfileDetail2).apply(gyPolicyContext, subscription2.getPackageId(), subscription2, quotaReservation);
            doReturn(false).when(quotaProfileDetail3).apply(gyPolicyContext, subscription3.getPackageId(), subscription3, quotaReservation);


            assertFalse(rnCTopUpQuotaProfileDetail.applyTopUps(gyPolicyContext, quotaReservation, subscriptions));
            assertTrue(quotaReservation.get().isEmpty());
            verify(quotaProfileDetail1, times(1)).apply(gyPolicyContext, quotaTopUp1.getId(), subscription1, quotaReservation);
            verify(quotaProfileDetail2, times(1)).apply(gyPolicyContext, quotaTopUp2.getId(), subscription2, quotaReservation);
            verify(quotaProfileDetail3, times(1)).apply(gyPolicyContext, quotaTopUp3.getId(), subscription3, quotaReservation);
        }

    }


    public class WhenApplicablePCCProfileConfiguredInTopUp {

        private RnCQuotaProfileDetail quotaProfileDetail1;
        private RnCQuotaProfileDetail quotaProfileDetail2;
        private RnCQuotaProfileDetail quotaProfileDetail3;
        private MockQuotaTopUp quotaTopUp1;
        private MockQuotaTopUp quotaTopUp2;
        private MockQuotaTopUp quotaTopUp3;
        private List<Subscription> subscriptions;
        private String quotaProfileId1 = UUID.randomUUID().toString();
        private String quotaProfileId2 = UUID.randomUUID().toString();
        private String quotaProfileId3 = UUID.randomUUID().toString();


        @Before
        public void setUp() throws OperationFailedException {
            prepareQuotaProfiles();
        }

        private void prepareQuotaProfiles() {
            quotaProfileDetail1 = spy(createRnCQuotaDetail(quotaProfileId1));
            quotaProfileDetail2 = spy(createRnCQuotaDetail(quotaProfileId2));
            quotaProfileDetail3 = spy(createRnCQuotaDetail(quotaProfileId3));
        }


        @Test
        public void applyTopUpShouldSkipProcessingTopUpWithZeroBalance() throws OperationFailedException {
            quotaTopUp1 = createTopUp(TopUpType.TOP_UP, validApplicablePCCProfiles);
            quotaTopUp1.setQuotaProfiles(createQuotaProfile(quotaProfileDetail1, quotaProfileId1, quotaTopUp1.getId()));
            quotaTopUp2 = createTopUp(TopUpType.TOP_UP, validApplicablePCCProfiles);
            quotaTopUp2.setQuotaProfiles(createQuotaProfile(quotaProfileDetail2, quotaProfileId2, quotaTopUp2.getId(), true));
            quotaTopUp3 = createTopUp(TopUpType.TOP_UP, validApplicablePCCProfiles);
            quotaTopUp3.setQuotaProfiles(createQuotaProfile(quotaProfileDetail3, quotaProfileId3, quotaTopUp3.getId()));

            prepareSubscriptions();

            Subscription subscription1 = subscriptions.get(0);
            Subscription subscription2 = subscriptions.get(1);
            Subscription subscription3 = subscriptions.get(2);
            doReturn(false).when(quotaProfileDetail1).apply(gyPolicyContext, subscription1.getPackageId(), subscription1, quotaReservation);
            assertTrue(rnCTopUpQuotaProfileDetail.apply(gyPolicyContext, basePackage.getId(), null, quotaReservation));

            assertTrue(quotaReservation.get().size() == 1);
            assertEquals(quotaProfileId3, getQuotaProfileIdFromReservation());
            verify(quotaProfileDetail1, times(1)).apply(gyPolicyContext, quotaTopUp1.getId(), subscription1, quotaReservation);
            verify(quotaProfileDetail2, times(1)).apply(gyPolicyContext, quotaTopUp2.getId(), subscription2, quotaReservation);
            verify(quotaProfileDetail3, times(1)).apply(gyPolicyContext, quotaTopUp3.getId(), subscription3, quotaReservation);
        }

        @Test
        public void applyTopUpShouldSkipProcessingTopUpWithInvalidApplicablePCCProfileConfiguration() throws OperationFailedException {

            quotaTopUp1 = createTopUp(TopUpType.TOP_UP, validApplicablePCCProfiles);
            quotaTopUp1.setQuotaProfiles(createQuotaProfile(quotaProfileDetail1, quotaProfileId1, quotaTopUp1.getId()));
            quotaTopUp2 = createTopUp(TopUpType.TOP_UP, inValidApplicablePCCProfiles);
            quotaTopUp2.setQuotaProfiles(createQuotaProfile(quotaProfileDetail2, quotaProfileId2, quotaTopUp2.getId()));
            quotaTopUp3 = createTopUp(TopUpType.TOP_UP, validApplicablePCCProfiles);
            quotaTopUp3.setQuotaProfiles(createQuotaProfile(quotaProfileDetail3, quotaProfileId3, quotaTopUp3.getId()));

            prepareSubscriptions();

            Subscription subscription1 = subscriptions.get(0);
            Subscription subscription2 = subscriptions.get(1);
            Subscription subscription3 = subscriptions.get(2);
            doReturn(false).when(quotaProfileDetail1).apply(gyPolicyContext, subscription1.getPackageId(), subscription1, quotaReservation);
            assertTrue(rnCTopUpQuotaProfileDetail.apply(gyPolicyContext, basePackage.getId(), null, quotaReservation));

            assertTrue(quotaReservation.get().size() == 1);
            assertEquals(quotaProfileId3, getQuotaProfileIdFromReservation());
            verify(quotaProfileDetail1, times(1)).apply(gyPolicyContext, quotaTopUp1.getId(), subscription1, quotaReservation);
            verify(quotaProfileDetail2, times(0)).apply(gyPolicyContext, quotaTopUp2.getId(), subscription2, quotaReservation);
            verify(quotaProfileDetail3, times(1)).apply(gyPolicyContext, quotaTopUp3.getId(), subscription3, quotaReservation);
        }

        private void prepareSubscriptions() throws OperationFailedException {
            policyRepository.addTopUp(quotaTopUp1, quotaTopUp2, quotaTopUp3);

            subscriptions = new ArrayList<>();
            Subscription subscription1 = createSubscription(quotaTopUp1.getId(), quotaTopUp1.getId() + "x");
            Subscription subscription2 = createSubscription(quotaTopUp2.getId(), quotaTopUp2.getId() + "x");
            Subscription subscription3 = createSubscription(quotaTopUp3.getId(), quotaTopUp3.getId() + "x");
            createSubscription(quotaTopUp1.getId(), quotaTopUp1.getId() + "x");
            subscriptions.add(subscription1);
            subscriptions.add(subscription2);
            subscriptions.add(subscription3);

            LinkedHashMap<String, Subscription> map = new LinkedHashMap<>();
            map.put(subscription1.getId(), subscription1);
            map.put(subscription2.getId(), subscription2);
            map.put(subscription3.getId(), subscription3);
            doReturn(map).when(gyPolicyContext).getSubscriptions();
        }


    }

    private QuotaProfile createQuotaProfile(RnCQuotaProfileDetail quotaProfileDetail, String quotaProfileId, String packageId) {
        return createQuotaProfile(quotaProfileDetail, quotaProfileId, packageId, false);

    }

    private QuotaProfile createQuotaProfile(RnCQuotaProfileDetail quotaProfileDetail1, String quotaProfileId1, String packageId, boolean withZeroBalance) {
        Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();
        fupLevelServiceWiseQuotaProfileDetails.put(CommonConstants.ALL_SERVICE_ID, quotaProfileDetail1);

        NonMonetoryBalance nonMonetoryBalance;
        if (withZeroBalance) {
            nonMonetoryBalance = createZeroNonMonetaryBalance(sprInfo, quotaProfileDetail1, packageId, packageId + "x");
        } else {
            nonMonetoryBalance = createNonMonetaryBalance(sprInfo, quotaProfileDetail1, packageId, packageId + "x");
        }
        response.getCurrentNonMonetoryBalance().addBalance(nonMonetoryBalance);

        return new QuotaProfile("QuotaProfile",
                "PkgName",
                quotaProfileId1, BalanceLevel.HSQ, 2, RenewalIntervalUnit.MONTH,
                QuotaProfileType.RnC_BASED, Arrays.asList(fupLevelServiceWiseQuotaProfileDetails), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());
    }



    public long getRgIdFromReservation() {
        return quotaReservation.get().iterator().next().getValue().getRatingGroup();
    }

    private String getQuotaProfileIdFromReservation() {
        return quotaReservation.get().iterator().next().getValue().getGrantedServiceUnits().getQuotaProfileIdOrRateCardId();
    }


    private Subscription createSubscription(String packageId, String subscriptionId) {
        return new Subscription.SubscriptionBuilder().withId(subscriptionId)
                .withPackageId(packageId)
                .withEndTime(new Timestamp(new Date().getTime() + TimeUnit.DAY.toSeconds(1) * 1000))
                .withStartTime(new Timestamp(new Date().getTime()))
                .build();
    }

}
