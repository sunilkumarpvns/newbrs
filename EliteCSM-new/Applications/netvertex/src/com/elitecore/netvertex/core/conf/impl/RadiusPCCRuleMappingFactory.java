package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.sm.gateway.AttributeMappingData;
import com.elitecore.corenetvertex.sm.gateway.PCCRuleMappingData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGwProfilePCCRuleMappingData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.symbol.impl.Operator;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMappingFactory;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusChargingRuleDefinitionPacketMapping;
import com.elitecore.netvertex.gateway.radius.utility.PCCToRadiusPacketMapping;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RadiusPCCRuleMappingFactory {
    private static final String MODULE = "RAD-TO-PCC-MAPPING-FCTRY";
    private static final String ROOT_PARENT_ID = "0";
    private PCCToRadiusMappingFactory pccToRadiusMappingFactory;
    private Compiler compiler;
    private Splitter splitter = Splitter.on(',').trimTokens();

    public RadiusPCCRuleMappingFactory(PCCToRadiusMappingFactory pccToRadiusMappingFactory, Compiler compiler) {
        this.pccToRadiusMappingFactory = pccToRadiusMappingFactory;
        this.compiler = compiler;
    }

    public RadiusChargingRuleDefinitionPacketMapping create(String profileName,
                                                            String profileId,
                                                            List<RadiusGwProfilePCCRuleMappingData> radiusGwProfilePCCRuleMappings)
            throws LoadConfigurationException {


        LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> staticRuleMapping = new LinkedHashMap<>();
        LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> dynamicRuleMapping = new LinkedHashMap<>();


        addMappingFromConfig(profileName, profileId, radiusGwProfilePCCRuleMappings, staticRuleMapping, dynamicRuleMapping);

        return new RadiusChargingRuleDefinitionPacketMapping(staticRuleMapping, dynamicRuleMapping);
    }

    private void addMappingFromConfig(String profileName, String profileId,
                                      List<RadiusGwProfilePCCRuleMappingData> radiusGwProfilePCCRuleMappings,
                                      LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> staticRuleMapping,
                                      LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> dynamicRuleMapping) throws LoadConfigurationException {

        if(CollectionUtils.isEmpty(radiusGwProfilePCCRuleMappings)) {
            return;
        }


        ArrayList<PCCToRadiusPacketMapping> staticPCCRuleMappings = new ArrayList<>();

        for (RadiusGwProfilePCCRuleMappingData gwProfilePccRuleMappingData : radiusGwProfilePCCRuleMappings) {

            PCCRuleMappingData pccRuleMappingData = gwProfilePccRuleMappingData.getPccRuleMappingData();

            LogicalExpression expression;

            try {
                expression = createExpression(gwProfilePccRuleMappingData);
            } catch (InvalidExpressionException invalidExpEx) {
                getLogger().warn(MODULE, "Error while evaluting expression for profile id : " + profileId + " and name : " + profileName
                        + ", skipping this package. Reason: " + invalidExpEx.getMessage());
                getLogger().trace(invalidExpEx);
                continue;
            }

            AttributeMappingData staticAttributeMappings = pccRuleMappingData.getStaticAttributeMappings();


            if (Objects.nonNull(staticAttributeMappings)) {

                String[] staticMappings = staticAttributeMappings.getMappings();

                Map<String, PacketMappingData> tempIdToPacketMappingDatas = new HashMap<>();

                List<PacketMappingData> rootPacketMappingDatas = new ArrayList<>();

                createRootPacketMappingDatas(staticMappings, tempIdToPacketMappingDatas, rootPacketMappingDatas);

                for (PacketMappingData rootPacketMapping : rootPacketMappingDatas) {

                    try {
                        PCCToRadiusPacketMapping attributeMapping = pccToRadiusMappingFactory.create(rootPacketMapping,
                                null);

                        if (attributeMapping != null) {
                            staticPCCRuleMappings.add(attributeMapping);
                        }

                    } catch (Exception e) {
                        LogManager.getLogger().error(MODULE, "Error while reading PCCRule Mappings in diameter gateway profile : " + profileName
                                + " . Reason : " + e.getMessage() + ". So skipping this attribute.");
                        LogManager.getLogger().trace(e);
                    }
                }
            }

            staticRuleMapping.put(expression, staticPCCRuleMappings);

            ArrayList<PCCToRadiusPacketMapping> dynamicPCCRuleMappings = new ArrayList<>();

            AttributeMappingData dynamicAttributeMappings = pccRuleMappingData.getDynamicAttributeMappings();

            if (Objects.nonNull(dynamicAttributeMappings)) {

                String[] dynamicMappings = dynamicAttributeMappings.getMappings();

                Map<String, PacketMappingData> tempIdToPacketMappingDatas = new HashMap<>();
                List<PacketMappingData> rootPacketMappingDatas = new ArrayList<>();

                createRootPacketMappingDatas(dynamicMappings, tempIdToPacketMappingDatas, rootPacketMappingDatas);

                for (PacketMappingData rootPacketMapping : rootPacketMappingDatas) {

                    try {
                        PCCToRadiusPacketMapping attributeMapping = pccToRadiusMappingFactory.create(rootPacketMapping, null);

                        if (attributeMapping != null) {
                            dynamicPCCRuleMappings.add(attributeMapping);
                        }
                    } catch (Exception e) {
                        LogManager.getLogger().error(MODULE, "Error while reading PCCRule Mappings in diameter gateway profile : " + profileName
                                + " . Reason : " + e.getMessage() + ". So skipping this attribute.");
                        LogManager.getLogger().trace(e);
                    }
                }
            }

            dynamicRuleMapping.put(expression, dynamicPCCRuleMappings);
        }
    }

    private void createRootPacketMappingDatas(String[] mappings,
                                              Map<String, PacketMappingData> tempIdToPacketMappingDatas,
                                              List<PacketMappingData> rootPacketMappingDatas)
            throws LoadConfigurationException {

        for (String mapping : mappings) {
            if (Strings.isNullOrBlank(mapping) == false) {
                PacketMappingData packetMappingData1 = GsonFactory.defaultInstance().fromJson(mapping, PacketMappingData.class);
                tempIdToPacketMappingDatas.put(packetMappingData1.getId(), packetMappingData1);
            } else {
                break;
            }
        }

        for (PacketMappingData packetMappingData1 : tempIdToPacketMappingDatas.values()) {
            if (packetMappingData1.getParentId().equals(ROOT_PARENT_ID)) {
                rootPacketMappingDatas.add(packetMappingData1);
            } else {
                PacketMappingData parentMapping = tempIdToPacketMappingDatas.get(packetMappingData1.getParentId());
                if (parentMapping == null)  {
                    throw new LoadConfigurationException("Parent Mapping not found for mapping: " + packetMappingData1.getId());
                }
                parentMapping.addChildMapping(packetMappingData1);
            }
        }
    }

    public LogicalExpression createExpression(RadiusGwProfilePCCRuleMappingData gwProfilePccRuleMappingData) throws InvalidExpressionException{
        String accessNetworkStr = gwProfilePccRuleMappingData.getCondition();
        if (Strings.isNullOrBlank(accessNetworkStr) == false) {

            List<String> accessNetworks = parse(accessNetworkStr);
            if (accessNetworks.isEmpty() == false) {


                StringBuilder expStr = new StringBuilder(PCRFKeyConstants.CS_ACCESS_NETWORK.val)
                        .append(" ")
                        .append(Operator.IN.getOperatorType())
                        .append(CommonConstants.OPENING_PARENTHESES)
                        .append(CommonConstants.DOUBLE_QUOTES)
                        .append(accessNetworks.get(0))
                        .append(CommonConstants.DOUBLE_QUOTES);

                for (int i = 1; i < accessNetworks.size(); i++) {
                    expStr.append(CommonConstants.COMMA)
                            .append(CommonConstants.DOUBLE_QUOTES)
                            .append(accessNetworks.get(i))
                            .append(CommonConstants.DOUBLE_QUOTES);
                }

                expStr.append(CommonConstants.CLOSING_PARENTHESES);

                return compiler.parseLogicalExpression(expStr.toString());
            }
        }
        return null;
    }

    private List<String> parse(String accessNetworkStr) {
        return splitter.split(accessNetworkStr);

    }
}
