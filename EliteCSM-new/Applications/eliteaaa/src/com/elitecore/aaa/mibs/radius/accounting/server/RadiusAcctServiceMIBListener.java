package com.elitecore.aaa.mibs.radius.accounting.server;

import java.util.Map;

import com.elitecore.aaa.mibs.radius.accounting.server.autogen.TableRadiusAccClientTable;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.service.acct.RadiusAcctServiceMIBCounters;
import com.elitecore.aaa.radius.service.acct.RadiusAcctServiceMIBCounters.RadiusAcctClientEntry;


public class RadiusAcctServiceMIBListener {
	private static RadiusAcctServiceMIBCounters acctServiceMIBCounters = new RadiusAcctServiceMIBCounters(null);
	private static TableRadiusAccClientTable tableRadiusAccClientTable;
	public RadiusAcctServiceMIBListener(RadiusAcctServiceMIBCounters radiusAcctServiceMIBCounters){		
		acctServiceMIBCounters = radiusAcctServiceMIBCounters;	 
	}
	
	public static void init(){			
		acctServiceMIBCounters.init();		
	}
	
	public static boolean reInitialize(){		
			acctServiceMIBCounters.reInitialize();		
		return true;
	}	
		
	public void listenUpdateRadiusAcctServResetTimeEvent() {
		acctServiceMIBCounters.radiusAccServResetTime.set(System.currentTimeMillis());
	}

	public void listenRadiusAccServTotalRequestsEevnt() {	
		acctServiceMIBCounters.radiusAccServTotalRequests.incrementAndGet();
	}
	
	public void listenRadiusAccServTotalRequestsEvent(String clientAddress) {
		RadiusAcctClientEntry acctClientEntry = (RadiusAcctClientEntry)acctServiceMIBCounters.acctClientEntryMap.get(clientAddress);
		if (acctClientEntry != null){
			acctClientEntry.incrementRadiusAccServRequests();
		}		
	}
	public void listenRadiusAccServTotalInvalidRequestsEvent() {	
		acctServiceMIBCounters.radiusAccServTotalInvalidRequests.incrementAndGet();
	}
	public void listenRadiusAccServTotalDupRequestsEvent(String clientAddress) {
		acctServiceMIBCounters.radiusAccServTotalDupRequests.incrementAndGet();
		RadiusAcctClientEntry acctClientEntry = (RadiusAcctClientEntry)acctServiceMIBCounters.acctClientEntryMap.get(clientAddress);
		if (acctClientEntry != null){
			acctClientEntry.incrementRadiusAccServDupRequests();		
		}
	}
	public void listenRadiusAccServTotalResponsesEvent(String clientAddress) {	
		acctServiceMIBCounters.radiusAccServTotalResponses.incrementAndGet();
		RadiusAcctClientEntry acctClientEntry = (RadiusAcctClientEntry)acctServiceMIBCounters.acctClientEntryMap.get(clientAddress);
		if (acctClientEntry != null){
			acctClientEntry.incrementRadiusAccServResponses();
		}		
	}
	public void listenRadiusAccServTotalMalformedRequestsEvent(String clientAddress) {	
		acctServiceMIBCounters.radiusAccServTotalMalformedRequests.incrementAndGet();
		RadiusAcctClientEntry acctClientEntry = (RadiusAcctClientEntry)acctServiceMIBCounters.acctClientEntryMap.get(clientAddress);		
		if (acctClientEntry != null){
			acctClientEntry.incrementRadiusAccServMalformedRequests();
		}
	}
	public void listenRadiusAccServTotalAccessRejectsEvent() {
		acctServiceMIBCounters.radiusAccServTotalAccessRejects.incrementAndGet();
	}
	public void listenRadiusAccServTotalNoRecordsEvent(String clientAddress) {		
		acctServiceMIBCounters.radiusAccServTotalNoRecords.incrementAndGet();
		RadiusAcctClientEntry acctClientEntry = (RadiusAcctClientEntry)acctServiceMIBCounters.acctClientEntryMap.get(clientAddress);
		if (acctClientEntry != null){
			acctClientEntry.incrementRadiusAccServNoRecords();
		}		
	}
	public void listenRadiusAccServTotalBadAuthenticatorsEvent(String clientAddress) {		
		acctServiceMIBCounters.radiusAccServTotalBadAuthenticators.incrementAndGet();
		RadiusAcctClientEntry acctClientEntry = (RadiusAcctClientEntry)acctServiceMIBCounters.acctClientEntryMap.get(clientAddress);
		if (acctClientEntry != null){
			acctClientEntry.incrementRadiusAccServBadAuthenticators();
		}		
	}
	public void listenRadiusAccServTotalPacketsDroppedEvent(String clientAddress) {	
		acctServiceMIBCounters.radiusAccServTotalPacketsDropped.incrementAndGet();
		RadiusAcctClientEntry acctClientEntry = (RadiusAcctClientEntry)acctServiceMIBCounters.acctClientEntryMap.get(clientAddress);
		if (acctClientEntry != null){
			acctClientEntry.incrementRadiusAccServPacketsDropped();
		}
	}
	public void listenRadiusAccServTotalUnknownTypesEvent(String clientAddress) {
		acctServiceMIBCounters.radiusAccServTotalUnknownTypes.incrementAndGet();
		RadiusAcctClientEntry acctClientEntry = (RadiusAcctClientEntry)acctServiceMIBCounters.acctClientEntryMap.get(clientAddress);
		if (acctClientEntry != null){
			acctClientEntry.incrementRadiusAccServUnknownTypes();
		}
	}
	
	
	
	
	public static long getRadiusAccServTotalRequests() {
		return acctServiceMIBCounters.radiusAccServTotalRequests.get();
	}

	public static long getRadiusAccServTotalRequests(String clientAddress) {
		RadiusAcctClientEntry acctClientEntry = (RadiusAcctClientEntry)acctServiceMIBCounters.acctClientEntryMap.get(clientAddress);
		if (acctClientEntry != null){
			return acctClientEntry.getRadiusAccServRequests();
		}
		return 0;
	}	

	public static long getRadiusAccServTotalInvalidRequests() {
		return acctServiceMIBCounters.radiusAccServTotalInvalidRequests.get();
	}

	public static long getRadiusAccServTotalDupRequests() {
		return acctServiceMIBCounters.radiusAccServTotalDupRequests.get();
	}

	public static long getRadiusAccServTotalDupRequests(String clientAddress) {
		RadiusAcctClientEntry acctClientEntry = (RadiusAcctClientEntry)acctServiceMIBCounters.acctClientEntryMap.get(clientAddress);
		if (acctClientEntry != null){
			return acctClientEntry.getRadiusAccServDupRequests();
		}
		return 0;
	}

	public static long getRadiusAccServTotalResponses() {
		return acctServiceMIBCounters.radiusAccServTotalResponses.get();
	}

	public static long getRadiusAccServTotalResponses(String clientAddress) {
		RadiusAcctClientEntry acctClientEntry = (RadiusAcctClientEntry)acctServiceMIBCounters.acctClientEntryMap.get(clientAddress);
		if (acctClientEntry != null){
			return acctClientEntry.getRadiusAccServResponses();
		}
		return 0;
	}

	public static long getRadiusAccServTotalMalformedRequests() {
		return acctServiceMIBCounters.radiusAccServTotalMalformedRequests.get();
	}

	public static long getRadiusAccServTotalMalformedRequests(String clientAddress) {
		RadiusAcctClientEntry acctClientEntry = (RadiusAcctClientEntry)acctServiceMIBCounters.acctClientEntryMap.get(clientAddress);
		if (acctClientEntry != null){
			return acctClientEntry.getRadiusAccServMalformedRequests();
		}
		return 0;
	}	

	public static long getRadiusAccServTotalAccessRejects() {
		return acctServiceMIBCounters.radiusAccServTotalAccessRejects.get();
	}	
	
	public static long getRadiusAccServTotalBadAuthenticators() {
		return acctServiceMIBCounters.radiusAccServTotalBadAuthenticators.get();
	}

	public static long getRadiusAccServTotalBadAuthenticators(String clientAddress) {
		RadiusAcctClientEntry acctClientEntry = (RadiusAcctClientEntry)acctServiceMIBCounters.acctClientEntryMap.get(clientAddress);
		if (acctClientEntry != null){
			return acctClientEntry.getRadiusAccServBadAuthenticators();
		}
		return 0;
	}	

	public static long getRadiusAccServTotalPacketsDropped() {
		return acctServiceMIBCounters.radiusAccServTotalPacketsDropped.get();
	}

	public static long getRadiusAccServTotalPacketsDropped(String clientAddress) {
		RadiusAcctClientEntry acctClientEntry = (RadiusAcctClientEntry)acctServiceMIBCounters.acctClientEntryMap.get(clientAddress);
		if (acctClientEntry != null){
			return acctClientEntry.getRadiusAccServPacketsDropped();
		}
		return 0;
	}

	public static long getRadiusAccServTotalNoRecords() {
		return acctServiceMIBCounters.radiusAccServTotalNoRecords.get();
	}

	public static long getRadiusAccServTotalNoRecords(String clientAddress) {
		RadiusAcctClientEntry acctClientEntry = (RadiusAcctClientEntry)acctServiceMIBCounters.acctClientEntryMap.get(clientAddress);
		if (acctClientEntry != null){
			return acctClientEntry.getRadiusAccServNoRecords();
		}
		return 0;
	}
	
	public static long getRadiusAccServTotalUnknownTypes() {
		return acctServiceMIBCounters.radiusAccServTotalUnknownTypes.get();
	}

	public static long getRadiusAccServTotalUnknownTypes(String clientAddress) {
		RadiusAcctClientEntry acctClientEntry = (RadiusAcctClientEntry)acctServiceMIBCounters.acctClientEntryMap.get(clientAddress);
		if (acctClientEntry != null){
			return acctClientEntry.getRadiusAccServUnknownTypes();
		}
		return 0;
	}
	
	public static Map<String, String> getClientMap(){
		return acctServiceMIBCounters.clientMap;
	}
	
	public static long getRadiusAcctServUpTime() {
		return acctServiceMIBCounters.radiusAccServUpTime.get();
	}
	
	public static long getRadiusAcctServResetTime() {
		return acctServiceMIBCounters.radiusAccServResetTime.get();
	}
	
	public static boolean getInitializedState() {
		return acctServiceMIBCounters.isInitialized;
	}
	
	public static boolean getOtherState() {
		return acctServiceMIBCounters.isOther;
	}
	public static RadClientData getClientData(String strIp){
		return acctServiceMIBCounters.getClientData(strIp);
	}

	public static TableRadiusAccClientTable getTableRadiusAccClientTable() {
		return tableRadiusAccClientTable;
	}

	public static void setTableRadiusAccClientTable(TableRadiusAccClientTable tableRadiusAccClientTable) {
		RadiusAcctServiceMIBListener.tableRadiusAccClientTable = tableRadiusAccClientTable;
	}
}
