package com.elitecore.diameterapi.plugins.universal.conf;

import java.util.List;

import com.elitecore.core.commons.plugins.PluginConfiguration;
import com.elitecore.diameterapi.plugins.DiameterUniversalPluginPolicyDetail;

public interface UniversalDiameterPluginConf extends PluginConfiguration{
	public List<DiameterUniversalPluginPolicyDetail> getInPluginList();
	public List<DiameterUniversalPluginPolicyDetail> getOutPluginList();
	
}
