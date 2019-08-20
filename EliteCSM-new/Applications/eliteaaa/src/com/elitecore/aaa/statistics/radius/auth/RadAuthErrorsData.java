package com.elitecore.aaa.statistics.radius.auth;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.aaa.statistics.StatisticsData;

public class RadAuthErrorsData extends StatisticsData{
	private long timestamp;
	private long badAuthenticators;
	private long duplicateRequests;
	private long malformedRequests;
	private long invalidRequests;
	private long unknownRequests;
	private long dropped;
	
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public long getBadAuthenticators() {
		return badAuthenticators;
	}
	public void setBadAuthenticators(long badAuthenticators) {
		this.badAuthenticators = badAuthenticators;
		setChanged();
	}
	public long getDuplicateRequests() {
		return duplicateRequests;
	}
	public void setDuplicateRequests(long duplicateRequests) {
		this.duplicateRequests = duplicateRequests;
		setChanged();
	}
	public long getMalformedRequests() {
		return malformedRequests;
	}
	public void setMalformedRequests(long malformedRequests) {
		this.malformedRequests = malformedRequests;
		setChanged();
	}
	public long getInvalidRequests() {
		return invalidRequests;
	}
	public void setInvalidRequests(long invalidRequests) {
		this.invalidRequests = invalidRequests;
		setChanged();
	}
	public long getUnknownRequests() {
		return unknownRequests;
	}
	public void setUnknownRequests(long unknownRequests) {
		this.unknownRequests = unknownRequests;
		setChanged();
	}
	public long getDropped() {
		return dropped;
	}
	public void setDropped(long dropped) {
		this.dropped = dropped;
		setChanged();
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("---------------- Auth Errors -------------");
		out.println("Bad Authenticators : " + badAuthenticators);
		out.println("Duplicate Requests : " + duplicateRequests);
		out.println("Malformed Requests : " + malformedRequests);
		out.println("Invalid Requests   : " + invalidRequests);
		out.println("Unknown Requests   : " + unknownRequests);
		out.println("Dropped            : " + dropped);
		out.println("-------------------------------------------");
		return stringBuffer.toString();
	}
}
