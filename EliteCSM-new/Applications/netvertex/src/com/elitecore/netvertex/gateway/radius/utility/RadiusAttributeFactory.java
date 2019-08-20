package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface RadiusAttributeFactory {
    @Nullable
    IRadiusAttribute create(@Nonnull String id);

    @Nullable IRadiusAttribute create(@Nonnull String id, @Nonnull String value);
}
