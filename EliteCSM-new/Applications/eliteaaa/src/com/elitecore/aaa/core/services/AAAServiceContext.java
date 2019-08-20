package com.elitecore.aaa.core.services;

import java.util.List;

import com.elitecore.aaa.core.plugins.PluginRequestHandler;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.servicex.ServiceContext;

public interface AAAServiceContext extends ServiceContext {
	public PluginRequestHandler createPluginRequestHandler(List<PluginEntryDetail> prePluginList, List<PluginEntryDetail> postPluginList);
}
