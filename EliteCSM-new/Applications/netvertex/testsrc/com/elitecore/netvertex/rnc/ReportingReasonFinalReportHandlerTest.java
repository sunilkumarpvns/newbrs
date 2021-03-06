package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DeploymentMode;
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
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.gy.ReportingReason;
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
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.netvertex.core.util.Maps.Entry.newEntry;
import static com.elitecore.netvertex.core.util.Maps.newLinkedHashMap;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class ReportingReasonFinalReportHandlerTest {

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
        doReturn("INR").when(systemParameterConfiguration).getSystemCurrency();
        serverContext.setNetvertexServerConfiguration(netvertexServerConfiguration);
        dummyPCRFServiceContext.setServerContext(serverContext);
    }

    private void initPolicyManager() throws Exception {
        hibernateSessionFactory = PackageHibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml");
        PackageFactory packageFactory = new PackageFactory();
        generatePolicyBackUpFile();

        PolicyManager policyManager = new PolicyManager();
        PolicyManager.setInstance(policyManager);
        RnCFactory rnCFactory = new RnCFactory();
        policyManager.init(folder.getRoot().getAbsolutePath(), hibernateSessionFactory.getSessionFactory(), new HibernateDataReader(),
				packageFactory,new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new RateCardVersionFactory(rnCFactory),rnCFactory),rnCFactory),rnCFactory,
						new ThresholdNotificationSchemeFactory(rnCFactory)), deploymentMode);
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

    public class MonetaryAndNonMonetaryBalance {

        private NonMonetoryBalance nonMonetoryBalance;
        private MonetaryBalance monetaryBalance;
        private MonetaryBalanceOperation monetaryBalanceOperation;
        private MockBasePackage mockBasePackage;

        public class rateOnVolume {

            @Before
            public void setUp() {

                mockBasePackage = createPackage(2.0, UsageType.VOLUME);

                nonMonetoryBalance = createNonMonetaryBalance(request.getSPRInfo(), mockBasePackage);

                SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(asList(nonMonetoryBalance));

                response.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);
                SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(fixedTimeSource);
                monetaryBalance = ReportingReasonFinalReportHandlerTest.this.createMonetaryBalance(request.getSPRInfo().getSubscriberIdentity());
                subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
                response.setCurrentMonetaryBalance(subscriberMonetaryBalance);

                createGrantedAndReportedMSCCForBalance(nonMonetoryBalance, monetaryBalance);


                monetaryBalanceOperation = mock(MonetaryBalanceOperation.class);
                when(executionContext.getDDFTable().getMonetaryBalanceOp()).thenReturn(monetaryBalanceOperation);


                getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Non-Monetary Balance is:" + nonMonetoryBalance);
                getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Monetary Balance is:" + monetaryBalance);
                getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Quota Reservation is:" + response.getQuotaReservation());
                getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Reported Quota is:" + request.getReportedMSCCs());
                getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Quota Profile Detail is:" + getQuotaProfileDetail(mockBasePackage));

            }


            public class UnAccountedQuotaIsNull {

                @Before
                public void execute() {
                    reportHandler.process(request, response, executionContext);
                }

                @Test
                public void deductReportedUsageFromCurrentBalance() {

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    SubscriberNonMonitoryBalance expected = new SubscriberNonMonitoryBalance(asList(createNonMonetoryBalance(nonMonetoryBalance, usedServiceUnits.getVolume(), usedServiceUnits.getTime(), mockBasePackage)));
                    ReflectionAssert.assertReflectionEquals(expected, response.getCurrentNonMonetoryBalance());
                }

                @Test
                public void deductReportedUsageFromABMF() {

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    SubscriberNonMonitoryBalance expected = new SubscriberNonMonitoryBalance(asList(createDeductedNonMonetoryBalance(nonMonetoryBalance, usedServiceUnits.getVolume(), usedServiceUnits.getTime(), mockBasePackage)));
                    ReflectionAssert.assertReflectionEquals(expected, response.getAccountedNonMonetaryBalance());
                }

                @Test
                public void deductMonetaryBalanceFromABMF() {

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    NonMonetoryBalance deductedNonMonetaryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance,
                            usedServiceUnits.getVolume(), usedServiceUnits.getTime(),
                            mockBasePackage);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().remove(deductedNonMonetaryBalance.getRatingGroupId()),
                            deductedNonMonetaryBalance,
                            mockBasePackage));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromABMF() {
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    NonMonetoryBalance deductedNonMonetaryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance,
                            usedServiceUnits.getVolume(), usedServiceUnits.getTime(),
                            mockBasePackage);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().remove(deductedNonMonetaryBalance.getRatingGroupId()),
                            deductedNonMonetaryBalance,
                            mockBasePackage));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void deductMonetaryBalanceAccordingToRate() {

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    NonMonetoryBalance deductedNonMonatoryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance,
                            usedServiceUnits.getVolume(), usedServiceUnits.getTime(),
                            mockBasePackage);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(deductedNonMonatoryBalance.getRatingGroupId()),
                            deductedNonMonatoryBalance,
                            mockBasePackage));

                    SubscriberMonetaryBalance actual = response.getCurrentMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromCurrentBalance() {

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    NonMonetoryBalance deductedNonMonatoryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance,
                            usedServiceUnits.getVolume(), usedServiceUnits.getTime(),
                            mockBasePackage);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(deductedNonMonatoryBalance.getRatingGroupId()),
                            deductedNonMonatoryBalance,
                            mockBasePackage));

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

                    getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "UnAccounted Quota:" + unAccountedQuota);
                    reportHandler.process(request, response, executionContext);
                }

                @Test
                public void deductReportedUsageFromCurrentBalance() {

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();
                    long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                    SubscriberNonMonitoryBalance expected = new SubscriberNonMonitoryBalance(asList(createNonMonetoryBalance(nonMonetoryBalance, volume, time, mockBasePackage)));
                    ReflectionAssert.assertReflectionEquals(expected, response.getCurrentNonMonetoryBalance());
                }

                @Test
                public void deductReportedUsageFromABMF() {
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();
                    long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                    SubscriberNonMonitoryBalance expected = new SubscriberNonMonitoryBalance(asList(createDeductedNonMonetoryBalance(nonMonetoryBalance, volume, time, mockBasePackage)));
                    ReflectionAssert.assertReflectionEquals(expected, response.getAccountedNonMonetaryBalance());
                }

                @Test
                public void deductMonetaryBalanceFromABMF() {

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                    long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();
                    NonMonetoryBalance deductedNonMonetaryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance,
                            volume, time,
                            mockBasePackage);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().remove(deductedNonMonetaryBalance.getRatingGroupId()),
                            deductedNonMonetaryBalance,
                            mockBasePackage));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromABMF() {
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                    long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();
                    NonMonetoryBalance deductedNonMonetaryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance,
                            volume, time,
                            mockBasePackage);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().remove(deductedNonMonetaryBalance.getRatingGroupId()),
                            deductedNonMonetaryBalance,
                            mockBasePackage));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void deductMonetaryBalanceAccordingToRate() {

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();

                    long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                    NonMonetoryBalance deductedNonMonatoryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance,
                            volume, time,
                            mockBasePackage);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(deductedNonMonatoryBalance.getRatingGroupId()),
                            deductedNonMonatoryBalance,
                            mockBasePackage));

                    SubscriberMonetaryBalance actual = response.getCurrentMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromCurrentBalance() {
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();

                    long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                    NonMonetoryBalance deductedNonMonatoryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance,
                            volume, time,
                            mockBasePackage);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(deductedNonMonatoryBalance.getRatingGroupId()),
                            deductedNonMonatoryBalance,
                            mockBasePackage));

                    SubscriberMonetaryBalance actual = response.getCurrentMonetaryBalance();
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

            public class NonMonetaryQuotaReserved {

                private MSCC grantedMSCC;

                @Before
                public void execute() {
                    grantedMSCC = response.getQuotaReservation().get(nonMonetoryBalance.getRatingGroupId());
                    grantedMSCC.getGrantedServiceUnits().setReservationRequired(true);
                    reportHandler.process(request, response, executionContext);
                }

                @Test
                public void addReserveBalanceIntoCurrentBalance() {

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long volume = usedServiceUnits.getVolume();
                    long time = usedServiceUnits.getTime();
                    NonMonetoryBalance nonMonetoryBalance = createNonMonetoryBalance(MonetaryAndNonMonetaryBalance.this.nonMonetoryBalance, volume, time, mockBasePackage);
                    removeReservation(nonMonetoryBalance, grantedMSCC.getGrantedServiceUnits().getVolume(), grantedMSCC.getGrantedServiceUnits().getTime());
                    SubscriberNonMonitoryBalance expected = new SubscriberNonMonitoryBalance(asList(nonMonetoryBalance));
                    ReflectionAssert.assertReflectionEquals(expected, response.getCurrentNonMonetoryBalance());
                }

                @Test
                public void addReserveBalanceIntoReportedUsageFromABMF() {
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long volume = usedServiceUnits.getVolume();
                    long time = usedServiceUnits.getTime();
                    NonMonetoryBalance deductedNonMonetoryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance, volume, time, mockBasePackage);
                    deductedNonMonetoryBalance.substractReservation(grantedMSCC.getGrantedServiceUnits().getVolume(), grantedMSCC.getGrantedServiceUnits().getTime());
                    SubscriberNonMonitoryBalance expected = new SubscriberNonMonitoryBalance(asList(deductedNonMonetoryBalance));
                    ReflectionAssert.assertReflectionEquals(expected, response.getAccountedNonMonetaryBalance());
                }


                @Test
                public void removeQuotaReservationEntryForRatingGroupFromResponse() {
                    Assert.assertNull(response.getQuotaReservation());
                }
            }

        }

        private void removeReservation(NonMonetoryBalance nonMonetoryBalance, long volume, long time) {
            nonMonetoryBalance.substractReservation(volume, time);
        }

        public class rateOnTime {

            @Before
            public void setUp() {

                mockBasePackage = createPackage(2.0, UsageType.TIME);

                nonMonetoryBalance = createNonMonetaryBalance(request.getSPRInfo(), mockBasePackage);

                SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(asList(nonMonetoryBalance));

                response.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);
                SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(fixedTimeSource);
                monetaryBalance = ReportingReasonFinalReportHandlerTest.this.createMonetaryBalance(request.getSPRInfo().getSubscriberIdentity());
                subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
                response.setCurrentMonetaryBalance(subscriberMonetaryBalance);

                createGrantedAndReportedMSCCForBalance(nonMonetoryBalance, monetaryBalance);


                monetaryBalanceOperation = mock(MonetaryBalanceOperation.class);
                when(executionContext.getDDFTable().getMonetaryBalanceOp()).thenReturn(monetaryBalanceOperation);


                getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Non-Monetary Balance is:" + nonMonetoryBalance);
                getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Monetary Balance is:" + monetaryBalance);
                getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Quota Reservation is:" + response.getQuotaReservation());
                getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Reported Quota is:" + request.getReportedMSCCs());
                getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Quota Profile Detail is:" + getQuotaProfileDetail(mockBasePackage));

            }


            public class UnAccountedQuotaIsNull {

                @Before
                public void execute() {
                    reportHandler.process(request, response, executionContext);

                    getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Non-Monetary Balance after reporting is:" + response.getCurrentNonMonetoryBalance().getBalanceById(nonMonetoryBalance.getId()));
                    getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Monetary Balance after reporting is:" + response.getCurrentMonetaryBalance().getBalanceById(monetaryBalance.getId()));
                    getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Accounted Non-Monetary Balance after reporting is:" + response.getAccountedNonMonetaryBalance().getBalanceById(nonMonetoryBalance.getId()));
                    getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Accounted Monetary Balance after reporting is:" + response.getAccountedMonetaryBalance().getBalanceById(monetaryBalance.getId()));
                }

                @Test
                public void deductReportedUsageFromCurrentBalance() {

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    SubscriberNonMonitoryBalance expected = new SubscriberNonMonitoryBalance(asList(createNonMonetoryBalance(nonMonetoryBalance, usedServiceUnits.getVolume(), usedServiceUnits.getTime(), mockBasePackage)));
                    ReflectionAssert.assertReflectionEquals(expected, response.getCurrentNonMonetoryBalance());
                }

                @Test
                public void deductReportedUsageFromABMF() {

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    SubscriberNonMonitoryBalance expected = new SubscriberNonMonitoryBalance(asList(createDeductedNonMonetoryBalance(nonMonetoryBalance, usedServiceUnits.getVolume(), usedServiceUnits.getTime(), mockBasePackage)));
                    ReflectionAssert.assertReflectionEquals(expected, response.getAccountedNonMonetaryBalance());
                }

                @Test
                public void deductMonetaryBalanceFromABMF() {

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    NonMonetoryBalance deductedNonMonetaryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance,
                            usedServiceUnits.getVolume(), usedServiceUnits.getTime(),
                            mockBasePackage);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().remove(deductedNonMonetaryBalance.getRatingGroupId()),
                            deductedNonMonetaryBalance,
                            mockBasePackage));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromABMF() {

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    NonMonetoryBalance deductedNonMonetaryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance,
                            usedServiceUnits.getVolume(), usedServiceUnits.getTime(),
                            mockBasePackage);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().remove(deductedNonMonetaryBalance.getRatingGroupId()),
                            deductedNonMonetaryBalance,
                            mockBasePackage));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void deductMonetaryBalanceAccordingToRate() {

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    NonMonetoryBalance deductedNonMonatoryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance,
                            usedServiceUnits.getVolume(), usedServiceUnits.getTime(),
                            mockBasePackage);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(deductedNonMonatoryBalance.getRatingGroupId()),
                            deductedNonMonatoryBalance,
                            mockBasePackage));

                    SubscriberMonetaryBalance actual = response.getCurrentMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromCurrentBalance() {
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    NonMonetoryBalance deductedNonMonatoryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance,
                            usedServiceUnits.getVolume(), usedServiceUnits.getTime(),
                            mockBasePackage);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(deductedNonMonatoryBalance.getRatingGroupId()),
                            deductedNonMonatoryBalance,
                            mockBasePackage));

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

                    getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "UnAccounted Quota:" + unAccountedQuota);
                    reportHandler.process(request, response, executionContext);
                    getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Non-Monetary Balance after reporting is:" + response.getCurrentNonMonetoryBalance().getBalanceById(nonMonetoryBalance.getId()));
                    getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Monetary Balance after reporting is:" + response.getCurrentMonetaryBalance().getBalanceById(monetaryBalance.getId()));
                    getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Accounted Non-Monetary Balance after reporting is:" + response.getAccountedNonMonetaryBalance().getBalanceById(nonMonetoryBalance.getId()));
                    getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Accounted Monetary Balance after reporting is:" + response.getAccountedMonetaryBalance().getBalanceById(monetaryBalance.getId()));
                }

                @Test
                public void deductReportedUsageFromCurrentBalance() {

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();
                    long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                    SubscriberNonMonitoryBalance expected = new SubscriberNonMonitoryBalance(asList(createNonMonetoryBalance(nonMonetoryBalance, volume, time, mockBasePackage)));
                    ReflectionAssert.assertReflectionEquals(expected, response.getCurrentNonMonetoryBalance());
                }

                @Test
                public void deductReportedUsageFromABMF() {
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();
                    long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                    SubscriberNonMonitoryBalance expected = new SubscriberNonMonitoryBalance(asList(createDeductedNonMonetoryBalance(nonMonetoryBalance, volume, time, mockBasePackage)));
                    ReflectionAssert.assertReflectionEquals(expected, response.getAccountedNonMonetaryBalance());
                }

                @Test
                public void deductMonetaryBalanceFromABMF() {

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                    long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();
                    NonMonetoryBalance deductedNonMonetaryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance,
                            volume, time,
                            mockBasePackage);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().remove(deductedNonMonetaryBalance.getRatingGroupId()),
                            deductedNonMonetaryBalance,
                            mockBasePackage));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromABMF() {

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                    long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();
                    NonMonetoryBalance deductedNonMonetaryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance,
                            volume, time,
                            mockBasePackage);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().remove(deductedNonMonetaryBalance.getRatingGroupId()),
                            deductedNonMonetaryBalance,
                            mockBasePackage));


                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void deductMonetaryBalanceAccordingToRate() {

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();

                    long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                    NonMonetoryBalance deductedNonMonatoryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance,
                            volume, time,
                            mockBasePackage);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(deductedNonMonatoryBalance.getRatingGroupId()),
                            deductedNonMonatoryBalance,
                            mockBasePackage));

                    SubscriberMonetaryBalance actual = response.getCurrentMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromCurrentBalance() {
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();

                    long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                    NonMonetoryBalance deductedNonMonatoryBalance = createDeductedNonMonetoryBalance(nonMonetoryBalance,
                            volume, time,
                            mockBasePackage);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(deductedNonMonatoryBalance.getRatingGroupId()),
                            deductedNonMonatoryBalance,
                            mockBasePackage));

                    SubscriberMonetaryBalance actual = response.getCurrentMonetaryBalance();
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
                System.currentTimeMillis(),
                0,
                "","");
    }

    public class OnlyNonMonetaryBalance {

        private NonMonetoryBalance nonMonetoryBalance;
        private MockBasePackage mockBasePackage;

        @Before
        public void execute() {

            mockBasePackage = createPackage(0.0, UsageType.VOLUME);

            nonMonetoryBalance = createNonMonetaryBalance(request.getSPRInfo(), mockBasePackage);

            SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(asList(nonMonetoryBalance));

            response.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);
            response.setCurrentMonetaryBalance(new SubscriberMonetaryBalance(fixedTimeSource));

            createGrantedAndReportedMSCCForBalance(nonMonetoryBalance);

            getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Non-Monetary Balance is:" + nonMonetoryBalance);
            getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Quota Reservation is:" + response.getQuotaReservation());
            getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Reported Quota is:" + request.getReportedMSCCs());
            getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Quota Profile Detail is:" + getQuotaProfileDetail(mockBasePackage));

        }


        public class UnAccountedQuotaIsNull {

            @Before
            public void execute() {
                reportHandler.process(request, response, executionContext);

                getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Non-Monetary Balance after reporting is:" + response.getCurrentNonMonetoryBalance().getBalanceById(nonMonetoryBalance.getId()));
                getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Accounted Non-Monetary Balance after reporting is:" + response.getAccountedNonMonetaryBalance().getBalanceById(nonMonetoryBalance.getId()));
            }

            @Test
            public void deductReportedUsageFromCurrentBalance() {

                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                SubscriberNonMonitoryBalance expected = new SubscriberNonMonitoryBalance(asList(createNonMonetoryBalance(nonMonetoryBalance, usedServiceUnits.getVolume(), usedServiceUnits.getTime(), mockBasePackage)));
                ReflectionAssert.assertReflectionEquals(expected, response.getCurrentNonMonetoryBalance());
            }

            @Test
            public void deductReportedUsageFromABMF() {

                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                SubscriberNonMonitoryBalance expected = new SubscriberNonMonitoryBalance(asList(createDeductedNonMonetoryBalance(nonMonetoryBalance, usedServiceUnits.getVolume(), usedServiceUnits.getTime(), mockBasePackage)));
                ReflectionAssert.assertReflectionEquals(expected, response.getAccountedNonMonetaryBalance());
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
                QuotaReservation unAccountedReservation = new QuotaReservation();
                unAccountedReservation.put(unAccountedQuota);
                response.setUnAccountedQuota(unAccountedReservation);

                getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "UnAccounted Quota:" + unAccountedQuota);
                reportHandler.process(request, response, executionContext);

                getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Non-Monetary Balance after reporting is:" + response.getCurrentNonMonetoryBalance().getBalanceById(nonMonetoryBalance.getId()));
                getLogger().debug(ReportingReasonFinalReportHandlerTest.class.getSimpleName(), "Accounted Non-Monetary Balance after reporting is:" + response.getAccountedNonMonetaryBalance().getBalanceById(nonMonetoryBalance.getId()));
            }

            @Test
            public void deductReportedUsageFromCurrentBalance() {

                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();
                long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                SubscriberNonMonitoryBalance expected = new SubscriberNonMonitoryBalance(asList(createNonMonetoryBalance(nonMonetoryBalance, volume, time, mockBasePackage)));
                ReflectionAssert.assertReflectionEquals(expected, response.getCurrentNonMonetoryBalance());
            }

            @Test
            public void deductReportedUsageFromABMF() {
                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();
                long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                SubscriberNonMonitoryBalance expected = new SubscriberNonMonitoryBalance(asList(createDeductedNonMonetoryBalance(nonMonetoryBalance, volume, time, mockBasePackage)));
                ReflectionAssert.assertReflectionEquals(expected, response.getAccountedNonMonetaryBalance());
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


    private MSCC createGrantedAndReportedMSCCForBalance(NonMonetoryBalance nonMonetoryBalance) {
        MSCC grantedMSCC = createGrantedMSCC(nonMonetoryBalance);
        response.getQuotaReservation().put(grantedMSCC);
        request.getQuotaReservation().put(grantedMSCC);
        MSCC usedMSCC = createUsedMSCC(grantedMSCC);
        request.getReportedMSCCs().add(usedMSCC);
        response.getReportedMSCCs().add(usedMSCC);
        return grantedMSCC;
    }

    private MSCC createGrantedAndReportedMSCCForBalance(NonMonetoryBalance nonMonetoryBalance, MonetaryBalance monetaryBalance) {
        MSCC grantedMSCC = createGrantedMSCC(nonMonetoryBalance, monetaryBalance);
        response.getQuotaReservation().put(grantedMSCC);
        request.getQuotaReservation().put(grantedMSCC);
        MSCC usedMSCC = createUsedMSCC(grantedMSCC);
        request.getReportedMSCCs().add(usedMSCC);
        response.getReportedMSCCs().add(usedMSCC);
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
        grantedServiceUnit.setReservedMonetaryBalance(nextLong(0, Double.valueOf(monetaryBalance.getTotalReservation()).intValue()));
        MSCC grantedMSCC = new MSCC();
        grantedMSCC.setRatingGroup(nonMonetoryBalance.getRatingGroupId());
        grantedMSCC.setGrantedServiceUnits(grantedServiceUnit);
        return grantedMSCC;
    }

    private MSCC createUsedMSCC(MSCC grantedMSCC) {
        GyServiceUnits usedServiceUnits = createUsedServiceUnit(grantedMSCC.getGrantedServiceUnits());
        MSCC reportedMSCC = new MSCC();
        reportedMSCC.setRatingGroup(grantedMSCC.getRatingGroup());
        reportedMSCC.setReportingReason(ReportingReason.FINAL);
        reportedMSCC.setUsedServiceUnits(usedServiceUnits);

        return reportedMSCC;
    }

    private GyServiceUnits createGSU(NonMonetoryBalance nonMonetoryBalance) {
        return new GyServiceUnits(nonMonetoryBalance.getPackageId(),
                nextLong(1, nonMonetoryBalance.getBillingCycleAvailableVolume()),
                nonMonetoryBalance.getQuotaProfileId(),
                nextLong(1, nonMonetoryBalance.getBillingCycleAvailableTime()),
                nonMonetoryBalance.getSubscriptionId(),
                0,
                nonMonetoryBalance.getId(),
                false,
                null,
                0d,
                0.0,0,0, 0,0,-1, nonMonetoryBalance.getLevel());
    }

    private GyServiceUnits createUsedServiceUnit(GyServiceUnits grantedServiceUnit) {
        long time = nextLong(1, grantedServiceUnit.getTime());
        return new GyServiceUnits(null,
                nextLong(1, grantedServiceUnit.getVolume()),
                null,
                time,
                null,
                nextLong(),
                null,
                false,
                null,
                0d,
                0.0,0,0, 0,0,-1,grantedServiceUnit.getFupLevel());
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
                .withDailyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .withWeeklyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(6))
                .withBillingCycleResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .build();
    }

    private NonMonetoryBalance createNonMonetoryBalance(NonMonetoryBalance subscriberNonMonitoryBalance,
                                                        long volume,
                                                        long time,
                                                        UserPackage userPackage) {

        RnCQuotaProfileDetail quotaProfileDetail = getQuotaProfileDetail(userPackage);

        NonMonetoryBalance copy = subscriberNonMonitoryBalance.copy();
        PulseCalculator volumePulseCalculator = quotaProfileDetail.getVolumePulseCalculator();
        long deductableVolumePulse = volumePulseCalculator.ceil(volume);
        long deductableVolume = volumePulseCalculator.multiply(deductableVolumePulse);
        PulseCalculator timePulseCalculator = quotaProfileDetail.getTimePulseCalculator();
        long deductedTimePulse = timePulseCalculator.ceil(time);
        long deductedTime = timePulseCalculator.multiply(deductedTimePulse);
        copy.substractBillingCycle(deductableVolume, deductedTime);
        copy.addWeekly(deductableVolume, deductedTime);
        copy.addDaily(deductableVolume, deductedTime);

        return copy;

    }

    private NonMonetoryBalance createDeductedNonMonetoryBalance(NonMonetoryBalance subscriberNonMonitoryBalance,
                                                                long volume,
                                                                long time,
                                                                UserPackage userPackage) {

        RnCQuotaProfileDetail quotaProfileDetail = getQuotaProfileDetail(userPackage);

        NonMonetoryBalance copyOfNonMonetaryBalance = subscriberNonMonitoryBalance.copy();
        PulseCalculator volumePulseCalculator = quotaProfileDetail.getVolumePulseCalculator();
        long deductedVolumePulse = volumePulseCalculator.ceil(volume);
        long deductedVolume = volumePulseCalculator.multiply(deductedVolumePulse);
        PulseCalculator timePulseCalculator = quotaProfileDetail.getTimePulseCalculator();
        long deductedTimePulse = timePulseCalculator.ceil(time);
        long deductedTime = timePulseCalculator.multiply(deductedTimePulse);


        copyOfNonMonetaryBalance.setBillingCycleAvailableVolume(deductedVolume);
        copyOfNonMonetaryBalance.setBillingCycleAvailableTime(deductedTime);
        copyOfNonMonetaryBalance.setWeeklyTime(deductedTime);
        copyOfNonMonetaryBalance.setWeeklyVolume(deductedVolume);
        copyOfNonMonetaryBalance.setDailyTime(deductedTime);
        copyOfNonMonetaryBalance.setDailyVolume(deductedVolume);

        return copyOfNonMonetaryBalance;

    }

    private MonetaryBalance createMonetaryBalance(MonetaryBalance monetaryBalance,
                                                  MSCC grantedMSCC,
                                                  NonMonetoryBalance deductedNonMonetoryBalance,
                                                  MockBasePackage mockBasePackage) {
        RnCQuotaProfileDetail quotaProfileDetail = getQuotaProfileDetail(mockBasePackage);
        MonetaryBalance copy = monetaryBalance.copy();
        long calculatedVolumePulse = quotaProfileDetail.getVolumePulseCalculator().ceil(deductedNonMonetoryBalance.getBillingCycleAvailableVolume());
        long calculatedTimePulse = quotaProfileDetail.getTimePulseCalculator().ceil(deductedNonMonetoryBalance.getBillingCycleAvailableTime());
        double deductedMonetaryBalance = quotaProfileDetail.getRateCalculator().calculate(calculatedVolumePulse, calculatedTimePulse);
        copy.substract(deductedMonetaryBalance);
        copy.substractReservation(grantedMSCC.getGrantedServiceUnits().getReservedMonetaryBalance());

        return copy;
    }

    private MonetaryBalance createDeductedMonetaryBalance(MonetaryBalance monetaryBalance,
                                                          MSCC grantedMSCC,
                                                          NonMonetoryBalance deductedNonMonetoryBalance,
                                                          MockBasePackage mockBasePackage) {
        RnCQuotaProfileDetail quotaProfileDetail = getQuotaProfileDetail(mockBasePackage);
        MonetaryBalance copy = monetaryBalance.copy();
        long calculatedVolumePulse = quotaProfileDetail.getVolumePulseCalculator().ceil(deductedNonMonetoryBalance.getBillingCycleAvailableVolume());
        long calculatedTimePulse = quotaProfileDetail.getTimePulseCalculator().ceil(deductedNonMonetoryBalance.getBillingCycleAvailableTime());
        double deductedMonetaryBalance = quotaProfileDetail.getRateCalculator().calculate(calculatedVolumePulse, calculatedTimePulse);
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
                , BalanceLevel.HSQ
                ,2, RenewalIntervalUnit.MONTH, QuotaProfileType.RnC_BASED,
                asList(newLinkedHashMap(newEntry(rnCQuotaProfileDetail.getServiceId(), rnCQuotaProfileDetail))), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());

        mockBasePackage.mockQuotaProfie(quotaProfile);

        return mockBasePackage;
    }
}
