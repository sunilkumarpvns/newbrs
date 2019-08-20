package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.service.pcrf.DummyPCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(HierarchicalContextRunner.class)
public class RnCReportHandlerTest {

    public static final String INR = "INR";
    private DummyPCRFServiceContext serviceContext;
    private RnCReportHandler reportHandler;
    private PCRFRequestImpl pcrfRequest;
    private PCRFResponseImpl pcrfResponse;
    private ExecutionContext executionContext;


    @Before
    public void setUp() {
        serviceContext = DummyPCRFServiceContext.spy();
        reportHandler = new RnCReportHandler(serviceContext, new RnCQuotaReportHandlerFactory(serviceContext.getServerContext().getPolicyRepository()));
        pcrfRequest = spy(new PCRFRequestImpl(new FixedTimeSource()));
        pcrfResponse = spy(new PCRFResponseImpl());
        executionContext = spy(new ExecutionContext(pcrfRequest, pcrfResponse, mock(CacheAwareDDFTable.class), INR));

    }


    public class Process {

        @Before
        public void setUp() throws OperationFailedException {
            pcrfResponse.setQuotaReservation(new QuotaReservation());
            doReturn(new SubscriberRnCNonMonetaryBalance(null)).when(executionContext).getCurrentRnCNonMonetaryBalance();
            doReturn(new SubscriberMonetaryBalance(new FixedTimeSource(System.currentTimeMillis()))).when(executionContext).getCurrentMonetaryBalance();
        }

        @Test
        public void skipProcessingWhenQuotaReservationNotFound() {
            pcrfResponse.setQuotaReservation(null);
            reportHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verifyNoMoreInteractions(executionContext);
            verify(pcrfRequest, times(0)).getReportedMSCCs();
        }

        @Test
        public void skipProcessingWhenExeceptionIsGeneratedWhileFetchingCurrentBalance() throws OperationFailedException {
            doThrow(new OperationFailedException("from test")).when(executionContext).getCurrentRnCNonMonetaryBalance();
            doThrow(new OperationFailedException("from test")).when(executionContext).getCurrentMonetaryBalance();



            MonetaryBalance monetaryBalance = createMonetaryBalance("");
            MSCC unAccountedQuota = createGrantedMSCC(monetaryBalance);
            unAccountedQuota.getGrantedServiceUnits().setMonetaryBalanceId(monetaryBalance.getId());
            QuotaReservation unAccountedReservation = new QuotaReservation();
            unAccountedReservation.put(unAccountedQuota);
            pcrfResponse.setQuotaReservation(unAccountedReservation);
            List<MSCC> reportedMSCCs = new ArrayList<>();
            reportedMSCCs.add(unAccountedQuota);
            pcrfRequest.setReportedMSCCs(reportedMSCCs);


            reportHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(executionContext).getCurrentRnCNonMonetaryBalance();
            verify(pcrfRequest, times(1)).getReportedMSCCs();
        }

        @Test
        public void skipProcessingWhenExeceptionIsGeneratedWhileFetchingCurrentBalanceWhenUnAccountedQuotaIsMonetary() throws OperationFailedException {
            doThrow(new OperationFailedException("from test")).when(executionContext).getCurrentRnCNonMonetaryBalance();
            doThrow(new OperationFailedException("from test")).when(executionContext).getCurrentMonetaryBalance();

            MonetaryBalance monetaryBalance = createMonetaryBalance("");
            MSCC unAccountedQuota = createGrantedMSCC(monetaryBalance);
            unAccountedQuota.getGrantedServiceUnits().setMonetaryBalanceId(monetaryBalance.getId());
            QuotaReservation unAccountedReservation = new QuotaReservation();
            unAccountedReservation.put(unAccountedQuota);
            pcrfResponse.setUnAccountedQuota(unAccountedReservation);
            pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_STOP);


            reportHandler.process(pcrfRequest, pcrfResponse, executionContext);
            verify(executionContext).getCurrentRnCNonMonetaryBalance();
            verify(pcrfRequest, times(0)).getReportedMSCCs();
            verify(pcrfResponse, times(2)).getUnAccountedQuota();
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
                System.currentTimeMillis(),0
                ,"","");
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
        return new GyServiceUnits("pkg_base",
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

    public class  IsApplicable {

        @Test
        public void returnTrueWhenRequestContainsReportedUsgaeAndSessionIsRO() {
            PCRFRequestImpl serviceRequest = new PCRFRequestImpl(new FixedTimeSource());
            serviceRequest.setReportedMSCCs(Arrays.asList(new MSCC()));
            serviceRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.RO.val);
            serviceRequest.setAttribute(PCRFKeyConstants.CS_SERVICE.val, PCRFKeyValueConstants.VOICE_SERVICE_ID.name());
            assertTrue(reportHandler.isApplicable(serviceRequest, new PCRFResponseImpl()));
        }

        @Test
        public void returnFalseWhenRequestDoesNotContainsReportedUsgaeAndSessionIsRO() {
            PCRFRequestImpl serviceRequest = new PCRFRequestImpl(new FixedTimeSource());
            serviceRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.RO.val);
            serviceRequest.setAttribute(PCRFKeyConstants.CS_SERVICE.val, PCRFKeyValueConstants.VOICE_SERVICE_ID.name());
            assertFalse(reportHandler.isApplicable(serviceRequest, new PCRFResponseImpl()));
        }

        @Test
        public void returnFalseWhenRequestContainsReportedUsgaeAndSessionIsNotRO() {
            PCRFRequestImpl serviceRequest = new PCRFRequestImpl(new FixedTimeSource());
            serviceRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GY.val);
            serviceRequest.setAttribute(PCRFKeyConstants.CS_SERVICE.val, PCRFKeyValueConstants.VOICE_SERVICE_ID.name());
            assertFalse(reportHandler.isApplicable(serviceRequest, new PCRFResponseImpl()));
        }

        @Test
        public void returnFalseWhenServiceIsData() {
            PCRFRequestImpl serviceRequest = new PCRFRequestImpl(new FixedTimeSource());
            serviceRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GY.val);
            serviceRequest.setAttribute(PCRFKeyConstants.CS_SERVICE.val, PCRFKeyValueConstants.DATA_SERVICE_ID.name());
            assertFalse(reportHandler.isApplicable(serviceRequest, new PCRFResponseImpl()));
        }

    }
}