package com.elitecore.aaa.statistics.radius.auth;

import java.util.Date;

import com.elitecore.aaa.statistics.BaseStatisticsNotifier;
import com.elitecore.aaa.statistics.StatisticsData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class RadAuthErrorsNotifier extends BaseStatisticsNotifier{
	
	private final static String MODULE = "AUTH-REQ-ERRORS-NOTIFIER";
	private RadAuthErrorsData currentRadAuthErrorsData;
	private RadAuthErrorsData oldRadAuthErrorsData;
	
	public RadAuthErrorsNotifier(RadAuthErrorsData radAuthErrorsData){
		super(radAuthErrorsData);
		this.currentRadAuthErrorsData=radAuthErrorsData;
		oldRadAuthErrorsData = currentRadAuthErrorsData;
	}
	

	@Override
	public boolean updateServiceStatisticsData(StatisticsData statisticsData) {
		try{
			RadAuthErrorsData radAuthErrorsData = (RadAuthErrorsData) statisticsData;
			if(oldRadAuthErrorsData != null) {

				currentRadAuthErrorsData.setBadAuthenticators(radAuthErrorsData.getBadAuthenticators()-oldRadAuthErrorsData.getBadAuthenticators());
				currentRadAuthErrorsData.setDuplicateRequests(radAuthErrorsData.getDuplicateRequests()-oldRadAuthErrorsData.getDuplicateRequests());
				currentRadAuthErrorsData.setMalformedRequests(radAuthErrorsData.getMalformedRequests()-oldRadAuthErrorsData.getMalformedRequests());
				currentRadAuthErrorsData.setInvalidRequests(radAuthErrorsData.getInvalidRequests()-oldRadAuthErrorsData.getInvalidRequests());
				currentRadAuthErrorsData.setUnknownRequests(radAuthErrorsData.getUnknownRequests()-oldRadAuthErrorsData.getUnknownRequests());
				currentRadAuthErrorsData.setDropped(radAuthErrorsData.getDropped()-oldRadAuthErrorsData.getDropped());

				currentRadAuthErrorsData.setTimestamp((new Date()).getTime());
			}

			oldRadAuthErrorsData = radAuthErrorsData;
			currentRadAuthErrorsData.notifyObservers();

			return true;
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Error in updating statistics for Auth Request Errors, Reason:"+e.getMessage());
			}
			LogManager.getLogger().trace(MODULE,e);
		}
		return false;
	}
	
}
