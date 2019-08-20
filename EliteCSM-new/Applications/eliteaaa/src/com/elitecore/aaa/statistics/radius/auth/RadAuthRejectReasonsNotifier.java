package com.elitecore.aaa.statistics.radius.auth;

import java.util.Date;

import com.elitecore.aaa.statistics.BaseStatisticsNotifier;
import com.elitecore.aaa.statistics.StatisticsData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class RadAuthRejectReasonsNotifier extends BaseStatisticsNotifier{
	private final static String MODULE = "AUTH-REJECT-REASONS-NOTIFIER";
	private	RadAuthRejectReasonsData currentRadAuthRejectReasonsData;
	private RadAuthRejectReasonsData oldRadAuthRejectReasonsData;
	
	public RadAuthRejectReasonsNotifier(RadAuthRejectReasonsData radAuthRejectReasonsData){
		super(radAuthRejectReasonsData);
		this.currentRadAuthRejectReasonsData=radAuthRejectReasonsData;
		oldRadAuthRejectReasonsData = currentRadAuthRejectReasonsData;
	}
	
	public boolean updateServiceStatisticsData( StatisticsData statisticsData){
		try{
			RadAuthRejectReasonsData radAuthRejectReasonsData = (RadAuthRejectReasonsData)statisticsData;
			if(oldRadAuthRejectReasonsData != null) {

				currentRadAuthRejectReasonsData.setAccountExpired(radAuthRejectReasonsData.getAccountExpired() - oldRadAuthRejectReasonsData.getAccountExpired());
				currentRadAuthRejectReasonsData.setUserNotFound(radAuthRejectReasonsData.getUserNotFound() - oldRadAuthRejectReasonsData.getUserNotFound());
				currentRadAuthRejectReasonsData.setAccountIsNotActive(radAuthRejectReasonsData.getAccountIsNotActive() - oldRadAuthRejectReasonsData.getAccountIsNotActive());
				currentRadAuthRejectReasonsData.setCreditLimitExceeded(radAuthRejectReasonsData.getCreditLimitExceeded() - oldRadAuthRejectReasonsData.getCreditLimitExceeded());
				currentRadAuthRejectReasonsData.setDigestFailure(radAuthRejectReasonsData.getDigestFailure() - oldRadAuthRejectReasonsData.getDigestFailure());
				currentRadAuthRejectReasonsData.setRmCommTimeOut(radAuthRejectReasonsData.getRmCommTimeOut() - oldRadAuthRejectReasonsData.getRmCommTimeOut());
				currentRadAuthRejectReasonsData.setInvalidMSCHAPv1Password(radAuthRejectReasonsData.getInvalidMSCHAPv1Password() - oldRadAuthRejectReasonsData.getInvalidMSCHAPv1Password());
				currentRadAuthRejectReasonsData.setInvalidMSCHAPv2Password(radAuthRejectReasonsData.getInvalidMSCHAPv2Password() - oldRadAuthRejectReasonsData.getInvalidMSCHAPv2Password());
				currentRadAuthRejectReasonsData.setInvalidCHAPPassword(radAuthRejectReasonsData.getInvalidCHAPPassword() - oldRadAuthRejectReasonsData.getInvalidCHAPPassword() );
				currentRadAuthRejectReasonsData.setEapFailure(radAuthRejectReasonsData.getEapFailure() - oldRadAuthRejectReasonsData.getEapFailure());
				currentRadAuthRejectReasonsData.setInvalidPassword(radAuthRejectReasonsData.getInvalidPassword() - oldRadAuthRejectReasonsData.getInvalidPassword());

				currentRadAuthRejectReasonsData.setTimestamp((new Date()).getTime());

			}

			oldRadAuthRejectReasonsData = radAuthRejectReasonsData;
			currentRadAuthRejectReasonsData.notifyObservers();

			return true;
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Error in updating statistics for Auth Reject Errors, Reason:"+e.getMessage());
			}
			LogManager.getLogger().trace(MODULE,e);
		}
		return false;
	}

	
}
