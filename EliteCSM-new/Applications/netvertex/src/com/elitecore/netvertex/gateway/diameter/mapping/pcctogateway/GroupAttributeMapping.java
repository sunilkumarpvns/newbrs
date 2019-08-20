package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway;

import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.netvertex.gateway.diameter.utility.*;

public class GroupAttributeMapping implements PCRFToDiameterPacketMapping {

    @Nonnull private final String id;
    @Nonnull private final List<PCRFToDiameterPacketMapping> mappings;
    @Nonnull private AttributeFactory attributeFactory;

	public GroupAttributeMapping(@Nonnull String id, @Nonnull List<PCRFToDiameterPacketMapping> mappings, @Nonnull AttributeFactory attributeFactory) {
        this.id = id;
        this.mappings = mappings;
        this.attributeFactory = attributeFactory;
    }

    @Override
    public void apply(@Nonnull DiameterPacketMappingValueProvider valueProvider, @Nonnull AvpAccumalator accumalator) {


        IDiameterAVP avp = attributeFactory.create(id);

        if (avp == null) {
            return;
        }
        
        if (avp.isGrouped() == false) {
            return;
        }
        
        AvpAccumalator avpAccumalator = AvpAccumalators.of((AvpGrouped) avp);
        
        for (PCRFToDiameterPacketMapping mapping : mappings) {
        	mapping.apply(valueProvider, avpAccumalator);
        }

        if(avpAccumalator.isEmpty() == false) {
            accumalator.add(avp);
        }

    }
    
    @Override
   	public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- Group Attribute mapping -- ");
        toString(builder);
        return builder.toString();
   	}

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.appendChildObject(id, mappings);
    }
}