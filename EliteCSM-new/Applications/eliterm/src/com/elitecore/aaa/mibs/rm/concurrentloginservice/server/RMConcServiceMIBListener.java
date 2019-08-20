package com.elitecore.aaa.mibs.rm.concurrentloginservice.server;

import java.util.Map;

import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.rm.service.concurrentloginservice.RMConcServiceMIBCounters;
import com.elitecore.aaa.rm.service.concurrentloginservice.RMConcServiceMIBCounters.RMConcClientEntry;


public class RMConcServiceMIBListener {
private static RMConcServiceMIBCounters concServiceMIBCounters;
	public RMConcServiceMIBListener(RMConcServiceMIBCounters rmConcServiceMIBCounters){		
		concServiceMIBCounters = rmConcServiceMIBCounters;	 
	}
	
	public static void init(){			
		concServiceMIBCounters.init();		
	}
	
	public static boolean reInitialize(){		
		concServiceMIBCounters.reInitialize();		
		return true;
	}	
		
	public void listenUpdateRMConcServResetTimeEvent() {
		concServiceMIBCounters.rmConcServResetTime.set(System.currentTimeMillis());
	}

	public void listenRMConcServTotalRequestsEvent() {	
		concServiceMIBCounters.rmConcServTotalRequests.incrementAndGet();
	}
	
	public void listenRMConcServTotalRequestsEvent(String clientAddress) {
		RMConcClientEntry concClientEntry = (RMConcClientEntry)concServiceMIBCounters.concClientEntryMap.get(clientAddress);
		if (concClientEntry != null){
			concClientEntry.incrementRMConcServRequests();
		}		
	}
	public void listenRMConcServTotalInvalidRequestsEvent() {	
		concServiceMIBCounters.rmConcServTotalInvalidRequests.incrementAndGet();
	}
	public void listenRMConcServTotalDupRequestsEvent(String clientAddress) {
		concServiceMIBCounters.rmConcServTotalDupRequests.incrementAndGet();
		RMConcClientEntry concClientEntry = (RMConcClientEntry)concServiceMIBCounters.concClientEntryMap.get(clientAddress);
		if (concClientEntry != null){
			concClientEntry.incrementRMConcServDupRequests();		
		}
	}
	public void listenRMConcServTotalResponsesEvent(String clientAddress) {	
		concServiceMIBCounters.rmConcServTotalResponses.incrementAndGet();
		RMConcClientEntry concClientEntry = (RMConcClientEntry)concServiceMIBCounters.concClientEntryMap.get(clientAddress);
		if (concClientEntry != null){
			concClientEntry.incrementRMConcServResponses();
		}		
	}
	public void listenRMConcServTotalMalformedRequestsEvent(String clientAddress) {	
		concServiceMIBCounters.rmConcServTotalMalformedRequests.incrementAndGet();
		RMConcClientEntry concClientEntry = (RMConcClientEntry)concServiceMIBCounters.concClientEntryMap.get(clientAddress);		
		if (concClientEntry != null){
			concClientEntry.incrementRMConcServMalformedRequests();
		}
	}
	public void listenRMConcServTotalAccessRejectsEvent() {
		concServiceMIBCounters.rmConcServTotalAccessRejects.incrementAndGet();
	}
	public void listenRMConcServTotalNoRecordsEvent(String clientAddress) {		
		concServiceMIBCounters.rmConcServTotalNoRecords.incrementAndGet();
		RMConcClientEntry concClientEntry = (RMConcClientEntry)concServiceMIBCounters.concClientEntryMap.get(clientAddress);
		if (concClientEntry != null){
			concClientEntry.incrementRMConcServNoRecords();
		}		
	}
	public void listenRMConcServTotalBadAuthenticatorsEvent(String clientAddress) {		
		concServiceMIBCounters.rmConcServTotalBadAuthenticators.incrementAndGet();
		RMConcClientEntry concClientEntry = (RMConcClientEntry)concServiceMIBCounters.concClientEntryMap.get(clientAddress);
		if (concClientEntry != null){
			concClientEntry.incrementRMConcServBadAuthenticators();
		}		
	}
	public void listenRMConcServTotalPacketsDroppedEvent(String clientAddress) {	
		concServiceMIBCounters.rmConcServTotalPacketsDropped.incrementAndGet();
		RMConcClientEntry concClientEntry = (RMConcClientEntry)concServiceMIBCounters.concClientEntryMap.get(clientAddress);
		if (concClientEntry != null){
			concClientEntry.incrementRMConcServPacketsDropped();
		}
	}
	public void listenRMConcServTotalUnknownTypesEvent(String clientAddress) {
		concServiceMIBCounters.rmConcServTotalUnknownTypes.incrementAndGet();
		RMConcClientEntry concClientEntry = (RMConcClientEntry)concServiceMIBCounters.concClientEntryMap.get(clientAddress);
		if (concClientEntry != null){
			concClientEntry.incrementRMConcServUnknownTypes();
		}
	}
	
	
	
	
	public static long getRMConcServTotalRequests() {
		return concServiceMIBCounters.rmConcServTotalRequests.get();
	}

	public static long getRMConcServTotalRequests(String clientAddress) {
		RMConcClientEntry concClientEntry = (RMConcClientEntry)concServiceMIBCounters.concClientEntryMap.get(clientAddress);
		if (concClientEntry != null){
			return concClientEntry.getRMConcServRequests();
		}
		return 0;
	}	

	public static long getRMConcServTotalInvalidRequests() {
		return concServiceMIBCounters.rmConcServTotalInvalidRequests.get();
	}

	public static long getRMConcServTotalDupRequests() {
		return concServiceMIBCounters.rmConcServTotalDupRequests.get();
	}

	public static long getRMConcServTotalDupRequests(String clientAddress) {
		RMConcClientEntry concClientEntry = (RMConcClientEntry)concServiceMIBCounters.concClientEntryMap.get(clientAddress);
		if (concClientEntry != null){
			return concClientEntry.getRMConcServDupRequests();
		}
		return 0;
	}

	public static long getRMConcServTotalResponses() {
		return concServiceMIBCounters.rmConcServTotalResponses.get();
	}

	public static long getRMConcServTotalResponses(String clientAddress) {
		RMConcClientEntry concClientEntry = (RMConcClientEntry)concServiceMIBCounters.concClientEntryMap.get(clientAddress);
		if (concClientEntry != null){
			return concClientEntry.getRMConcServResponses();
		}
		return 0;
	}

	public static long getRMConcServTotalMalformedRequests() {
		return concServiceMIBCounters.rmConcServTotalMalformedRequests.get();
	}

	public static long getRMConcServTotalMalformedRequests(String clientAddress) {
		RMConcClientEntry concClientEntry = (RMConcClientEntry)concServiceMIBCounters.concClientEntryMap.get(clientAddress);
		if (concClientEntry != null){
			return concClientEntry.getRMConcServMalformedRequests();
		}
		return 0;
	}	

	public static long getRMConcServTotalAccessRejects() {
		return concServiceMIBCounters.rmConcServTotalAccessRejects.get();
	}	
	
	public static long getRMConcServTotalBadAuthenticators() {
		return concServiceMIBCounters.rmConcServTotalBadAuthenticators.get();
	}

	public static long getRMConcServTotalBadAuthenticators(String clientAddress) {
		RMConcClientEntry concClientEntry = (RMConcClientEntry)concServiceMIBCounters.concClientEntryMap.get(clientAddress);
		if (concClientEntry != null){
			return concClientEntry.getRMConcServBadAuthenticators();
		}
		return 0;
	}	

	public static long getRMConcServTotalPacketsDropped() {
		return concServiceMIBCounters.rmConcServTotalPacketsDropped.get();
	}

	public static long getRMConcServTotalPacketsDropped(String clientAddress) {
		RMConcClientEntry concClientEntry = (RMConcClientEntry)concServiceMIBCounters.concClientEntryMap.get(clientAddress);
		if (concClientEntry != null){
			return concClientEntry.getRMConcServPacketsDropped();
		}
		return 0;
	}

	public static long getRMConcServTotalNoRecords() {
		return concServiceMIBCounters.rmConcServTotalNoRecords.get();
	}

	public static long getRMConcServTotalNoRecords(String clientAddress) {
		RMConcClientEntry concClientEntry = (RMConcClientEntry)concServiceMIBCounters.concClientEntryMap.get(clientAddress);
		if (concClientEntry != null){
			return concClientEntry.getRMConcServNoRecords();
		}
		return 0;
	}
	
	public static long getRMConcServTotalUnknownTypes() {
		return concServiceMIBCounters.rmConcServTotalUnknownTypes.get();
	}

	public static long getRMConcServTotalUnknownTypes(String clientAddress) {
		RMConcClientEntry concClientEntry = (RMConcClientEntry)concServiceMIBCounters.concClientEntryMap.get(clientAddress);
		if (concClientEntry != null){
			return concClientEntry.getRMConcServUnknownTypes();
		}
		return 0;
	}
	
	public static Map getClientMap(){
		return concServiceMIBCounters.clientMap;
	}
	
	public static long getRMConcServUpTime() {
		return concServiceMIBCounters.rmConcServUpTime.get();
	}
	
	public static long getRMConcServResetTime() {
		return concServiceMIBCounters.rmConcServResetTime.get();
	}
	
	public static boolean getInitializedState() {
		return concServiceMIBCounters.isInitialized;
	}
	
	public static boolean getOtherState() {
		return concServiceMIBCounters.isOther;
	}
	public static RadClientData getClientData(String strIp){
		return concServiceMIBCounters.getClientData(strIp);
	}
}
