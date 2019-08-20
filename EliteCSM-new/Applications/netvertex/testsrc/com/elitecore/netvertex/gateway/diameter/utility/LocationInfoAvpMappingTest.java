package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.core.mapping.DiameterRequestBuilder;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.LocationInfoAvpMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocationInfoAvpMappingTest {

    private static final String LOCATION_HAX = "0x8205225100010522510003ea0a";
    private LocationInfoAvpMapping localInfoAVPMapping;
    private DiameterRequest diameterRequest;
    private PCRFRequest pcrfRequest;
    private PCRFRequestMappingValueProvider valueProvider;
    
    @Before
    public void before() {
        
        DummyDiameterDictionary.getInstance();
        diameterRequest = new DiameterRequestBuilder().addLocationInformationAVPs(LOCATION_HAX).build();
        pcrfRequest = new PCRFRequestImpl();
        valueProvider = new PCRFRequestMappingValueProvider(diameterRequest, pcrfRequest, null);
    }
    
    @Test
    public void test_apply_stores_locationInfo_when_foundLocationAVPInDiameterRequest() {
        
        localInfoAVPMapping = new LocationInfoAvpMapping(DiameterAVPConstants.TGPP_USER_LOCATION_INFO);
        localInfoAVPMapping.apply(valueProvider);
        
        assertEquals(pcrfRequest.getAttribute(PCRFKeyConstants.LOCATION_MCC.val), "502");
        assertEquals(pcrfRequest.getAttribute(PCRFKeyConstants.LOCATION_ECGI_ECI.val), "256522");
        assertEquals(pcrfRequest.getAttribute(PCRFKeyConstants.LOCATION_MNC.val), "152");
        assertEquals(pcrfRequest.getAttribute(PCRFKeyConstants.LOCATION_TYPE.val), "130");
        assertEquals(pcrfRequest.getAttribute(PCRFKeyConstants.LOCATION_TAC.val), "1");
        assertEquals(pcrfRequest.getAttribute(PCRFKeyConstants.LOCATION_ECGI_SPARE.val), "0");
        
    }
}
