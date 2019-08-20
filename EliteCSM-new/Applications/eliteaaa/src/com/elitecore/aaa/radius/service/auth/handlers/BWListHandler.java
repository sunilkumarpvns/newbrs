package com.elitecore.aaa.radius.service.auth.handlers;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.aaa.radius.conf.RadBWListConfiguration;
import com.elitecore.aaa.radius.conf.impl.RadBWListConfigurable;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider;
import com.elitecore.core.serverx.manager.cache.Cacheable;

public class BWListHandler implements Cacheable {
	private static final String MODULE = "RAD-BW-HDR"; 
	private RadBWListConfiguration radBWListConfiguration;
	private RadAuthServiceContext serviceContext;
	private Map<Integer, BlackListValidator> handlerMap ;
	
	
	public BWListHandler(RadAuthServiceContext authServiceContext) {
		this.serviceContext = authServiceContext;
		this.handlerMap = new HashMap<Integer, BlackListValidator>();
	}
	
	public void init() {
		this.radBWListConfiguration = this.serviceContext.getAuthConfiguration().getBwListConfiguration();
		for(BWMode mode:BWMode.VALUES){
			BlackListValidator baseBlackHandler = BWMode.getHandler(mode.id);
			if(baseBlackHandler!=null){
				baseBlackHandler.init(radBWListConfiguration.getAttributeDetailList());
				handlerMap.put(mode.id, baseBlackHandler);
			}
		}
	}
	
	public boolean isBlockedUser(RadAuthRequest request,BWMode mode){
		if(this.radBWListConfiguration.isInitialized() && mode!=null && handlerMap.get(mode.id)!=null && handlerMap.get(mode.id).isBlockedUser(request)){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().trace(MODULE, "User is black Listed");
			return true;
		}
		return false;
	}

	@Override
	public CacheDetail reloadCache() {
		CacheDetailProvider cacheDetail = new CacheDetailProvider();
		cacheDetail.setName(getName());
		cacheDetail.setSource("AAA_Server_DS");
		try {
			RadBWListConfigurable configurable = (RadBWListConfigurable)this.radBWListConfiguration;
			configurable.readFromDB();
			configurable.postReadProcessing();
			init();
			cacheDetail.setResultCode(CacheConstants.SUCCESS);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Reloaded blacklist and whitelist clients successfully");
			}
		} catch(Exception e) {
			cacheDetail.setResultCode(CacheConstants.FAIL);
			cacheDetail.setDescription("Failed to reload BW list, Reason: " + e.getMessage());
			LogManager.getLogger().error(MODULE, "Reloading blacklist and whitelist clients Failed, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}
		return cacheDetail;
	}

	@Override
	public String getName() {
		return "BLACKLIST-CANDIDATES";
	}
}
