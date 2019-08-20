package com.elitecore.netvertex.rnc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pm.HibernateDataReader;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.factory.PackageHibernateSessionFactory;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationSchemeFactory;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackageFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardVersionFactory;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroupFactory;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.pm.rnc.RnCFactory;
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


import static com.elitecore.commons.logging.LogManager.getLogger;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.mockito.Mockito.mock;

@RunWith(HierarchicalContextRunner.class)
public class ReportingReasonNonFinalNonMonetaryRnCReportHandlerTest {

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
	private DeploymentMode deploymentMode = DeploymentMode.PCC;

    @Before
    public void setUp() throws Exception {
        request = new PCRFRequestImpl(new FixedTimeSource(100));
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
        initPolicyManager();
    }

    private void initPolicyManager() throws Exception {
        hibernateSessionFactory = PackageHibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml");
        PackageFactory packageFactory = new PackageFactory();
        generatePolicyBackUpFile();

        PolicyManager policyManager = new PolicyManager();
        PolicyManager.setInstance(policyManager);
        RnCFactory rnCFactory = new RnCFactory();
        policyManager.init(folder.getRoot().getAbsolutePath(), hibernateSessionFactory.getSessionFactory(), new HibernateDataReader(),
				packageFactory,new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new RateCardVersionFactory(rnCFactory),rnCFactory),
						rnCFactory),rnCFactory,new ThresholdNotificationSchemeFactory(rnCFactory)), deploymentMode);
    }

    private void generatePolicyBackUpFile() throws IOException {
        File systemFolder = folder.newFolder("system");
        File file = new File(systemFolder.getAbsolutePath(), "policies.bkp");
        file.createNewFile();
    }

    @After
    public void tearDown() {
        hibernateSessionFactory.shutdown();
        PolicyManager.release();
        folder.delete();
    }

    public class OnlyNonMonetaryBalance {

        private RnCNonMonetaryBalance nonMonetoryBalance;

        @Before
        public void execute() {

            nonMonetoryBalance = createNonMonetaryBalance(request.getSPRInfo());

            SubscriberRnCNonMonetaryBalance subscriberNonMonitoryBalance = new SubscriberRnCNonMonetaryBalance(asList(nonMonetoryBalance));

            response.setCurrentRnCNonMonetaryBalance(subscriberNonMonitoryBalance);
            response.setCurrentMonetaryBalance(new SubscriberMonetaryBalance(new FixedTimeSource(System.currentTimeMillis())));

            createGrantedAndReportedMSCCForBalance(nonMonetoryBalance);

        }


        public class UnAccountedQuotaIsNull {

            @Test
            public void deductReportedUsageFromCurrentBalance() {

                process();

                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                SubscriberRnCNonMonetaryBalance expected = new SubscriberRnCNonMonetaryBalance(asList(createNonMonetoryBalance(nonMonetoryBalance , usedServiceUnits.getTime(), usedServiceUnits.getTimePulse())));
                ReflectionAssert.assertReflectionEquals(expected, response.getCurrentRnCNonMonetaryBalance());
            }

            @Test
            public void deductReportedUsageFromABMF() {

                process();

                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                SubscriberRnCNonMonetaryBalance expected = new SubscriberRnCNonMonetaryBalance(asList(createDeductedNonMonetoryBalance(nonMonetoryBalance, usedServiceUnits.getTime(), usedServiceUnits.getTimePulse())));
                ReflectionAssert.assertReflectionEquals(expected, response.getAccountedRnCNonMonetaryBalance());
            }

            @Test
            public void doesNotDeductReportedUsageFromABMFWhenReportedQuotaIsLowerThanPulse() {
                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                usedServiceUnits.setTime(usedServiceUnits.getTimePulse()-1);
                process();
                Assert.assertNull(response.getAccountedRnCNonMonetaryBalance());
            }

            @Test
            public void doesNotCreateAnyUnAccountedUsageWhenReportedQuotaDivisibleByPulse() {
                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                usedServiceUnits.setTime(usedServiceUnits.getTime() + (usedServiceUnits.getTimePulse() - usedServiceUnits.getTime() % usedServiceUnits.getTimePulse()));
                process();

                Assert.assertNull(response.getUnAccountedQuota());
            }

            @Test
            public void createAnyUnAccountedUsageWhenAllReportedNotDivisibleByPulse() {
                MSCC mscc = request.getReportedMSCCs().get(0);
                GyServiceUnits usedServiceUnits = mscc.getUsedServiceUnits();
                QuotaReservation quotaReservation = request.getQuotaReservation();
                MSCC quotaReservationEntry = quotaReservation.get(mscc.getRatingGroup());
                usedServiceUnits.setTime(usedServiceUnits.getTimePulse()+1);
                process();
                MSCC expectedUnAccoutedMSCC = quotaReservationEntry.copy();
                expectedUnAccoutedMSCC.getGrantedServiceUnits().setTime(1);
                QuotaReservation expectedUnAccountedQuota  = new QuotaReservation();
                expectedUnAccountedQuota.put(expectedUnAccoutedMSCC);
                ReflectionAssert.assertReflectionEquals(expectedUnAccountedQuota, response.getUnAccountedQuota());
            }


            @Test
            public void removeQuotaReservationEntryForRatingGroupFromResponse() {
                process();
                Assert.assertNull(response.getQuotaReservation());
            }

            @Test
            public void removeUnAccountedUsageIfMonetaryBalanceIdAndNonMonetaryBalanceIdMatch() {
                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();

                long time = usedServiceUnits.getTime() + (usedServiceUnits.getTimePulse() - (usedServiceUnits.getTime()) % usedServiceUnits.getTimePulse());
                usedServiceUnits.setTime(time);
                process();
                Assert.assertNull(response.getUnAccountedQuota());
            }


        }

        public class UnAccountedQuotaIsNotNull {

            private MSCC unAccountedQuota;

            @Before
            public void execute() {
                unAccountedQuota = createGrantedMSCC(nonMonetoryBalance);
                QuotaReservation unAccountedReservation = new QuotaReservation();
                unAccountedReservation.put(unAccountedQuota);
                response.setUnAccountedQuota(unAccountedReservation);


            }

            @Test
            public void deductReportedUsageFromCurrentBalance() {

                process();
                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                SubscriberRnCNonMonetaryBalance expected = new SubscriberRnCNonMonetaryBalance(asList(createNonMonetoryBalance(nonMonetoryBalance, time, usedServiceUnits.getTimePulse())));
                ReflectionAssert.assertReflectionEquals(expected, response.getCurrentRnCNonMonetaryBalance());
            }

            @Test
            public void deductReportedUsageFromABMF() {
                process();
                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                SubscriberRnCNonMonetaryBalance expected = new SubscriberRnCNonMonetaryBalance(asList(createDeductedNonMonetoryBalance(nonMonetoryBalance, time, usedServiceUnits.getTimePulse())));
                ReflectionAssert.assertReflectionEquals(expected, response.getAccountedRnCNonMonetaryBalance());
            }

            @Test
            public void doesNotDeductReportedUsageFromABMFWhenReportedQuotaPlusUnAccountedQuotaIsLowerThanPulse() {
                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                usedServiceUnits.setTime(usedServiceUnits.getTimePulse()- unAccountedQuota.getGrantedServiceUnits().getTime() - 1);
                process();
                Assert.assertNull(response.getAccountedRnCNonMonetaryBalance());
            }


            @Test
            public void doesNotCreateAnyUnAccountedUsageWhenSumOfReportedAndUnAccountedQuotaIsDivisibleByPulse() {
                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();

                long time = usedServiceUnits.getTime() + (usedServiceUnits.getTimePulse() - (this.unAccountedQuota.getGrantedServiceUnits().getTime() + usedServiceUnits.getTime()) % usedServiceUnits.getTimePulse());
                usedServiceUnits.setTime(time);
                process();
                Assert.assertNull(response.getUnAccountedQuota());
            }

            @Test
            public void createUnAccountedUsageWhenSumOfReportedAndUnAccountedQuotaIsNotDivisibleByPulse() {
                MSCC mscc = request.getReportedMSCCs().get(0);
                GyServiceUnits usedServiceUnits = mscc.getUsedServiceUnits();
                QuotaReservation quotaReservation = request.getQuotaReservation();
                MSCC quotaReservationEntry = quotaReservation.get(mscc.getRatingGroup());
                long time = usedServiceUnits.getTime() + (usedServiceUnits.getTimePulse() - (this.unAccountedQuota.getGrantedServiceUnits().getTime() + usedServiceUnits.getTime()) % usedServiceUnits.getTimePulse());
                usedServiceUnits.setTime(time+1);
                process();
                MSCC expectedUnAccoutedMSCC = quotaReservationEntry.copy();
                expectedUnAccoutedMSCC.getGrantedServiceUnits().setTime(1);
                QuotaReservation expectedUnAccountedQuota  = new QuotaReservation();
                expectedUnAccountedQuota.put(expectedUnAccoutedMSCC);
                ReflectionAssert.assertReflectionEquals(expectedUnAccountedQuota, response.getUnAccountedQuota());
            }

            @Test
            public void removeUnAccountedUsageIfMonetaryBalanceIdAndNonMonetaryBalanceIdMatch() {
                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();

                long time = usedServiceUnits.getTime() + (usedServiceUnits.getTimePulse() - (this.unAccountedQuota.getGrantedServiceUnits().getTime() + usedServiceUnits.getTime()) % usedServiceUnits.getTimePulse());
                usedServiceUnits.setTime(time);
                process();
                Assert.assertNull(response.getUnAccountedQuota());
            }


            @Test
            public void removeQuotaReservationEntryForRatingGroupFromResponse() {
                process();
                Assert.assertNull(response.getQuotaReservation());
            }
        }
    }

    private void process() {

        getLogger().debug(ReportingReasonNonFinalNonMonetaryRnCReportHandlerTest.class.getSimpleName(), "Non-Monetary Balance is:" + response.getCurrentRnCNonMonetaryBalance());
        getLogger().debug(ReportingReasonNonFinalNonMonetaryRnCReportHandlerTest.class.getSimpleName(), "Monetary Balance is:" + response.getCurrentMonetaryBalance());
        getLogger().debug(ReportingReasonNonFinalNonMonetaryRnCReportHandlerTest.class.getSimpleName(), "Quota Reservation is:" + response.getQuotaReservation());
        getLogger().debug(ReportingReasonNonFinalNonMonetaryRnCReportHandlerTest.class.getSimpleName(), "Reported Quota is:" + request.getReportedMSCCs());
        getLogger().debug(ReportingReasonNonFinalNonMonetaryRnCReportHandlerTest.class.getSimpleName(), "UnAccounted Quota:" + response.getUnAccountedQuota());

        reportHandler.process(request, response, executionContext);

        getLogger().debug(ReportingReasonNonFinalNonMonetaryRnCReportHandlerTest.class.getSimpleName(), "Non-Monetary Balance after reporting is:" + response.getCurrentRnCNonMonetaryBalance());
        getLogger().debug(ReportingReasonNonFinalNonMonetaryRnCReportHandlerTest.class.getSimpleName(), "Monetary Balance after reporting is:" + response.getCurrentMonetaryBalance());
        getLogger().debug(ReportingReasonNonFinalNonMonetaryRnCReportHandlerTest.class.getSimpleName(), "Accounted Non-Monetary Balance after reporting is:" + response.getAccountedNonMonetaryBalance());
        getLogger().debug(ReportingReasonNonFinalNonMonetaryRnCReportHandlerTest.class.getSimpleName(), "Accounted Monetary Balance after reporting is:" + response.getAccountedMonetaryBalance());
        getLogger().debug(ReportingReasonNonFinalNonMonetaryRnCReportHandlerTest.class.getSimpleName(), "UnAccounted Quota after reporting is:" + response.getUnAccountedQuota());
    }

    private MSCC createGrantedAndReportedMSCCForBalance(RnCNonMonetaryBalance nonMonetoryBalance) {
        MSCC grantedMSCC = createGrantedMSCC(nonMonetoryBalance);
        response.getQuotaReservation().put(grantedMSCC);
        request.getQuotaReservation().put(grantedMSCC);
        MSCC usedMSCC = createUsedMSCC(grantedMSCC);
        request.getReportedMSCCs().add(usedMSCC);
        response.getReportedMSCCs().add(usedMSCC);
        return grantedMSCC;
    }

    private MSCC createGrantedMSCC(RnCNonMonetaryBalance nonMonetoryBalance) {
        GyServiceUnits grantedServiceUnit = createGSU(nonMonetoryBalance);
        MSCC grantedMSCC = new MSCC();
        grantedMSCC.setGrantedServiceUnits(grantedServiceUnit);
        return grantedMSCC;
    }

    private MSCC createUsedMSCC(MSCC grantedMSCC) {
        GyServiceUnits usedServiceUnits = createUsedServiceUnit(grantedMSCC.getGrantedServiceUnits());
        MSCC reportedMSCC = new MSCC();
        reportedMSCC.setRatingGroup(grantedMSCC.getRatingGroup());
        reportedMSCC.setUsedServiceUnits(usedServiceUnits);

        return reportedMSCC;
    }

    private GyServiceUnits createGSU(RnCNonMonetaryBalance nonMonetoryBalance) {
        return new GyServiceUnits(nonMonetoryBalance.getPackageId(),
                0,
                nonMonetoryBalance.getRatecardId(),
                nextLong(61, nonMonetoryBalance.getBillingCycleAvailable()),
                nonMonetoryBalance.getSubscriptionId(),
                0,
                nonMonetoryBalance.getId(),
                false,
                null,
                0d,
                0.0,60,0, 60,0,0, 0);
    }

    private GyServiceUnits createUsedServiceUnit(GyServiceUnits grantedServiceUnit) {
        long time = nextLong(61, grantedServiceUnit.getTime());
        return new GyServiceUnits(null,
                0,
                null,
                time,
                null,
                nextLong(),
                null,
                false,
                null,
                0d,
                0.0,60,0, 60,0,0,0);
    }

    private RnCNonMonetaryBalance createNonMonetaryBalance(SPRInfo sprInfo) {

        long billingCycleTotalTime = nextLong(61, 1000);
        return new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(
                UUID.randomUUID().toString(),
                "test",
                "",
                sprInfo.getSubscriberIdentity(),
                null,
                "0",
                ResetBalanceStatus.NOT_RESET, null, ChargingType.SESSION).
                withBillingCycleTimeBalance(billingCycleTotalTime, nextLong(61, billingCycleTotalTime))
                .withDailyUsage(nextLong(61, billingCycleTotalTime))
                .withWeeklyUsage(nextLong(61, billingCycleTotalTime))
                .withDailyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .withWeeklyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(6))
                .withBillingCycleResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .build();
    }

    private RnCNonMonetaryBalance createNonMonetoryBalance(RnCNonMonetaryBalance subscriberNonMonitoryBalance, long time, long pulse) {

        RnCNonMonetaryBalance copy = subscriberNonMonitoryBalance.copy();

        long deductedTimePulse = RnCPulseCalculator.ceil(time, pulse);
        long deductedTime = RnCPulseCalculator.multiply(deductedTimePulse, pulse);
        copy.substractBillingCycle(deductedTime);
        copy.addWeekly(deductedTime);
        copy.addDaily(deductedTime);

        return copy;
    }

    private RnCNonMonetaryBalance createDeductedNonMonetoryBalance(RnCNonMonetaryBalance subscriberNonMonitoryBalance, long time, long pulse) {

        RnCNonMonetaryBalance copyOfNonMonetaryBalance = subscriberNonMonitoryBalance.copy();
        long deductedTimePulse = RnCPulseCalculator.floor(time, pulse);
        long deductedTime = RnCPulseCalculator.multiply(deductedTimePulse, pulse);

        copyOfNonMonetaryBalance.setBillingCycleAvailable(deductedTime);
        copyOfNonMonetaryBalance.setWeeklyLimit(deductedTime);
        copyOfNonMonetaryBalance.setDailyLimit(deductedTime);

        return copyOfNonMonetaryBalance;
    }
}
