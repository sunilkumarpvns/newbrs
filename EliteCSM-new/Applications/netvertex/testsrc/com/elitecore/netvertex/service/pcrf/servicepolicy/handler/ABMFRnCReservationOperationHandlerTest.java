package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.ddf.MonetaryBalanceOperation;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.rnc.ABMFRnCReservationOperationHandler;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class ABMFRnCReservationOperationHandlerTest {
    public static final String INR = "INR";
    private ExecutionContext executionContext;
    private ABMFRnCReservationOperationHandler postProcessHandler;
    private QuotaReservation quotaReservation;
    private GyServiceUnits gyServiceUnits;
    private SubscriberMonetaryBalance subscriberMonetaryBalance;
    private SubscriberRnCNonMonetaryBalance subscriberNonMonitoryBalance;
    private long currentTime = System.currentTimeMillis();
    private String id = UUID.randomUUID().toString();
    private PCRFResponse pcrfResponse;
    private MonetaryBalance monetaryBalance;
    private RnCNonMonetaryBalance nonMonetoryBalance;
    private String subscriberId = UUID.randomUUID().toString();

    @Mock private MonetaryBalanceOperation monetaryBalanceOperation;
    @Mock private CacheAwareDDFTable ddfTable;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        postProcessHandler = new ABMFRnCReservationOperationHandler();

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
        gyServiceUnits.setTime(1);
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

        nonMonetoryBalance = createNonMonetaryBalance(id);
        subscriberNonMonitoryBalance = new SubscriberRnCNonMonetaryBalance(Arrays.asList(nonMonetoryBalance));
    }

    @Test
    public void testSkipProcessingWhenQuotaReservationIsNull() {
        pcrfResponse.setQuotaReservation(null);

        postProcessHandler.handle(pcrfResponse, executionContext);

        verify(executionContext, never()).getDDFTable();
    }

    @Test
    public void reserveMonetaryBalanceCallSuccessfully() throws OperationFailedException {
        pcrfResponse.setQuotaReservation(quotaReservation);
        pcrfResponse.setCurrentMonetaryBalance(subscriberMonetaryBalance);
        pcrfResponse.setCurrentRnCNonMonetaryBalance(subscriberNonMonitoryBalance);
        pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val);

        postProcessHandler.handle(pcrfResponse, executionContext);

        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(monetaryBalanceOperation, times(1)).reserve(captor.capture());

        List<List> allValues = captor.getAllValues();

        assertThat(allValues.size(), is(1));
        ReflectionAssert.assertLenientEquals(monetaryBalance, allValues.get(0).get(0));

    }

    @Test
    public void currentMonetaryBalanceNotCallWhneMonetoryBalanceIsNotReserve() throws OperationFailedException {
        gyServiceUnits.setReservedMonetaryBalance(0);
        when(pcrfResponse.getQuotaReservation()).thenReturn(quotaReservation);
        pcrfResponse.setCurrentRnCNonMonetaryBalance(subscriberNonMonitoryBalance);

        postProcessHandler.handle(pcrfResponse, executionContext);

        Mockito.verify(pcrfResponse, times(0)).getCurrentMonetaryBalance();

    }

    @Test
    public void reserveBalanceNotBeCallWhenSubscriberMonetaryBalanceIsNull() throws OperationFailedException {
        pcrfResponse.setQuotaReservation(quotaReservation);
        pcrfResponse.setCurrentMonetaryBalance(null);
        pcrfResponse.setCurrentRnCNonMonetaryBalance(subscriberNonMonitoryBalance);

        postProcessHandler.handle(pcrfResponse, executionContext);

        Mockito.verify(monetaryBalanceOperation, times(0)).reserve(Arrays.asList(monetaryBalance));

    }

    @Test
    public void reserveNonMonetaryBalanceCallSuccessfully() throws OperationFailedException {
        pcrfResponse.setQuotaReservation(quotaReservation);
        pcrfResponse.setCurrentRnCNonMonetaryBalance(subscriberNonMonitoryBalance);
        pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val);

        postProcessHandler.handle(pcrfResponse, executionContext);

        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(ddfTable, times(1)).reserveRnCBalance(Matchers.eq(subscriberId), captor.capture());

        List<List> allValues = captor.getAllValues();

        assertThat(allValues.size(), is(1));
        ReflectionAssert.assertReflectionEquals(nonMonetoryBalance, allValues.get(0).get(0));

    }

    @Test
    public void currentNonMonetaryBalanceNotCallWhenNonMonetaryBalanceIsNotReserve() throws OperationFailedException {
        gyServiceUnits.setReservationRequired(false);
        when(pcrfResponse.getQuotaReservation()).thenReturn(quotaReservation);
        pcrfResponse.setCurrentRnCNonMonetaryBalance(subscriberNonMonitoryBalance);

        postProcessHandler.handle(pcrfResponse, executionContext);

        Mockito.verify(pcrfResponse, times(0)).getCurrentRnCNonMonetaryBalance();

    }

    private MonetaryBalance createMonetaryBalance(String id) {
        return new MonetaryBalance( id,"1", "1"
                ,1,1,
                1, 0, 0, currentTime, currentTime,"INR",null, currentTime,0,
                "","");
    }

    private RnCNonMonetaryBalance createNonMonetaryBalance(String id){
        Random random = new Random();
        return new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(id, UUID.randomUUID().toString(), UUID.randomUUID().toString(), subscriberId, "test", UUID.randomUUID().toString(), ResetBalanceStatus.RESET, null, ChargingType.SESSION).withBillingCycleResetTime(CommonConstants.QUOTA_UNLIMITED).withBillingCycleTimeBalance(random.nextInt(),random.nextInt()).withDailyUsage(random.nextInt()).withDailyResetTime(1).withWeeklyUsage(random.nextInt()).withWeeklyResetTime(1).withReservation(1).build();
    }
}
