package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCardVersion;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.FlatRating;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.FlatSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateCardVersion;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.unitils.reflectionassert.ReflectionAssert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class NonFinalReportedRateCardProcessorTest {

    public static final String INR = "INR";
    private ReportHandler reportHandler;
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
        setUpGetCurrency();
        policyRepository = (DummyPolicyRepository) dummyPCRFServiceContext.getServerContext().getPolicyRepository();
        reportHandler = new ReportHandler(dummyPCRFServiceContext, new QuotaReportHandlerFactory(policyRepository));

        response.setQuotaReservation(new QuotaReservation());
        request.setQuotaReservation(new QuotaReservation());
    }

    private void setUpGetCurrency() {
        DummyNetvertexServerContextImpl serverContext = DummyNetvertexServerContextImpl.spy();
        DummyNetvertexServerConfiguration netvertexServerConfiguration = DummyNetvertexServerConfiguration.spy();
        SystemParameterConfiguration systemParameterConfiguration = netvertexServerConfiguration.spySystemParameterConf();
        doReturn("INR").when(systemParameterConfiguration).getSystemCurrency();
        serverContext.setNetvertexServerConfiguration(netvertexServerConfiguration);
        dummyPCRFServiceContext.setServerContext(serverContext);
    }


    public class MonetaryBalances {

        private NonMonetoryBalance nonMonetoryBalance;
        private MonetaryBalance monetaryBalance;
        private MonetaryBalanceOperation monetaryBalanceOperation;
        private MockBasePackage mockBasePackage;

        public class rateOnTime {

            @Before
            public void setUp() {

                mockBasePackage = createPackage(BigDecimal.valueOf(2.0), Uom.SECOND);

                nonMonetoryBalance = createNonMonetaryBalance();

                SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(asList(nonMonetoryBalance));

                response.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);
                SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(fixedTimeSource);
                monetaryBalance = NonFinalReportedRateCardProcessorTest.createMonetaryBalance(request.getSPRInfo().getSubscriberIdentity());
                subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
                response.setCurrentMonetaryBalance(subscriberMonetaryBalance);

                createGrantedAndReportedMSCCForBalance(monetaryBalance);

                monetaryBalanceOperation = mock(MonetaryBalanceOperation.class);
                when(executionContext.getDDFTable().getMonetaryBalanceOp()).thenReturn(monetaryBalanceOperation);

            }


            public class UnAccountedQuotaIsNull {

                @Test
                public void deductMonetaryBalanceFromABMF() {

                    process(mockBasePackage);
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().remove(Long.valueOf(0))
                    ));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromABMF() {
                    process(mockBasePackage);
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().remove(Long.valueOf(0))
                    ));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void deductMonetaryBalanceAccordingToRate() {

                    process(mockBasePackage);
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(Long.valueOf(0))
                    ));

                    SubscriberMonetaryBalance actual = response.getCurrentMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromCurrentBalance() {

                    process(mockBasePackage);
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(Long.valueOf(0))
                    ));

                    SubscriberMonetaryBalance actual = response.getCurrentMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void doesNotCreateAnyUnAccountedUsageWhenSumOfReportedAndUnAccountedQuotaIsDivisibleByPulse() {

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().remove(Long.valueOf(0)).getGrantedServiceUnits();

                    long timeModulo = (usedServiceUnits.getTime()) % grantedServiceUnits.getTimePulse();
                    long time = usedServiceUnits.getTime() + (grantedServiceUnits.getTimePulse() - timeModulo);
                    usedServiceUnits.setTime(time);
                    process(mockBasePackage);
                    Assert.assertNull(response.getUnAccountedQuota());
                }

                @Test
                public void createUnAccountedUsageWhenSumOfReportedAndUnAccountedQuotaIsNotDivisibleByPulse() {

                    MSCC mscc = request.getReportedMSCCs().get(0);
                    GyServiceUnits usedServiceUnits = mscc.getUsedServiceUnits();
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().get(Long.valueOf(0)).getGrantedServiceUnits();
                    QuotaReservation quotaReservation = request.getQuotaReservation();
                    MSCC quotaReservationEntry = quotaReservation.get(mscc.getRatingGroup());
                    long time = usedServiceUnits.getTime() + (grantedServiceUnits.getPulseMinorUnit() - (usedServiceUnits.getTime()) % grantedServiceUnits.getPulseMinorUnit());
                    usedServiceUnits.setTime(time+1);
                    process(mockBasePackage);
                    MSCC expectedUnAccoutedMSCC = quotaReservationEntry.copy();
                    expectedUnAccoutedMSCC.getGrantedServiceUnits().setTime(1);
                    QuotaReservation expectedUnAccountedQuota  = new QuotaReservation();
                    expectedUnAccountedQuota.put(expectedUnAccoutedMSCC);
                    ReflectionAssert.assertReflectionEquals(expectedUnAccountedQuota, response.getUnAccountedQuota());
                }

                @Test
                public void doesNotDeductReportedUsageFromABMFWhenReportedQuotaPlusUnAccountedQuotaIsLowerThanPulse() {
                    MSCC mscc = request.getReportedMSCCs().get(0);
                    GyServiceUnits usedServiceUnits = mscc.getUsedServiceUnits();
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().get(Long.valueOf(0)).getGrantedServiceUnits();
                    usedServiceUnits.setTime(grantedServiceUnits.getPulseMinorUnit()- 1);
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
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().get(Long.valueOf(0)).getGrantedServiceUnits();
                    usedServiceUnits.setTime(grantedServiceUnits.getTimePulse()- 1);
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
                    unAccountedQuota = createGrantedMSCC(monetaryBalance);
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
                    long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                    usedServiceUnits.setTime(time);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().remove(Long.valueOf(0))
                    ));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromABMF() {
                    process(mockBasePackage);
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                    usedServiceUnits.setTime(time);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().remove(Long.valueOf(0))
                    ));
                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void deductMonetaryBalanceAccordingToRate() {

                    process(mockBasePackage);

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();

                    long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                    usedServiceUnits.setTime(time);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(Long.valueOf(0))
                    ));

                    SubscriberMonetaryBalance actual = response.getCurrentMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromCurrentBalance() {
                    process(mockBasePackage);
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long time = usedServiceUnits.getTime() + unAccountedQuota.getGrantedServiceUnits().getTime();
                    usedServiceUnits.setTime(time);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(Long.valueOf(0))
                    ));

                    SubscriberMonetaryBalance actual = response.getCurrentMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void doesNotCreateAnyUnAccountedUsageWhenSumOfReportedAndUnAccountedQuotaIsDivisibleByPulse() {
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().get(Long.valueOf(0)).getGrantedServiceUnits();

                    long timeModulo = (this.unAccountedQuota.getGrantedServiceUnits().getTime() + usedServiceUnits.getTime()) % grantedServiceUnits.getPulseMinorUnit();
                    long time = usedServiceUnits.getTime() + (grantedServiceUnits.getPulseMinorUnit() - timeModulo);
                    usedServiceUnits.setTime(time);
                    process(mockBasePackage);
                    Assert.assertNull(response.getUnAccountedQuota());
                }

                @Test
                public void createUnAccountedUsageWhenSumOfReportedAndUnAccountedQuotaIsNotDivisibleByPulse() {
                    MSCC mscc = request.getReportedMSCCs().get(0);
                    GyServiceUnits usedServiceUnits = mscc.getUsedServiceUnits();
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().get(Long.valueOf(0)).getGrantedServiceUnits();
                    QuotaReservation quotaReservation = request.getQuotaReservation();
                    MSCC quotaReservationEntry = quotaReservation.get(mscc.getRatingGroup());
                    long time = usedServiceUnits.getTime() + (grantedServiceUnits.getPulseMinorUnit() - (this.unAccountedQuota.getGrantedServiceUnits().getTime() + usedServiceUnits.getTime()) % grantedServiceUnits.getPulseMinorUnit());
                    usedServiceUnits.setTime(time+1);
                    process(mockBasePackage);
                    MSCC expectedUnAccoutedMSCC = quotaReservationEntry.copy();
                    expectedUnAccoutedMSCC.getGrantedServiceUnits().setTime(1);
                    QuotaReservation expectedUnAccountedQuota  = new QuotaReservation();
                    expectedUnAccountedQuota.put(expectedUnAccoutedMSCC);
                    ReflectionAssert.assertReflectionEquals(expectedUnAccountedQuota, response.getUnAccountedQuota());
                }

                @Test
                public void removeUnAccountedUsageIfMonetaryBalanceIdAndNonMonetaryBalanceIdMatch() {

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().get(Long.valueOf(0)).getGrantedServiceUnits();

                    long time = usedServiceUnits.getTime() + (grantedServiceUnits.getPulseMinorUnit() - (this.unAccountedQuota.getGrantedServiceUnits().getTime() + usedServiceUnits.getTime()) % grantedServiceUnits.getPulseMinorUnit());
                    usedServiceUnits.setTime(time);
                    process(mockBasePackage);
                    Assert.assertNull(response.getUnAccountedQuota());
                }

                @Test
                public void doesNotDeductReportedUsageFromABMFWhenReportedQuotaPlusUnAccountedQuotaIsLowerThanPulse() {
                    MSCC mscc = request.getReportedMSCCs().get(0);
                    GyServiceUnits usedServiceUnits = mscc.getUsedServiceUnits();
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().get(Long.valueOf(0)).getGrantedServiceUnits();
                    usedServiceUnits.setTime(grantedServiceUnits.getPulseMinorUnit()- unAccountedQuota.getGrantedServiceUnits().getTime() - 1);
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
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().get(Long.valueOf(0)).getGrantedServiceUnits();
                    usedServiceUnits.setTime(grantedServiceUnits.getTimePulse()- unAccountedQuota.getGrantedServiceUnits().getTime() - 1);
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

            private MonetaryBalance createMonetaryBalance(MonetaryBalance monetaryBalance,
                                                          MSCC grantedMSCC) {

                MonetaryBalance copy = monetaryBalance.copy();
                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                GyServiceUnits grantedServiceUnits = grantedMSCC.getGrantedServiceUnits();
                long calculatedTimePulse = grantedServiceUnits.calculateCeilPulse(usedServiceUnits.getTime());
                long deductableTime = grantedServiceUnits.calculateDeductableQuota(calculatedTimePulse);
                BigDecimal deductedMonetaryBalance = grantedServiceUnits.calculateDeductableMoney(deductableTime);
                copy.substract(deductedMonetaryBalance.doubleValue());
                copy.substractReservation(grantedServiceUnits.getReservedMonetaryBalance());

                return copy;
            }

            private MonetaryBalance createDeductedMonetaryBalance(MonetaryBalance monetaryBalance,
                                                                  MSCC grantedMSCC) {
                MonetaryBalance copy = monetaryBalance.copy();

                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                GyServiceUnits grantedServiceUnits = request.getReportedMSCCs().get(0).getGrantedServiceUnits();
                long calculatedTimePulse = grantedServiceUnits.calculateFloorPulse(usedServiceUnits.getTime());
                long deductableTime = grantedServiceUnits.calculateDeductableQuota(calculatedTimePulse);
                BigDecimal deductedMonetaryBalance = grantedServiceUnits.calculateDeductableMoney(deductableTime);
                copy.setAvailBalance(deductedMonetaryBalance.doubleValue());
                copy.setTotalReservation(0);
                copy.substractReservation(grantedMSCC.getGrantedServiceUnits().getReservedMonetaryBalance());
                return copy;
            }
        }

        public class rateOnVolume {

            @Before
            public void setUp() {

                mockBasePackage = createPackage(BigDecimal.valueOf(2.0), Uom.MB);

                nonMonetoryBalance = createNonMonetaryBalance();

                SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(asList(nonMonetoryBalance));

                response.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);
                SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(fixedTimeSource);
                monetaryBalance = NonFinalReportedRateCardProcessorTest.createMonetaryBalance(request.getSPRInfo().getSubscriberIdentity());
                subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
                response.setCurrentMonetaryBalance(subscriberMonetaryBalance);

                createGrantedAndReportedMSCCForBalance(monetaryBalance);

                monetaryBalanceOperation = mock(MonetaryBalanceOperation.class);
                when(executionContext.getDDFTable().getMonetaryBalanceOp()).thenReturn(monetaryBalanceOperation);

            }


            public class UnAccountedQuotaIsNull {

                @Test
                public void deductMonetaryBalanceFromABMF() {

                    process(mockBasePackage);
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().remove(Long.valueOf(0))
                    ));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromABMF() {
                    process(mockBasePackage);
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().remove(Long.valueOf(0))
                    ));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void deductMonetaryBalanceAccordingToRate() {

                    process(mockBasePackage);
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(Long.valueOf(0))
                    ));

                    SubscriberMonetaryBalance actual = response.getCurrentMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromCurrentBalance() {

                    process(mockBasePackage);
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(Long.valueOf(0))
                    ));

                    SubscriberMonetaryBalance actual = response.getCurrentMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void doesNotCreateAnyUnAccountedUsageWhenSumOfReportedAndUnAccountedQuotaIsDivisibleByPulse() {

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().remove(Long.valueOf(0)).getGrantedServiceUnits();

                    long timeModulo = (usedServiceUnits.getTime()) % grantedServiceUnits.getTimePulse();
                    long time = usedServiceUnits.getTime() + (grantedServiceUnits.getTimePulse() - timeModulo);
                    usedServiceUnits.setTime(time);
                    process(mockBasePackage);
                    Assert.assertNull(response.getUnAccountedQuota());
                }

                @Test
                public void createUnAccountedUsageWhenSumOfReportedAndUnAccountedQuotaIsNotDivisibleByPulse() {

                    MSCC mscc = request.getReportedMSCCs().get(0);
                    GyServiceUnits usedServiceUnits = mscc.getUsedServiceUnits();
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().get(Long.valueOf(0)).getGrantedServiceUnits();
                    QuotaReservation quotaReservation = request.getQuotaReservation();
                    MSCC quotaReservationEntry = quotaReservation.get(mscc.getRatingGroup());
                    long volume = usedServiceUnits.getVolume() + (grantedServiceUnits.getPulseMinorUnit() - (usedServiceUnits.getVolume()) % grantedServiceUnits.getPulseMinorUnit());
                    usedServiceUnits.setVolume(volume+1);
                    process(mockBasePackage);
                    MSCC expectedUnAccoutedMSCC = quotaReservationEntry.copy();
                    expectedUnAccoutedMSCC.getGrantedServiceUnits().setVolume(1);
                    QuotaReservation expectedUnAccountedQuota  = new QuotaReservation();
                    expectedUnAccountedQuota.put(expectedUnAccoutedMSCC);
                    ReflectionAssert.assertReflectionEquals(expectedUnAccountedQuota, response.getUnAccountedQuota());
                }

                @Test
                public void doesNotDeductReportedUsageFromABMFWhenReportedQuotaPlusUnAccountedQuotaIsLowerThanPulse() {
                    MSCC mscc = request.getReportedMSCCs().get(0);
                    GyServiceUnits usedServiceUnits = mscc.getUsedServiceUnits();
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().get(Long.valueOf(0)).getGrantedServiceUnits();
                    usedServiceUnits.setVolume(grantedServiceUnits.getPulseMinorUnit()- 1);
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
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().get(Long.valueOf(0)).getGrantedServiceUnits();
                    usedServiceUnits.setTime(grantedServiceUnits.getTimePulse()- 1);
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
                    unAccountedQuota = createGrantedMSCC(monetaryBalance);
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
                    long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();
                    usedServiceUnits.setVolume(volume);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().remove(Long.valueOf(0))
                    ));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromABMF() {
                    process(mockBasePackage);
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();
                    usedServiceUnits.setVolume(volume);

                    expected.addMonitoryBalances(createDeductedMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().remove(Long.valueOf(0))
                    ));

                    SubscriberMonetaryBalance actual = response.getAccountedMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void deductMonetaryBalanceAccordingToRate() {

                    process(mockBasePackage);

                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();

                    long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();
                    usedServiceUnits.setVolume(volume);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(Long.valueOf(0))
                    ));

                    SubscriberMonetaryBalance actual = response.getCurrentMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void removeMonetaryReservationFromCurrentBalance() {
                    process(mockBasePackage);
                    SubscriberMonetaryBalance expected = new SubscriberMonetaryBalance(fixedTimeSource);

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    long volume = usedServiceUnits.getVolume() + unAccountedQuota.getGrantedServiceUnits().getVolume();

                    usedServiceUnits.setVolume(volume);

                    expected.addMonitoryBalances(createMonetaryBalance(monetaryBalance,
                            request.getQuotaReservation().get(Long.valueOf(0))
                    ));

                    SubscriberMonetaryBalance actual = response.getCurrentMonetaryBalance();
                    actual.setCurrentTime(expected.getCurrentTime());
                    ReflectionAssert.assertReflectionEquals(expected, actual);
                }

                @Test
                public void doesNotCreateAnyUnAccountedUsageWhenSumOfReportedAndUnAccountedQuotaIsDivisibleByPulse() {
                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().get(Long.valueOf(0)).getGrantedServiceUnits();

                    long volumeModulo = (this.unAccountedQuota.getGrantedServiceUnits().getVolume() + usedServiceUnits.getVolume()) % grantedServiceUnits.getPulseMinorUnit();
                    long volume = usedServiceUnits.getVolume() + (grantedServiceUnits.getPulseMinorUnit() - volumeModulo);
                    usedServiceUnits.setVolume(volume);
                    process(mockBasePackage);
                    Assert.assertNull(response.getUnAccountedQuota());
                }

                @Test
                public void createUnAccountedUsageWhenSumOfReportedAndUnAccountedQuotaIsNotDivisibleByPulse() {
                    MSCC mscc = request.getReportedMSCCs().get(0);
                    GyServiceUnits usedServiceUnits = mscc.getUsedServiceUnits();
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().get(Long.valueOf(0)).getGrantedServiceUnits();
                    QuotaReservation quotaReservation = request.getQuotaReservation();
                    MSCC quotaReservationEntry = quotaReservation.get(mscc.getRatingGroup());
                    long volume = usedServiceUnits.getVolume() + (grantedServiceUnits.getPulseMinorUnit() - (this.unAccountedQuota.getGrantedServiceUnits().getVolume() + usedServiceUnits.getVolume()) % grantedServiceUnits.getPulseMinorUnit());
                    usedServiceUnits.setVolume(volume+1);
                    process(mockBasePackage);
                    MSCC expectedUnAccoutedMSCC = quotaReservationEntry.copy();
                    expectedUnAccoutedMSCC.getGrantedServiceUnits().setVolume(1);
                    QuotaReservation expectedUnAccountedQuota  = new QuotaReservation();
                    expectedUnAccountedQuota.put(expectedUnAccoutedMSCC);
                    ReflectionAssert.assertReflectionEquals(expectedUnAccountedQuota, response.getUnAccountedQuota());
                }

                @Test
                public void removeUnAccountedUsageIfMonetaryBalanceIdAndNonMonetaryBalanceIdMatch() {

                    GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().get(Long.valueOf(0)).getGrantedServiceUnits();

                    long volume = usedServiceUnits.getVolume() + (grantedServiceUnits.getPulseMinorUnit() - (this.unAccountedQuota.getGrantedServiceUnits().getVolume() + usedServiceUnits.getVolume()) % grantedServiceUnits.getPulseMinorUnit());
                    usedServiceUnits.setVolume(volume);
                    process(mockBasePackage);
                    Assert.assertNull(response.getUnAccountedQuota());
                }

                @Test
                public void doesNotDeductReportedUsageFromABMFWhenReportedQuotaPlusUnAccountedQuotaIsLowerThanPulse() {
                    MSCC mscc = request.getReportedMSCCs().get(0);
                    GyServiceUnits usedServiceUnits = mscc.getUsedServiceUnits();
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().get(Long.valueOf(0)).getGrantedServiceUnits();
                    usedServiceUnits.setVolume(grantedServiceUnits.getPulseMinorUnit()- unAccountedQuota.getGrantedServiceUnits().getVolume() - 1);
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
                    GyServiceUnits grantedServiceUnits = response.getQuotaReservation().get(Long.valueOf(0)).getGrantedServiceUnits();
                    usedServiceUnits.setTime(grantedServiceUnits.getTimePulse()- unAccountedQuota.getGrantedServiceUnits().getTime() - 1);
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

            private MonetaryBalance createMonetaryBalance(MonetaryBalance monetaryBalance,
                                                          MSCC grantedMSCC) {

                MonetaryBalance copy = monetaryBalance.copy();
                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                GyServiceUnits grantedServiceUnits = grantedMSCC.getGrantedServiceUnits();
                long calculatedVolumePulse = grantedServiceUnits.calculateCeilPulse(usedServiceUnits.getVolume());
                long deductableVolume = grantedServiceUnits.calculateDeductableQuota(calculatedVolumePulse);
                BigDecimal deductedMonetaryBalance = grantedServiceUnits.calculateDeductableMoney(deductableVolume);
                copy.substract(deductedMonetaryBalance.doubleValue());
                copy.substractReservation(grantedServiceUnits.getReservedMonetaryBalance());

                return copy;
            }

            private MonetaryBalance createDeductedMonetaryBalance(MonetaryBalance monetaryBalance,
                                                                  MSCC grantedMSCC) {
                MonetaryBalance copy = monetaryBalance.copy();

                GyServiceUnits usedServiceUnits = request.getReportedMSCCs().get(0).getUsedServiceUnits();
                GyServiceUnits grantedServiceUnits = grantedMSCC.getGrantedServiceUnits();
                long calculatedVolumePulse = grantedServiceUnits.calculateFloorPulse(usedServiceUnits.getVolume());
                long deductableVolume = grantedServiceUnits.calculateDeductableQuota(calculatedVolumePulse);
                BigDecimal deductedMonetaryBalance = grantedServiceUnits.calculateDeductableMoney(deductableVolume);
                copy.setAvailBalance(deductedMonetaryBalance.doubleValue());
                copy.setTotalReservation(0);
                copy.substractReservation(grantedServiceUnits.getReservedMonetaryBalance());
                return copy;
            }
        }

        private MockBasePackage createPackage(BigDecimal rate, Uom rateOn) {
            MockBasePackage mockBasePackage = policyRepository.mockBasePackage();
            mockBasePackage.quotaProfileTypeIsRnC();

            mockBasePackage.mockRateCard(createRateCardDetails(rate, rateOn));

            return mockBasePackage;
        }

        private MSCC createGrantedAndReportedMSCCForBalance(MonetaryBalance monetaryBalance) {
            MSCC grantedMSCC = createGrantedMSCC(monetaryBalance);
            response.getQuotaReservation().put(grantedMSCC);
            request.getQuotaReservation().put(grantedMSCC);

            request.setReportedMSCCs(Arrays.asList(grantedMSCC));
            response.setReportedMSCCs(Arrays.asList(grantedMSCC));
            return grantedMSCC;
        }

        private MSCC createGrantedMSCC(MonetaryBalance monetaryBalance) {
            GyServiceUnits grantedServiceUnit = createGSU(monetaryBalance);
            grantedServiceUnit.setTimePulse(nextInt(2, 5));
            grantedServiceUnit.setVolumePulse(nextInt(2, 5));
            grantedServiceUnit.setPulseMinorUnit(nextInt(2, 5));
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
                    monetaryBalance.getTotalReservation(),
                    100.0,
                    nextInt(2, 5),
                    100, nextInt(2, 5), 0,0, nonMonetoryBalance.getLevel());
        }

        private DataRateCard createRateCardDetails(BigDecimal rate, Uom on){
            RateSlab slab = new FlatSlab(20, 10, rate, on, on);
            VersionDetail versionDetail = new FlatRating("Calling-Station-ID", "Calling-Station-ID", Arrays.asList(slab), null);

            RateCardVersion rateCardVersion = new DataRateCardVersion("1", "RC1", "version1", Arrays.asList(versionDetail));

            return new DataRateCard("1", "test", "Calling-Station-ID", "Calling-Station-ID", Arrays.asList(rateCardVersion), on, on);

        }

        private DataRateCard getRateCardDetails(UserPackage userPackage){
            return userPackage.getDataRateCard("1");
        }

        private NonMonetoryBalance createNonMonetaryBalance() {


            return new NonMonetoryBalance.NonMonetaryBalanceBuilder()
                    .build();
        }

        private void process(UserPackage mockBasePackage) {

            PolicyManager policyManager = mock(PolicyManager.class);

            PolicyManager.setInstance(policyManager);
            when(policyManager.getPkgDataById(mockBasePackage.getId())).thenReturn(mockBasePackage);
            ProductOfferStore productOfferStore = mock(ProductOfferStore.class);
            when(policyManager.getProductOffer()).thenReturn(productOfferStore);
            when(productOfferStore.byId(Mockito.anyString())).thenReturn(null);


            getLogger().debug(ReportingReasonNonFinalReportHandlerTest.class.getSimpleName(), "Non-Monetary Balance is:" + response.getCurrentNonMonetoryBalance());
            getLogger().debug(ReportingReasonNonFinalReportHandlerTest.class.getSimpleName(), "Monetary Balance is:" + response.getCurrentMonetaryBalance());
            getLogger().debug(ReportingReasonNonFinalReportHandlerTest.class.getSimpleName(), "Quota Reservation is:" + response.getQuotaReservation());
            getLogger().debug(ReportingReasonNonFinalReportHandlerTest.class.getSimpleName(), "Reported Quota is:" + request.getReportedMSCCs());
            getLogger().debug(ReportingReasonNonFinalReportHandlerTest.class.getSimpleName(), "UnAccounted Quota:" + response.getUnAccountedQuota());
            getLogger().debug(ReportingReasonNonFinalReportHandlerTest.class.getSimpleName(), "Rate card Profile Detail is:" + getRateCardDetails(mockBasePackage));



            reportHandler.process(request, response, executionContext);

            getLogger().debug(ReportingReasonNonFinalReportHandlerTest.class.getSimpleName(), "Non-Monetary Balance after reporting is:" + response.getCurrentNonMonetoryBalance());
            getLogger().debug(ReportingReasonNonFinalReportHandlerTest.class.getSimpleName(), "Monetary Balance after reporting is:" + response.getCurrentMonetaryBalance());
            getLogger().debug(ReportingReasonNonFinalReportHandlerTest.class.getSimpleName(), "Accounted Non-Monetary Balance after reporting is:" + response.getAccountedNonMonetaryBalance());
            getLogger().debug(ReportingReasonNonFinalReportHandlerTest.class.getSimpleName(), "Accounted Monetary Balance after reporting is:" + response.getAccountedMonetaryBalance());
            getLogger().debug(ReportingReasonNonFinalReportHandlerTest.class.getSimpleName(), "UnAccounted Quota after reporting is:" + response.getUnAccountedQuota());
        }
    }

    private static MonetaryBalance createMonetaryBalance(String subscriberId) {
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
                0
                ,"","");
    }
}
