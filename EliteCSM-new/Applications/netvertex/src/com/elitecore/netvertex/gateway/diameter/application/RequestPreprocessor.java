package com.elitecore.netvertex.gateway.diameter.application;

import javax.annotation.Nonnull;

import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;

public interface RequestPreprocessor {
	
	void process(@Nonnull DiameterRequest request, @Nonnull DiameterGatewayConfiguration gatewayConf);
}
