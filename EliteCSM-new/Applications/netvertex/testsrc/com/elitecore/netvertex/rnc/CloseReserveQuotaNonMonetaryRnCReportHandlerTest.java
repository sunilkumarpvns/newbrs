package com.elitecore.netvertex.rnc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.UUID;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PCRFEvent;
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
public class CloseReserveQuotaNonMonetaryRnCReportHandlerTest {

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
        policyManager.init(folder.getRoot().getAbsolutePath(), hibernateSessionFactory.getSessionFactory(), new HibernateDataReader(), packageFactory,new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new RateCardVersionFactory(rnCFactory),rnCFactory),rnCFactory),rnCFactory,new ThresholdNotificationSchemeFactory(rnCFactory)), deploymentMode);
    }

    private void generatePolicyBackUpFile() throws IOException {
        File systemFolder = folder.newFolder("system");
        File file = new File(systemFolder.getAbsolutePath(), "policies.bkp");
        file.createNewFile();
    }


    public class OnlyNonMonetaryBalance {

        private RnCNonMonetaryBalance nonMonetoryBalance;

        public class rateOnTime {

            @Before
            public void setUp() {

                nonMonetoryBalance = createNonMonetaryBalance(request.getSPRInfo());

                SubscriberRnCNonMonetaryBalance SubscriberRnCNonMonetaryBalance = new SubscriberRnCNonMonetaryBalance(asList(nonMonetoryBalance));

                response.setCurrentRnCNonMonetaryBalance(SubscriberRnCNonMonetaryBalance);
                response.setCurrentMonetaryBalance(new SubscriberMonetaryBalance(new FixedTimeSource(System.currentTimeMillis())));

                createGrantedAndReportedMSCCForBalance(nonMonetoryBalance);

                getLogger().debug(CloseReserveQuotaNonMonetaryRnCReportHandlerTest.class.getSimpleName(), "Non-Monetary Balance is:" + nonMonetoryBalance);
                getLogger().debug(CloseReserveQuotaNonMonetaryRnCReportHandlerTest.class.getSimpleName(), "Quota Reservation is:" + response.getQuotaReservation());
                getLogger().debug(CloseReserveQuotaNonMonetaryRnCReportHandlerTest.class.getSimpleName(), "Reported Quota is:" + request.getReportedMSCCs());
            }

            public class NonMonetaryQuotaReserved {

                private MSCC grantedMSCC;

                @Before
                public void execute() {
                    grantedMSCC = response.getQuotaReservation().get(0l);
                    grantedMSCC.getGrantedServiceUnits().setReservationRequired(true);
                    reportHandler.process(request, response, executionContext);
                }

                @Test
                public void addReserveBalanceIntoCurrentBalance() {

                    RnCNonMonetaryBalance nonMonetoryBalance = OnlyNonMonetaryBalance.this.nonMonetoryBalance.copy();
                    removeReservation(nonMonetoryBalance, grantedMSCC.getGrantedServiceUnits().getTime());
                    SubscriberRnCNonMonetaryBalance expected = new SubscriberRnCNonMonetaryBalance(asList(nonMonetoryBalance));
                    ReflectionAssert.assertReflectionEquals(expected, response.getCurrentRnCNonMonetaryBalance());
                }

                @Test
                public void addReserveBalanceIntoReportedUsageFromABMF() {
                    RnCNonMonetaryBalance deductedNonMonetoryBalance = nonMonetoryBalance.copy();
                    deductedNonMonetoryBalance.resetWeeklyUsage();
                    deductedNonMonetoryBalance.resetDailyUsage();
                    deductedNonMonetoryBalance.setBillingCycleAvailable(0);
                    deductedNonMonetoryBalance.substractReservation(grantedMSCC.getGrantedServiceUnits().getTime());
                    SubscriberRnCNonMonetaryBalance expected = new SubscriberRnCNonMonetaryBalance(asList(deductedNonMonetoryBalance));
                    ReflectionAssert.assertReflectionEquals(expected, response.getAccountedRnCNonMonetaryBalance());
                }


                @Test
                public void removeQuotaReservationEntryForRatingGroupFromResponse() {
                    Assert.assertNull(response.getQuotaReservation());
                }
            }

        }
    }

    private void removeReservation(RnCNonMonetaryBalance nonMonetoryBalance, long time) {
        nonMonetoryBalance.substractReservation(time);
    }

    private MSCC createGrantedAndReportedMSCCForBalance(RnCNonMonetaryBalance nonMonetoryBalance) {
        MSCC grantedMSCC = createGrantedMSCC(nonMonetoryBalance);
        response.getQuotaReservation().put(grantedMSCC);
        request.getQuotaReservation().put(grantedMSCC);
        return grantedMSCC;
    }


    private MSCC createGrantedMSCC(RnCNonMonetaryBalance nonMonetoryBalance) {
        GyServiceUnits grantedServiceUnit = createGSU(nonMonetoryBalance);
        MSCC grantedMSCC = new MSCC();
        grantedMSCC.setRatingGroup(0);
        grantedMSCC.setGrantedServiceUnits(grantedServiceUnit);
        return grantedMSCC;
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
                0.0,60,0, 60,0,-1,0);
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
}