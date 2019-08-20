package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.corenetvertex.sm.gateway.AttributeMappingData;
import com.elitecore.corenetvertex.sm.gateway.PCCRuleMappingData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGatewayProfileData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGwProfilePCCRuleMappingData;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMappingFactory;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusChargingRuleDefinitionPacketMapping;
import com.elitecore.netvertex.gateway.radius.utility.PCCToRadiusNonGroupAVPExpressionMapping;
import com.elitecore.netvertex.gateway.radius.utility.PCCToRadiusPacketMapping;
import com.elitecore.netvertex.gateway.radius.utility.RadiusAttributeFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class RadiusPCCRuleMappingFactoryTest {
    private PCCToRadiusMappingFactory pccToRadiusMappingFactory;
    private RadiusPCCRuleMappingFactory pccRuleMappingFactory;
    @Mock
    private Compiler compiler;
    @Mock private LogicalExpression logicalExpression;
    @Mock private LogicalExpression alwaysTruelogicalExpression;
    @Mock private RadiusAttributeFactory radiusAttributeFactory;
    private Compiler defaultCompiler;

    @Before
    public void setUp() throws InvalidExpressionException {
        defaultCompiler = Compiler.getDefaultCompiler();
        MockitoAnnotations.initMocks(this);
        when(compiler.parseLogicalExpression(AdditionalMatchers.not(Matchers.eq("\"1\"=\"1\"")))).thenReturn(logicalExpression);
        when(compiler.parseLogicalExpression(Matchers.eq("\"1\"=\"1\""))).thenReturn(alwaysTruelogicalExpression);
        this.pccToRadiusMappingFactory = spy(new PCCToRadiusMappingFactory(radiusAttributeFactory));
        this.pccRuleMappingFactory = new RadiusPCCRuleMappingFactory(pccToRadiusMappingFactory, compiler);
    }

    @Test
    public void createChargingRuleDefinitionPacketForStaticPCCRuleMapping() throws LoadConfigurationException, InvalidExpressionException {
        String nonGroupedMappingStr = "{\"id\":\"eba47b32-acb1-40ea-b4b1-1797ac2702fb\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policykey\":\"trim( CS.UserName )\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String type = "STATIC";
        String accessNetworkType = "access, network";
        RadiusGatewayProfileData radiusGatewayProfileData = createRadiusGatewayProfileDataForPCCRuleMapping(new String[]{nonGroupedMappingStr},
                accessNetworkType,
                "STATIC");
        RadiusChargingRuleDefinitionPacketMapping actualChargingRuleDefinitionPacketMapping = pccRuleMappingFactory.create(radiusGatewayProfileData.getName(),
                radiusGatewayProfileData.getId(), radiusGatewayProfileData.getRadiusGwProfilePCCRuleMappings());
        LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> staticMap = new LinkedHashMap();
        staticMap.put(logicalExpression, asList(new PCCToRadiusNonGroupAVPExpressionMapping("0:1", defaultCompiler.parseExpression("trim( CS.UserName )"),
                "trim( CS.UserName )", new HashMap<>(), null, radiusAttributeFactory)));
        LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> dynamicMap = new LinkedHashMap();
        dynamicMap.put(logicalExpression, new ArrayList<>());
        RadiusChargingRuleDefinitionPacketMapping expected = new RadiusChargingRuleDefinitionPacketMapping(staticMap, dynamicMap);
        ReflectionAssert.assertLenientEquals(expected, actualChargingRuleDefinitionPacketMapping);
    }

    @Test
    public void createChargingRuleDefinitionPacketForDynamicPCCRuleMapping() throws LoadConfigurationException, InvalidExpressionException {
        String nonGroupedMappingStr = "{\"id\":\"eba47b32-acb1-40ea-b4b1-1797ac2702fb\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policykey\":\"trim( CS.UserName )\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";
        String type = "DYNAMIC";
        String accessNetworkType = "access, network";
        RadiusGatewayProfileData radiusGatewayProfileData = createRadiusGatewayProfileDataForPCCRuleMapping(new String[]{nonGroupedMappingStr},
                accessNetworkType,
                "DYNAMIC");
        RadiusChargingRuleDefinitionPacketMapping actualChargingRuleDefinitionPacketMapping = pccRuleMappingFactory.create(radiusGatewayProfileData.getName(),
                radiusGatewayProfileData.getId(), radiusGatewayProfileData.getRadiusGwProfilePCCRuleMappings());
        LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> staticMap = new LinkedHashMap();
        staticMap.put(logicalExpression, new ArrayList<>());
        LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> dynamicMap = new LinkedHashMap();
        dynamicMap.put(logicalExpression, asList(new PCCToRadiusNonGroupAVPExpressionMapping("0:1", defaultCompiler.parseExpression("trim( CS.UserName )"), "trim( CS.UserName )", new HashMap<>(), null, radiusAttributeFactory)));
        RadiusChargingRuleDefinitionPacketMapping expected = new RadiusChargingRuleDefinitionPacketMapping(staticMap, dynamicMap);
        ReflectionAssert.assertLenientEquals(expected, actualChargingRuleDefinitionPacketMapping);
    }

    @Test
    public void createChargingRuleDefinitionPacketMappingForMultiplePCCRuleDataWithDifferentPCCRuleTypes() throws LoadConfigurationException, InvalidExpressionException {
        String nonGroupedMappingStr = "{\"id\":\"eba47b32-acb1-40ea-b4b1-1797ac2702fb\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policykey\":\"trim( CS.UserName )\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        String accessNetworkType = "access, network";

        RadiusGatewayProfileData radiusGatewayProfileData = createRadiusGatewayProfileDataForPCCRuleMapping(new String[]{nonGroupedMappingStr},
                accessNetworkType,
                "STATIC",
                "DYNAMIC");

        RadiusChargingRuleDefinitionPacketMapping actualChargingRuleDefinitionPacketMapping = pccRuleMappingFactory.create(radiusGatewayProfileData.getName(),
                radiusGatewayProfileData.getId(), radiusGatewayProfileData.getRadiusGwProfilePCCRuleMappings());

        LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> staticMap = new LinkedHashMap();

        staticMap.put(logicalExpression, asList(new PCCToRadiusNonGroupAVPExpressionMapping("0:1", defaultCompiler.parseExpression("trim( CS.UserName )"), "trim( CS.UserName )", new HashMap<>(), null, radiusAttributeFactory)));

        LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> dynamicMap = new LinkedHashMap();

        dynamicMap.put(logicalExpression, asList(new PCCToRadiusNonGroupAVPExpressionMapping("0:1", defaultCompiler.parseExpression("trim( CS.UserName )"), "trim( CS.UserName )", new HashMap<>(), null, radiusAttributeFactory)));

        RadiusChargingRuleDefinitionPacketMapping expected = new RadiusChargingRuleDefinitionPacketMapping(staticMap, dynamicMap);

        ReflectionAssert.assertLenientEquals(expected, actualChargingRuleDefinitionPacketMapping);
    }

    @Test
    public void testRuleMapKeyIsNullWhenAccessNetworkIsNotConfigured() throws LoadConfigurationException, InvalidExpressionException {
        String nonGroupedMappingStr = "{\"id\":\"eba47b32-acb1-40ea-b4b1-1797ac2702fb\",\"pid\":" +
                "\"0\",\"attribute\":\"0:1\",\"policykey\":\"trim( CS.UserName )\",\"defaultvalue\":\"\",\"valuemapping\":\"\"}";

        String accessNetworkType = null;

        RadiusGatewayProfileData radiusGatewayProfileData = createRadiusGatewayProfileDataForPCCRuleMapping(new String[]
                {nonGroupedMappingStr},
                accessNetworkType,
                "STATIC",
                "DYNAMIC");

        RadiusChargingRuleDefinitionPacketMapping actualChargingRuleDefinitionPacketMapping = pccRuleMappingFactory.create(radiusGatewayProfileData.getName(),
                radiusGatewayProfileData.getId(), radiusGatewayProfileData.getRadiusGwProfilePCCRuleMappings());

        LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> staticMap = new LinkedHashMap();

        staticMap.put(null, asList(new PCCToRadiusNonGroupAVPExpressionMapping("0:1", defaultCompiler.parseExpression("trim( CS.UserName )"), "trim( CS.UserName )", new HashMap<>(), null, radiusAttributeFactory)));

        LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> dynamicMap = new LinkedHashMap();

        dynamicMap.put(null, asList(new PCCToRadiusNonGroupAVPExpressionMapping("0:1", defaultCompiler.parseExpression("trim( CS.UserName )"), "trim( CS.UserName )", new HashMap<>(), null, radiusAttributeFactory)));

        RadiusChargingRuleDefinitionPacketMapping expected = new RadiusChargingRuleDefinitionPacketMapping(staticMap, dynamicMap);

        ReflectionAssert.assertLenientEquals(expected, actualChargingRuleDefinitionPacketMapping);
    }

    private RadiusGatewayProfileData createRadiusGatewayProfileDataForPCCRuleMapping (String[] mapping,
                                                                                      String accessNeworktype,
                                                                                      String ... types) {
        RadiusGatewayProfileData radiusGatewayProfileData = new RadiusGatewayProfileData();
        radiusGatewayProfileData.setName("DIA_GATEWAY");
        PCCRuleMappingData pccRuleMappingData = new PCCRuleMappingData();
        pccRuleMappingData.setName("PCCRuleMappingData");

        AttributeMappingData firstAttributeMappingData = createAttributeMappingData(types[0]);
        firstAttributeMappingData.setMappings(mapping);

        if (types[0].equals("STATIC")) {
            pccRuleMappingData.setStaticAttributeMappings(firstAttributeMappingData);
        } else {
            pccRuleMappingData.setDynamicAttributeMappings(firstAttributeMappingData);
        }

        if (types.length == 2) {
            AttributeMappingData secondAttributeMappingData = createAttributeMappingData(types[1]);
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
        radiusGwProfilePCCRuleMappingData.setCondition(accessNeworktype);
        radiusGatewayProfileData.setRadiusGwProfilePCCRuleMappings(asList(radiusGwProfilePCCRuleMappingData));
        radiusGatewayProfileData.setId(UUID.randomUUID().toString());
        return radiusGatewayProfileData;
    }

    private AttributeMappingData createAttributeMappingData(String type) {
        AttributeMappingData attributeMappingData = new AttributeMappingData();
        attributeMappingData.setId(UUID.randomUUID().toString());
        attributeMappingData.setType(type);
        return attributeMappingData;
    }
}
