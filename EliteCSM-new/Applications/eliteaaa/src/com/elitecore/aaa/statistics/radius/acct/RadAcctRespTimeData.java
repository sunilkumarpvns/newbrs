package com.elitecore.aaa.statistics.radius.acct;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.aaa.statistics.StatisticsData;

public class RadAcctRespTimeData extends StatisticsData{
	
	private long timestamp;
	
	private long databaseAvgResponseTime;
	private long rmCommAvgResponseTime;
	private long totalAvgResponseTime;
	private long queueAvgTime;
	private long prePluginAvgTime;
	private long postPluginAvgTime;
	
	public RadAcctRespTimeData(){}
	

	public long getQueueAvgTime() {
		return queueAvgTime;
	}

	public void setQueueAvgTime(long queueAvgTime) {
		this.queueAvgTime = queueAvgTime;
		setChanged();
	}

	public long getPrePluginAvgTime() {
		return prePluginAvgTime;
	}

	public void setPrePluginAvgTime(long prePluginAvgTime) {
		this.prePluginAvgTime = prePluginAvgTime;
		setChanged();
	}

	public long getPostPluginAvgTime() {
		return postPluginAvgTime;
	}

	public void setPostPluginAvgTime(long postPluginAvgTime) {
		this.postPluginAvgTime = postPluginAvgTime;
		setChanged();
	}

	public long getTotalAvgResponseTime() {
		return totalAvgResponseTime;
	}

	public void setTotalAvgResponseTime(long totalAvgResponseTime) {
		this.totalAvgResponseTime = totalAvgResponseTime;
		setChanged();
	}

	public long getDatabaseAvgResponseTime() {
		return databaseAvgResponseTime;
	}

	public void setDatabaseAvgResponseTime(long databaseAvgResponseTime) {
		this.databaseAvgResponseTime = databaseAvgResponseTime;
		setChanged();
	}
	public long getRmCommAvgResponseTime() {
		return rmCommAvgResponseTime;
	}


	public void setRmCommAvgResponseTime(long rmCommAvgResponseTime) {
		this.rmCommAvgResponseTime = rmCommAvgResponseTime;
		setChanged();
	}


	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("---------------------- Acct Response Time -----------------------");
		out.println("Total Average Response Time    : " + totalAvgResponseTime);
		out.println("Queue Time   	     			: " + queueAvgTime);
		out.println("RM Average Response Time       : " + rmCommAvgResponseTime);
		out.println("------------------------------------------------------------");
		return stringBuffer.toString();
	}
}
