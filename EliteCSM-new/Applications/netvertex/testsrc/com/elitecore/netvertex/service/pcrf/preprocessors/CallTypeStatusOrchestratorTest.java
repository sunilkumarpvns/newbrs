package com.elitecore.netvertex.service.pcrf.preprocessors;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.core.util.PCRFRequestBuilder;
import com.elitecore.netvertex.core.util.PCRFResponseBuilder;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.elitecore.netvertex.service.pcrf.preprocessors.NetworkInformationOrchestratorConstants.AIRTEL_INDIA;
import static com.elitecore.netvertex.service.pcrf.preprocessors.NetworkInformationOrchestratorConstants.INDIA;
import static com.elitecore.netvertex.service.pcrf.preprocessors.NetworkInformationOrchestratorConstants.SRILANKA;
import static com.elitecore.netvertex.service.pcrf.preprocessors.NetworkInformationOrchestratorConstants.VODAFONE_IN;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class CallTypeStatusOrchestratorTest {

    private DummyNetvertexServerContextImpl serverContext;
    private CallTypeStatusOrchestrator callTypeStatusOrchestrator;
    private PCRFRequest request;
    private PCRFResponse response;


    @Before
    public void setUp() throws Exception {
        serverContext = spy(new DummyNetvertexServerContextImpl());
        SystemParameterConfiguration systemParameterConfiguration = new SystemParameterConfiguration();
        systemParameterConfiguration.setOperator(VODAFONE_IN);
        systemParameterConfiguration.setCountry(INDIA);
        serverContext.getServerConfiguration().setSystemParameterConfiguration(systemParameterConfiguration);
        callTypeStatusOrchestrator = new CallTypeStatusOrchestrator(serverContext);
        request = new PCRFRequestBuilder().build();
        response = spy(new PCRFResponseBuilder().build());
        response.setAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val, PCRFKeyValueConstants.SUBSCRIBER_INTERNATIONAL_ROAMING_FALSE.val);

    }


    public class CallTypeStatusOrchestrationSkipsWhen {

        @Test
        public void systemOperatorIsNotSet() {
            DummyNetvertexServerContextImpl serverContext = spy(new DummyNetvertexServerContextImpl());
            SystemParameterConfiguration systemParameterConfiguration = new SystemParameterConfiguration();
            systemParameterConfiguration.setOperator(null);
            serverContext.getServerConfiguration().setSystemParameterConfiguration(systemParameterConfiguration);
            CallTypeStatusOrchestrator callTypeStatusOrchestrator = new CallTypeStatusOrchestrator(serverContext);
            callTypeStatusOrchestrator.process(request, null);
            verify(response, times(0)).getAttribute(PCRFKeyConstants.CS_CALLED_OPERATOR.getVal());
        }

    }

    public class CallTypeStatusIdentifiedAs {

        @Test
        public void onNetCalledPartyIsOnNetworkAsCallingParty() {
            response.setAttribute(PCRFKeyConstants.CS_CALLED_OPERATOR.getVal(), VODAFONE_IN);
            callTypeStatusOrchestrator.process(request, response);
            Assert.assertEquals(response.getAttribute(PCRFKeyConstants.CS_CALLTYPE.getVal()), PCRFKeyValueConstants.CALLTYPE_ONNET.val);
        }

        @Test
        public void offNetIfCalledPartyIsOnDifferentNetworkAsCallingParty() {
            response.setAttribute(PCRFKeyConstants.CS_CALLED_OPERATOR.getVal(), AIRTEL_INDIA);
            callTypeStatusOrchestrator.process(request, response);
            Assert.assertEquals(response.getAttribute(PCRFKeyConstants.CS_CALLTYPE.getVal()), PCRFKeyValueConstants.CALLTYPE_OFFNET.val);
        }

        @Test
        public void internationalIfCalledPartyIsOnDifferentCountryAsCallingParty() {
            response.setAttribute(PCRFKeyConstants.CS_CALLED_COUNTRY.getVal(), SRILANKA);
            callTypeStatusOrchestrator.process(request, response);
            Assert.assertEquals(response.getAttribute(PCRFKeyConstants.CS_CALLTYPE.getVal()), PCRFKeyValueConstants.CALLTYPE_INTERNATIONAL.val);
        }

        public class InterNationalRoamingAs {

            @Before
            public void setUp(){
                response.setAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val, PCRFKeyValueConstants.SUBSCRIBER_INTERNATIONAL_ROAMING_TRUE.val);
            }

            @Test
            public void whenCalledPartyIsRoamingInternationally() {
                callTypeStatusOrchestrator.process(request, response);
                Assert.assertEquals(response.getAttribute(PCRFKeyConstants.CS_CALLTYPE.getVal()), PCRFKeyValueConstants.CALLTYPE_INTERNATIONAL_ROAMING.val);
            }

            @Test
            public void whenCalledPartyIsRoamingInternationallyAndOnDifferentNetworkThanCallingParty() {
                response.setAttribute(PCRFKeyConstants.CS_CALLED_OPERATOR.getVal(), AIRTEL_INDIA);
                callTypeStatusOrchestrator.process(request, response);
                Assert.assertEquals(response.getAttribute(PCRFKeyConstants.CS_CALLTYPE.getVal()), PCRFKeyValueConstants.CALLTYPE_INTERNATIONAL_ROAMING.val);
            }

            @Test
            public void whenCalledPartyIsRoamingInternationallyAndOnSameNetworkThanCallingParty() {
                response.setAttribute(PCRFKeyConstants.CS_CALLED_OPERATOR.getVal(), VODAFONE_IN);
                callTypeStatusOrchestrator.process(request, response);
                Assert.assertEquals(response.getAttribute(PCRFKeyConstants.CS_CALLTYPE.getVal()), PCRFKeyValueConstants.CALLTYPE_INTERNATIONAL_ROAMING.val);
            }

        }
    }


}
