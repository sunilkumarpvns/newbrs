package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.corenetvertex.constants.ConversionType;
import com.elitecore.corenetvertex.sm.gateway.AttributeMappingData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGatewayProfileData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGwProfilePacketMapData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMapping;
import com.elitecore.netvertex.gateway.radius.mapping.PCCToRadiusMappingFactory;
import com.elitecore.netvertex.gateway.radius.mapping.RadApplicationPacketType;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusChargingRuleDefinitionPacketMapping;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCMapping;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCMappingFactory;
import com.elitecore.netvertex.gateway.radius.utility.PCCToRadiusPacketMapping;
import com.elitecore.netvertex.gateway.radius.utility.RadiusToPCCPacketMapping;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RadiusPacketMappingFactory {
    private static final Compiler DEFAULT_COMPILER = Compiler.getDefaultCompiler();
    private static final String MODULE = "PCKT-MAPPING-FCTRY";
    public static final String ROOT_PARENT_ID = "0";

    private PCCToRadiusMappingFactory pccToRadiusMappingFactory;
    private RadiusToPCCMappingFactory radiusToPCCMappingFactory;
    private Map<RadApplicationPacketType, PCCToRadiusMapping> pccToRADIUSMappings;
    private Map<RadApplicationPacketType, RadiusToPCCMapping> radiusToPCCMappings;

    public RadiusPacketMappingFactory(PCCToRadiusMappingFactory pccToRadiusMappingFactory,
                                      RadiusToPCCMappingFactory radiusToPCCMappingFactory) {
        this.pccToRadiusMappingFactory = pccToRadiusMappingFactory;
        this.radiusToPCCMappingFactory = radiusToPCCMappingFactory;
        this.pccToRADIUSMappings = Maps.newHashMap();
		this.radiusToPCCMappings = Maps.newHashMap();
    }

    public void create(RadiusGatewayProfileData gatewayProfileData,
                       List<RadiusGwProfilePacketMapData> radiusGwProfilePacketMappings,
                       RadiusChargingRuleDefinitionPacketMapping pccRuleMappings) throws LoadConfigurationException {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Reading request response mapping for RADIUS gateway profile with profile = " + gatewayProfileData.getName());
        }

        Map<RadApplicationPacketType, LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>>> pccToRADIUSPacketMappingMap = new EnumMap<>(RadApplicationPacketType.class);
        Map<RadApplicationPacketType, LinkedHashMap<LogicalExpression, List<RadiusToPCCPacketMapping>>> radiusToPCCPacketMappingMap = new EnumMap<>(RadApplicationPacketType.class);

		if(Collectionz.isNullOrEmpty(radiusGwProfilePacketMappings) == false) {
            createConfiguredMappings(gatewayProfileData, radiusGwProfilePacketMappings, pccRuleMappings, pccToRADIUSPacketMappingMap, radiusToPCCPacketMappingMap);
            createMappings(radiusToPCCPacketMappingMap, pccToRADIUSPacketMappingMap, gatewayProfileData, pccRuleMappings);
		}

		return;
    }

    private void createConfiguredMappings(RadiusGatewayProfileData radiusGatewayProfileData,
                                          List<RadiusGwProfilePacketMapData> radiusGwProfilePacketMapDatas,
                                          RadiusChargingRuleDefinitionPacketMapping pccRuleMappings,
                                          Map<RadApplicationPacketType, LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>>> pccToRADIUSPacketMappingMap,
                                          Map<RadApplicationPacketType, LinkedHashMap<LogicalExpression, List<RadiusToPCCPacketMapping>>> radiusToPCCPacketMappingMap) throws LoadConfigurationException {
        for (RadiusGwProfilePacketMapData radiusGWPacketMappingData : radiusGwProfilePacketMapDatas) {

            com.elitecore.corenetvertex.sm.gateway.PacketMappingData packetMappingData = radiusGWPacketMappingData.getPacketMappingData();
            LogicalExpression logicalExpression = null;

            try {
                String condition = radiusGWPacketMappingData.getCondition();

                if (Strings.isNullOrBlank(condition)) {
                    condition = "\"1\"=\"1\"";
                }

                logicalExpression = DEFAULT_COMPILER.parseLogicalExpression(condition);

            } catch (InvalidExpressionException e) {
                getLogger().error(MODULE, "Skipping packet mapping:" + radiusGWPacketMappingData.getPacketMappingData().getName() + ". Reason: Invalid condition = " + radiusGWPacketMappingData.getCondition() + " configured in"
                        + " gateway profile: " + radiusGatewayProfileData.getName());
                getLogger().trace(MODULE, e);
                continue;
            }


            RadApplicationPacketType radApplicationPacketType = RadApplicationPacketType.fromString(packetMappingData.getPacketType());

            try {

                if (radApplicationPacketType.getConversionType() == ConversionType.GATEWAY_TO_PCC) {
                    createGatewayToPccMapping(radiusToPCCPacketMappingMap, packetMappingData, logicalExpression, radApplicationPacketType);
                } else {
                    createPccToGatewayMapping(pccRuleMappings, pccToRADIUSPacketMappingMap, packetMappingData, logicalExpression, radApplicationPacketType);
                }

            } catch (InvalidExpressionException ex) {
                getLogger().error(MODULE, "Skip packet mapping with invalid mapping configured in"
                        + " gateway profile: " + radiusGatewayProfileData.getName());
                getLogger().trace(MODULE, ex);

                continue;
            }

        }
    }

    private void createPccToGatewayMapping(RadiusChargingRuleDefinitionPacketMapping pccRuleMappings,
                                           Map<RadApplicationPacketType, LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>>> pccToRADIUSPacketMappingMap,
                                           com.elitecore.corenetvertex.sm.gateway.PacketMappingData packetMappingData,
                                           LogicalExpression logicalExpression,
                                           RadApplicationPacketType radApplicationPacketType) throws InvalidExpressionException, LoadConfigurationException {
        AttributeMappingData attributeMappingData = packetMappingData.getAttributeMappingData();

        String[] mappings = attributeMappingData.getMappings();

        Map<String, PacketMappingData> tempIdToPacketMappingDatas = new HashMap<>();
        List<PacketMappingData> rootPacketMappingDatas = new ArrayList<>();

        if(ArrayUtils.isEmpty(mappings)) {
            return;
        }

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
                if (parentMapping == null) {
                    throw new LoadConfigurationException("Parent Mapping not found for mapping: " + packetMappingData1.getId());
                }
                parentMapping.addChildMapping(packetMappingData1);
            }
        }

        LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> appToExpToMappings = pccToRADIUSPacketMappingMap.computeIfAbsent(radApplicationPacketType,
                applicationPacketType1 -> new LinkedHashMap<>());

        List<PCCToRadiusPacketMapping> pccToRADIUSPacketMappings = appToExpToMappings.computeIfAbsent(logicalExpression, expression -> new ArrayList<>());


        for (PacketMappingData packetMappingData1 : rootPacketMappingDatas) {
            PCCToRadiusPacketMapping pccToRadiusPacketMapping = pccToRadiusMappingFactory.create(packetMappingData1, pccRuleMappings);
            pccToRADIUSPacketMappings.add(pccToRadiusPacketMapping);
        }
    }

    private void createGatewayToPccMapping(Map<RadApplicationPacketType, LinkedHashMap<LogicalExpression, List<RadiusToPCCPacketMapping>>> radiusToPCCPacketMappingMap,
                                           com.elitecore.corenetvertex.sm.gateway.PacketMappingData packetMappingData,
                                           LogicalExpression logicalExpression,
                                           RadApplicationPacketType radApplicationPacketType) throws InvalidExpressionException {

        AttributeMappingData attributeMappingData = packetMappingData.getAttributeMappingData();

        String[] mappings = attributeMappingData.getMappings();

        LinkedHashMap<LogicalExpression, List<RadiusToPCCPacketMapping>> appToExpToMappings = radiusToPCCPacketMappingMap.computeIfAbsent(radApplicationPacketType,
                applicationPacketType1 -> new LinkedHashMap<>());
        List<RadiusToPCCPacketMapping> radiusToPCCPacketMappings = appToExpToMappings.computeIfAbsent(logicalExpression, expression -> new ArrayList<>());

        if(ArrayUtils.isEmpty(mappings)) {
            return;
        }

        for (String mapping : mappings) {
            if (Strings.isNullOrBlank(mapping) == false) {
                PacketMappingData data = GsonFactory.defaultInstance().fromJson(mapping, PacketMappingData.class);
                RadiusToPCCPacketMapping radiusToPCCPacketMapping = radiusToPCCMappingFactory.create(data);
                radiusToPCCPacketMappings.add(radiusToPCCPacketMapping);
            } else {
                break;
            }
        }
    }

    private void createMappings(
            Map<RadApplicationPacketType, LinkedHashMap<LogicalExpression, List<RadiusToPCCPacketMapping>>> configuredRADIUSToPCCPacketMapping,
            Map<RadApplicationPacketType, LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>>> configuredPCCToRADIUSPacketMappingMap,
            RadiusGatewayProfileData gatewayProfileData,
            RadiusChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping) {

        Map<RadApplicationPacketType, RadiusToPCCMapping> tempRADIUSToPCCMappings = new EnumMap<>(RadApplicationPacketType.class);
        Map<RadApplicationPacketType, PCCToRadiusMapping> tempPCCToRADIUSMappings = new EnumMap<>(RadApplicationPacketType.class);


        for (RadApplicationPacketType radApplicationPacketType : RadApplicationPacketType.getGatwayToPccAppPacketType()) {
            RadiusToPCCMapping radiusToPccMapping = radApplicationPacketType.createRadiusToPCCMapping(configuredRADIUSToPCCPacketMapping.get(radApplicationPacketType), gatewayProfileData);
            tempRADIUSToPCCMappings.put(radApplicationPacketType, radiusToPccMapping);
        }

        for (RadApplicationPacketType applicationPacketType : RadApplicationPacketType.getPccToGatetypeAppPacketType()) {
            PCCToRadiusMapping pccToRADIUSMapping = applicationPacketType.createPCCToRadiusMapping(configuredPCCToRADIUSPacketMappingMap.get(applicationPacketType), pccToRadiusMappingFactory, gatewayProfileData, chargingRuleDefinitionPacketMapping);
            tempPCCToRADIUSMappings.put(applicationPacketType, pccToRADIUSMapping);
        }

        this.radiusToPCCMappings = tempRADIUSToPCCMappings;
        this.pccToRADIUSMappings = tempPCCToRADIUSMappings;

    }

    public Map<RadApplicationPacketType, PCCToRadiusMapping> getPCCToRadiusMappings() {
        return pccToRADIUSMappings;
    }

    public Map<RadApplicationPacketType, RadiusToPCCMapping> getRadiusToPCCMappings() {
        return radiusToPCCMappings;
    }

}
