package com.elitecore.netvertex.pm.rnc.ratecard;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pm.rnc.ratecard.MonetaryRateCardVeresionDetail;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateSlab;
import com.elitecore.corenetvertex.spr.BalanceProvider;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.RoPolicyContextImpl;
import com.elitecore.netvertex.pm.quota.VoiceSliceConfiguration;
import com.elitecore.netvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RateCardVersionTest {

    public static final String INR = "INR";
    private RateCardVersion rateCardVersion;
    private PCRFRequest request;
    private PCRFResponse response;
    private ExecutionContext executionContext;
    private RoPolicyContextImpl roPolicyContext;
    private RateSlab rateSlab;
    private MonetaryBalance monetaryBalance;
    private MonetaryRateCardVeresionDetail monetaryRateCardVeresionDetail;
    private SubscriberMonetaryBalance subscriberMonetaryBalance;
    private static VoiceSliceConfiguration voiceSliceConfiguration = new VoiceSliceConfiguration();
    private static final double DELTA = 1e-15;


    @Before
    public void setUp(){

        rateSlab = new RateSlab(0, 1, BigDecimal.ONE, Uom.MINUTE, Uom.MINUTE, null, 50, "");
        monetaryRateCardVeresionDetail = new MonetaryRateCardVeresionDetail(null, "12345",
                "67890", null, null, Arrays.asList(rateSlab), null, null, null, null, null, null, null, null);
        rateCardVersion = new RateCardVersion(null, null, Arrays.asList(monetaryRateCardVeresionDetail), null, null, null, null, null, null);

        request = new PCRFRequestImpl();
        response = new PCRFResponseImpl();

        SPRInfoImpl sprInfo = (SPRInfoImpl) new SPRInfoImpl.SPRInfoBuilder()
                .withSubscriberIdentity("test")
                .build();

        sprInfo.setBalanceProvider(mock(BalanceProvider.class));
        request.setSPRInfo(sprInfo);
        GyServiceUnits requestedServiceUnit = new GyServiceUnits();
        requestedServiceUnit.setServiceSpecificUnits(10);
        MSCC reportedMSCC = new MSCC();
        reportedMSCC.setRequestedServiceUnits(requestedServiceUnit);
        request.setReportedMSCCs(Arrays.asList(reportedMSCC));
        request.setAttribute(PCRFKeyConstants.REQUESTED_ACTION.val, PCRFKeyValueConstants.REQUESTED_ACTION_DIRECT_DEBITING.val);
        response.setAttribute("CS.CalledStationId", "12345");
        response.setAttribute("CS.CallingStationId", "67890");
        executionContext = new ExecutionContext(request, response, mock(CacheAwareDDFTable.class), INR);

        subscriberMonetaryBalance = new SubscriberMonetaryBalance(new FixedTimeSource(System.currentTimeMillis()));
        monetaryBalance = createMonetaryBalance("test");
        subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);
        response.setCurrentMonetaryBalance(subscriberMonetaryBalance);

        RnCPackage rnCPackage = mock(RnCPackage.class);
        when(rnCPackage.getChargingType()).thenReturn(ChargingType.SESSION);
        roPolicyContext = new RoPolicyContextImpl(request, response, rnCPackage, executionContext, null, null);
    }

    @Test
    public void rateCardShouldBeAppliedSuccessfully(){
        Assert.assertTrue(rateCardVersion.apply(roPolicyContext, roPolicyContext.getReservations(), null, null, null));
        Assert.assertNotNull(roPolicyContext.getReservations().get());
        assertEquals(calculateGrantedTime(), roPolicyContext.getGrantedAllMSCC().get().iterator().next().getValue().getGrantedServiceUnits().getTime());
    }

    @Test
    public void rateCardShouldBeAppliedSuccessfullyForDeductedLessThanAvailable(){
        monetaryBalance.setAvailBalance(5);
        Assert.assertTrue(rateCardVersion.apply(roPolicyContext, roPolicyContext.getReservations(), null, "CS.CalledStationId", "CS.CallingStationId"));
        Assert.assertNotNull(roPolicyContext.getReservations().get());

        double actualRate = roPolicyContext.getGrantedAllMSCC().get().iterator().next().getValue().getGrantedServiceUnits().getActualRate();
        assertEquals(rateSlab.getRate().doubleValue(), actualRate, DELTA);

        double expectedDiscountedRate = rateSlab.getRate().multiply(BigDecimal.valueOf(rateSlab.getDiscount())).divide(BigDecimal.valueOf(100)).doubleValue();
        double actualDiscountedRate = roPolicyContext.getGrantedAllMSCC().get().iterator().next().getValue().getGrantedServiceUnits().getRate();
        assertEquals(expectedDiscountedRate,
                actualDiscountedRate, DELTA);
    }

    @Test
    public void rateCardShouldBeAppliedSuccessfullyForDeductedLessThanAvailableCredit(){
        monetaryBalance.setAvailBalance(0);
        monetaryBalance.setCreditLimit(5);
        Assert.assertTrue(rateCardVersion.apply(roPolicyContext, roPolicyContext.getReservations(), null, "CS.CalledStationId", "CS.CallingStationId"));
        Assert.assertNotNull(roPolicyContext.getReservations().get());

        double actualRate = roPolicyContext.getGrantedAllMSCC().get().iterator().next().getValue().getGrantedServiceUnits().getActualRate();
        assertEquals(rateSlab.getRate().doubleValue(), actualRate, DELTA);

        double expectedDiscountedRate = rateSlab.getRate().multiply(BigDecimal.valueOf(rateSlab.getDiscount())).divide(BigDecimal.valueOf(100)).doubleValue();
        double actualDiscountedRate = roPolicyContext.getGrantedAllMSCC().get().iterator().next().getValue().getGrantedServiceUnits().getRate();
        assertEquals(expectedDiscountedRate,
                actualDiscountedRate, DELTA);
    }

    @Test
    public void rateCardShouldBeAppliedSuccessfullyIfDiscountIsNotSpecified(){
        RateSlab rateSlab = new RateSlab(0, 1, BigDecimal.ONE, Uom.MINUTE, Uom.MINUTE, null, null, "");
        MonetaryRateCardVeresionDetail monetaryRateCardVeresionDetail = new MonetaryRateCardVeresionDetail(null, "12345",
                "67890", null, null, Arrays.asList(rateSlab), null, null, null, null, null, null, null, null);
        RateCardVersion rateCardVersion = new RateCardVersion(null, null, Arrays.asList(monetaryRateCardVeresionDetail), null, null, null, null, null, null);
        monetaryBalance.setAvailBalance(5);

        Assert.assertTrue(rateCardVersion.apply(roPolicyContext, roPolicyContext.getReservations(), null, "CS.CalledStationId", "CS.CallingStationId"));
        Assert.assertNotNull(roPolicyContext.getReservations().get());

        double actualRate = roPolicyContext.getGrantedAllMSCC().get().iterator().next().getValue().getGrantedServiceUnits().getRate();
        double expectedRate = rateSlab.getRate().doubleValue();
        Assert.assertEquals(expectedRate, actualRate, DELTA);
    }

    @Test
    public void rateCardShouldBeAppliedSuccessfullyForDeductedEqualThanAvailable(){
        monetaryBalance.setAvailBalance(10);
        Assert.assertTrue(rateCardVersion.apply(roPolicyContext, roPolicyContext.getReservations(), null, null, null));
        Assert.assertNotNull(roPolicyContext.getReservations().get());
        assertEquals((long)monetaryBalance.getAvailBalance() * rateSlab.getPulseMinorUnit() , roPolicyContext.getGrantedAllMSCC().get().iterator().next().getValue().getGrantedServiceUnits().getTime());
    }

    @Test
    public void rateCardShouldBeAppliedSuccessfullyForDeductedEqualThanAvailableCredit(){
        monetaryBalance.setAvailBalance(0);
        monetaryBalance.setCreditLimit(10);
        Assert.assertTrue(rateCardVersion.apply(roPolicyContext, roPolicyContext.getReservations(), null, null, null));
        Assert.assertNotNull(roPolicyContext.getReservations().get());
        assertEquals((long)monetaryBalance.getUsableBalance() * rateSlab.getPulseMinorUnit() , roPolicyContext.getGrantedAllMSCC().get().iterator().next().getValue().getGrantedServiceUnits().getTime());
    }

    @Test
    public void rateCardRejectedIfRatingBehaviorNotFound(){
        RateCardVersion rateCardVersion = new RateCardVersion(null, null, null, null, null, null, null, null, null);
        Assert.assertFalse(rateCardVersion.apply(roPolicyContext, roPolicyContext.getReservations(), null, null, null));

    }

    @Test
    public void rateCardAplliedIfRatingBehaviorFound(){
        Assert.assertTrue(rateCardVersion.apply(roPolicyContext, roPolicyContext.getReservations(), null, "CS.CalledStationId", "CS.CallingStationId"));

    }

    @Test
    public void rateCardRejectedIfRateSlabNotFound(){
        MonetaryRateCardVeresionDetail monetaryRateCardVeresionDetail = new MonetaryRateCardVeresionDetail(null, null,
                null, null, null, null, null, null, null, null, null, null, null, null);
        RateCardVersion rateCardVersion = new RateCardVersion(null, null, Arrays.asList(monetaryRateCardVeresionDetail), null, null, null, null, null, null);
        Assert.assertFalse(rateCardVersion.apply(roPolicyContext, roPolicyContext.getReservations(), null, null, null));

    }

    @Test
    public void rateCardRejectedIfMonetaryBalanceIsZero(){
        monetaryBalance.setAvailBalance(0);
        Assert.assertFalse(rateCardVersion.apply(roPolicyContext, roPolicyContext.getReservations(), null, null, null));
    }

    @Test
    public void rateCardRejectedIfCreditIsExhausted(){
        monetaryBalance.setAvailBalance(-100);
        monetaryBalance.setCreditLimit(100);
        Assert.assertFalse(rateCardVersion.apply(roPolicyContext, roPolicyContext.getReservations(), null, null, null));
    }

    @Test
    public void rateCardRejectedIfMonetaryBalanceIsLessThanSinglePulse(){
        monetaryBalance.setAvailBalance(0.5);
        Assert.assertFalse(rateCardVersion.apply(roPolicyContext, roPolicyContext.getReservations(), null, "12345", "67890"));
    }

    @Test
    public void rateCardRejectedIfUsableMonetaryBalanceIsLessThanSinglePulse(){
        monetaryBalance.setAvailBalance(-50.5);
        monetaryBalance.setCreditLimit(51);
        Assert.assertFalse(rateCardVersion.apply(roPolicyContext, roPolicyContext.getReservations(), null, "12345", "67890"));
    }

    @Test
    public void rateCardShouldBeAppliedSuccessfullyWhenChargingTypeIsEvent(){
        setUpForEventBasedCharging();
        Assert.assertTrue(rateCardVersion.apply(roPolicyContext, roPolicyContext.getReservations(), null, null, null));
        Assert.assertNotNull(roPolicyContext.getReservations().get());
        assertEquals(request.getReportedMSCCs().get(0).getRequestedServiceUnits().getServiceSpecificUnits(), roPolicyContext.getGrantedAllMSCC().get().iterator().next().getValue().getGrantedServiceUnits().getServiceSpecificUnits());
    }

    private void setUpForEventBasedCharging() {
        RnCPackage rnCPackage = mock(RnCPackage.class);
        when(rnCPackage.getChargingType()).thenReturn(ChargingType.EVENT);
        roPolicyContext = new RoPolicyContextImpl(request, response, rnCPackage, executionContext, null, null);
    }

    @Test
    public void discountedRateShouldBeAppliedWhenChargingTypeIsEvent(){
        monetaryBalance.setAvailBalance(5);
        setUpForEventBasedCharging();
        Assert.assertTrue(rateCardVersion.apply(roPolicyContext, roPolicyContext.getReservations(), null, "CS.CalledStationId", "CS.CallingStationId"));
        Assert.assertNotNull(roPolicyContext.getReservations().get());

        double actualRate = roPolicyContext.getGrantedAllMSCC().get().iterator().next().getValue().getGrantedServiceUnits().getActualRate();
        assertEquals(rateSlab.getRate().doubleValue(), actualRate, DELTA);

        double expectedDiscountedRate = rateSlab.getRate().multiply(BigDecimal.valueOf(rateSlab.getDiscount())).divide(BigDecimal.valueOf(100)).doubleValue();
        double actualDiscountedRate = roPolicyContext.getGrantedAllMSCC().get().iterator().next().getValue().getGrantedServiceUnits().getRate();
        assertEquals(expectedDiscountedRate,
                actualDiscountedRate, DELTA);
    }

    @Test
    public void rateCardShouldNotBeAppliedWhenAvailableBalanceIsLessThanReportedEventCharging(){
        setUpForEventBasedCharging();
        monetaryBalance.setAvailBalance(1);
        Assert.assertFalse(rateCardVersion.apply(roPolicyContext, roPolicyContext.getReservations(), null, null, null));
    }

    private long calculateGrantedTime() {
        return rateSlab.getPulseMinorUnit() * voiceSliceConfiguration.getSlicePulse();
    }

    private MonetaryBalance createMonetaryBalance(String subscriberId) {


        return new MonetaryBalance(UUID.randomUUID().toString(),
                subscriberId,
                null,
                1000,
                1000,
                0,0,
                0,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString(),null,
                System.currentTimeMillis(),0,"","");
    }
}
