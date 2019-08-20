package com.elitecore.netvertex.gateway.diameter.scripts;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

public abstract class DiameterGroovyScript {
	
	protected DiameterGatewayControllerContext context;
	protected DiameterGatewayConfiguration configuration;
	private static final String MODULE = "DIA-GROOVY-SCRIPT";
	
	public DiameterGroovyScript(DiameterGatewayControllerContext diameterGatewayControllerContext, DiameterGatewayConfiguration configuration){
		this.context = diameterGatewayControllerContext;
		this.configuration = configuration;
	}

	
	public void init(String arg) throws InitializationFailedException{
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Diameter Groovy Scripts \"" + getName() + "\" for gateway = " + configuration.getHostIdentity() + "initialized successfully");
	}
	
	
	public void postReceived(DiameterPacket diameterPacket){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "postReceived of \"" + getName() + "\" for gateway = " + configuration.getHostIdentity() + " and command code "+getCommandCodeDisplayName(diameterPacket)+" called");
		
		
	}
	
	public void postReceived(DiameterPacket diameterPacket, PCRFRequest pcrfRequest){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "postReceived of \"" + getName() + "\" for gateway = " + configuration.getHostIdentity() + " and command code "+getCommandCodeDisplayName(diameterPacket)+" called");

	}
	//Used in Sy call flow for handling groovy in SLA scenario
	public void postReceived(DiameterPacket diameterPacket, PCRFResponse pcrfResponse){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "postReceived of diameter packet and pcrfResponse\"" + getName() + "\" for gateway = " + configuration.getHostIdentity() + " and command code "+getCommandCodeDisplayName(diameterPacket)+" called");

	}
	
	public void preSend(DiameterPacket diameterPacket){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "preSend of \"" + getName() + "\" for gateway = " + configuration.getHostIdentity() + " and command code "+getCommandCodeDisplayName(diameterPacket)+" called");

	}
	
	public void preSend(DiameterPacket diameterPacket, PCRFResponse pcrfResponse){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "preSend of \"" + getName() + "\" for gateway = " + configuration.getHostIdentity() + " and command code "+getCommandCodeDisplayName(diameterPacket)+" called");

	}
	
	public void preSend(PCRFResponse pcrfResponse){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "preSend of  \"" + getName() + "\" for gateway = " + configuration.getHostIdentity() + " called");

	}

	public String getCommandCodeDisplayName(DiameterPacket diameterPacket) {
		String cmd = CommandCode.getDisplayName(diameterPacket.getCommandCode());
		if(diameterPacket.isRequest()) {
			cmd += "R";
		} else {
			cmd += "A";
		}
		cmd += "(" + diameterPacket.getCommandCode() + ")";
		return cmd;
	}

	public abstract String getName();

}
