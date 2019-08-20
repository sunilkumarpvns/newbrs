package com.elitecore.aaa.mibs.rm.chargingservice.server;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.rm.service.chargingservice.RMChargingServiceMIBCounters;
import com.elitecore.aaa.rm.service.chargingservice.RMChargingServiceMIBCounters.RMChargingClientEntry;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.snmp.MIBStates;


public class RMChargingServiceMIBListener {
	
	private final static String MODULE = "RM-CHARGING-LISTENER";

	private static final long DEFAULT_CNT_VAL = 0;
	private RMChargingServiceMIBCounters chargingServiceMIBCounters;
	
	private ConcurrentMap<String, RMChargingClientEntry> chargingClientEntryMap = new ConcurrentHashMap<String, RMChargingClientEntry>();
	private ConcurrentMap<String, String> clientMap = new ConcurrentHashMap<String,String>();
	
	private RadClientConfiguration radClientConfiguration;
	private MIBStates mibStates;
	
	public RMChargingServiceMIBListener(RMChargingServiceMIBCounters rmChargingServiceMIBCounters,RadClientConfiguration radClientConfiguration){		
		this.chargingServiceMIBCounters = rmChargingServiceMIBCounters;
		this.radClientConfiguration = radClientConfiguration;
		chargingClientEntryMap = new ConcurrentHashMap<String, RMChargingClientEntry>();
		clientMap = new ConcurrentHashMap<String,String>();                                            
		mibStates = MIBStates.OTHER;
	}
	
	public void init(){			
		if (mibStates != MIBStates.RUNNING){
			mibStates = MIBStates.INITIALIZING;
			long currentTime = System.currentTimeMillis();
			chargingServiceMIBCounters.rmChargingServUpTime.set(currentTime);
			chargingServiceMIBCounters.rmChargingServResetTime.set(currentTime);
			
			List<String> clientAddressList = radClientConfiguration.getClientAddresses();
			if (clientAddressList != null && !clientAddressList.isEmpty()){
				int len = clientAddressList.size();				
				for(int clientIndexCounter = 1; clientIndexCounter <= len; clientIndexCounter++){					
					String clientAddress = clientAddressList.get(clientIndexCounter-1);
					if (clientAddress != null && clientAddress.trim().length() > 0) {
						clientAddress = clientAddress.trim();
						clientMap.put(String.valueOf(clientIndexCounter), clientAddress);
						chargingClientEntryMap.put(clientAddress, new RMChargingClientEntry(clientIndexCounter, clientAddress, clientAddress));
					}
				}
			}
			mibStates = MIBStates.RUNNING;
		}
	}
	
	public boolean reInitialize(){
		mibStates = MIBStates.INITIALIZING;
		reInitClientEntry();
		mibStates = MIBStates.RUNNING;
		return true;
	}	
		
	private void reInitClientEntry(){
		List<String> clientAddressList = radClientConfiguration.getClientAddresses();
		
		ConcurrentMap<String, RMChargingClientEntry> chargingClientEntryMap_new = new ConcurrentHashMap<String, RMChargingClientEntry>();
		ConcurrentMap<String, String> clientMap_new = new ConcurrentHashMap<String, String>();
		
		chargingServiceMIBCounters.rmChargingServResetTime.set(System.currentTimeMillis());
		
		if (clientAddressList != null && !clientAddressList.isEmpty()){
			RMChargingClientEntry rmChargingEntry;
			ListIterator<String> iterator = clientAddressList.listIterator();
			int clientIndexCounter = 1;
			while(iterator.hasNext()){
				String clientAddress = String.valueOf(iterator.next());
				clientMap_new.put(String.valueOf(clientIndexCounter), clientAddress);
				rmChargingEntry = (RMChargingClientEntry)chargingClientEntryMap.get(clientAddress);
				if(rmChargingEntry == null){
					chargingClientEntryMap_new.put(clientAddress, new RMChargingClientEntry(clientIndexCounter, clientAddress, clientAddress));
				}else{
					chargingClientEntryMap_new.put(clientAddress, rmChargingEntry);
				}
			}
			chargingClientEntryMap = chargingClientEntryMap_new;
			clientMap = clientMap_new;
		}
	}
	
	public void resetCounter(){

		mibStates = MIBStates.RESET;
		
		//Resetting the global service counters
		chargingServiceMIBCounters.resetCounter();

		//Resetting the per client entries
		/**
		 * aaa client counter reset call.
		 */
		for (RMChargingClientEntry clientEntry : chargingClientEntryMap.values()) {
			clientEntry.resetCounter();
		}

		chargingServiceMIBCounters.rmChargingServResetTime.set(System.currentTimeMillis());
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Resetting the counters successfully.");
		mibStates = MIBStates.RUNNING;
	}
	
	public void listenUpdateRMChargingServResetTimeEvent() {
		chargingServiceMIBCounters.rmChargingServResetTime.set(System.currentTimeMillis());
	}

	public void listenRMChargingServTotalRequestsEvent() {	
		chargingServiceMIBCounters.rmChargingServTotalRequests.incrementAndGet();
	}
	
	public void listenRMChargingServTotalRequestsEvent(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if (chargingClientEntry != null){
			chargingClientEntry.incrementRMChargingServRequests();
		}		
	}
	public void listenRMChargingServTotalInvalidRequestsEvent() {	
		chargingServiceMIBCounters.rmChargingServTotalInvalidRequests.incrementAndGet();
	}
	public void listenRMChargingServTotalDupRequestsEvent(String clientAddress) {
		chargingServiceMIBCounters.rmChargingServTotalDupRequests.incrementAndGet();
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if (chargingClientEntry != null){
			chargingClientEntry.incrementRMChargingServDupRequests();		
		}
	}
	public void listenRMChargingServTotalResponsesEvent(String clientAddress) {	
		chargingServiceMIBCounters.rmChargingServTotalResponses.incrementAndGet();
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if (chargingClientEntry != null){
			chargingClientEntry.incrementRMChargingServResponses();
		}		
	}
	public void listenRMChargingServTotalMalformedRequestsEvent(String clientAddress) {	
		chargingServiceMIBCounters.rmChargingServTotalMalformedRequests.incrementAndGet();
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);		
		if (chargingClientEntry != null){
			chargingClientEntry.incrementRMChargingServMalformedRequests();
		}
	}
	public void listenRMChargingServTotalAccessRejects() {
		chargingServiceMIBCounters.rmChargingServTotalAccessRejects.incrementAndGet();
	}
	public void listenRMChargingServTotalNoRecordsEvent(String clientAddress) {		
		chargingServiceMIBCounters.rmChargingServTotalNoRecords.incrementAndGet();
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if (chargingClientEntry != null){
			chargingClientEntry.incrementRMChargingServNoRecords();
		}		
	}
	public void listenRMChargingServTotalBadAuthenticatorsEvent(String clientAddress) {		
		chargingServiceMIBCounters.rmChargingServTotalBadAuthenticators.incrementAndGet();
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if (chargingClientEntry != null){
			chargingClientEntry.incrementRMChargingServBadAuthenticators();
		}		
	}
	public void listenRMChargingServTotalPacketsDroppedEvent(String clientAddress) {	
		chargingServiceMIBCounters.rmChargingServTotalPacketsDropped.incrementAndGet();
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if (chargingClientEntry != null){
			chargingClientEntry.incrementRMChargingServPacketsDropped();
		}
	}
	public void listenRMChargingServTotalUnknownTypesEvent(String clientAddress) {
		chargingServiceMIBCounters.rmChargingServTotalUnknownTypes.incrementAndGet();
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if (chargingClientEntry != null){
			chargingClientEntry.incrementRMChargingServUnknownTypes();
		}
	}
	
	public long getRMChargingServTotalRequests() {
		return chargingServiceMIBCounters.rmChargingServTotalRequests.get();
	}

	public long getRMChargingServTotalRequests(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if (chargingClientEntry != null){
			return chargingClientEntry.getRMChargingServRequests();
		}
		return 0;
	}	

	public long getRMChargingServTotalInvalidRequests() {
		return chargingServiceMIBCounters.rmChargingServTotalInvalidRequests.get();
	}

	public long getRMChargingServTotalDupRequests() {
		return chargingServiceMIBCounters.rmChargingServTotalDupRequests.get();
	}

	public long getRMChargingServTotalDupRequests(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if (chargingClientEntry != null){
			return chargingClientEntry.getRMChargingServDupRequests();
		}
		return 0;
	}

	public long getRMChargingServTotalResponses() {
		return chargingServiceMIBCounters.rmChargingServTotalResponses.get();
	}

	public long getRMChargingServTotalResponses(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if (chargingClientEntry != null){
			return chargingClientEntry.getRMChargingServResponses();
		}
		return 0;
	}

	public long getRMChargingServTotalMalformedRequests() {
		return chargingServiceMIBCounters.rmChargingServTotalMalformedRequests.get();
	}

	public long getRMChargingServTotalMalformedRequests(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if (chargingClientEntry != null){
			return chargingClientEntry.getRMChargingServMalformedRequests();
		}
		return 0;
	}	

	public long getRMChargingServTotalAccessRejects() {
		return chargingServiceMIBCounters.rmChargingServTotalAccessRejects.get();
	}	
	
	public long getRMChargingServTotalBadAuthenticators() {
		return chargingServiceMIBCounters.rmChargingServTotalBadAuthenticators.get();
	}

	public long getRMChargingServTotalBadAuthenticators(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if (chargingClientEntry != null){
			return chargingClientEntry.getRMChargingServBadAuthenticators();
		}
		return 0;
	}	

	public long getRMChargingServTotalPacketsDropped() {
		return chargingServiceMIBCounters.rmChargingServTotalPacketsDropped.get();
	}

	public long getRMChargingServTotalPacketsDropped(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if (chargingClientEntry != null){
			return chargingClientEntry.getRMChargingServPacketsDropped();
		}
		return 0;
	}

	public long getRMChargingServTotalNoRecords() {
		return chargingServiceMIBCounters.rmChargingServTotalNoRecords.get();
	}

	public long getRMChargingServTotalNoRecords(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if (chargingClientEntry != null){
			return chargingClientEntry.getRMChargingServNoRecords();
		}
		return 0;
	}
	
	public long getRMChargingServTotalUnknownTypes() {
		return chargingServiceMIBCounters.rmChargingServTotalUnknownTypes.get();
	}

	public long getRMChargingServTotalUnknownTypes(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if (chargingClientEntry != null){
			return chargingClientEntry.getRMChargingServUnknownTypes();
		}
		return 0;
	}
	
	public long getRMChargingServUpTime() {
		return chargingServiceMIBCounters.rmChargingServUpTime.get();
	}
	
	public long getRMChargingServResetTime() {
		return chargingServiceMIBCounters.rmChargingServResetTime.get();
	}
	
	
    public long getRmChargingServTotalAccessRequest() {
		return chargingServiceMIBCounters.rmChargingServTotalAccessRequest.get();
	}

	public void listenRmChargingServTotalAccessRequest() {
		chargingServiceMIBCounters.rmChargingServTotalAccessRequest.incrementAndGet();
	}

	public long getRmChargingServTotalAccessRequest(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if(chargingClientEntry != null){
			return chargingClientEntry.getRmChargingServAccessRequest();
		}
		return DEFAULT_CNT_VAL;
	}

	public void listenRmChargingServTotalAccessRequest(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if(chargingClientEntry != null){
			chargingClientEntry.incrementRmChargingServAccessRequest();
		}
	}

	public long getRmChargingServTotalAcctRequest() {
		return chargingServiceMIBCounters.rmChargingServTotalAcctRequest.get();
	}
	
	public void listenRmChargingServTotalAcctRequest() {
		chargingServiceMIBCounters.rmChargingServTotalAcctRequest.incrementAndGet();
	}
	
	public long getRmChargingServTotalAcctRequest(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if(chargingClientEntry != null){
			return chargingClientEntry.getRmChargingServAcctRequest();
		}
		return DEFAULT_CNT_VAL;
	}

	public void listenRmChargingServTotalAcctRequest(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if(chargingClientEntry != null){
			chargingClientEntry.incrementRmChargingServAcctRequest();
		}
	}

	
	public long getRmChargingServTotalAccessAccept() {
		return chargingServiceMIBCounters.rmChargingServTotalAccessAccept.get();
	}
	
	public void listenRmChargingServTotalAccessAccept() {
		chargingServiceMIBCounters.rmChargingServTotalAccessAccept.incrementAndGet();
	}
	
	public long getRmChargingServTotalAccessAccept(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if(chargingClientEntry != null){
			return chargingClientEntry.getRmChargingServAccessAccept();
		}
		return DEFAULT_CNT_VAL;
	}
	
	public void listenRmChargingServTotalAccessAccept(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if(chargingClientEntry != null){
			chargingClientEntry.incrementRmChargingServAccessAccept();
		}
	}

	public long getRMChargingServTotalAccessRejects(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if(chargingClientEntry != null){
			return chargingClientEntry.getRmChargingServAccessReject();
		}
		return DEFAULT_CNT_VAL;
	}
	
	public void listenRMChargingServTotalAccessRejects(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if(chargingClientEntry != null){
			chargingClientEntry.incrementRmChargingServAccessReject();
		}
	}
	
	public long getRmChargingServTotalAcctResponse() {
		return chargingServiceMIBCounters.rmChargingServTotalAcctResponse.get();
	}

	public void listenRmChargingServTotalAcctResponse() {
		chargingServiceMIBCounters.rmChargingServTotalAcctResponse.incrementAndGet();
	}

	public long getRmChargingServTotalAcctResponse(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if(chargingClientEntry != null){
			return chargingClientEntry.getRmChargingServAcctResponse();
		}
		return DEFAULT_CNT_VAL;
	}

	public void listenRmChargingServTotalAcctResponse(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if(chargingClientEntry != null){
			chargingClientEntry.incrementRmChargingServAcctResponse();
		}
	}
	
	public long getRmChargingServTotalAcctStartRequest() {
		return chargingServiceMIBCounters.rmChargingServTotalAcctStartRequest.get();
	}

	public void listenRmChargingServTotalAcctStartRequest() {
		chargingServiceMIBCounters.rmChargingServTotalAcctStartRequest.incrementAndGet();
	}

	public long getRmChargingServTotalAcctStartRequest(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if(chargingClientEntry != null){
			return chargingClientEntry.getRmChargingServAcctStartRequest();
		}
		return DEFAULT_CNT_VAL;
	}

	public void listenRmChargingServTotalAcctStartRequest(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if(chargingClientEntry != null){
			chargingClientEntry.incrementRmChargingServAcctStartRequest();
		}
	}
	
	public long getRmChargingServTotalAcctStopRequest() {
		return chargingServiceMIBCounters.rmChargingServTotalAcctStopRequest.get();
	}

	public void listenRmChargingServTotalAcctStopRequest() {
		chargingServiceMIBCounters.rmChargingServTotalAcctStopRequest.incrementAndGet();
	}
	
	public long getRmChargingServTotalAcctStopRequest(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if(chargingClientEntry != null){
			return chargingClientEntry.getRmChargingServAcctStopRequest();
		}
		return DEFAULT_CNT_VAL;
	}

	public void listenRmChargingServTotalAcctStopRequest(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if(chargingClientEntry != null){
			chargingClientEntry.incrementRmChargingServAcctStopRequest();
		}
	}

	public long getRmChargingServTotalAcctUpdateRequest() {
		return chargingServiceMIBCounters.rmChargingServTotalAcctUpdateRequest.get();
	}

	public void listenRmChargingServTotalAcctUpdateRequest() {
		chargingServiceMIBCounters.rmChargingServTotalAcctUpdateRequest.incrementAndGet();
	}
	
	public long getRmChargingServTotalAcctUpdateRequest(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if(chargingClientEntry != null){
			return chargingClientEntry.getRmChargingServAcctUpdateRequest();
		}
		return DEFAULT_CNT_VAL;
	}

	public void listenRmChargingServTotalAcctUpdateRequest(String clientAddress) {
		RMChargingClientEntry chargingClientEntry = chargingClientEntryMap.get(clientAddress);
		if(chargingClientEntry != null){
			chargingClientEntry.incrementRmChargingServAcctUpdateRequest();
		}
	}
	
	public MIBStates getMibStates() {
		return mibStates;
	}
}
