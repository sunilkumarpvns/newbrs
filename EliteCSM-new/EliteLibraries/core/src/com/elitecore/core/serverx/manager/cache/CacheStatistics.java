package com.elitecore.core.serverx.manager.cache;

import java.util.Date;

public interface CacheStatistics {	
	
	public Date getLastRefreshDate();
	
	public Date getLastSuccessfulRefreshDate();
	
	public String getName();
	
	public String getDescription();
	
	public int getResultCode();
	
	public String getSource();
}
