package com.elitecore.aaa.radius.service.acct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.radius.data.RadClientData;


public class RadiusAcctServiceMIBCounters {
	public AtomicLong radiusAccServUpTime = new AtomicLong(0);
	public AtomicLong radiusAccServResetTime = new AtomicLong(0);	
	public AtomicLong radiusAccServTotalRequests = new AtomicLong(0);
	public AtomicLong radiusAccServTotalInvalidRequests = new AtomicLong(0);
	public AtomicLong radiusAccServTotalDupRequests = new AtomicLong(0);
	public AtomicLong radiusAccServTotalResponses = new AtomicLong(0);
	public AtomicLong radiusAccServTotalMalformedRequests = new AtomicLong(0);
	public AtomicLong radiusAccServTotalAccessRejects = new AtomicLong(0);
	public AtomicLong radiusAccServTotalBadAuthenticators = new AtomicLong(0);
	public AtomicLong radiusAccServTotalPacketsDropped = new AtomicLong(0);
	public AtomicLong radiusAccServTotalNoRecords = new AtomicLong(0);
	public AtomicLong radiusAccServTotalUnknownTypes = new AtomicLong(0);
	
	public Map<String, RadiusAcctClientEntry> acctClientEntryMap = new HashMap<String, RadiusAcctClientEntry>();
	public Map<String, String> clientMap = new HashMap<String,String>();
	
	public boolean isInitialized = false;
	public boolean isOther = true;
	private AAAServerContext serverContext;
	public RadiusAcctServiceMIBCounters(AAAServerContext serverContext){
		this.serverContext = serverContext;
	}
	public void init(){		
		if (!isInitialized){
			isOther = false;
			radiusAccServUpTime.set(System.currentTimeMillis());
			radiusAccServResetTime.set(System.currentTimeMillis());
			List<String> clientAddressList = getClientAddresses();
			if (clientAddressList != null && !clientAddressList.isEmpty()){
				int len = clientAddressList.size();				
				for(int clientIndexCounter = 1; clientIndexCounter <= len; clientIndexCounter++){					
					String clientAddress = clientAddressList.get(clientIndexCounter-1);
					if (clientAddress != null && clientAddress.trim().length() > 0) {
						clientAddress = clientAddress.trim();
						clientMap.put(String.valueOf(clientIndexCounter), clientAddress);
						acctClientEntryMap.put(clientAddress, new RadiusAcctClientEntry(clientIndexCounter, clientAddress, clientAddress));
					}
				}
			}
			isInitialized = true;
		}
	}
	private List<String>getClientAddresses(){
		RadClientConfiguration clientConfiguration = serverContext.getServerConfiguration().getRadClientConfiguration();
		
		if(clientConfiguration != null){
			return clientConfiguration.getClientAddresses();				
		}
		
		return new ArrayList<String>();
	}
	public void reInitialize(){
		List<String> clientAddressList = getClientAddresses();
		Map<String, RadiusAcctClientEntry> acctClientEntryMap_new = new HashMap<String, RadiusAcctClientEntry>();
		Map<String, String> clientMap_new = new HashMap<String, String>();
		radiusAccServResetTime.set(System.currentTimeMillis());
		
		RadiusAcctClientEntry radiusAcctEntry;
		if (clientAddressList != null && !clientAddressList.isEmpty()){
			ListIterator<?> iterator = clientAddressList.listIterator();
			int clientIndexCounter = 0;
			while(iterator.hasNext()){
				clientIndexCounter++;					
				String clientAddress = String.valueOf(iterator.next());
				clientMap_new.put(String.valueOf(clientIndexCounter), clientAddress);
				radiusAcctEntry = (RadiusAcctClientEntry)acctClientEntryMap.get(clientAddress);
				if(radiusAcctEntry == null){
					acctClientEntryMap_new.put(clientAddress, new RadiusAcctClientEntry(clientIndexCounter, clientAddress, clientAddress));
				}else{
					acctClientEntryMap_new.put(clientAddress, radiusAcctEntry);
				}
			}
			acctClientEntryMap = acctClientEntryMap_new;
			clientMap = clientMap_new;
		}
		isInitialized = true;
		
	}
	public RadClientData getClientData(String strIp){
		RadClientConfiguration clientConfiguration = serverContext.getServerConfiguration().getRadClientConfiguration();
		RadClientData clientData = null;
		if(clientConfiguration != null){
			clientData = clientConfiguration.getClientData(strIp);
		}
		return clientData;
	}
	public static class RadiusAcctClientEntry {        
		private int radiusAccClientIndex;                   
        private String radiusAccClientAddress;                 
        private String radiusAccClientID;                      
        private long radiusAccServPacketsDropped;            
        private long radiusAccServRequests;         
        private long radiusAccServDupRequests;             
        private long radiusAccServResponses;             
        private long radiusAccServBadAuthenticators;          
        private long radiusAccServMalformedRequests;   
        private long radiusAccServNoRecords;         
        private long radiusAccServUnknownTypes;            
        
		public RadiusAcctClientEntry(int radiusAcctClientIndex, String radiusAccClientAddress, String radiusAccClientID){
			this.radiusAccClientIndex = radiusAcctClientIndex;
			this.radiusAccClientAddress = radiusAccClientAddress;
			this.radiusAccClientID = radiusAccClientID;
		}
        public String getRadiusAccClientAddress() {
			return radiusAccClientAddress;
		}
		public String getRadiusAccClientID() {
			return radiusAccClientID;
		}
		public int getRadiusAccClientIndex() {
			return radiusAccClientIndex;
		}
		public long getRadiusAccServPacketsDropped() {
			return radiusAccServPacketsDropped;
		}
		public void incrementRadiusAccServPacketsDropped() {
			this.radiusAccServPacketsDropped++;
		}
		public long getRadiusAccServRequests() {
			return radiusAccServRequests;
		}
		public void incrementRadiusAccServRequests() {
			this.radiusAccServRequests++;
		}
		public long getRadiusAccServDupRequests() {
			return radiusAccServDupRequests;
		}
		public void incrementRadiusAccServDupRequests() {
			this.radiusAccServDupRequests++;
		}
		public long getRadiusAccServResponses() {
			return radiusAccServResponses;
		}
		public void incrementRadiusAccServResponses() {
			this.radiusAccServResponses++;
		}
		public long getRadiusAccServBadAuthenticators() {
			return radiusAccServBadAuthenticators;
		}
		public void incrementRadiusAccServBadAuthenticators() {
			this.radiusAccServBadAuthenticators++;
		}
		public long getRadiusAccServMalformedRequests() {
			return radiusAccServMalformedRequests;
		}
		public void incrementRadiusAccServMalformedRequests() {
			this.radiusAccServMalformedRequests++;
		}
		public long getRadiusAccServNoRecords() {
			return radiusAccServNoRecords;
		}
		public void incrementRadiusAccServNoRecords() {
			this.radiusAccServNoRecords++;
		}
		public long getRadiusAccServUnknownTypes() {
			return radiusAccServUnknownTypes;
		}
		public void incrementRadiusAccServUnknownTypes() {
			this.radiusAccServUnknownTypes++;
		}
	}
}
