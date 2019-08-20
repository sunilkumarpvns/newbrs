package com.elitecore.aaa.radius.service.dynauth;

import java.util.Date;

import com.elitecore.aaa.mibs.radius.dynauth.server.RadiusDynAuthServerMIB;
import com.elitecore.aaa.radius.service.BaseRadServiceSummaryWriter;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;

public class RadDynAuthServiceSummaryWriter extends BaseRadServiceSummaryWriter{
	
	
	private long lastDynAuthTotalRequestCount;
	private long lastDynAuthServTotalCOARequestCount;
	private long lastDynAuthServTotalDisConnectRequestCount;
	
	private long lastDynAuthClientDisConnectInvalidClientAddressesCount;
	private long lastDynAuthClientCoAInvalidClientAddressesCount;
	private long lastDynAuthServTotalInvalidRequestsCount;
	
	
	
	private long lastDynAuthServTotalDupCOARequestsCount;
	private long lastDynAuthServTotalDupDisConnectRequestsCount;
	
	private long lastDynAuthServTotalCOANakCount;
	private long lastDynAuthServTotalCOAAckCount;
	
	private long lastDynAuthServTotalDisConnectNakCount;
	private long lastDynAuthServTotalDisConnectAckCount;
	
	private long lastDynAuthServTotalMalformedCOARequestCount;
	private long lastDynAuthServTotalMalformedDisConnectRequestCount;
	
	private long lastDynAuthServTotalBadAuthenticatorsRequestCount;
	private long lastDynAuthServTotalBadAuthenticatorsCOARequestCount;
	private long lastDynAuthServTotalBadAuthenticatorsDisConnectRequestCount;
	
	private long lastDynAuthServTotalCOAPacketsDroppedCount;
	private long lastDynAuthServTotalDisConnectPacketsDroppedCount;
	
	private long lastDynAuthServTotalUnknownTypesCount;
	
	public static final String MODULE = "DYN-AUTH SERVICE SUMMARY WRITER";
	
	public RadDynAuthServiceSummaryWriter(RadDynAuthServiceContext serviceContext) {
		super(serviceContext);
	}

	public void execute(AsyncTaskContext context) {
		
		try{
		
			long currentDynAuthTotalRequestCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalRequestsEvent();
			long currentDynAuthServTotalCOARequestCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalCOARequests() ;
			long currentDynAuthServTotalDisConnectRequestCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalDisConnectRequests();
			
			long currentDynAuthClientDisConnectInvalidClientAddressesCount  = RadiusDynAuthServerMIB.getDynAuthClientDisconInvalidClientAddresses() ;
			long currentDynAuthClientCoAInvalidClientAddressesCount  = RadiusDynAuthServerMIB.getDynAuthClientCoAInvalidClientAddresses();
			long currentDynAuthServTotalInvalidRequestsCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalInvalidRequests();
		
			long currentDynAuthServTotalDupCOARequestsCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalDupCOARequests();
			long currentDynAuthServTotalDupDisConnectRequestsCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalDupDisConnectRequests();
			long currentDynAuthServTotalCOANakCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalCOANak();
		
			
			long currentDynAuthServTotalCOAAckCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalCOAAck();
			long currentDynAuthServTotalDisConnectNakCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalDisConnectNak();
			long currentDynAuthServTotalDisConnectAckCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalConnectAck();
		
			
			long currentDynAuthServTotalMalformedCOARequestCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalMalformedCOARequests();
			long currentDynAuthServTotalMalformedDisConnectRequestCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalMalformedDisConnectRequests();
			long currentDynAuthServTotalBadAuthenticatorsRequestCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalBadAuthenticatorsRequests();
	
			
			long currentDynAuthServTotalBadAuthenticatorsCOARequestCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalBadAuthenticatorsCOARequests();
			long currentDynAuthServTotalBadAuthenticatorsDisConnectRequestCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalBadAuthenticatorsDisConnectRequests();
			long currentDynAuthServTotalCOAPacketsDroppedCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalCOAPacketsDropped();
	
			
			long currentDynAuthServTotalDisConnectPacketsDroppedCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalDisConnectPacketsDropped();
			long currentDynAuthServTotalUnknownTypesCount  = RadiusDynAuthServerMIB.getRadiusDynAuthServTotalUnknownTypes();
			
			
			Date currentDate = new Date();
			
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.appendRecordln("------------------------------------------------------------------------------------------");
			summaryReportWriter.appendRecordln(serviceContext.getServiceIdentifier() + " summary from " + sdf.format(lastSummaryReportWriteDate) +" to " + sdf.format(currentDate));
			
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.appendRecordln("DynaAuth Requests                       : [" + (currentDynAuthTotalRequestCount - lastDynAuthTotalRequestCount)    + "] of [" + currentDynAuthTotalRequestCount + "]");
			summaryReportWriter.appendRecordln("COA Requests                            : [" + (currentDynAuthServTotalCOARequestCount - lastDynAuthServTotalCOARequestCount)    + "] of [" + currentDynAuthServTotalCOARequestCount + "]");
			summaryReportWriter.appendRecordln("Disconnect Requests                     : [" + (currentDynAuthServTotalDisConnectRequestCount - lastDynAuthServTotalDisConnectRequestCount)    + "] of [" + currentDynAuthServTotalDisConnectRequestCount + "]");
			summaryReportWriter.appendRecordln("Disconnect Requests From Invalid Client : [" + (currentDynAuthClientDisConnectInvalidClientAddressesCount - lastDynAuthClientDisConnectInvalidClientAddressesCount)     + "] of [" + currentDynAuthClientDisConnectInvalidClientAddressesCount  + "]");
			summaryReportWriter.appendRecordln("COA Requests From Invalid Client        : [" + (currentDynAuthClientCoAInvalidClientAddressesCount - lastDynAuthClientCoAInvalidClientAddressesCount)     + "] of [" + currentDynAuthClientCoAInvalidClientAddressesCount  + "]");
			summaryReportWriter.appendRecordln("Invalid Requests                        : [" + (currentDynAuthServTotalInvalidRequestsCount - lastDynAuthServTotalInvalidRequestsCount)  + "] of [" + currentDynAuthServTotalInvalidRequestsCount  + "]");
			summaryReportWriter.appendRecordln("Duplicate COA Requests                  : [" + (currentDynAuthServTotalDupCOARequestsCount - lastDynAuthServTotalDupCOARequestsCount)    + "] of [" + currentDynAuthServTotalDupCOARequestsCount + "]");
			summaryReportWriter.appendRecordln("Duplicate Disconnect Requests           : [" + (currentDynAuthServTotalDupDisConnectRequestsCount - lastDynAuthServTotalDupDisConnectRequestsCount) + "] of [" + currentDynAuthServTotalDupDisConnectRequestsCount + "]");
			summaryReportWriter.appendRecordln("BadAuthenticator Requests               : [" + (currentDynAuthServTotalCOANakCount - lastDynAuthServTotalCOANakCount) + "] of [" + currentDynAuthServTotalCOANakCount + "]");
			summaryReportWriter.appendRecordln("COA Nak Response                        : [" + (currentDynAuthServTotalCOAAckCount - lastDynAuthServTotalCOAAckCount) + "] of [" + currentDynAuthServTotalCOAAckCount + "]");
			summaryReportWriter.appendRecordln("Disconnect Nak Response                 : [" + (currentDynAuthServTotalDisConnectNakCount - lastDynAuthServTotalDisConnectNakCount) + "] of [" + currentDynAuthServTotalDisConnectNakCount + "]");
			summaryReportWriter.appendRecordln("Disconnect Ack Response                 : [" + (currentDynAuthServTotalDisConnectAckCount - lastDynAuthServTotalDisConnectAckCount) + "] of [" + currentDynAuthServTotalDisConnectAckCount + "]");
			summaryReportWriter.appendRecordln("Malformed COA Requests                  : [" + (currentDynAuthServTotalMalformedCOARequestCount - lastDynAuthServTotalMalformedCOARequestCount)    + "] of [" + currentDynAuthServTotalMalformedCOARequestCount + "]");
			summaryReportWriter.appendRecordln("Malformed Disconnect Requests           : [" + (currentDynAuthServTotalMalformedDisConnectRequestCount - lastDynAuthServTotalMalformedDisConnectRequestCount)     + "] of [" + currentDynAuthServTotalMalformedDisConnectRequestCount  + "]");
			summaryReportWriter.appendRecordln("BadAuthenticators Requests              : [" + (currentDynAuthServTotalBadAuthenticatorsRequestCount - lastDynAuthServTotalBadAuthenticatorsRequestCount)     + "] of [" + currentDynAuthServTotalBadAuthenticatorsRequestCount  + "]");
			summaryReportWriter.appendRecordln("BadAuthenticators COA Requests          : [" + (currentDynAuthServTotalBadAuthenticatorsCOARequestCount - lastDynAuthServTotalBadAuthenticatorsCOARequestCount)  + "] of [" + currentDynAuthServTotalBadAuthenticatorsCOARequestCount  + "]");
			summaryReportWriter.appendRecordln("BadAuthenticators Disconnect Requests   : [" + (currentDynAuthServTotalBadAuthenticatorsDisConnectRequestCount - lastDynAuthServTotalBadAuthenticatorsDisConnectRequestCount)    + "] of [" + currentDynAuthServTotalBadAuthenticatorsDisConnectRequestCount + "]");
			summaryReportWriter.appendRecordln("COA Packets Dropped                     : [" + (currentDynAuthServTotalCOAPacketsDroppedCount - lastDynAuthServTotalCOAPacketsDroppedCount) + "] of [" + currentDynAuthServTotalCOAPacketsDroppedCount + "]");
			summaryReportWriter.appendRecordln("Disconnect Packets Dropped              : [" + (currentDynAuthServTotalDisConnectPacketsDroppedCount - lastDynAuthServTotalDisConnectPacketsDroppedCount) + "] of [" + currentDynAuthServTotalDisConnectPacketsDroppedCount + "]");
			summaryReportWriter.appendRecordln("UnknownType Requests                    : [" + (currentDynAuthServTotalUnknownTypesCount - lastDynAuthServTotalUnknownTypesCount) + "] of [" + currentDynAuthServTotalUnknownTypesCount + "]");
			
			summaryReportWriter.appendRecordln("");
			summaryReportWriter.flush();
			
			lastSummaryReportWriteDate = currentDate;
			
			lastDynAuthTotalRequestCount = currentDynAuthTotalRequestCount;
			lastDynAuthServTotalCOARequestCount = currentDynAuthServTotalCOARequestCount;
			lastDynAuthServTotalDisConnectRequestCount = currentDynAuthServTotalDisConnectRequestCount;
			
			lastDynAuthClientDisConnectInvalidClientAddressesCount = currentDynAuthClientDisConnectInvalidClientAddressesCount ;
			lastDynAuthClientCoAInvalidClientAddressesCount = currentDynAuthClientCoAInvalidClientAddressesCount;
			lastDynAuthServTotalInvalidRequestsCount = currentDynAuthServTotalInvalidRequestsCount;
			
			lastDynAuthServTotalDupCOARequestsCount = currentDynAuthServTotalDupCOARequestsCount;
			lastDynAuthServTotalDupDisConnectRequestsCount = currentDynAuthServTotalDupDisConnectRequestsCount;
			
			lastDynAuthServTotalCOANakCount = currentDynAuthServTotalCOANakCount;
			lastDynAuthServTotalCOAAckCount = currentDynAuthServTotalCOAAckCount;
			
			lastDynAuthServTotalDisConnectNakCount = currentDynAuthServTotalDisConnectNakCount;
			lastDynAuthServTotalDisConnectAckCount = currentDynAuthServTotalDisConnectAckCount;
			
			lastDynAuthServTotalMalformedCOARequestCount = currentDynAuthServTotalMalformedCOARequestCount ;
			lastDynAuthServTotalMalformedDisConnectRequestCount = currentDynAuthServTotalMalformedDisConnectRequestCount;
			
			lastDynAuthServTotalBadAuthenticatorsRequestCount = currentDynAuthServTotalBadAuthenticatorsRequestCount;
			lastDynAuthServTotalBadAuthenticatorsCOARequestCount = currentDynAuthServTotalBadAuthenticatorsCOARequestCount;
			lastDynAuthServTotalBadAuthenticatorsDisConnectRequestCount = currentDynAuthServTotalBadAuthenticatorsDisConnectRequestCount ;
			
			lastDynAuthServTotalCOAPacketsDroppedCount = currentDynAuthServTotalCOAPacketsDroppedCount;
			lastDynAuthServTotalDisConnectPacketsDroppedCount = currentDynAuthServTotalDisConnectPacketsDroppedCount;
			
			lastDynAuthServTotalUnknownTypesCount = currentDynAuthServTotalUnknownTypesCount;
		}catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Problem during writing Dyna-Auth service summary. Reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}	
		
	}
	
}
