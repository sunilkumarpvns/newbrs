package com.elitecore.corenetvertex.util;

public interface CacheStatistics {

	long getCacheCount();

	long getHitCount();

	long getEvictionCount();

	long getMissCount();

	long getRequestCount();

	long getAverageLoadPanelty();

	long getLoadCount();
	
	void clear();

	void incrementLoadCount();

}
