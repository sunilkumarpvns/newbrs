package com.elitecore.aaa.statistics.radius.auth;

import java.util.Date;

import com.elitecore.aaa.statistics.BaseStatisticsNotifier;
import com.elitecore.aaa.statistics.StatisticsData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class RadAuthRespTimeNotifier extends BaseStatisticsNotifier{
	private final static String MODULE = "AUTH-RESP-TIME-NOTIFIER";
	private RadAuthRespTimeData currentRadAuthRespTimeData;
	private RadAuthRespTimeData oldRadAuthRespTimeData;
	
	public RadAuthRespTimeNotifier(RadAuthRespTimeData radAuthRespTimeData){
		super(radAuthRespTimeData);
		this.currentRadAuthRespTimeData=radAuthRespTimeData;
		oldRadAuthRespTimeData= currentRadAuthRespTimeData;
	}

	@Override
	public boolean updateServiceStatisticsData(StatisticsData statisticsData) {
		try{
			RadAuthRespTimeData radAuthRespTimeData = (RadAuthRespTimeData) statisticsData;
			if(oldRadAuthRespTimeData!=null){

				currentRadAuthRespTimeData.setLdapAvgResponseTime(radAuthRespTimeData.getLdapAvgResponseTime() - oldRadAuthRespTimeData.getLdapAvgResponseTime());
				currentRadAuthRespTimeData.setDatabaseAvgResponseTime(radAuthRespTimeData.getDatabaseAvgResponseTime() - oldRadAuthRespTimeData.getDatabaseAvgResponseTime());
				currentRadAuthRespTimeData.setResourceServerAvgResponseTime(radAuthRespTimeData.getResourceServerAvgResponseTime() - oldRadAuthRespTimeData.getResourceServerAvgResponseTime());
				currentRadAuthRespTimeData.setTotalAvgResponseTime(radAuthRespTimeData.getTotalAvgResponseTime() - oldRadAuthRespTimeData.getTotalAvgResponseTime());
				currentRadAuthRespTimeData.setPrePluginAvgTime(radAuthRespTimeData.getPrePluginAvgTime() - oldRadAuthRespTimeData.getPrePluginAvgTime());
				currentRadAuthRespTimeData.setPostPluginAvgTime(radAuthRespTimeData.getPostPluginAvgTime() - oldRadAuthRespTimeData.getPostPluginAvgTime());
				currentRadAuthRespTimeData.setQueueAvgTime(radAuthRespTimeData.getQueueAvgTime() - oldRadAuthRespTimeData.getQueueAvgTime());
				currentRadAuthRespTimeData.setUsersFileAvgResponseTime(radAuthRespTimeData.getUsersFileAvgResponseTime() - oldRadAuthRespTimeData.getUsersFileAvgResponseTime());
				currentRadAuthRespTimeData.setTimestamp((new Date()).getTime());

			}
			oldRadAuthRespTimeData = radAuthRespTimeData;
			currentRadAuthRespTimeData.notifyObservers();
			return true;
		}
		catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Error in updating statistics for Auth Response Time, Reason:"+e.getMessage());
			}
			LogManager.getLogger().trace(MODULE,e);
		}

		return false;
	}
}
