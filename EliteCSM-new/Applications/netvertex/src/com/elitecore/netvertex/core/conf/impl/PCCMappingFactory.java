package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.corenetvertex.constants.ChargingRuleInstallMode;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.sm.gateway.AttributeMappingData;
import com.elitecore.corenetvertex.sm.gateway.DiameterGwProfilePCCRuleMappingData;
import com.elitecore.corenetvertex.sm.gateway.PCCRuleMappingData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.ChargingRuleDefinitionPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterMappingFactory;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterPacketMapping;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class PCCMappingFactory {

	private static final String ALWAYS_TRUE_EXPRESSION = "\"1\"=\"1\"";
	private static final String MODULE = "PCC-MAPPING-FCTRY";
	private static final String ROOT_PARENT_ID = "0";
	private PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory;
	private Compiler compiler;

	public PCCMappingFactory(PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory, Compiler compiler) {
		this.pcrfToDiameterMappingFactory = pcrfToDiameterMappingFactory;
		this.compiler = compiler;
	}
	
	public ChargingRuleDefinitionPacketMapping create(String profileName,
													  String profileId,
													  ChargingRuleInstallMode chargingRuleInstallMode,
													  List<DiameterGwProfilePCCRuleMappingData> diameterGwProfilePCCRuleMappings)
			throws LoadConfigurationException {


		LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> staticRuleMapping = new LinkedHashMap<>();
		LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> dynamicRuleMapping = new LinkedHashMap<>();


		addMappingFromConfig(profileName, profileId, chargingRuleInstallMode, diameterGwProfilePCCRuleMappings, staticRuleMapping, dynamicRuleMapping);

		addDefaultMapping(profileName, chargingRuleInstallMode, staticRuleMapping, dynamicRuleMapping);

		return new ChargingRuleDefinitionPacketMapping(staticRuleMapping, dynamicRuleMapping);
	}

	private void addMappingFromConfig(String profileName,
									  String profileId,
									  ChargingRuleInstallMode chargingRuleInstallMode,
									  List<DiameterGwProfilePCCRuleMappingData> diameterGwProfilePCCRuleMappings,
									  LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> staticRuleMapping,
									  LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> dynamicRuleMapping)
			throws LoadConfigurationException {

		if(CollectionUtils.isEmpty(diameterGwProfilePCCRuleMappings)) {
			return;
		}


		for (DiameterGwProfilePCCRuleMappingData gwProfilePccRuleMappingData : diameterGwProfilePCCRuleMappings) {

			PCCRuleMappingData pccRuleMappingData = gwProfilePccRuleMappingData.getPccRuleMappingData();

			LogicalExpression expression;

			String accessNetworkStr = gwProfilePccRuleMappingData.getCondition();

			if (Strings.isNullOrBlank(accessNetworkStr)) {
				accessNetworkStr = ALWAYS_TRUE_EXPRESSION;
			}

			try {
				expression = compiler.parseLogicalExpression(accessNetworkStr);
			} catch (InvalidExpressionException invalidExpEx) {
				getLogger().warn(MODULE, "Error while evaluting expression for profile id : " + profileId + " and name : " + profileName
						+ ", skipping this package. Reason: " + invalidExpEx.getMessage());
				getLogger().trace(invalidExpEx);
				continue;
			}

			ArrayList<PCRFToDiameterPacketMapping> staticPCCRuleMappings = new ArrayList<>();
			AttributeMappingData staticAttributeMappings = pccRuleMappingData.getStaticAttributeMappings();

			if (Objects.nonNull(staticAttributeMappings)) {
				String[] staticMappings = staticAttributeMappings.getMappings();

				Map<String, PacketMappingData> tempIdToPacketMappingDatas = new HashMap<>();
				List<PacketMappingData> rootPacketMappingDatas = new ArrayList<>();

				createRootPacketMappingDatas(staticMappings, tempIdToPacketMappingDatas, rootPacketMappingDatas);

				for (PacketMappingData rootPacketMapping : rootPacketMappingDatas) {

					try {
						PCRFToDiameterPacketMapping attributeMapping = pcrfToDiameterMappingFactory.create(rootPacketMapping,
								chargingRuleInstallMode,
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

			ArrayList<PCRFToDiameterPacketMapping> dynamicPCCRuleMappings = new ArrayList<>();
			AttributeMappingData dynamicAttributeMappings = pccRuleMappingData.getDynamicAttributeMappings();

			if (Objects.nonNull(dynamicAttributeMappings)) {
				String[] dynamicMappings = dynamicAttributeMappings.getMappings();

				Map<String, PacketMappingData> tempIdToPacketMappingDatas = new HashMap<>();
				List<PacketMappingData> rootPacketMappingDatas = new ArrayList<>();

				createRootPacketMappingDatas(dynamicMappings, tempIdToPacketMappingDatas, rootPacketMappingDatas);

				for (PacketMappingData rootPacketMapping : rootPacketMappingDatas) {

					try {
						PCRFToDiameterPacketMapping attributeMapping = pcrfToDiameterMappingFactory.create(rootPacketMapping, chargingRuleInstallMode, null);

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

	private void addDefaultMapping(String profileName,
								   ChargingRuleInstallMode chargingRuleInstallMode,
								   LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> staticRuleMapping,
								   LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> dynamicRuleMapping)
			throws LoadConfigurationException {

		try {
            PacketMappingData packetMappingForStaticRule = createPacketMapping(DiameterAVPConstants.TGPP_CHARGING_RULE_NAME, PCRFKeyConstants.PCC_RULE_NAME.val, null, null);

			staticRuleMapping.put(compiler.parseLogicalExpression(ALWAYS_TRUE_EXPRESSION),
                    Arrays.asList(pcrfToDiameterMappingFactory.create(packetMappingForStaticRule, chargingRuleInstallMode, null)));

            PacketMappingData packetMappingForDynamicRule = createPacketMappingForDynamicRule();

            dynamicRuleMapping.put(compiler.parseLogicalExpression(ALWAYS_TRUE_EXPRESSION),
                    Arrays.asList(pcrfToDiameterMappingFactory.create(packetMappingForDynamicRule, chargingRuleInstallMode, null)));

        } catch (InvalidExpressionException e) {
            throw new LoadConfigurationException("Error while reading default dynamic and static pcc rule mappings for gateway profile name : "+ profileName +" configuration. Reason: " + e.getMessage(), e);
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

	private PacketMappingData createPacketMappingForDynamicRule() {
		PacketMappingData rootMapping = createPacketMapping(DiameterAVPConstants.TGPP_CHARGING_RULE_DEFINITION,
				null, null, null);
		rootMapping.addChildMapping(createPacketMapping("0:432", PCRFKeyConstants.PCC_RULE_CHARGING_KEY.val,
				null, null));
		rootMapping.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_CHARGING_RULE_NAME, PCRFKeyConstants.PCC_RULE_NAME.val,
				null, null));

		PacketMappingData parentMapping1 = createPacketMapping(DiameterAVPConstants.TGPP_QOS_INFORMATION, null, null, null);
		parentMapping1.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_QCI, PCRFKeyConstants.IPCAN_QCI.val, null, null));
		parentMapping1.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_GUARANTEED_BR_DL, PCRFKeyConstants.PCC_RULE_GBRDL.val,
				null, null));
		parentMapping1.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_GUARANTEED_BR_UL, PCRFKeyConstants.PCC_RULE_GBRUL.val,
				null, null));
		parentMapping1.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_MAX_REQUESTED_BW_DL, PCRFKeyConstants.PCC_RULE_MBRDL.val,
				null, null));
		parentMapping1.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_MAX_REQUESTED_BW_UL, PCRFKeyConstants.PCC_RULE_MBRUL.val,
				null, null));
		PacketMappingData arpMapping = createPacketMapping(DiameterAVPConstants.TGPP_ALLOCATION_RETENTION_PRIORIY, null, null, null);
		arpMapping.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_PRIORIY_LEVEL, PCRFKeyConstants.IPCAN_PRIORITY_LEVEL.val, null, null));
		arpMapping.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_PRE_EMPTION_CAPABILITY, PCRFKeyConstants.IPCAN_PREEMPTION_CAPABILITY.val,
				null, null));
		arpMapping.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_PRE_EMPTION_VALNERABILITY, PCRFKeyConstants.IPCAN_PREEMPTION_VULNERABILITY.val,
				null, null));
		parentMapping1.addChildMapping(arpMapping);
		rootMapping.addChildMapping(parentMapping1);
		rootMapping.addChildMapping(createPacketMapping("10415:1010", PCRFKeyConstants.PCC_RULE_PRECEDENCE.val, null, null));
		rootMapping.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_USAGE_MONITORING_KEY, PCRFKeyConstants.PCC_RULE_MONITORING_KEY.val,
				null, null));
		rootMapping.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_ONLINE_CHARGING, PCRFKeyConstants.PCC_RULE_ONLINE_CHARGING.val,
				null, null));
		rootMapping.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_OFFLINE_CHARGING, PCRFKeyConstants.PCC_RULE_OFFLINE_CHARGING.val,
				null, null));
		rootMapping.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_FLOW_STATUS, PCRFKeyConstants.PCC_RULE_FLOW_STATUS.val,
				null, null));
		PacketMappingData parentMapping2 = createPacketMapping(DiameterAVPConstants.TGPP_FLOW_INFORMATION, null, null, null);
		parentMapping2.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_FLOW_DESCRIPTION, PCRFKeyConstants.PCC_RULE_SERVICE_DATA_FLOW.val,
				null, null));
		rootMapping.addChildMapping(parentMapping2);

		return rootMapping;
	}

	private PacketMappingData createPacketMapping(String attributeId, String policyKey, String defaultVal, String valMapping) {
		PacketMappingData mapping1 = new PacketMappingData();
		mapping1.setAttribute(attributeId);
		mapping1.setPolicyKey(policyKey);
		mapping1.setDefaultValue(defaultVal);
		mapping1.setValueMapping(valMapping);
		return mapping1;
	}
}
