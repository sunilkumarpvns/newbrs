package com.elitecore.netvertex.pm.rnc;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
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
import com.elitecore.corenetvertex.spr.*;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.ddf.MonetaryBalanceOperation;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.rnc.ReportSummary;
import com.elitecore.netvertex.rnc.RnCEventABMFHandler;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class RnCEventABMFHandlerTest {

    private String subscriberId = UUID.randomUUID().toString();
    private static final String INR = "INR";
    private RnCEventABMFHandler rnCEventABMFHandler;
    private PCRFResponse response;
    private PCRFRequest request;
    private ExecutionContext executionContext;
    private SPRInfoImpl sprInfo;
    @Mock private MonetaryBalanceOperation monetaryBalanceOperation;
    @Rule public TemporaryFolder folder = new TemporaryFolder();
    private PackageHibernateSessionFactory hibernateSessionFactory;
	private DeploymentMode deploymentMode = DeploymentMode.PCC;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        rnCEventABMFHandler = new RnCEventABMFHandler();
        response = new PCRFResponseImpl();
        request = new PCRFRequestImpl();

        sprInfo = spy((SPRInfoImpl) new SPRInfoImpl.SPRInfoBuilder()
                .withSubscriberIdentity(subscriberId)
                .build());
        sprInfo.setBalanceProvider(mock(BalanceProvider.class));
        sprInfo.setProductOffer(UUID.randomUUID().toString());
        request.setSPRInfo(sprInfo);

        response.setQuotaReservation(new QuotaReservation());
        response.setReportedMSCCs(new ArrayList<>());
        response.setReportSummary(new ReportSummary());

        executionContext = spy(new ExecutionContext(request, response, mock(CacheAwareDDFTable.class), INR));
        when(executionContext.getDDFTable().getMonetaryBalanceOp()).thenReturn(monetaryBalanceOperation);

        initPolicyManager();
    }

    public class directDebitOperation {

        private RnCNonMonetaryBalance nonMonetoryBalance;
        private MonetaryBalance monetaryBalance;

        @Before
        public void execute() throws OperationFailedException {

            nonMonetoryBalance = createNonMonetaryBalance(request.getSPRInfo());

            SubscriberRnCNonMonetaryBalance subscriberNonMonitoryBalance = new SubscriberRnCNonMonetaryBalance(asList(nonMonetoryBalance));

            when(sprInfo.getRnCNonMonetaryBalance()).thenReturn(subscriberNonMonitoryBalance);
            when(executionContext.getCurrentRnCNonMonetaryBalance()).thenReturn(subscriberNonMonitoryBalance);

            SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(new FixedTimeSource(System.currentTimeMillis()));
            monetaryBalance = createMonetaryBalance(request.getSPRInfo().getSubscriberIdentity());
            subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
            response.setCurrentMonetaryBalance(subscriberMonetaryBalance);
            when(executionContext.getCurrentMonetaryBalance()).thenReturn(subscriberMonetaryBalance);

            createGrantedAndReportedMSCCForBalance(nonMonetoryBalance, monetaryBalance);
        }

        @Test
        public void testSkipProcessingWhenQuotaReservationIsNull() throws OperationFailedException {
            response.setQuotaReservation(null);

            rnCEventABMFHandler.handleDirectDebit(response, executionContext);

            verify(executionContext.getDDFTable(), times(0)).reportRnCBalance(anyString(), anyList());
            verify(executionContext.getDDFTable(), times(0)).getMonetaryBalanceOp();
        }

        @Test
        public void forNonMonetaryBalanceReportRnCBalanceShouldBeCalled() throws OperationFailedException {
            rnCEventABMFHandler.handleDirectDebit(response, executionContext);

            verify(executionContext.getDDFTable(), atLeastOnce()).reportRnCBalance(Mockito.anyString(), Mockito.anyListOf(RnCNonMonetaryBalance.class));
        }

        @Test
        public void forMonetaryBalanceDirectDebitBalanceShouldBeCalled() throws OperationFailedException {
            rnCEventABMFHandler.handleDirectDebit(response, executionContext);

            verify(monetaryBalanceOperation, atLeastOnce()).directDebitBalance(Mockito.anyListOf(MonetaryBalance.class));
        }

        @Test
        public void shouldNotBeCalledIfMonetaryAndNonMonetaryBalanceDoesNotExists() throws OperationFailedException {
            when(executionContext.getCurrentRnCNonMonetaryBalance()).thenReturn(null);
            when(executionContext.getCurrentMonetaryBalance()).thenReturn(null);

            rnCEventABMFHandler.handleDirectDebit(response, executionContext);

            verify(monetaryBalanceOperation, never()).directDebitBalance(Mockito.anyListOf(MonetaryBalance.class));
            verify(executionContext.getDDFTable(), never()).reportRnCBalance(Mockito.anyString(), Mockito.anyListOf(RnCNonMonetaryBalance.class));
        }

    }

    public class refundOperation {

        private RnCNonMonetaryBalance nonMonetoryBalance;
        private MonetaryBalance monetaryBalance;
        private MSCC mscc;

        @Before
        public void execute() throws OperationFailedException {

            nonMonetoryBalance = createNonMonetaryBalance(request.getSPRInfo());

            SubscriberRnCNonMonetaryBalance subscriberNonMonitoryBalance = new SubscriberRnCNonMonetaryBalance(asList(nonMonetoryBalance));

            when(sprInfo.getRnCNonMonetaryBalance()).thenReturn(subscriberNonMonitoryBalance);
            when(executionContext.getCurrentRnCNonMonetaryBalance()).thenReturn(subscriberNonMonitoryBalance);

            SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(new FixedTimeSource(System.currentTimeMillis()));
            monetaryBalance = createMonetaryBalance(request.getSPRInfo().getSubscriberIdentity());
            subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
            response.setCurrentMonetaryBalance(subscriberMonetaryBalance);
            when(executionContext.getCurrentMonetaryBalance()).thenReturn(subscriberMonetaryBalance);

            mscc = createGrantedAndReportedMSCCForBalance(nonMonetoryBalance, monetaryBalance);
        }

        @Test
        public void forNonMonetaryBalanceRefundRnCBalanceShouldBeCalled() throws OperationFailedException {
            rnCEventABMFHandler.handleRefund(response, mscc, executionContext);

            verify(executionContext.getDDFTable(), atLeastOnce()).refundRnCBalance(Mockito.anyString(), Mockito.anyListOf(RnCNonMonetaryBalance.class));
        }

        @Test
        public void forMonetaryBalanceRefundRnCBalanceShouldBeCalled() throws OperationFailedException {
            when(executionContext.getCurrentRnCNonMonetaryBalance()).thenReturn(null);
            rnCEventABMFHandler.handleRefund(response, mscc, executionContext);

            verify(executionContext.getDDFTable(), atLeastOnce()).updateMonetaryBalance(Mockito.anyString(), Mockito.any(SubscriberMonetaryBalanceWrapper.class), Mockito.anyString(), Mockito.anyString());

        }

        @Test
        public void shouldNotBeCalledifMonetaryAndNonMonetaryBalanceNotFoundForSubscriber() throws OperationFailedException {
            when(executionContext.getCurrentRnCNonMonetaryBalance()).thenReturn(null);
            when(executionContext.getCurrentMonetaryBalance()).thenReturn(null);
            rnCEventABMFHandler.handleRefund(response, mscc, executionContext);

            verify(executionContext.getDDFTable(), never()).refundRnCBalance(Mockito.anyString(), Mockito.anyListOf(RnCNonMonetaryBalance.class));
            verify(executionContext.getDDFTable(), never()).updateMonetaryBalance(Mockito.anyString(), Mockito.any(SubscriberMonetaryBalanceWrapper.class), Mockito.anyString(), Mockito.anyString());
        }

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
                System.currentTimeMillis(),0,
                "","");
    }

    private MSCC createGrantedAndReportedMSCCForBalance(RnCNonMonetaryBalance nonMonetoryBalance, MonetaryBalance monetaryBalance) {
        MSCC grantedMSCC = createGrantedMSCC(nonMonetoryBalance, monetaryBalance);
        response.getQuotaReservation().put(grantedMSCC);
        return grantedMSCC;
    }

    private MSCC createGrantedMSCC(RnCNonMonetaryBalance nonMonetoryBalance, MonetaryBalance monetaryBalance) {
        GyServiceUnits grantedServiceUnit = createGSU(nonMonetoryBalance, monetaryBalance);
        MSCC grantedMSCC = new MSCC();
        grantedMSCC.setGrantedServiceUnits(grantedServiceUnit);
        return grantedMSCC;
    }

    private GyServiceUnits createGSU(RnCNonMonetaryBalance nonMonetoryBalance, MonetaryBalance monetaryBalance) {
        return new GyServiceUnits(nonMonetoryBalance.getPackageId(),
                0,
                nonMonetoryBalance.getRatecardId(),
                nextLong(61, nonMonetoryBalance.getBillingCycleAvailable()),
                nonMonetoryBalance.getSubscriptionId(),
                0,
                nonMonetoryBalance.getId(),
                false,
                monetaryBalance.getId(),
                0d,
                0.0,60,0, 60,10,0, 0);
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

    @After
    public void tearDown() {
        hibernateSessionFactory.shutdown();
        PolicyManager.release();
        folder.delete();
    }
}
