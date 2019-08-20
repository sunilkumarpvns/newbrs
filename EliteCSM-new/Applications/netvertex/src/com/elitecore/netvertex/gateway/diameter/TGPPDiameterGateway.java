package com.elitecore.netvertex.gateway.diameter;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;

/**
 * Interface for Diameter Gateway
 * 
 * @author Subhash Punani
 *
 */
public class TGPPDiameterGateway implements DiameterGateway{
	
	private static final String MODULE = "DIA-GTW";
	
	private DiameterGatewayConfiguration configuration;
	private DiameterGatewayControllerContext context;
	
	
	public TGPPDiameterGateway(DiameterGatewayControllerContext context, DiameterGatewayConfiguration configuration) {
		this.context = context;
		this.configuration = configuration;
	}
	
	@Override
	public void init() throws InitializationFailedException{
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "3GPP Diameter gateway = " + configuration.getName() + " initialized successfully");
	}

	
	@Override
	public String getStatus() {
		return null;
	}

	@Override
	public DiameterGatewayConfiguration getConfiguration() {
		return configuration;
	}
	
	@Override
	public void reInit() throws InitializationFailedException{
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Cisco Gx gateway = " + configuration.getName() + " re-initialized successfully");
	}
	
}
