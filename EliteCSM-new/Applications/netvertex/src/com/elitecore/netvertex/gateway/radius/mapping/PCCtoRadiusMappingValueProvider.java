package com.elitecore.netvertex.gateway.radius.mapping;

import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.conf.NetvertexServerConfiguration;
import com.elitecore.netvertex.gateway.radius.RadiusGatewayControllerContext;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;
import com.elitecore.netvertex.gateway.util.PCRFResponseValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import javax.annotation.Nonnull;

public class PCCtoRadiusMappingValueProvider extends PCRFResponseValueProvider{

    private RadiusPacket radiusPacket;
    private RadiusGatewayConfiguration radiusGatewayConfiguration;
    private RadiusGatewayControllerContext controllerContext;

    public PCCtoRadiusMappingValueProvider(PCRFResponse pcrfResponse,
                                              RadiusPacket radiusPacket,
                                              RadiusGatewayConfiguration radiusGatewayConfiguration,
                                              RadiusGatewayControllerContext controllerContext) {
        this(null, pcrfResponse, radiusPacket, radiusGatewayConfiguration, controllerContext);
    }

    public PCCtoRadiusMappingValueProvider(PCCRule pccRule,
                                           PCRFResponse pcrfResponse,
                                           RadiusPacket radiusPacket,
                                           RadiusGatewayConfiguration radiusGatewayConfiguration,
                                           RadiusGatewayControllerContext controllerContext) {
        super(pcrfResponse, pccRule);
        this.radiusPacket = radiusPacket;
        this.radiusGatewayConfiguration = radiusGatewayConfiguration;
        this.controllerContext = controllerContext;

    }

    public RadiusPacket getRadiusPacket() {
        return radiusPacket;
    }

    public void setRadiusPacket(RadiusPacket radiusPacket) {
        this.radiusPacket = radiusPacket;
    }

    @Nonnull
    public RadiusGatewayConfiguration getRadiusGWConfiguration() {
        return radiusGatewayConfiguration;
    }

    @Nonnull
    public RadiusGatewayControllerContext getControllerContext() {
        return controllerContext;
    }

    @Nonnull
    public NetVertexServerContext getServerContext() {
        return controllerContext.getServerContext();
    }

    @Nonnull
    public NetvertexServerConfiguration getServerConfiguration() {
        return controllerContext.getServerContext().getServerConfiguration();
    }
}
