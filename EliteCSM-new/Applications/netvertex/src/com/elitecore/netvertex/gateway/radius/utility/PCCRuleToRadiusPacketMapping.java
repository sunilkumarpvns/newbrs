package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.gateway.radius.mapping.PCCtoRadiusMappingValueProvider;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class PCCRuleToRadiusPacketMapping implements PCCToRadiusPacketMapping {
    @Nonnull private final List<PCCToRadiusPacketMapping> mappings;

    public PCCRuleToRadiusPacketMapping(@Nonnull List<PCCToRadiusPacketMapping> mappings) {
        this.mappings = mappings;
    }

    protected void applyMapping(@Nonnull PCCtoRadiusMappingValueProvider valueProvider, AvpAccumalator radiusAttributes) {
        for (int mappingIndex = 0; mappingIndex < mappings.size(); mappingIndex++) {
            mappings.get(mappingIndex).apply(valueProvider, radiusAttributes);
        }
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- PCC rule to RADIUS Packet Mapping -- ");
        toString(builder);
        return builder.toString();
    }

    @Nonnull
    public List<PCCToRadiusPacketMapping> getMappings() {
        return mappings;
    }
}
