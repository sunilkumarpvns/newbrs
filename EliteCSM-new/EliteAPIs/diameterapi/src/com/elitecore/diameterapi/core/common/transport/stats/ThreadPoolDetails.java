package com.elitecore.diameterapi.core.common.transport.stats;

public interface ThreadPoolDetails {

	int getMinSize();
	int getMaxSize();
	int getActiveCount();
	int getPoolSize();
	int getPeakPoolSize();
	int getQueueSize();
}
