package com.elitecore.aaa.radius.plugins.script;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.manager.scripts.ScriptContext;
import com.elitecore.coreradius.commons.packet.RadiusPacket;

public abstract class ExternalRadiusScript {
	private static final String MODULE = "EXTERNAL_RADIUS_SCRIPT";
	private ScriptContext scriptContext;
	private ServerContext serverContext;
	
	public ExternalRadiusScript(ScriptContext scriptContext){
		this.scriptContext = scriptContext;
		this.serverContext = scriptContext.getServerContext();
	}
	
	public void preRequest(RadServiceRequest originalRequest, RadiusPacket remoteRequest) {
		try{
			pre(originalRequest, remoteRequest);
		}catch (Throwable ex) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Error in executing \"pre\" method of external script: " + getName() + ". Reason: " + ex.getMessage());
			}
			LogManager.getLogger().trace(ex);
		}
	}
	public void postRequest(RadServiceRequest originalRequest, RadiusPacket remoteRequest, RadServiceResponse originalResponse, RadiusPacket remoteResponse) {
		try{
			post(originalRequest, remoteRequest, originalResponse, remoteResponse);
		}catch (Throwable ex) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Error in executing \"post\" method of external script: " + getName() + ". Reason: " + ex.getMessage());
			}
			LogManager.getLogger().trace(ex);
		}
	}
	
	protected final ServerContext getServerContext(){
		return this.serverContext;
	}
	
	protected final ScriptContext getScriptContext(){
		return this.scriptContext;
	}
	
	public abstract void pre(RadServiceRequest originalRequest, RadiusPacket remoteRequest);
	
	public abstract void post(RadServiceRequest originalRequest, RadiusPacket remoteRequest, RadServiceResponse originalResponse, RadiusPacket remoteResponse);
	
	public abstract String getName();
}
