package com.elitecore.netvertex.gateway.radius.mapping;

import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.netvertex.core.conf.impl.PacketMappingData;
import com.elitecore.netvertex.gateway.radius.utility.RadiusToPCCPacketMapping;

import java.util.HashMap;
import java.util.Map;

public class RadiusToPCCMappingFactory {
    private static final String COMMA = ",";
    private static final String EQUALS = "=";

    private static Compiler compiler = Compiler.getDefaultCompiler();

    private Map<String,String> parseValue(String value){
        Map<String,String> valueMap = new HashMap<>();
        if(value != null && value.trim().length() > 0){
            for(String val : value.split(COMMA)){
                valueMap.put(val.split(EQUALS)[0],val.split(EQUALS)[1]);
            }
        }
        return valueMap;
    }

    public RadiusToPCCPacketMapping create(PacketMappingData packetMappingData) throws InvalidExpressionException {
        try {
            String policyKey = packetMappingData.getPolicyKey();
            String radiusAttributeId = packetMappingData.getAttribute();
            String valueMapping = packetMappingData.getValueMapping();
            String defaultValue = packetMappingData.getDefaultValue();

            if (radiusAttributeId.contains("(") && radiusAttributeId.contains(")")) {
                return new RadiusToPCCExpressionAttribiuteMapping(compiler.parseExpression(radiusAttributeId), radiusAttributeId, policyKey, defaultValue, parseValue(valueMapping));
            }else {
                return new RadiusToPCCAttributeMapping(radiusAttributeId, policyKey, defaultValue, parseValue(valueMapping));
            }

        } catch (Exception e) {
            throw new InvalidExpressionException(e);
        }
    }
}
