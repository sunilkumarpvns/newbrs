package com.elitecore.netvertex.pm.rnc.ratecard;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.spr.BalanceProvider;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
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

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class NonMonetaryRateCardTest {

    public static final String INR = "INR";
    private NonMonetaryRateCard nonMonetaryRateCard;
    private PCRFRequest request;
    private PCRFResponse response;
    private ExecutionContext executionContext;
    private RoPolicyContextImpl roPolicyContext;
    private SubscriberRnCNonMonetaryBalance subscriberRnCNonMonetaryBalance;
    private com.elitecore.corenetvertex.pm.rnc.ratecard.NonMonetaryRateCard coreNonMonetaryRateCard;
    private static final String balanceId = UUID.randomUUID().toString();
    private static final String rateCardId = UUID.randomUUID().toString();
    private static final String packageId = UUID.randomUUID().toString();
    private static VoiceSliceConfiguration voiceSliceConfiguration = new VoiceSliceConfiguration();
    private Subscription subscription;

    @Before
    public void setUp(){
        coreNonMonetaryRateCard = new com.elitecore.corenetvertex.pm.rnc.ratecard.NonMonetaryRateCard(
                rateCardId,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                Uom.MINUTE,
                1000,
                60,
                0,
                Uom.MINUTE,
                60,
                60,
                packageId,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), 1,RenewalIntervalUnit.MONTH, CommonStatusValues.DISABLE.isBooleanValue()
        );


        nonMonetaryRateCard = new NonMonetaryRateCard(rateCardId,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                Uom.MINUTE,
                1000,
                60,
                0,
                Uom.MINUTE,
                60,
                60,
                packageId,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),1,RenewalIntervalUnit.MONTH,CommonStatusValues.DISABLE.isBooleanValue());
        request = new PCRFRequestImpl();
        response = spy(new PCRFResponseImpl());

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
        executionContext = spy(new ExecutionContext(request, response, mock(CacheAwareDDFTable.class), INR));
        subscriberRnCNonMonetaryBalance = new SubscriberRnCNonMonetaryBalance(Arrays.asList(createNonMonetaryBalance()));
        response.setCurrentRnCNonMonetaryBalance(subscriberRnCNonMonetaryBalance);

        RnCPackage rnCPackage = mock(RnCPackage.class);
        when(rnCPackage.getChargingType()).thenReturn(ChargingType.SESSION);

        subscription = new Subscription.SubscriptionBuilder().withPackageId(packageId).withSubscriberIdentity("test").build();
        roPolicyContext = new RoPolicyContextImpl(request, response, rnCPackage, executionContext, null, null);
    }

    @Test
    public void nonMonetaryRateCardShouldBeAppliedSuccessfully(){
        Assert.assertTrue(nonMonetaryRateCard.apply(roPolicyContext, roPolicyContext.getReservations(), null));
        Assert.assertNotNull(roPolicyContext.getReservations().get());
        Assert.assertEquals(calculateGrantedTime(), roPolicyContext.getGrantedAllMSCC().get().iterator().next().getValue().getGrantedServiceUnits().getTime());
    }

    @Test
    public void nonMonetaryRateCardShouldNotBeAppliedWhenBalanceNotFound() throws OperationFailedException {
        when(executionContext.getCurrentRnCNonMonetaryBalance()).thenReturn(new SubscriberRnCNonMonetaryBalance(Arrays.asList(createNotFoundNonMonetaryBalance())));
        Assert.assertFalse(nonMonetaryRateCard.apply(roPolicyContext, roPolicyContext.getReservations(), null));
    }

    @Test
    public void nonMonetaryRateCardShouldBeAppliedSuccessfullyWhenAvailableIsLessThanSliceValue(){
        subscriberRnCNonMonetaryBalance.getBalanceById(balanceId).setBillingCycleAvailable(300);
        Assert.assertTrue(nonMonetaryRateCard.apply(roPolicyContext, roPolicyContext.getReservations(), null));
        Assert.assertNotNull(roPolicyContext.getReservations().get());
        Assert.assertEquals(299, roPolicyContext.getGrantedAllMSCC().get().iterator().next().getValue().getGrantedServiceUnits().getTime());
        Assert.assertTrue(roPolicyContext.getGrantedAllMSCC().get().iterator().next().getValue().getGrantedServiceUnits().isNonMonetaryReservationRequired());
        Assert.assertNotNull(roPolicyContext.getGrantedAllMSCC().get().iterator().next().getValue().getFinalUnitIndiacation());
    }

    @Test
    public void nonMonetaryRateCardShouldBeAppliedSuccessfullyWhenChargingTypeIsEvent(){
        setUpForEventBasedCharging();
        Assert.assertTrue(nonMonetaryRateCard.apply(roPolicyContext, roPolicyContext.getReservations(), null));
        Assert.assertNotNull(roPolicyContext.getReservations().get());
        Assert.assertEquals(request.getReportedMSCCs().get(0).getRequestedServiceUnits().getServiceSpecificUnits(), roPolicyContext.getGrantedAllMSCC().get().iterator().next().getValue().getGrantedServiceUnits().getServiceSpecificUnits());
    }

    @Test
    public void nonMonetaryRateCardShouldNotBeAppliedWhenBalanceNotFoundAndChargingTypeIsEvent() throws OperationFailedException {
        setUpForEventBasedCharging();
        when(executionContext.getCurrentRnCNonMonetaryBalance()).thenReturn(new SubscriberRnCNonMonetaryBalance(Arrays.asList(createNotFoundNonMonetaryBalance())));
        Assert.assertFalse(nonMonetaryRateCard.apply(roPolicyContext, roPolicyContext.getReservations(), null));
    }

    @Test
    public void nonMonetaryRateCardShouldNotBeAppliedIfAvailableBalanceIsLessThanReported(){
        setUpForEventBasedCharging();
        subscriberRnCNonMonetaryBalance.getBalanceById(balanceId).setBillingCycleAvailable(5);
        Assert.assertFalse(nonMonetaryRateCard.apply(roPolicyContext, roPolicyContext.getReservations(), null));
    }

    @Test
    public void nonMonetaryRateCardShouldNotBeAppliedIfGetRnCBalanceThrowsOperationFailedException() throws OperationFailedException {
        when(executionContext.getCurrentRnCNonMonetaryBalance()).thenThrow(OperationFailedException.class);
        Assert.assertFalse(nonMonetaryRateCard.apply(roPolicyContext, roPolicyContext.getReservations(), subscription));
    }

    private void setUpForEventBasedCharging() {
        RnCPackage rnCPackage = mock(RnCPackage.class);
        when(rnCPackage.getChargingType()).thenReturn(ChargingType.EVENT);
        roPolicyContext = new RoPolicyContextImpl(request, response, rnCPackage, executionContext, null, null);
    }

    private RnCNonMonetaryBalance createNonMonetaryBalance(){
        Random random = new Random();
        return new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(balanceId, packageId, UUID.randomUUID().toString(),UUID.randomUUID().toString(), null, rateCardId, ResetBalanceStatus.RESET, "1:MONTH", ChargingType.SESSION).withBillingCycleResetTime(CommonConstants.QUOTA_UNLIMITED).withBillingCycleTimeBalance(1000,1000).withDailyUsage(random.nextInt()).withDailyResetTime(1).withWeeklyUsage(random.nextInt()).withWeeklyResetTime(1).withReservation(1).build();
    }

    private RnCNonMonetaryBalance createNotFoundNonMonetaryBalance(){
        Random random = new Random();
        return new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(balanceId, packageId, UUID.randomUUID().toString(),UUID.randomUUID().toString(), null, UUID.randomUUID().toString(), ResetBalanceStatus.RESET, "1:MONTH", ChargingType.SESSION).withBillingCycleResetTime(CommonConstants.QUOTA_UNLIMITED).withBillingCycleTimeBalance(1000,1000).withDailyUsage(random.nextInt()).withDailyResetTime(1).withWeeklyUsage(random.nextInt()).withWeeklyResetTime(1).withReservation(1).build();
    }

    private long calculateGrantedTime() {
        return coreNonMonetaryRateCard.getPulseMinorUnit() * voiceSliceConfiguration.getSlicePulse();
    }

}
