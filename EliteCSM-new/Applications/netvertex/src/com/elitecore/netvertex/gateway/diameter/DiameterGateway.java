package com.elitecore.netvertex.gateway.diameter;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;

public interface DiameterGateway {
	public DiameterGatewayConfiguration getConfiguration();
	public String getStatus();
	public void init() throws InitializationFailedException;
	public void reInit() throws InitializationFailedException;
}

