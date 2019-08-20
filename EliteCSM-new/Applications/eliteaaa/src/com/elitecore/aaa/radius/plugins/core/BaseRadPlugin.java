package com.elitecore.aaa.radius.plugins.core;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.core.commons.plugins.BasePlugin;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;

public abstract class BaseRadPlugin<T extends RadServiceRequest, V extends RadServiceResponse> extends BasePlugin implements RadPlugin<T, V> {

	public BaseRadPlugin(PluginContext pluginContext, PluginInfo pluginInfo) {
		super(pluginContext, pluginInfo);
	}

}
