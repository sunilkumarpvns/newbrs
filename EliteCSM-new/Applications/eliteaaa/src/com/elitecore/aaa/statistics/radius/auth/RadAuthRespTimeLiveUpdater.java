package com.elitecore.aaa.statistics.radius.auth;

import java.util.Observable;

import com.elitecore.aaa.statistics.BaseLiveUpdater;

public class RadAuthRespTimeLiveUpdater  extends BaseLiveUpdater {
	
	@Override
	public void update(Observable o, Object arg) {
		RadAuthRespTimeData radAuthRespTimeData = (RadAuthRespTimeData)o;
		Long[] data ={
				radAuthRespTimeData.getTimestamp(),
				radAuthRespTimeData.getTotalAvgResponseTime(),
				radAuthRespTimeData.getDatabaseAvgResponseTime(),
				radAuthRespTimeData.getLdapAvgResponseTime(),
				radAuthRespTimeData.getUsersFileAvgResponseTime(),
				radAuthRespTimeData.getResourceServerAvgResponseTime(),
				radAuthRespTimeData.getPrePluginAvgTime(),
				radAuthRespTimeData.getPostPluginAvgTime(),
				radAuthRespTimeData.getQueueAvgTime()
				};
		
		addData(data);
		//Logger.logDebug(MODULE," added data :"+Arrays.toString(data) );
	}
	
	
	
	
}
