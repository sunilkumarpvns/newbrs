package com.elitecore.aaa.statistics.radius.acct;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.aaa.statistics.StatisticsData;

public class RadAcctSummaryData extends StatisticsData{
	private long timestamp;
	private long accountingStart;
	private long accountingStop;
	private long accountingIntrim;
	private long accountingRequest;
	private long dropped;
	


	public long getAccountingStart() {
		return accountingStart;
	}
	public void setAccountingStart(long accountingStart) {
		this.accountingStart = accountingStart;
	}
	public long getAccountingStop() {
		return accountingStop;
	}
	public void setAccountingStop(long accountingStop) {
		this.accountingStop = accountingStop;
	}
	public long getAccountingIntrim() {
		return accountingIntrim;
	}
	public void setAccountingIntrim(long accountingIntrim) {
		this.accountingIntrim = accountingIntrim;
	}
	public long getDropped() {
		return dropped;
	}
	public void setDropped(long dropped) {
		this.dropped = dropped;
		setChanged();
	}
	public long getAccountingRequest() {
		return accountingRequest;
	}
	public void setAccountingRequest(long accountingRequest) {
		this.accountingRequest = accountingRequest;
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
		out.println("---------------- Acct Summary -------------");
		out.println("Accounting Start   : " + accountingStart);
		out.println("Acccounting Stop   : " + accountingStop);
		out.println("Accounting Intrim  : " + accountingIntrim);
		out.println("Accounting Request : " + accountingRequest);
		out.println("Dropped            : " + dropped);
		out.println("-------------------------------------------");
		return stringBuffer.toString();
	}
}
