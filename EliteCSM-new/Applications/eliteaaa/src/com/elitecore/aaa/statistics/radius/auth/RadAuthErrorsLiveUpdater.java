package com.elitecore.aaa.statistics.radius.auth;

import java.util.Observable;

import com.elitecore.aaa.statistics.BaseLiveUpdater;

public class RadAuthErrorsLiveUpdater   extends BaseLiveUpdater{
	
	@Override
	public void update(Observable o, Object arg) {
		RadAuthErrorsData radAuthErrorsData = (RadAuthErrorsData)o;
		Long[] data ={
				radAuthErrorsData.getTimestamp(),
				radAuthErrorsData.getBadAuthenticators(),
				radAuthErrorsData.getDuplicateRequests(),
				radAuthErrorsData.getMalformedRequests(),
				radAuthErrorsData.getInvalidRequests(),
				radAuthErrorsData.getUnknownRequests(),
				radAuthErrorsData.getDropped()
				};
		
		addData(data);
		//Logger.logDebug(MODULE," added data :"+Arrays.toString(data) );
	}
	

	
}
