package com.elitecore.aaa.radius.service.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.radius.data.RadClientData;

public class RadiusAuthServiceMIBCounters {
	
	public AtomicLong radiusAuthServUpTime;
	public AtomicLong radiusAuthServResetTime;
	/**
	 * RFC 2619 : The number of packets received on the authentication port.
	 */
	public AtomicLong radiusAuthServTotalAccessRequests;
	
	/**
	 * RFC 2619 : The number of RADIUS Access-Request packets received from 
	 *            unknown addresses.
	 */
	public AtomicLong radiusAuthServTotalInvalidRequests;

	/**
	 * RFC 2619 : 
	 */
	public AtomicLong radiusAuthServTotalDupAccessRequests;
	public AtomicLong radiusAuthServTotalAccessAccepts;
	public AtomicLong radiusAuthServTotalAccessRejects;
	public AtomicLong radiusAuthServTotalAccessChallenges;
	public AtomicLong radiusAuthServTotalMalformedAccessRequests;
	public AtomicLong radiusAuthServTotalBadAuthenticators;
	public AtomicLong radiusAuthServTotalPacketsDropped;
	public AtomicLong radiusAuthServTotalUnknownTypes;
	
	public Map <String, RadiusAuthClientEntry> authClientEntryMap ;
	public Map <String, String>clientMap ;
	public Map <String,Integer>rejectReasonMap;
	
	public boolean isInitialized;
	public boolean isOther = true;
	private AAAServerContext serverContext;
	public RadiusAuthServiceMIBCounters(AAAServerContext serverContext){
		this.serverContext = serverContext;
		radiusAuthServUpTime = new AtomicLong(0);
		radiusAuthServResetTime = new AtomicLong(0);
		radiusAuthServTotalAccessRequests = new AtomicLong(0);
		radiusAuthServTotalInvalidRequests = new AtomicLong(0);
		radiusAuthServTotalDupAccessRequests = new AtomicLong(0);
		radiusAuthServTotalAccessAccepts = new AtomicLong(0);
		radiusAuthServTotalAccessRejects = new AtomicLong(0);
		radiusAuthServTotalAccessChallenges = new AtomicLong(0);
		radiusAuthServTotalMalformedAccessRequests = new AtomicLong(0);
		radiusAuthServTotalBadAuthenticators = new AtomicLong(0);
		radiusAuthServTotalPacketsDropped = new AtomicLong(0);
		radiusAuthServTotalUnknownTypes = new AtomicLong(0);		
		authClientEntryMap = new HashMap<String, RadiusAuthClientEntry>();
		clientMap = new LinkedHashMap<String, String>();
		rejectReasonMap = new HashMap<String,Integer>();		
	}
	private List<String>getClientAdresses(){
		RadClientConfiguration clientConfiguration = serverContext.getServerConfiguration().getRadClientConfiguration();		
		if(clientConfiguration !=null){
			return clientConfiguration.getClientAddresses();
		}
		return new ArrayList<String>();
	}
	public void init(){
		List<String>clientAddressList = getClientAdresses();
		if (!isInitialized){
			isOther = false;
			
			radiusAuthServUpTime.set(System.currentTimeMillis());
			radiusAuthServResetTime.set(System.currentTimeMillis());
			
			if (clientAddressList != null && !clientAddressList.isEmpty()){
				ListIterator<?> iterator = clientAddressList.listIterator();
				int clientIndexCounter = 0;
				while(iterator.hasNext()){
					clientIndexCounter++;
					String clientAddress = String.valueOf(iterator.next());
					if (clientAddress != null && clientAddress.trim().length() > 0) {
						clientAddress = clientAddress.trim();
						clientMap.put(String.valueOf(clientIndexCounter), clientAddress);
						authClientEntryMap.put(clientAddress, new RadiusAuthClientEntry(clientIndexCounter, clientAddress, clientAddress));
					}
				}
			}
			isInitialized = true;
		}
	}

	public void reInitialize(){
		List<String> clientAddressList = getClientAdresses();
		Map <String, RadiusAuthClientEntry> localAuthClientEntryMap = new HashMap<String, RadiusAuthClientEntry>();
		Map <String, String>localClientMap = new HashMap<String, String>();
		radiusAuthServResetTime.set(System.currentTimeMillis());
		
		RadiusAuthClientEntry radiusAuthEntry;
		if (clientAddressList != null && !clientAddressList.isEmpty()){
			ListIterator<?> iterator = clientAddressList.listIterator();
			int clientIndexCounter = 0;
			while(iterator.hasNext()){
				clientIndexCounter++;					
				String clientAddress = String.valueOf(iterator.next());
				localClientMap.put(String.valueOf(clientIndexCounter), clientAddress);
				radiusAuthEntry = (RadiusAuthClientEntry)authClientEntryMap.get(clientAddress);
				if(radiusAuthEntry == null){
					localAuthClientEntryMap.put(clientAddress, new RadiusAuthClientEntry(clientIndexCounter, clientAddress, clientAddress));
				}else{
					localAuthClientEntryMap.put(clientAddress, radiusAuthEntry);
				}
			}
			authClientEntryMap = localAuthClientEntryMap;
			clientMap = localClientMap;
		}
		isInitialized = true;
	}

	
	/**
	 * 
	 * This method will reset the complete state of the MIB variables. This method
	 * should be called when the service is restarted without restarting the server. 
	 * 
	 * After call to this method, init method must be called with client list, 
	 * otherwise client wise details will not be maintained.
	 * 
	 */
	public void reset(){
		
		isInitialized = false;
		
		radiusAuthServUpTime.set(0);
		radiusAuthServResetTime.set(0);
		radiusAuthServTotalAccessRequests.set(0);
		radiusAuthServTotalInvalidRequests.set(0);
		radiusAuthServTotalDupAccessRequests.set(0);
		radiusAuthServTotalAccessAccepts.set(0);
		radiusAuthServTotalAccessRejects.set(0);
		radiusAuthServTotalAccessChallenges.set(0);
		radiusAuthServTotalMalformedAccessRequests.set(0);
		radiusAuthServTotalBadAuthenticators.set(0);
		radiusAuthServTotalPacketsDropped.set(0);
		radiusAuthServTotalUnknownTypes.set(0);
		
		clientMap = new HashMap<String, String>();
		authClientEntryMap = new HashMap<String, RadiusAuthClientEntry>();		
	}
	public RadClientData getClientData(String strIp){
		RadClientConfiguration clientConfiguration = serverContext.getServerConfiguration().getRadClientConfiguration();
		RadClientData clientData = null;
		if(clientConfiguration != null){
			clientData = clientConfiguration.getClientData(strIp);
		}
		return clientData;
	}

	public static class RadiusAuthClientEntry {
        
		private int radiusAuthClientIndex;                   
        private String radiusAuthClientAddress;                 
        private String radiusAuthClientID;                      
        private long radiusAuthServAccessRequests;            
        private long radiusAuthServDupAccessRequests;         
        private long radiusAuthServAccessAccepts;             
        private long radiusAuthServAccessRejects;             
        private long radiusAuthServAccessChallenges;          
        private long radiusAuthServMalformedAccessRequests;   
        private long radiusAuthServBadAuthenticators;         
        private long radiusAuthServPacketsDropped;            
        private long radiusAuthServUnknownTypes;
        private HashMap<String,Integer> rejectReasonMap;
        
		public RadiusAuthClientEntry(int radiusAuthClientIndex, String radiusAuthClientAddress, String radiusAuthClientID){
			this.radiusAuthClientIndex = radiusAuthClientIndex;
			this.radiusAuthClientAddress = radiusAuthClientAddress;
			this.radiusAuthClientID = radiusAuthClientID;
			rejectReasonMap = new HashMap<String,Integer>();
		}
        public String getRadiusAuthClientAddress() {
			return radiusAuthClientAddress;
		}
		public String getRadiusAuthClientID() {
			return radiusAuthClientID;
		}
		public int getRadiusAuthClientIndex() {
			return radiusAuthClientIndex;
		}
		public long getRadiusAuthServAccessAccepts() {
			return radiusAuthServAccessAccepts;
		}
		public void incrementRadiusAuthServAccessAccepts() {
			this.radiusAuthServAccessAccepts++;
		}
		public long getRadiusAuthServAccessChallenges() {
			return radiusAuthServAccessChallenges;
		}
		public void incrementRadiusAuthServAccessChallenges() {
			this.radiusAuthServAccessChallenges++;
		}
		public long getRadiusAuthServAccessRejects() {
			return radiusAuthServAccessRejects;
		}
		
		public HashMap<String, Integer> getRadiusAuthServAccessRejectsReasons() {
			return rejectReasonMap;
		}

		public void incrementRadiusAuthServAccessRejects() {
			this.radiusAuthServAccessRejects++;
		}
		public void incrementRadiusAuthServAccessRejectsReason(String strReason) {
			if(rejectReasonMap.containsKey(strReason)) {
				Integer cnt = rejectReasonMap.get(strReason);
				cnt++;
				rejectReasonMap.put(strReason,cnt);
			}else {
				Integer cnt = new Integer(1);
				rejectReasonMap.put(strReason,cnt);
			}
		}
		public long getRadiusAuthServAccessRequests() {
			return radiusAuthServAccessRequests;
		}
		public void incrementRadiusAuthServAccessRequests() {
			this.radiusAuthServAccessRequests++;
		}
		public long getRadiusAuthServBadAuthenticators() {
			return radiusAuthServBadAuthenticators;
		}
		public void incrementRadiusAuthServBadAuthenticators() {
			this.radiusAuthServBadAuthenticators++;
		}
		public long getRadiusAuthServDupAccessRequests() {
			return radiusAuthServDupAccessRequests;
		}
		public void incrementRadiusAuthServDupAccessRequests() {
			this.radiusAuthServDupAccessRequests++;
		}
		public long getRadiusAuthServMalformedAccessRequests() {
			return radiusAuthServMalformedAccessRequests;
		}
		public void incrementRadiusAuthServMalformedAccessRequests() {
			this.radiusAuthServMalformedAccessRequests++;
		}
		public long getRadiusAuthServPacketsDropped() {
			return radiusAuthServPacketsDropped;
		}
		public void incrementRadiusAuthServPacketsDropped() {
			this.radiusAuthServPacketsDropped++;
		}
		public long getRadiusAuthServUnknownTypes() {
			return radiusAuthServUnknownTypes;
		}
		public void incrementRadiusAuthServUnknownTypes() {
			this.radiusAuthServUnknownTypes++;
		}
	}
	
}
