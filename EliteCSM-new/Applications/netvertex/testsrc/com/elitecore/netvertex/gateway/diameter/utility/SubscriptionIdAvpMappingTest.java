package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.core.mapping.DiameterRequestBuilder;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.SubscriptionIdAvpMapping;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SubscriptionIdAvpMappingTest {

    private static final String MSISDN = "1234456985";
    private static final String IMSI = "123456789012345";
    private static final String SIP_URI = "123456789012345@elitecore.com";
    
    private SubscriptionIdAvpMapping subscriptionIdAvpMapping = new SubscriptionIdAvpMapping();
    private DiameterRequest diameterRequest;
    private PCRFRequest pcrfRequest;
    private PCRFRequestMappingValueProvider valueProvider;
    
    @Before
    public void before() {
        
        diameterRequest = new DiameterRequestBuilder().addSubscriptionAVPs(MSISDN, IMSI, SIP_URI, null).build();
        pcrfRequest = new PCRFRequestImpl();
        valueProvider = new PCRFRequestMappingValueProvider(diameterRequest, pcrfRequest, null);
    }
    
    @Test
    public void test_apply_stores_subscriberInfo_when_foundSubscriberIdAVPInDiameterRequest() {
        
        subscriptionIdAvpMapping.apply(valueProvider);
      
        Assert.assertEquals(MSISDN , pcrfRequest.getAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.val));
        Assert.assertEquals(IMSI, pcrfRequest.getAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.val));
        Assert.assertEquals(SIP_URI, pcrfRequest.getAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_SIPURI.val));
    }
}
