package com.elitecore.core.serverx.sessionx;

public interface SystemPropertiesProvider {
	
	boolean isBatchEnabled();
	boolean isNoWaitEnabled();
	int getQueryTimeout();
	int getBatchQueryTimeout();
}
