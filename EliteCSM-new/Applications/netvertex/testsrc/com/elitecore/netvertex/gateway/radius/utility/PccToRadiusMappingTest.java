package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.netvertex.gateway.radius.mapping.PCCtoRadiusMappingValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class PccToRadiusMappingTest {

    private RadiusPacket radiusPacket;
    private PCRFResponse pcrfResponse;

    @Before
    public void setUp() throws InvalidExpressionException {
        pcrfResponse = new PCRFResponseImpl();
        radiusPacket = new RadiusPacket();
        radiusPacket.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
    }

    @Test
    public void onAARequestPacketTypeSetSuccessfully(){

        AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();
        RadiusAttributeFactory radiusAttributeFactory = RadiusAttributeFactories.fromDummyDictionary();
        PCCtoRadiusMappingValueProvider valueProvider = new PCCtoRadiusMappingValueProvider(pcrfResponse, radiusPacket, null, null);
        PCCToRadiusNonGroupAVPExpressionMapping pccToRadiusNonGroupAVPMapping = spy(new PCCToRadiusNonGroupAVPExpressionMapping("5:20", null, null
                , null , "2", radiusAttributeFactory));
        radiusAttributeFactory.create("5:20");

        pccToRadiusNonGroupAVPMapping.apply(valueProvider, accumalator);

        ArgumentCaptor<PCCtoRadiusMappingValueProvider> pcCtoRadiusMappingValueProviderArgumentCaptor = ArgumentCaptor.forClass(PCCtoRadiusMappingValueProvider.class);
        ArgumentCaptor<AvpAccumalator> avpAccumalatorArgumentCaptor = ArgumentCaptor.forClass(AvpAccumalator.class);
        verify(pccToRadiusNonGroupAVPMapping, atLeastOnce()).apply(pcCtoRadiusMappingValueProviderArgumentCaptor.capture(), avpAccumalatorArgumentCaptor.capture());
        Assert.assertEquals(pcCtoRadiusMappingValueProviderArgumentCaptor.getValue().getRadiusPacket().getPacketType(), RadiusConstants.ACCESS_ACCEPT_MESSAGE);
    }
}
