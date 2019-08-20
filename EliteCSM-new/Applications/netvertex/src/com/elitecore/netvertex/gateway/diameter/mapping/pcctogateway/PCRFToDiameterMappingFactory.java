package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.ChargingRuleInstallMode;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PacketMappingConstants;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.netvertex.core.conf.impl.PacketMappingData;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.*;
import com.elitecore.netvertex.gateway.diameter.utility.*;

import java.util.*;

public class PCRFToDiameterMappingFactory {
	
	private static final String COMMA = ",";
    private static final String EQUALS = "=";
    private static final String PCC_RULE = "PCCRule";

    private static Compiler compiler = Compiler.getDefaultCompiler();
	
	private AttributeFactory attributeFactory;
	
	public PCRFToDiameterMappingFactory() {
		this.attributeFactory = new AttributeFactory() {
			
			@Override
			public IDiameterAVP create(String id) {
				return DiameterDictionary.getInstance().getKnownAttribute(id);
			}

			@Override
			public IDiameterAVP create(String id, String value) {
				IDiameterAVP avp = create(id);
				
				if (avp != null) {
					avp.setStringValue(value);
				}
				
				return avp;
			}
		};
	}
	
	public PCRFToDiameterMappingFactory(AttributeFactory attributeFactory) {
		this.attributeFactory = attributeFactory;
	}

    private PCCRuleToDiameterPacketMapping createPCCRuleMappingBasedOnCRIMode(String diameterAttributeId,
                                                                              ChargingRuleInstallMode chargingRuleInstallMode, List<PCRFToDiameterPacketMapping> mappings) {
		
		if (ChargingRuleInstallMode.SINGLE == chargingRuleInstallMode){
			
			return new SingleModePCCRuletoDiameterMapping(diameterAttributeId, mappings, attributeFactory);
			
		} else {
		    
		    return new GroupModePCCRuletoDiameterMapping(diameterAttributeId, mappings, attributeFactory);
		}
		
	}

	private static Map<String,String> parseValue(String value){
		Map<String,String> valueMap = new HashMap<String, String>();
		if(value != null && value.trim().length() > 0){
			for(String val : value.split(COMMA)){
				valueMap.put(val.split(EQUALS)[0],val.split(EQUALS)[1]);
			}
		}
		return valueMap;
	}
	
	private static Map<String, String> parseGrpValueMapping(String valueMappings) {
		Map<String, String> grpAttMap = new HashMap<>();
		if(valueMappings != null && valueMappings.isEmpty() == false){
			for(String value : valueMappings.split(COMMA)){
				String [] values = value.split(EQUALS);
				grpAttMap.put(values[0].trim(), values[1].trim());
			}
		}
		return grpAttMap;
	}


	private List<PCRFToDiameterPacketMapping> createGroupAttributeMapping(PacketMappingData groupPacketMappingData,
																		  ChargingRuleDefinitionPacketMapping pccRuleMappings) throws InvalidExpressionException {

		List<PCRFToDiameterPacketMapping> mappings = new ArrayList<>();

		for (PacketMappingData packetMappingData : groupPacketMappingData.getChildMappings()) {
			String diameterAttributeId = packetMappingData.getAttribute();
			String policyKey = packetMappingData.getPolicyKey();

			if (Strings.isNullOrBlank(policyKey)) {
				if (diameterAttributeId.equalsIgnoreCase(PacketMappingConstants.NONE.getVal())) {
					throw new InvalidExpressionException("For Attribute : [" + PacketMappingConstants.NONE.getVal() + "] only Policy Key : [" + PCRFKeyConstants.PCC_RULE.getVal() + "] is allowed");
				}

				mappings.add(new GroupAttributeMapping(diameterAttributeId, createGroupAttributeMapping(packetMappingData, pccRuleMappings), attributeFactory));
			} else if (policyKey.equalsIgnoreCase(PCRFKeyConstants.PCC_RULE.getVal())) {
				mappings.add(pccRuleMappings);
			} else {

				if (diameterAttributeId.equalsIgnoreCase(PacketMappingConstants.NONE.getVal()) && policyKey.equalsIgnoreCase(PCRFKeyConstants.PCC_RULE.getVal()) == false) {
					throw new InvalidExpressionException("For Attribute : [" + PacketMappingConstants.NONE.getVal() + "] only Policy Key : [" + PCRFKeyConstants.PCC_RULE.getVal() + "] is allowed");
				}

				Map<String, String> valueMap = parseGrpValueMapping(packetMappingData.getValueMapping());

				if (policyKey.equalsIgnoreCase(PCRFKeyConstants.PCC_RULE_SERVICE_DATA_FLOW.getVal())) {
					mappings.add(new ServiceDataFlowMapping(diameterAttributeId, attributeFactory));
				}

				String defaultValue = packetMappingData.getDefaultValue();

				if(Strings.isNullOrBlank(defaultValue)) {
					defaultValue = null;
				}

				if (policyKey.contains("(") && policyKey.contains(")")) {
					Expression sourceExpression = compiler.parseExpression(policyKey);
					mappings.add(new PCRFAttributeToNonGroupAVPMapping(diameterAttributeId, sourceExpression, policyKey, valueMap, defaultValue, attributeFactory));
				}else {
					mappings.add(new PCCAttributeToNonGroupAVPMapping(diameterAttributeId, policyKey, valueMap, defaultValue, attributeFactory));
				}
			}
		}

		return mappings;
	}

	public PCRFToDiameterPacketMapping create(PacketMappingData packetMappingData, ChargingRuleInstallMode chargingRuleInstallMode,
											  ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping) throws InvalidExpressionException {

		if (packetMappingData.getAttribute().equalsIgnoreCase(PacketMappingConstants.NONE.getVal()) && packetMappingData.getPolicyKey().equalsIgnoreCase(PCRFKeyConstants.PCC_RULE.getVal()) == false) {
			throw new InvalidExpressionException("For Attribute: [" + PacketMappingConstants.NONE.getVal() + "] only Policy Key : [" + PCRFKeyConstants.PCC_RULE.getVal() + "] is allowed");
		}

		try {
			String policyKey = packetMappingData.getPolicyKey();
			String diameterAttributeId = packetMappingData.getAttribute();
			String valueMapping = packetMappingData.getValueMapping();
			String defaultValue = packetMappingData.getDefaultValue();

			if(Strings.isNullOrBlank(defaultValue)) {
				defaultValue = null;
			}

			if (Strings.isNullOrBlank(policyKey)) {
				List<PCRFToDiameterPacketMapping> attributeMappings = createGroupAttributeMapping(packetMappingData, chargingRuleDefinitionPacketMapping);

				if (packetMappingData.childPolicyKeyContains(PCC_RULE)) {
					return createPCCRuleMappingBasedOnCRIMode(diameterAttributeId, chargingRuleInstallMode, attributeMappings);
				} else {
					return new GroupAttributeMapping(diameterAttributeId, attributeMappings, attributeFactory);
				}
			} else {

				if (policyKey.equalsIgnoreCase(PCRFKeyConstants.PCC_RULE.getVal())) {
					return createPCCRuleMappingBasedOnCRIMode(diameterAttributeId, chargingRuleInstallMode, Arrays.asList(chargingRuleDefinitionPacketMapping));
				} else {
					if (policyKey.contains("(") && policyKey.contains(")")) {
						Expression sourceExpression = compiler.parseExpression(policyKey);
						return new PCRFAttributeToNonGroupAVPMapping(diameterAttributeId, sourceExpression, policyKey, parseValue(valueMapping), defaultValue, attributeFactory);
					}else {
						return new PCCAttributeToNonGroupAVPMapping(diameterAttributeId, policyKey, parseValue(valueMapping), defaultValue, attributeFactory);
					}
				}
			}

		} catch (Exception e) {
			throw new InvalidExpressionException(e);
		}
	}
}
