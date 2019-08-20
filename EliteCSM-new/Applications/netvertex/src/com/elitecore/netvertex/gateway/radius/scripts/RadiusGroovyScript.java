package com.elitecore.netvertex.gateway.radius.scripts;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.netvertex.gateway.radius.RadiusGatewayControllerContext;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceRequest;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceResponse;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

public abstract class RadiusGroovyScript {
	
	private static final String MODULE = "RAD-GROOVY-SCRIPT";
	protected RadiusGatewayControllerContext context;
	protected RadiusGatewayConfiguration configuration;
	
	
	public RadiusGroovyScript(RadiusGatewayControllerContext radiusGatewayControllerContext, RadiusGatewayConfiguration configuration){
		this.context = radiusGatewayControllerContext;
		this.configuration = configuration;
	}

	
	public void init(String arg) throws InitializationFailedException{
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Radius Groovy Scripts \"" + getName() + "\" for gateway = "+ configuration.getIPAddress() +" initialized successfully");
	}
	
	
	public void postReceived(RadServiceRequest request, RadServiceResponse response){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "postReceived of \"" + getName() + "\" for gateway = "+ configuration.getIPAddress() + " called");
		
		
	}
	
	public void postReceived(RadServiceRequest request, RadServiceResponse response, PCRFRequest pcrfRequest){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "postReceived  of \"" + getName() + "\" for gateway = "+ configuration.getIPAddress() + " called");

	}
	
	public void preSend(PCRFResponse pcrfResponse, RadiusPacket radiusPacket){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "preSend of \"" + getName() + "\" for gateway = "+ configuration.getIPAddress() + " called");

	}
	
	public void preSend(PCRFResponse pcrfResponse){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "preSend of \"" + getName() + "\" for gateway = "+ configuration.getIPAddress() + " called");

	}
	
	public abstract String getName();

}
