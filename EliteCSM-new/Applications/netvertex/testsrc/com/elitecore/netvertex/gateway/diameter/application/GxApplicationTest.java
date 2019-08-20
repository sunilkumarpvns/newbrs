package com.elitecore.netvertex.gateway.diameter.application;

import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.derived.AvpEnumerated;
import com.elitecore.diameterapi.diameter.common.packet.avps.derived.AvpUTF8String;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.handler.tgpp.TGPPGyAppHandler;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class GxApplicationTest {
    private GxApplication gxApplication;
    private TGPPGyAppHandler tgppGyAppHandler;
    private DummyDiameterGatewayControllerContext context;
    private static DiameterDictionary diameterDictionary;
    private DiameterGatewayConfiguration gatewayConf;
    private String gatewayName = "gatewayName";
    private DiameterSession session;
    private DiameterRequest request;

    static {
        diameterDictionary = DummyDiameterDictionary.getInstance();
    }

    @Before
    public void setUp() {
        this.context = spy(new DummyDiameterGatewayControllerContext());
        this.gxApplication = spy(new GxApplication(context));
        this.gatewayConf = spy(DiameterGatewayConfiguration.class);
        doReturn(gatewayConf).when(context).getGatewayConfigurationByHostId(anyString());
        doReturn(gatewayName).when(gatewayConf).getName();
        this.session = new DiameterSession("sessionId", null);
        this.request = spy(new DiameterRequest());
        String originatorPeerName = "GGSN";
        request.setRequestingHost(originatorPeerName);
        request.addInfoAvp(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME, originatorPeerName);
        request.addInfoAvp(DiameterAVPConstants.EC_PROXY_AGENT_NAME, originatorPeerName);
    }

    @Test
    public void processApplicationRequestSetSubsIdAVPsInSessionWhenFoundInRequestAndWasNotPresentInSession(){


        IDiameterAVP requestTypeAvp = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.CC_REQUEST_TYPE);
        requestTypeAvp.setStringValue("1");
        request.addAvp(requestTypeAvp);

        IDiameterAVP subscriberIdAvp = DummyDiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID);
        subscriberIdAvp.setStringValue("1");
        request.addAvp(subscriberIdAvp);
        gxApplication.processApplicationRequest(session, request);
        ArrayList<IDiameterAVP> subscriberIDAVPs = (ArrayList<IDiameterAVP>)session.getParameter("subscriberIDAVPlist");
        Assert.assertNotNull("Subscriber ID Avp should be present in session", subscriberIDAVPs);
        Assert.assertEquals(1, subscriberIDAVPs.size());
        Assert.assertSame(subscriberIdAvp, subscriberIDAVPs.get(0));
    }
}
