package com.elitecore.core.commons.plugins.script;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.manager.scripts.ScriptContext;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;

public abstract class DriverScript {
	
	private static final String MODULE = "DRIVER_SCRIPT";
	private ScriptContext scriptContext;
	
	public DriverScript(ScriptContext scriptContext){
		this.scriptContext = scriptContext;
	}
	
	protected final ScriptContext getScriptContext(){
		return this.scriptContext;
	}
	
	public final void preDriverProcessing(ServiceRequest serviceRequest,ServiceResponse serviceResponse){
		try{
			pre(serviceRequest, serviceResponse);
		}catch(Throwable ex){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Error in executing \"pre\" method of driver script: " + getName() + ". Reason: " + ex.getMessage());
			}
			LogManager.getLogger().trace(ex);
		}
	}
	
	public final void postDriverProcessing(ServiceRequest serviceRequest, ServiceResponse serviceResponse){
		try{
			post(serviceRequest, serviceResponse);
		}catch(Throwable ex){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Error in executing \"post\" method of driver script: " + getName() + ". Reason: " + ex.getMessage());
			}
			LogManager.getLogger().trace(ex);
		}
	}
	
	/**
	 * NOTE: This is the most important method to be implemented. 
	 * The name given here will be used to identify the script, the same name MUST be
	 * configured in GUI for executing the script.
	 * Do not use spaces in the name.
	**/
	public abstract String getName();
	
	protected abstract void pre(ServiceRequest serviceRequest, ServiceResponse serviceResponse);
	/**
	 * This hook is called after the driver processing is completed
	**/
	protected abstract void post(ServiceRequest serviceRequest, ServiceResponse serviceResponse);
	
}
