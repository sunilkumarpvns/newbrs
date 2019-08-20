package com.elitecore.aaa.statistics.radius;

import java.util.Observable;

public class BaseReportData extends Observable {
	private long timestamp;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
