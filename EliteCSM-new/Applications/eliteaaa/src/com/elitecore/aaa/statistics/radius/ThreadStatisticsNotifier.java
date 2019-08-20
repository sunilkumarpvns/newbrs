package com.elitecore.aaa.statistics.radius;

import java.util.Date;

import com.elitecore.aaa.statistics.BaseStatisticsNotifier;
import com.elitecore.aaa.statistics.StatisticsData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class ThreadStatisticsNotifier extends BaseStatisticsNotifier {
	private final static String MODULE = "THREAD-STATISTICS-NOTIFIER";
	private ThreadStatisticsData currentThreadStatisticsData;
	private ThreadStatisticsData oldThreadStatisticsData;
	
	public ThreadStatisticsNotifier(ThreadStatisticsData threadStatisticsData){
		super(threadStatisticsData);
		this.currentThreadStatisticsData=threadStatisticsData;
		this.oldThreadStatisticsData = currentThreadStatisticsData;
	}
	
	@Override
	public boolean updateServiceStatisticsData(StatisticsData statisticsData) {
		try{
		ThreadStatisticsData threadStatisticsData = (ThreadStatisticsData)statisticsData;
		if(oldThreadStatisticsData!=null){
			
			currentThreadStatisticsData.setActiveThread(threadStatisticsData.getActiveThread() - oldThreadStatisticsData.getActiveThread());
			currentThreadStatisticsData.setTimestamp((new Date()).getTime());
		}	
		oldThreadStatisticsData = threadStatisticsData;
		currentThreadStatisticsData.notifyObservers();
		return true;
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Error in updating statistics for Thread Statistics, Reason:"+e.getMessage());
			}
			LogManager.getLogger().trace(MODULE,e);
		}
		return false;
	}
}
