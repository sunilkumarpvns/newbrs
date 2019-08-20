package com.elitecore.core.commons.conf.impl;

import com.elitecore.core.commons.plugins.PluginConfiguration;
import com.elitecore.core.serverx.ServerContext;

public abstract class BasePluginConfigurationImpl extends BaseConfigurationImpl
		implements PluginConfiguration {
	
	private String pluginName;
	public BasePluginConfigurationImpl(ServerContext serverContext,String pluginName) {
		super(serverContext);
		this.pluginName = pluginName;
	}
	
	
	
}
