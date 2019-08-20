package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.corenetvertex.constants.ChargingRuleInstallMode;
import com.elitecore.corenetvertex.constants.GatewayComponent;
import com.elitecore.corenetvertex.constants.PacketType;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.corenetvertex.sm.gateway.AttributeMappingData;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayProfileData;
import com.elitecore.corenetvertex.sm.gateway.DiameterGwProfilePCCRuleMappingData;
import com.elitecore.corenetvertex.sm.gateway.DiameterGwProfilePacketMapData;
import com.elitecore.corenetvertex.sm.gateway.PCCRuleMappingData;
import com.elitecore.corenetvertex.sm.gateway.PacketMappingData;
import com.elitecore.corenetvertex.sm.gateway.VendorData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.diameter.mapping.ApplicationPacketType;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.ChargingRuleDefinitionPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMappingFactory;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterMappingFactory;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.TgppR9Builder;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterPacketMapping;
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
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class PCCToDiameterPacketMappingTest {
    private PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory;
    private DiameterPacketMappingFactory diameterPacketMappingFactory;
    private DiameterGatewayProfileFactory diameterGatewayProfileFactory;
    private PCCMappingFactory pccMappingFactory;
    private ChargingRuleDefinitionPacketMapping pccRuleMappings;
    private TgppR9Builder umBuilder;
    private Map<String, LogicalExpression> expressionStrToLogicalExpression = new HashMap<>();


    @Before
    public void setUp() {
        this.pcrfToDiameterMappingFactory = spy(new PCRFToDiameterMappingFactory());
        this.pccMappingFactory = spy(new PCCMappingFactory(pcrfToDiameterMappingFactory, Compiler.getDefaultCompiler()));
        this.diameterPacketMappingFactory = new DiameterPacketMappingFactory(pcrfToDiameterMappingFactory, new DiameterToPCCMappingFactory());
        this.diameterGatewayProfileFactory = new DiameterGatewayProfileFactory(pccMappingFactory, diameterPacketMappingFactory, mock(ScriptDataFactory.class), new ServiceGuideFactory());
        this.pccRuleMappings = new ChargingRuleDefinitionPacketMapping(new LinkedHashMap<>(), new LinkedHashMap<>());
        this.umBuilder = new TgppR9Builder();
    }

    @Test
    public void defaultMappingOnlyCreatedWhenPAcketMappingsNotConfigured() throws Exception {

        DiameterGatewayProfileData diameterGatewayProfileData = getDiameterGatewayProfileData();

        List<DiameterGwProfilePacketMapData> diameterGwProfilePacketMappings = new ArrayList<>();

        diameterPacketMappingFactory.create(diameterGatewayProfileData, pccRuleMappings, umBuilder);

        Map<ApplicationPacketType, PCCToDiameterMapping> actualMappings = diameterPacketMappingFactory.getPCCToDiameterMappings();

        Map<ApplicationPacketType, PCCToDiameterMapping> expectedMapping = getExpectedMappings(diameterGatewayProfileData, diameterGwProfilePacketMappings);

        assertReflectionEquals(expectedMapping, actualMappings);

        createExpressionMap();
    }

    @Test
    public void defaultMappingOnlyCreatedWhenEmptyMappingsConfigured() throws Exception {
        AttributeMappingData attributeMappingData = new AttributeMappingData();

        attributeMappingData.setMappings(new String[]{});

        DiameterGatewayProfileData diameterGatewayProfileData = getDiameterGatewayProfileData(attributeMappingData);

        List<DiameterGwProfilePacketMapData> diameterGwProfilePacketMappings = new ArrayList<>();

        diameterPacketMappingFactory.create(diameterGatewayProfileData, pccRuleMappings, umBuilder);

        Map<ApplicationPacketType, PCCToDiameterMapping> actualMappings = diameterPacketMappingFactory.getPCCToDiameterMappings();

        Map<ApplicationPacketType, PCCToDiameterMapping> expectedMapping = getExpectedMappings(diameterGatewayProfileData, diameterGwProfilePacketMappings);

        assertReflectionEquals(expectedMapping, actualMappings);

        createExpressionMap();
    }

    private void createExpressionMap() throws InvalidExpressionException {
        String expression1 = "\"1\"=\"1\"";
        this.expressionStrToLogicalExpression.put(expression1, Compiler.getDefaultCompiler().parseLogicalExpression(expression1));
    }

    private Map<ApplicationPacketType, PCCToDiameterMapping> getExpectedMappings(DiameterGatewayProfileData diameterGatewayProfileData,
                                                                                 List<DiameterGwProfilePacketMapData> diameterGwProfilePacketMappings) {

        LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings = new LinkedHashMap<>();

        for (DiameterGwProfilePacketMapData diameterGwProfilePacketMapData : diameterGwProfilePacketMappings) {
            LogicalExpression logicalExpression = expressionStrToLogicalExpression.get(diameterGwProfilePacketMapData.getCondition());
        }

        Map<ApplicationPacketType, PCCToDiameterMapping> expectedMapping = new HashMap<>();
        for(ApplicationPacketType applicationPacketType : ApplicationPacketType.getPccToGatetypeAppPacketType()) {
            expectedMapping.put(applicationPacketType, applicationPacketType.createPCCToDiameterMapping(null, pcrfToDiameterMappingFactory,
                    diameterGatewayProfileData, pccRuleMappings, umBuilder));
        }

        return expectedMapping;
    }

    private DiameterGatewayProfileData getDiameterGatewayProfileData(AttributeMappingData attributeMappingData) {
        PacketMappingData packetMappingData = createPacketMappingData(attributeMappingData);
        List<DiameterGwProfilePacketMapData> diameterGwProfilePacketMappings = createDiameterGwProfilePacketMapData(packetMappingData);
        DiameterGatewayProfileData diameterGatewayProfileData = new DiameterGatewayProfileData();
        diameterGatewayProfileData.setName("DIA_GATEWAY");
        diameterGatewayProfileData.setChargingRuleInstallMode("GROUPED");
        diameterGatewayProfileData.setSupportedStandard(SupportedStandard.CISCOSCE.name());
        diameterGatewayProfileData.setGxApplicationId("gx");
        diameterGatewayProfileData.setGyApplicationId("gy");
        diameterGatewayProfileData.setS9ApplicationId("s9");
        diameterGatewayProfileData.setRxApplicationId("rx");
        diameterGatewayProfileData.setSyApplicationId("sy");
        diameterGatewayProfileData.setSendDPRCloseEvent(true);
        diameterGatewayProfileData.setSessionCleanUpCER(true);
        diameterGatewayProfileData.setSessionCleanUpDPR(true);
        diameterGatewayProfileData.setTcpNagleAlgorithm(true);
        diameterGatewayProfileData.setGatewayType(GatewayComponent.ACCESS_GATEWAY.name());
        diameterGatewayProfileData.setDprAvps("dprAvp");
        diameterGatewayProfileData.setDwrAvps("dwrAvp");
        diameterGatewayProfileData.setSocketReceiveBufferSize(new Integer(10));
        diameterGatewayProfileData.setSocketSendBufferSize(new Integer(10));
        diameterGatewayProfileData.setGroovyScriptDatas(new ArrayList<>());
        diameterGatewayProfileData.setDiameterGwProfilePacketMappings(diameterGwProfilePacketMappings);
        diameterGatewayProfileData.setPccToGWGxPacketMappings(diameterGwProfilePacketMappings);
        diameterGatewayProfileData.setVendorData(mock(VendorData.class));
        diameterGatewayProfileData.setTimeout(new Integer(1));
        return diameterGatewayProfileData;
    }

    private DiameterGatewayProfileData getDiameterGatewayProfileData() {
        DiameterGatewayProfileData diameterGatewayProfileData = new DiameterGatewayProfileData();
        diameterGatewayProfileData.setName("DIA_GATEWAY");
        diameterGatewayProfileData.setChargingRuleInstallMode("GROUPED");
        return diameterGatewayProfileData;
    }

    @Test
    public void whenPacketMappingIsConfiguredWithOneNonGroupedAttribute() throws Exception {

        String noGroupedMappingStr = "{\"id\":\"eba47b32-acb1-40ea-b4b1-1797ac2702fb\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        AttributeMappingData attributeMappingData = createAttributeMappingData();

        attributeMappingData.setMappings(new String[]{noGroupedMappingStr});

        DiameterGatewayProfileData profileData = getDiameterGatewayProfileData(attributeMappingData);

        diameterPacketMappingFactory.create(profileData, pccRuleMappings, umBuilder);


        com.elitecore.netvertex.core.conf.impl.PacketMappingData expectedPacketMappingData = createExpectedPacketMappingData(noGroupedMappingStr);
        verify(pcrfToDiameterMappingFactory, times(1)).create(eq(expectedPacketMappingData),
                same(ChargingRuleInstallMode.fromValue(profileData.getChargingRuleInstallMode())), same(pccRuleMappings));
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
            verify(pcrfToDiameterMappingFactory, times(1)).create(eq(expectedPacketMappingData),
                    same(ChargingRuleInstallMode.fromValue(profileData.getChargingRuleInstallMode())), same(pccRuleMappings));
        }

    }

    @Test
    public void whenPacketMappingIsConfiguredWithOneGroupedAttribute() throws Exception {

        String groupedMappingStr1 = "{\"id\":\"1\",\"pid\":\"0\",\"attribute\":\"10415:1016\",\"policyKey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr2 = "{\"id\":\"2\",\"pid\":\"1\",\"attribute\":\"10415:1034\",\"policyKey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr3 = "{\"id\":\"3\",\"pid\":\"2\",\"attribute\":\"10415:1046\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        AttributeMappingData attributeMappingData = createAttributeMappingData();
        attributeMappingData.setMappings(new String[]{groupedMappingStr1, groupedMappingStr2, groupedMappingStr3});

        DiameterGatewayProfileData profileData = getDiameterGatewayProfileData(attributeMappingData);

        diameterPacketMappingFactory.create(profileData, pccRuleMappings, umBuilder);

        com.elitecore.netvertex.core.conf.impl.PacketMappingData expectedPacketMappingData = createExpectedPacketMappingDataForOneGroupedAttributeMapping(groupedMappingStr1, groupedMappingStr2, groupedMappingStr3);
        verify(pcrfToDiameterMappingFactory, times(1)).create(eq(expectedPacketMappingData),
                same(ChargingRuleInstallMode.fromValue(profileData.getChargingRuleInstallMode())), same(pccRuleMappings));
    }

    @Test
    public void whenPacketMappingIsConfiguredWithMoreThanOneGroupedAttribute() throws Exception {

        String groupedMappingStr1 = "{\"id\":\"1\",\"pid\":\"0\",\"attribute\":\"10415:1016\",\"policyKey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr2 = "{\"id\":\"2\",\"pid\":\"1\",\"attribute\":\"10415:1034\",\"policyKey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr3 = "{\"id\":\"3\",\"pid\":\"2\",\"attribute\":\"10415:1046\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr4 = "{\"id\":\"4\",\"pid\":\"0\",\"attribute\":\"10415:1016\",\"policyKey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr5 = "{\"id\":\"5\",\"pid\":\"4\",\"attribute\":\"10415:1034\",\"policyKey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr6 = "{\"id\":\"6\",\"pid\":\"5\",\"attribute\":\"10415:1046\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        AttributeMappingData attributeMappingData = createAttributeMappingData();

        attributeMappingData.setMappings(new String[]{groupedMappingStr1, groupedMappingStr2, groupedMappingStr3, groupedMappingStr4, groupedMappingStr5, groupedMappingStr6});

        DiameterGatewayProfileData profileData = getDiameterGatewayProfileData(attributeMappingData);

        ArgumentCaptor<com.elitecore.netvertex.core.conf.impl.PacketMappingData> argument = ArgumentCaptor.forClass(com.elitecore.netvertex.core.conf.impl.PacketMappingData.class);
        diameterPacketMappingFactory.create(profileData, pccRuleMappings, umBuilder);
        verify(pcrfToDiameterMappingFactory, atLeastOnce()).create(argument.capture(),
                same(ChargingRuleInstallMode.fromValue(profileData.getChargingRuleInstallMode())), same(pccRuleMappings));

        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> allParams = argument.getAllValues();

        /// 2 for this test cases, 1 for CCA default and 1 for RAR default mapping
        assertEquals(4, allParams.size());

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
        String groupedMappingStr2 = "{\"id\":\"4\",\"pid\":\"3\",\"attribute\":\"10415:1034\",\"policyKey\":\"\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String groupedMappingStr3 = "{\"id\":\"5\",\"pid\":\"4\",\"attribute\":\"10415:1046\",\"policyKey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        AttributeMappingData attributeMappingData = createAttributeMappingData();
        attributeMappingData.setMappings(new String[]{nonGroupedMappingStr1, nonGroupedMappingStr2, groupedMappingStr1, groupedMappingStr2, groupedMappingStr3});
        DiameterGatewayProfileData profileData = getDiameterGatewayProfileData(attributeMappingData);
        ArgumentCaptor<com.elitecore.netvertex.core.conf.impl.PacketMappingData> argument = ArgumentCaptor.forClass(com.elitecore.netvertex.core.conf.impl.PacketMappingData.class);

        diameterPacketMappingFactory.create(profileData, pccRuleMappings, umBuilder);

        verify(pcrfToDiameterMappingFactory, atLeastOnce()).create(argument.capture(),
                same(ChargingRuleInstallMode.fromValue(profileData.getChargingRuleInstallMode())), same(pccRuleMappings));

        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> allParams = argument.getAllValues();

        /// 3 for this, 1 for CCA default and 1 for RAR default mapping
        assertEquals(5, allParams.size());

        List<com.elitecore.netvertex.core.conf.impl.PacketMappingData> expectedPacketMappingDatas = createExpectedPacketMappingDataForNonGroupedAndGroupedAtrributeMappings(nonGroupedMappingStr1,
                nonGroupedMappingStr2, groupedMappingStr1, groupedMappingStr2, groupedMappingStr3);

        for (com.elitecore.netvertex.core.conf.impl.PacketMappingData expectedPacketMappingData : expectedPacketMappingDatas) {
            assertTrue(allParams.contains(expectedPacketMappingData));
        }
    }

    @Test
    public void testPCCRulesApplyWhenPolicyKeyIsPCCRule() throws LoadConfigurationException, InvalidExpressionException {
        String nonGroupedMappingStr1 = "{\"id\":\"1\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policykey\":\"PCCRule\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

//        String pccMapping = "{\"id\":\"1\",\"pid\":" +
//                "\"0\",\"attribute\":\"0:1\",\"policykey\":\"CS.UserName\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String pccMapping = "{\"id\":\"1\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policykey\":\"PCCRule\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";


        AttributeMappingData attributeMappingData = createAttributeMappingData();

        attributeMappingData.setMappings(new String[]{nonGroupedMappingStr1});

        DiameterGatewayProfileData profileData = getDiameterGatewayProfileData(attributeMappingData);

        DiameterGwProfilePCCRuleMappingData diameterGwProfilePCCRuleMappingData = createDiameterGatewayPCCRuleDataForPCCRuleMapping(new String[]{pccMapping},
                "access, network",
                "STATIC", "DYNAMIC");

        profileData.setDiameterGwProfilePCCRuleMappings(Arrays.asList(diameterGwProfilePCCRuleMappingData));

        diameterGatewayProfileFactory.create(profileData);

        verify(pccMappingFactory, times(1)).create(same(profileData.getId()),
                same(profileData.getName()),
                same(ChargingRuleInstallMode.fromValue(profileData.getChargingRuleInstallMode())),
                same(profileData.getDiameterGwProfilePCCRuleMappings()));

        verify(pcrfToDiameterMappingFactory, times(9)).create(any(), any(), any());


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

    private List<DiameterGwProfilePacketMapData> createDiameterGwProfilePacketMapData(PacketMappingData packetMappingData) {
        DiameterGwProfilePacketMapData diameterGwProfilePacketMapData = new DiameterGwProfilePacketMapData();
        diameterGwProfilePacketMapData.setCondition("\"1\"=\"1\"");
        diameterGwProfilePacketMapData.setPacketMappingData(packetMappingData);
        diameterGwProfilePacketMapData.setApplicationType("GX");
        List<DiameterGwProfilePacketMapData> diameterGwProfilePacketMappings = new ArrayList<>();
        diameterGwProfilePacketMappings.add(diameterGwProfilePacketMapData);
        return diameterGwProfilePacketMappings;
    }

    private AttributeMappingData createAttributeMappingData() {
        AttributeMappingData attributeMappingData = new AttributeMappingData();
        attributeMappingData.setId(UUID.randomUUID().toString());
        return attributeMappingData;
    }

    private PacketMappingData createPacketMappingData(AttributeMappingData attributeMappingData) {
        PacketMappingData packetMappingData = new PacketMappingData();
        packetMappingData.setType("PCC TO GATEWAY");
        packetMappingData.setPacketType(PacketType.CREDIT_CONTROL_RESPONSE.name());
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

    private DiameterGwProfilePCCRuleMappingData createDiameterGatewayPCCRuleDataForPCCRuleMapping (String[] mapping,
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
        DiameterGwProfilePCCRuleMappingData diameterGwProfilePCCRuleMappingData = new DiameterGwProfilePCCRuleMappingData();
        diameterGwProfilePCCRuleMappingData.setId(UUID.randomUUID().toString());
        diameterGwProfilePCCRuleMappingData.setOrderNumber(1);
        diameterGwProfilePCCRuleMappingData.setPccRuleMappingData(pccRuleMappingData);
        diameterGwProfilePCCRuleMappingData.setCondition(accessNeworkType);
        return diameterGwProfilePCCRuleMappingData;
    }
}
