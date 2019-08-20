package com.elitecore.netvertex.gateway.diameter.application.handler.tgpp;

import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMapping;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.scripts.DiameterGroovyScript;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.SyHandlerResponseListener;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.apache.logging.log4j.ThreadContext;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TGPPSyAppHandlerTest {
    private static final String SESSION_ID = "SessionId";
    private DiameterRequest diameterRequest;
    private DiameterAnswer diameterAnswer;
    private DiameterSession session;
    private TGPPSyAppHandler tgppSyAppHandler;
    private SyHandlerResponseListener responseListener;
    private DummyDiameterGroovyScript dummyDiameterGroovyScript;
    private DiameterGatewayControllerContext context;

    @BeforeClass
    public static void loadDictionary() {
        DummyDiameterDictionary.getInstance();
    }
    @Before
    public void setUp() {


        context = spy(new DummyDiameterGatewayControllerContext());

        DiameterGatewayConfiguration diameterGatewayConfiguration = mock(DiameterGatewayConfiguration.class);

        doReturn(diameterGatewayConfiguration).when(context).getGatewayConfiguration(anyString(), anyString());

        when(diameterGatewayConfiguration.getSLAMappings()).thenReturn(mock(DiameterToPCCMapping.class));

        dummyDiameterGroovyScript = spy(new DummyDiameterGroovyScript(context, diameterGatewayConfiguration));
        session = new DiameterSession(SESSION_ID, null);
        diameterRequest = new DiameterRequest();
        diameterRequest.addAvp(DiameterAVPConstants.TGPP_SL_REQUEST_TYPE, DiameterAttributeValueConstants.TGPP_SL_REQUET_TYPE_INITIAL_REQUEST);
        diameterAnswer = new DiameterAnswer(diameterRequest);
        diameterAnswer.setCommandCode(CommandCode.SPENDING_LIMIT.code);
        diameterAnswer.addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_SUCCESS.code);
        tgppSyAppHandler = new TGPPSyAppHandler(context);
        responseListener = new SyHandlerResponseListener(new PCRFRequestImpl(), new PCRFResponseImpl(), Mockito.mock(ExecutionContext.class), Mockito.mock(PCRFServiceContext.class), ThreadContext.getContext());
        session.setParameter(String.valueOf(diameterAnswer.getHop_by_hopIdentifier()), responseListener);

    }

    @Test
    public void testHandleRecievedResponseSetsSyCommunicationTime() {
        PCRFResponse response = responseListener.getPCRFResponse();
        Assume.assumeTrue(response.getSyCommunicationTime() == -1);
        tgppSyAppHandler.handleReceivedResponse(session, diameterRequest, diameterAnswer);
        assertThat(response.getSyCommunicationTime(), is(greaterThanOrEqualTo(0l)));

    }

    @Test
    public void testHandleRecievedResponseSetsSyPacketQueueTime() {
        PCRFResponse response = responseListener.getPCRFResponse();
        Assume.assumeTrue(response.getSyPacketQueueTime() == -1);
        tgppSyAppHandler.handleReceivedResponse(session, diameterRequest, diameterAnswer);
        assertThat(response.getSyPacketQueueTime(), is(greaterThanOrEqualTo(0l)));
    }

    @Test
    public void testHandleTimeoutResponseSetsSyCommunicationTime() {
        PCRFResponse response = responseListener.getPCRFResponse();
        Assume.assumeTrue(response.getSyCommunicationTime() == -1);
        tgppSyAppHandler.handleTimeoutRequest(session, diameterRequest);
        assertTrue(response.getSyCommunicationTime() >= 0);
    }

    @Test
    public void handleReceivedResponseAppliesGroovyScriptWhenGroovyScriptFoundInGatewayConfiguration() {
        doReturn(Arrays.asList(dummyDiameterGroovyScript)).when(context).getDiameterGroovyScripts(anyString());
        PCRFResponse response = responseListener.getPCRFResponse();
        Assume.assumeTrue(response.getSyCommunicationTime() == -1);
        tgppSyAppHandler.handleReceivedResponse(session, diameterRequest, diameterAnswer);
        verify(dummyDiameterGroovyScript, times(1)).postReceived(any(DiameterPacket.class),any(PCRFResponse.class));
    }

    @Test
    public void handleReceivedResponseSkipsGroovyScriptWhenGroovyScriptNotConfiguredInGatewayConfiguration() {
        doReturn(null).when(context).getDiameterGroovyScripts(anyString());
        PCRFResponse response = responseListener.getPCRFResponse();
        Assume.assumeTrue(response.getSyCommunicationTime() == -1);
        tgppSyAppHandler.handleReceivedResponse(session, diameterRequest, diameterAnswer);
        verify(dummyDiameterGroovyScript, times(0)).postReceived(any(DiameterPacket.class),any(PCRFResponse.class));
    }

    private static class DummyDiameterGroovyScript extends DiameterGroovyScript {

        public DummyDiameterGroovyScript(DiameterGatewayControllerContext diameterGatewayControllerContext,
                                         DiameterGatewayConfiguration configuration) {
            super(diameterGatewayControllerContext, configuration);
        }
        @Override
        public String getName() {
            return "name";
        }
    }

}


