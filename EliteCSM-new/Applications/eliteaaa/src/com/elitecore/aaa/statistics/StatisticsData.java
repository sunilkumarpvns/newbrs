package com.elitecore.aaa.statistics;

import java.util.Observable;

public abstract class StatisticsData extends Observable{
	private long timestamp;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
