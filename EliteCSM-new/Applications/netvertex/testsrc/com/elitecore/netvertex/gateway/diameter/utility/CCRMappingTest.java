package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.CCRMapping;
import com.elitecore.netvertex.core.mapping.DiameterRequestBuilder;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CCRMappingTest {

    private static final String DEFAULT_SOURCE_GW = "sourceGW";
    private static final String DEFAULT_CALLED_STATION = "calledStation";
    private static final String DEFAULT_REQ_NUM = "1";
    private static final String DEFAULT_SESSION_ID = "session@Gx";
    private static final String IPV6 = "0x00402402b400100e0021";
    
    private CCRMapping ccrMapping = new CCRMapping();
    private DiameterRequest diameterRequest;
    private PCRFRequest pcrfRequest;
    private PCRFRequestMappingValueProvider valueProvider;
    
    @Before
    public void before() {
        
        diameterRequest = new DiameterRequestBuilder().addCCRMapping(DEFAULT_SOURCE_GW, DEFAULT_CALLED_STATION, DEFAULT_REQ_NUM, DEFAULT_SESSION_ID, DEFAULT_REQ_NUM, IPV6, 1l).build();
        pcrfRequest = new PCRFRequestImpl();
        valueProvider = new PCRFRequestMappingValueProvider(diameterRequest, pcrfRequest, null);
    }
    
    @Test
    public void test_apply_stores_requestNumber_when_foundSubscriberIdAVPInDiameterRequest() {
        
        ccrMapping.apply(valueProvider);
        assertEquals(DEFAULT_REQ_NUM, pcrfRequest.getAttribute(PCRFKeyConstants.REQUEST_NUMBER.val));
    }
    
    @Test
    public void test_apply_stores_requestType_when_foundSubscriberIdAVPInDiameterRequest() {
        
        ccrMapping.apply(valueProvider);
        assertEquals(PCRFKeyValueConstants.REQUEST_TYPE_INITIAL_REQUEST.val , pcrfRequest.getAttribute(PCRFKeyConstants.REQUEST_TYPE.val));
    }   
}
