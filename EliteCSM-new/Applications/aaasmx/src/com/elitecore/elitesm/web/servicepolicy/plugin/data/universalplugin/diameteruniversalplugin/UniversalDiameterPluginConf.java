package com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.diameteruniversalplugin;

import java.util.List;

public interface UniversalDiameterPluginConf{
	public List<DiameterUniversalPluginPolicyDetail> getInPluginList();
	public List<DiameterUniversalPluginPolicyDetail> getOutPluginList();
	
}
