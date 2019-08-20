package com.elitecore.aaa.statistics.radius;

import com.elitecore.aaa.statistics.StatisticsData;


public class MemoryUsageData  extends StatisticsData{
	private long totalMemoryUsed;
	

	public long getTotalMemoryUsed() {
		return totalMemoryUsed;
	}

	public void setTotalMemoryUsed(long totalMemoryUsed) {
		this.totalMemoryUsed = totalMemoryUsed;
	}
	
}
