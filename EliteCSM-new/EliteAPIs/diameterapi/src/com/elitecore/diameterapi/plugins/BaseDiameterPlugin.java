package com.elitecore.diameterapi.plugins;

import com.elitecore.core.commons.plugins.BasePlugin;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.diameterapi.plugins.DiameterPlugin;

public abstract class BaseDiameterPlugin extends BasePlugin implements DiameterPlugin {
	
	public BaseDiameterPlugin(PluginContext pluginContext, PluginInfo pluginInfo) {
		super(pluginContext, pluginInfo);
	}
	public String toString(){ 
		return getPluginName();
	}
	
}
