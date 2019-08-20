package com.elitecore.aaa.core.wimax.keys;

import java.util.concurrent.ConcurrentHashMap;

import com.elitecore.aaa.core.wimax.keys.HAKeyManagerImpl.HAKeyStatistics;

class HAInMemoryAccessObject {
	
	private final HAKeyStatistics statistics;
	private ConcurrentHashMap<String, HAKeyDetails> haKeys = new ConcurrentHashMap<String, HAKeyDetails>();

	public HAInMemoryAccessObject(HAKeyStatistics statistics) {
		this.statistics = statistics;
	}
	
	public HAKeyDetails get(String haIpAddress) {
		HAKeyDetails haKeyDetails = haKeys.get(haIpAddress);
		if (haKeyDetails == null) {
			statistics.getHAKeyCounter(haIpAddress).incrementCacheMissCount();
		}
		return haKeyDetails;
	}

	public boolean delete(String haIpAddress) {
		return haKeys.remove(haIpAddress) != null;
	}

	public void save(String haIpAddress, HAKeyDetails details) {
		haKeys.put(haIpAddress, details);
	}

}
