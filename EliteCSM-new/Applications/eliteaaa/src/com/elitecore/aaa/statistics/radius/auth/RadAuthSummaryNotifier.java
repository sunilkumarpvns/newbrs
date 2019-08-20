package com.elitecore.aaa.statistics.radius.auth;

import java.util.Date;

import com.elitecore.aaa.statistics.BaseStatisticsNotifier;
import com.elitecore.aaa.statistics.StatisticsData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class RadAuthSummaryNotifier extends BaseStatisticsNotifier {
	private final static String MODULE = "AUTH-SUMMARY-REASONS-NOTIFIER";
	private RadAuthSummaryData currentRadAuthSummaryData;
	private RadAuthSummaryData oldRadAuthSummaryData;
	
	public RadAuthSummaryNotifier(RadAuthSummaryData radAuthSummaryData){
		super(radAuthSummaryData);
		this.currentRadAuthSummaryData=radAuthSummaryData;
		oldRadAuthSummaryData  = currentRadAuthSummaryData;
	}
		
	@Override
	public boolean updateServiceStatisticsData(StatisticsData statisticsData) {
		try{
			RadAuthSummaryData radAuthSummaryData = (RadAuthSummaryData) statisticsData;
			if(oldRadAuthSummaryData != null) {

				currentRadAuthSummaryData.setAccessRequest(radAuthSummaryData.getAccessRequest() - oldRadAuthSummaryData.getAccessRequest());
				currentRadAuthSummaryData.setAccessReject(radAuthSummaryData.getAccessReject() - oldRadAuthSummaryData.getAccessReject());
				currentRadAuthSummaryData.setAccessAccept(radAuthSummaryData.getAccessAccept() - oldRadAuthSummaryData.getAccessAccept());
				currentRadAuthSummaryData.setAccessChallenge(radAuthSummaryData.getAccessChallenge() - oldRadAuthSummaryData.getAccessChallenge());
				currentRadAuthSummaryData.setDropped(radAuthSummaryData.getDropped() - oldRadAuthSummaryData.getDropped());

				currentRadAuthSummaryData.setTimestamp((new Date()).getTime());

			}
			oldRadAuthSummaryData = radAuthSummaryData;
			currentRadAuthSummaryData.notifyObservers();
			return true;
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Error in updating statistics for Auth Service Summary, Reason:"+e.getMessage());
			}
			LogManager.getLogger().trace(MODULE,e);
		}
		return false;
	}
}
