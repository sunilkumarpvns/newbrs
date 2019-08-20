package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.corenetvertex.constants.GatewayComponent;
import com.elitecore.corenetvertex.constants.PacketType;
import com.elitecore.corenetvertex.sm.gateway.AttributeMappingData;
import com.elitecore.corenetvertex.sm.gateway.PCCRuleMappingData;
import com.elitecore.corenetvertex.sm.gateway.PacketMappingData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGatewayProfileData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGwProfilePCCRuleMappingData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGwProfilePacketMapData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMapping;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMappingFactory;
import com.elitecore.netvertex.gateway.radius.mapping.RadApplicationPacketType;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusChargingRuleDefinitionPacketMapping;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCMappingFactory;
import com.elitecore.netvertex.gateway.radius.utility.PCCToRadiusPacketMapping;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RADIUSPacketMappingFactory_PCCToRADIUSPacketMappingParsingTest {
    private PCCToRadiusMappingFactory pccToRadiusMappingFactory;
    private RadiusPacketMappingFactory radiusPacketMappingFactory;
    private RadiusChargingRuleDefinitionPacketMapping pccRuleMappings;
    private RadiusPCCRuleMappingFactory radiusPCCRuleMappingFactory;
    private RadiusGatewayProfileFactory radiusGatewayProfileFactory;
    private Map<String, LogicalExpression> expressionStrToLogicalExpression = new HashMap<>();


    @Before
    public void setUp() {
        this.pccToRadiusMappingFactory = spy(new PCCToRadiusMappingFactory());
        this.radiusPCCRuleMappingFactory = spy(new RadiusPCCRuleMappingFactory(pccToRadiusMappingFactory, Compiler.getDefaultCompiler()));
        this.radiusPacketMappingFactory = new RadiusPacketMappingFactory(pccToRadiusMappingFactory, new RadiusToPCCMappingFactory());
        this.pccRuleMappings = new RadiusChargingRuleDefinitionPacketMapping(new LinkedHashMap<>(), new LinkedHashMap<>());
        this.radiusGatewayProfileFactory = new RadiusGatewayProfileFactory(radiusPCCRuleMappingFactory,
                radiusPacketMappingFactory, mock(ScriptDataFactory.class), new ServiceGuideFactory());
    }

    private void createExpressionMap() throws InvalidExpressionException {
        String expression1 = "\"1\"=\"1\"";
        this.expressionStrToLogicalExpression.put(expression1, Compiler.getDefaultCompiler().parseLogicalExpression(expression1));
    }

    private Map<RadApplicationPacketType, PCCToRadiusMapping> getExpectedMappings(RadiusGatewayProfileData radiusGatewayProfileData,
                                                                                 List<RadiusGwProfilePacketMapData> radiusGwProfilePacketMappings) {

        LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> mappings = new LinkedHashMap<>();

        for (RadiusGwProfilePacketMapData radiusGwProfilePacketMapData : radiusGwProfilePacketMappings) {
            LogicalExpression logicalExpression = expressionStrToLogicalExpression.get(radiusGwProfilePacketMapData.getCondition());
            //CReate mapping
        }

        Map<RadApplicationPacketType, PCCToRadiusMapping> expectedMapping = new HashMap<>();
        for(RadApplicationPacketType applicationPacketType : RadApplicationPacketType.getPccToGatetypeAppPacketType()) {
            expectedMapping.put(applicationPacketType, applicationPacketType.createPCCToRadiusMapping(null, pccToRadiusMappingFactory,
                    radiusGatewayProfileData, pccRuleMappings));
        }

        return expectedMapping;
    }

    private RadiusGatewayProfileData getRadiusGatewayProfileData() {
        RadiusGatewayProfileData radiusGatewayProfileData = new RadiusGatewayProfileData();
        radiusGatewayProfileData.setName("RAD_GATEWAY");
        return radiusGatewayProfileData;
    }

    @Test
    public void whenPacketMappingIsConfiguredWithOneNonGroupedAttribute() throws Exception {

        String noGroupedMappingStr = "{\"id\":\"eba47b32-acb1-40ea-b4b1-1797ac2702fb\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        AttributeMappingData attributeMappingData = createAttributeMappingData();
        attributeMappingData.setMappings(new String[]{noGroupedMappingStr});
        PacketMappingData packetMappingData = createPacketMappingData(attributeMappingData);
        List<RadiusGwProfilePacketMapData> radiusGwProfilePacketMappings = createRadiusGwProfilePacketMapData(packetMappingData);
        RadiusGatewayProfileData profileData = getRadiusGatewayProfileData();

        radiusPacketMappingFactory.create(profileData, radiusGwProfilePacketMappings, pccRuleMappings);


        com.elitecore.netvertex.core.conf.impl.PacketMappingData expectedPacketMappingData = createExpectedPacketMappingData(noGroupedMappingStr);
        verify(pccToRadiusMappingFactory, times(1)).create(eq(expectedPacketMappingData), same(pccRuleMappings));
    }

    @Test
    public void whenPacketMappingIsConfiguredWithMoreThanOneNonGroupedAttribute() throws Exception {

        String nonGroupedMappingStr1 = "{\"id\":\"1\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String nonGroupedMappingStr2 = "{\"id\":\"2\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policyKey\":\"CS.RequestType\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        AttributeMappingData attributeMappingData = createAttributeMappingData();
        attributeMappingData.setMappings(new String[]{nonGroupedMappingStr1, nonGroupedMappingStr2});
        PacketMappingData packetMappingData = createPacketMappingData(attributeMappingData);
        List<RadiusGwProfilePacketMapData> radiusGwProfilePacketMappings = createRadiusGwProfilePacketMapData(packetMappingData);
        RadiusGatewayProfileData profileData = getRadiusGatewayProfileData();

        radiusPacketMappingFactory.create(profileData, radiusGwProfilePacketMappings, pccRuleMappings);


        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> expectedPacketMappingDatas = createExpectedPacketMappingDataForMoreThanOneNonGroupedAttributeMappings
                (nonGroupedMappingStr1, nonGroupedMappingStr2);
        for (com.elitecore.netvertex.core.conf.impl.PacketMappingData expectedPacketMappingData : expectedPacketMappingDatas) {
            verify(pccToRadiusMappingFactory, times(1)).create(eq(expectedPacketMappingData), same(pccRuleMappings));
        }

    }

    @Test
    public void whenPacketMappingIsConfiguredWithOneGroupedAttribute() throws Exception {

        String groupedMappingStr1 = "{\"id\":\"1\",\"pid\":\"0\",\"attribute\":\"10415:1016\",\"policyKey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr2 = "{\"id\":\"2\",\"pid\":\"1\",\"attribute\":\"10415:1016:1034\",\"policyKey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr3 = "{\"id\":\"3\",\"pid\":\"2\",\"attribute\":\"10415:1016:1034:1046\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        AttributeMappingData attributeMappingData = createAttributeMappingData();
        attributeMappingData.setMappings(new String[]{groupedMappingStr1, groupedMappingStr2, groupedMappingStr3});
        PacketMappingData packetMappingData = createPacketMappingData(attributeMappingData);
        List<RadiusGwProfilePacketMapData> radiusGwProfilePacketMappings = createRadiusGwProfilePacketMapData(packetMappingData);
        RadiusGatewayProfileData profileData = getRadiusGatewayProfileData();

        radiusPacketMappingFactory.create(profileData, radiusGwProfilePacketMappings, pccRuleMappings);

        com.elitecore.netvertex.core.conf.impl.PacketMappingData expectedPacketMappingData = createExpectedPacketMappingDataForOneGroupedAttributeMapping(groupedMappingStr1, groupedMappingStr2, groupedMappingStr3);
        verify(pccToRadiusMappingFactory, times(1)).create(eq(expectedPacketMappingData), same(pccRuleMappings));
    }

    @Test
    public void whenPacketMappingIsConfiguredWithMoreThanOneGroupedAttribute() throws Exception {

        String groupedMappingStr1 = "{\"id\":\"1\",\"pid\":\"0\",\"attribute\":\"10415:1016\",\"policyKey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr2 = "{\"id\":\"2\",\"pid\":\"1\",\"attribute\":\"10415:1016:1034\",\"policyKey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr3 = "{\"id\":\"3\",\"pid\":\"2\",\"attribute\":\"10415:1016:1034:1046\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr4 = "{\"id\":\"4\",\"pid\":\"0\",\"attribute\":\"10415:1016\",\"policyKey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr5 = "{\"id\":\"5\",\"pid\":\"4\",\"attribute\":\"10415:1016:1034\",\"policyKey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr6 = "{\"id\":\"6\",\"pid\":\"5\",\"attribute\":\"10415:1016:1034:1046\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        AttributeMappingData attributeMappingData = createAttributeMappingData();
        attributeMappingData.setMappings(new String[]{groupedMappingStr1, groupedMappingStr2, groupedMappingStr3, groupedMappingStr4, groupedMappingStr5, groupedMappingStr6});
        PacketMappingData packetMappingData = createPacketMappingData(attributeMappingData);
        List<RadiusGwProfilePacketMapData> radiusGwProfilePacketMappings = createRadiusGwProfilePacketMapData(packetMappingData);
        RadiusGatewayProfileData profileData = getRadiusGatewayProfileData();
        ArgumentCaptor<com.elitecore.netvertex.core.conf.impl.PacketMappingData> argument = ArgumentCaptor.forClass(com.elitecore.netvertex.core.conf.impl.PacketMappingData.class);
        radiusPacketMappingFactory.create(profileData, radiusGwProfilePacketMappings, pccRuleMappings);
        verify(pccToRadiusMappingFactory, atLeastOnce()).create(argument.capture(), same(pccRuleMappings));

        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> allParams = argument.getAllValues();

        assertEquals(2, allParams.size());

        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> expectedPacketMappingDatas = createExpectedPacketMappingDataForMoreThanOneGroupedAttributeMapping(groupedMappingStr1, groupedMappingStr2,
                groupedMappingStr3, groupedMappingStr4, groupedMappingStr5, groupedMappingStr6);
        for (com.elitecore.netvertex.core.conf.impl.PacketMappingData expectedPacketMappingData : expectedPacketMappingDatas) {
            assertTrue(allParams.contains(expectedPacketMappingData));
        }

    }

    private List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> createExpectedPacketMappingDataForMoreThanOneGroupedAttributeMapping(String groupedMappingStr1, String groupedMappingStr2, String groupedMappingStr3,
                                                                                                                                                String groupedMappingStr4, String groupedMappingStr5, String groupedMappingStr6) {
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping1 = createExpectedPacketMappingData(groupedMappingStr1);
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping2 = createExpectedPacketMappingData(groupedMappingStr2);
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping3 = createExpectedPacketMappingData(groupedMappingStr3);
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping4 = createExpectedPacketMappingData(groupedMappingStr4);
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping5 = createExpectedPacketMappingData(groupedMappingStr5);
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping6 = createExpectedPacketMappingData(groupedMappingStr6);
        mapping1.addChildMapping(mapping2);
        mapping2.addChildMapping(mapping3);
        mapping4.addChildMapping(mapping5);
        mapping5.addChildMapping(mapping6);
        return Arrays.asList(mapping1, mapping4);
    }

    @Test
    public void whenPacketMappingIsConfiguredWithGroupedAttributesAndNonGroupedAttributes() throws Exception {
        String nonGroupedMappingStr1 = "{\"id\":\"1\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String nonGroupedMappingStr2 = "{\"id\":\"2\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policyKey\":\"CS.RequestType\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        String groupedMappingStr1 = "{\"id\":\"3\",\"pid\":\"0\",\"attribute\":\"10415:1016\",\"policyKey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr2 = "{\"id\":\"4\",\"pid\":\"3\",\"attribute\":\"10415:1016:1034\",\"policyKey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr3 = "{\"id\":\"5\",\"pid\":\"4\",\"attribute\":\"10415:1016:1034:1046\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        AttributeMappingData attributeMappingData = createAttributeMappingData();
        attributeMappingData.setMappings(new String[]{nonGroupedMappingStr1, nonGroupedMappingStr2, groupedMappingStr1, groupedMappingStr2, groupedMappingStr3});
        PacketMappingData packetMappingData = createPacketMappingData(attributeMappingData);
        List<RadiusGwProfilePacketMapData> radiusGwProfilePacketMapData = createRadiusGwProfilePacketMapData(packetMappingData);
        RadiusGatewayProfileData profileData = getRadiusGatewayProfileData();
        ArgumentCaptor<com.elitecore.netvertex.core.conf.impl.PacketMappingData> argument = ArgumentCaptor.forClass(com.elitecore.netvertex.core.conf.impl.PacketMappingData.class);

        radiusPacketMappingFactory.create(profileData, radiusGwProfilePacketMapData, pccRuleMappings);

        verify(pccToRadiusMappingFactory, atLeastOnce()).create(argument.capture(), same(pccRuleMappings));

        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> allParams = argument.getAllValues();

        assertEquals(3, allParams.size());

        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> expectedPacketMappingDatas = createExpectedPacketMappingDataForNonGroupedAndGroupedAtrributeMappings(nonGroupedMappingStr1,
                nonGroupedMappingStr2, groupedMappingStr1, groupedMappingStr2, groupedMappingStr3);

        for (com.elitecore.netvertex.core.conf.impl.PacketMappingData expectedPacketMappingData : expectedPacketMappingDatas) {
            assertTrue(allParams.contains(expectedPacketMappingData));
        }
    }

    @Test
    public void test_when_packet_mapping_is_not_configured_with_any_mapping() throws LoadConfigurationException, InvalidExpressionException {
        AttributeMappingData attributeMappingData = createAttributeMappingData();
        attributeMappingData.setMappings(new String[]{});
        PacketMappingData packetMappingData = createPacketMappingData(attributeMappingData);
        List<RadiusGwProfilePacketMapData> radiusGwProfilePacketMapData = createRadiusGwProfilePacketMapData(packetMappingData);
        RadiusGatewayProfileData profileData = getRadiusGatewayProfileData();
        ArgumentCaptor<com.elitecore.netvertex.core.conf.impl.PacketMappingData> argument = ArgumentCaptor.forClass(com.elitecore.netvertex.core.conf.impl.PacketMappingData.class);

        radiusPacketMappingFactory.create(profileData, radiusGwProfilePacketMapData, pccRuleMappings);

        verify(pccToRadiusMappingFactory, times(0)).create(argument.capture(), same(pccRuleMappings));

        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> allParams = argument.getAllValues();

        assertEquals(0, allParams.size());
    }

    @Test
    public void testPCCRulesApplyWhenPolicyKeyIsPCCRule() throws LoadConfigurationException, InvalidExpressionException {
        String nonGroupedMappingStr1 = "{\"id\":\"1\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policykey\":\"PCCRule\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        String pccMapping = "{\"id\":\"1\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policykey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        AttributeMappingData attributeMappingData = createAttributeMappingData();

        attributeMappingData.setMappings(new String[]{nonGroupedMappingStr1});

        RadiusGatewayProfileData profileData = getRadiusGatewayProfileData(attributeMappingData);

        RadiusGwProfilePCCRuleMappingData radiusGwProfilePCCRuleMappingData = createRadiusGatewayPCCRuleDataForPCCRuleMapping(new String[]{pccMapping},
                "access, network",
                "STATIC", "DYNAMIC");

        profileData.setRadiusGwProfilePCCRuleMappings(Arrays.asList(radiusGwProfilePCCRuleMappingData));

        profileData.setGatewayType(GatewayComponent.ACCESS_GATEWAY.name());
        ArgumentCaptor<com.elitecore.netvertex.core.conf.impl.PacketMappingData> argument = ArgumentCaptor.forClass(com.elitecore.netvertex.core.conf.impl.PacketMappingData.class);

        radiusGatewayProfileFactory.create(profileData);

        verify(radiusPCCRuleMappingFactory, times(1)).create(same(profileData.getId()),
                same(profileData.getName()),
                same(profileData.getRadiusGwProfilePCCRuleMappings()));

        verify(pccToRadiusMappingFactory, times(3)).create(any(), any());

    }

    private RadiusGatewayProfileData getRadiusGatewayProfileData(AttributeMappingData attributeMappingData) {
        PacketMappingData packetMappingData = createPacketMappingData(attributeMappingData);
        List<RadiusGwProfilePacketMapData> radiusGwProfilePacketMappings = createRadiusGwProfilePacketMapData(packetMappingData);
        RadiusGatewayProfileData radiusGatewayProfileData = new RadiusGatewayProfileData();
        radiusGatewayProfileData.setName("RAD_GATEWAY");
        radiusGatewayProfileData.setVendorId("999999999999");
        radiusGatewayProfileData.setMaxRequestTimeout(new Integer(1));
        radiusGatewayProfileData.setStatusCheckDuration(new Integer(1));
        radiusGatewayProfileData.setIcmpPingEnabled(true);
        radiusGatewayProfileData.setRetryCount(new Integer(1));
        radiusGatewayProfileData.setSendAccountingResponse(true);
        radiusGatewayProfileData.setRadiusGwProfilePacketMappings(radiusGwProfilePacketMappings);
        radiusGatewayProfileData.setTimeout(new Integer(1));
        radiusGatewayProfileData.setGroovyScriptDatas(new ArrayList<>());
        return radiusGatewayProfileData;
    }

    private List<RadiusGwProfilePacketMapData> createRadiusGwProfilePacketMapData(PacketMappingData packetMappingData) {
        RadiusGwProfilePacketMapData radiusGwProfilePacketMapData = new RadiusGwProfilePacketMapData();
        radiusGwProfilePacketMapData.setCondition("\"1\"=\"1\"");
        radiusGwProfilePacketMapData.setPacketMappingData(packetMappingData);
        List<RadiusGwProfilePacketMapData> radiusGwProfilePacketMappings = new ArrayList<>();
        radiusGwProfilePacketMappings.add(radiusGwProfilePacketMapData);
        return radiusGwProfilePacketMappings;
    }



    private com.elitecore.netvertex.core.conf.impl.PacketMappingData createExpectedPacketMappingDataForOneGroupedAttributeMapping(String groupedMappingStr1, String groupedMappingStr2,
                                                                                                                                  String groupedMappingStr3) {
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping1 = createExpectedPacketMappingData(groupedMappingStr1);
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping2 = createExpectedPacketMappingData(groupedMappingStr2);
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping3 = createExpectedPacketMappingData(groupedMappingStr3);

        mapping1.addChildMapping(mapping2);
        mapping2.addChildMapping(mapping3);

        return mapping1;
    }

    private List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> createExpectedPacketMappingDataForNonGroupedAndGroupedAtrributeMappings
            (String nonGroupedMappingStr1, String groupedMappingStr2, String groupedMappingStr3, String groupedMappingStr4, String groupedMappingStr5) {
        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> packetMappingDatas = new ArrayList<>();
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping1 = createExpectedPacketMappingData(nonGroupedMappingStr1);
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping2 = createExpectedPacketMappingData(groupedMappingStr2);
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping3 = createExpectedPacketMappingData(groupedMappingStr3);
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping4 = createExpectedPacketMappingData(groupedMappingStr4);
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping5 = createExpectedPacketMappingData(groupedMappingStr5);
        mapping3.addChildMapping(mapping4);
        mapping4.addChildMapping(mapping5);
        packetMappingDatas.addAll(Arrays.asList(mapping1, mapping2, mapping3));
        return packetMappingDatas;
    }

    private com.elitecore.netvertex.core.conf.impl.PacketMappingData createExpectedPacketMappingData(String nonGroupedMappingStr) {
        return GsonFactory.defaultInstance().fromJson(nonGroupedMappingStr,
                com.elitecore.netvertex.core.conf.impl.PacketMappingData.class);
    }

    private AttributeMappingData createAttributeMappingData() {
        AttributeMappingData attributeMappingData = new AttributeMappingData();
        attributeMappingData.setId(UUID.randomUUID().toString());
        return attributeMappingData;
    }

    private PacketMappingData createPacketMappingData(AttributeMappingData attributeMappingData) {
        PacketMappingData packetMappingData = new PacketMappingData();
        packetMappingData.setType("AA");
        packetMappingData.setApplicationType("AA");
        packetMappingData.setPacketType(PacketType.ACCESS_ACCEPT.name());
        packetMappingData.setAttributeMappingData(attributeMappingData);
        return packetMappingData;
    }

    private List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> createExpectedPacketMappingDataForMoreThanOneNonGroupedAttributeMappings(String nonGroupedMappingStr1, String nonGroupedMappingStr2) {
        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> packetMappingDatas = new ArrayList<>();
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping1 = createExpectedPacketMappingData(nonGroupedMappingStr1);
        com.elitecore.netvertex.core.conf.impl.PacketMappingData mapping2 = createExpectedPacketMappingData(nonGroupedMappingStr2);
        packetMappingDatas.addAll(Arrays.asList(mapping1, mapping2));
        return packetMappingDatas;
    }

    private RadiusGwProfilePCCRuleMappingData createRadiusGatewayPCCRuleDataForPCCRuleMapping (String[] mapping,
                                                                                                   String accessNeworkType,
                                                                                                   String ... types) {
        com.elitecore.corenetvertex.sm.gateway.PCCRuleMappingData pccRuleMappingData = new PCCRuleMappingData();
        pccRuleMappingData.setName("PCCRuleMappingData");

        AttributeMappingData firstAttributeMappingData = createAttributeMappingData();
        firstAttributeMappingData.setType(types[0]);
        firstAttributeMappingData.setMappings(mapping);

        if (types[0].equals("STATIC")) {
            pccRuleMappingData.setStaticAttributeMappings(firstAttributeMappingData);
        } else {
            pccRuleMappingData.setDynamicAttributeMappings(firstAttributeMappingData);
        }

        if (types.length == 2) {
            AttributeMappingData secondAttributeMappingData = createAttributeMappingData();
            secondAttributeMappingData.setType(types[1]);
            secondAttributeMappingData.setMappings(mapping);

            if (types[1].equals("STATIC")) {
                pccRuleMappingData.setStaticAttributeMappings(secondAttributeMappingData);
            } else {
                pccRuleMappingData.setDynamicAttributeMappings(secondAttributeMappingData);
            }
        }

        pccRuleMappingData.setDescription("Description");
        RadiusGwProfilePCCRuleMappingData radiusGwProfilePCCRuleMappingData = new RadiusGwProfilePCCRuleMappingData();
        radiusGwProfilePCCRuleMappingData.setId(UUID.randomUUID().toString());
        radiusGwProfilePCCRuleMappingData.setOrderNumber(1);
        radiusGwProfilePCCRuleMappingData.setPccRuleMappingData(pccRuleMappingData);
        radiusGwProfilePCCRuleMappingData.setCondition(accessNeworkType);
        return radiusGwProfilePCCRuleMappingData;
    }
}
