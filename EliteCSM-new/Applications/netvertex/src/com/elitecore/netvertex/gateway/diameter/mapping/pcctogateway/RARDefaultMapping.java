package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway;

import com.elitecore.corenetvertex.constants.ChargingRuleInstallMode;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayProfileData;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.core.conf.impl.PacketMappingData;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.ChargingRuleDefinitionPacketMapping;

import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RARDefaultMapping {

	private static final String MODULE = "RAR-DEF-MAPPING";
	private static final String DEFAULT_RAR = "DEFAULT_RAR";

	public static List<PCRFToDiameterPacketMapping> create(PCRFToDiameterMappingFactory pcrfToDiameterMappingFactory
															,DiameterGatewayProfileData gatewayProfileData,
														   ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping) {

		List<PCRFToDiameterPacketMapping> rarDefaultMappings = new ArrayList<>();
		ChargingRuleInstallMode chargingRuleInstallMode = ChargingRuleInstallMode.valueOf(gatewayProfileData.getChargingRuleInstallMode());
		PCRFToDiameterPacketMapping charingRuleMapping = null;
		PacketMappingData packetMappingForChargingRule = null;
		try{
			packetMappingForChargingRule = createPacketMapping(DiameterAVPConstants.TGPP_CHARGING_RULE_INSTALL, PCRFKeyConstants.PCC_RULE.val, null, null);
			charingRuleMapping = pcrfToDiameterMappingFactory.create(packetMappingForChargingRule, chargingRuleInstallMode, chargingRuleDefinitionPacketMapping);
		}catch (Exception e) {
			if (packetMappingForChargingRule != null) {
				getLogger().error(MODULE, " Error in parsing for packetMapping :" + DEFAULT_RAR + " , attributeid = "
                        + packetMappingForChargingRule.getAttribute() + " , policykey = " + packetMappingForChargingRule.getPolicyKey() + " ,Reason : " + e.getMessage());
			}
			getLogger().trace(MODULE, e);
		}
		
		if (charingRuleMapping != null) {
			rarDefaultMappings.add(charingRuleMapping);
		}

		PacketMappingData qosInformationPacketMappingData = createDefaultPacketMappingDataForQoSInformation();
		PCRFToDiameterPacketMapping qosInformationMapping = null;
		try {
			qosInformationMapping = pcrfToDiameterMappingFactory.create(qosInformationPacketMappingData, chargingRuleInstallMode, null);
		} catch (Exception e) {
			getLogger().error(MODULE, " Error in parsing for packetMapping :" + DEFAULT_RAR + " , attributeid = "
					+ qosInformationPacketMappingData.getAttribute() + " , policykey = " + qosInformationPacketMappingData.getPolicyKey() + ",Reason : " + e.getMessage());
			getLogger().trace(MODULE, e);
		}

		if (qosInformationMapping != null) {
			rarDefaultMappings.add(qosInformationMapping);
		}
		
		PCRFToDiameterPacketMapping eventTriggerMapping = null;
		try {
			
			long usageReportValue = DiameterDictionary.getInstance().getKeyFromValue(DiameterAVPConstants.TGPP_EVENT_TRIGGER, DiameterAttributeValueConstants.TGPP_EVENT_TRIGGER_USAGE_REPORT_STR);
			if (usageReportValue == DiameterDictionary.VALUE_NOT_FOUND) {
				getLogger().warn(MODULE, "Considering value of USAGE_REPORT event-trigger as " + DiameterAttributeValueConstants.TGPP_R11_EVENT_TRIGGER_USAGE_REPORT_ID + ". Reason:value not found from dictionary");
				usageReportValue = DiameterAttributeValueConstants.TGPP_R11_EVENT_TRIGGER_USAGE_REPORT_ID;
			}

			PacketMappingData packetMappingForEventTrigger = createPacketMapping(DiameterAVPConstants.TGPP_EVENT_TRIGGER, PCRFKeyConstants.EVENT_TRIGGER.getVal(), Long.toString(usageReportValue), null);
			eventTriggerMapping = pcrfToDiameterMappingFactory.create(packetMappingForEventTrigger, chargingRuleInstallMode, null);
		} catch (Exception e) {
			getLogger().error(MODULE, "Error in parsing for packetMapping: " + DEFAULT_RAR + ", attributeid = " + DiameterAVPConstants.TGPP_EVENT_TRIGGER 
					+ ", policykey = " + PCRFKeyConstants.EVENT_TRIGGER.getVal() + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
		
		if (eventTriggerMapping != null) {
			rarDefaultMappings.add(eventTriggerMapping);
		}
		
		return rarDefaultMappings;
	}

	private static PacketMappingData createDefaultPacketMappingDataForQoSInformation() {
		PacketMappingData parentMapping = createPacketMapping(DiameterAVPConstants.TGPP_QOS_INFORMATION, null, null, null);
		parentMapping.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_QCI, PCRFKeyConstants.IPCAN_QCI.val, null, null));
		parentMapping.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_APN_AGGREGATED_MAX_BR_DL, PCRFKeyConstants.IPCAN_AAMBRDL.val, null, null));
		parentMapping.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_APN_AGGREGATED_MAX_BR_UL, PCRFKeyConstants.IPCAN_AAMBRUL.val, null, null));
		parentMapping.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_MAX_REQUESTED_BW_DL, PCRFKeyConstants.IPCAN_AAMBRDL.val, null, null));
		parentMapping.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_MAX_REQUESTED_BW_UL, PCRFKeyConstants.IPCAN_AAMBRUL.val, null, null));
		PacketMappingData arpMapping = createPacketMapping(DiameterAVPConstants.TGPP_ALLOCATION_RETENTION_PRIORIY, null, null, null);
		arpMapping.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_PRIORIY_LEVEL, PCRFKeyConstants.IPCAN_PRIORITY_LEVEL.val, null, null));
		arpMapping.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_PRE_EMPTION_CAPABILITY, PCRFKeyConstants.IPCAN_PREEMPTION_CAPABILITY.val, null, null));
		arpMapping.addChildMapping(createPacketMapping(DiameterAVPConstants.TGPP_PRE_EMPTION_VALNERABILITY, PCRFKeyConstants.IPCAN_PREEMPTION_VULNERABILITY.val, null, null));
		parentMapping.addChildMapping(arpMapping);
		return parentMapping;
	}

	private static PacketMappingData createPacketMapping(String attributeId, String policyKey, String defaultVal, String valMapping) {
		PacketMappingData mapping1 = new PacketMappingData();
		mapping1.setAttribute(attributeId);
		mapping1.setPolicyKey(policyKey);
		mapping1.setDefaultValue(defaultVal);
		mapping1.setValueMapping(valMapping);
		return mapping1;
	}
}
