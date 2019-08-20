package com.elitecore.aaa.core.plugins.conf;

import java.util.List;

import com.elitecore.aaa.core.plugins.AAAUniversalPluginPolicyDetail;
import com.elitecore.core.commons.plugins.PluginConfiguration;

public interface UniversalPluginConfiguration extends PluginConfiguration{
	public List<AAAUniversalPluginPolicyDetail> getPrePolicyLists();
	public List<AAAUniversalPluginPolicyDetail> getPostPolicyLists();

}
