package com.elitecore.netvertex.gateway.diameter.plugin.conf.impl;

import com.elitecore.core.commons.conf.impl.BasePluginConfigurationImpl;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.netvertex.gateway.diameter.plugin.conf.MCCMNCDiameterPluginConfiguration;

public class MCCMNCDiameterPluginConfigurationImpl extends BasePluginConfigurationImpl implements MCCMNCDiameterPluginConfiguration {

	public MCCMNCDiameterPluginConfigurationImpl(ServerContext serverContext, String pluginName) {
		super(serverContext, pluginName);
	}


	@Override
	public void readConfiguration() throws LoadConfigurationException {
		//no need to handle
	}

}
