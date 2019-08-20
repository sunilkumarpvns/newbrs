package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway;

import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.netvertex.gateway.diameter.mapping.DiameterValueProvider;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by harsh on 6/28/17.
 */
public class PCRFResponseMappingValueProvider extends DiameterValueProvider {
    private final PCRFResponse pcrfResponse;

    public PCRFResponseMappingValueProvider(@Nullable DiameterAnswer diameterAnswer, @Nonnull PCRFResponse pcrfResponse, DiameterGatewayConfiguration configuration) {
        super(null, diameterAnswer, configuration);
        this.pcrfResponse = pcrfResponse;
    }

    public final PCRFResponse getPcrfResponse() {
        return pcrfResponse;
    }
}
