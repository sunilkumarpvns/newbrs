package com.elitecore.netvertex.gateway.diameter.utility;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;

/**
 * Created by harsh on 6/29/17.
 */
public interface AttributeFactory {

    @Nullable IDiameterAVP create(@Nonnull String id);
    
    @Nullable IDiameterAVP create(@Nonnull String id, @Nonnull String value);
}
