package com.elitecore.aaa.core.conf;

import java.util.List;

import com.elitecore.core.commons.plugins.PluginConfiguration;
import com.elitecore.core.commons.plugins.PluginInfo;

public interface AAAPluginConfManager {
	public List<PluginInfo> getRadPluginInfoList(); 
	public PluginConfiguration getRadPluginConfiguration(String pluginName);
	public List<PluginInfo> getDiameterPluginInfoList();
	public PluginConfiguration getDiameterPluginConfiguration(String pluginName);
}
