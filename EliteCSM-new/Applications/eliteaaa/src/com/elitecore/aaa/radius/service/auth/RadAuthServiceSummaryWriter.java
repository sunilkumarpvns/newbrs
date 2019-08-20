package com.elitecore.aaa.radius.service.auth;

import java.util.Date;

import com.elitecore.aaa.mibs.radius.authentication.server.RadiusAuthServiceMIBListener;
import com.elitecore.aaa.radius.service.BaseRadServiceSummaryWriter;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;

public class RadAuthServiceSummaryWriter extends BaseRadServiceSummaryWriter{
	
	private long lastAccessRequestCount;
	private long lastAccessAcceptCount;
	private long lastAccessRejectCount;
	private long lastAccessChallengeCount;
	private long lastAccessDroppedCount;
	private long lastDuplicateRequestCount;
	private long lastMalformedAccessRequestCount;
	private long lastBadAuthenticatorsRequestCount;
	private long lastUnknownTypeRequestCount;
	private long lastInvalidRequestsCount;
	
	
	public static final String MODULE = "AUTH SERVICE SUMMARY WRITER";
	
	public RadAuthServiceSummaryWriter(RadAuthServiceContext serviceContext) {
		super(serviceContext);
	}

	public void execute(AsyncTaskContext context) {
		
		try {
			
			long currentAccessRequestCount = RadiusAuthServiceMIBListener.getRadiusAuthServTotalAccessRequests();
			long currentAccessAcceptCount = RadiusAuthServiceMIBListener.getRadiusAuthServTotalAccessAccepts();
			long currentAccessRejectCount = RadiusAuthServiceMIBListener.getRadiusAuthServTotalAccessRejects();
			long currentAccessChallengeCount = RadiusAuthServiceMIBListener.getRadiusAuthServTotalAccessChallenges();
			long currentAccessDroppedCount = RadiusAuthServiceMIBListener.getRadiusAuthServTotalPacketsDropped();
			long currentDuplicateRequestCount = RadiusAuthServiceMIBListener.getRadiusAuthServTotalDupAccessRequests();
			long currentUnknownTypeRequestCount = RadiusAuthServiceMIBListener.getRadiusAuthServTotalUnknownTypes();
			long currentBadAuthenticatorsRequestCount = RadiusAuthServiceMIBListener.getRadiusAuthServTotalBadAuthenticators();
			long currentMalformedAccessRequestsCount = RadiusAuthServiceMIBListener.getRadiusAuthServTotalMalformedAccessRequests();
			long currentInvalidRequestsCount = RadiusAuthServiceMIBListener.getRadiusAuthServTotalInvalidRequests();
			
			
			Date currentDate = new Date();
			
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.appendRecordln("------------------------------------------------------------------------------------------");
			summaryReportWriter.appendRecordln(serviceContext.getServiceIdentifier() + " summary from " + sdf.format(lastSummaryReportWriteDate) +" to " + sdf.format(currentDate));
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.appendRecordln("Access Request             : [" + (currentAccessRequestCount - lastAccessRequestCount)    + "] of [" + currentAccessRequestCount + "]");
			summaryReportWriter.appendRecordln("Access Accepts             : [" + (currentAccessAcceptCount - lastAccessAcceptCount)     + "] of [" + currentAccessAcceptCount  + "]");
			summaryReportWriter.appendRecordln("Access Rejects             : [" + (currentAccessRejectCount - lastAccessRejectCount)     + "] of [" + currentAccessRejectCount  + "]");
			summaryReportWriter.appendRecordln("Access Challenges          : [" + (currentAccessChallengeCount - lastAccessChallengeCount)  + "] of [" + currentAccessChallengeCount  + "]");
			summaryReportWriter.appendRecordln("Dropped Requests           : [" + (currentAccessDroppedCount - lastAccessDroppedCount)    + "] of [" + currentAccessDroppedCount + "]");
			summaryReportWriter.appendRecordln("Duplicate Requests         : [" + (currentDuplicateRequestCount - lastDuplicateRequestCount) + "] of [" + currentDuplicateRequestCount + "]");
			summaryReportWriter.appendRecordln("BadAuthenticator Requests  : [" + (currentBadAuthenticatorsRequestCount - lastBadAuthenticatorsRequestCount) + "] of [" + currentBadAuthenticatorsRequestCount + "]");
			summaryReportWriter.appendRecordln("UnknownType Requests       : [" + (currentUnknownTypeRequestCount - lastUnknownTypeRequestCount) + "] of [" + currentUnknownTypeRequestCount + "]");
			summaryReportWriter.appendRecordln("Malformed Requests         : [" + (currentMalformedAccessRequestsCount - lastMalformedAccessRequestCount) + "] of [" + currentMalformedAccessRequestsCount + "]");
			summaryReportWriter.appendRecordln("Invalid Requests           : [" + (currentInvalidRequestsCount - lastInvalidRequestsCount) + "] of [" + currentInvalidRequestsCount + "]");			
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.flush();
			
			lastSummaryReportWriteDate = currentDate;
			
			lastAccessRequestCount = currentAccessRequestCount;
			lastAccessAcceptCount = currentAccessAcceptCount;
			lastAccessRejectCount = currentAccessRejectCount;
			lastAccessChallengeCount = currentAccessChallengeCount;
			lastAccessDroppedCount = currentAccessDroppedCount;
			lastDuplicateRequestCount = currentDuplicateRequestCount;
			lastInvalidRequestsCount = currentInvalidRequestsCount;
			lastBadAuthenticatorsRequestCount = currentBadAuthenticatorsRequestCount;
			lastMalformedAccessRequestCount = currentMalformedAccessRequestsCount;
			lastUnknownTypeRequestCount = currentUnknownTypeRequestCount;
			
		}catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Problem during writing Authentication service summary. Reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
	}

}
