package com.elitecore.netvertex.core.conf.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.corenetvertex.constants.ChargingRuleInstallMode;
import com.elitecore.corenetvertex.sm.gateway.AttributeMappingData;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayProfileData;
import com.elitecore.corenetvertex.sm.gateway.DiameterGwProfilePCCRuleMappingData;
import com.elitecore.corenetvertex.sm.gateway.PCCRuleMappingData;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.GroupAttributeMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCAttributeToNonGroupAVPMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFAttributeToNonGroupAVPMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterMappingFactory;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.ChargingRuleDefinitionPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.ServiceDataFlowMapping;
import com.elitecore.netvertex.gateway.diameter.utility.AttributeFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;


import static java.util.Arrays.asList;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class PCCMappingFactoryTest {
    private PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory;
    private PCCMappingFactory pccMappingFactory;
    @Mock
    private Compiler compiler;
    @Mock private LogicalExpression logicalExpression;
    @Mock private LogicalExpression alwaysTruelogicalExpression;
    @Mock private AttributeFactory attributeFactory;
    private Compiler defaultCompiler;
    private GroupAttributeMapping defaultDynamicPCCRuleMapping;

    @Before
    public void setUp() throws InvalidExpressionException {
        defaultCompiler = Compiler.getDefaultCompiler();
        defaultDynamicPCCRuleMapping = createDefaultDynamicPCCRuleMapping();
        MockitoAnnotations.initMocks(this);
        when(compiler.parseLogicalExpression(AdditionalMatchers.not(Matchers.eq("\"1\"=\"1\"")))).thenReturn(logicalExpression);
        when(compiler.parseLogicalExpression(Matchers.eq("\"1\"=\"1\""))).thenReturn(alwaysTruelogicalExpression);
        this.pcrfToDiameterMappingFactory = spy(new PCRFToDiameterMappingFactory(attributeFactory));
        this.pccMappingFactory = new PCCMappingFactory(pcrfToDiameterMappingFactory, compiler);
    }

    @Test
    public void createChargingRuleDefinitionPacketForStaticPCCRuleMapping() throws LoadConfigurationException, InvalidExpressionException {

        String nonGroupedMappingStr = "{\"id\":\"eba47b32-acb1-40ea-b4b1-1797ac2702fb\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policykey\":\"trim( CS.UserName )\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        String type = "STATIC";

        String accessNetworkType = "access, network";

        DiameterGatewayProfileData diameterGatewayProfileData = createDiameterGatewayProfileDataForPCCRuleMapping(Arrays.<String[]>asList(new String[]{nonGroupedMappingStr}), accessNetworkType, "STATIC");

        ChargingRuleDefinitionPacketMapping actualChargingRuleDefinitionPacketMapping = pccMappingFactory.create(diameterGatewayProfileData.getName(),
                diameterGatewayProfileData.getId(), ChargingRuleInstallMode.GROUPED, diameterGatewayProfileData.getDiameterGwProfilePCCRuleMappings());

        LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> staticMap = new LinkedHashMap();

        staticMap.put(logicalExpression, asList(new PCRFAttributeToNonGroupAVPMapping("0:1", defaultCompiler.parseExpression("trim( CS.UserName )"), "trim( CS.UserName )", new HashMap<>(), null, attributeFactory)));

        staticMap.put(alwaysTruelogicalExpression, asList(new PCCAttributeToNonGroupAVPMapping("10415:1005", "PCCRule.Name", new HashMap<>(), null, attributeFactory)));

        LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> dynamicMap = new LinkedHashMap();

        dynamicMap.put(logicalExpression, new ArrayList<>());

        dynamicMap.put(alwaysTruelogicalExpression, asList(defaultDynamicPCCRuleMapping));

        ChargingRuleDefinitionPacketMapping expected = new ChargingRuleDefinitionPacketMapping(staticMap, dynamicMap);

        ReflectionAssert.assertLenientEquals(expected, actualChargingRuleDefinitionPacketMapping);
    }

	@Test
	public void createChargingRuleDefinitionPacketWith2StaticPCCRuleMapping() throws LoadConfigurationException, InvalidExpressionException {

		String nonGroupedMappingStr1 = "{\"id\":\"eba47b32-acb1-40ea-b4b1-1797ac2702fb\",\"pid\":" +
				"\"0\",\"attribute\":\"0:1\",\"policykey\":\"trim( CS.UserName )\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
		String nonGroupedMappingStr2 = "{\"id\":\"eba47b32-acb1-40ea-b4b1-1797ac2702ge\",\"pid\":" +
				"\"0\",\"attribute\":\"0:2\",\"policykey\":\"trim( CS.UserName )\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

		String type = "STATIC";

		String accessNetworkType = "access, network";

		DiameterGatewayProfileData diameterGatewayProfileData = createDiameterGatewayProfileDataForPCCRuleMapping(
				Arrays.asList(new String[]{nonGroupedMappingStr1}, new String[]{nonGroupedMappingStr2}), accessNetworkType, "STATIC");

		ChargingRuleDefinitionPacketMapping actualChargingRuleDefinitionPacketMapping = pccMappingFactory.create(diameterGatewayProfileData.getName(),
				diameterGatewayProfileData.getId(), ChargingRuleInstallMode.GROUPED, diameterGatewayProfileData.getDiameterGwProfilePCCRuleMappings());

		LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> staticMap = new LinkedHashMap();

		staticMap.put(logicalExpression, asList(new PCRFAttributeToNonGroupAVPMapping("0:1", defaultCompiler.parseExpression("trim( CS.UserName )"), "trim( CS.UserName )", new HashMap<>(), null, attributeFactory)));
		staticMap.put(logicalExpression, asList(new PCRFAttributeToNonGroupAVPMapping("0:2", defaultCompiler.parseExpression("trim( CS.UserName )"), "trim( CS.UserName )", new HashMap<>(), null, attributeFactory)));

		staticMap.put(alwaysTruelogicalExpression, asList(new PCCAttributeToNonGroupAVPMapping("10415:1005", "PCCRule.Name", new HashMap<>(), null, attributeFactory)));

		LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> dynamicMap = new LinkedHashMap();

		dynamicMap.put(logicalExpression, new ArrayList<>());

		dynamicMap.put(alwaysTruelogicalExpression, asList(defaultDynamicPCCRuleMapping));

		ChargingRuleDefinitionPacketMapping expected = new ChargingRuleDefinitionPacketMapping(staticMap, dynamicMap);

		ReflectionAssert.assertLenientEquals(expected, actualChargingRuleDefinitionPacketMapping);
	}

    @Test
    public void createChargingRuleDefinitionPacketForDynamicPCCRuleMapping() throws LoadConfigurationException, InvalidExpressionException {

        String nonGroupedMappingStr = "{\"id\":\"eba47b32-acb1-40ea-b4b1-1797ac2702fb\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policykey\":\"trim( CS.UserName )\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        String accessNetworkType = "access, network";

        DiameterGatewayProfileData diameterGatewayProfileData = createDiameterGatewayProfileDataForPCCRuleMapping(Arrays.<String[]>asList(new String[]{nonGroupedMappingStr}),
                accessNetworkType,
                "DYNAMIC");

        ChargingRuleDefinitionPacketMapping actualChargingRuleDefinitionPacketMapping = pccMappingFactory.create(diameterGatewayProfileData.getName(),
                diameterGatewayProfileData.getId(), ChargingRuleInstallMode.GROUPED, diameterGatewayProfileData.getDiameterGwProfilePCCRuleMappings());

        LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> staticMap = new LinkedHashMap();

        staticMap.put(logicalExpression, new ArrayList<>());

        staticMap.put(alwaysTruelogicalExpression, asList(new PCCAttributeToNonGroupAVPMapping("10415:1005", "PCCRule.Name", new HashMap<>(), null, attributeFactory)));

        LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> dynamicMap = new LinkedHashMap();

        dynamicMap.put(logicalExpression, asList(new PCRFAttributeToNonGroupAVPMapping("0:1", defaultCompiler.parseExpression("trim( CS.UserName )"), "trim( CS.UserName )", new HashMap<>(), null, attributeFactory)));

        dynamicMap.put(alwaysTruelogicalExpression, asList(defaultDynamicPCCRuleMapping));

        ChargingRuleDefinitionPacketMapping expected = new ChargingRuleDefinitionPacketMapping(staticMap, dynamicMap);

        ReflectionAssert.assertLenientEquals(expected, actualChargingRuleDefinitionPacketMapping);
    }

    @Test
    public void createChargingRuleDefinitionPacketMappingForMultiplePCCRuleDataWithDifferentPCCRuleTypes() throws LoadConfigurationException, InvalidExpressionException {

        String nonGroupedMappingStr = "{\"id\":\"eba47b32-acb1-40ea-b4b1-1797ac2702fb\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policykey\":\"trim( CS.UserName )\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        String accessNetworkType = "access, network";

        DiameterGatewayProfileData diameterGatewayProfileData = createDiameterGatewayProfileDataForPCCRuleMapping(Arrays.<String[]>asList(new String[]{nonGroupedMappingStr}),
                accessNetworkType,
                "STATIC", "DYNAMIC");

        ChargingRuleDefinitionPacketMapping actualChargingRuleDefinitionPacketMapping = pccMappingFactory.create(diameterGatewayProfileData.getName(),
                diameterGatewayProfileData.getId(), ChargingRuleInstallMode.GROUPED, diameterGatewayProfileData.getDiameterGwProfilePCCRuleMappings());

        LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> staticMap = new LinkedHashMap();

        staticMap.put(logicalExpression, asList(new PCRFAttributeToNonGroupAVPMapping("0:1", defaultCompiler.parseExpression("trim( CS.UserName )"), "trim( CS.UserName )", new HashMap<>(), null, attributeFactory)));

        staticMap.put(alwaysTruelogicalExpression, asList(new PCCAttributeToNonGroupAVPMapping("10415:1005", "PCCRule.Name", new HashMap<>(), null, attributeFactory)));

        LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> dynamicMap = new LinkedHashMap();

        dynamicMap.put(logicalExpression, asList(new PCRFAttributeToNonGroupAVPMapping("0:1", defaultCompiler.parseExpression("trim( CS.UserName )"), "trim( CS.UserName )", new HashMap<>(), null, attributeFactory)));

        dynamicMap.put(alwaysTruelogicalExpression, asList(defaultDynamicPCCRuleMapping));

        ChargingRuleDefinitionPacketMapping expected = new ChargingRuleDefinitionPacketMapping(staticMap, dynamicMap);

        ReflectionAssert.assertLenientEquals(expected, actualChargingRuleDefinitionPacketMapping);
    }

    @Test
    public void testDefaultPCCRuleMappingIsCreatedWhenDiameterGwPCCRuleMappingIsNotConfigured() throws LoadConfigurationException, InvalidExpressionException {

        DiameterGatewayProfileData diameterGatewayProfileData = getDiameterGatewayProfileData();

        ChargingRuleDefinitionPacketMapping actualChargingRuleDefinitionPacketMapping = pccMappingFactory.create(diameterGatewayProfileData.getName(),
                diameterGatewayProfileData.getId(), ChargingRuleInstallMode.GROUPED, diameterGatewayProfileData.getDiameterGwProfilePCCRuleMappings());

        LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> staticMap = new LinkedHashMap();

        staticMap.put(alwaysTruelogicalExpression, asList(new PCCAttributeToNonGroupAVPMapping("10415:1005", "PCCRule.Name", new HashMap<>(), null, attributeFactory)));

        LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> dynamicMap = new LinkedHashMap();

        dynamicMap.put(alwaysTruelogicalExpression, asList(defaultDynamicPCCRuleMapping));

        ChargingRuleDefinitionPacketMapping expected = new ChargingRuleDefinitionPacketMapping(staticMap, dynamicMap);

        ReflectionAssert.assertLenientEquals(expected, actualChargingRuleDefinitionPacketMapping);
    }

    private GroupAttributeMapping createDefaultDynamicPCCRuleMapping() throws InvalidExpressionException {
        return new GroupAttributeMapping("10415:1003", asList(new PCCAttributeToNonGroupAVPMapping(
                        "0:432", "PCCRule.ChargingKey", new HashMap<>(), null, attributeFactory),
                new PCCAttributeToNonGroupAVPMapping("10415:1005", "PCCRule.Name", new HashMap<>(), null, attributeFactory),
                new GroupAttributeMapping("10415:1016", asList(new PCCAttributeToNonGroupAVPMapping("10415:1028", "IPCAN.QCI",
                                new HashMap<>(), null, attributeFactory),
                        new PCCAttributeToNonGroupAVPMapping("10415:1025", "PCCRule.GBRDL", new HashMap<>(), null, attributeFactory),
                        new PCCAttributeToNonGroupAVPMapping("10415:1026", "PCCRule.GBRUL", new HashMap<>(), null, attributeFactory),
                        new PCCAttributeToNonGroupAVPMapping("10415:515", "PCCRule.MBRDL", new HashMap<>(), null, attributeFactory),
                        new PCCAttributeToNonGroupAVPMapping("10415:516", "PCCRule.MBRUL", new HashMap<>(), null, attributeFactory),
                        new GroupAttributeMapping("10415:1034", asList(new PCCAttributeToNonGroupAVPMapping("10415:1046", "IPCAN.PriorityLevel", new HashMap<>(), null, attributeFactory),
                                new PCCAttributeToNonGroupAVPMapping("10415:1047", "IPCAN.PEC", new HashMap<>(), null, attributeFactory),
                                new PCCAttributeToNonGroupAVPMapping("10415:1048", "IPCAN.PEV", new HashMap<>(), null, attributeFactory)), attributeFactory)), attributeFactory),
                new PCCAttributeToNonGroupAVPMapping("10415:1010", "PCCRule.Precedence", new HashMap<>(), null, attributeFactory),
                new PCCAttributeToNonGroupAVPMapping("10415:1066", "PCCRule.MonitoringKey", new HashMap<>(), null, attributeFactory),
                new PCCAttributeToNonGroupAVPMapping("10415:1009", "PCCRule.OnlineCharging", new HashMap<>(), null, attributeFactory),
                new PCCAttributeToNonGroupAVPMapping("10415:1008", "PCCRule.OfflineCharging", new HashMap<>(), null, attributeFactory),
                new PCCAttributeToNonGroupAVPMapping("10415:511", "PCCRule.FlowStatus", new HashMap<>(), null, attributeFactory),
                new GroupAttributeMapping("10415:1058", asList(new ServiceDataFlowMapping("10415:507", attributeFactory), new PCCAttributeToNonGroupAVPMapping("10415:507",
                        "PCCRule.ServiceDataFlow", new HashMap<>(), null, attributeFactory)), attributeFactory)), attributeFactory);
    }

    @Test
    public void testRuleMapKeyIsNullWhenAccessNetworkIsNotConfigured() throws LoadConfigurationException, InvalidExpressionException {

        String nonGroupedMappingStr = "{\"id\":\"eba47b32-acb1-40ea-b4b1-1797ac2702fb\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policykey\":\"trim( CS.UserName )\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        String accessNetworkType = null;

        DiameterGatewayProfileData diameterGatewayProfileData = createDiameterGatewayProfileDataForPCCRuleMapping(Arrays.<String[]>asList(new String[]{nonGroupedMappingStr}),
                accessNetworkType,
                "STATIC", "DYNAMIC");

        ChargingRuleDefinitionPacketMapping actualChargingRuleDefinitionPacketMapping = pccMappingFactory.create(diameterGatewayProfileData.getName(),
                diameterGatewayProfileData.getId(), ChargingRuleInstallMode.GROUPED, diameterGatewayProfileData.getDiameterGwProfilePCCRuleMappings());

        LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> staticMap = new LinkedHashMap();

        staticMap.put(alwaysTruelogicalExpression, asList(new PCRFAttributeToNonGroupAVPMapping("0:1", defaultCompiler.parseExpression("trim( CS.UserName )"), "trim( CS.UserName )", new HashMap<>(), null, attributeFactory)));

        staticMap.put(alwaysTruelogicalExpression, asList(new PCCAttributeToNonGroupAVPMapping("10415:1005", "PCCRule.Name", new HashMap<>(), null, attributeFactory)));

        LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> dynamicMap = new LinkedHashMap();

        dynamicMap.put(alwaysTruelogicalExpression, asList(new PCRFAttributeToNonGroupAVPMapping("0:1", defaultCompiler.parseExpression("trim( CS.UserName )"), "trim( CS.UserName )", new HashMap<>(), null, attributeFactory)));

        dynamicMap.put(alwaysTruelogicalExpression, asList(defaultDynamicPCCRuleMapping));

        ChargingRuleDefinitionPacketMapping expected = new ChargingRuleDefinitionPacketMapping(staticMap, dynamicMap);

        ReflectionAssert.assertLenientEquals(expected, actualChargingRuleDefinitionPacketMapping);
    }

    private DiameterGatewayProfileData getDiameterGatewayProfileData() {
        DiameterGatewayProfileData diameterGatewayProfileData = new DiameterGatewayProfileData();
        diameterGatewayProfileData.setName("DIA_GATEWAY");
        diameterGatewayProfileData.setChargingRuleInstallMode("GROUPED");
        return diameterGatewayProfileData;
    }

	private DiameterGatewayProfileData createDiameterGatewayProfileDataForPCCRuleMapping (List<String[]> mappings,
																						  String accessNeworktype,
																						  String ... types) {
		DiameterGatewayProfileData diameterGatewayProfileData = new DiameterGatewayProfileData();
		diameterGatewayProfileData.setName("DIA_GATEWAY");
		diameterGatewayProfileData.setChargingRuleInstallMode("GROUPED");

		List<DiameterGwProfilePCCRuleMappingData> diameterGwProfilePCCRuleMappingDatas = new ArrayList<>(mappings.size());

		int orderNum = 1;
		for (String[] mapping : mappings) {
			PCCRuleMappingData pccRuleMappingData = new PCCRuleMappingData();
			pccRuleMappingData.setName("PCCRuleMappingData");
			pccRuleMappingData.setDescription("Description");

			for (String type : types) {
				AttributeMappingData attributeMappingData = createAttributeMappingData(type);
				attributeMappingData.setMappings(mapping);

				if (type.equals("STATIC")) {
					pccRuleMappingData.setStaticAttributeMappings(attributeMappingData);
				} else {
					pccRuleMappingData.setDynamicAttributeMappings(attributeMappingData);
				}
			}

			DiameterGwProfilePCCRuleMappingData diameterGwProfilePCCRuleMappingData = new DiameterGwProfilePCCRuleMappingData();
			diameterGwProfilePCCRuleMappingData.setId(UUID.randomUUID().toString());
			diameterGwProfilePCCRuleMappingData.setOrderNumber(orderNum++);
			diameterGwProfilePCCRuleMappingData.setPccRuleMappingData(pccRuleMappingData);
			diameterGwProfilePCCRuleMappingData.setCondition(accessNeworktype);
			diameterGwProfilePCCRuleMappingDatas.add(diameterGwProfilePCCRuleMappingData);
		}

		diameterGatewayProfileData.setDiameterGwProfilePCCRuleMappings(diameterGwProfilePCCRuleMappingDatas);
		diameterGatewayProfileData.setId(UUID.randomUUID().toString());
		return diameterGatewayProfileData;
	}

    private AttributeMappingData createAttributeMappingData(String type) {
        AttributeMappingData attributeMappingData = new AttributeMappingData();
        attributeMappingData.setId(UUID.randomUUID().toString());
        attributeMappingData.setType(type);
        return attributeMappingData;
    }
}
