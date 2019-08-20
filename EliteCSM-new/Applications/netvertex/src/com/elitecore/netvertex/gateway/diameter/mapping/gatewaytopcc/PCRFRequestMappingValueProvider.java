package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc;

import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.gateway.diameter.mapping.DiameterValueProvider;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by harsh on 6/28/17.
 */
public class PCRFRequestMappingValueProvider extends DiameterValueProvider {
    private final PCRFRequest pcrfRequest;


    public PCRFRequestMappingValueProvider(@Nullable DiameterRequest diameterRequest, @Nonnull PCRFRequest pcrfRequest, DiameterGatewayConfiguration configuration) {
        super(diameterRequest, null, configuration);
        this.pcrfRequest = pcrfRequest;
    }
    public PCRFRequest getPCRFRequest() {
        return pcrfRequest;
    }

}
