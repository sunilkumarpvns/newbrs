package com.elitecore.aaa.statistics.radius;

import java.util.Date;

import com.elitecore.aaa.statistics.BaseStatisticsNotifier;
import com.elitecore.aaa.statistics.StatisticsData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class MemoryUsageNotifier extends BaseStatisticsNotifier{
	private final static String MODULE = "MEMORY-USAGE-NOTIFIER";
	private MemoryUsageData currentMemoryUsageData;
	private MemoryUsageData oldMemoryUsageData;
	
	public MemoryUsageNotifier(MemoryUsageData memoryUsageData){
		super(memoryUsageData);
		this.currentMemoryUsageData=memoryUsageData;
		oldMemoryUsageData = currentMemoryUsageData;
	}
	

	@Override
	public boolean updateServiceStatisticsData(StatisticsData statisticsData) {
		try{
			MemoryUsageData memoryUsageData = (MemoryUsageData)statisticsData;
			if(oldMemoryUsageData!=null){
				currentMemoryUsageData.setTotalMemoryUsed(memoryUsageData.getTotalMemoryUsed()-oldMemoryUsageData.getTotalMemoryUsed());
				currentMemoryUsageData.setTimestamp((new Date()).getTime());
			}
			oldMemoryUsageData=memoryUsageData;
			currentMemoryUsageData.notifyObservers();
			return true;
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Error in updating statistics for Memory Usage, Reason:"+e.getMessage());
			}
			LogManager.getLogger().trace(MODULE,e);
		}
		return false;
	}
}
