package com.elitecore.aaa.statistics.radius;

import java.util.Observable;

import com.elitecore.aaa.statistics.BaseLiveUpdater;

public class MemoryUsageLiveUpdater extends BaseLiveUpdater{
	
	@Override
	public void update(Observable o, Object arg) {
		MemoryUsageData memoryUsageData = (MemoryUsageData)o;
		Long[] data ={
				memoryUsageData.getTimestamp(),
				memoryUsageData.getTotalMemoryUsed(),
				};
		addData(data);
	}
	

	
}
