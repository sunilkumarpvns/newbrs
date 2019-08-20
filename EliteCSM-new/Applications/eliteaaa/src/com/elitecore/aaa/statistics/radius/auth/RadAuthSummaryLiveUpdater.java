package com.elitecore.aaa.statistics.radius.auth;

import java.util.Observable;

import com.elitecore.aaa.statistics.BaseLiveUpdater;

public class RadAuthSummaryLiveUpdater extends BaseLiveUpdater{
	
	@Override
	public void update(Observable o, Object arg) {
		RadAuthSummaryData radAuthSummaryData = (RadAuthSummaryData)o;
		Long[] data ={
				radAuthSummaryData.getTimestamp(),
				radAuthSummaryData.getAccessRequest(),
				radAuthSummaryData.getAccessAccept(),
				radAuthSummaryData.getAccessReject(),
				radAuthSummaryData.getAccessChallenge(),
				radAuthSummaryData.getDropped()
				};
		
		addData(data);
		//Logger.logDebug(MODULE," added data :"+Arrays.toString(data) );
	}
	
	
}
