package com.elitecore.netvertex.core.groovy;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;

public abstract class ServerGroovyScript extends BaseIntervalBasedTask{
	
	private static final String MODULE = "SRV-GRV-SCRPT";
	protected GroovyContext groovyContext;
	
	public ServerGroovyScript(GroovyContext groovyContext){
		this.groovyContext = groovyContext;
	}
	
	public void init(String arg) throws InitializationFailedException{
		if(getLogger().isLogLevel(LogLevel.INFO))
			getLogger().info(MODULE, "Server Groovy Scripts \"" + getName() + "\" initialized successfully");
	}
	
	protected ILogger getLogger(){
		return groovyContext.getLogger();
	}
	
	public abstract String getName();
}
