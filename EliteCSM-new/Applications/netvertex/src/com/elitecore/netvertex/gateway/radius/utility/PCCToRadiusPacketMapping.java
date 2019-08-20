package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.netvertex.gateway.radius.mapping.PCCtoRadiusMappingValueProvider;

import javax.annotation.Nonnull;

public interface PCCToRadiusPacketMapping extends ToStringable{
    public boolean apply(@Nonnull PCCtoRadiusMappingValueProvider valueProvider, @Nonnull AvpAccumalator accumalator);

    default void toString(IndentingToStringBuilder builder) {

    }
}
