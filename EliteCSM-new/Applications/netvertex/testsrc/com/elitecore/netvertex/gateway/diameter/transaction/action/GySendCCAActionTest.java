package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.data.ResultCode;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.core.util.PCRFRequestBuilder;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.DiameterAnswerListener;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.RatingGroupSelectionState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;

public class GySendCCAActionTest {

    private GySendCCAAction gySendCCAAction;


    @Before
    public void setUp() throws Exception {
        gySendCCAAction = new GySendCCAAction(new DummyDiameterTransactionContext());

        DiameterRequest diameterRequest = new DiameterRequest();

        gySendCCAAction.getTransactionContext().getTransactionSession().put(SessionKeys.DIAMETER_REQUEST, diameterRequest);

        PCRFRequest pcrfRequest = new PCRFRequestBuilder().addEvents(PCRFEvent.SESSION_UPDATE).build();

        gySendCCAAction.getTransactionContext().getTransactionSession().put(SessionKeys.PCRF_REQUEST, pcrfRequest);

        gySendCCAAction.getTransactionContext().getTransactionSession().put(SessionKeys.PCRF_RESPONSE, new PCRFResponseImpl());

        gySendCCAAction.getTransactionContext().getTransactionSession().put(SessionKeys.DIAMETER_ANSWER_LISTENER, mock(DiameterAnswerListener.class));

    }

    @Test
    public void testNotInitiateRarWhenQuotaReservationIsNull() {

        PCRFRequest pcrfRequest  = (PCRFRequest) get(SessionKeys.PCRF_REQUEST);

        RatingGroupSelectionState ratingGroupSelectionState = new RatingGroupSelectionState();
        pcrfRequest.setPCCProfileSelectionState(ratingGroupSelectionState);

        pcrfRequest.setAttribute(PCRFKeyConstants.GX_SESSION_ID.val, "Gx_Session_Id");


        gySendCCAAction.handle();

        TaskScheduler taskScheduler = gySendCCAAction.getTransactionContext().getControllerContext().getServerContext().getTaskScheduler();

        Mockito.verify(taskScheduler, Mockito.times(0)).scheduleSingleExecutionTask(any(SingleExecutionAsyncTask.class));


        DiameterGatewayControllerContext controllerContext = gySendCCAAction.getTransactionContext().getControllerContext();
        Mockito.verify(controllerContext, Mockito.times(0)).reauthorizeSesion(any(PCRFKeyConstants.class),anyString(),anyString(),anyBoolean(), anyMap());
    }

    @Test
    public void testInitiateRarWhenReservationFail() {

        PCRFRequest pcrfRequest  = (PCRFRequest) get(SessionKeys.PCRF_REQUEST);

        RatingGroupSelectionState ratingGroupSelectionState = new RatingGroupSelectionState();
        pcrfRequest.setPCCProfileSelectionState(ratingGroupSelectionState);

        pcrfRequest.setAttribute(PCRFKeyConstants.GX_SESSION_ID.val, "Gx_Session_Id");


        PCRFResponse pcrfResponse = (PCRFResponse) get(SessionKeys.PCRF_RESPONSE);

        MSCC grantedMscc = new MSCC();

        grantedMscc.setResultCode(ResultCode.DIAMETER_END_USER_SERVICE_DENIED);

        QuotaReservation quotaReservation = new QuotaReservation();

        quotaReservation.put(grantedMscc);

        pcrfResponse.setQuotaReservation(quotaReservation);

        gySendCCAAction.handle();

        FakeTaskScheduler taskScheduler = (FakeTaskScheduler) gySendCCAAction.getTransactionContext().getControllerContext().getServerContext().getTaskScheduler();

        Mockito.verify(taskScheduler, Mockito.times(1)).scheduleSingleExecutionTask(any(SingleExecutionAsyncTask.class));

        taskScheduler.tick();

        DiameterGatewayControllerContext controllerContext = gySendCCAAction.getTransactionContext().getControllerContext();
        Mockito.verify(controllerContext, Mockito.times(1)).reauthorizeSesion(any(PCRFKeyConstants.class),anyString(),anyString(),anyBoolean(), anyMap());
    }



    private Object get(String sessionKeys) {
        return gySendCCAAction.getTransactionContext().getTransactionSession().get(sessionKeys);
    }
}