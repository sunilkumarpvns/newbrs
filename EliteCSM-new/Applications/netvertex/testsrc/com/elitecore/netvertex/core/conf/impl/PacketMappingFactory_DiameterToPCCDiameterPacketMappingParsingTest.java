package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.corenetvertex.constants.PacketType;
import com.elitecore.corenetvertex.data.DiameterGatewayDataBuilder;
import com.elitecore.corenetvertex.sm.gateway.AttributeMappingData;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayProfileData;
import com.elitecore.corenetvertex.sm.gateway.DiameterGwProfilePacketMapData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.diameter.mapping.ApplicationPacketType;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMappingFactory;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.ChargingRuleDefinitionPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.TgppR9Builder;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterMappingFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class PacketMappingFactory_DiameterToPCCDiameterPacketMappingParsingTest {
    private DiameterToPCCMappingFactory diameterToPCCMappingFactory;
    private DiameterPacketMappingFactory diameterPacketMappingFactory;
    private ChargingRuleDefinitionPacketMapping pccRuleMappings;
    private TgppR9Builder umBuilder;
    private Map<String, LogicalExpression> expressionStrToLogicalExpression = new HashMap<>();
    private AttributeMappingData attributeMappingData;
    private DiameterGatewayProfileData profileData;

    @Before
    public void setUp() {
        this.diameterToPCCMappingFactory = spy(new DiameterToPCCMappingFactory());
        this.diameterPacketMappingFactory = new DiameterPacketMappingFactory(new PCRFToDiameterMappingFactory(), diameterToPCCMappingFactory);
        this.pccRuleMappings = new ChargingRuleDefinitionPacketMapping(new LinkedHashMap<>(), new LinkedHashMap<>());
        this.umBuilder = new TgppR9Builder();
        profileData = DiameterGatewayDataBuilder.getDiameterGatewayProfileData();
        attributeMappingData = profileData.getDiameterGwProfilePacketMappings().get(0).getPacketMappingData().getAttributeMappingData();
    }

    private Map<ApplicationPacketType, DiameterToPCCMapping> getExpectedMappings(DiameterGatewayProfileData diameterGatewayProfileData) {

        Map<ApplicationPacketType, DiameterToPCCMapping> expectedMapping = new HashMap<>();
        for(ApplicationPacketType applicationPacketType : ApplicationPacketType.getGatwayToPccAppPacketType()) {
            expectedMapping.put(applicationPacketType, applicationPacketType.createDiameterToPCCMapping(null,
                    diameterGatewayProfileData, umBuilder));
        }

        return expectedMapping;
    }

    private void createExpressionMap() throws InvalidExpressionException {
        String expression1 = "\"1\"=\"1\"";
        this.expressionStrToLogicalExpression.put(expression1, Compiler.getDefaultCompiler().parseLogicalExpression(expression1));
    }

    @Test
    public void defaultMappingOnlyCreatedWhenMappingsNotConfigured() throws Exception {

        diameterPacketMappingFactory.create(profileData, pccRuleMappings, umBuilder);

        Map<ApplicationPacketType, DiameterToPCCMapping> actualMappings = diameterPacketMappingFactory.getDiameterToPCCMappings();

        Map<ApplicationPacketType, DiameterToPCCMapping> expectedMapping = getExpectedMappings(profileData);

        assertReflectionEquals(expectedMapping, actualMappings);

        createExpressionMap();
    }


    private com.elitecore.netvertex.core.conf.impl.PacketMappingData createExpectedPacketMappingData(String nonGroupedMappingStr) {
        return GsonFactory.defaultInstance().fromJson(nonGroupedMappingStr,
                com.elitecore.netvertex.core.conf.impl.PacketMappingData.class);
    }

    private List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> createExpectedPacketMappingDataForMoreThanOneNonGroupedAttributeMappings(String nonGroupedMappingStr1, String nonGroupedMappingStr2) {
        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> packetMappingDatas = new ArrayList<>();
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping1 = createExpectedPacketMappingData(nonGroupedMappingStr1);
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping2 = createExpectedPacketMappingData(nonGroupedMappingStr2);
        packetMappingDatas.addAll(Arrays.asList(mapping1, mapping2));
        return packetMappingDatas;
    }

    @Test
    public void whenPacketMappingIsConfiguredWithMoreThanOneNonGroupedAttribute() throws Exception {

        String nonGroupedMappingStr1 = "{\"id\":\"1\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        String nonGroupedMappingStr2 = "{\"id\":\"2\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policyKey\":\"CS.RequestType\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        AttributeMappingData attributeMappingData = createAttributeMappingData();

        attributeMappingData.setMappings(new String[]{nonGroupedMappingStr1, nonGroupedMappingStr2});

        DiameterGatewayProfileData profileData = getDiameterGatewayProfileData(attributeMappingData);

        diameterPacketMappingFactory.create(profileData, pccRuleMappings, umBuilder);

        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> expectedPacketMappingDatas = createExpectedPacketMappingDataForMoreThanOneNonGroupedAttributeMappings
                (nonGroupedMappingStr1, nonGroupedMappingStr2);
        for (com.elitecore.netvertex.core.conf.impl.PacketMappingData expectedPacketMappingData : expectedPacketMappingDatas) {
            verify(diameterToPCCMappingFactory, times(1)).create(eq(expectedPacketMappingData)
            );
        }

    }

    @Test
    public void whenPacketMappingIsConfiguredWithMoreThanOneGroupedAttribute() throws Exception {

        String groupedMappingStr1 = "{\"id\":\"1\",\"pid\":\"0\",\"attribute\":\"10415:1016.10415:1034.10415:1046\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"2\",\"valuemapping\":\"1=MAPPED1,2=MAPPED2\"}";
        String groupedMappingStr2 = "{\"id\":\"4\",\"pid\":\"0\",\"attribute\":\"10415:1016.10415:1034.10415:1046\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"3\",\"valuemapping\":\"3=MAPPED3\"}";
        AttributeMappingData attributeMappingData = createAttributeMappingData();
        attributeMappingData.setMappings(new String[]{groupedMappingStr1, groupedMappingStr2});
        DiameterGatewayProfileData profileData = getDiameterGatewayProfileData(attributeMappingData);
        ArgumentCaptor<com.elitecore.netvertex.core.conf.impl.PacketMappingData> argument = ArgumentCaptor.forClass(com.elitecore.netvertex.core.conf.impl.PacketMappingData.class);
        diameterPacketMappingFactory.create(profileData, pccRuleMappings, umBuilder);
        verify(diameterToPCCMappingFactory, atLeastOnce()).create(argument.capture()
        );

        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> allParams = argument.getAllValues();

        /// 2 for this test cases, 1 for CCA default and 1 for RAR default mapping
        assertEquals(2, allParams.size());

        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> expectedPacketMappingDatas = Arrays.asList(createExpectedPacketMappingData(groupedMappingStr1), createExpectedPacketMappingData(groupedMappingStr2));
        for (com.elitecore.netvertex.core.conf.impl.PacketMappingData expectedPacketMappingData : expectedPacketMappingDatas) {
            assertTrue(allParams.contains(expectedPacketMappingData));
        }

    }

    private List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> createExpectedPacketMappingDataForNonGroupedAndGroupedAtrributeMappings
            (String nonGroupedMappingStr1, String groupedMappingStr2, String groupedMappingStr3) {
        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> packetMappingDatas = new ArrayList<>();
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping1 = createExpectedPacketMappingData(nonGroupedMappingStr1);
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping2 = createExpectedPacketMappingData(groupedMappingStr2);
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping3 = createExpectedPacketMappingData(groupedMappingStr3);

        packetMappingDatas.addAll(Arrays.asList(mapping1, mapping2, mapping3));
        return packetMappingDatas;
    }

    @Test
    public void whenPacketMappingIsConfiguredWithGroupedAttributesAndNonGroupedAttributes() throws Exception {
        String nonGroupedMappingStr1 = "{\"id\":\"1\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String nonGroupedMappingStr2 = "{\"id\":\"2\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policyKey\":\"CS.RequestType\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        String groupedMappingStr1 = "{\"id\":\"3\",\"pid\":\"0\",\"attribute\":\"10415:1016.10415:1034.10415:1046\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        attributeMappingData.setMappings(new String[]{nonGroupedMappingStr1, nonGroupedMappingStr2, groupedMappingStr1});

        profileData = getDiameterGatewayProfileData(attributeMappingData);
        ArgumentCaptor<com.elitecore.netvertex.core.conf.impl.PacketMappingData> argument = ArgumentCaptor.forClass(com.elitecore.netvertex.core.conf.impl.PacketMappingData.class);

        diameterPacketMappingFactory.create(profileData, pccRuleMappings, umBuilder);

        verify(diameterToPCCMappingFactory, atLeastOnce()).create(argument.capture()
        );

        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> allParams = argument.getAllValues();

        /// 3 for this, 1 for CCA default and 1 for RAR default mapping
        assertEquals(3, allParams.size());

        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> expectedPacketMappingDatas = createExpectedPacketMappingDataForNonGroupedAndGroupedAtrributeMappings(nonGroupedMappingStr1,
                nonGroupedMappingStr2, groupedMappingStr1);

        for (com.elitecore.netvertex.core.conf.impl.PacketMappingData expectedPacketMappingData : expectedPacketMappingDatas) {
            assertTrue(allParams.contains(expectedPacketMappingData));
        }
    }

    private AttributeMappingData createAttributeMappingData() {
        AttributeMappingData attributeMappingData = new AttributeMappingData();
        attributeMappingData.setId(UUID.randomUUID().toString());
        return attributeMappingData;
    }

    private com.elitecore.corenetvertex.sm.gateway.PacketMappingData createPacketMappingData(AttributeMappingData attributeMappingData) {
        com.elitecore.corenetvertex.sm.gateway.PacketMappingData packetMappingData = new com.elitecore.corenetvertex.sm.gateway.PacketMappingData();
        packetMappingData.setType("GATEWAY TO PCC");
        packetMappingData.setPacketType(PacketType.CREDIT_CONTROL_REQUEST.name());
        packetMappingData.setAttributeMappingData(attributeMappingData);
        return packetMappingData;
    }

    private List<DiameterGwProfilePacketMapData> createDiameterGwProfilePacketMapData(com.elitecore.corenetvertex.sm.gateway.PacketMappingData packetMappingData) {
        DiameterGwProfilePacketMapData diameterGwProfilePacketMapData = new DiameterGwProfilePacketMapData();
        diameterGwProfilePacketMapData.setCondition("\"1\"=\"1\"");
        diameterGwProfilePacketMapData.setPacketMappingData(packetMappingData);
        diameterGwProfilePacketMapData.setApplicationType("GX");
        List<DiameterGwProfilePacketMapData> diameterGwProfilePacketMappings = new ArrayList<>();
        diameterGwProfilePacketMappings.add(diameterGwProfilePacketMapData);
        return diameterGwProfilePacketMappings;
    }

    private DiameterGatewayProfileData getDiameterGatewayProfileData(AttributeMappingData attributeMappingData) {
        com.elitecore.corenetvertex.sm.gateway.PacketMappingData packetMappingData = createPacketMappingData(attributeMappingData);
        List<DiameterGwProfilePacketMapData> diameterGwProfilePacketMappings = createDiameterGwProfilePacketMapData(packetMappingData);
        DiameterGatewayProfileData diameterGatewayProfileData = new DiameterGatewayProfileData();
        diameterGatewayProfileData.setName("DIA_GATEWAY");
        diameterGatewayProfileData.setChargingRuleInstallMode("GROUPED");
        diameterGatewayProfileData.setDiameterGwProfilePacketMappings(diameterGwProfilePacketMappings);
        diameterGatewayProfileData.setGwToPccGxPacketMappings(diameterGwProfilePacketMappings);
        return diameterGatewayProfileData;
    }
}
