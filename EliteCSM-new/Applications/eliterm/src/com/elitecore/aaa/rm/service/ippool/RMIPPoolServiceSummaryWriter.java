package com.elitecore.aaa.rm.service.ippool;

import java.util.Date;

import com.elitecore.aaa.mibs.rm.ippool.server.RMIPPoolServiceMIBListener;
import com.elitecore.aaa.radius.service.BaseRadServiceSummaryWriter;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;

public class RMIPPoolServiceSummaryWriter  extends BaseRadServiceSummaryWriter{
	
	private long lastIpPoolRequestCount;
	private long lastIpPoolResponseCount;
	private long lastIpPoolDroppedCount;
	private long lastDuplicateRequestCount;
	private long lastMalformedRequestCount;
	private long lastBadAuthenticatorsRequestCount;
	private long lastUnknownTypeRequestCount;
	private long lastInvalidRequestsCount;
	private long lastIpAddressDiscoverCount;
	private long lastIpAddressOfferCount;
	private long lastIpAddressDeclineCount;
	private long lastIpAddressAllocationCount;
	private long lastIpAddressReleaseCount;
	private long lastIpAddressUpdateCount;
	
	private static final String MODULE = "RM IP-POOL SERVICE SUMMARY WRITER";
	private RMIPPoolServiceMIBListener ipPoolServiceListener;
	
	public RMIPPoolServiceSummaryWriter(RMIPPoolServiceContext serviceContext,RMIPPoolServiceMIBListener ipPoolServiceListener) {
		super(serviceContext);
		this.ipPoolServiceListener = ipPoolServiceListener;
	}
	
	public void execute(AsyncTaskContext context) {
		
		try {
			
			long currentIpPoolRequestCount  = ipPoolServiceListener.getRMIPPoolServTotalRequests();
			long currentIpPoolResponseCount = ipPoolServiceListener.getRMIPPoolServTotalResponses();
			long currentIpPoolDroppedCount  = ipPoolServiceListener.getRMIPPoolServTotalPacketsDropped();
			long currentDuplicateRequestCount = ipPoolServiceListener.getRMIPPoolServTotalDupRequests();
			long currentUnknownTypeRequestCount = ipPoolServiceListener.getRMIPPoolServTotalUnknownTypes();
			long currentBadAuthenticatorsRequestCount = ipPoolServiceListener.getRMIPPoolServTotalBadAuthenticators();
			long currentMalformedAccessRequestsCount = ipPoolServiceListener.getRMIPPoolServTotalMalformedRequests();
			long currentInvalidRequestsCount = ipPoolServiceListener.getRMIPPoolServTotalInvalidRequests();
			long currentIpAddressDiscoverCount = ipPoolServiceListener.getIPAddressDiscoverTotalRequest();
			long currentIpAddressOfferCount = ipPoolServiceListener.getIPAddressOfferTotalResponse();;
			long currentIpAddressDeclineCount = ipPoolServiceListener.getIPAddressDeclineTotalResponse();
			long currentIpAddressAllocationCount = ipPoolServiceListener.getIPAddressTotalAllocationRequest();
			long currentIpAddressReleaseCount = ipPoolServiceListener.getIPAddressTotalReleaseRequest();
			long currentIpAddressUpdateCount = ipPoolServiceListener.getIPAddressTotalUpdateRequest();
			
			
			Date currentDate = new Date();
			
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.appendRecordln("------------------------------------------------------------------------------------------");
			summaryReportWriter.appendRecordln(serviceContext.getServiceIdentifier() + " summary from " + sdf.format(lastSummaryReportWriteDate) +" to " + sdf.format(currentDate));
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.appendRecordln("IP-Pool Request                   : [" + (currentIpPoolRequestCount  - lastIpPoolRequestCount) + "] of [" + currentIpPoolRequestCount + "]");
			summaryReportWriter.appendRecordln("IP-Pool Response                  : [" + (currentIpPoolResponseCount - lastIpPoolResponseCount)  + "] of [" + currentIpPoolResponseCount  + "]");
			summaryReportWriter.appendRecordln("Dropped Requests                  : [" + (currentIpPoolDroppedCount  - lastIpPoolDroppedCount) + "] of [" + currentIpPoolDroppedCount + "]");
			summaryReportWriter.appendRecordln("Duplicate Requests                : [" + (currentDuplicateRequestCount - lastDuplicateRequestCount) + "] of [" + currentDuplicateRequestCount+ "]");
			summaryReportWriter.appendRecordln("BadAuthenticator Requests         : [" + (currentBadAuthenticatorsRequestCount - lastBadAuthenticatorsRequestCount) + "] of [" + currentBadAuthenticatorsRequestCount + "]");
			summaryReportWriter.appendRecordln("UnknownType Requests              : [" + (currentUnknownTypeRequestCount - lastUnknownTypeRequestCount) + "] of [" + currentUnknownTypeRequestCount + "]");
			summaryReportWriter.appendRecordln("Malformed Requests                : [" + (currentMalformedAccessRequestsCount - lastMalformedRequestCount) + "] of [" + currentMalformedAccessRequestsCount + "]");
			summaryReportWriter.appendRecordln("Invalid Requests                  : [" + (currentInvalidRequestsCount - lastInvalidRequestsCount) + "] of [" + currentInvalidRequestsCount + "]");
			summaryReportWriter.appendRecordln("IP-Pool Discover Address Requests : [" + (currentIpAddressDiscoverCount - lastIpAddressDiscoverCount) + "] of [" + currentIpAddressDiscoverCount + "]");
			summaryReportWriter.appendRecordln("IP-Pool Offer Address Requests    : [" + (currentIpAddressOfferCount - lastIpAddressOfferCount) + "] of [" + currentIpAddressOfferCount + "]");
			summaryReportWriter.appendRecordln("IP-Pool Decline Address Response  : [" + (currentIpAddressDeclineCount - lastIpAddressDeclineCount) + "] of [" + currentIpAddressDeclineCount + "]");
			summaryReportWriter.appendRecordln("IP-Pool Allocation Requests       : [" + (currentIpAddressAllocationCount - lastIpAddressAllocationCount) + "] of [" + currentIpAddressAllocationCount + "]");
			summaryReportWriter.appendRecordln("IP-Pool Release Address Requests  : [" + (currentIpAddressReleaseCount - lastIpAddressReleaseCount) + "] of [" + currentIpAddressReleaseCount + "]");
			summaryReportWriter.appendRecordln("IP-Pool Update Address Requests   : [" + (currentIpAddressUpdateCount - lastIpAddressUpdateCount) + "] of [" + currentIpAddressUpdateCount + "]");
			
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.flush();
			
			lastSummaryReportWriteDate = currentDate;
			lastIpPoolRequestCount  = currentIpPoolRequestCount;
			lastIpPoolResponseCount = currentIpPoolResponseCount;
			lastIpPoolDroppedCount  = currentIpPoolDroppedCount;
			lastDuplicateRequestCount = currentDuplicateRequestCount;
			lastInvalidRequestsCount = currentInvalidRequestsCount;
			lastBadAuthenticatorsRequestCount = currentBadAuthenticatorsRequestCount;
			lastMalformedRequestCount = currentMalformedAccessRequestsCount;
			lastUnknownTypeRequestCount = currentUnknownTypeRequestCount;
			
			lastIpAddressDiscoverCount = currentIpAddressDiscoverCount;  
			lastIpAddressOfferCount = currentIpAddressOfferCount;     
			lastIpAddressDeclineCount = currentIpAddressDeclineCount;   
			lastIpAddressAllocationCount = currentIpAddressAllocationCount;
			lastIpAddressReleaseCount = currentIpAddressReleaseCount;   
			lastIpAddressUpdateCount = currentIpAddressUpdateCount;    
			
		}catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Problem during writing IP Pool service summary . Reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
	}
}
