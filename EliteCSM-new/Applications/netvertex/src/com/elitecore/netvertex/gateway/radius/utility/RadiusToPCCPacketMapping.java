package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.netvertex.gateway.radius.mapping.PCRFRequestRadiusMappingValuProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFResponseMappingValueProvider;

import javax.annotation.Nonnull;

public interface RadiusToPCCPacketMapping extends ToStringable {

    default boolean apply(@Nonnull PCRFRequestRadiusMappingValuProvider valueProvider) {
        throw new UnsupportedOperationException("Operation is not supported");
    }

    default void apply(PCRFResponseMappingValueProvider valueProvider) {
        throw new UnsupportedOperationException("Operation is not supported");
    }

    default void toString(IndentingToStringBuilder builder) {

    }
}
