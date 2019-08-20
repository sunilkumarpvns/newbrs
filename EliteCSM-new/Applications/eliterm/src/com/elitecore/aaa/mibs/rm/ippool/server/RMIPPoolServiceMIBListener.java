package com.elitecore.aaa.mibs.rm.ippool.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.rm.service.ippool.RMIPPoolServiceMIBCounters;
import com.elitecore.aaa.rm.service.ippool.RMIPPoolServiceMIBCounters.RMIPPoolClientEntry;
import com.elitecore.aaa.rm.service.ippool.snmp.IpPoolNasClientEntryMBeanImpl;
import com.elitecore.aaa.rm.service.ippool.snmp.autogen.TableIpPoolNASClientStatisticsTable;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.snmp.MIBStates;
import com.sun.management.snmp.SnmpStatusException;

/**
 * Listener class that serves the 
 * response for SNMP Request.
 * 
 * This class keeps the entries for client specific counters and call them if the counter for the client is to 
 * be incremented
 * @author elitecore
 */

public class RMIPPoolServiceMIBListener {
	
	private final String MODULE = "RM-IP-POOL-LISTENER";	
	
	private MIBStates state;
	private RMIPPoolServiceMIBCounters ipPoolServiceMIBCounters;

	private ConcurrentMap<String, RMIPPoolClientEntry> ipPoolClientEntryMap;
	private Map<String, String> clientMap = new HashMap<String,String>();
	
	/**
	 * Map contains the NAS Client Entry with their Counters
	 * Communicate with RM IP-Pool service Through AAA Server
	 */
	
	private ConcurrentMap<String, RMIPPoolClientEntry> nasClientEntryMap;
	private TableIpPoolNASClientStatisticsTable nasClientTable;
	
	private RadClientConfiguration radClientConfiguration;
	private static final long DEFAULT_CNT_VALUE = 0;
	
	public RMIPPoolServiceMIBListener(RMIPPoolServiceMIBCounters ipPoolServiceMIBCounters, RadClientConfiguration radClientConfiguration){		
		this.ipPoolServiceMIBCounters = ipPoolServiceMIBCounters;
		this.radClientConfiguration = radClientConfiguration;
		ipPoolClientEntryMap = new ConcurrentHashMap<String, RMIPPoolClientEntry>();
		nasClientEntryMap = new ConcurrentHashMap<String, RMIPPoolClientEntry>();
		state = MIBStates.OTHER;
	}
	
	public void init(){			
		if(state != MIBStates.RUNNING){
			state = MIBStates.INITIALIZING;
			
			long currentTime = System.currentTimeMillis();
			ipPoolServiceMIBCounters.rmIPPoolServUpTime.set(currentTime);
			ipPoolServiceMIBCounters.rmIPPoolServResetTime.set(currentTime);
			
			List<String> clientAddressList = radClientConfiguration.getClientAddresses();
			if (clientAddressList != null && !clientAddressList.isEmpty()){
				int len = clientAddressList.size();				
				for(int clientIndexCounter = 0; clientIndexCounter < len; clientIndexCounter++){					
					String clientAddress = clientAddressList.get(clientIndexCounter);
					if (clientAddress != null && clientAddress.trim().length() > 0) {
						clientAddress = clientAddress.trim();
						clientMap.put(String.valueOf(clientIndexCounter), clientAddress);
						ipPoolClientEntryMap.put(clientAddress, ipPoolServiceMIBCounters.new RMIPPoolClientEntry(clientIndexCounter, clientAddress, clientAddress));
					}
				}
			}
			state = MIBStates.RUNNING;
		}
	}
	
	public boolean reInit(){		
		state = MIBStates.INITIALIZING;
		reInitClientEntry();
		state = MIBStates.RUNNING;
		return true;
	}	
		
	private void reInitClientEntry(){
		
		List<String> clientAddressList = radClientConfiguration.getClientAddresses();
		ConcurrentMap<String, RMIPPoolClientEntry> ipPoolClientEntryMap_new = new ConcurrentHashMap<String, RMIPPoolClientEntry>();
		Map<String, String> clientMap_new = new HashMap<String, String>();
		RMIPPoolClientEntry rmIpPoolEntry;
		ipPoolServiceMIBCounters.rmIPPoolServResetTime.set(System.currentTimeMillis());
		
		if (clientAddressList != null && !clientAddressList.isEmpty()){
			java.util.ListIterator<String> iterator = clientAddressList.listIterator();
			int clientIndexCounter = 0;
			while(iterator.hasNext()){
				clientIndexCounter++;					
				String clientAddress = String.valueOf(iterator.next());
				clientMap_new.put(String.valueOf(clientIndexCounter), clientAddress);
				rmIpPoolEntry = (RMIPPoolClientEntry)ipPoolClientEntryMap.get(clientAddress);
				if(rmIpPoolEntry == null){
					ipPoolClientEntryMap_new.put(clientAddress, ipPoolServiceMIBCounters.new RMIPPoolClientEntry(clientIndexCounter, clientAddress, clientAddress));
				}else{
					ipPoolClientEntryMap_new.put(clientAddress, rmIpPoolEntry);
				}
			}
			ipPoolClientEntryMap = ipPoolClientEntryMap_new;
			clientMap = clientMap_new;
		}
	}
	
	public void resetCounter(){

		state = MIBStates.RESET;
		
		//Resetting the global service counters
		ipPoolServiceMIBCounters.resetCounter();

		//Resetting the per client entries
		/**
		 * aaa client counter reset call.
		 */
		for (RMIPPoolClientEntry aaaclientEntry : ipPoolClientEntryMap.values()) {
			aaaclientEntry.resetCounter();
		}

		/**
		 * nas client counter reset call.
		 */
		for (RMIPPoolClientEntry nasClientEntry : nasClientEntryMap.values()) {
			nasClientEntry.resetCounter();
		}
		
		ipPoolServiceMIBCounters.rmIPPoolServResetTime.set(System.currentTimeMillis());
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Resetting the counters successfully.");
		state = MIBStates.RUNNING;
	}
	
	public String getClientId(String clientAddress){
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if(ipPoolClientEntry == null) {
			clientAddress = "NOT AVAILABLE";
		} else {
			return ipPoolClientEntry.getRMIPPoolClientID();
		}
		return clientAddress;
	}
	
	public MIBStates getState() {
		return state;
	}
	
	public void listenUpdateRMIPPoolServResetTimeEvent() {
		ipPoolServiceMIBCounters.rmIPPoolServResetTime.set(System.currentTimeMillis());
	}

	public void listenRMIPPoolServTotalRequestsEvent() {	
		ipPoolServiceMIBCounters.rmIPPoolServTotalRequests.incrementAndGet();
	}
	
	public void listenRMIPPoolServTotalRequestsEvent(String clientAddress) {
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if (ipPoolClientEntry != null){
			ipPoolClientEntry.incrementRMIPPoolServRequests();
		}		
	}
	
	public long getRMIPPoolServTotalInvalidRequests() {
		return ipPoolServiceMIBCounters.rmIPPoolServTotalInvalidRequests.get();
	}

	public long getRMIPPoolServTotalInvalidRequests(String clientAddress){
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if(ipPoolClientEntry != null){
			return ipPoolClientEntry.getRMIPPoolServInvalidTypes();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenRMIPPoolServTotalInvalidRequestsEvent() {	
		ipPoolServiceMIBCounters.rmIPPoolServTotalInvalidRequests.incrementAndGet();
	}
	
	public void listenRMIPPoolServTotalInvalidRequests(String clientAddress){
		listenRMIPPoolServTotalInvalidRequestsEvent();
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if(ipPoolClientEntry != null){
			ipPoolClientEntry.incrementRMIPPoolServInvalidTypes();
		}
	}
	
	public void listenRMIPPoolServTotalDupRequestsEvent(String clientAddress) {
		ipPoolServiceMIBCounters.rmIPPoolServTotalDupRequests.incrementAndGet();
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if (ipPoolClientEntry != null){
			ipPoolClientEntry.incrementRMIPPoolServDupRequests();		
		}
	}
	
	public void listenRMIPPoolServTotalResponsesEvent(String clientAddress) {	
		ipPoolServiceMIBCounters.rmIPPoolServTotalResponses.incrementAndGet();
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if (ipPoolClientEntry != null){
			ipPoolClientEntry.incrementRMIPPoolServResponses();
		}		
	}
	
	public void listenRMIPPoolServTotalMalformedRequestsEvent(String clientAddress) {	
		ipPoolServiceMIBCounters.rmIPPoolServTotalMalformedRequests.incrementAndGet();
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);		
		if (ipPoolClientEntry != null){
			ipPoolClientEntry.incrementRMIPPoolServMalformedRequests();
		}
	}
	
	public void listenRMIPPoolServTotalNoRecordsEvent(String clientAddress) {		
		ipPoolServiceMIBCounters.rmIPPoolServTotalNoRecords.incrementAndGet();
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if (ipPoolClientEntry != null){
			ipPoolClientEntry.incrementRMIPPoolServNoRecords();
		}		
	}
	
	public void listenRMIPPoolServTotalBadAuthenticatorsEvent(String clientAddress) {		
		ipPoolServiceMIBCounters.rmIPPoolServTotalBadAuthenticators.incrementAndGet();
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if (ipPoolClientEntry != null){
			ipPoolClientEntry.incrementRMIPPoolServBadAuthenticators();
		}		
	}
	
	public void listenRMIPPoolServTotalPacketsDroppedEvent(String clientAddress) {	
		ipPoolServiceMIBCounters.rmIPPoolServTotalPacketsDropped.incrementAndGet();
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if (ipPoolClientEntry != null){
			ipPoolClientEntry.incrementRMIPPoolServPacketsDropped();
		}
	}
	
	public void listenRMIPPoolServTotalUnknownTypesEvent(String clientAddress) {
		ipPoolServiceMIBCounters.rmIPPoolServTotalUnknownTypes.incrementAndGet();
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if (ipPoolClientEntry != null){
			ipPoolClientEntry.incrementRMIPPoolServUnknownTypes();
		}
	}
	
	public long getRMIPPoolServTotalRequests() {
		return ipPoolServiceMIBCounters.rmIPPoolServTotalRequests.get();
	}

	public long getRMIPPoolServTotalRequests(String clientAddress) {
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if (ipPoolClientEntry != null){
			return ipPoolClientEntry.getRMIPPoolServRequests();
		}
		return DEFAULT_CNT_VALUE;
	}	

	public long getRMIPPoolServTotalDupRequests() {
		return ipPoolServiceMIBCounters.rmIPPoolServTotalDupRequests.get();
	}

	public long getRMIPPoolServTotalDupRequests(String clientAddress) {
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if (ipPoolClientEntry != null){
			return ipPoolClientEntry.getRMIPPoolServDupRequests();
		}
		return DEFAULT_CNT_VALUE;
	}

	public long getRMIPPoolServTotalResponses() {
		return ipPoolServiceMIBCounters.rmIPPoolServTotalResponses.get();
	}

	public long getRMIPPoolServTotalResponses(String clientAddress) {
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if (ipPoolClientEntry != null){
			return ipPoolClientEntry.getRMIPPoolServResponses();
		}
		return DEFAULT_CNT_VALUE;
	}

	public long getRMIPPoolServTotalMalformedRequests() {
		return ipPoolServiceMIBCounters.rmIPPoolServTotalMalformedRequests.get();
	}

	public long getRMIPPoolServTotalMalformedRequests(String clientAddress) {
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if (ipPoolClientEntry != null){
			return ipPoolClientEntry.getRMIPPoolServMalformedRequests();
		}
		return DEFAULT_CNT_VALUE;
	}	

	public long getRMIPPoolServTotalBadAuthenticators() {
		return ipPoolServiceMIBCounters.rmIPPoolServTotalBadAuthenticators.get();
	}

	public long getRMIPPoolServTotalBadAuthenticators(String clientAddress) {
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if (ipPoolClientEntry != null){
			return ipPoolClientEntry.getRMIPPoolServBadAuthenticators();
		}
		return DEFAULT_CNT_VALUE;
	}	

	public long getRMIPPoolServTotalPacketsDropped() {
		return ipPoolServiceMIBCounters.rmIPPoolServTotalPacketsDropped.get();
	}

	public long getRMIPPoolServTotalPacketsDropped(String clientAddress) {
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if (ipPoolClientEntry != null){
			return ipPoolClientEntry.getRMIPPoolServPacketsDropped();
		}
		return DEFAULT_CNT_VALUE;
	}

	public long getRMIPPoolServTotalNoRecords() {
		return ipPoolServiceMIBCounters.rmIPPoolServTotalNoRecords.get();
	}

	public long getRMIPPoolServTotalNoRecords(String clientAddress) {
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if (ipPoolClientEntry != null){
			return ipPoolClientEntry.getRMIPPoolServNoRecords();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public long getRMIPPoolServTotalUnknownTypes() {
		return ipPoolServiceMIBCounters.rmIPPoolServTotalUnknownTypes.get();
	}

	public long getRMIPPoolServTotalUnknownTypes(String clientAddress) {
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if (ipPoolClientEntry != null){
			return ipPoolClientEntry.getRMIPPoolServUnknownTypes();
		}
		return DEFAULT_CNT_VALUE;
	}
		
	public long getIPAddressDiscoverTotalRequest(){
		return ipPoolServiceMIBCounters.ipAddressDiscoverTotalRequest.get();
	}
	
	public long getIPAddressDiscoverTotalRequest(String clientAddress){
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if(ipPoolClientEntry != null){
			return ipPoolClientEntry.getIPAddressDiscoverRequest();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenIPAddressDiscoverTotalRequest(){
		ipPoolServiceMIBCounters.ipAddressDiscoverTotalRequest.incrementAndGet();
	}
	
	public void listenIPAddressDiscoverTotalRequest(String clientAddress){
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if(ipPoolClientEntry != null){
			ipPoolClientEntry.incrementIPAddressDiscoverRequest();
		}
	}
		
	public long getIPAddressOfferTotalResponse(){
		return ipPoolServiceMIBCounters.ipAddressOfferTotalResponse.get();
	}
	
	public long getIPAddressOfferTotalResponse(String clientAddress){
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if(ipPoolClientEntry != null){
			return ipPoolClientEntry.getIPAddressOfferResponse();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenIPAddressOfferTotalResponse(){
		ipPoolServiceMIBCounters.ipAddressOfferTotalResponse.incrementAndGet();
	}
	
	public void listenIPAddressOfferTotalResponse(String clientAddress){
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if(ipPoolClientEntry != null){
			ipPoolClientEntry.incrementIPAddressOfferResponse();
		}
	}
	
	public long getIPAddressDeclineTotalResponse(){
		return ipPoolServiceMIBCounters.ipAddressDeclineTotalResponse.get();
	}
	
	public long getIPAddressDeclineTotalResponse(String clientAddress){
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if(ipPoolClientEntry != null){
			return ipPoolClientEntry.getIPAddressDeclineResponse();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenIPAddressDeclineTotalResponse(){
		ipPoolServiceMIBCounters.ipAddressDeclineTotalResponse.incrementAndGet();
	}
	
	public void listenIPAddressDeclineTotalResponse(String clientAddress){
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if(ipPoolClientEntry != null){
			ipPoolClientEntry.incrementIPAddressDeclineResponse();
		}
	}
	
	public long getIPAddressTotalAllocationRequest(){
		return ipPoolServiceMIBCounters.ipAddressTotalAllocationRequest.get();
	}
	
	public long getIPAddressTotalAllocationRequest(String clientAddress){
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if(ipPoolClientEntry != null){
			return ipPoolClientEntry.getIPAddressAllocationRequest();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenIPAddressTotalAllocationRequest(){
		ipPoolServiceMIBCounters.ipAddressTotalAllocationRequest.incrementAndGet();
	}
	
	public void listenIPAddressTotalAllocationRequest(String clientAddress){
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if(ipPoolClientEntry != null){
			ipPoolClientEntry.incrementIPAddressAllocationRequest();
		}
	}
	
	public long getIPAddressTotalReleaseRequest(){
		return ipPoolServiceMIBCounters.ipAddressTotalReleaseRequest.get();
	}
	
	public long getIPAddressTotalReleaseRequest(String clientAddress){
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if(ipPoolClientEntry != null){
			return ipPoolClientEntry.getIPAddressReleaseRequest();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenIPAddressTotalReleaseRequest(){
		ipPoolServiceMIBCounters.ipAddressTotalReleaseRequest.incrementAndGet();
	}
	
	public void listenIPAddressTotalReleaseRequest(String clientAddress){
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if( ipPoolClientEntry != null){
			ipPoolClientEntry.incrementIPAddressReleaseRequest();
		}
	}
	
	public long getIPAddressTotalUpdateRequest(){
		return ipPoolServiceMIBCounters.ipAddressTotalUpdateRequest.get(); 
	}
	
	public long getIPAddressTotalUpdateRequest(String clientAddress){
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if(ipPoolClientEntry != null){
			return ipPoolClientEntry.getIPAddressUpdateRequest();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenIPAddressTotalUpdateRequest(){
		ipPoolServiceMIBCounters.ipAddressTotalUpdateRequest.incrementAndGet();
	}

	public void listenIPAddressTotalUpdateRequest(String clientAddress){
		RMIPPoolClientEntry ipPoolClientEntry = ipPoolClientEntryMap.get(clientAddress);
		if(ipPoolClientEntry != null){
			ipPoolClientEntry.incrementIPAddressUpdateRequest();
		}
	}
	
	public long getRMIPPoolServUpTime() {
		return (System.currentTimeMillis() - ipPoolServiceMIBCounters.rmIPPoolServUpTime.get())/10;
	}
	
	public long getRMIPPoolServResetTime() {
		return (System.currentTimeMillis() - ipPoolServiceMIBCounters.rmIPPoolServResetTime.get())/10;
	}
	
	/**
	 *  below methods are used to maintain the entries for
	 *  the NAS Client Communicate with RM IP-Pool service
	 *  Through AAA Server.
	 *  
	 *  means
	 *  
	 *  	 request              request
	 *  NAS  -------  AAA Server  -------   RM IP-Pool
	 * 
	 */

	public String getNASIdentity(String nasIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasIdentity);
		if(clientEntry != null){
			return clientEntry.getRMIPPoolClientID();
		}
		return nasIdentity;
	}
	
	public void listenNASIPPoolServTotalRequest(String nasClientIdentity) {
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry == null){
			clientEntry = addNASClient(nasClientIdentity);
		}
		clientEntry.incrementRMIPPoolServRequests();
	}
	
	public long getNASIPPoolServTotalRequest(String nasClientIdentity) {
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry != null){
			 return clientEntry.getRMIPPoolServRequests();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenNASIPPoolServTotalResponse(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry == null){
			clientEntry = addNASClient(nasClientIdentity);
		}
		clientEntry.incrementRMIPPoolServResponses();
	}
	
	public long getNASIPPoolServTotalResponse(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry != null){
			return clientEntry.getRMIPPoolServResponses();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenNASIPPoolServTotalPacketsDropped(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry == null){
			clientEntry = addNASClient(nasClientIdentity);
		}
		clientEntry.incrementRMIPPoolServPacketsDropped();
	}
	
	public long getNASIPPoolServTotalPacketsDropped(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = getNasClientEntryMap().get(nasClientIdentity);
		if(clientEntry != null){
			return clientEntry.getRMIPPoolServPacketsDropped();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenNASIPPoolServTotalUnknownTypes(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = getNasClientEntryMap().get(nasClientIdentity);
		if(clientEntry == null){
			clientEntry = addNASClient(nasClientIdentity);
		}
		clientEntry.incrementRMIPPoolServUnknownTypes();
	}
	
	public long getNASIPPoolServTotalUnknownTypes(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = getNasClientEntryMap().get(nasClientIdentity);
		if(clientEntry != null){
			return clientEntry.getRMIPPoolServUnknownTypes();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenNASIPPoolServTotalInvalidRequests(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry == null){
			clientEntry = addNASClient(nasClientIdentity);
		}
		clientEntry.incrementRMIPPoolServInvalidTypes();
	}
	
	public long getNASIPPoolServTotalInvalidRequests(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry != null){
			return clientEntry.getRMIPPoolServInvalidTypes();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenNASIPPoolServTotalDiscoverRequest(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry == null){
			clientEntry = addNASClient(nasClientIdentity);
		}
		clientEntry.incrementIPAddressDiscoverRequest();
	}
	
	public long getNASIPPoolServTotalDiscoverRequest(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry != null){
			return clientEntry.getIPAddressDiscoverRequest();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenNASIPPoolServTotalDeclineResponse(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry == null){
			clientEntry = addNASClient(nasClientIdentity);
		}
		clientEntry.incrementIPAddressDeclineResponse();
	}
	
	public long getNASIPPoolServTotalDeclineResponse(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry != null){
			return clientEntry.getIPAddressDeclineResponse();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenNASIPPoolServTotalOfferResponse(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry == null){
			clientEntry = addNASClient(nasClientIdentity);
		}
		clientEntry.incrementIPAddressOfferResponse();
	}
	
	public long getNASIPPoolServTotalOfferResponse(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry != null){
			return clientEntry.getIPAddressOfferResponse();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenNASIPPoolServTotalAllocationRequest(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry == null){
			clientEntry = addNASClient(nasClientIdentity);
		}
		clientEntry.incrementIPAddressAllocationRequest();
	}
	
	public long getNASIPPoolServTotalAllocationRequest(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry != null){
			return clientEntry.getIPAddressAllocationRequest();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenNASIPPoolServTotalReleaseRequest(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry == null){
			clientEntry = addNASClient(nasClientIdentity);
		}
		clientEntry.incrementIPAddressReleaseRequest();
	}
	
	public long getNASIPPoolServTotalReleaseRequest(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry != null){
			return clientEntry.getIPAddressReleaseRequest();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public void listenNASIPPoolServTotalUpdateRequest(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry == null){
			clientEntry = addNASClient(nasClientIdentity);
		}
		clientEntry.incrementIPAddressUpdateRequest();
	}
	
	public long getNASIPPoolServTotalUpdateRequest(String nasClientIdentity){
		RMIPPoolClientEntry clientEntry = nasClientEntryMap.get(nasClientIdentity);
		if(clientEntry != null){
			return clientEntry.getIPAddressUpdateRequest();
		}
		return DEFAULT_CNT_VALUE;
	}
	
	public RMIPPoolClientEntry addNASClient(String nasClientIdentity){

		RMIPPoolClientEntry nasClient = nasClientEntryMap.get(nasClientIdentity);

		if(nasClient == null){	

			synchronized (this) {
				nasClient = nasClientEntryMap.get(nasClientIdentity);

				if(nasClient == null){
					int index = nasClientEntryMap.size();
					int nasClientIndex = index + 1;
					nasClient = ipPoolServiceMIBCounters.new RMIPPoolClientEntry(nasClientIndex, nasClientIdentity, nasClientIdentity);
					nasClientEntryMap.put(nasClientIdentity, nasClient);

					IpPoolNasClientEntryMBeanImpl nasClientEntry = new IpPoolNasClientEntryMBeanImpl(nasClientIndex,nasClientIdentity,this);
					try {
						if(nasClientTable != null){
							nasClientTable.addEntry(nasClientEntry,new ObjectName(nasClientEntry.getObjectName()));
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
								LogManager.getLogger().debug(MODULE, "Client Entry for NAS Client: " + nasClientIdentity + " added successfully at client Index: "+ nasClientIndex + " in Table.");
							}
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Client Entry for NAS Client: " + nasClientIdentity + " does not added into the NAS Client table. Reason: NAS Client Table is null.");
							}
						}
					} catch (SnmpStatusException e) {
						LogManager.getLogger().error(MODULE, "Error while adding Entry for NAS Client: " + nasClientIdentity + " in the NAS Client table. Reason: "+ e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
					}catch (MalformedObjectNameException e) {
						LogManager.getLogger().error(MODULE, "Error while adding Entry for NAS Client: " + nasClientIdentity + " in the NAS Client table. Reason: "+ e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
					}
				}
			}
		}
		return nasClient;
	}
		
	public void setNasClientTable(TableIpPoolNASClientStatisticsTable nasClientTable) {
		this.nasClientTable = nasClientTable;
	}
	
	public Map<String, RMIPPoolClientEntry> getNasClientEntryMap() {
		return nasClientEntryMap;
	}
	
	@Override
	public String toString() {
		
		Map<String, RMIPPoolClientEntry> nasClientEntry = getNasClientEntryMap();
		StringBuilder responseBuilder = new StringBuilder();
		
		int size = nasClientEntry.size();
		if(nasClientEntry != null && size > 0){

			for (RMIPPoolClientEntry nasClient : nasClientEntry.values()) {
				if(nasClient != null){
					responseBuilder.append("\n IP Pool NAS Client Statistics Summary for Nas :" + nasClient.getRMIPPoolClientID());
					responseBuilder.append("\n---------------------------------------------------------");

					responseBuilder.append("\nNas-Identity  			:" + nasClient.getRMIPPoolClientID());
					responseBuilder.append("\nIP-Pool-Request           :" + nasClient.getRMIPPoolServRequests());
					responseBuilder.append("\nIP-Pool-Response          :" + nasClient.getRMIPPoolServResponses());
					responseBuilder.append("\nDropped                   :" + nasClient.getRMIPPoolServPacketsDropped());
					responseBuilder.append("\nUnknown Type Request      :" + nasClient.getRMIPPoolServUnknownTypes());
					responseBuilder.append("\nInvalid Request           :" + nasClient.getRMIPPoolServInvalidTypes());

					responseBuilder.append("\nDiscover Request          :" + nasClient.getIPAddressDiscoverRequest());
					responseBuilder.append("\nOffer Success             :" + nasClient.getIPAddressOfferResponse());
					responseBuilder.append("\nDecline Response          :" + nasClient.getIPAddressDeclineResponse());
					responseBuilder.append("\nAllocation Request        :" + nasClient.getIPAddressAllocationRequest());
					responseBuilder.append("\nRelease Request           :" + nasClient.getIPAddressReleaseRequest());
					responseBuilder.append("\nUpdate Request            :" + nasClient.getIPAddressUpdateRequest());
					responseBuilder.append("\n");
				}
			}
		}else{
			responseBuilder.append("no data available for NAS clients.");
		}
		return responseBuilder.toString();
	}
}