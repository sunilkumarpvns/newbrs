package com.elitecore.aaa.statistics.radius.auth;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.aaa.statistics.StatisticsData;

public class RadAuthSummaryData extends StatisticsData{
	private long timestamp;
	private long accessRequest;
	private long accessAccept;
	private long accessReject;
	private long accessChallenge;
	private long dropped;
	
	public long getAccessRequest() {
		return accessRequest;
	}
	public void setAccessRequest(long accessRequest) {
		this.accessRequest = accessRequest;
		setChanged();
	}
	public long getAccessAccept() {
		return accessAccept;
	}
	public void setAccessAccept(long accessAccept) {
		this.accessAccept = accessAccept;
		setChanged();
	}
	public long getAccessReject() {
		return accessReject;
	}
	public void setAccessReject(long accessReject) {
		this.accessReject = accessReject;
		setChanged();
	}
	public long getAccessChallenge() {
		return accessChallenge;
	}
	public void setAccessChallenge(long accessChallenge) {
		this.accessChallenge = accessChallenge;
		setChanged();
	}
	public long getDropped() {
		return dropped;
	}
	public void setDropped(long dropped) {
		this.dropped = dropped;
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
		out.println("---------------- Auth Summary -------------");
		out.println("Access Request	  : " + accessRequest);
		out.println("Access Accept    : " + accessAccept);
		out.println("Access Reject    : " + accessReject);
		out.println("Access Challenge : " + accessChallenge);
		out.println("Dropped          : " + dropped);
		out.println("-------------------------------------------");
		return stringBuffer.toString();
	}
}
