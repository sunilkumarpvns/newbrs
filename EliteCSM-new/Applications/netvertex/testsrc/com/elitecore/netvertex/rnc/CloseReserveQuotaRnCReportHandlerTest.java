package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.HibernateDataReader;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.factory.PackageHibernateSessionFactory;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationSchemeFactory;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackageFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardVersionFactory;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroupFactory;
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
import com.elitecore.netvertex.pm.rnc.RnCFactory;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.DummyPCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
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
public class CloseReserveQuotaRnCReportHandlerTest {

    public static final String INR = "INR";
    private RnCReportHandler reportHandler;
    private DummyPCRFServiceContext dummyPCRFServiceContext;
    private DummyPolicyRepository policyRepository;
    private PCRFRequest request;
    private PCRFResponse response;
    private ExecutionContext executionContext;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private PackageHibernateSessionFactory hibernateSessionFactory;
    private FixedTimeSource fixedTimeSource;
	private DeploymentMode deploymentMode = DeploymentMode.PCC;

    @Before
    public void setUp() throws Exception {

        fixedTimeSource = new FixedTimeSource(System.currentTimeMillis());
        request = new PCRFRequestImpl(fixedTimeSource);
        request.setSessionStartTime(new Date());
        request.setPCRFEvents(EnumSet.of(PCRFEvent.SESSION_STOP));

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
        initPolicyManager();
    }

    @After
    public void tearDown() {
        hibernateSessionFactory.shutdown();
        PolicyManager.release();
        folder.delete();
    }

    private void initPolicyManager() throws Exception {
        hibernateSessionFactory = PackageHibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml");
        PackageFactory packageFactory = new PackageFactory();
        generatePolicyBackUpFile();
        PolicyManager policyManager = new PolicyManager();
        PolicyManager.setInstance(policyManager);
        RnCFactory rnCFactory = new RnCFactory();
        PolicyManager.getInstance().init(folder.getRoot().getAbsolutePath(), hibernateSessionFactory.getSessionFactory(), new HibernateDataReader(),
				packageFactory,new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new RateCardVersionFactory(rnCFactory),rnCFactory),
						rnCFactory),rnCFactory,new ThresholdNotificationSchemeFactory(rnCFactory)), deploymentMode);
    }

    private void generatePolicyBackUpFile() throws IOException {
        File systemFolder = folder.newFolder("system");
        File file = new File(systemFolder.getAbsolutePath(), "policies.bkp");
        file.createNewFile();
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
                monetaryBalance = CloseReserveQuotaRnCReportHandlerTest.this.createMonetaryBalance(request.getSPRInfo().getSubscriberIdentity());
                subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
                response.setCurrentMonetaryBalance(subscriberMonetaryBalance);

                createGrantedAndReportedMSCCForBalance(nonMonetoryBalance, monetaryBalance);


                monetaryBalanceOperation = mock(MonetaryBalanceOperation.class);
                when(executionContext.getDDFTable().getMonetaryBalanceOp()).thenReturn(monetaryBalanceOperation);

                getLogger().debug(CloseReserveQuotaRnCReportHandlerTest.class.getSimpleName(), "Monetary Balance is:" + monetaryBalance);
                getLogger().debug(CloseReserveQuotaRnCReportHandlerTest.class.getSimpleName(), "Quota Reservation is:" + response.getQuotaReservation());
                getLogger().debug(CloseReserveQuotaRnCReportHandlerTest.class.getSimpleName(), "Reported Quota is:" + request.getReportedMSCCs());
                getLogger().debug(CloseReserveQuotaRnCReportHandlerTest.class.getSimpleName(), "Quota Profile Detail is:" + getQuotaProfileDetail(mockBasePackage));

            }


            public class UnAccountedQuotaIsNull {

                @Before
                public void execute() {
                    reportHandler.process(request, response, executionContext);

                    getLogger().debug(CloseReserveQuotaRnCReportHandlerTest.class.getSimpleName(), "Monetary Balance after reporting is:" + response.getCurrentMonetaryBalance().getBalanceById(monetaryBalance.getId()));
                    getLogger().debug(CloseReserveQuotaRnCReportHandlerTest.class.getSimpleName(), "Accounted Monetary Balance after reporting is:" + response.getAccountedMonetaryBalance().getBalanceById(monetaryBalance.getId()));
                }


                @Test
                public void removeMonetaryReservationFromABMF() {

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance, request.getQuotaReservation().remove(nonMonetoryBalance.getRatingGroupId())));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void deductMonetaryBalanceAccordingToRate() {

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);


                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance, request.getQuotaReservation().get(nonMonetoryBalance.getRatingGroupId())));

                    SubscriberMonetaryBalance actual = response.getCurrentMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromCurrentBalance() {
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance, request.getQuotaReservation().get(nonMonetoryBalance.getRatingGroupId())));

                    SubscriberMonetaryBalance actual = response.getCurrentMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void doesNotCreateAnyUnAccountedUsage() {
                    Assert.assertNull(response.getUnAccountedQuota());
                }


                @Test
                public void removeQuotaReservationEntryForRatingGroupFromResponse() {
                    Assert.assertNull(response.getQuotaReservation());
                }
            }

            public class UnAccountedQuotaIsNotNull {

                private MSCC unAccountedQuota;

                @Before
                public void execute() {
                    unAccountedQuota = createGrantedMSCC(nonMonetoryBalance);
                    unAccountedQuota.getGrantedServiceUnits().setMonetaryBalanceId(monetaryBalance.getId());
                    QuotaReservation unAccountedReservation = new QuotaReservation();
                    unAccountedReservation.put(unAccountedQuota);
                    response.setUnAccountedQuota(unAccountedReservation);

                    getLogger().debug(CloseReserveQuotaRnCReportHandlerTest.class.getSimpleName(), "UnAccounted Quota:" + unAccountedQuota);
                    reportHandler.process(request, response, executionContext);

                    getLogger().debug(CloseReserveQuotaRnCReportHandlerTest.class.getSimpleName(), "Monetary Balance after reporting is:" + response.getCurrentMonetaryBalance().getBalanceById(monetaryBalance.getId()));
                    getLogger().debug(CloseReserveQuotaRnCReportHandlerTest.class.getSimpleName(), "Accounted Monetary Balance after reporting is:" + response.getAccountedMonetaryBalance().getBalanceById(monetaryBalance.getId()));
                }

                @Test
                public void removeMonetaryReservationFromABMF() {

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(nonMonetoryBalance.getRatingGroupId()),
                            unAccountedQuota
                            ));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void doesNotCreateAnyUnAccountedUsage() {
                    Assert.assertNull(response.getUnAccountedQuota());
                }

                @Test
                public void removeUnAccountedUsageIfMonetaryBalanceIdAndNonMonetaryBalanceIdMatch() {
                    Assert.assertNull(response.getUnAccountedQuota());
                }


                @Test
                public void removeQuotaReservationEntryForRatingGroupFromResponse() {
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
                System.currentTimeMillis(),0
                ,"","");
    }


    private MSCC createGrantedAndReportedMSCCForBalance(NonMonetoryBalance nonMonetoryBalance, MonetaryBalance monetaryBalance) {
        MSCC grantedMSCC = createGrantedMSCC(nonMonetoryBalance, monetaryBalance);
        response.getQuotaReservation().put(grantedMSCC);
        request.getQuotaReservation().put(grantedMSCC);
        return grantedMSCC;
    }


    private MSCC createGrantedMSCC(NonMonetoryBalance nonMonetoryBalance) {
        GyServiceUnits grantedServiceUnit = createGSU(nonMonetoryBalance);
        MSCC grantedMSCC = new MSCC();
        grantedMSCC.setRatingGroup(nonMonetoryBalance.getRatingGroupId());
        grantedMSCC.setGrantedServiceUnits(grantedServiceUnit);
        return grantedMSCC;
    }

    private MSCC createGrantedMSCC(NonMonetoryBalance nonMonetoryBalance, MonetaryBalance monetaryBalance) {
        GyServiceUnits grantedServiceUnit = createGSU(nonMonetoryBalance);
        grantedServiceUnit.setMonetaryBalanceId(monetaryBalance.getId());
        grantedServiceUnit.setReservedMonetaryBalance(nextLong(1, Double.valueOf(monetaryBalance.getTotalReservation()).intValue()));
        MSCC grantedMSCC = new MSCC();
        grantedMSCC.setRatingGroup(nonMonetoryBalance.getRatingGroupId());
        grantedMSCC.setGrantedServiceUnits(grantedServiceUnit);
        return grantedMSCC;
    }

    private GyServiceUnits createGSU(NonMonetoryBalance nonMonetoryBalance) {
        return new GyServiceUnits(nonMonetoryBalance.getPackageId(),
                nextLong(1, nonMonetoryBalance.getBillingCycleAvailableVolume()),
                nonMonetoryBalance.getQuotaProfileId(),
                nextLong(1, nonMonetoryBalance.getBillingCycleAvailableTime()),
                nonMonetoryBalance.getSubscriptionId(),
                0,
                null,
                false,
                null,
                0d,
                1.0,60,60, 0,0,-1,0);
    }

    private NonMonetoryBalance createNonMonetaryBalance(SPRInfo sprInfo,
                                                        UserPackage userPackage) {

        RnCQuotaProfileDetail rnCQuotaProfileDetail = getQuotaProfileDetail(userPackage);

        long billingCycleTotalVolume = nextLong(2, 1000);
        long billingCycleTotalTime = nextLong(2, 1000);
        return new NonMonetoryBalance.NonMonetaryBalanceBuilder(
                UUID.randomUUID().toString(), rnCQuotaProfileDetail.getDataServiceType().getServiceIdentifier(),
                userPackage.getId(),
                rnCQuotaProfileDetail.getRatingGroup().getIdentifier(),
                sprInfo.getSubscriberIdentity(),
                null,
                0,
                userPackage.getQuotaProfiles().get(0).getId(), ResetBalanceStatus.NOT_RESET, null, null).
                withBillingCycleVolumeBalance(billingCycleTotalVolume, nextLong(2, billingCycleTotalVolume)).
                withBillingCycleTimeBalance(billingCycleTotalTime, nextLong(2, billingCycleTotalTime))
                .withDailyUsage(nextLong(2, billingCycleTotalVolume), nextLong(2, billingCycleTotalTime))
                .withWeeklyUsage(nextLong(2, billingCycleTotalVolume), nextLong(2, billingCycleTotalTime))
                .withBillingCycleResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .withDailyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .withWeeklyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(6))
                .build();
    }

    private MonetaryBalance createMonetaryBalance(MonetaryBalance monetaryBalance, MSCC grantedMSCC) {
        MonetaryBalance copy = monetaryBalance.copy();

        copy.substractReservation(grantedMSCC.getGrantedServiceUnits().getReservedMonetaryBalance());

        return copy;
    }

    private MonetaryBalance createDeductedMonetaryBalance(MonetaryBalance monetaryBalance,MSCC grantedMSCC) {
        MonetaryBalance copy = monetaryBalance.copy();

        copy.setAvailBalance(0);
        copy.setTotalReservation(0);
        copy.substractReservation(grantedMSCC.getGrantedServiceUnits().getReservedMonetaryBalance());
        return copy;
    }

    private MonetaryBalance createDeductedMonetaryBalance(MonetaryBalance monetaryBalance,
                                                          MSCC grantedMSCC,
                                                          MSCC unAccountedQuota) {

        MonetaryBalance copy = monetaryBalance.copy();
        long calculatedTimePulse = RnCPulseCalculator.ceil(unAccountedQuota.getGrantedServiceUnits().getTime(), grantedMSCC.getGrantedServiceUnits().getTimePulse());
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
        RnCQuotaProfileDetail rnCQuotaProfileDetail = new RnCQuotaProfileFactory(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                .withRate(rate)
                .withRateOn(rateOn)
                .withPulse(nextInt(2, 5), timePulse)
                .randomBalanceWithRate().create();
        QuotaProfile quotaProfile = new QuotaProfile(rnCQuotaProfileDetail.getQuotaProfileId()
                , mockBasePackage.getId()
                , rnCQuotaProfileDetail.getName()
                , BalanceLevel.HSQ,2, RenewalIntervalUnit.MONTH
                , QuotaProfileType.RnC_BASED,
                asList(newLinkedHashMap(newEntry(rnCQuotaProfileDetail.getServiceId(), rnCQuotaProfileDetail))), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());

        mockBasePackage.mockQuotaProfie(quotaProfile);

        return mockBasePackage;
    }
}