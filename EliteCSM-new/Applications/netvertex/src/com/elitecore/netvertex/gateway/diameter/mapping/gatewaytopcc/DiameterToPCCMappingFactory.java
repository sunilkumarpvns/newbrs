package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc;

import com.elitecore.commons.base.Strings;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.netvertex.core.conf.impl.PacketMappingData;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Saloni Shah
 *
 */
public class DiameterToPCCMappingFactory {

    private static final String COMMA = ",";
    private static final String EQUALS = "=";

    private static Compiler compiler = Compiler.getDefaultCompiler();

    private static Map<String,String> parseValue(String value){
        Map<String,String> valueMap = new HashMap<>();
        if(value != null && value.trim().length() > 0){
            for(String val : value.split(COMMA)){
                valueMap.put(val.split(EQUALS)[0],val.split(EQUALS)[1]);
            }
        }
        return valueMap;
    }

    public DiameterToPCCPacketMapping create(PacketMappingData packetMappingData) throws InvalidExpressionException {

        try {
            String policyKey = packetMappingData.getPolicyKey();
            String diameterAttributeId = packetMappingData.getAttribute();
            String valueMapping = packetMappingData.getValueMapping();
            String defaultValue = packetMappingData.getDefaultValue();
            if(Strings.isNullOrBlank(defaultValue)) {
                defaultValue = null;
            }

            if (diameterAttributeId.contains("(") && diameterAttributeId.contains(")")) {
                return new DiameterToPCCExpressionAttributeMapping(compiler.parseExpression(diameterAttributeId), diameterAttributeId, policyKey, defaultValue, parseValue(valueMapping));
            }else {
                return new DiameterToPCCAttributeMapping(diameterAttributeId, policyKey, defaultValue, parseValue(valueMapping));
            }

        } catch (Exception e) {
            throw new InvalidExpressionException(e);
        }
    }
}
