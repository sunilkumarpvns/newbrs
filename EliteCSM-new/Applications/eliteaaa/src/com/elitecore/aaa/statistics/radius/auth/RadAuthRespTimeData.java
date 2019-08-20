package com.elitecore.aaa.statistics.radius.auth;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.aaa.statistics.StatisticsData;

public class RadAuthRespTimeData extends StatisticsData{
	
	private long ldapAvgResponseTime;
	private long databaseAvgResponseTime;
	private long usersFileAvgResponseTime;
	
	private long resourceServerAvgResponseTime;
	private long totalAvgResponseTime;
	private long queueAvgTime;
	private long prePluginAvgTime;
	private long postPluginAvgTime;
	
	private long timestamp;
	
	public long getUsersFileAvgResponseTime() {
		return usersFileAvgResponseTime;
	}

	public void setUsersFileAvgResponseTime(long usersFileAvgResponseTime) {
		this.usersFileAvgResponseTime = usersFileAvgResponseTime;
	}

	public long getQueueAvgTime() {
		return queueAvgTime;
	}

	public void setQueueAvgTime(long queueAvgTime) {
		this.queueAvgTime = queueAvgTime;
	}

	public long getPrePluginAvgTime() {
		return prePluginAvgTime;
	}

	public void setPrePluginAvgTime(long prePluginAvgTime) {
		this.prePluginAvgTime = prePluginAvgTime;
	}

	public long getPostPluginAvgTime() {
		return postPluginAvgTime;
	}

	public void setPostPluginAvgTime(long postPluginAvgTime) {
		this.postPluginAvgTime = postPluginAvgTime;
	}

	public long getTotalAvgResponseTime() {
		return totalAvgResponseTime;
	}

	public void setTotalAvgResponseTime(long totalAvgResponseTime) {
		this.totalAvgResponseTime = totalAvgResponseTime;
		setChanged();
	}

	public long getLdapAvgResponseTime() {
		return ldapAvgResponseTime;
	}

	public void setLdapAvgResponseTime(long ldapAvgResponseTime) {
		this.ldapAvgResponseTime = ldapAvgResponseTime;
		setChanged();
	}
	
	public long getDatabaseAvgResponseTime() {
		return databaseAvgResponseTime;
	}

	public void setDatabaseAvgResponseTime(long databaseAvgResponseTime) {
		this.databaseAvgResponseTime = databaseAvgResponseTime;
		setChanged();
	}

	public long getResourceServerAvgResponseTime() {
		return resourceServerAvgResponseTime;
	}

	public void setResourceServerAvgResponseTime(long resourceServerAvgResponseTime) {
		this.resourceServerAvgResponseTime = resourceServerAvgResponseTime;
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
		out.println("---------------------- Auth Response Time -----------------------");
		out.println("Total Average Response Time    : " + totalAvgResponseTime);
		out.println("Database Average Response Time : " + databaseAvgResponseTime);
		out.println("RM Average Response Time       : " + resourceServerAvgResponseTime);
		out.println("LDAP Average Response Time     : " + ldapAvgResponseTime);
		out.println("------------------------------------------------------------");
		return stringBuffer.toString();
	}
}
