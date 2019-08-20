package com.elitecore.aaa.statistics.radius.acct;

import java.util.Observable;

import com.elitecore.aaa.statistics.BaseLiveUpdater;

public class RadAcctSummaryLiveUpdater  extends BaseLiveUpdater{

	@Override
	public void update(Observable o, Object arg) {
		RadAcctSummaryData radAcctSummaryData = (RadAcctSummaryData)o;
		Long[] data ={
				radAcctSummaryData.getTimestamp(),
				radAcctSummaryData.getAccountingStart(),
				radAcctSummaryData.getAccountingStop(),
				radAcctSummaryData.getAccountingIntrim(),
				radAcctSummaryData.getAccountingRequest(),
				radAcctSummaryData.getDropped()
				};
		addData(data);
		//Logger.logDebug(MODULE," added data :"+Arrays.toString(data) );
	}
	
}
