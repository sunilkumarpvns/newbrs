package com.elitecore.aaa.statistics;


public interface StatisticsNotifier {
	
	public boolean updateServiceStatisticsData( StatisticsData statisticsData);
	
	public boolean addObserver(String key,StatisticsObserver observer);
	
	public boolean removeExpiredObservers();
	
	public boolean removeObserver(String key);
	
	public StatisticsObserver getObserver(String key);
	
}
