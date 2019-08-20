package com.elitecore.core.commons.plugins;

import com.elitecore.core.commons.InitializationFailedException;

public interface Plugin {

	public void init() throws InitializationFailedException;
	
	public String getPluginName();
	public String getPluginClass();
	public String getDescription();
	public PluginContext getPluginContext();
	
}
