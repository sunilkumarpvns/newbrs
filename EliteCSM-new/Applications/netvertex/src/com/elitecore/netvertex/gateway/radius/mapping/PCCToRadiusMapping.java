package com.elitecore.netvertex.gateway.radius.mapping;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.radius.utility.AvpAccumalator;
import com.elitecore.netvertex.gateway.radius.utility.PCCToRadiusPacketMapping;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class PCCToRadiusMapping implements PCCToRadiusPacketMapping {
    private static final String MODULE = "PCC-TO-RADIUS-MAPPING";

    @Nonnull private final Map<LogicalExpression, List<PCCToRadiusPacketMapping>> customMappings;
    @Nonnull private final int packetType;

    public PCCToRadiusMapping(@Nonnull Map<LogicalExpression, List<PCCToRadiusPacketMapping>> customMappings,
                              @Nonnull int packetType) {
        this.customMappings = customMappings;
        this.packetType = packetType;
    }

    @Override
    public boolean apply(PCCtoRadiusMappingValueProvider valueProvider, AvpAccumalator accumalator) {
        valueProvider.getRadiusPacket().setPacketType(packetType);
        PCRFResponse pcrfResponse = valueProvider.getPcrfResponse();
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "PCRF Response received for creating RADIUS packet: " + pcrfResponse) ;
        }

        boolean isApplied = applyConfiguredMapping(valueProvider, accumalator);

        if(isApplied == false) {
            if(getLogger().isLogLevel(LogLevel.INFO))
                getLogger().info(MODULE, "No Attribute mapping configured for PCRFResponse: "+
                        pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));
            return false;
        }
        valueProvider.getRadiusPacket().refreshPacketHeader();
        return true;
    }

    private boolean applyConfiguredMapping(PCCtoRadiusMappingValueProvider valueProvider, AvpAccumalator accumalator) {
        for (Map.Entry<LogicalExpression, List<PCCToRadiusPacketMapping>> configuredMapping : customMappings.entrySet()) {

            if (configuredMapping.getKey().evaluate(valueProvider)) {

                for (PCCToRadiusPacketMapping PCCToRadiusPacketMapping : configuredMapping.getValue()) {
                    PCCToRadiusPacketMapping.apply(valueProvider, accumalator);
                }

                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- PCC to RADIUS Mapping -- ");
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {

        builder.appendHeading("Custom Mappings --");
        builder.incrementIndentation();
        for (Map.Entry<LogicalExpression, List<PCCToRadiusPacketMapping>> entry: customMappings.entrySet()) {
            builder.appendChildObject(entry.getKey().toString(), entry.getValue());
        }
        builder.decrementIndentation();
    }

    public static class PCCToRADIUSMappingBuilder {

        private Map<LogicalExpression, List<PCCToRadiusPacketMapping>> customMappings;
        private int packetType;

        public PCCToRADIUSMappingBuilder withConfiguredMapping(@Nonnull Map<LogicalExpression, List<PCCToRadiusPacketMapping>> configuredMappings) {
            this.customMappings = configuredMappings;
            return this;
        }

        public PCCToRADIUSMappingBuilder withDefaultPacketType(@Nonnull int packetType) {
            this.packetType = packetType;
            return this;
        }

        public PCCToRadiusMapping build() {

            if(Maps.isNullOrEmpty(customMappings)) {
                return null;
            }

            return new PCCToRadiusMapping(customMappings, packetType);
        }

    }
}
