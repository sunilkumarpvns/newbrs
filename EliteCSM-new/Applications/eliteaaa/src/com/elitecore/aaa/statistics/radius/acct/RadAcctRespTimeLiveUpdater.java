package com.elitecore.aaa.statistics.radius.acct;

import java.util.Observable;

import com.elitecore.aaa.statistics.BaseLiveUpdater;

public class RadAcctRespTimeLiveUpdater  extends BaseLiveUpdater{

	@Override
	public void update(Observable o, Object arg) {
		RadAcctRespTimeData radAcctRespTimeData = (RadAcctRespTimeData)o;
		Long[] data ={
				radAcctRespTimeData.getTimestamp(),
				radAcctRespTimeData.getTotalAvgResponseTime(),
				radAcctRespTimeData.getDatabaseAvgResponseTime(),
				radAcctRespTimeData.getRmCommAvgResponseTime(),
				radAcctRespTimeData.getPrePluginAvgTime(),
				radAcctRespTimeData.getPostPluginAvgTime(),
				radAcctRespTimeData.getQueueAvgTime()
				};
		addData(data);
	}
	

	
}
