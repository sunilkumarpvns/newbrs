package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import javax.annotation.Nonnull;

/**
 * Created by harsh on 6/28/17.
 */
public interface PCRFToDiameterPacketMapping extends ToStringable{

    public void apply(@Nonnull DiameterPacketMappingValueProvider valueProvider, @Nonnull AvpAccumalator accumalator);

    default void toString(IndentingToStringBuilder builder) {

    }

}
