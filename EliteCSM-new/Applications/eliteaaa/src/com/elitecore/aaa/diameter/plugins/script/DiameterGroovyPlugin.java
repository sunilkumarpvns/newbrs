package com.elitecore.aaa.diameter.plugins.script;

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
	public void init() {
		scriptPlugin.init();
	}
	
	@Override
	public void handleInMessage(DiameterPacket diameterRequest,
			DiameterPacket diameterAnswer, ISession session, String argument, PluginCallerIdentity callerID) {
		scriptPlugin.handlePreRequest(diameterRequest, diameterAnswer, argument, callerID);
	}

	@Override
	public void handleOutMessage(DiameterPacket diameterRequest,
			DiameterPacket diameterAnswer, ISession session, String argument, PluginCallerIdentity callerID) {
		scriptPlugin.handlePostRequest(diameterRequest, diameterAnswer, argument, callerID);		
	}
}
