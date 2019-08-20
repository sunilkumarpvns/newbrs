package com.elitecore.aaa.statistics;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseLiveUpdater implements StatisticsObserver {
	
	private List<Long[]> dataList = new ArrayList<Long[]>();
	
	public void addData(Long[] data){
		if(data!=null)
			dataList.add(data);
	}
	
	public List<Long[]> reset(){
		List<Long[]> tempList = dataList;
		synchronized (this) {
			dataList=new ArrayList<Long[]>();
		}
		return tempList;
	}

	public boolean isExpired(){		
		return dataList == null || dataList.size() > 100;
	}
}
