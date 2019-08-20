package com.elitecore.netvertex.service.pcrf.preprocessors;

import static org.mockito.Mockito.spy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.util.PCRFRequestBuilder;
import com.elitecore.netvertex.core.util.PCRFResponseBuilder;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class RoamingStatusOrchestratorTest {

    private DummyNetvertexServerContextImpl serverContext;
    private RoamingStatusOrchestrator roamingStatusOrchestrator;
    private PCRFResponse response;
    private PCRFRequest request;

    @Before
    public void setUp() throws Exception {
    	
        serverContext = spy(new DummyNetvertexServerContextImpl());
        roamingStatusOrchestrator = spy(new RoamingStatusOrchestrator(serverContext));
        request = new PCRFRequestBuilder().build();
        response = new PCRFResponseBuilder().build();
    }

    public class SubscriberInRoamingRequest{
    	
    	@Before
    	public void setupRoamingRequest() {
    		response.setAttribute(PCRFKeyConstants.SUB_NETWORK_COUNTRY.getVal(), "US");
            request.setAttribute(PCRFKeyConstants.LOCATION_COUNTRY.getVal(), "IN");
    	}
    	
        @Test
        public void SubscriberInRoamingRequest_WhenSubscriberInRoaming() {
            roamingStatusOrchestrator.process(request,response);
            Assert.assertEquals(response.getAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.getVal()),PCRFKeyValueConstants.SUBSCRIBER_INTERNATIONAL_ROAMING_TRUE.val);
        }
        
        @Test
        public void SubscriberInRoamingRequest_WhenSubscriberCountryAttributeIsNull() {
        	response.removeAttribute(PCRFKeyConstants.SUB_NETWORK_COUNTRY.getVal());
            request.removeAttribute(PCRFKeyConstants.LOCATION_COUNTRY.getVal());
            roamingStatusOrchestrator.process(request,response);
            Assert.assertEquals(response.getAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.getVal()),PCRFKeyValueConstants.SUBSCRIBER_INTERNATIONAL_ROAMING_FALSE.val);
        }
    	
    }
    
    public class SubscriberNonRoamingRequest{
    	
    	@Before
    	public void setupNonRoamingRequest() {
    		response.setAttribute(PCRFKeyConstants.SUB_NETWORK_COUNTRY.getVal(), "US");
            request.setAttribute(PCRFKeyConstants.LOCATION_COUNTRY.getVal(), "US");
    	}
    	
        @Test
        public void SubscriberNonRoamingRequest_WhenSubscriberInNonRoaming() {
            roamingStatusOrchestrator.process(request,response);
            Assert.assertEquals(response.getAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.getVal()),PCRFKeyValueConstants.SUBSCRIBER_INTERNATIONAL_ROAMING_FALSE.val);
        }
        
        @Test
        public void SubscriberNonRoamingRequest_WhenSubscriberNetworkCountryIsNull() {
        	response.removeAttribute(PCRFKeyConstants.SUB_NETWORK_COUNTRY.getVal());
            roamingStatusOrchestrator.process(request,response);
            Assert.assertEquals(response.getAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.getVal()),PCRFKeyValueConstants.SUBSCRIBER_INTERNATIONAL_ROAMING_FALSE.val);
        }
    }

}



