package com.elitecore.aaa.diameter.plugins.script;

import com.elitecore.aaa.core.plugins.script.ScriptPlugin;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;

public abstract class DiameterScriptPlugin implements ScriptPlugin{
	private PluginContext pluginContext;
	public DiameterScriptPlugin(PluginContext context, PluginInfo info){
		this.pluginContext = context;
	}
	
	public PluginContext getPluginContext(){
		return pluginContext;
	}
	
	public abstract void handlePreRequest(DiameterPacket request, DiameterPacket answer, String argument, PluginCallerIdentity callerID);
	public abstract void handlePostRequest(DiameterPacket request, DiameterPacket answer, String argument, PluginCallerIdentity callerID);
}
