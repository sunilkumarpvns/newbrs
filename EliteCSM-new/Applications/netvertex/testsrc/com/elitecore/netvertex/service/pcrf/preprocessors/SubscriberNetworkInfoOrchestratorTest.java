package com.elitecore.netvertex.service.pcrf.preprocessors;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.locationmanagement.DummyLocationRepository;
import com.elitecore.netvertex.core.locationmanagement.data.NetworkConfiguration;
import com.elitecore.netvertex.core.util.PCRFRequestBuilder;
import com.elitecore.netvertex.core.util.PCRFResponseBuilder;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.elitecore.netvertex.service.pcrf.preprocessors.NetworkInformationOrchestratorConstants.*;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class SubscriberNetworkInfoOrchestratorTest {

    private DummyNetvertexServerContextImpl serverContext;
    private SubscriberNetworkInfoOrchestrator subscriberNetworkInfoOrchestrator;
    private DummyLocationRepository locationRepository;
    private PCRFRequest request;
    private PCRFResponse response;


    @Before
    public void setUp() throws Exception {
        serverContext = spy(new DummyNetvertexServerContextImpl());
        locationRepository = spy(new DummyLocationRepository());
        serverContext.setLocationRepository(locationRepository);
        subscriberNetworkInfoOrchestrator = spy(new SubscriberNetworkInfoOrchestrator(serverContext));
        request = new PCRFRequestBuilder().build();
        response = new PCRFResponseBuilder().build();
        locationRepository.spyNetworkConfigurationByMCCMNC(VODAFONE_IN_MCC,
                VODAFONE_GUJ_MNC, INDIA,
                VODAFONE_GUJ,
                VODAFONE_IN, VODAFONE,GEOGRAPHY);

        locationRepository.spyNetworkConfigurationByMCCMNC(VODAFONE_IN_MCC,
                VODAFONE_RAJ_MNC, INDIA,
                VODAFONE_RAJ,
                VODAFONE_IN, VODAFONE,GEOGRAPHY);
        request.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.getVal(), VODAFONE_IN_MCC+VODAFONE_GUJ_MNC+789456123);

    }


    public class SubscriberNetworkInfoOrchestrationSkipsWhen {


        @Test
        public void subscriberIMSINotReceived() {
            request.removeAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.getVal());
            subscriberNetworkInfoOrchestrator.process(request, null);
            verify(serverContext, times(0)).getLocationRepository();
        }



        @Test
        public void subscriberMccMncnotExistInLocationRepository() {
            String invalidImsi = INVALID_MCC + VODAFONE_GUJ_MNC + 789456123;
            request.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.getVal(), invalidImsi);
            subscriberNetworkInfoOrchestrator.process(request, null);
            verifyNoOrchestrationDoneOnRequest();
        }

    }

    public class SubscriberNetworkInfoOrchestrationDoneWhen {

        @Test
        public void subscriberMCCMNCOf6DigiExistInLocationRepository() {
            NetworkConfiguration networkConfiguration = locationRepository.getNetworkInformationByMCCMNC(VODAFONE_IN_MCC+VODAFONE_GUJ_MNC);
            subscriberNetworkInfoOrchestrator.process(request, response);
            verifyOrchestratedNetworkInformation(networkConfiguration);
        }

        @Test
        public void subscriberMCCMNCOf5DigiExistInLocationRepository() {
            request.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.getVal(), VODAFONE_IN_MCC+VODAFONE_RAJ_MNC+789456123);
            NetworkConfiguration networkConfiguration = locationRepository.getNetworkInformationByMCCMNC(VODAFONE_IN_MCC+VODAFONE_RAJ_MNC);
            subscriberNetworkInfoOrchestrator.process(request, response);
            verifyOrchestratedNetworkInformation(networkConfiguration);
        }

        @Test
        public void setSubscriberMccMncInRequestIfNetworkInfomationFound() {
            NetworkConfiguration networkConfiguration = locationRepository.getNetworkInformationByMCCMNC(VODAFONE_IN_MCC+VODAFONE_GUJ_MNC);
            subscriberNetworkInfoOrchestrator.process(request, response);
            Assert.assertEquals(VODAFONE_IN_MCC+VODAFONE_GUJ_MNC, response.getAttribute(PCRFKeyConstants.SUB_MCC_MNC.getVal()));
        }
    }


    private void verifyNoOrchestrationDoneOnRequest() {
        Assert.assertTrue(response.getAttribute(PCRFKeyConstants.SUB_NETWORK_COUNTRY.getVal()) == null);
        Assert.assertTrue(response.getAttribute(PCRFKeyConstants.SUB_NETWORK_NAME.getVal()) == null);
        Assert.assertTrue(response.getAttribute(PCRFKeyConstants.SUB_NETWORK_BRAND.getVal()) == null);
        Assert.assertTrue(response.getAttribute(PCRFKeyConstants.SUB_NETWORK_TECHNOLOGY.getVal()) == null);
        Assert.assertTrue(response.getAttribute(PCRFKeyConstants.SUB_NETWORK_OPERATOR.getVal()) == null);
        Assert.assertTrue(response.getAttribute(PCRFKeyConstants.SUB_NETWORK_GEOGRAPHY.getVal()) == null);
    }

    private void verifyOrchestratedNetworkInformation(NetworkConfiguration network) {
        Assert.assertEquals(response.getAttribute(PCRFKeyConstants.SUB_NETWORK_COUNTRY.getVal()), network.getCountryName());
        Assert.assertEquals(response.getAttribute(PCRFKeyConstants.SUB_NETWORK_NAME.getVal()), network.getNetworkName());
        Assert.assertEquals(response.getAttribute(PCRFKeyConstants.SUB_NETWORK_BRAND.getVal()), network.getBrand());
        Assert.assertEquals(response.getAttribute(PCRFKeyConstants.SUB_NETWORK_TECHNOLOGY.getVal()), network.getTechnology());
        Assert.assertEquals(response.getAttribute(PCRFKeyConstants.SUB_NETWORK_OPERATOR.getVal()), network.getOperator());
        Assert.assertEquals(response.getAttribute(PCRFKeyConstants.SUB_NETWORK_GEOGRAPHY.getVal()),network.getGeography());
    }

}