package com.elitecore.netvertex.gateway.diameter.transaction.action;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.util.TimeSourceChain;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.core.session.DummySessionLocator;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.impl.DiameterGatewayConfigurationImpl;
import com.elitecore.netvertex.gateway.diameter.conf.impl.DiameterGatewayProfileConfigurationImpl;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMapping;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import static com.elitecore.corenetvertex.constants.PCRFKeyConstants.CS_SESSION_TYPE;
import static com.elitecore.corenetvertex.constants.PCRFKeyConstants.GX_SESSION_ID;
import static com.elitecore.corenetvertex.constants.PCRFKeyConstants.RADIUS_SESSION_ID;
import static com.elitecore.netvertex.core.util.Maps.Entry.newEntry;
import static com.elitecore.netvertex.core.util.Maps.newLinkedHashMap;
import static com.elitecore.netvertex.gateway.diameter.mapping.ApplicationPacketType.GY_CCR;
import static com.elitecore.netvertex.service.pcrf.DiagnosticKey.CO_RELATED_SESSION_IPV4_LOAD;
import static com.elitecore.netvertex.service.pcrf.DiagnosticKey.CO_RELATED_SESSION_IPV6_LOAD;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(HierarchicalContextRunner.class)
public class GyAuthorizeActionTest {


    private DummyDiameterTransactionContext transactionContext;
    private GyAuthorizeAction gyAuthorizeAction;
    private DummySessionLocator dummySessionLocator;
    private DiameterGatewayConfigurationImpl diameterGatewayConfiguration;
    private DiameterGatewayProfileConfigurationImpl diameterGatewayProfileConfiguration;
    private TimeSourceChain timeSourceChain;


    @Before
    public void setUp() {
        initializeDictionary();
        timeSourceChain = new TimeSourceChain(5, 10, 10);
        transactionContext = spy(new DummyDiameterTransactionContext());
        transactionContext.setTimeSource(timeSourceChain);
        gyAuthorizeAction = new GyAuthorizeAction(transactionContext);
        dummySessionLocator = spy(new DummySessionLocator());
        DummyDiameterGatewayControllerContext controllerContext = (DummyDiameterGatewayControllerContext) transactionContext.getControllerContext();
        controllerContext.setSessionLocator(dummySessionLocator);

        diameterGatewayProfileConfiguration = new DiameterGatewayProfileConfigurationImpl();
        diameterGatewayProfileConfiguration.setServiceGuides(Collections.emptyList());
        DiameterToPCCMapping diameterToPCCMapping = new DiameterToPCCMapping.DiameterToPCCMappingBuilder().withGyCCRMapping().build();
        diameterGatewayProfileConfiguration.setDiameterToPCCPacketMappings(newLinkedHashMap(newEntry(GY_CCR, diameterToPCCMapping)));
        diameterGatewayProfileConfiguration.setRevalidationMode(PCRFKeyValueConstants.REVALIDATION_MODE_CLIENT_INITIATED);

        diameterGatewayConfiguration = new DiameterGatewayConfigurationImpl();
        diameterGatewayConfiguration.setName("test");
        diameterGatewayConfiguration.setGatewayId("testId");
        diameterGatewayConfiguration.setHostIdentity("test.sterlite.com");
        diameterGatewayConfiguration.setProfile(diameterGatewayProfileConfiguration);

        controllerContext.getServerContext().getServerConfiguration().addDiameterGatewayConfiguration(diameterGatewayConfiguration);

        DiameterRequest diameterRequest = transactionContext.getDiameterRequest();
        diameterRequest.addInfoAvp(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME, "test");
        diameterRequest.addAvp(DiameterAVPConstants.SESSION_ID, "test1");
        diameterRequest.addAvp(DiameterAVPConstants.CC_REQUEST_TYPE, 1);

    }

    private void initializeDictionary() {
        DummyDiameterDictionary.getInstance();
    }

    public class CoRelateSession {

        public static final int LATEST_TIME = 500;
        private SessionData sessionData;
        private SessionData sessionDataLatest;

        @Before
        public void setUp() {
            Date date = new Date(System.currentTimeMillis());
            sessionData = new SessionDataImpl(null, date, date);
            sessionData.addValue(PCRFKeyConstants.CS_CORESESSION_ID.val, "test1:Gx");
            sessionData.addValue(CS_SESSION_TYPE.val, SessionTypeConstant.GX.val);
            sessionData.addValue(PCRFKeyConstants.CS_PCC_PROFILE_SELECTION_STATE.val, "{}");
            sessionData.setSessionLoadTime(100);

            date = new Date(System.currentTimeMillis() + LATEST_TIME);
            sessionDataLatest = new SessionDataImpl(null, date, date);
            sessionDataLatest.addValue(PCRFKeyConstants.CS_CORESESSION_ID.val, "test2:Radius");
            sessionDataLatest.addValue(CS_SESSION_TYPE.val, SessionTypeConstant.RADIUS.val);
            sessionDataLatest.addValue(PCRFKeyConstants.CS_PCC_PROFILE_SELECTION_STATE.val, "{}");
            sessionDataLatest.setSessionLoadTime(100);
        }

        public class BaseOnIpV4 {


            private String framedIp;

            @Before
            public void setUp() {
                framedIp = "10.1.10.1";
                sessionData.addValue(PCRFKeyConstants.CS_SESSION_IPV4.val, framedIp);
                sessionDataLatest.addValue(PCRFKeyConstants.CS_SESSION_IPV4.val, framedIp);
                DiameterRequest diameterRequest = transactionContext.getDiameterRequest();
                diameterRequest.addAvp(DiameterAVPConstants.FRAMED_IP_ADDRESS, framedIp);

            }

            @Test

            public void setLoadTimeOfSessionWhenSessionFound() {

                dummySessionLocator.addSession(sessionData);

                gyAuthorizeAction.handle();

                verifyLoadTime(100l, CO_RELATED_SESSION_IPV4_LOAD);
            }

            @Test
            public void calculateAndSetLoadTimeOfSessionWhenSessionNotFound() {
                gyAuthorizeAction.handle();

                verifyLoadTime(10l, CO_RELATED_SESSION_IPV4_LOAD);
            }


            @Test
            public void notFetchSessionBasedOnIpV6WhenSessionFoundFromIpV4() {
                dummySessionLocator.addSession(sessionData);

                transactionContext.getDiameterRequest().addAvp(DiameterAVPConstants.FRAMED_IPV6_PREFIX, framedIp);

                gyAuthorizeAction.handle();

                verify(dummySessionLocator, never()).getCoreSessionBySessionIPv6(anyString());
            }

            @Test
            public void checkLatestPCRFRequestBeingServed() {
                dummySessionLocator.addSession(sessionData);
                dummySessionLocator.addSession(sessionDataLatest);

                gyAuthorizeAction.handle();

                ArgumentCaptor<PCRFRequest> captor = ArgumentCaptor.forClass(PCRFRequest.class);
                verify(transactionContext, times(1)).submitPCRFRequest(captor.capture());
                PCRFRequest pcrfRequest = captor.getValue();

                assertNotNull(pcrfRequest.getAttribute(RADIUS_SESSION_ID.val));
                assertNull(pcrfRequest.getAttribute(GX_SESSION_ID.val));
            }

        }


        public class BaseOnIpV6 {


            public static final String FRAMED_IPv6_CONVERTED_VALUE = "1373931";
            private String framedIpv6;

            @Before
            public void setUp() {
                framedIpv6 = "0a791";
                sessionData.addValue(PCRFKeyConstants.CS_SESSION_IPV6.val, FRAMED_IPv6_CONVERTED_VALUE);
                sessionDataLatest.addValue(PCRFKeyConstants.CS_SESSION_IPV6.val, FRAMED_IPv6_CONVERTED_VALUE);

                DiameterRequest diameterRequest = transactionContext.getDiameterRequest();
                diameterRequest.addAvp(DiameterAVPConstants.FRAMED_IPV6_PREFIX, framedIpv6);

            }

            @Test
            public void setLoadTimeOfSessionWhenSessionFound() {
                sessionData.addValue(PCRFKeyConstants.CS_SESSION_IPV6.val, transactionContext.getDiameterRequest().getAVPValue(DiameterAVPConstants.FRAMED_IPV6_PREFIX));

                doReturn(Arrays.asList(sessionData).iterator()).when(dummySessionLocator).getCoreSessionBySessionIPv6(anyString());
                dummySessionLocator.addSession(sessionData);

                gyAuthorizeAction.handle();

                verifyLoadTime(100l, CO_RELATED_SESSION_IPV6_LOAD);
            }

            @Test
            public void calculateAndSetLoadTimeOfSessionWhenSessionNotFound() {
                gyAuthorizeAction.handle();
                verifyLoadTime(10l, CO_RELATED_SESSION_IPV6_LOAD);
            }

            @Test
            public void checkLatestPCRFRequestBeingServed() {
                dummySessionLocator.addSession(sessionData);
                dummySessionLocator.addSession(sessionDataLatest);

                gyAuthorizeAction.handle();

                ArgumentCaptor<PCRFRequest> captor = ArgumentCaptor.forClass(PCRFRequest.class);
                verify(transactionContext, times(1)).submitPCRFRequest(captor.capture());
                PCRFRequest pcrfRequest = captor.getValue();

                assertNotNull(pcrfRequest.getAttribute(RADIUS_SESSION_ID.val));
                assertNull(pcrfRequest.getAttribute(GX_SESSION_ID.val));
            }
        }

    }

    private void verifyLoadTime(long l, String coRelatedSessionIpType) {
        ArgumentCaptor<PCRFRequest> captor = ArgumentCaptor.forClass(PCRFRequest.class);
        verify(transactionContext, times(1)).submitPCRFRequest(captor.capture());
        PCRFRequest pcrfRequest = captor.getValue();
        String longTimeInStr = pcrfRequest.getDiagnosticInformation().get(coRelatedSessionIpType);
        assertThat(longTimeInStr, is(notNullValue()));
        Long loadTime = Long.parseLong(longTimeInStr);
        assertThat(loadTime, is(l));
    }
}


