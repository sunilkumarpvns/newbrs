package com.elitecore.aaa.radius.plugins.script;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.commons.plugins.script.ScriptPlugin;

public abstract class RadiusScriptPlugin implements ScriptPlugin{
	private PluginContext pluginContext;
	public RadiusScriptPlugin(PluginContext context){
		this.pluginContext = context;
	}
	
	public PluginContext getPluginContext(){
		return pluginContext;
	}
	
	public abstract void handlePreRequest(RadServiceRequest request, RadServiceResponse response, String argument, PluginCallerIdentity callerId);
	public abstract void handlePostRequest(RadServiceRequest request, RadServiceResponse response, String argument, PluginCallerIdentity callerId);

}
