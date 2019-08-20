package com.elitecore.aaa.statistics.radius.acct;

import java.util.Date;

import com.elitecore.aaa.statistics.BaseStatisticsNotifier;
import com.elitecore.aaa.statistics.StatisticsData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class RadAcctSummaryNotifier extends BaseStatisticsNotifier {
	
	private final static String MODULE = "ACCT-REQ-ERRORS-NOTIFIER";
	private RadAcctSummaryData currentRadAcctSummaryData;
	private RadAcctSummaryData oldRadAcctSummaryData;
	
	public RadAcctSummaryNotifier(RadAcctSummaryData radAcctSummaryData){
		super(radAcctSummaryData);
		this.currentRadAcctSummaryData=radAcctSummaryData;
		oldRadAcctSummaryData = currentRadAcctSummaryData;
	}
		
	@Override
	public boolean updateServiceStatisticsData(StatisticsData statisticsData) {

		try{
			RadAcctSummaryData radAcctSummaryData = (RadAcctSummaryData)statisticsData;
			if(oldRadAcctSummaryData != null) {
				long lastUpdatedDropped =  radAcctSummaryData.getDropped() - oldRadAcctSummaryData.getDropped();
				long lastUpdatedAccountingStart = radAcctSummaryData.getAccountingStart() - oldRadAcctSummaryData.getAccountingStart();
				long lastUpdatedAccountingStop =radAcctSummaryData.getAccountingStop() - oldRadAcctSummaryData.getAccountingStop();
				long lastUpdatedAccountingIntrim = radAcctSummaryData.getAccountingIntrim() - oldRadAcctSummaryData.getAccountingIntrim();
				long lastUpdatedAccountingRequest = radAcctSummaryData.getAccountingRequest() - oldRadAcctSummaryData.getAccountingRequest();

				currentRadAcctSummaryData.setDropped(lastUpdatedDropped);
				currentRadAcctSummaryData.setAccountingStart(lastUpdatedAccountingStart);
				currentRadAcctSummaryData.setAccountingStop(lastUpdatedAccountingStop);
				currentRadAcctSummaryData.setAccountingIntrim(lastUpdatedAccountingIntrim);
				currentRadAcctSummaryData.setAccountingRequest(lastUpdatedAccountingRequest);

				currentRadAcctSummaryData.setTimestamp((new Date()).getTime());

			}
			oldRadAcctSummaryData = radAcctSummaryData;
			currentRadAcctSummaryData.notifyObservers();
			return true;

		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Error in updating statistics for Acct Service Summary, Reason:"+e.getMessage());
			}
			LogManager.getLogger().trace(MODULE,e);
		}
		return false;
	}

	
}
