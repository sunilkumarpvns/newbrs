package com.elitecore.aaa.statistics.radius.auth;

import java.util.Observable;

import com.elitecore.aaa.statistics.BaseLiveUpdater;

public class RadAuthRejectResonsLiveUpdater  extends BaseLiveUpdater {
	
	@Override
	public void update(Observable o, Object arg) {
		RadAuthRejectReasonsData radAuthRejectReasonsData = (RadAuthRejectReasonsData)o;
		Long[] data ={
				radAuthRejectReasonsData.getTimestamp(),
				radAuthRejectReasonsData.getUserNotFound(),
				radAuthRejectReasonsData.getInvalidPassword(),
				radAuthRejectReasonsData.getInvalidCHAPPassword(),
				radAuthRejectReasonsData.getInvalidMSCHAPv1Password(),
				radAuthRejectReasonsData.getInvalidMSCHAPv2Password(),
				radAuthRejectReasonsData.getInvalidDigestPassword(),
				radAuthRejectReasonsData.getEapFailure(),
				radAuthRejectReasonsData.getAuthenticationFailed(),
				radAuthRejectReasonsData.getAccountIsNotActive(),
				radAuthRejectReasonsData.getAccountExpired(),
				radAuthRejectReasonsData.getCreditLimitExceeded(),
				radAuthRejectReasonsData.getDigestFailure(),
				radAuthRejectReasonsData.getRmCommTimeOut()
			
				};
		addData(data);
		//Logger.logDebug(MODULE," added data :"+Arrays.toString(data) );
	}
	
	

	
}
