package com.elitecore.aaa.rm.service.concurrentloginservice;

import java.util.Date;

import com.elitecore.aaa.mibs.rm.concurrentloginservice.server.RMConcServiceMIBListener;
import com.elitecore.aaa.radius.service.BaseRadServiceSummaryWriter;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;

public class RMConcServiceSummaryWriter  extends BaseRadServiceSummaryWriter{
	
	private long lastConcRequestCount;
	private long lastConcResponseCount;
	private long lastConcDroppedCount;
	private long lastDuplicateRequestCount;
	private long lastMalformedRequestCount;
	private long lastBadAuthenticatorsRequestCount;
	private long lastUnknownTypeRequestCount;
	private long lastInvalidRequestsCount;
	
	
	public static final String MODULE = "RM CONC SERVICE SUMMARY WRITER";
	
	public RMConcServiceSummaryWriter(RMConcurrentLoginServiceContext serviceContext) {
		super(serviceContext);
	}
	
	public void execute(AsyncTaskContext context) {
		
		try {
			
			long currentConcRequestCount  = RMConcServiceMIBListener.getRMConcServTotalRequests();
			long currentConcResponseCount = RMConcServiceMIBListener.getRMConcServTotalResponses();
			long currentConcDroppedCount  = RMConcServiceMIBListener.getRMConcServTotalPacketsDropped();
			long currentDuplicateRequestCount = RMConcServiceMIBListener.getRMConcServTotalDupRequests();
			long currentUnknownTypeRequestCount = RMConcServiceMIBListener.getRMConcServTotalUnknownTypes();
			long currentBadAuthenticatorsRequestCount = RMConcServiceMIBListener.getRMConcServTotalBadAuthenticators();
			long currentMalformedAccessRequestsCount = RMConcServiceMIBListener.getRMConcServTotalMalformedRequests();
			long currentInvalidRequestsCount = RMConcServiceMIBListener.getRMConcServTotalInvalidRequests();
			
			Date currentDate = new Date();
			
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.appendRecordln("------------------------------------------------------------------------------------------");
			summaryReportWriter.appendRecordln(serviceContext.getServiceIdentifier() + " summary from " + sdf.format(lastSummaryReportWriteDate) +" to " + sdf.format(currentDate));
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.appendRecordln("Concurrent Login Request   : [" + (currentConcRequestCount  - lastConcRequestCount) + "] of [" + currentConcRequestCount + "]");
			summaryReportWriter.appendRecordln("Concurrent Login Response  : [" + (currentConcResponseCount - lastConcResponseCount)  + "] of [" + currentConcResponseCount  + "]");
			summaryReportWriter.appendRecordln("Dropped Requests           : [" + (currentConcDroppedCount  - lastConcDroppedCount) + "] of [" + currentConcDroppedCount + "]");
			summaryReportWriter.appendRecordln("Duplicate Requests         : [" + (currentDuplicateRequestCount - lastDuplicateRequestCount) + "] of [" + currentDuplicateRequestCount+ "]");
			summaryReportWriter.appendRecordln("BadAuthenticator Requests  : [" + (currentBadAuthenticatorsRequestCount - lastBadAuthenticatorsRequestCount) + "] of [" + currentBadAuthenticatorsRequestCount + "]");
			summaryReportWriter.appendRecordln("UnknownType Requests       : [" + (currentUnknownTypeRequestCount - lastUnknownTypeRequestCount) + "] of [" + currentUnknownTypeRequestCount + "]");
			summaryReportWriter.appendRecordln("Malformed Requests         : [" + (currentMalformedAccessRequestsCount - lastMalformedRequestCount) + "] of [" + currentMalformedAccessRequestsCount + "]");
			summaryReportWriter.appendRecordln("Invalid Requests           : [" + (currentInvalidRequestsCount - lastInvalidRequestsCount) + "] of [" + currentInvalidRequestsCount + "]");
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.flush();
			
			lastSummaryReportWriteDate = currentDate;
			lastConcRequestCount  = currentConcRequestCount;
			lastConcResponseCount = currentConcResponseCount;
			lastConcDroppedCount  = currentConcDroppedCount;
			lastDuplicateRequestCount = currentDuplicateRequestCount;
			lastInvalidRequestsCount = currentInvalidRequestsCount;
			lastBadAuthenticatorsRequestCount = currentBadAuthenticatorsRequestCount;
			lastMalformedRequestCount = currentMalformedAccessRequestsCount;
			lastUnknownTypeRequestCount = currentUnknownTypeRequestCount;
			
		}catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Problem during writing Concurrent service summary . Reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
	}
}
