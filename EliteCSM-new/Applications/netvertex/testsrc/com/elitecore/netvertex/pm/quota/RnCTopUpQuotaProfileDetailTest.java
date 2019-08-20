package com.elitecore.netvertex.pm.quota;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.pd.topup.TopUpType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.util.MockQuotaTopUp;
import com.elitecore.corenetvertex.pm.util.PolicyProvider;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.elitecore.netvertex.pm.quota.RncTopUpQuotaProfileDetailTestUtil.createMonetaryBalance;
import static com.elitecore.netvertex.pm.quota.RncTopUpQuotaProfileDetailTestUtil.createNonMonetaryBalance;
import static com.elitecore.netvertex.pm.quota.RncTopUpQuotaProfileDetailTestUtil.createTopUp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class RnCTopUpQuotaProfileDetailTest {

    public static final String INR = "INR";
    private static final String pccProfileName = "PCC_PROFILE_name";
    private static final String UNKNOWN = "UNKNOWN";
    private RnCTopUpQuotaProfileDetail rnCTopUpQuotaProfileDetail;
    private PCRFRequest request;
    private PCRFResponse response;
    private QuotaReservation quotaReservation;
    private MockBasePackage basePackage;
    private GyPolicyContextImpl gyPolicyContext;
    private ExecutionContext executionContext;
    private PolicyProvider policyRepository;
    private MockQuotaTopUp preTopUp;
    private MockQuotaTopUp spareTopUp;
    private static final String PRE_SUBSCRIPION_ID = "1";
    private static final String SPARE_SUBSCRIPION_ID = "2";
    private SPRInfoImpl sprInfo;
    private String baseQuotaProfileId;
    private String preTopUpQuotaProfileId;
    private String spareTopUpQuotaProfileId;
    private long baseRGId = 101;
    private long preTopUpRGId = 102;
    private long spareTopUpRGId = 103;

    @Before
    public void setUp() {

        String subscriberID = "subscriberID";
        baseQuotaProfileId = UUID.randomUUID().toString();
        preTopUpQuotaProfileId = UUID.randomUUID().toString();
        spareTopUpQuotaProfileId = UUID.randomUUID().toString();

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

        preTopUp = createTopUp(TopUpType.TOP_UP);
        QuotaProfile preTopUpQuotaProfile = createQuotaProfile(sprInfo, preTopUp.getId(), PRE_SUBSCRIPION_ID, preTopUpQuotaProfileId, preTopUpRGId);
        preTopUp.setQuotaProfiles(preTopUpQuotaProfile);
        spareTopUp = createTopUp(TopUpType.SPARE_TOP_UP);
        QuotaProfile spareTopUpQuotaProfile = createQuotaProfile(sprInfo, spareTopUp.getId(), SPARE_SUBSCRIPION_ID, spareTopUpQuotaProfileId, spareTopUpRGId);
        spareTopUp.setQuotaProfiles(spareTopUpQuotaProfile);
        policyRepository.addTopUp(preTopUp);
        policyRepository.addTopUp(spareTopUp);
    }



    public class NoSubscriptions {
        @Test
        public void applyTopUpReturnsFalse() {
            assertFalse(rnCTopUpQuotaProfileDetail.applyTopUps(gyPolicyContext, quotaReservation, Collectionz.newArrayList()));
            assertTrue(quotaReservation.get().isEmpty());
        }
    }

    public class PreSubscriptions {
       private Subscription preSubscription;

        @Before
        public void setUp() throws OperationFailedException {
            preSubscription = createSubscription(preTopUp.getId(), PRE_SUBSCRIPION_ID);
            LinkedHashMap<String, Subscription> map = new LinkedHashMap<>();
            map.put(preSubscription.getId(), preSubscription);
            doReturn(map).when(gyPolicyContext).getSubscriptions();
        }

        @Test
        public void preTopUpQuotaShouldBeSelectedWhenPreTopUpIsApplied() {

            assertTrue(rnCTopUpQuotaProfileDetail.apply(gyPolicyContext, basePackage.getId(), null, quotaReservation));
            assertTrue(quotaReservation.get().size() == 1);
            assertEquals(preTopUpQuotaProfileId, getQuotaProfileIdFromReservation());
            assertEquals(baseRGId, getRgIdFromReservation());
            verify(rnCTopUpQuotaProfileDetail, never()).applyBasePackage(gyPolicyContext, basePackage.getId(), null, quotaReservation);
            verify(rnCTopUpQuotaProfileDetail, times(1)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getPreTopUpSubscriptions());
        }

        @Test
        public void basePackageQuotaShouldBeSelectedWhenPreTopUpFailed() {
            setPreSubscriptionApply(false);
            assertTrue(rnCTopUpQuotaProfileDetail.apply(gyPolicyContext, basePackage.getId(), null, quotaReservation));
            assertTrue(quotaReservation.get().size() == 1);
            assertEquals(baseQuotaProfileId, getQuotaProfileIdFromReservation());
            assertEquals(baseRGId, getRgIdFromReservation());
            verify(rnCTopUpQuotaProfileDetail, times(1)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getPreTopUpSubscriptions());
            verify(rnCTopUpQuotaProfileDetail, times(0)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getSpareTopUpSubscriptions());
            verify(rnCTopUpQuotaProfileDetail, times(1)).applyBasePackage(gyPolicyContext, basePackage.getId(), null, quotaReservation);
        }

        @Test
        public void skipApplyingTopUpIfPreTopIsAlreadyApplied() {
            gyPolicyContext.preTopUpChecked();
            rnCTopUpQuotaProfileDetail.apply(gyPolicyContext, basePackage.getId(), null, quotaReservation);
            assertTrue(quotaReservation.get().size() == 1);
            assertEquals(baseRGId, getRgIdFromReservation());
            assertEquals(baseQuotaProfileId, getQuotaProfileIdFromReservation());
            verify(rnCTopUpQuotaProfileDetail, times(0)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getPreTopUpSubscriptions());
            verify(rnCTopUpQuotaProfileDetail, times(0)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getSpareTopUpSubscriptions());
            verify(rnCTopUpQuotaProfileDetail, times(1)).applyBasePackage(gyPolicyContext, basePackage.getId(), null, quotaReservation);
        }


    }


    public class SpareSubscriptions {
        Subscription spareSubscription;

        @Before
        public void setUp() throws OperationFailedException {
            spareSubscription = createSubscription(spareTopUp.getId(), SPARE_SUBSCRIPION_ID);
            LinkedHashMap<String, Subscription> map = new LinkedHashMap<>();
            map.put(spareSubscription.getId(), spareSubscription);
            doReturn(map).when(gyPolicyContext).getSubscriptions();
        }

        @Test
        public void spareTopUpApplyShouldCallWhenPreAndBaseNotApplied() {
            setPreSubscriptionApply(false);
            setBaseApplied(false);

            rnCTopUpQuotaProfileDetail.apply(gyPolicyContext, basePackage.getId(), null, quotaReservation);
            assertTrue(quotaReservation.get().size() == 1);
            assertEquals(spareTopUpQuotaProfileId, getQuotaProfileIdFromReservation());
            assertEquals(baseRGId, getRgIdFromReservation());
            verify(rnCTopUpQuotaProfileDetail, times(1)).applyBasePackage(gyPolicyContext, basePackage.getId(), null, quotaReservation);
            verify(rnCTopUpQuotaProfileDetail, times(1)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getPreTopUpSubscriptions());
            verify(rnCTopUpQuotaProfileDetail, times(1)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getSpareTopUpSubscriptions());
        }

        @Test
        public void spareTopUpShouldNotApplyIfCurrentLevelIsNotHSQ() {
            setPreSubscriptionApply(false);
            setBaseApplied(false);
            doReturn(false).when(rnCTopUpQuotaProfileDetail).isHsqLevel();

            assertFalse(rnCTopUpQuotaProfileDetail.apply(gyPolicyContext, basePackage.getId(), null, quotaReservation));
            assertTrue(quotaReservation.get().size() == 0);
            verify(rnCTopUpQuotaProfileDetail, times(1)).applyBasePackage(gyPolicyContext, basePackage.getId(), null, quotaReservation);
            verify(rnCTopUpQuotaProfileDetail, times(1)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getPreTopUpSubscriptions());
            verify(rnCTopUpQuotaProfileDetail, times(0)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getSpareTopUpSubscriptions());
        }
    }


    public class WhenApplicablePCCProfilesConfiguredInTopUP {

        Subscription preSubscription;


        public class PreTopUp {

            @Test
            public void selectedIfApplicablePCCProfileMatch() throws OperationFailedException {
                setupPreTopUpSubscriptionsWithApplicablePCCProfiles(Arrays.asList(new String[]{pccProfileName}));
                assertTrue(rnCTopUpQuotaProfileDetail.apply(gyPolicyContext, basePackage.getId(), null, quotaReservation));
                assertTrue(quotaReservation.get().size() == 1);
                assertEquals(preTopUpQuotaProfileId, getQuotaProfileIdFromReservation());
                assertEquals(baseRGId, getRgIdFromReservation());
                verify(rnCTopUpQuotaProfileDetail, times(1)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getPreTopUpSubscriptions());
                verify(rnCTopUpQuotaProfileDetail, times(0)).applyBasePackage(gyPolicyContext, basePackage.getId(), null, quotaReservation);
            }


            @Test
            public void selectedIfApplicablePCCProfileIsNotConfiguredInQuotaTopUp() throws OperationFailedException {
                setupPreTopUpSubscriptionsWithApplicablePCCProfiles(Collections.emptyList());
                assertTrue(rnCTopUpQuotaProfileDetail.apply(gyPolicyContext, basePackage.getId(), null, quotaReservation));
                assertTrue(quotaReservation.get().size() == 1);
                assertEquals(preTopUpQuotaProfileId, getQuotaProfileIdFromReservation());
                assertEquals(baseRGId, getRgIdFromReservation());
                verify(rnCTopUpQuotaProfileDetail, times(1)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getPreTopUpSubscriptions());
                verify(rnCTopUpQuotaProfileDetail, times(0)).applyBasePackage(gyPolicyContext, basePackage.getId(), null, quotaReservation);
            }


            @Test
            public void notSelectedIfApplicablePCCProfileDoesnotMatch() throws OperationFailedException {
                setupPreTopUpSubscriptionsWithApplicablePCCProfiles(Arrays.asList(new String[]{pccProfileName + UNKNOWN}));
                assertTrue(rnCTopUpQuotaProfileDetail.apply(gyPolicyContext, basePackage.getId(), null, quotaReservation));
                assertTrue(quotaReservation.get().size() == 1);
                assertNotEquals(preTopUpQuotaProfileId, getQuotaProfileIdFromReservation());
                assertEquals(baseQuotaProfileId, getQuotaProfileIdFromReservation());
                assertEquals(baseRGId, getRgIdFromReservation());
                verify(rnCTopUpQuotaProfileDetail, times(1)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getPreTopUpSubscriptions());
                verify(rnCTopUpQuotaProfileDetail, times(1)).applyBasePackage(gyPolicyContext, basePackage.getId(), null, quotaReservation);
            }

            private void setupPreTopUpSubscriptionsWithApplicablePCCProfiles(List<String> applicablePCCProfiles) throws OperationFailedException {
                preTopUp = createTopUp(TopUpType.TOP_UP, applicablePCCProfiles);
                QuotaProfile preTopUpQuotaProfile = createQuotaProfile(sprInfo, preTopUp.getId(), PRE_SUBSCRIPION_ID, preTopUpQuotaProfileId, preTopUpRGId);
                preTopUp.setQuotaProfiles(preTopUpQuotaProfile);
                policyRepository.addTopUp(preTopUp);
                preSubscription = createSubscription(preTopUp.getId(), PRE_SUBSCRIPION_ID);
                LinkedHashMap<String, Subscription> map = new LinkedHashMap<>();
                map.put(preSubscription.getId(), preSubscription);
                doReturn(map).when(gyPolicyContext).getSubscriptions();
            }
        }

        public class SpareTopUp {

            Subscription spareSubscription;

            @Test
            public void selectedIfApplicablePCCProfileMatch() throws OperationFailedException {
                setupWithSpareTopUpSubscriptionsWithApplicablePCCProfiles(Arrays.asList(new String[]{pccProfileName}));
                setPreSubscriptionApply(false);
                setBaseApplied(false);
                doReturn(true).when(rnCTopUpQuotaProfileDetail).isHsqLevel();

                assertTrue(rnCTopUpQuotaProfileDetail.apply(gyPolicyContext, basePackage.getId(), null, quotaReservation));
                assertTrue(quotaReservation.get().size() == 1);
                assertEquals(spareTopUpQuotaProfileId, getQuotaProfileIdFromReservation());
                assertEquals(baseRGId, getRgIdFromReservation());

                verify(rnCTopUpQuotaProfileDetail, times(1)).applyBasePackage(gyPolicyContext, basePackage.getId(), null, quotaReservation);
                verify(rnCTopUpQuotaProfileDetail, times(1)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getPreTopUpSubscriptions());
                verify(rnCTopUpQuotaProfileDetail, times(1)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getSpareTopUpSubscriptions());
            }

            @Test
            public void selectedIfApplicablePCCProfileIsNotconfiguredInTopUp() throws OperationFailedException {
                setupWithSpareTopUpSubscriptionsWithApplicablePCCProfiles(Collections.emptyList());
                setPreSubscriptionApply(false);
                setBaseApplied(false);

                doReturn(true).when(rnCTopUpQuotaProfileDetail).isHsqLevel();
                assertTrue(rnCTopUpQuotaProfileDetail.apply(gyPolicyContext, basePackage.getId(), null, quotaReservation));
                assertTrue(quotaReservation.get().size() == 1);
                assertEquals(spareTopUpQuotaProfileId, getQuotaProfileIdFromReservation());
                assertEquals(baseRGId, getRgIdFromReservation());

                verify(rnCTopUpQuotaProfileDetail, times(1)).applyBasePackage(gyPolicyContext, basePackage.getId(), null, quotaReservation);
                verify(rnCTopUpQuotaProfileDetail, times(1)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getPreTopUpSubscriptions());
                verify(rnCTopUpQuotaProfileDetail, times(1)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getSpareTopUpSubscriptions());
            }


            @Test
            public void notSelectedIfApplicablePCCProfileDoesotMatch() throws OperationFailedException {
                setupWithSpareTopUpSubscriptionsWithApplicablePCCProfiles(Arrays.asList(new String[]{pccProfileName + UNKNOWN}));
                setPreSubscriptionApply(false);
                setBaseApplied(false);

                doReturn(true).when(rnCTopUpQuotaProfileDetail).isHsqLevel();
                assertFalse(rnCTopUpQuotaProfileDetail.apply(gyPolicyContext, basePackage.getId(), null, quotaReservation));
                assertTrue(quotaReservation.get().isEmpty());

                verify(rnCTopUpQuotaProfileDetail, times(1)).applyBasePackage(gyPolicyContext, basePackage.getId(), null, quotaReservation);
                verify(rnCTopUpQuotaProfileDetail, times(1)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getPreTopUpSubscriptions());
                verify(rnCTopUpQuotaProfileDetail, times(1)).applyTopUps(gyPolicyContext, quotaReservation, gyPolicyContext.getSpareTopUpSubscriptions());
            }

            private void setupWithSpareTopUpSubscriptionsWithApplicablePCCProfiles(List<String> applicablePCCProfiles) throws OperationFailedException {
                spareTopUp = createTopUp(TopUpType.SPARE_TOP_UP, applicablePCCProfiles);
                QuotaProfile preTopUpQuotaProfile = createQuotaProfile(sprInfo, spareTopUp.getId(), SPARE_SUBSCRIPION_ID, spareTopUpQuotaProfileId, spareTopUpRGId);
                spareTopUp.setQuotaProfiles(preTopUpQuotaProfile);
                policyRepository.addTopUp(spareTopUp);
                spareSubscription = createSubscription(spareTopUp.getId(), SPARE_SUBSCRIPION_ID);
                LinkedHashMap<String, Subscription> map = new LinkedHashMap<>();
                map.put(spareSubscription.getId(), spareSubscription);
                doReturn(map).when(gyPolicyContext).getSubscriptions();
            }

        }
    }

    private String getQuotaProfileIdFromReservation() {
        return quotaReservation.get().iterator().next().getValue().getGrantedServiceUnits().getQuotaProfileIdOrRateCardId();
    }

    private void setPreSubscriptionApply(boolean isTrue) {
        Collection<Subscription> preTopUpSubscriptions = gyPolicyContext.getPreTopUpSubscriptions();
        doReturn(isTrue).when(rnCTopUpQuotaProfileDetail).applyTopUps(gyPolicyContext, quotaReservation, preTopUpSubscriptions);
    }

    private Subscription createSubscription(String packageId, String subscriptionId) {
        return new Subscription.SubscriptionBuilder().withId(subscriptionId)
                .withPackageId(packageId)
                .withEndTime(new Timestamp(new Date().getTime() + TimeUnit.DAY.toSeconds(1) * 1000))
                .withStartTime(new Timestamp(new Date().getTime()))
                .build();
    }

    private String setBaseApplied(boolean isTrue) {
        String basePackageId = basePackage.getId();
        doReturn(isTrue).when(rnCTopUpQuotaProfileDetail).applyBasePackage(gyPolicyContext, basePackageId, null, quotaReservation);
        return basePackageId;
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



    public long getRgIdFromReservation() {
        return quotaReservation.get().iterator().next().getValue().getRatingGroup();
    }

}
