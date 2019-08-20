package com.elitecore.aaa.statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observer;

import com.elitecore.commons.logging.LogManager;

public abstract class BaseStatisticsNotifier implements StatisticsNotifier {
	private static String MODULE = "STATISTICS-NOTIFIER";
	private StatisticsData currentRadAuthSummaryData;
	private Map<String,StatisticsObserver> observerMap = new HashMap<String,StatisticsObserver>();
	
	public BaseStatisticsNotifier(StatisticsData radAuthSummaryData){
		this.currentRadAuthSummaryData=radAuthSummaryData;
	}
	
	public boolean addObserver(String key,StatisticsObserver observer){
		currentRadAuthSummaryData.addObserver(observer);
		observerMap.put(key,observer);
		return true;
	}

	public boolean removeExpiredObservers(){
		if(observerMap==null || observerMap.isEmpty()){
			return false;
		}
		for(Entry<String, StatisticsObserver> entry : observerMap.entrySet()){
			if(entry.getValue().isExpired()){
			
				boolean isRemoved = removeObserver(entry.getKey());
				if(isRemoved){
					LogManager.getLogger().debug(MODULE, "removed : observer ["+entry.getKey()+"]");
				}
			}
		}
		return true;
	}
	public boolean removeObserver(String key){
		Observer observer = observerMap.get(key);
		if(observer!=null){
			currentRadAuthSummaryData.deleteObserver(observer);
			observerMap.remove(key);
			return true;
		}
		return false;
	}
	
	public StatisticsObserver getObserver(String key){
		return observerMap.get(key);
	}

}
