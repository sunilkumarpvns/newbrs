package com.elitecore.aaa.core.wimax.keys;

import javax.annotation.Nullable;

import com.elitecore.aaa.core.conf.WimaxConfiguration;
import com.elitecore.aaa.core.wimax.WimaxRequest;
import com.elitecore.aaa.core.wimax.WimaxResponse;
import com.elitecore.commons.logging.LogManager;

public class HAInMemoryKeyManager extends HAKeyManagerImpl {
	private static final String MODULE = "WIMAX-IN-MEM-KEY-MGR";
	
	HAInMemoryAccessObject inMemoryCache = new HAInMemoryAccessObject(haKeyStatistics);
	
	public HAInMemoryKeyManager(WimaxConfiguration wimaxConfiguration) {
		super(wimaxConfiguration);
	}

	@Override
	public HAKeyDetails getOrCreateHaKeyDetails(String ha_Ip_address,
			WimaxRequest wimaxRequest, WimaxResponse wimaxResponse) {
		HAKeyDetails haKeyDetails = inMemoryCache.get(ha_Ip_address);
		if (haKeyDetails == null) {
			synchronized (this) {
				haKeyDetails = inMemoryCache.get(ha_Ip_address);
				if (haKeyDetails == null) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "HA Key details not found in cache for ha-ip: " + ha_Ip_address 
								+ ". So, creating Keys");
					}
					haKeyDetails = generateHaKeyDetails(ha_Ip_address, wimaxRequest, wimaxResponse);
					inMemoryCache.save(ha_Ip_address, haKeyDetails);
				}
			}
		} else if (haKeyDetails.isKeyExpired(wimaxRequest.getRequestReceivedTimeInMillis())) {
			synchronized (this) {
				haKeyDetails = inMemoryCache.get(ha_Ip_address);
				if (haKeyDetails == null || haKeyDetails.isKeyExpired(wimaxRequest.getRequestReceivedTimeInMillis())) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "HA Keys are expired for ha-ip: " + ha_Ip_address 
								+ ". So, creating new Keys");
					}
					haKeyDetails = generateHaKeyDetails(ha_Ip_address, wimaxRequest, wimaxResponse);
				    inMemoryCache.save(ha_Ip_address, haKeyDetails);
				}
			}
		} else {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE," Keys already cached for " + ha_Ip_address) ;
			}
		}
		return haKeyDetails;
	}

	@Override
	public boolean removeHAKey(String haip) {
		return inMemoryCache.delete(haip);
	}

	@Override
	@Nullable
	public HAKeyDetails getHaKeyDetails(String haIp) {
		return inMemoryCache.get(haIp);
	}
}
