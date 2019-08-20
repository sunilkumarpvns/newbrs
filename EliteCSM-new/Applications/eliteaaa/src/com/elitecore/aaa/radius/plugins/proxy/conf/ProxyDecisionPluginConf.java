package com.elitecore.aaa.radius.plugins.proxy.conf;

import com.elitecore.core.commons.plugins.PluginConfiguration;


public interface ProxyDecisionPluginConf extends PluginConfiguration {

	public String getDataSourceName();
	public String getTableName();
	public String getUserIdentity();
}
