package com.elitecore.aaa.statistics.radius.acct;

import java.util.Date;

import com.elitecore.aaa.statistics.BaseStatisticsNotifier;
import com.elitecore.aaa.statistics.StatisticsData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class RadAcctRespTimeNotifier extends BaseStatisticsNotifier {
	private final static String MODULE = "ACCT-RESP-TIME-NOTIFIER";
	private RadAcctRespTimeData currentRadAcctRespTimeData;
	private RadAcctRespTimeData oldRadAcctRespTimeData;
	
	public RadAcctRespTimeNotifier(RadAcctRespTimeData radAcctRespTimeData){
		super(radAcctRespTimeData);
		this.currentRadAcctRespTimeData=radAcctRespTimeData;
		oldRadAcctRespTimeData = currentRadAcctRespTimeData;
	}
	
	@Override
	public boolean updateServiceStatisticsData(StatisticsData statisticsData) {
		try{
			RadAcctRespTimeData radAcctRespTimeData =(RadAcctRespTimeData) statisticsData;
			if(oldRadAcctRespTimeData!=null){

				currentRadAcctRespTimeData.setDatabaseAvgResponseTime(radAcctRespTimeData.getDatabaseAvgResponseTime() - oldRadAcctRespTimeData.getDatabaseAvgResponseTime());
				currentRadAcctRespTimeData.setRmCommAvgResponseTime(radAcctRespTimeData.getRmCommAvgResponseTime() - oldRadAcctRespTimeData.getRmCommAvgResponseTime());
				currentRadAcctRespTimeData.setTotalAvgResponseTime(radAcctRespTimeData.getTotalAvgResponseTime() - oldRadAcctRespTimeData.getTotalAvgResponseTime());
				currentRadAcctRespTimeData.setPrePluginAvgTime(radAcctRespTimeData.getPrePluginAvgTime() - oldRadAcctRespTimeData.getPrePluginAvgTime());
				currentRadAcctRespTimeData.setPostPluginAvgTime(radAcctRespTimeData.getPostPluginAvgTime() - oldRadAcctRespTimeData.getPostPluginAvgTime());
				currentRadAcctRespTimeData.setQueueAvgTime(radAcctRespTimeData.getQueueAvgTime() - oldRadAcctRespTimeData.getQueueAvgTime());

				currentRadAcctRespTimeData.setTimestamp((new Date()).getTime());

			}
			oldRadAcctRespTimeData = radAcctRespTimeData;
			currentRadAcctRespTimeData.notifyObservers();
			return true;
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Error in updating statistics for Acct Response Time, Reason:"+e.getMessage());
			}
			LogManager.getLogger().trace(MODULE,e);
		}
		return false;
	}

	
	
}
