package com.elitecore.netvertex.gateway.radius.mapping;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PacketMappingConstants;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.netvertex.core.conf.impl.PacketMappingData;
import com.elitecore.netvertex.gateway.radius.utility.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PCCToRadiusMappingFactory {
    private static final String COMMA = ",";
    private static final String EQUALS = "=";
    private static final String PCC_RULE = "PCCRule";

    private static Compiler compiler = Compiler.getDefaultCompiler();
    private RadiusAttributeFactory radiusAttributeFactory;

    public PCCToRadiusMappingFactory() {
        this.radiusAttributeFactory = new RadiusAttributeFactory() {
            @Nullable
            @Override
            public IRadiusAttribute create(@Nonnull String id) {
                return Dictionary.getInstance().getKnownAttribute(id);
            }

            @Nullable
            @Override
            public IRadiusAttribute create(@Nonnull String id, @Nonnull String value) {
                IRadiusAttribute attribute = create(id);

                if (attribute != null) {
                    attribute.setStringValue(value);
                }

                return attribute;
            }
        };
    }

    public PCCToRadiusMappingFactory(RadiusAttributeFactory radiusAttributeFactory) {
        this.radiusAttributeFactory = radiusAttributeFactory;
    }

    private PCCRuleToRadiusPacketMapping createPCCRuleMappingBasedOnCRIMode(String radiusAttributeId,
                                                                            List<PCCToRadiusPacketMapping> mappings) {
        return new GroupModePCCRuleToRadiusMapping(radiusAttributeId, mappings, radiusAttributeFactory);

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

    private List<PCCToRadiusPacketMapping> createGroupAttributeMapping(PacketMappingData groupPacketMappingData,
                                                                       RadiusChargingRuleDefinitionPacketMapping pccRuleMappings) throws InvalidExpressionException {
        List<PCCToRadiusPacketMapping> mappings = new ArrayList<>();

        for (PacketMappingData packetMappingData : groupPacketMappingData.getChildMappings()) {
            String radiusAttributeId = packetMappingData.getAttribute();
            String policyKey = packetMappingData.getPolicyKey();

            if (Strings.isNullOrBlank(policyKey)) {
                if (radiusAttributeId.equalsIgnoreCase(PacketMappingConstants.NONE.getVal())) {
                    throw new InvalidExpressionException("For Attribute : [" + PacketMappingConstants.NONE.getVal() + "] only Policy Key : [" + PCRFKeyConstants.PCC_RULE.getVal() + "] is allowed");
                }

                mappings.add(new PCCToGroupAVPRadiusMapping(radiusAttributeId, createGroupAttributeMapping(packetMappingData, pccRuleMappings), radiusAttributeFactory));
            } else if (policyKey.equalsIgnoreCase(PCRFKeyConstants.PCC_RULE.getVal())) {
                mappings.add(pccRuleMappings);
            } else {
                if (radiusAttributeId.equalsIgnoreCase(PacketMappingConstants.NONE.getVal()) && policyKey.equalsIgnoreCase(PCRFKeyConstants.PCC_RULE.getVal()) == false) {
                    throw new InvalidExpressionException("For Attribute : [" + PacketMappingConstants.NONE.getVal() + "] only Policy Key : [" + PCRFKeyConstants.PCC_RULE.getVal() + "] is allowed");
                }

                Map<String, String> valueMap = parseGrpValueMapping(packetMappingData.getValueMapping());

                if (policyKey.contains("(") && policyKey.contains(")")) {
                    Expression sourceExpression = compiler.parseExpression(policyKey);
                    mappings.add(new PCCToRadiusNonGroupAVPExpressionMapping(radiusAttributeId, sourceExpression, policyKey, valueMap, packetMappingData.getDefaultValue(), radiusAttributeFactory));
                }else {
                    mappings.add(new PCCToRadiusNonGroupAVPMapping(radiusAttributeId, policyKey, valueMap, packetMappingData.getDefaultValue(), radiusAttributeFactory));
                }
            }

        }
        return mappings;
    }

    public PCCToRadiusPacketMapping create(PacketMappingData packetMappingData,
                                           RadiusChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping) throws InvalidExpressionException {

        if (packetMappingData.getAttribute().equalsIgnoreCase(PacketMappingConstants.NONE.getVal()) && packetMappingData.getPolicyKey().equalsIgnoreCase(PCRFKeyConstants.PCC_RULE.getVal()) == false) {
            throw new InvalidExpressionException("For Attribute: [" + PacketMappingConstants.NONE.getVal() + "] only Policy Key : [" + PCRFKeyConstants.PCC_RULE.getVal() + "] is allowed");
        }

        try {
            String policyKey = packetMappingData.getPolicyKey();
            String radiusAttributeId = packetMappingData.getAttribute();
            String valueMapping = packetMappingData.getValueMapping();
            String defaultValue = packetMappingData.getDefaultValue();

            if (Strings.isNullOrBlank(policyKey)) {
                List<PCCToRadiusPacketMapping> attributeMappings = createGroupAttributeMapping(packetMappingData, chargingRuleDefinitionPacketMapping);

                if (packetMappingData.childPolicyKeyContains(PCC_RULE)) {
                    return createPCCRuleMappingBasedOnCRIMode(radiusAttributeId, attributeMappings);
                } else {
                    return new PCCToGroupAVPRadiusMapping(radiusAttributeId, attributeMappings, radiusAttributeFactory);
                }
            } else {

                if (policyKey.equalsIgnoreCase(PCRFKeyConstants.PCC_RULE.getVal())) {
                    return createPCCRuleMappingBasedOnCRIMode(radiusAttributeId, Arrays.asList(chargingRuleDefinitionPacketMapping));
                } else {
                    if (policyKey.contains("(") && policyKey.contains(")")) {
                        Expression sourceExpression = compiler.parseExpression(policyKey);
                        return new PCCToRadiusNonGroupAVPExpressionMapping(radiusAttributeId, sourceExpression, policyKey, parseValue(valueMapping), defaultValue, radiusAttributeFactory);
                    }else {
                        return new PCCToRadiusNonGroupAVPMapping(radiusAttributeId, policyKey, parseValue(valueMapping), defaultValue, radiusAttributeFactory);
                    }
                }
            }

        } catch (Exception e) {
            throw new InvalidExpressionException(e);
        }
    }
}
