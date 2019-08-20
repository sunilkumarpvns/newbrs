package com.elitecore.corenetvertex.core.jmx;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class SubscriberImportStatisticsImpl implements SubscriberImportStatistics {

	private static final long serialVersionUID = 1L;
	private long submittedTaskCount;
	private long successCount;
	private long failCount;
	private Date importStartTime;
	
	public SubscriberImportStatisticsImpl() {
		this.importStartTime = new Date(); 
	}
	
	public void incrementSubmittedTaskCount() {
		submittedTaskCount += 1;
	}
	
	public void incrementSuccessCount() {
		successCount += 1; 
	}
	
	public void incrementFailCount() {
		failCount += 1;
	}

	@Override
	public long getSubmittedTaskCount() {
		return submittedTaskCount;
	}

	@Override
	public long getSuccessCount() {
		return successCount;
	}

	@Override
	public long getFailCount() {
		return failCount;
	}
	
	@Override
	public Date getImportStartTime() {
		return importStartTime;
	}
	
	@Override
	public long getInprogressCount() {
		return submittedTaskCount - successCount - failCount;
	}

	@Override
	public String toString() {
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		
		writer.println("Submitted Task: " + submittedTaskCount);
		writer.println("Success: " + successCount);
		writer.println("Fail: " + failCount);
		writer.println("Inprogress Task: " + getInprogressCount());
		
		writer.close();
		
		return stringWriter.toString();
	}
}