package com.elitecore.core.systemx.esix.statistics;

public interface ESIStatistics {
	public static final String ALIVE = "ALIVE";
	public static final String DEAD = "DEAD";
	
	
	public String getName();
	
	public String getTypeName();
	
	public String currentStatus();
	
	public float getLastMinAvgResponseTime();
	
	public float getLastTenMinAvgResponseTime();
	
	public float getLastHourAvgResponseTime();
	
	public long getLastScanTimestamp();
	
	public long getLastDeadTimestamp();
	
	public long getTotalTimedouts();
	
	public long getTotalRequests();
	
	public long getTotalSuccesses();
	
	public long getTotalErrors();
	
	public long getDeadCount();

	long getLastMarkDeadTimestamp();
	
}
