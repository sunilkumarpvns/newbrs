package com.elitecore.netvertex.pm.qos.rnc.ratecard;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.FlatSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail;
import com.elitecore.corenetvertex.pm.sliceconfig.DataSliceConfiguration;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.FinalUnitAction;
import com.elitecore.netvertex.gateway.diameter.gy.FinalUnitIndication;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.PCRFPolicyContextImpl;
import com.elitecore.netvertex.pm.PCRFQoSProcessor;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QoSInformation;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class DataRateCardVersionReservationCalculationTest {

    public static final String INR = "INR";
    private DataRateCardVersion dataRateCardVersion;
    private final String id= "Id";
    private final String rcName = "RC";
    private final String name = "version1";
    private List<VersionDetail> versionDetails;
    private static final String keyOne = "k1";
    private static final String keyTwo = "k2";
    private PolicyContext policyContext;
    private QoSInformation qoSInformation;
    private SubscriberMonetaryBalance subscriberMonetaryBalance;
    private MonetaryBalance monetaryBalance;
    private String packageId= "packageId";
    private Subscription subscription;
    private MockBasePackage basePackage;
    private QuotaReservation quotaReservation;
    private DataSliceConfiguration dataSliceConfiguration = new DataSliceConfiguration();
    private PolicyRepository policyRepository;

    @Before
    public void setUp() throws OperationFailedException {
        quotaReservation = new QuotaReservation();
        basePackage = MockBasePackage.create(UUID.randomUUID().toString(), UUID.randomUUID().toString()+"name");
        versionDetails = new ArrayList<>();
        dataRateCardVersion = new DataRateCardVersion(id, rcName, name, versionDetails);
        PCRFRequest pcrfRequest = new PCRFRequestImpl();
        PCRFResponse pcrfResponse = new PCRFResponseImpl();
        subscription = new Subscription(basePackage.getId(), "test", basePackage.getId(),"productOfferId", null,  null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,
                null, null);
        qoSInformation = new QoSInformation();
        policyContext = spy(new PCRFPolicyContextImpl(pcrfRequest, pcrfResponse,
                new MockBasePackage(),
                new ExecutionContext(pcrfRequest, pcrfResponse, CacheAwareDDFTable.getInstance(), INR),
                new PCRFQoSProcessor(qoSInformation), new DummyPolicyRepository()));

        policyContextReturnsSubscriberMonetaryBalance(100000);

        policyRepository = spy(new DummyPolicyRepository());
        when(policyContext.getPolicyRepository()).thenReturn(policyRepository);
        dataSliceConfiguration = policyRepository.getSliceConfiguration();

    }

    @Test
    public void testMSCChasFinalUnitIndicationWhenAvailMonetaryBalanceIsTobeReservedMonetaryBalance() throws OperationFailedException {

        policyContextReturnsSubscriberMonetaryBalance(10);

        addVersionDetailWithApplicabilityAndSlabNotFree(true, Uom.MB, Uom.MB, new BigDecimal(1), 10);

        assertTrue(dataRateCardVersion.applyReservation(policyContext, keyOne, keyTwo, packageId, subscription, quotaReservation));

        verify(policyContext, times(1)).getCurrentMonetaryBalance();
        FinalUnitIndication expectedFinalUnitIndication = new FinalUnitIndication();
        expectedFinalUnitIndication.setAction(FinalUnitAction.TERMINATE);
        ReflectionAssert.assertReflectionEquals(expectedFinalUnitIndication, quotaReservation.get(CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER).getFinalUnitIndiacation());

    }

    @Test
    public void testMSCCdoesNotHaveFinalUnitIndicationWhenAvailMonetaryBalanceIsNotLessThanTobeReservedMonetaryBalance() throws OperationFailedException {
        policyContextReturnsSubscriberMonetaryBalance(10000);

        addVersionDetailWithApplicabilityAndSlabNotFree(true, Uom.MB, Uom.MB, new BigDecimal(10), 100);

        assertTrue(dataRateCardVersion.applyReservation(policyContext, keyOne, keyTwo, packageId, subscription, quotaReservation));

        verify(policyContext, times(1)).getCurrentMonetaryBalance();

        ReflectionAssert.assertReflectionEquals(null, quotaReservation.get(CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER).getFinalUnitIndiacation());

    }

    @Test
    public void testTimeIsDefaultWhenRateAndPulseUomHaveVolumeUnitType() throws OperationFailedException {
        addVersionDetailWithApplicabilityAndSlabNotFree(true, Uom.BYTE, Uom.BYTE, new BigDecimal(5), 10);

        assertTrue(dataRateCardVersion.applyReservation(policyContext, keyOne, keyTwo, packageId, subscription, quotaReservation));

        verify(policyContext, times(1)).getCurrentMonetaryBalance();

        GyServiceUnits expectedServiceUnits = createExpectedGyServiceUnits(0,
                20000, 100000, 5, 10, 1, 10, 0);

        ReflectionAssert.assertReflectionEquals(expectedServiceUnits, quotaReservation.get(CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER).
                getGrantedServiceUnits());

        MSCC actualMSCC = quotaReservation.get(CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER);
        assertNotNull(actualMSCC);
        assertEquals(0, actualMSCC.getGrantedServiceUnits().getTime());
    }

    @Test
    public void testVolumeIsDefaultWhenRateAndPulseUomHaveTimeUnitType() throws OperationFailedException {
        addVersionDetailWithApplicabilityAndSlabNotFree(true, Uom.SECOND, Uom.SECOND, new BigDecimal(5), 10);

        assertTrue(dataRateCardVersion.applyReservation(policyContext, keyOne, keyTwo, packageId, subscription, quotaReservation));

        verify(policyContext, times(1)).getCurrentMonetaryBalance();

        MSCC actualMSCC = quotaReservation.get(CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER);
        assertNotNull(actualMSCC);
        assertEquals(0, actualMSCC.getGrantedServiceUnits().getVolume());
    }



    public Object[][] dataProviderFor_test_quota_reservation_calcualtion() {


        //rate, pulse, expectedReservedMonetaray, expectedReservedVolume
        return new Object[][] {
                {
                        BigDecimal.valueOf(0.000001), 1, 10.48576, 10485760, 100000
                },
                {
                        BigDecimal.valueOf(0.1), 1, 1000, 10000, 1000
                },
                {
                        BigDecimal.valueOf(1), 1, 100, 100, 100
                },
                {
                        BigDecimal.valueOf(1), 100000, 100000, 100000, 100000
                },
        };
    }


    @Test
    @Parameters(method = "dataProviderFor_test_quota_reservation_calcualtion")
    public void testQuotaReservationCalcualtion(BigDecimal rate, long pulse, double expectedReservedMoney, long expectedReservedVolume,
                                                long availableBalance) throws OperationFailedException {

        policyContextReturnsSubscriberMonetaryBalance(availableBalance);

        addVersionDetailWithApplicabilityAndSlabNotFree(true, Uom.BYTE, Uom.BYTE, rate, pulse);

        assertTrue(dataRateCardVersion.applyReservation(policyContext, keyOne, keyTwo, packageId, subscription, quotaReservation));

        GyServiceUnits expectedAllocatedServiceUnits = createExpectedGyServiceUnits(0, expectedReservedVolume, expectedReservedMoney, rate.doubleValue(),
                pulse, 1, pulse, 0);


        ReflectionAssert.assertReflectionEquals(expectedAllocatedServiceUnits, quotaReservation.get(CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER).
                getGrantedServiceUnits());

    }

    @Test
    public void testDefaultValidityTimeIsSetInCreatedMSCC() throws OperationFailedException {
        addVersionDetailWithApplicabilityAndSlabNotFree(true, Uom.BYTE, Uom.BYTE, new BigDecimal(5), 10);

        assertTrue(dataRateCardVersion.applyReservation(policyContext, keyOne, keyTwo, packageId, subscription, quotaReservation));

        verify(policyContext, times(1)).getCurrentMonetaryBalance();
        FinalUnitIndication finalUnitIndication = new FinalUnitIndication();
        finalUnitIndication.setAction(FinalUnitAction.TERMINATE);

        DataSliceConfiguration dataSliceConfiguration = new DataSliceConfiguration();

        assertEquals(quotaReservation.get(CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER).
                getValidityTime(), dataSliceConfiguration.getMinValidityTime());

    }


    private void addVersionDetailWithApplicabilityAndSlabNotFree(boolean isApplicable, Uom pulseUom, Uom rateUom, BigDecimal rate, long pulse) {
        com.elitecore.netvertex.pm.qos.rnc.ratecard.VersionDetail versionDetail = createVersionDetailWithApplicability(isApplicable);
        FlatSlab flatSlab = createNonFreeSlab(pulseUom, rateUom, rate, pulse);
        doReturn(Arrays.asList(flatSlab)).when(versionDetail).getSlabs();
        versionDetails.add(versionDetail);
    }

    private com.elitecore.netvertex.pm.qos.rnc.ratecard.VersionDetail createVersionDetailWithApplicability(boolean isApplicable) {
        com.elitecore.netvertex.pm.qos.rnc.ratecard.VersionDetail versionDetail = mock(com.elitecore.netvertex.pm.qos.rnc.ratecard.VersionDetail.class);
        doReturn(isApplicable).when(versionDetail).isApplicable(any(), any(), any());
        return versionDetail;
    }

    private FlatSlab createNonFreeSlab(Uom pulseUom, Uom rateUom, BigDecimal rate, long pulse) {
        FlatSlab flatSlab = new FlatSlab(1l, pulse, rate, pulseUom, rateUom);
        return flatSlab;
    }

    private MonetaryBalance createDataMonetaryBalance(long availableBalance) {
        return new MonetaryBalance("id",
                "subscriberId",
                CommonConstants.MONEY_DATA_SERVICE,
                availableBalance,
                100,
                0,0,
                0,
                0,
                0,
                "INR",null,
                0,
                0,
                null,
                null);
    }

    private void policyContextReturnsSubscriberMonetaryBalance(long availbaleBalnace) throws OperationFailedException {
        subscriberMonetaryBalance = new SubscriberMonetaryBalance(new FixedTimeSource(System.currentTimeMillis()));


        monetaryBalance = createDataMonetaryBalance(availbaleBalnace);
        subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
        doReturn(subscriberMonetaryBalance).when(policyContext).getCurrentMonetaryBalance();
    }

    private GyServiceUnits createExpectedGyServiceUnits(long time, long volume, double reservedMonetaryBalance, double rate, long volumePulse,
                                                        long rateMinorUnit, long pulseMinorUnit, long timePulse) {
        GyServiceUnits allocatedServiceUnits = new GyServiceUnits();
        allocatedServiceUnits.setTime(time);
        allocatedServiceUnits.setVolume(volume);
        allocatedServiceUnits.setQuotaProfileIdOrRateCardId("Id");
        allocatedServiceUnits.setProductOfferId("productOfferId");
        allocatedServiceUnits.setRateCardId(id);
        allocatedServiceUnits.setRateCardName(rcName);
        allocatedServiceUnits.setPackageId(packageId);
        allocatedServiceUnits.setSubscriptionId(subscription.getId());
        allocatedServiceUnits.setMonetaryBalanceId(monetaryBalance.getId());
        allocatedServiceUnits.setPulseMinorUnit(pulseMinorUnit);
        allocatedServiceUnits.setRateMinorUnit(rateMinorUnit);
        allocatedServiceUnits.setVolumePulse(volumePulse);
        allocatedServiceUnits.setReservedMonetaryBalance(reservedMonetaryBalance);
        allocatedServiceUnits.setRate(rate);
        allocatedServiceUnits.setTimePulse(timePulse);
        return allocatedServiceUnits;
    }



}

