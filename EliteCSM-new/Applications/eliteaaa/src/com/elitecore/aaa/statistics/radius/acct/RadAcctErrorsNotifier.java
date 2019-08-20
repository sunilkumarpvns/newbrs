package com.elitecore.aaa.statistics.radius.acct;

import java.util.Date;

import com.elitecore.aaa.statistics.BaseStatisticsNotifier;
import com.elitecore.aaa.statistics.StatisticsData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class RadAcctErrorsNotifier extends BaseStatisticsNotifier {
	
	private final static String MODULE = "ACCT-REQ-ERRORS-NOTIFIER";
	private RadAcctErrorsData currentRadAcctErrorsData;
	private RadAcctErrorsData oldRadAcctErrorsData;
	
	public RadAcctErrorsNotifier(RadAcctErrorsData radAcctErrorsData){
		super(radAcctErrorsData);
		this.currentRadAcctErrorsData=radAcctErrorsData;
		oldRadAcctErrorsData = currentRadAcctErrorsData;
	}
		
	@Override
	public boolean updateServiceStatisticsData(StatisticsData statisticsData) {
		try{
			RadAcctErrorsData radAcctErrorsData = (RadAcctErrorsData)statisticsData;

			if(oldRadAcctErrorsData != null) {

				currentRadAcctErrorsData.setBadAuthenticators(radAcctErrorsData.getBadAuthenticators()-oldRadAcctErrorsData.getBadAuthenticators());
				currentRadAcctErrorsData.setDuplicateRequests(radAcctErrorsData.getDuplicateRequests()-oldRadAcctErrorsData.getDuplicateRequests());
				currentRadAcctErrorsData.setMalformedRequests(radAcctErrorsData.getMalformedRequests()-oldRadAcctErrorsData.getMalformedRequests());
				currentRadAcctErrorsData.setInvalidRequests(radAcctErrorsData.getInvalidRequests()-oldRadAcctErrorsData.getInvalidRequests());
				currentRadAcctErrorsData.setUnknownRequests(radAcctErrorsData.getUnknownRequests()-oldRadAcctErrorsData.getUnknownRequests());
				currentRadAcctErrorsData.setDropped(radAcctErrorsData.getDropped()-oldRadAcctErrorsData.getDropped());

				currentRadAcctErrorsData.setTimestamp((new Date()).getTime());

			}
			oldRadAcctErrorsData = radAcctErrorsData;
			currentRadAcctErrorsData.notifyObservers();
			return true;
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Error in updating statistics for Acct Request Errors, Reason:"+e.getMessage());
			}
			LogManager.getLogger().trace(MODULE,e);
		}
		return false;
	}
	

	
}
