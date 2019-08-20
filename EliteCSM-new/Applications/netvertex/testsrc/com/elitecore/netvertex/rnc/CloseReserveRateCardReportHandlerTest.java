package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.HibernateDataReader;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.factory.PackageHibernateSessionFactory;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCardVersion;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.FlatRating;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.FlatSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateCardVersion;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.ddf.MonetaryBalanceOperation;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class CloseReserveRateCardReportHandlerTest {

    public static final String INR = "INR";
    private ReportHandler reportHandler;
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
        setUpGetCurrency();
        policyRepository = (DummyPolicyRepository) dummyPCRFServiceContext.getServerContext().getPolicyRepository();
        reportHandler = new ReportHandler(dummyPCRFServiceContext, new QuotaReportHandlerFactory(policyRepository));

        response.setQuotaReservation(new QuotaReservation());
        request.setQuotaReservation(new QuotaReservation());

        request.setReportedMSCCs(new ArrayList<>());
        response.setReportedMSCCs(new ArrayList<>());

        initPolicyManager();
    }

    private void setUpGetCurrency() {
        DummyNetvertexServerContextImpl serverContext = DummyNetvertexServerContextImpl.spy();
        DummyNetvertexServerConfiguration netvertexServerConfiguration = DummyNetvertexServerConfiguration.spy();
        SystemParameterConfiguration systemParameterConfiguration = netvertexServerConfiguration.spySystemParameterConf();
        doReturn(INR).when(systemParameterConfiguration).getSystemCurrency();
        serverContext.setNetvertexServerConfiguration(netvertexServerConfiguration);
        dummyPCRFServiceContext.setServerContext(serverContext);
    }

    private void initPolicyManager() throws Exception {
        hibernateSessionFactory = PackageHibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml");
        PackageFactory packageFactory = new PackageFactory();
        generatePolicyBackUpFile();
        PolicyManager policyManager = new PolicyManager();
        PolicyManager.setInstance(policyManager);
        PolicyManager.getInstance().init(folder.getRoot().getAbsolutePath(), hibernateSessionFactory.getSessionFactory(), new HibernateDataReader(), packageFactory, null, deploymentMode);
    }

    @After
    public void tearDown() {
        hibernateSessionFactory.shutdown();
        PolicyManager.release();
        folder.delete();
    }

    private void generatePolicyBackUpFile() throws IOException {
        File systemFolder = folder.newFolder("system");
        File file = new File(systemFolder.getAbsolutePath(), "policies.bkp");
        file.createNewFile();
    }

    public class MonetaryBalances {

        private NonMonetoryBalance nonMonetoryBalance;
        private MonetaryBalance monetaryBalance;
        private MonetaryBalanceOperation monetaryBalanceOperation;
        private MockBasePackage mockBasePackage;

        public class rateOnVolume {

            @Before
            public void setUp() {

                mockBasePackage = createPackage(BigDecimal.valueOf(10.0), Uom.MB);

                nonMonetoryBalance = createNonMonetaryBalance();
                SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(asList(nonMonetoryBalance));

                response.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);

                SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(fixedTimeSource);
                monetaryBalance = createMonetaryBalance(request.getSPRInfo().getSubscriberIdentity());
                subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
                response.setCurrentMonetaryBalance(subscriberMonetaryBalance);

                createGrantedAndReportedMSCCForBalance(monetaryBalance);

                monetaryBalanceOperation = mock(MonetaryBalanceOperation.class);
                when(executionContext.getDDFTable().getMonetaryBalanceOp()).thenReturn(monetaryBalanceOperation);

                getLogger().debug(CloseReserveQuotaReportHandlerTest.class.getSimpleName(), "Monetary Balance is:" + monetaryBalance);
                getLogger().debug(CloseReserveQuotaReportHandlerTest.class.getSimpleName(), "Quota Reservation is:" + response.getQuotaReservation());
                getLogger().debug(CloseReserveQuotaReportHandlerTest.class.getSimpleName(), "Reported Quota is:" + request.getReportedMSCCs());
                getLogger().debug(CloseReserveQuotaReportHandlerTest.class.getSimpleName(), "Rate Card Profile Detail is:" + getRateCardDetails(mockBasePackage));

            }

            public class UnAccountedQuotaIsNull {

                @Before
                public void execute() {
                    reportHandler.process(request, response, executionContext);
                }


                @Test
                public void removeMonetaryReservationFromABMF() {
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance, request.getQuotaReservation().remove(Long.valueOf(0))));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromCurrentBalance() {

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance, request.getQuotaReservation().get(Long.valueOf(0))));

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
                    unAccountedQuota = createGrantedMSCC(monetaryBalance);
                    unAccountedQuota.getGrantedServiceUnits().setMonetaryBalanceId(monetaryBalance.getId());
                    QuotaReservation unAccountedReservation = new QuotaReservation();
                    unAccountedReservation.put(unAccountedQuota);
                    response.setUnAccountedQuota(unAccountedReservation);

                    getLogger().debug(CloseReserveQuotaReportHandlerTest.class.getSimpleName(), "UnAccounted Quota:" + unAccountedQuota);
                    reportHandler.process(request, response, executionContext);
                }


                @Test
                public void removeMonetaryReservationFromABMF() {
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(Long.valueOf(0))));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }


                @Test
                public void removeMonetaryReservationFromCurrentBalance() {
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(Long.valueOf(0))));

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

                private MonetaryBalance createDeductedMonetaryBalance(MonetaryBalance monetaryBalance,
                                                                      MSCC grantedMSCC) {
                    GyServiceUnits grantedServiceUnits = unAccountedQuota.getGrantedServiceUnits();
                    MonetaryBalance copy = monetaryBalance.copy();
                    long calculatedVolumePulse = grantedServiceUnits.calculateCeilPulse(grantedServiceUnits.getVolume());
                    long deductableVolume = grantedServiceUnits.calculateDeductableQuota(calculatedVolumePulse);
                    BigDecimal deductedMonetaryBalance = grantedServiceUnits.calculateDeductableMoney(deductableVolume);
                    copy.setAvailBalance(deductedMonetaryBalance.doubleValue());
                    copy.setTotalReservation(0);
                    copy.substractReservation(grantedMSCC.getGrantedServiceUnits().getReservedMonetaryBalance());

                    return copy;
                }

                private MonetaryBalance createMonetaryBalance(MonetaryBalance monetaryBalance,
                                                              MSCC grantedMSCC) {
                    GyServiceUnits usedServiceUnits = unAccountedQuota.getUsedServiceUnits();
                    GyServiceUnits grantedServiceUnits = unAccountedQuota.getGrantedServiceUnits();
                    MonetaryBalance copy = monetaryBalance.copy();
                    long calculatedVolumePulse = grantedServiceUnits.calculateCeilPulse(usedServiceUnits.getVolume());
                    long deductableVolume = grantedServiceUnits.calculateDeductableQuota(calculatedVolumePulse);
                    BigDecimal deductedMonetaryBalance = grantedServiceUnits.calculateDeductableMoney(deductableVolume);
                    copy.substract(deductedMonetaryBalance.doubleValue());
                    copy.substractReservation(grantedMSCC.getGrantedServiceUnits().getReservedMonetaryBalance());

                    return copy;
                }
            }
        }

        public class rateOnTime {

            @Before
            public void setUp() {

                mockBasePackage = createPackage(BigDecimal.valueOf(10.0), Uom.SECOND);

                nonMonetoryBalance = createNonMonetaryBalance();
                SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(asList(nonMonetoryBalance));

                response.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);

                SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(fixedTimeSource);
                monetaryBalance = createMonetaryBalance(request.getSPRInfo().getSubscriberIdentity());
                subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
                response.setCurrentMonetaryBalance(subscriberMonetaryBalance);

                createGrantedAndReportedMSCCForBalance(monetaryBalance);

                monetaryBalanceOperation = mock(MonetaryBalanceOperation.class);
                when(executionContext.getDDFTable().getMonetaryBalanceOp()).thenReturn(monetaryBalanceOperation);

                getLogger().debug(CloseReserveQuotaReportHandlerTest.class.getSimpleName(), "Monetary Balance is:" + monetaryBalance);
                getLogger().debug(CloseReserveQuotaReportHandlerTest.class.getSimpleName(), "Quota Reservation is:" + response.getQuotaReservation());
                getLogger().debug(CloseReserveQuotaReportHandlerTest.class.getSimpleName(), "Reported Quota is:" + request.getReportedMSCCs());
                getLogger().debug(CloseReserveQuotaReportHandlerTest.class.getSimpleName(), "Rate Card Profile Detail is:" + getRateCardDetails(mockBasePackage));

            }

            public class UnAccountedQuotaIsNull {

                @Before
                public void execute() {
                    reportHandler.process(request, response, executionContext);
                }


                @Test
                public void removeMonetaryReservationFromABMF() {
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance, request.getQuotaReservation().remove(Long.valueOf(0))));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromCurrentBalance() {

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance, request.getQuotaReservation().get(Long.valueOf(0))));

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
                    unAccountedQuota = createGrantedMSCC(monetaryBalance);
                    unAccountedQuota.getGrantedServiceUnits().setMonetaryBalanceId(monetaryBalance.getId());
                    QuotaReservation unAccountedReservation = new QuotaReservation();
                    unAccountedReservation.put(unAccountedQuota);
                    response.setUnAccountedQuota(unAccountedReservation);

                    getLogger().debug(CloseReserveQuotaReportHandlerTest.class.getSimpleName(), "UnAccounted Quota:" + unAccountedQuota);
                    reportHandler.process(request, response, executionContext);
                }


                @Test
                public void removeMonetaryReservationFromABMF() {
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(Long.valueOf(0))));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }


                @Test
                public void removeMonetaryReservationFromCurrentBalance() {
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(Long.valueOf(0))));

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

                private MonetaryBalance createDeductedMonetaryBalance(MonetaryBalance monetaryBalance,
                                                                      MSCC grantedMSCC) {
                    GyServiceUnits grantedServiceUnits = unAccountedQuota.getGrantedServiceUnits();
                    MonetaryBalance copy = monetaryBalance.copy();
                    long calculatedTimePulse = grantedServiceUnits.calculateCeilPulse(grantedServiceUnits.getTime());
                    long deductableTime = grantedServiceUnits.calculateDeductableQuota(calculatedTimePulse);
                    BigDecimal deductedMonetaryBalance = grantedServiceUnits.calculateDeductableMoney(deductableTime);
                    copy.setAvailBalance(deductedMonetaryBalance.doubleValue());
                    copy.setTotalReservation(0);
                    copy.substractReservation(grantedMSCC.getGrantedServiceUnits().getReservedMonetaryBalance());

                    return copy;
                }

                private MonetaryBalance createMonetaryBalance(MonetaryBalance monetaryBalance,
                                                              MSCC grantedMSCC) {
                    GyServiceUnits usedServiceUnits = unAccountedQuota.getUsedServiceUnits();
                    GyServiceUnits grantedServiceUnits = unAccountedQuota.getGrantedServiceUnits();
                    MonetaryBalance copy = monetaryBalance.copy();
                    long calculatedTimePulse = grantedServiceUnits.calculateCeilPulse(usedServiceUnits.getTime());
                    long deductableTime = grantedServiceUnits.calculateDeductableQuota(calculatedTimePulse);
                    BigDecimal deductedMonetaryBalance = grantedServiceUnits.calculateDeductableMoney(deductableTime);
                    copy.substract(deductedMonetaryBalance.doubleValue());
                    copy.substractReservation(grantedMSCC.getGrantedServiceUnits().getReservedMonetaryBalance());

                    return copy;
                }
            }
        }

        private MockBasePackage createPackage(BigDecimal rate, Uom rateOn) {
            MockBasePackage mockBasePackage = policyRepository.mockBasePackage();
            mockBasePackage.quotaProfileTypeIsRnC();

            mockBasePackage.mockRateCard(createRateCardDetails(rate, rateOn));

            return mockBasePackage;
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

        private MSCC createGrantedAndReportedMSCCForBalance(MonetaryBalance monetaryBalance) {
            MSCC grantedMSCC = createGrantedMSCC(monetaryBalance);
            response.getQuotaReservation().put(grantedMSCC);
            request.getQuotaReservation().put(grantedMSCC);
            return grantedMSCC;
        }

        private MSCC createGrantedMSCC(MonetaryBalance monetaryBalance) {
            GyServiceUnits grantedServiceUnit = createGSU(monetaryBalance);
            grantedServiceUnit.setMonetaryBalanceId(monetaryBalance.getId());
            grantedServiceUnit.setReservedMonetaryBalance(nextLong(1, Double.valueOf(monetaryBalance.getTotalReservation()).intValue()));
            grantedServiceUnit.setTimePulse(nextInt(2, 5));
            grantedServiceUnit.setVolumePulse(nextInt(2, 5));
            grantedServiceUnit.setRateMinorUnit(100);
            MSCC grantedMSCC = new MSCC();
            grantedMSCC.setGrantedServiceUnits(grantedServiceUnit);
            grantedMSCC.setUsedServiceUnits(grantedServiceUnit);

            return grantedMSCC;
        }

        private GyServiceUnits createGSU(MonetaryBalance monetaryBalance) {
            return new GyServiceUnits(mockBasePackage.getId(),
                    nextInt(5, 10),
                    "1",
                    nextInt(5, 10),
                    null,
                    0,
                    null,
                    false,
                    monetaryBalance.getId(),
                    0d,
                    100.0,
                    nextInt(2, 5),
                    100,
                    nextInt(2, 5), 0,-1, 0);
        }

        private DataRateCard createRateCardDetails(BigDecimal rate, Uom rateOn){
            RateSlab slab = new FlatSlab(20, 10, rate, rateOn, rateOn);
            VersionDetail versionDetail = new FlatRating("Calling-Station-ID", "Calling-Station-ID", Arrays.asList(slab), null);

            RateCardVersion rateCardVersion = new DataRateCardVersion("1", "RC1", "version1", Arrays.asList(versionDetail));

            return new DataRateCard("1", "test", "Calling-Station-ID", "Calling-Station-ID", Arrays.asList(rateCardVersion), rateOn, rateOn);

        }

        private DataRateCard getRateCardDetails(UserPackage userPackage){
            return userPackage.getDataRateCard("1");
        }

        private MonetaryBalance createDeductedMonetaryBalance(MonetaryBalance monetaryBalance,MSCC grantedMSCC) {
            MonetaryBalance copy = monetaryBalance.copy();

            copy.setAvailBalance(0);
            copy.setTotalReservation(0);
            copy.substractReservation(grantedMSCC.getGrantedServiceUnits().getReservedMonetaryBalance());
            return copy;
        }

        private NonMonetoryBalance createNonMonetaryBalance() {

            return new NonMonetoryBalance.NonMonetaryBalanceBuilder()
                    .build();
        }

        private MonetaryBalance createMonetaryBalance(MonetaryBalance monetaryBalance, MSCC grantedMSCC) {
            MonetaryBalance copy = monetaryBalance.copy();

            copy.substractReservation(grantedMSCC.getGrantedServiceUnits().getReservedMonetaryBalance());

            return copy;
        }
    }

}
