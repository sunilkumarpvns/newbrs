package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.AccessNetworkAvpMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class AccessNetworkAvpMappingTest {

    private DiameterRequest diameterRequest = new DiameterRequest();
    private PCRFRequest pcrfRequest = new PCRFRequestImpl();
    private PCRFRequestMappingValueProvider valueProvider = new PCRFRequestMappingValueProvider(diameterRequest, pcrfRequest, null);

    @Before
    public void before() {
     DummyDiameterDictionary.getInstance();   
    }
    
    public Object[][] dataProviderFor_test_apply_stores_accessNetworkSpecificAVPs_when_RATTypeFound() {
    
        return new Object[][] {
                {
                    DiameterAttributeValueConstants.TGPP_RAT_TYPE_WLAN, PCRFKeyValueConstants.ACCESS_NETWORK_WLAN.val
                },
                {
                    DiameterAttributeValueConstants.TGPP_RAT_TYPE_VIRTUAL, PCRFKeyValueConstants.ACCESS_NETWORK_VIRTUAL.val
                },
                {
                    DiameterAttributeValueConstants.TGPP_RAT_TYPE_UTRAN, PCRFKeyValueConstants.ACCESS_NETWORK_UTRAN.val
                },
                {
                    DiameterAttributeValueConstants.TGPP_RAT_TYPE_GERAN, PCRFKeyValueConstants.ACCESS_NETWORK_GERAN.val
                },
                {
                    DiameterAttributeValueConstants.TGPP_RAT_TYPE_GAN, PCRFKeyValueConstants.ACCESS_NETWORK_GAN.val
                },
                {
                    DiameterAttributeValueConstants.TGPP_RAT_TYPE_HSPA_EVO, PCRFKeyValueConstants.ACCESS_NETWORK_HSPA_EVO.val
                },
                {
                    DiameterAttributeValueConstants.TGPP_RAT_TYPE_EUTRAN, PCRFKeyValueConstants.ACCESS_NETWORK_EUTRAN.val
                },
                {
                    DiameterAttributeValueConstants.TGPP_RAT_TYPE_CDMA2000_1X, PCRFKeyValueConstants.ACCESS_NETWORK_CDMA2000_1X.val
                },
                {
                    DiameterAttributeValueConstants.TGPP_RAT_TYPE_HRPD, PCRFKeyValueConstants.ACCESS_NETWORK_HRPD.val
                },
                {
                    DiameterAttributeValueConstants.TGPP_RAT_TYPE_UMB, PCRFKeyValueConstants.ACCESS_NETWORK_UMB.val
                },
                {
                    DiameterAttributeValueConstants.TGPP_RAT_TYPE_EHRPD, PCRFKeyValueConstants.ACCESS_NETWORK_EHRPD.val
                }
                   
        };
    }
    
    @Test
    @Parameters(method="dataProviderFor_test_apply_stores_accessNetworkSpecificAVPs_when_RATTypeFound")
    public void test_apply_stores_accessNetworkSpecificAVPs_when_RATTypeFound(String diamterAttributeValue, String expectedPcrfValue) {
        
        diameterRequest.addAvp(DiameterAVPConstants.TGPP_RAT_TYPE, diamterAttributeValue);
        AccessNetworkAvpMapping accessNetworkAvpMapping = new AccessNetworkAvpMapping(DiameterAVPConstants.TGPP_RAT_TYPE, DiameterAVPConstants.TGPP_IP_CAN_TYPE);
        accessNetworkAvpMapping.apply(valueProvider);
        
        Assert.assertEquals(expectedPcrfValue, pcrfRequest.getAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val));
    }
    
    public Object[][] dataProviderFor_test_apply_stores_accessNetworkSpecificAVPs_when_AccessNetworkAVPFound() {
     
        return new Object[][] {
          
                {
                    DiameterAttributeValueConstants.TGPP_IP_CAN_TYPE_3GPP_GPRS, PCRFKeyValueConstants.ACCESS_NETWORK_3GPP_GPRS.val
                },
                {
                    DiameterAttributeValueConstants.TGPP_IP_CAN_TYPE_DOCSIS, PCRFKeyValueConstants.ACCESS_NETWORK_DOCSIS.val
                },
                {
                    DiameterAttributeValueConstants.TGPP_IP_CAN_TYPE_XDSL, PCRFKeyValueConstants.ACCESS_NETWORK_XDSL.val
                },
                {
                    DiameterAttributeValueConstants.TGPP_IP_CAN_TYPE_WIMAX, PCRFKeyValueConstants.ACCESS_NETWORK_WIMAX.val
                },
                {
                    DiameterAttributeValueConstants.TGPP_IP_CAN_TYPE_3GPP2, PCRFKeyValueConstants.ACCESS_NETWORK_3GPP2.val
                },
                {
                    DiameterAttributeValueConstants.TGPP_IP_CAN_TYPE_3GPP_EPS, PCRFKeyValueConstants.ACCESS_NETWORK_3GPP_EPS.val
                },
                {
                    DiameterAttributeValueConstants.TGPP_IP_CAN_TYPE_NON_3GPP_EPS, PCRFKeyValueConstants.ACCESS_NETWORK_NON_3GPP_EPS.val
                }

        };
    }
    
    @Test
    @Parameters(method="dataProviderFor_test_apply_stores_accessNetworkSpecificAVPs_when_AccessNetworkAVPFound")
    public void test_apply_stores_accessNetworkSpecificAVPs_when_AccessNetworkAVPFound(String diamterAttributeValue, String expectedPcrfValue) {
        
        diameterRequest.addAvp(DiameterAVPConstants.TGPP_IP_CAN_TYPE, diamterAttributeValue);
        AccessNetworkAvpMapping accessNetworkAvpMapping = new AccessNetworkAvpMapping(DiameterAVPConstants.TGPP_RAT_TYPE, DiameterAVPConstants.TGPP_IP_CAN_TYPE);
        accessNetworkAvpMapping.apply(valueProvider);
        
        Assert.assertEquals(expectedPcrfValue, pcrfRequest.getAttribute(PCRFKeyConstants.CS_ACCESS_NETWORK.val));
    }
}
