package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.ddf.MonetaryBalanceOperation;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.RnCQuotaProfileFactory;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.DummyPCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.netvertex.core.util.Maps.Entry.newEntry;
import static com.elitecore.netvertex.core.util.Maps.newLinkedHashMap;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class ReportingReasonNonFinalRnCReportHandlerTest {

    public static final String INR = "INR";
    private RnCReportHandler reportHandler;
    private DummyPCRFServiceContext dummyPCRFServiceContext;
    private DummyPolicyRepository policyRepository;
    private PCRFRequest request;
    private PCRFResponse response;
    private ExecutionContext executionContext;
    private FixedTimeSource fixedTimeSource;

    @Before
    public void setUp() {

        fixedTimeSource = new FixedTimeSource(System.currentTimeMillis());
        request = new PCRFRequestImpl(fixedTimeSource);
        request.setSessionStartTime(new Date());

        response = new PCRFResponseImpl();
        response.setSessionStartTime(new Date());
        SPRInfo sprInfo = new SPRInfoImpl.SPRInfoBuilder()
                .withSubscriberIdentity(UUID.randomUUID().toString())
                .build();

        request.setSPRInfo(sprInfo);
        PCRFPacketUtil.setProfileAttributes(request, response, sprInfo);

        executionContext = new ExecutionContext(request, response, mock(CacheAwareDDFTable.class), INR);

        dummyPCRFServiceContext = DummyPCRFServiceContext.spy();
        policyRepository = (DummyPolicyRepository) dummyPCRFServiceContext.getServerContext().getPolicyRepository();
        reportHandler = new RnCReportHandler(dummyPCRFServiceContext, new RnCQuotaReportHandlerFactory(policyRepository));

        response.setQuotaReservation(new QuotaReservation());
        request.setQuotaReservation(new QuotaReservation());

        request.setReportedMSCCs(new ArrayList<>());
        response.setReportedMSCCs(new ArrayList<>());
    }


    public class MonetaryBalanceTest {

        private NonMonetoryBalance nonMonetoryBalance;
        private MonetaryBalance monetaryBalance;
        private MonetaryBalanceOperation monetaryBalanceOperation;
        private MockBasePackage mockBasePackage;

        public class rateOnTime {

            @Before
            public void setUp() {

                mockBasePackage = createPackage(2.0, UsageType.TIME);

                nonMonetoryBalance = createNonMonetaryBalance(request.getSPRInfo(), mockBasePackage);

                SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(asList(nonMonetoryBalance));

                response.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);
                SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(fixedTimeSource);
                monetaryBalance = ReportingReasonNonFinalRnCReportHandlerTest.this.createMonetaryBalance(request.getSPRInfo().getSubscriberIdentity());
                subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
                response.setCurrentMonetaryBalance(subscriberMonetaryBalance);

                createGrantedAndReportedMSCCForBalance(nonMonetoryBalance, monetaryBalance, mockBasePackage);


                monetaryBalanceOperation = mock(MonetaryBalanceOperation.class);
                when(executionContext.getDDFTable().getMonetaryBalanceOp()).thenReturn(monetaryBalanceOperation);

            }


            public class UnAccountedQuotaIsNull {

                @Test
                public void deductMonetaryBalanceFromABMF() {

                    process(mockBasePackage);
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get().iterator().next().getValue(),
                            usedServiceUnits));


                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, response.getAccountedMonetaryBalance());
                }

                @Test
                public void doesNotDeductReportedUsageFromABMFWhenReportedQuotaPlusUnAccountedQuotaIsLowerThanPulse() {
                    MSCC mscc = request.getReportedMSCCs().get(0);
                    GyServiceUnits usedServiceUnits = mscc.getUsedServiceUnits();
                    usedServiceUnits.setTime(getQuotaProfileDetail(mockBasePackage).getTimePulse()- 1);
                    QuotaReservation quotaReservation = request.getQuotaReservation();
                    MSCC quotaReservationEntry = quotaReservation.get(mscc.getRatingGroup());
                    quotaReservationEntry.getGrantedServiceUnits().setReservedMonetaryBalance(0);
                    process(mockBasePackage);
                    Assert.assertNull(response.getAccountedMonetaryBalance());
                }

                @Test
                public void removeReservationFromABMFWhenReportedQuotaPlusUnAccountedQuotaIsLowerThanPulse() {
                    MSCC mscc = request.getReportedMSCCs().get(0);
                    GyServiceUnits usedServiceUnits = mscc.getUsedServiceUnits();
                    usedServiceUnits.setTime(getQuotaProfileDetail(mockBasePackage).getTimePulse()- 1);
                    QuotaReservation quotaReservation = request.getQuotaReservation();
                    MSCC quotaReservationEntry = quotaReservation.get(mscc.getRatingGroup());
                    double reservedMonetaryBalance = quotaReservationEntry.getGrantedServiceUnits().getReservedMonetaryBalance();
                    process(mockBasePackage);
                    Assert.assertNull(response.getAccountedNonMonetaryBalance());
                    Assert.assertEquals( 0, reservedMonetaryBalance + response.getAccountedMonetaryBalance().getBalanceById(monetaryBalance.getId()).getTotalReservation(), 0.0d);
                }


                @Test
                public void removeQuotaReservationEntryForRatingGroupFromResponse() {
                    process(mockBasePackage);
                    Assert.assertNull(response.getQuotaReservation());
                }
            }

            public class UnAccountedQuotaIsNotNull {

                private MSCC unAccountedQuota;

                @Before
                public void execute() {
                    unAccountedQuota = createGrantedMSCC(nonMonetoryBalance, mockBasePackage);
                    unAccountedQuota.getGrantedServiceUnits().setMonetaryBalanceId(monetaryBalance.getId());
                    QuotaReservation unAccountedReservation = new QuotaReservation();
                    unAccountedReservation.put(unAccountedQuota);
                    response.setUnAccountedQuota(unAccountedReservation);
                }

                @Test
                public void deductMonetaryBalanceFromABMF() {
                    process(mockBasePackage);

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    usedServiceUnits.setTime(usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime());
                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get().iterator().next().getValue(),
                            usedServiceUnits));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void doesNotDeductReportedUsageFromABMFWhenReportedQuotaPlusUnAccountedQuotaIsLowerThanPulse() {
                    MSCC mscc = request.getReportedMSCCs().get(0);
                    GyServiceUnits usedServiceUnits = mscc.getUsedServiceUnits();
                    usedServiceUnits.setTime(usedServiceUnits.getTimePulse()- unAccountedQuota.getGrantedServiceUnits().getTime() - 1);
                    QuotaReservation quotaReservation = request.getQuotaReservation();
                    MSCC quotaReservationEntry = quotaReservation.get(mscc.getRatingGroup());
                    quotaReservationEntry.getGrantedServiceUnits().setReservedMonetaryBalance(0);
                    process(mockBasePackage);
                    Assert.assertNull(response.getAccountedMonetaryBalance());
                }

                @Test
                public void removeReservationFromABMFWhenReportedQuotaPlusUnAccountedQuotaIsLowerThanPulse() {
                    MSCC mscc = request.getReportedMSCCs().get(0);
                    GyServiceUnits usedServiceUnits = mscc.getUsedServiceUnits();
                    usedServiceUnits.setTime(getQuotaProfileDetail(mockBasePackage).getTimePulse()- unAccountedQuota.getGrantedServiceUnits().getTime() - 1);
                    QuotaReservation quotaReservation = request.getQuotaReservation();
                    MSCC quotaReservationEntry = quotaReservation.get(mscc.getRatingGroup());
                    double reservedMonetaryBalance = quotaReservationEntry.getGrantedServiceUnits().getReservedMonetaryBalance();
                    process(mockBasePackage);
                    Assert.assertEquals( 0, reservedMonetaryBalance + response.getAccountedMonetaryBalance().getBalanceById(monetaryBalance.getId()).getTotalReservation(), 0.0d);
                }



                @Test
                public void removeQuotaReservationEntryForRatingGroupFromResponse() {
                    process(mockBasePackage);
                    Assert.assertNull(response.getQuotaReservation());
                }
            }

        }

    }

    private MonetaryBalance createMonetaryBalance(String subscriberId) {
        int totalBalance = nextInt(2, 1000);
        int availableBalance = nextInt(1, totalBalance);
        int reservation = nextInt(1, availableBalance);
        return new MonetaryBalance(UUID.randomUUID().toString(),
                subscriberId,
                CommonConstants.MONEY_DATA_SERVICE,
                availableBalance,
                totalBalance,
                reservation,0, 0,
                System.currentTimeMillis(),
                CommonConstants.FUTURE_DATE,
                UUID.randomUUID().toString(),null,
                System.currentTimeMillis(),
                0,
                "","");
    }

    private void process(UserPackage mockBasePackage) {

        PolicyManager policyManager = mock(PolicyManager.class);

        PolicyManager.setInstance(policyManager);
        when(policyManager.getPkgDataById(mockBasePackage.getId())).thenReturn(mockBasePackage);
        ProductOfferStore productOfferStore = mock(ProductOfferStore.class);
        when(policyManager.getProductOffer()).thenReturn(productOfferStore);
        when(productOfferStore.byId(Mockito.anyString())).thenReturn(null);


        getLogger().debug(ReportingReasonNonFinalRnCReportHandlerTest.class.getSimpleName(), "Monetary Balance is:" + response.getCurrentMonetaryBalance());
        getLogger().debug(ReportingReasonNonFinalRnCReportHandlerTest.class.getSimpleName(), "Quota Reservation is:" + response.getQuotaReservation());
        getLogger().debug(ReportingReasonNonFinalRnCReportHandlerTest.class.getSimpleName(), "Reported Quota is:" + request.getReportedMSCCs());
        getLogger().debug(ReportingReasonNonFinalRnCReportHandlerTest.class.getSimpleName(), "UnAccounted Quota:" + response.getUnAccountedQuota());
        getLogger().debug(ReportingReasonNonFinalRnCReportHandlerTest.class.getSimpleName(), "Quota Profile Detail is:" + getQuotaProfileDetail(mockBasePackage));

        reportHandler.process(request, response, executionContext);

        getLogger().debug(ReportingReasonNonFinalRnCReportHandlerTest.class.getSimpleName(), "Monetary Balance after reporting is:" + response.getCurrentMonetaryBalance());
        getLogger().debug(ReportingReasonNonFinalRnCReportHandlerTest.class.getSimpleName(), "Accounted Non-Monetary Balance after reporting is:" + response.getAccountedNonMonetaryBalance());
        getLogger().debug(ReportingReasonNonFinalRnCReportHandlerTest.class.getSimpleName(), "Accounted Monetary Balance after reporting is:" + response.getAccountedMonetaryBalance());
        getLogger().debug(ReportingReasonNonFinalRnCReportHandlerTest.class.getSimpleName(), "UnAccounted Quota after reporting is:" + response.getUnAccountedQuota());
    }


    private MSCC createGrantedAndReportedMSCCForBalance(NonMonetoryBalance nonMonetoryBalance, MonetaryBalance monetaryBalance, UserPackage userPackage) {
        MSCC grantedMSCC = createGrantedMSCC(nonMonetoryBalance, monetaryBalance, userPackage);
        response.getQuotaReservation().put(grantedMSCC);
        request.getQuotaReservation().put(grantedMSCC);
        MSCC usedMSCC = createUsedMSCC(grantedMSCC, userPackage);
        request.getReportedMSCCs().add(usedMSCC);
        response.getReportedMSCCs().add(usedMSCC);
        return grantedMSCC;
    }


    private MSCC createGrantedMSCC(NonMonetoryBalance nonMonetoryBalance, UserPackage userPackage) {
        GyServiceUnits grantedServiceUnit = createGSU(nonMonetoryBalance, userPackage);
        MSCC grantedMSCC = new MSCC();
        grantedMSCC.setRatingGroup(nonMonetoryBalance.getRatingGroupId());
        grantedMSCC.setGrantedServiceUnits(grantedServiceUnit);
        return grantedMSCC;
    }

    private MSCC createGrantedMSCC(NonMonetoryBalance nonMonetoryBalance, MonetaryBalance monetaryBalance, UserPackage userPackage) {
        GyServiceUnits grantedServiceUnit = createGSU(nonMonetoryBalance, userPackage);
        grantedServiceUnit.setMonetaryBalanceId(monetaryBalance.getId());
        grantedServiceUnit.setReservedMonetaryBalance(nextLong(1, Double.valueOf(monetaryBalance.getTotalReservation()).intValue()));
        MSCC grantedMSCC = new MSCC();
        grantedMSCC.setRatingGroup(nonMonetoryBalance.getRatingGroupId());
        grantedMSCC.setGrantedServiceUnits(grantedServiceUnit);
        return grantedMSCC;
    }

    private MSCC createUsedMSCC(MSCC grantedMSCC, UserPackage userPackage) {
        GyServiceUnits usedServiceUnits = createUsedServiceUnit(grantedMSCC.getGrantedServiceUnits(), userPackage);
        MSCC reportedMSCC = new MSCC();
        reportedMSCC.setRatingGroup(grantedMSCC.getRatingGroup());
        reportedMSCC.setUsedServiceUnits(usedServiceUnits);

        return reportedMSCC;
    }

    private GyServiceUnits createGSU(NonMonetoryBalance nonMonetoryBalance, UserPackage userPackage) {
        return new GyServiceUnits(nonMonetoryBalance.getPackageId(),
                nextLong(getQuotaProfileDetail(userPackage).getVolumePulseInBytes(), nonMonetoryBalance.getBillingCycleAvailableVolume()),
                nonMonetoryBalance.getQuotaProfileId(),
                nextLong(getQuotaProfileDetail(userPackage).getTimePulseInSeconds(), nonMonetoryBalance.getBillingCycleAvailableTime()),
                nonMonetoryBalance.getSubscriptionId(),
                0,
                null,
                false,
                null,
                0d,
                1.0,60,60, 0,0,-1, nonMonetoryBalance.getLevel());
    }

    private GyServiceUnits createUsedServiceUnit(GyServiceUnits grantedServiceUnit, UserPackage userPackage) {
        long time = nextLong(61, 1000);
        return new GyServiceUnits(null,
                nextLong(getQuotaProfileDetail(userPackage).getVolumePulseInBytes(), grantedServiceUnit.getVolume()),
                null,
                time,
                null,
                nextLong(),
                null,
                false,
                null,
                0d,
                1.0,60,60, 0,0,-1, grantedServiceUnit.getFupLevel());
    }

    private NonMonetoryBalance createNonMonetaryBalance(SPRInfo sprInfo,
                                                        UserPackage userPackage) {

        RnCQuotaProfileDetail rnCQuotaProfileDetail = getQuotaProfileDetail(userPackage);

        long billingCycleTotalVolume = nextLong(rnCQuotaProfileDetail.getVolumePulseInBytes(), 1000);
        long billingCycleTotalTime = nextLong(rnCQuotaProfileDetail.getTimePulseInSeconds(), 1000);
        return new NonMonetoryBalance.NonMonetaryBalanceBuilder(
                UUID.randomUUID().toString(), rnCQuotaProfileDetail.getDataServiceType().getServiceIdentifier(),
                userPackage.getId(),
                rnCQuotaProfileDetail.getRatingGroup().getIdentifier(),
                sprInfo.getSubscriberIdentity(),
                null,
                0,
                userPackage.getQuotaProfiles().get(0).getId(), ResetBalanceStatus.NOT_RESET, null, null).
                withBillingCycleVolumeBalance(billingCycleTotalVolume, nextLong(rnCQuotaProfileDetail.getVolumePulseInBytes(), billingCycleTotalVolume)).
                withBillingCycleTimeBalance(billingCycleTotalTime, nextLong(rnCQuotaProfileDetail.getTimePulseInSeconds(), billingCycleTotalTime))
                .withDailyUsage(nextLong(2, billingCycleTotalVolume), nextLong(rnCQuotaProfileDetail.getTimePulseInSeconds(), billingCycleTotalTime))
                .withWeeklyUsage(nextLong(2, billingCycleTotalVolume), nextLong(rnCQuotaProfileDetail.getTimePulseInSeconds(), billingCycleTotalTime))
                .withDailyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .withWeeklyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(6))
                .withBillingCycleResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .build();
    }

    private MonetaryBalance createDeductedMonetaryBalance(MonetaryBalance monetaryBalance,
                                                  MSCC grantedMSCC,
                                                  GyServiceUnits gyServiceUnits) {

        MonetaryBalance copy = monetaryBalance.copy();
        long calculatedTimePulse = RnCPulseCalculator.floor(gyServiceUnits.getTime(), grantedMSCC.getGrantedServiceUnits().getTimePulse());
        double deductedMonetaryBalance = RnCRateCalculator.calculateMoney(calculatedTimePulse, grantedMSCC.getGrantedServiceUnits().getTimePulse(), grantedMSCC.getGrantedServiceUnits().getRateMinorUnit());
        copy.setAvailBalance(deductedMonetaryBalance);
        copy.setTotalReservation(0);
        copy.substractReservation(grantedMSCC.getGrantedServiceUnits().getReservedMonetaryBalance());
        return copy;
    }

    private RnCQuotaProfileDetail getQuotaProfileDetail(UserPackage userPackage) {
        return (RnCQuotaProfileDetail) userPackage.getQuotaProfiles().get(0).getHsqLevelServiceWiseQuotaProfileDetails().values().stream().findFirst().get();
    }

    private MockBasePackage createPackage(double rate, UsageType rateOn) {
        MockBasePackage mockBasePackage = policyRepository.mockBasePackage();
        mockBasePackage.quotaProfileTypeIsRnC();

        int timePulse = nextInt(2, 5);
        RncProfileDetail rnCQuotaProfileDetail = new RnCQuotaProfileFactory(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                .withRate(rate)
                .withRateOn(rateOn)
                .withPulse(nextInt(2, 5), timePulse)
                .randomBalanceWithRate().create();
        QuotaProfile quotaProfile = new QuotaProfile(rnCQuotaProfileDetail.getQuotaProfileId()
                , mockBasePackage.getId()
                , rnCQuotaProfileDetail.getName()
                , BalanceLevel.HSQ
                ,2, RenewalIntervalUnit.MONTH, QuotaProfileType.RnC_BASED,
                asList(newLinkedHashMap(newEntry(rnCQuotaProfileDetail.getServiceId(), rnCQuotaProfileDetail))), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());

        mockBasePackage.mockQuotaProfie(quotaProfile);

        return mockBasePackage;
    }
}
