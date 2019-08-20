package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.core.util.PCRFRequestBuilder;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;

public class RxAuthorizeActionTest {

    private DummyDiameterTransactionContext transactionContext;
    private RxAuthorizeAction rxAuthorizeAction;
    private DiameterRequest diameterRequest;
    private PCRFRequest pcrfRequest;

    @Before
    public void setUp() {
        transactionContext = spy(new DummyDiameterTransactionContext());
        rxAuthorizeAction = new RxAuthorizeAction(transactionContext);
        diameterRequest = transactionContext.getDiameterRequest();
        pcrfRequest = new PCRFRequestBuilder().addEvents(PCRFEvent.SESSION_STOP).build();
    }

    @Test
    public void sendSuccessResToGatewayShouldGetCallAtleastOnce() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = RxAuthorizeAction.class.getDeclaredMethod("sendSuccessResToGateway", DiameterRequest.class, PCRFRequest.class);
        method.setAccessible(true);
        method.invoke(rxAuthorizeAction, diameterRequest, pcrfRequest);
        Mockito.verify(transactionContext, Mockito.times(1)).sendAnswer(any(),any());
    }
}


