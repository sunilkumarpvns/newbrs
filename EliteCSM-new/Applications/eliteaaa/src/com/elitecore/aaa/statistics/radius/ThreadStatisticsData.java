package com.elitecore.aaa.statistics.radius;

import com.elitecore.aaa.statistics.StatisticsData;


public class ThreadStatisticsData extends  StatisticsData{
	private long activeThread;

	public long getActiveThread() {
		return activeThread;
	}

	public void setActiveThread(long activeThread) {
		this.activeThread = activeThread;
		setChanged();
	}
	
	
	
}
