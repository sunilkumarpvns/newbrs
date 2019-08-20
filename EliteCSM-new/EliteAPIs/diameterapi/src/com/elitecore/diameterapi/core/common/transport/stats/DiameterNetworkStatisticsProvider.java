package com.elitecore.diameterapi.core.common.transport.stats;

public interface DiameterNetworkStatisticsProvider {

	ThreadPoolDetails getBaseThreadPoolDetails();
	
	ThreadPoolDetails getMessageThreadPoolDetails();
}
