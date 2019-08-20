package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.ddf.MonetaryBalanceOperation;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.rnc.ABMFReservationOperationHandler;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class ABMFReservationOperationHandlerTest {
    public static final String INR = "INR";
    private ExecutionContext executionContext;
    private ABMFReservationOperationHandler postProcessHandler;
    private QuotaReservation quotaReservation;
    private GyServiceUnits gyServiceUnits;
    private SubscriberMonetaryBalance subscriberMonetaryBalance;
    private SubscriberNonMonitoryBalance subscriberNonMonitoryBalance;
    private long currentTime = System.currentTimeMillis();
    private String id = UUID.randomUUID().toString();
    private String subscriberId = UUID.randomUUID().toString();
    private PCRFResponse pcrfResponse;
    private PCRFRequest pcrfRequest;
    private MonetaryBalance monetaryBalance;
    private NonMonetoryBalance nonMonetoryBalance;
    private MockBasePackage basePackage;

    @Mock private MonetaryBalanceOperation monetaryBalanceOperation;
    @Mock private CacheAwareDDFTable ddfTable;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        postProcessHandler = new ABMFReservationOperationHandler();

        PCRFRequest pcrfRequest = new PCRFRequestImpl();
        pcrfRequest.setPCRFEvents(EnumSet.of(PCRFEvent.USAGE_REPORT));
        pcrfResponse = spy(new PCRFResponseImpl());
        pcrfRequest = spy(new PCRFRequestImpl());

        when(ddfTable.getMonetaryBalanceOp()).thenReturn(monetaryBalanceOperation);
        executionContext = spy(new ExecutionContext(pcrfRequest, pcrfResponse, ddfTable, INR));

        gyServiceUnits = new GyServiceUnits();
        gyServiceUnits.setBalanceId(id);
        gyServiceUnits.setMonetaryBalanceId(id);
        gyServiceUnits.setReservationRequired(true);
        gyServiceUnits.setVolume(1);
        gyServiceUnits.setReservedMonetaryBalance(1);

        MSCC mscc = new MSCC();
        mscc.setRatingGroup(0l);
        mscc.setServiceIdentifiers(Arrays.asList(0l));
        mscc.setGrantedServiceUnits(gyServiceUnits);

        quotaReservation = new QuotaReservation();
        quotaReservation.put(mscc);

        monetaryBalance = createMonetaryBalance(id);
        subscriberMonetaryBalance = new SubscriberMonetaryBalance(new FixedTimeSource(System.currentTimeMillis()));
        subscriberMonetaryBalance.addMonitoryBalances(monetaryBalance);

        nonMonetoryBalance = createNonMonetaryBalanace(id, subscriberId);
        subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(Arrays.asList(nonMonetoryBalance));

        basePackage = MockBasePackage.create("test", "test");

        Subscription subscription = new Subscription("test", "test", basePackage.getId(),"productOfferId", null, new Timestamp(System.currentTimeMillis()), null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);
        LinkedHashMap<String, Subscription> subscriptionMap = new LinkedHashMap<>();
        subscriptionMap.put(subscription.getId(), subscription);
        pcrfResponse.setSubscriptions(subscriptionMap);
    }

    @Test
    public void testSkipProcessingWhenQuotaReservationIsNull() {
        pcrfResponse.setQuotaReservation(null);

        postProcessHandler.handle(pcrfRequest, pcrfResponse, executionContext);

        verify(executionContext, never()).getDDFTable();
    }


    @Test
    public void reserveMonetaryBalanceCallSuccessfully() throws OperationFailedException {
        pcrfResponse.setQuotaReservation(quotaReservation);
        pcrfResponse.setCurrentMonetaryBalance(subscriberMonetaryBalance);
        pcrfResponse.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);

        postProcessHandler.handle(pcrfRequest, pcrfResponse, executionContext);

        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(monetaryBalanceOperation, times(1)).reserve(captor.capture());

        List<List> allValues = captor.getAllValues();

        assertThat(allValues.size(), is(1));
        ReflectionAssert.assertLenientEquals(monetaryBalance, allValues.get(0).get(0));

    }

    @Test
    public void currentMonetaryBalanceNotCallWhenMonetaryBalanceIsNotReserve() {
        gyServiceUnits.setReservedMonetaryBalance(0);
        when(pcrfResponse.getQuotaReservation()).thenReturn(quotaReservation);
        pcrfResponse.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);

        postProcessHandler.handle(pcrfRequest, pcrfResponse, executionContext);

        Mockito.verify(pcrfResponse, times(0)).getCurrentMonetaryBalance();

    }

    @Test
    public void reserveBalanceNotBeCallWhenSubscriberMonetaryBalanceIsNull() throws OperationFailedException {
        pcrfResponse.setQuotaReservation(quotaReservation);
        pcrfResponse.setCurrentMonetaryBalance(null);
        pcrfResponse.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);

        postProcessHandler.handle(pcrfRequest, pcrfResponse, executionContext);

        Mockito.verify(monetaryBalanceOperation, times(0)).reserve(Arrays.asList(monetaryBalance));

    }

    @Test
    public void reserveNonMonetaryBalanceCallSuccessfully() throws OperationFailedException {
        pcrfResponse.setQuotaReservation(quotaReservation);
        pcrfResponse.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);

        postProcessHandler.handle(pcrfRequest, pcrfResponse, executionContext);

        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(ddfTable, times(1)).reserveBalance(Matchers.eq(subscriberId), captor.capture());

        List<List> allValues = captor.getAllValues();

        assertThat(allValues.size(), is(1));
        ReflectionAssert.assertLenientEquals(nonMonetoryBalance, allValues.get(0).get(0));

    }

    private MonetaryBalance createMonetaryBalance(String id) {
        return new MonetaryBalance( id,"1", null, 1,1,
                1,0, 0, currentTime, currentTime,"INR", MonetaryBalanceType.DEFAULT.name(), currentTime,0,"","");
    }

    private NonMonetoryBalance createNonMonetaryBalanace(String id, String subscriberId){
        Random random = new Random();
        return new NonMonetoryBalance.NonMonetaryBalanceBuilder(id, RandomUtils.nextInt(0, Integer.MAX_VALUE), UUID.randomUUID().toString(),1l, subscriberId, "test", random.nextInt(2), UUID.randomUUID().toString(), ResetBalanceStatus.RESET, null, null).withBillingCycleVolumeBalance(random.nextInt(),random.nextInt()).withBillingCycleResetTime(CommonConstants.QUOTA_UNLIMITED).withBillingCycleTimeBalance(random.nextInt(),random.nextInt()).withDailyUsage(random.nextInt(),random.nextInt()).withDailyResetTime(1).withWeeklyUsage(random.nextInt(),random.nextInt()).withWeeklyResetTime(1).withReservation(1,0).build();
    }
}
