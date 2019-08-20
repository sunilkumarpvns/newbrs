package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway;

import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.conf.NetvertexServerConfiguration;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.util.PCRFResponseValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import javax.annotation.Nonnull;

/**
 * Created by harsh on 6/28/17.
 */
public class DiameterPacketMappingValueProvider extends PCRFResponseValueProvider{

	@Nonnull private DiameterPacket diameterPacket;
	@Nonnull private DiameterGatewayConfiguration diameterGatewayConfiguration;
	@Nonnull private DiameterGatewayControllerContext controllerContext;

	public DiameterPacketMappingValueProvider(@Nonnull PCRFResponse pcrfResponse,
											  @Nonnull DiameterPacket diameterPacket,
											  @Nonnull DiameterGatewayConfiguration diameterGatewayConfiguration,
											  @Nonnull DiameterGatewayControllerContext controllerContext) {
	    this(null, pcrfResponse, diameterPacket, diameterGatewayConfiguration, controllerContext);
        
    }

    public DiameterPacketMappingValueProvider(PCCRule pccRule,
                                              @Nonnull PCRFResponse pcrfResponse,
                                              @Nonnull DiameterPacket diameterPacket,
                                              @Nonnull DiameterGatewayConfiguration diameterGatewayConfiguration,
                                              @Nonnull DiameterGatewayControllerContext controllerContext) {
        super(pcrfResponse, pccRule);
        this.diameterPacket = diameterPacket;
        this.diameterGatewayConfiguration = diameterGatewayConfiguration;
        this.controllerContext = controllerContext;

    }

	public DiameterPacket getDiameterPacket() {
		return diameterPacket;
	}

	@Nonnull
	public DiameterGatewayConfiguration getDiameterGatewayConfiguration() {
		return diameterGatewayConfiguration;
	}

	@Nonnull
	public DiameterGatewayControllerContext getControllerContext() {
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
