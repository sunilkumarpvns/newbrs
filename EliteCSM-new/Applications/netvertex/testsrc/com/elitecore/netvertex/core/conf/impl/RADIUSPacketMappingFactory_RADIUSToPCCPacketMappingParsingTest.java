package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.corenetvertex.data.RADIUSGatewayDataBuilder;
import com.elitecore.corenetvertex.sm.gateway.AttributeMappingData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGatewayProfileData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMappingFactory;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusChargingRuleDefinitionPacketMapping;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCMappingFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class RADIUSPacketMappingFactory_RADIUSToPCCPacketMappingParsingTest {
    private RadiusToPCCMappingFactory radiusToPCCMappingFactory;
    private RadiusPacketMappingFactory radiusPacketMappingFactory;
    private RadiusChargingRuleDefinitionPacketMapping pccRuleMappings;
    private AttributeMappingData attributeMappingData;
    private RadiusGatewayProfileData radiusGatewayProfileData;

    @Before
    public void setUp() {
        this.radiusToPCCMappingFactory = spy (new RadiusToPCCMappingFactory());
        this.radiusPacketMappingFactory = new RadiusPacketMappingFactory(new PCCToRadiusMappingFactory(), radiusToPCCMappingFactory);
        this.pccRuleMappings = new RadiusChargingRuleDefinitionPacketMapping(new LinkedHashMap<>(), new LinkedHashMap<>());
        radiusGatewayProfileData = RADIUSGatewayDataBuilder.getRADIUSGatewayProfileData();
        attributeMappingData = radiusGatewayProfileData.getRadiusGwProfilePacketMappings().
                get(0).getPacketMappingData().getAttributeMappingData();
    }

    private PacketMappingData createExpectedPacketMappingData(String nonGroupedMappingStr) {
        return GsonFactory.defaultInstance().fromJson(nonGroupedMappingStr,
                PacketMappingData.class);
    }

    @Test
    public void whenPacketMappingIsConfiguredWithSimpleAttributes() throws Exception {

        String nonGroupedMappingStr1 = "{\"id\":\"1\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String nonGroupedMappingStr2 = "{\"id\":\"2\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policyKey\":\"CS.RequestType\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        String groupedMappingStr1 = "{\"id\":\"1\",\"pid\":\"0\",\"attribute\":\"10415:1034:1046\",\"policyKey\":\"CS.UserName\"," +
                "\"defaultvalue\":\"2\",\"valuemapping\":\"1=MAPPED1,2=MAPPED2\"}";
        String groupedMappingStr2 = "{\"id\":\"4\",\"pid\":\"0\",\"attribute\":\"10415:1016:1034:1046\",\"policyKey\":\"CS.UserName\"," +
                "\"defaultvalue\":\"3\",\"valuemapping\":\"3=MAPPED3\"}";
        attributeMappingData.setMappings(new String[]{nonGroupedMappingStr1, nonGroupedMappingStr2, groupedMappingStr1, groupedMappingStr2});

        ArgumentCaptor<PacketMappingData> argument = ArgumentCaptor.forClass(PacketMappingData.class);

        radiusPacketMappingFactory.create(radiusGatewayProfileData, radiusGatewayProfileData.getRadiusGwProfilePacketMappings(),
                pccRuleMappings);

        verify(radiusToPCCMappingFactory, atLeastOnce()).create(argument.capture());
        List<PacketMappingData> allParams = argument.getAllValues();

        assertEquals(4, allParams.size());
        List<PacketMappingData> expectedPacketMappingDatas = createExpectedPacketMappingDataForNonGroupedAndGroupedAtrributeMappings(nonGroupedMappingStr1,
                nonGroupedMappingStr2, groupedMappingStr1, groupedMappingStr2);
        for (PacketMappingData expectedPacketMappingData : expectedPacketMappingDatas) {
            assertTrue(allParams.contains(expectedPacketMappingData));
        }
    }

    @Test
    public void whenPacketMappingIsConfiguredWithExpression() throws Exception {

        String mappingStr1 = "{\"id\":\"1\",\"pid\":\"0\",\"attribute\":\"  trim(0.5)  \",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"2\",\"valuemapping\":\"1=MAPPED1,2=MAPPED2\"}";
        String mappingStr2 = "{\"id\":\"4\",\"pid\":\"0\",\"attribute\":\"trim(  0:5:6)\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"3\",\"valuemapping\":\"3=MAPPED3\"}";
        attributeMappingData.setMappings(new String[]{mappingStr1, mappingStr2});
        ArgumentCaptor<PacketMappingData> argument = ArgumentCaptor.forClass(PacketMappingData.class);
        radiusPacketMappingFactory.create(radiusGatewayProfileData, radiusGatewayProfileData.getRadiusGwProfilePacketMappings(),
                pccRuleMappings);
        verify(radiusToPCCMappingFactory, atLeastOnce()).create(argument.capture()
        );

        List<PacketMappingData> allParams = argument.getAllValues();

        assertEquals(2, allParams.size());
        List<PacketMappingData> expectedPacketMappingDatas = new ArrayList<>();
        PacketMappingData mapping1 = createExpectedPacketMappingData(mappingStr1);
        PacketMappingData mapping2 = createExpectedPacketMappingData(mappingStr2);
        expectedPacketMappingDatas.addAll(Arrays.asList(mapping1, mapping2));
        for (PacketMappingData expectedPacketMappingData : expectedPacketMappingDatas) {
            assertTrue(allParams.contains(expectedPacketMappingData));
        }
    }

    private List<PacketMappingData> createExpectedPacketMappingDataForNonGroupedAndGroupedAtrributeMappings
            (String mappingStr1, String mappingStr2, String mappingStr3, String mappingStr4) {
        List<PacketMappingData> packetMappingDatas = new ArrayList<>();
        PacketMappingData mapping1 = createExpectedPacketMappingData(mappingStr1);
        PacketMappingData mapping2 = createExpectedPacketMappingData(mappingStr2);
        PacketMappingData mapping3 = createExpectedPacketMappingData(mappingStr3);
        PacketMappingData mapping4 = createExpectedPacketMappingData(mappingStr4);
        packetMappingDatas.addAll(Arrays.asList(mapping1, mapping2, mapping3, mapping4));
        return packetMappingDatas;
    }
}
