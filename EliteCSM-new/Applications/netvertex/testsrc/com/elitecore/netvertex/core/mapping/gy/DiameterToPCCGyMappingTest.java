package com.elitecore.netvertex.core.mapping.gy;

import com.elitecore.corenetvertex.constants.GatewayComponent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.conf.impl.DiameterGatewayConfigurationImpl;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gy.DiameterToPCCGyMapping;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class DiameterToPCCGyMappingTest {

    private DiameterToPCCGyMapping diameterToPCCGyMapping = new DiameterToPCCGyMapping();
    private DiameterRequest diameterRequest = new DiameterRequest();
    private PCRFRequest pcrfRequest = new PCRFRequestImpl();
    private DiameterGatewayConfiguration diameterGatewayConfiguration = Mockito.mock(DiameterGatewayConfigurationImpl.class);
    private PCRFRequestMappingValueProvider valueProvider = new PCRFRequestMappingValueProvider(diameterRequest, pcrfRequest, diameterGatewayConfiguration);
    private static DiameterDictionary diameterDictionary;
    private IDiameterAVP callingStationId;
    private IDiameterAVP calledStationId;

    @BeforeClass
    public static void initializeDictionary() throws Exception {
        diameterDictionary = DummyDiameterDictionary.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        AvpGrouped serviceInformation = (AvpGrouped) diameterDictionary.getKnownAttribute(DiameterAVPConstants.TGPP_SERVICE_INFORMATION);
        AvpGrouped imsInformation = (AvpGrouped) diameterDictionary.getKnownAttribute(DiameterAVPConstants.TGPP_IMS_INFORMATION);

        callingStationId = diameterDictionary.getKnownAttribute(DiameterAVPConstants.TGPP_CALLING_PARTY_ADDRESS);
        calledStationId = diameterDictionary.getKnownAttribute(DiameterAVPConstants.TGPP_CALLED_PARTY_ADDRESS);

        imsInformation.addSubAvp(calledStationId);
        imsInformation.addSubAvp(callingStationId);
        serviceInformation.addSubAvp(imsInformation);
        diameterRequest.addAvp(serviceInformation);

        IDiameterAVP requestType = diameterDictionary.getKnownAttribute(DiameterAVPConstants.CC_REQUEST_TYPE);
        diameterRequest.addAvp(requestType);

        Mockito.when(diameterGatewayConfiguration.getRevalidationMode()).thenReturn(PCRFKeyValueConstants.REVALIDATION_MODE_CLIENT_INITIATED);
    }

    @Test
    public void setCalledStationIdInPCRFRequestWhenFoundInDiameterRequest(){
        Mockito.when(diameterGatewayConfiguration.getGatewayType()).thenReturn(GatewayComponent.APPLICATION_FUNCTION);
        diameterToPCCGyMapping.apply(valueProvider);

        Assert.assertEquals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.val), calledStationId.getStringValue());
    }

    @Test
    public void setCallingStationIdInPCRFRequestWhenFoundInDiameterRequest(){
        Mockito.when(diameterGatewayConfiguration.getGatewayType()).thenReturn(GatewayComponent.APPLICATION_FUNCTION);
        diameterToPCCGyMapping.apply(valueProvider);

        Assert.assertEquals(pcrfRequest.getAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.val), callingStationId.getStringValue());
    }
}
