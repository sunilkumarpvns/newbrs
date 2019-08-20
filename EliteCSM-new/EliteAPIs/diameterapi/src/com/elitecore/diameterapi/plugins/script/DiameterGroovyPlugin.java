package com.elitecore.diameterapi.plugins.script;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.plugins.BaseDiameterPlugin;

public class DiameterGroovyPlugin extends BaseDiameterPlugin{

	private DiameterScriptPlugin scriptPlugin;
	public DiameterGroovyPlugin(DiameterScriptPlugin scriptPlugin,PluginContext pluginContext, PluginInfo pluginInfo) {
		super(pluginContext, pluginInfo);
		this.scriptPlugin = scriptPlugin;
	}
	
	@Override
	public void handleInMessage(DiameterPacket diameterRequest,
			DiameterPacket diameterAnswer, ISession session, String argument, PluginCallerIdentity callerID) {
		try{
			scriptPlugin.handleInMessage(diameterRequest, diameterAnswer, argument, callerID);
		}catch(Throwable ex){
			LogManager.getLogger().trace(ex);
		}
	}

	@Override
	public void handleOutMessage(DiameterPacket diameterRequest,
			DiameterPacket diameterAnswer, ISession session, String argument, PluginCallerIdentity callerID) {
		try{
			scriptPlugin.handleOutMessage(diameterRequest, diameterAnswer, argument, callerID);
		}catch(Throwable ex){
			LogManager.getLogger().trace(ex);
		}
	}


	@Override
	public void init() throws InitializationFailedException {
		try{
			scriptPlugin.init();
		}catch(Throwable ex){
			LogManager.getLogger().trace(ex);
			throw new InitializationFailedException(ex.getMessage());
		}
	}
}
