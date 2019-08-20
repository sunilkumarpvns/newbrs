package com.elitecore.aaa.core.plugins.conf;

import java.util.Map;

import com.elitecore.core.commons.config.core.Configurable;

public abstract class BasePluginConfigurable<T> extends Configurable {
	
	public abstract void createPlugin(Map<String, T> nameToPlugin);

}
