package com.elitecore.aaa.radius.service.acct;

import java.util.Date;

import com.elitecore.aaa.mibs.radius.accounting.server.RadiusAcctServiceMIBListener;
import com.elitecore.aaa.radius.service.BaseRadServiceSummaryWriter;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;

public class RadAcctServiceSummaryWriter  extends BaseRadServiceSummaryWriter{
	
	private long lastAcctRequestCount;
	private long lastAcctResponseCount;
	private long lastAcctDroppedCount;
	private long lastDuplicateRequestCount;
	private long lastMalformedAccessRequestCount;
	private long lastBadAuthenticatorsRequestCount;
	private long lastUnknownTypeRequestCount;
	private long lastInvalidRequestsCount;
	
	
	public static final String MODULE = "RAD ACCT SERVICE SUMMARY WRITER";
	
	public RadAcctServiceSummaryWriter(RadAcctServiceContext serviceContext) {
		super(serviceContext);
	}
	
	public void execute(AsyncTaskContext context) {
		
		try {
			
			long currentAcctRequestCount  = RadiusAcctServiceMIBListener.getRadiusAccServTotalRequests();
			long currentAcctResponseCount = RadiusAcctServiceMIBListener.getRadiusAccServTotalResponses();
			long currentAcctDroppedCount  = RadiusAcctServiceMIBListener.getRadiusAccServTotalPacketsDropped();
			long currentDuplicateRequestCount = RadiusAcctServiceMIBListener.getRadiusAccServTotalDupRequests();
			long currentUnknownTypeRequestCount = RadiusAcctServiceMIBListener.getRadiusAccServTotalUnknownTypes();
			long currentBadAuthenticatorsRequestCount = RadiusAcctServiceMIBListener.getRadiusAccServTotalBadAuthenticators();
			long currentMalformedAccessRequestsCount = RadiusAcctServiceMIBListener.getRadiusAccServTotalMalformedRequests();
			long currentInvalidRequestsCount = RadiusAcctServiceMIBListener.getRadiusAccServTotalInvalidRequests();
			
			Date currentDate = new Date();
			
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.appendRecordln("------------------------------------------------------------------------------------------");
			summaryReportWriter.appendRecordln(serviceContext.getServiceIdentifier() + " summary from " + sdf.format(lastSummaryReportWriteDate) +" to " + sdf.format(currentDate));
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.appendRecordln("Acct Request               : [" + (currentAcctRequestCount  - lastAcctRequestCount) + "] of [" + currentAcctRequestCount + "]");
			summaryReportWriter.appendRecordln("Acct Response              : [" + (currentAcctResponseCount - lastAcctResponseCount)  + "] of [" + currentAcctResponseCount  + "]");
			summaryReportWriter.appendRecordln("Dropped Requests           : [" + (currentAcctDroppedCount  - lastAcctDroppedCount) + "] of [" + currentAcctDroppedCount + "]");
			summaryReportWriter.appendRecordln("Duplicate Requests         : [" + (currentDuplicateRequestCount - lastDuplicateRequestCount) + "] of [" + currentDuplicateRequestCount+ "]");
			summaryReportWriter.appendRecordln("BadAuthenticator Requests  : [" + (currentBadAuthenticatorsRequestCount - lastBadAuthenticatorsRequestCount) + "] of [" + currentBadAuthenticatorsRequestCount + "]");
			summaryReportWriter.appendRecordln("UnknownType Requests       : [" + (currentUnknownTypeRequestCount - lastUnknownTypeRequestCount) + "] of [" + currentUnknownTypeRequestCount + "]");
			summaryReportWriter.appendRecordln("Malformed Requests         : [" + (currentMalformedAccessRequestsCount - lastMalformedAccessRequestCount) + "] of [" + currentMalformedAccessRequestsCount + "]");
			summaryReportWriter.appendRecordln("Invalid Requests           : [" + (currentInvalidRequestsCount - lastInvalidRequestsCount) + "] of [" + currentInvalidRequestsCount + "]");
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.flush();
			
			lastSummaryReportWriteDate = currentDate;
			lastAcctRequestCount  = currentAcctRequestCount;
			lastAcctResponseCount = currentAcctResponseCount;
			lastAcctDroppedCount  = currentAcctDroppedCount;
			lastDuplicateRequestCount = currentDuplicateRequestCount;
			lastInvalidRequestsCount = currentInvalidRequestsCount;
			lastBadAuthenticatorsRequestCount = currentBadAuthenticatorsRequestCount;
			lastMalformedAccessRequestCount = currentMalformedAccessRequestsCount;
			lastUnknownTypeRequestCount = currentUnknownTypeRequestCount;
			
		}catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Problem during writing Accounting service summary . Reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
	}
}
