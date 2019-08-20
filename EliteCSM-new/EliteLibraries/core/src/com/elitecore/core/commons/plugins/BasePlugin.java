package com.elitecore.core.commons.plugins;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.ReInitializable;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider;
import com.elitecore.core.serverx.manager.cache.Cacheable;


public abstract class BasePlugin implements Plugin, Cacheable ,ReInitializable{
	private PluginContext pluginContext;
	private PluginInfo pluginInfo;
	public BasePlugin(PluginContext pluginContext,PluginInfo pluginInfo){
		this.pluginContext = pluginContext;
		this.pluginInfo = pluginInfo;
	}
	
	public PluginContext getPluginContext(){
		return this.pluginContext;
	}
	protected final PluginConfiguration getPluginConfiguration(){
		return this.pluginContext.getPluginConfiguration(getPluginName());
	}
	@Override
	public final String getPluginName(){
		return this.pluginInfo.getPluginName();
	}
	@Override
	public final String getPluginClass(){
		return this.pluginInfo.getPluginClass();
	}
	@Override
	public final String getDescription(){
		return this.pluginInfo.getDescription();
	}
	public String getName(){
		return getPluginName();
	}
	@Override
	public CacheDetail reloadCache() {
		CacheDetailProvider cacheDetail = new CacheDetailProvider();
		cacheDetail.setName(getPluginName());
		cacheDetail.setResultCode(CacheConstants.SUCCESS);
		cacheDetail.setSource("--");
		cacheDetail.setDescription("Reload cache of BasePlugin is called.");
		return cacheDetail;
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		
	}
}
