package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.corenetvertex.constants.ChargingRuleInstallMode;
import com.elitecore.corenetvertex.constants.ConversionType;
import com.elitecore.corenetvertex.constants.PacketType;
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
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.ChargingRuleDefinitionPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.UMBuilder;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterMappingFactory;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterPacketMapping;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class DiameterPacketMappingFactory {

	private static final Compiler DEFAULT_COMPILER = Compiler.getDefaultCompiler();
	private static final String MODULE = "PCKT-MAPPING-FCTRY";
	public static final String ROOT_PARENT_ID = "0";

	private PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory;
	private DiameterToPCCMappingFactory diameterToPCCMappingFactory;
	private Map<ApplicationPacketType, PCCToDiameterMapping> pccToDiameterMappings;
	private Map<ApplicationPacketType, DiameterToPCCMapping> diameterToPCCMappings;

	public DiameterPacketMappingFactory(PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory,
										DiameterToPCCMappingFactory diameterToPCCMappingFactory) {
		this.pcrfToDiameterMappingFactory = pcrfToDiameterMappingFactory;
		this.diameterToPCCMappingFactory = diameterToPCCMappingFactory;
	}

	public void create(DiameterGatewayProfileData gatewayProfileData,
                       ChargingRuleDefinitionPacketMapping pccRuleMappings, UMBuilder umBuilder) throws LoadConfigurationException {

		if(getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reading request response mapping for Diameter gateway profile with profile = " + gatewayProfileData.getName());
		}

		Map<ApplicationPacketType, LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>>> pccToDiameterPacketMappingMap = new EnumMap<>(ApplicationPacketType.class);
		Map<ApplicationPacketType, LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>>> diameterToPCCPacketMappingMap = new EnumMap<>(ApplicationPacketType.class);

		List<DiameterGwProfilePacketMapData> pccToGWGxPacketMappings = gatewayProfileData.getPccToGWGxPacketMappings();
		List<DiameterGwProfilePacketMapData> gwToPccGxPacketMappings = gatewayProfileData.getGwToPccGxPacketMappings();
		List<DiameterGwProfilePacketMapData> pccToGWGyPacketMappings = gatewayProfileData.getPccToGWGyPacketMappings();
		List<DiameterGwProfilePacketMapData> gwToPccGyPacketMappings = gatewayProfileData.getGwToPccGyPacketMappings();
		List<DiameterGwProfilePacketMapData> pccToGWRxPacketMappings = gatewayProfileData.getPccToGWRxPacketMappings();
		List<DiameterGwProfilePacketMapData> gwToPccRxPacketMappings = gatewayProfileData.getGwToPccRxPacketMappings();

		if (Collectionz.isNullOrEmpty(pccToGWGxPacketMappings) == false) {
			createConfiguredMappings(gatewayProfileData, pccToGWGxPacketMappings, pccRuleMappings, pccToDiameterPacketMappingMap, diameterToPCCPacketMappingMap);
		}

		if (Collectionz.isNullOrEmpty(gwToPccGxPacketMappings) == false) {
			createConfiguredMappings(gatewayProfileData, gwToPccGxPacketMappings, pccRuleMappings, pccToDiameterPacketMappingMap, diameterToPCCPacketMappingMap);
		}

		if (Collectionz.isNullOrEmpty(gwToPccGyPacketMappings) == false) {
			createConfiguredMappings(gatewayProfileData, gwToPccGyPacketMappings, pccRuleMappings, pccToDiameterPacketMappingMap, diameterToPCCPacketMappingMap);
		}

		if (Collectionz.isNullOrEmpty(pccToGWGyPacketMappings) == false) {
			createConfiguredMappings(gatewayProfileData, pccToGWGyPacketMappings, pccRuleMappings, pccToDiameterPacketMappingMap, diameterToPCCPacketMappingMap);
		}

		if (Collectionz.isNullOrEmpty(pccToGWRxPacketMappings) == false) {
			createConfiguredMappings(gatewayProfileData, pccToGWRxPacketMappings, pccRuleMappings, pccToDiameterPacketMappingMap, diameterToPCCPacketMappingMap);
		}

		if (Collectionz.isNullOrEmpty(gwToPccRxPacketMappings) == false) {
			createConfiguredMappings(gatewayProfileData, gwToPccRxPacketMappings, pccRuleMappings, pccToDiameterPacketMappingMap, diameterToPCCPacketMappingMap);
		}

		createMappings(diameterToPCCPacketMappingMap, pccToDiameterPacketMappingMap, gatewayProfileData, pccRuleMappings, umBuilder);
	}

	private void createConfiguredMappings(DiameterGatewayProfileData diameterGatewayProfileData,
										  List<DiameterGwProfilePacketMapData> diameterGwProfilePacketMappings,
										  ChargingRuleDefinitionPacketMapping pccRuleMappings,
										  Map<ApplicationPacketType, LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>>> pccToDiameterPacketMappingMap,
										  Map<ApplicationPacketType, LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>>> diameterToPCCPacketMappingMap) throws LoadConfigurationException {
		for (DiameterGwProfilePacketMapData diameterGWPacketMappingData : diameterGwProfilePacketMappings) {

            com.elitecore.corenetvertex.sm.gateway.PacketMappingData packetMappingData = diameterGWPacketMappingData.getPacketMappingData();
            LogicalExpression logicalExpression;

            try {
				String condition = diameterGWPacketMappingData.getCondition();

				if (Strings.isNullOrBlank(condition)) {
					condition = "\"1\"=\"1\"";
				}

				logicalExpression = DEFAULT_COMPILER.parseLogicalExpression(condition);

            } catch (InvalidExpressionException e1) {
                getLogger().error(MODULE, "Skip packet mapping with invalid condition = " + diameterGWPacketMappingData.getCondition() + " configured in"
                        + " gateway profile: " + diameterGatewayProfileData.getName());
                getLogger().trace(MODULE, e1);
                continue;
            }

            ApplicationPacketType applicationPacketType = ApplicationPacketType.fromString(diameterGWPacketMappingData.getApplicationType()
                    , PacketType.valueOf(packetMappingData.getPacketType()).getPacketType());

            try {

                if (applicationPacketType.getConversionType() == ConversionType.GATEWAY_TO_PCC) {
					createGatewayToPccMapping(diameterToPCCPacketMappingMap, packetMappingData, logicalExpression, applicationPacketType);
                } else {
                    createPccToGatewayMapping(diameterGatewayProfileData, pccRuleMappings, pccToDiameterPacketMappingMap, packetMappingData, logicalExpression, applicationPacketType);
                }

            } catch (InvalidExpressionException ex) {
				getLogger().error(MODULE, "Skip packet mapping with invalid mapping configured in"
						+ " gateway profile: " + diameterGatewayProfileData.getName());
				getLogger().trace(MODULE, ex);

                continue;
            }

        }
	}

	private void createPccToGatewayMapping(DiameterGatewayProfileData data,
										   ChargingRuleDefinitionPacketMapping pccRuleMappings,
										   Map<ApplicationPacketType, LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>>> pccToDiameterPacketMappingMap,
										   com.elitecore.corenetvertex.sm.gateway.PacketMappingData packetMappingData,
										   LogicalExpression logicalExpression,
										   ApplicationPacketType applicationPacketType) throws InvalidExpressionException, LoadConfigurationException {
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
				if (parentMapping == null)  {
					throw new LoadConfigurationException("Parent Mapping not found for mapping: " + packetMappingData1.getId());
				}
				parentMapping.addChildMapping(packetMappingData1);
			}
		}

		LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> appToExpToMappings = pccToDiameterPacketMappingMap.get(applicationPacketType);
		if (appToExpToMappings== null) {
			appToExpToMappings = new LinkedHashMap<>();
			pccToDiameterPacketMappingMap.put(applicationPacketType, appToExpToMappings);
		}

		List<PCRFToDiameterPacketMapping> pcrfToDiameterPacketMappings = appToExpToMappings.get(logicalExpression);

		if (pcrfToDiameterPacketMappings == null) {
			pcrfToDiameterPacketMappings =  new ArrayList<>();
			appToExpToMappings.put(logicalExpression, pcrfToDiameterPacketMappings);
		}


		for (PacketMappingData packetMappingData1 : rootPacketMappingDatas) {
			PCRFToDiameterPacketMapping pcrfToDiameterPacketMapping = pcrfToDiameterMappingFactory.create(packetMappingData1, ChargingRuleInstallMode.fromValue(data.getChargingRuleInstallMode()), pccRuleMappings);
			pcrfToDiameterPacketMappings.add(pcrfToDiameterPacketMapping);
		}
	}

	private void createGatewayToPccMapping(Map<ApplicationPacketType, LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>>> diameterToPCCPacketMappingMap,
										   com.elitecore.corenetvertex.sm.gateway.PacketMappingData packetMappingData,
										   LogicalExpression logicalExpression,
										   ApplicationPacketType applicationPacketType) throws InvalidExpressionException {

		AttributeMappingData attributeMappingData = packetMappingData.getAttributeMappingData();

		String[] mappings = attributeMappingData.getMappings();

		LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>> appToExpToMappings = diameterToPCCPacketMappingMap.computeIfAbsent(applicationPacketType, applicationPacketType1 -> new LinkedHashMap<>());
		List<DiameterToPCCPacketMapping> diameterToPCCPacketMappings = appToExpToMappings.computeIfAbsent(logicalExpression, expression -> new ArrayList<>());

		if(ArrayUtils.isEmpty(mappings)) {
			return;
		}

		for (String mapping : mappings) {
			if (Strings.isNullOrBlank(mapping) == false) {
				PacketMappingData data = GsonFactory.defaultInstance().fromJson(mapping, PacketMappingData.class);
				DiameterToPCCPacketMapping diameterToPCCPacketMapping = diameterToPCCMappingFactory.create(data);
				diameterToPCCPacketMappings.add(diameterToPCCPacketMapping);
			} else {
				break;
			}
		}
	}

	private void createMappings(
			Map<ApplicationPacketType, LinkedHashMap<LogicalExpression, List<DiameterToPCCPacketMapping>>> configuredDiameterToPCCPacketMapping,
			Map<ApplicationPacketType, LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>>> configuredPCCToDiameterPacketMappingMap,
			DiameterGatewayProfileData gatewayProfileData,
			ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping, UMBuilder umBuilder) {

		Map<ApplicationPacketType, DiameterToPCCMapping> tempDiameterToPCCMappings = new EnumMap<>(ApplicationPacketType.class);
		Map<ApplicationPacketType, PCCToDiameterMapping> tempPCCToDiameterMappings = new EnumMap<>(ApplicationPacketType.class);


		for(ApplicationPacketType applicationPacketType : ApplicationPacketType.getGatwayToPccAppPacketType()) {
			DiameterToPCCMapping diameterToPccMapping = applicationPacketType.createDiameterToPCCMapping(configuredDiameterToPCCPacketMapping.get(applicationPacketType), gatewayProfileData, umBuilder);
			tempDiameterToPCCMappings.put(applicationPacketType, diameterToPccMapping);
		}

		for(ApplicationPacketType applicationPacketType : ApplicationPacketType.getPccToGatetypeAppPacketType()) {
			PCCToDiameterMapping pccToDiameterMapping = applicationPacketType.createPCCToDiameterMapping(configuredPCCToDiameterPacketMappingMap.get(applicationPacketType), pcrfToDiameterMappingFactory, gatewayProfileData, chargingRuleDefinitionPacketMapping, umBuilder);
			tempPCCToDiameterMappings.put(applicationPacketType, pccToDiameterMapping);
		}
		
		this.diameterToPCCMappings = tempDiameterToPCCMappings;
		this.pccToDiameterMappings = tempPCCToDiameterMappings;
		
	}

	public Map<ApplicationPacketType, PCCToDiameterMapping> getPCCToDiameterMappings(){
		return pccToDiameterMappings;
	}
	
	public Map<ApplicationPacketType, DiameterToPCCMapping> getDiameterToPCCMappings(){
		return diameterToPCCMappings;
	}

}
