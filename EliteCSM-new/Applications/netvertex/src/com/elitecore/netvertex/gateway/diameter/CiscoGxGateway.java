package com.elitecore.netvertex.gateway.diameter;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;

public class CiscoGxGateway implements DiameterGateway{
	private static final String MODULE = "CISCO-GX-GWT";
	private DiameterGatewayConfiguration configuration;
	private String status = "";
	
	public CiscoGxGateway(DiameterGatewayConfiguration configuration) {
		this.configuration = configuration;
	}

	public void init() throws InitializationFailedException{
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Cisco Gx gateway = " + configuration.getName() + " initialized successfully");
	}
	
	@Override
	public String getStatus() {
		return status;
	}

	@Override
	public DiameterGatewayConfiguration getConfiguration() {
		return configuration;
	}
	
	public void reInit() throws InitializationFailedException{
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Cisco Gx gateway = " + configuration.getName() + " re-initialized successfully");
	}
	
}
