package com.elitecore.diameterapi.plugins.script;

import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.commons.plugins.script.ScriptPlugin;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;

public abstract class DiameterScriptPlugin implements ScriptPlugin{
	private PluginContext pluginContext;
	public DiameterScriptPlugin(PluginContext context){
		this.pluginContext = context;
	}
	
	public PluginContext getPluginContext(){
		return pluginContext;
	}
	
	public abstract void handleInMessage(DiameterPacket request, DiameterPacket answer, String argument, PluginCallerIdentity callerID);
	public abstract void handleOutMessage(DiameterPacket request, DiameterPacket answer, String argument, PluginCallerIdentity callerID);
}
