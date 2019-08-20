package com.elitecore.netvertex.service.pcrf.preprocessors;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.locationmanagement.DummyLocationRepository;
import com.elitecore.netvertex.core.locationmanagement.data.NetworkConfiguration;
import com.elitecore.netvertex.core.util.PCRFRequestBuilder;
import com.elitecore.netvertex.core.util.PCRFResponseBuilder;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class OperatorNetworkInfoOrchestratorTest {

    private DummyNetvertexServerContextImpl serverContext;
    private OperatorNetworkInfoOrchestrator operatorNetworkInfoOrchestrator;
    private DummyLocationRepository locationRepository;
    private PCRFResponse response;
    private PCRFRequest request;

    @Before
    public void setUp() throws Exception {
        serverContext = spy(new DummyNetvertexServerContextImpl());
        locationRepository = spy(new DummyLocationRepository());
        serverContext.setLocationRepository(locationRepository);
        operatorNetworkInfoOrchestrator = spy(new OperatorNetworkInfoOrchestrator(serverContext));
        request = new PCRFRequestBuilder().build();
        response = new PCRFResponseBuilder().build();
    }

    public class SGSNMCCMNCNotRecievedInRequest{

        @Before
        public void setupForSGSNMCCMNCNotRecievedInRequest() throws Exception {
            locationRepository.spyNetworkConfigurationByMCCMNC(NetworkInformationOrchestratorConstants.VODAFONE_IN_MCC,
                    NetworkInformationOrchestratorConstants.VODAFONE_GUJ_MNC, NetworkInformationOrchestratorConstants.INDIA,
                    NetworkInformationOrchestratorConstants.VODAFONE_GUJ,
                    NetworkInformationOrchestratorConstants.VODAFONE_IN, NetworkInformationOrchestratorConstants.VODAFONE,NetworkInformationOrchestratorConstants.GEOGRAPHY);
            request.setAttribute(PCRFKeyConstants.LOCATION_MCC.getVal(), NetworkInformationOrchestratorConstants.VODAFONE_IN_MCC);
            request.setAttribute(PCRFKeyConstants.LOCATION_MNC.getVal(), NetworkInformationOrchestratorConstants.VODAFONE_GUJ_MNC);
        }


        @Test
        public void GetNetworkInformationNotCalled_WhenLocationMCCNotFound() {
            request.removeAttribute(PCRFKeyConstants.LOCATION_MCC.getVal());
            operatorNetworkInfoOrchestrator.process(request,response);
            verify(serverContext,times(0)).getLocationRepository();
        }
        @Test
        public void GetNetworkInformationNotCalled_WhenLocationMNCNotFound() {
            request.removeAttribute(PCRFKeyConstants.LOCATION_MNC.getVal());
            operatorNetworkInfoOrchestrator.process(request,response);
            verify(serverContext,times(0)).getLocationRepository();
        }

        @Test
        public void GetNetworkInformationCalledOnce_WhenLocationMCCMNCProvided() {
            operatorNetworkInfoOrchestrator.process(request,response);
            verify(serverContext,times(1)).getLocationRepository();
        }


        @Test
        public void OperatorNetworkInfoOrchestrationNotDone_WhenRecievedLocationMCCNotexist(){

            request.removeAttribute(PCRFKeyConstants.LOCATION_MCC.getVal());

            request.setAttribute(PCRFKeyConstants.LOCATION_MCC.getVal(), NetworkInformationOrchestratorConstants.INVALID_MCC);
            operatorNetworkInfoOrchestrator.process(request,response);
            verifyNoOrhestrationDoneOnRequest();
        }


        @Test
        public void OperatorNetworkInfoOrchestrationNotDone_WhenRecievedLocationMNCNotExist(){

            request.removeAttribute(PCRFKeyConstants.LOCATION_MNC.getVal());
            request.setAttribute(PCRFKeyConstants.LOCATION_MCC.getVal(), NetworkInformationOrchestratorConstants.INVALID_MNC);
            operatorNetworkInfoOrchestrator.process(request,response);
            verifyNoOrhestrationDoneOnRequest();
        }

        @Test
        public void OperatorNetworkInfoOrchestrationDone_WhenRecievedLocationMCCMNCExist(){
            NetworkConfiguration network = locationRepository.getNetworkInformationByMCCMNC(NetworkInformationOrchestratorConstants.VODAFONE_IN_MCC + NetworkInformationOrchestratorConstants.VODAFONE_GUJ_MNC);
            operatorNetworkInfoOrchestrator.process(request,response);
            verifyOrchestratedNetworkInformation(network);
        }

    }

    public class WhenLocationMCCMNCNotRecivedInRequest{

             @Before
             public void setUpForWhenLocationMCCMNCNotRecivedInRequest(){
                 request.removeAttribute(PCRFKeyConstants.LOCATION_MCC.getVal());
                 request.removeAttribute(PCRFKeyConstants.LOCATION_MNC.getVal());
                 request.setAttribute(PCRFKeyConstants.CS_SGSN_MCC_MNC.getVal(), NetworkInformationOrchestratorConstants.SGSN_MCC + NetworkInformationOrchestratorConstants.SGSN_MNC);
                 locationRepository.spyNetworkConfigurationByMCCMNC(NetworkInformationOrchestratorConstants.SGSN_MCC,NetworkInformationOrchestratorConstants.SGSN_MNC, NetworkInformationOrchestratorConstants.UAE, NetworkInformationOrchestratorConstants.E_MIRATES_TELECOM_CORP, NetworkInformationOrchestratorConstants.E_MIRATES_TELECOM_CORP, NetworkInformationOrchestratorConstants.ETISALAT,NetworkInformationOrchestratorConstants.GEOGRAPHY_UAE);

             }

        @Test
        public void OperatorNetworkInfoOrchestrationNotDone_WhenRecievedSGSNMCCNotexist(){
            request.setAttribute(PCRFKeyConstants.CS_SGSN_MCC_MNC.getVal(), NetworkInformationOrchestratorConstants.INVALID_MCC);
            operatorNetworkInfoOrchestrator.process(request,response);
            verifyNoOrhestrationDoneOnRequest();
        }


        @Test
        public void OperatorNetworkInfoOrchestrationDone_WhenRecievedSGSNMCCMNCExist(){
            NetworkConfiguration network = locationRepository.getNetworkInformationByMCCMNC(NetworkInformationOrchestratorConstants.SGSN_MCC+ NetworkInformationOrchestratorConstants.SGSN_MNC);
            operatorNetworkInfoOrchestrator.process(request,response);
            verifyOrchestratedNetworkInformation(network);
        }


    }

    public class WhenLocationANDSGSNMCCMNCExist{


        @Before
        public void setUpWhenLocationANDSGSNMCCMNCExist(){
            request.setAttribute(PCRFKeyConstants.CS_SGSN_MCC_MNC.getVal(), NetworkInformationOrchestratorConstants.SGSN_MCC+NetworkInformationOrchestratorConstants.SGSN_MNC);
            request.setAttribute(PCRFKeyConstants.LOCATION_MCC.getVal(), NetworkInformationOrchestratorConstants.VODAFONE_IN_MCC);
            request.setAttribute(PCRFKeyConstants.LOCATION_MNC.getVal(), NetworkInformationOrchestratorConstants.VODAFONE_GUJ_MNC);
            locationRepository.spyNetworkConfigurationByMCCMNC(NetworkInformationOrchestratorConstants.SGSN_MCC, NetworkInformationOrchestratorConstants.SGSN_MNC, NetworkInformationOrchestratorConstants.UAE, NetworkInformationOrchestratorConstants.E_MIRATES_TELECOM_CORP, NetworkInformationOrchestratorConstants.E_MIRATES_TELECOM_CORP, NetworkInformationOrchestratorConstants.ETISALAT,NetworkInformationOrchestratorConstants.GEOGRAPHY_UAE);
            locationRepository.spyNetworkConfigurationByMCCMNC(NetworkInformationOrchestratorConstants.VODAFONE_IN_MCC,NetworkInformationOrchestratorConstants.VODAFONE_GUJ_MNC, NetworkInformationOrchestratorConstants.INDIA, NetworkInformationOrchestratorConstants.VODAFONE_GUJ, NetworkInformationOrchestratorConstants.VODAFONE_IN, NetworkInformationOrchestratorConstants.VODAFONE,NetworkInformationOrchestratorConstants.GEOGRAPHY);
        }

        @Test
        public void OperatorNetworkInfoOrchestrationNotDone_WhenRecievedLocationAndSGSNMCCDoesntExist(){
            request.setAttribute(PCRFKeyConstants.LOCATION_MCC.getVal(), NetworkInformationOrchestratorConstants.INVALID_MCC+NetworkInformationOrchestratorConstants.INVALID_MNC);
            request.setAttribute(PCRFKeyConstants.CS_SGSN_MCC_MNC.getVal(), NetworkInformationOrchestratorConstants.INVALID_MCC+NetworkInformationOrchestratorConstants.INVALID_MNC);
            operatorNetworkInfoOrchestrator.process(request,response);
            verifyNoOrhestrationDoneOnRequest();
        }



        @Test
        public void OperatorNetworkInfoOrchestrationDoneByLocationInformation_WhenlocationAndSGSNMCMNCExist(){
            NetworkConfiguration network = locationRepository.getNetworkInformationByMCCMNC(NetworkInformationOrchestratorConstants.VODAFONE_IN_MCC + NetworkInformationOrchestratorConstants.VODAFONE_GUJ_MNC);
            operatorNetworkInfoOrchestrator.process(request,response);
            verifyOrchestratedNetworkInformation(network);
        }



        }

    private void verifyNoOrhestrationDoneOnRequest() {
        Assert.assertTrue(response.getAttribute(PCRFKeyConstants.OPERATOR_NETWORK_COUNTRY.getVal()) == null);
        Assert.assertTrue(response.getAttribute(PCRFKeyConstants.OPERATOR_NETWORK_NAME.getVal()) == null);
        Assert.assertTrue(response.getAttribute(PCRFKeyConstants.OPERATOR_NETWORK_BRAND.getVal()) == null);
        Assert.assertTrue(response.getAttribute(PCRFKeyConstants.OPERATOR_NETWORK_TECHNOLOGY.getVal()) == null);
        Assert.assertTrue(response.getAttribute(PCRFKeyConstants.OPERATOR_NAME.getVal()) == null);
        Assert.assertTrue(response.getAttribute(PCRFKeyConstants.LOCATION_GEOGRAPHY.getVal()) == null);
    }

    private void verifyOrchestratedNetworkInformation(NetworkConfiguration network) {
        Assert.assertEquals(response.getAttribute(PCRFKeyConstants.OPERATOR_NETWORK_COUNTRY.getVal()),network.getCountryName());
        Assert.assertEquals(response.getAttribute(PCRFKeyConstants.OPERATOR_NETWORK_NAME.getVal()),network.getNetworkName());
        Assert.assertEquals(response.getAttribute(PCRFKeyConstants.OPERATOR_NETWORK_BRAND.getVal()),network.getBrand());
        Assert.assertEquals(response.getAttribute(PCRFKeyConstants.OPERATOR_NETWORK_TECHNOLOGY.getVal()),network.getTechnology());
        Assert.assertEquals(response.getAttribute(PCRFKeyConstants.OPERATOR_NAME.getVal()),network.getOperator());
        Assert.assertEquals(response.getAttribute(PCRFKeyConstants.OPERATOR_NETWORK_GEOGRAPHY.getVal()),network.getGeography());
    }


}



