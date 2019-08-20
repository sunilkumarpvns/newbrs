package com.elitecore.aaa.radius.plugins.strop.conf;

import java.util.List;
import java.util.Map;

import com.elitecore.core.commons.plugins.PluginConfiguration;


public interface StringOperationPluginConf extends PluginConfiguration {
	public List<Map<String, Object>> getOperationDetails();
}
