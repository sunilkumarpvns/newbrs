package com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin;

import java.util.List;

import javax.validation.Valid;

public interface UniversalPluginConfiguration {
	public List<AAAUniversalPluginPolicyDetail> getPrePolicyLists();
	public List<AAAUniversalPluginPolicyDetail> getPostPolicyLists();
}
