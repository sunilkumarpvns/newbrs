package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.AvpAccumalator;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by harsh on 6/28/17.
 */
public abstract class PCCRuleToDiameterPacketMapping implements PCRFToDiameterPacketMapping {

    @Nonnull private final List<PCRFToDiameterPacketMapping> mappings;

    public PCCRuleToDiameterPacketMapping(@Nonnull List<PCRFToDiameterPacketMapping> mappings) {
        this.mappings = mappings;
    }

    protected void applyMapping(@Nonnull DiameterPacketMappingValueProvider valueProvider, AvpAccumalator diameterAVPs) {
        for (int mappingIndex = 0; mappingIndex < mappings.size(); mappingIndex++) {
            mappings.get(mappingIndex).apply(valueProvider, diameterAVPs);
        }
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- PCC rule to Diameter Packet Mapping -- ");
        toString(builder);
        return builder.toString();
    }

    @Nonnull
    public List<PCRFToDiameterPacketMapping> getMappings() {
        return mappings;
    }

}

