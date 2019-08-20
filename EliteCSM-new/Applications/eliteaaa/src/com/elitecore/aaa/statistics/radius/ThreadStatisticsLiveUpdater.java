package com.elitecore.aaa.statistics.radius;

import java.util.Observable;

import com.elitecore.aaa.statistics.BaseLiveUpdater;

public class ThreadStatisticsLiveUpdater  extends BaseLiveUpdater{

	@Override
	public void update(Observable o, Object arg) {
		ThreadStatisticsData threadStatisticsData = (ThreadStatisticsData)o;
		Long[] data ={
				threadStatisticsData.getTimestamp(),
				threadStatisticsData.getActiveThread()
		};
		addData(data);
	}
}
