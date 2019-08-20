package com.elitecore.aaa.core.wimax.keys;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.elitecore.aaa.core.wimax.WimaxRequest;
import com.elitecore.aaa.core.wimax.WimaxResponse;
import com.elitecore.aaa.core.wimax.keys.HAKeyManagerImpl.HAKeyStatistics.HAKeyCounter;

public interface HAKeyManager {
	
	HAKeyDetails getOrCreateHaKeyDetails(String ha_Ip_address, WimaxRequest wimaxRequest,
			WimaxResponse wimaxResponse) throws Exception;
	
	boolean removeHAKey(String string);
	
	@Nullable
	HAKeyDetails getHaKeyDetails(String haIp) throws Exception;
	ConcurrentHashMap<String, HAKeyCounter> getHAKeyStatistics();
}
