package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc;

import javax.annotation.Nonnull;

import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

/**
 * Created by harsh on 6/28/17.
 */
public interface EventMapping {
    public void apply(@Nonnull DiameterRequest diameterRequest, @Nonnull PCRFRequest pcrfRequest);
}
