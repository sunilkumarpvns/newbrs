package com.elitecore.core.commons.plugins;

import com.elitecore.core.serverx.ServerContext;

public interface PluginContext {
	public ServerContext getServerContext();
	public PluginConfiguration getPluginConfiguration(String pluginName);
}
