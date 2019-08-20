package com.elitecore.netvertex.pm.quota;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.util.PolicyProvider;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.FinalUnitAction;
import com.elitecore.netvertex.gateway.diameter.gy.FinalUnitIndication;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.RnCQuotaProfileFactory;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.elitecore.netvertex.pm.quota.GrantedMsccMatches.hasTimeThreshold;
import static com.elitecore.netvertex.pm.quota.GrantedMsccMatches.hasVolumeThreshold;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class RnCQuotaProfileDetailTest {

    public static final String INR = "INR";
    private RnCQuotaProfileDetail rnCQuotaProfileDetailForHybrid;
    private RnCQuotaProfileDetail rnCQuotaProfileDetailForVolume;
    private RnCQuotaProfileDetail rnCQuotaProfileDetailForTime;
    private PCRFRequest request;
    private PCRFResponse response;
    private QuotaReservation quotaReservation;
    private Subscription subscription;
    private MockBasePackage basePackage;
    private GyPolicyContextImpl gyPolicyContext;
    private ExecutionContext executionContext;
    private PolicyRepository policyRepository;
    private MonetaryBalance monetaryBalance;
    private NonMonetoryBalance nonMonetoryBalance;
    private NonMonetoryBalance volumeNonMonetoryBalance;
    private NonMonetoryBalance timeNonMonetoryBalance;
    private SubscriberMonetaryBalance subscriberMonetaryBalance;
    private String quotaProfileId;

    @Rule
    public PrintMethodRule printMethodRule = new PrintMethodRule();

    @Before
    public void setUp() {
        quotaProfileId = UUID.randomUUID().toString();
        String subscriberID = "subscriberID";
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberID);
        request = new PCRFRequestImpl();
        request.setSPRInfo(sprInfo);
        response = new PCRFResponseImpl();
        basePackage = MockBasePackage.create(UUID.randomUUID().toString(), UUID.randomUUID().toString() + "name");
        this.rnCQuotaProfileDetailForHybrid = new RnCQuotaProfileFactory(quotaProfileId, UUID.randomUUID().toString()).randomBalanceWithRate().create();
        this.rnCQuotaProfileDetailForVolume = new RnCQuotaProfileFactory(UUID.randomUUID().toString() , UUID.randomUUID().toString()).randomBalanceWithRate().withRateOn(UsageType.VOLUME).createQuotaProfileDetailForVolume();
        this.rnCQuotaProfileDetailForTime = new RnCQuotaProfileFactory(UUID.randomUUID().toString(), UUID.randomUUID().toString()).randomBalanceWithRate().withRateOn(UsageType.TIME).createQuotaProfileDetailForTime();
        this.nonMonetoryBalance = createNonMonetaryBalance(sprInfo, rnCQuotaProfileDetailForHybrid);
        this.volumeNonMonetoryBalance = createNonMonetaryBalance(sprInfo, rnCQuotaProfileDetailForVolume);
        this.timeNonMonetoryBalance = createNonMonetaryBalance(sprInfo, rnCQuotaProfileDetailForTime);

        List<NonMonetoryBalance> nonMonetoryBalances = new ArrayList<>();
        nonMonetoryBalances.add(nonMonetoryBalance);
        nonMonetoryBalances.add(volumeNonMonetoryBalance);
        nonMonetoryBalances.add(timeNonMonetoryBalance);

        SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(nonMonetoryBalances);
        response.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);

        this.subscriberMonetaryBalance = spy(new SubscriberMonetaryBalance(new FixedTimeSource(System.currentTimeMillis())));
        this.monetaryBalance = spy(createMonetaryBalance("test"));
        subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
        response.setCurrentMonetaryBalance(subscriberMonetaryBalance);

        this.policyRepository = spy(new PolicyProvider());
        when(policyRepository.getSliceConfiguration()).thenReturn(new DummyPolicyRepository().getSliceConfiguration());
        this.quotaReservation = new QuotaReservation();
        this.subscription = new Subscription.SubscriptionBuilder().withPackageId(basePackage.getId()).withSubscriberIdentity(subscriberID).build();
        this.executionContext = spy(new ExecutionContext(request, response, mock(CacheAwareDDFTable.class), INR));
        this.gyPolicyContext = spy(new GyPolicyContextImpl(request, response, basePackage, executionContext, policyRepository,600, null));
    }

    private NonMonetoryBalance createNonMonetaryBalance(SPRInfo sprInfo, RnCQuotaProfileDetail rnCQuotaProfileDetail) {

        long billingCycleTotalVolume = nextLong(2, 1000);
        long billingCycleTotalTime = nextLong(2, 1000);
        return new NonMonetoryBalance.NonMonetaryBalanceBuilder(
                UUID.randomUUID().toString(), rnCQuotaProfileDetail.getDataServiceType().getServiceIdentifier(),
                basePackage.getId(),
                rnCQuotaProfileDetail.getRatingGroup().getIdentifier(),
                sprInfo.getSubscriberIdentity(),
                null,
                0,
                rnCQuotaProfileDetail.getQuotaProfileId(), ResetBalanceStatus.NOT_RESET, null, null).
                withBillingCycleVolumeBalance(billingCycleTotalVolume, nextLong(2, billingCycleTotalVolume)).
                withBillingCycleTimeBalance(billingCycleTotalTime, nextLong(2, billingCycleTotalTime))
                .withDailyUsage(nextLong(2, billingCycleTotalVolume), nextLong(2, billingCycleTotalTime))
                .withWeeklyUsage(nextLong(2, billingCycleTotalVolume), nextLong(2, billingCycleTotalTime))
                .withDailyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .withWeeklyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(6))
                .withBillingCycleResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .build();
    }

    private MonetaryBalance createMonetaryBalance(String subscriberId) {
        int totalBalance = nextInt(3, 1000);
        int availableBalance = nextInt(2, totalBalance);
        int reservation = nextInt(1, availableBalance);
        return new MonetaryBalance(UUID.randomUUID().toString(),
                subscriberId,
                null,
                availableBalance,
                totalBalance,
                reservation,0, 0,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString(),null,
                System.currentTimeMillis(),
                0,
                "", "");
    }

    public class applyFalseWhen {
        @Test
        public void exceptionThrownWhileFetchingNonMonetaryBalance() throws Exception {
            doThrow(new OperationFailedException("SERVICE_UNAVAILABLE", ResultCode.SERVICE_UNAVAILABLE)).when(executionContext).getCurrentNonMonetoryBalance();
            runAndAssert();
        }

        private void runAndAssert() {
            assertFalse(rnCQuotaProfileDetailForHybrid.apply(gyPolicyContext, basePackage.getId(), subscription, quotaReservation));
            assertTrue(quotaReservation.get().isEmpty());
        }

        @Test
        public void exceptionThrownWhileFetchingMonetaryBalance() throws Exception {
            doThrow(new OperationFailedException("SERVICE_UNAVAILABLE", ResultCode.SERVICE_UNAVAILABLE)).when(executionContext).getCurrentMonetaryBalance();
            runAndAssert();
        }

        public class balanceIsNotExist {

            @Test
            public void hybrid_nonMonetaryBalance_BillingCycleAvailableVolumeIsZero() {
                nonMonetoryBalance.setBillingCycleAvailableVolume(0l);
                runAndAssert();
            }

            @Test
            public void hybrid_nonMonetaryBalance_BillingCycleAvailableVolumeIsLessThanZero() {
                nonMonetoryBalance.setBillingCycleAvailableVolume(-1l);
                runAndAssert();
            }

            @Test
            public void hybrid_nonMonetaryBalance_BillingCycleAvailableTimeIsZero() {
                nonMonetoryBalance.setBillingCycleAvailableTime(0l);
                runAndAssert();
            }

            @Test
            public void hybrid_nonMonetaryBalance_BillingCycleAvailableTimeIsLessThanZero() {
                nonMonetoryBalance.setBillingCycleAvailableTime(-1l);
                runAndAssert();
            }

            @Test
            public void volume_nonMonetaryBalance_BillingCycleAvailableVolumeIsZero() {
                volumeNonMonetoryBalance.setBillingCycleAvailableVolume(0l);
                assertFalse(rnCQuotaProfileDetailForVolume.apply(gyPolicyContext, basePackage.getId(), subscription, quotaReservation));
                assertTrue(quotaReservation.get().isEmpty());
            }

            @Test
            public void volume_nonMonetaryBalance_BillingCycleAvailableVolumeIsLessThanZero() {
                volumeNonMonetoryBalance.setBillingCycleAvailableVolume(-1l);
                assertFalse(rnCQuotaProfileDetailForVolume.apply(gyPolicyContext, basePackage.getId(), subscription, quotaReservation));
                assertTrue(quotaReservation.get().isEmpty());
            }

            @Test
            public void time_nonMonetaryBalance_BillingCycleAvailableTimeIsZero() {
                timeNonMonetoryBalance.setBillingCycleAvailableTime(0l);
                assertFalse(rnCQuotaProfileDetailForTime.apply(gyPolicyContext, basePackage.getId(), subscription, quotaReservation));
                assertTrue(quotaReservation.get().isEmpty());
            }

            @Test
            public void time_nonMonetaryBalance_BillingCycleAvailableTimeIsLessThanZero() {
                timeNonMonetoryBalance.setBillingCycleAvailableTime(-1l);
                assertFalse(rnCQuotaProfileDetailForTime.apply(gyPolicyContext, basePackage.getId(), subscription, quotaReservation));
                assertTrue(quotaReservation.get().isEmpty());
            }

            @Test
            public void monetaryBalanceIsNull() throws Exception {
                doReturn(null).when(subscriberMonetaryBalance).getServiceBalance(PCRFKeyValueConstants.DATA_SERVICE_ID.val);
                runAndAssert();
            }

            @Test
            public void monetaryBalanceIsZeroAndRateDefined() {
                monetaryBalance.setAvailBalance(0.0d);
                runAndAssert();
            }

            @Test
            public void monetaryBalanceIsLessThanZeroAndRateDefined() {
                doReturn(-1.0d).when(monetaryBalance).getActualBalance();
                runAndAssert();
            }

            @Test
            public void creditLimitIsReachedAndRateDefined() {
                monetaryBalance.setAvailBalance(-100.0d);
                monetaryBalance.setCreditLimit(100);
                runAndAssert();
            }

            @Test
            public void overUsageWithCreditLimitAndRateDefined() {
                monetaryBalance.setAvailBalance(-110.0d);
                monetaryBalance.setCreditLimit(100);
                runAndAssert();
            }
        }

        public class timeQuotaDefinedAndVolumeUndefined {

            @Before
            public void setUp() {
                nonMonetoryBalance.setBillingCycleTotalVolume(CommonConstants.QUOTA_UNDEFINED);
            }

            @Test
            public void dailyTimeLimitReached() {
                nonMonetoryBalance.setDailyTime(rnCQuotaProfileDetailForHybrid.getDailyAllowedUsage().getTimeInSeconds() + 1);
                runAndAssert();
            }

            @Test
            public void weeklyTimeLimitReached() {
                nonMonetoryBalance.setWeeklyTime(rnCQuotaProfileDetailForHybrid.getWeeklyAllowedUsage().getTimeInSeconds() + 1);
                runAndAssert();
            }

            @Test
            public void bothTimeLimitReached() {
                nonMonetoryBalance.setDailyTime(rnCQuotaProfileDetailForHybrid.getDailyAllowedUsage().getTimeInSeconds() + 1);
                nonMonetoryBalance.setWeeklyTime(rnCQuotaProfileDetailForHybrid.getWeeklyAllowedUsage().getTimeInSeconds() + 1);
                runAndAssert();
            }
        }

        public class timeQuotaUndefinedAndVolumeDefined {

            @Before
            public void setUp() {
                nonMonetoryBalance.setBillingCycleTime(CommonConstants.QUOTA_UNDEFINED);
            }

            @Test
            public void dailyVolumeLimitReached() {
                nonMonetoryBalance.setDailyVolume(rnCQuotaProfileDetailForHybrid.getDailyAllowedUsage().getTotalInBytes() + 1);
                runAndAssert();
            }

            @Test
            public void weeklyVolumeLimitReached() {
                nonMonetoryBalance.setWeeklyVolume(rnCQuotaProfileDetailForHybrid.getWeeklyAllowedUsage().getTotalInBytes() + 1);
                runAndAssert();
            }

            @Test
            public void bothVolumeLimitReached() {
                nonMonetoryBalance.setDailyVolume(rnCQuotaProfileDetailForHybrid.getDailyAllowedUsage().getTotalInBytes() + 1);
                nonMonetoryBalance.setWeeklyTime(rnCQuotaProfileDetailForHybrid.getWeeklyAllowedUsage().getTotalInBytes() + 1);
                runAndAssert();
            }
        }

        // both Defined
        public class timeQuotaDefinedAndVolumeDefined {

            @Test
            public void dailyVolumeLimitReached() {
                nonMonetoryBalance.setDailyVolume(rnCQuotaProfileDetailForHybrid.getDailyAllowedUsage().getTotalInBytes() + 1);
                runAndAssert();
            }

            @Test
            public void dailyTimeLimitReached() {
                nonMonetoryBalance.setDailyTime(rnCQuotaProfileDetailForHybrid.getDailyAllowedUsage().getTimeInSeconds() + 1);
                runAndAssert();
            }

            @Test
            public void weeklyVolumeLimitReached() {
                nonMonetoryBalance.setWeeklyVolume(rnCQuotaProfileDetailForHybrid.getWeeklyAllowedUsage().getTotalInBytes() + 1);
                runAndAssert();
            }

            @Test
            public void weeklyTimeLimitReached() {
                nonMonetoryBalance.setWeeklyTime(rnCQuotaProfileDetailForHybrid.getWeeklyAllowedUsage().getTimeInSeconds() + 1);
                runAndAssert();
            }
        }
    }

    public class applyTrueWhen {

        public class timeQuotaUndefinedAndVolumeUndefined {

            @Before
            public void setUp() {
                nonMonetoryBalance.setBillingCycleTotalVolume(CommonConstants.QUOTA_UNDEFINED);
                nonMonetoryBalance.setBillingCycleTime(CommonConstants.QUOTA_UNDEFINED);
            }

            @Test
            public void dailyVolumeLimitReached() {
                nonMonetoryBalance.setDailyVolume(rnCQuotaProfileDetailForHybrid.getDailyAllowedUsage().getTotalInBytes() + 1);
                runAndAssert();
            }

            @Test
            public void dailyTimeLimitReached() {
                nonMonetoryBalance.setDailyTime(rnCQuotaProfileDetailForHybrid.getDailyAllowedUsage().getTimeInSeconds() + 1);
                runAndAssert();
            }

            @Test
            public void weeklyVolumeLimitReached() {
                nonMonetoryBalance.setWeeklyVolume(rnCQuotaProfileDetailForHybrid.getWeeklyAllowedUsage().getTotalInBytes() + 1);
                runAndAssert();
            }

            @Test
            public void weeklyTimeLimitReached() {
                nonMonetoryBalance.setWeeklyTime(rnCQuotaProfileDetailForHybrid.getWeeklyAllowedUsage().getTimeInSeconds() + 1);
                runAndAssert();
            }
        }

        @Test
        public void monetaryAndNonMonetaryBalanceExist() throws Exception {
            assertTrue(rnCQuotaProfileDetailForHybrid.apply(gyPolicyContext, basePackage.getId(), subscription, quotaReservation));
        }

        @Test
        public void onlyCreditLimitAndNonMonetaryBalanceExist() throws Exception {
            // balance zero
            doReturn(0d).when(monetaryBalance).getAvailBalance();
            // Credit limit
            doReturn(500L).when(monetaryBalance).getCreditLimit();
            assertTrue(rnCQuotaProfileDetailForHybrid.apply(gyPolicyContext, basePackage.getId(), subscription, quotaReservation));
        }

        private void runAndAssert() {
            assertTrue(rnCQuotaProfileDetailForHybrid.apply(gyPolicyContext, basePackage.getId(), subscription, quotaReservation));
            // size one means one rating group reserved
            assertTrue(quotaReservation.get().size() == 1);
            assertEquals(quotaProfileId, getQuotaProfileIdFromReservation());
        }

        @Test
        public void monetaryBalanceIsLessThanZeroAndRateNotDefined() {
            rnCQuotaProfileDetailForHybrid = spy(rnCQuotaProfileDetailForHybrid);
            //setting rate zero
            doReturn(0.0d).when(rnCQuotaProfileDetailForHybrid).getRate();

            // balance negative
            doReturn(-1.0d).when(monetaryBalance).getAvailBalance();
            runAndAssert();
        }
        
        @Test
        public void creditLimitIsCrossedAndRateNotDefined() {
            rnCQuotaProfileDetailForHybrid = spy(rnCQuotaProfileDetailForHybrid);
            //setting rate zero
            doReturn(0.0d).when(rnCQuotaProfileDetailForHybrid).getRate();

            // balance negative
            doReturn(-11.0d).when(monetaryBalance).getAvailBalance();
            // Credit limit
            doReturn(10L).when(monetaryBalance).getCreditLimit();
            runAndAssert();
        }

        @Test
        public void msccContainsFinalUnitIndicationWhenAvailNonMonetaryBalanceIsTobeReservedNonMonetaryBalance() throws OperationFailedException {

            rnCQuotaProfileDetailForHybrid = spy(rnCQuotaProfileDetailForHybrid);
            //setting rate zero
            doReturn(0.0d).when(rnCQuotaProfileDetailForHybrid).getRate();
            volumeNonMonetoryBalance.setBillingCycleAvailableVolume(10);
            timeNonMonetoryBalance.setBillingCycleAvailableTime(10);
            assertTrue(rnCQuotaProfileDetailForHybrid.apply(gyPolicyContext, basePackage.getId(), subscription, quotaReservation));

            verify(gyPolicyContext, times(1)).getCurrentBalance();
            FinalUnitIndication expectedFinalUnitIndication = new FinalUnitIndication();
            expectedFinalUnitIndication.setAction(FinalUnitAction.TERMINATE);
            ReflectionAssert.assertReflectionEquals(expectedFinalUnitIndication, quotaReservation.get().iterator().next().getValue().getFinalUnitIndiacation());

            MSCC grantedMscc = quotaReservation.get(rnCQuotaProfileDetailForHybrid.getRatingGroup().getIdentifier());
            assertThat(grantedMscc, hasVolumeThreshold(grantedMscc.getGrantedServiceUnits().getVolume()));
            assertThat(grantedMscc, hasTimeThreshold(grantedMscc.getGrantedServiceUnits().getTime()));
        }

        @Test
        public void msccContainsFinalUnitIndicationWhenAvailNonMonetaryTimeBalanceExistsButMonetaryBalanceIsEnoughForLastSlice() throws OperationFailedException {

            rnCQuotaProfileDetailForHybrid = spy(rnCQuotaProfileDetailForHybrid);
            //setting rate zero
            doReturn(1.0d).when(rnCQuotaProfileDetailForHybrid).getRate();
            //setting rate zero
            doReturn(UsageType.TIME).when(rnCQuotaProfileDetailForHybrid).getRateUnit();
            // balance negative
            doReturn(5.0d).when(monetaryBalance).getUsableBalance();
            volumeNonMonetoryBalance.setBillingCycleAvailableVolume(10);
            timeNonMonetoryBalance.setBillingCycleAvailableTime(10);
            assertTrue(rnCQuotaProfileDetailForHybrid.apply(gyPolicyContext, basePackage.getId(), subscription, quotaReservation));

            verify(gyPolicyContext, times(1)).getCurrentBalance();
            FinalUnitIndication expectedFinalUnitIndication = new FinalUnitIndication();
            expectedFinalUnitIndication.setAction(FinalUnitAction.TERMINATE);
            ReflectionAssert.assertReflectionEquals(expectedFinalUnitIndication, quotaReservation.get().iterator().next().getValue().getFinalUnitIndiacation());

            MSCC grantedMscc = quotaReservation.get(rnCQuotaProfileDetailForHybrid.getRatingGroup().getIdentifier());
            assertThat(grantedMscc, hasVolumeThreshold(grantedMscc.getGrantedServiceUnits().getVolume()));
            assertThat(grantedMscc, hasTimeThreshold(grantedMscc.getGrantedServiceUnits().getTime()));
        }

        @Test
        public void msccContainsFinalUnitIndicationWhenAvailNonMonetaryDataBalanceExistsButMonetaryBalanceIsEnoughForLastSlice() throws OperationFailedException {

            rnCQuotaProfileDetailForHybrid = spy(rnCQuotaProfileDetailForHybrid);
            //setting rate zero
            doReturn(1.0d).when(rnCQuotaProfileDetailForHybrid).getRate();
            //setting rate zero
            doReturn(UsageType.VOLUME).when(rnCQuotaProfileDetailForHybrid).getRateUnit();
            // balance negative
            doReturn(5.0d).when(monetaryBalance).getUsableBalance();
            volumeNonMonetoryBalance.setBillingCycleAvailableVolume(10);
            timeNonMonetoryBalance.setBillingCycleAvailableTime(10);

            assertTrue(rnCQuotaProfileDetailForHybrid.apply(gyPolicyContext, basePackage.getId(), subscription, quotaReservation));

            verify(gyPolicyContext, times(1)).getCurrentBalance();
            FinalUnitIndication expectedFinalUnitIndication = new FinalUnitIndication();
            expectedFinalUnitIndication.setAction(FinalUnitAction.TERMINATE);
            ReflectionAssert.assertReflectionEquals(expectedFinalUnitIndication, quotaReservation.get().iterator().next().getValue().getFinalUnitIndiacation());

            MSCC grantedMscc = quotaReservation.get(rnCQuotaProfileDetailForHybrid.getRatingGroup().getIdentifier());
            assertThat(grantedMscc, hasVolumeThreshold(grantedMscc.getGrantedServiceUnits().getVolume()));
            assertThat(grantedMscc, hasTimeThreshold(grantedMscc.getGrantedServiceUnits().getTime()));
        }

        @Test
        public void grantVolumeSliceAsPerAvailableMonetaryBalanceIfDeductableBalanceIsLessThanAvailableBalance() throws OperationFailedException {

            volumeNonMonetoryBalance.setBillingCycleTime(CommonConstants.QUOTA_UNDEFINED);
            assertTrue(rnCQuotaProfileDetailForVolume.apply(gyPolicyContext, basePackage.getId(), subscription, quotaReservation));

            verify(gyPolicyContext, times(1)).getCurrentMonetaryBalance();
            ReflectionAssert.assertReflectionEquals(monetaryBalance.getUsableBalance(), quotaReservation.get().iterator().next().getValue().getGrantedServiceUnits().getReservedMonetaryBalance());

        }

        @Test
        public void grantTimeSliceAsPerAvailableMonetaryBalanceIfDeductableBalanceIsLessThanAvailableBalance() throws OperationFailedException {

            timeNonMonetoryBalance.setBillingCycleTotalVolume(CommonConstants.QUOTA_UNDEFINED);
            assertTrue(rnCQuotaProfileDetailForTime.apply(gyPolicyContext, basePackage.getId(), subscription, quotaReservation));

            verify(gyPolicyContext, times(1)).getCurrentMonetaryBalance();
            ReflectionAssert.assertReflectionEquals(monetaryBalance.getUsableBalance(), quotaReservation.get().iterator().next().getValue().getGrantedServiceUnits().getReservedMonetaryBalance());

        }

    }

    private String getQuotaProfileIdFromReservation() {
        return quotaReservation.get().iterator().next().getValue().getGrantedServiceUnits().getQuotaProfileIdOrRateCardId();
    }

    @Test
    public void applyRGShouldReturnFalseWhenProvidedRGNotSameAsCurrentDetail() {
        long anotherRatingGroup = 10;
        assertFalse(rnCQuotaProfileDetailForHybrid.applyRG(gyPolicyContext, basePackage.getId(), subscription, quotaReservation, anotherRatingGroup));
        assertTrue(quotaReservation.get().isEmpty());
    }

    @Test
    public void applyShouldCalledWhenRatingGroupIsSameAsCurrentDetail() {
        long anotherRatingGroup = rnCQuotaProfileDetailForHybrid.getRatingGroup().getIdentifier();
        rnCQuotaProfileDetailForHybrid = spy(rnCQuotaProfileDetailForHybrid);
        String baseId = basePackage.getId();

        rnCQuotaProfileDetailForHybrid.applyRG(gyPolicyContext, basePackage.getId(), subscription, quotaReservation, anotherRatingGroup);
        verify(rnCQuotaProfileDetailForHybrid, times(1)).apply(gyPolicyContext, baseId, subscription, quotaReservation);

        assertTrue(quotaReservation.get().size() == 1);
        assertEquals(quotaProfileId, getQuotaProfileIdFromReservation());
    }
}