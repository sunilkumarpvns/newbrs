package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFResponseMappingValueProvider;

import javax.annotation.Nonnull;

/**
 * Created by harsh on 6/28/17.
 */
public interface DiameterToPCCPacketMapping extends ToStringable{

    default void apply(@Nonnull PCRFRequestMappingValueProvider valueProvider) {
        throw new UnsupportedOperationException("Operation is not supported");
    }

    default void apply(PCRFResponseMappingValueProvider valueProvider) {
        throw new UnsupportedOperationException("Operation is not supported");
    }

    default void toString(IndentingToStringBuilder builder) {

    }
}
