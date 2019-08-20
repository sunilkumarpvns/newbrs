package com.elitecore.netvertex.gateway.radius.mapping;

import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceRequest;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceResponse;
import com.elitecore.netvertex.gateway.radius.utility.RadiusValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PCRFRequestRadiusMappingValuProvider extends RadiusValueProvider {
    private final PCRFRequest pcrfRequest;

    public PCRFRequestRadiusMappingValuProvider(@Nullable RadServiceRequest radiusRequest,
                                                RadServiceResponse radServiceResponse,
                                                @Nonnull PCRFRequest pcrfRequest,
                                                RadiusGatewayConfiguration configuration) {
        super(radiusRequest, radServiceResponse, configuration);
        this.pcrfRequest = pcrfRequest;
    }
    public PCRFRequest getPCRFRequest() { return pcrfRequest; }
}
