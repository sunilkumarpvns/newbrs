package com.elitecore.aaa.rm.service.chargingservice;

import java.util.Date;

import com.elitecore.aaa.mibs.rm.chargingservice.server.RMChargingServiceMIBListener;
import com.elitecore.aaa.radius.service.BaseRadServiceSummaryWriter;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;

public class RMChargingServiceSummaryWriter  extends BaseRadServiceSummaryWriter{
	
	private long lastChargingRequestCount;
	private long lastChargingResponseCount;
	private long lastChargingDroppedCount;
	private long lastDuplicateRequestCount;
	private long lastMalformedRequestCount;
	private long lastBadAuthenticatorsRequestCount;
	private long lastUnknownTypeRequestCount;
	private long lastInvalidRequestsCount;
	
	private long lastAccessRequestCount;
	private long lastAccessAcceptCount;
	private long lastAccessRejectsCount;
	private long lastAcctRequestCount;
	private long lastAcctResponseCount;
	private long lastAcctStartRequestCount;
	private long lastAcctStopRequestCount;
	private long lastAcctUpdateRequestCount;
	
	public static final String MODULE = "RM CHARGING SERVICE SUMMARY WRITER";
	private RMChargingServiceMIBListener rmChargingMIBListener;
	
	public RMChargingServiceSummaryWriter(RMChargingServiceContext serviceContext,RMChargingServiceMIBListener rmChargingMIBListener) {
		super(serviceContext);
		this.rmChargingMIBListener = rmChargingMIBListener;
	}
	
	public void execute(AsyncTaskContext context) {
		
		try {
			
			long currentChargingRequestCount  = rmChargingMIBListener.getRMChargingServTotalRequests();
			long currentChargingResponseCount = rmChargingMIBListener.getRMChargingServTotalResponses();
			long currentChargingDroppedCount  = rmChargingMIBListener.getRMChargingServTotalPacketsDropped();
			long currentDuplicateRequestCount = rmChargingMIBListener.getRMChargingServTotalDupRequests();
			long currentUnknownTypeRequestCount = rmChargingMIBListener.getRMChargingServTotalUnknownTypes();
			long currentBadAuthenticatorsRequestCount = rmChargingMIBListener.getRMChargingServTotalBadAuthenticators();
			long currentMalformedAccessRequestsCount = rmChargingMIBListener.getRMChargingServTotalMalformedRequests();
			long currentInvalidRequestsCount = rmChargingMIBListener.getRMChargingServTotalInvalidRequests();
			
			long currentAccessRequestCount = rmChargingMIBListener.getRmChargingServTotalAccessRequest();   
			long currentAccessAcceptCount = rmChargingMIBListener.getRmChargingServTotalAccessAccept();;    
			long currentAccessRejectsCount = rmChargingMIBListener.getRMChargingServTotalAccessRejects();   
			long currentAcctRequestCount = rmChargingMIBListener.getRmChargingServTotalAcctRequest();     
			long currentAcctResponseCount = rmChargingMIBListener.getRmChargingServTotalAcctResponse();    
			long currentAcctStartRequestCount = rmChargingMIBListener.getRmChargingServTotalAcctStartRequest();
			long currentAcctStopRequestCount = rmChargingMIBListener.getRmChargingServTotalAcctStopRequest(); 
			long currentAcctUpdateRequestCount = rmChargingMIBListener.getRmChargingServTotalAcctUpdateRequest();
			
			Date currentDate = new Date();
			
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.appendRecordln("------------------------------------------------------------------------------------------");
			summaryReportWriter.appendRecordln(serviceContext.getServiceIdentifier() + " summary from " + sdf.format(lastSummaryReportWriteDate) +" to " + sdf.format(currentDate));
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.appendRecordln("Charging Request           : [" + (currentChargingRequestCount  - lastChargingRequestCount) + "] of [" + currentChargingRequestCount + "]");
			summaryReportWriter.appendRecordln("Charging Response          : [" + (currentChargingResponseCount - lastChargingResponseCount)  + "] of [" + currentChargingResponseCount  + "]");
			summaryReportWriter.appendRecordln("Dropped Requests           : [" + (currentChargingDroppedCount  - lastChargingDroppedCount) + "] of [" + currentChargingDroppedCount + "]");
			summaryReportWriter.appendRecordln("Duplicate Requests         : [" + (currentDuplicateRequestCount - lastDuplicateRequestCount) + "] of [" + currentDuplicateRequestCount+ "]");
			summaryReportWriter.appendRecordln("BadAuthenticator Requests  : [" + (currentBadAuthenticatorsRequestCount - lastBadAuthenticatorsRequestCount) + "] of [" + currentBadAuthenticatorsRequestCount + "]");
			summaryReportWriter.appendRecordln("UnknownType Requests       : [" + (currentUnknownTypeRequestCount - lastUnknownTypeRequestCount) + "] of [" + currentUnknownTypeRequestCount + "]");
			summaryReportWriter.appendRecordln("Malformed Requests         : [" + (currentMalformedAccessRequestsCount - lastMalformedRequestCount) + "] of [" + currentMalformedAccessRequestsCount + "]");
			summaryReportWriter.appendRecordln("Invalid Requests           : [" + (currentInvalidRequestsCount - lastInvalidRequestsCount) + "] of [" + currentInvalidRequestsCount + "]");
			summaryReportWriter.appendRecordln("AccessRequest              : [" + (currentAccessRequestCount -   lastAccessRequestCount)+ "] of [" + currentAccessRequestCount + "]");
			summaryReportWriter.appendRecordln("AccessAccept               : [" + (currentAccessAcceptCount  -   lastAccessAcceptCount)+ "] of [" + currentAccessAcceptCount + "]");
			summaryReportWriter.appendRecordln("AccessRejects              : [" + (currentAccessRejectsCount -   lastAccessRejectsCount)+ "] of [" + currentAccessRejectsCount + "]");
			summaryReportWriter.appendRecordln("AcctRequest                : [" + (currentAcctRequestCount  - 	lastAcctRequestCount)+ "] of [" + currentAcctRequestCount + "]");
			summaryReportWriter.appendRecordln("AcctResponse               : [" + (currentAcctResponseCount  - 	lastAcctResponseCount)+ "] of [" + currentAcctResponseCount + "]");
			summaryReportWriter.appendRecordln("AcctStartRequest           : [" + (currentAcctStartRequestCount - lastAcctStartRequestCount) + "] of [" + currentAcctStartRequestCount + "]");
			summaryReportWriter.appendRecordln("AcctStopRequest            : [" + (currentAcctStopRequestCount  - lastAcctStopRequestCount) + "] of [" + currentAcctStopRequestCount + "]");
			summaryReportWriter.appendRecordln("AcctUpdateRequest          : [" + (currentAcctUpdateRequestCount - lastAcctUpdateRequestCount) + "] of [" + currentAcctUpdateRequestCount + "]");
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.flush();
			
			lastSummaryReportWriteDate = currentDate;
			lastChargingRequestCount  = currentChargingRequestCount;
			lastChargingResponseCount = currentChargingResponseCount;
			lastChargingDroppedCount  = currentChargingDroppedCount;
			lastDuplicateRequestCount = currentDuplicateRequestCount;
			lastInvalidRequestsCount = currentInvalidRequestsCount;
			lastBadAuthenticatorsRequestCount = currentBadAuthenticatorsRequestCount;
			lastMalformedRequestCount = currentMalformedAccessRequestsCount;
			lastUnknownTypeRequestCount = currentUnknownTypeRequestCount;
			
			lastAccessRequestCount = currentAccessRequestCount;
			lastAccessAcceptCount  = currentAccessAcceptCount; 
			lastAccessRejectsCount = currentAccessRejectsCount;
			lastAcctRequestCount = currentAcctRequestCount;
			lastAcctResponseCount = currentAcctResponseCount; 
			lastAcctStartRequestCount = currentAcctStartRequestCount;
			lastAcctStopRequestCount = currentAcctStopRequestCount;
			lastAcctUpdateRequestCount= currentAcctUpdateRequestCount;
			
		}catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Problem during writing Charging service summary . Reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
	}
}
