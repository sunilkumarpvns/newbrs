package com.elitecore.aaa.statistics.radius.acct;

import java.util.Observable;

import com.elitecore.aaa.statistics.BaseLiveUpdater;

public class RadAcctErrorsLiveUpdater  extends BaseLiveUpdater{
		
	@Override
	public void update(Observable o, Object arg) {
		RadAcctErrorsData radAcctErrorsData = (RadAcctErrorsData)o;
		Long[] data ={
				radAcctErrorsData.getTimestamp(),
				radAcctErrorsData.getBadAuthenticators(),
				radAcctErrorsData.getDuplicateRequests(),
				radAcctErrorsData.getMalformedRequests(),
				radAcctErrorsData.getInvalidRequests(),
				radAcctErrorsData.getUnknownRequests(),
				radAcctErrorsData.getDropped()
				};
		addData(data);
	}
	
	
}
