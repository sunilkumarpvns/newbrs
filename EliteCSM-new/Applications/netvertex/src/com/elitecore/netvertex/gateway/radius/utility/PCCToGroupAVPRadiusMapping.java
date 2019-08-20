package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.coreradius.commons.attributes.GroupedAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.netvertex.gateway.radius.mapping.PCCtoRadiusMappingValueProvider;

import javax.annotation.Nonnull;
import java.util.List;

public class PCCToGroupAVPRadiusMapping implements PCCToRadiusPacketMapping {
    @Nonnull private final String id;
    @Nonnull private final List<PCCToRadiusPacketMapping> mappings;
    @Nonnull private RadiusAttributeFactory radiusAttributeFactory;

    public PCCToGroupAVPRadiusMapping(@Nonnull String id, @Nonnull List<PCCToRadiusPacketMapping> mappings, @Nonnull RadiusAttributeFactory radiusAttributeFactory) {
        this.id = id;
        this.mappings = mappings;
        this.radiusAttributeFactory = radiusAttributeFactory;
    }

    @Override
    public boolean apply(@Nonnull PCCtoRadiusMappingValueProvider valueProvider, @Nonnull AvpAccumalator accumalator) {
        IRadiusAttribute avp = radiusAttributeFactory.create(id);
        boolean isApplied = false;

        if (avp == null) {
            return isApplied;
        }

        if(avp instanceof GroupedAttribute == false) {
            return isApplied;
        }

        AvpAccumalator avpAccumalator = AvpAccumalators.of((GroupedAttribute) avp);

        for (PCCToRadiusPacketMapping packetMapping : mappings) {
            packetMapping.apply(valueProvider, avpAccumalator);
        }

        if (avpAccumalator.isEmpty() == false) {
            accumalator.add(avp);
            isApplied = true;
        }
        return isApplied;
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- Group AVP RADIUS mapping -- ");
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.appendChildObject(id, mappings);
    }
}
