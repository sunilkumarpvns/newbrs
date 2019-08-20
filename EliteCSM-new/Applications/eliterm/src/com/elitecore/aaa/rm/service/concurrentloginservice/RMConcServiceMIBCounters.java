package com.elitecore.aaa.rm.service.concurrentloginservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.radius.data.RadClientData;


public class RMConcServiceMIBCounters {
	public AtomicLong rmConcServUpTime = new AtomicLong(0);
	public AtomicLong rmConcServResetTime = new AtomicLong(0);	
	public AtomicLong rmConcServTotalRequests = new AtomicLong(0);
	public AtomicLong rmConcServTotalInvalidRequests = new AtomicLong(0);
	public AtomicLong rmConcServTotalDupRequests = new AtomicLong(0);
	public AtomicLong rmConcServTotalResponses = new AtomicLong(0);
	public AtomicLong rmConcServTotalMalformedRequests = new AtomicLong(0);
	public AtomicLong rmConcServTotalAccessRejects = new AtomicLong(0);
	public AtomicLong rmConcServTotalBadAuthenticators = new AtomicLong(0);
	public AtomicLong rmConcServTotalPacketsDropped = new AtomicLong(0);
	public AtomicLong rmConcServTotalNoRecords = new AtomicLong(0);
	public AtomicLong rmConcServTotalUnknownTypes = new AtomicLong(0);
	
	public Map<String, RMConcClientEntry> concClientEntryMap = new HashMap<String, RMConcClientEntry>();
	public Map<String, String> clientMap = new HashMap<String,String>();
	
	public boolean isInitialized = false;
	public boolean isOther = true;
	private AAAServerContext serverContext;
	public RMConcServiceMIBCounters(AAAServerContext serverContext){
		this.serverContext = serverContext;
	}
	public void init(){		
		if (!isInitialized){
			isOther = false;
			rmConcServUpTime.set(System.currentTimeMillis());
			rmConcServResetTime.set(System.currentTimeMillis());
			List<String> clientAddressList = getClientAddresses();
			if (clientAddressList != null && !clientAddressList.isEmpty()){
				int len = clientAddressList.size();				
				for(int clientIndexCounter = 1; clientIndexCounter <= len; clientIndexCounter++){					
					String clientAddress = clientAddressList.get(clientIndexCounter-1);
					if (clientAddress != null && clientAddress.trim().length() > 0) {
						clientAddress = clientAddress.trim();
						clientMap.put(String.valueOf(clientIndexCounter), clientAddress);
						concClientEntryMap.put(clientAddress, new RMConcClientEntry(clientIndexCounter, clientAddress, clientAddress));
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
		Map<String, RMConcClientEntry> concClientEntryMap_new = new HashMap<String, RMConcClientEntry>();
		Map<String, String> clientMap_new = new HashMap<String, String>();
		rmConcServResetTime.set(System.currentTimeMillis());
		
		RMConcClientEntry rmConcEntry;
		if (clientAddressList != null && !clientAddressList.isEmpty()){
			ListIterator<String> iterator = clientAddressList.listIterator();
			int clientIndexCounter = 0;
			while(iterator.hasNext()){
				clientIndexCounter++;					
				String clientAddress = String.valueOf(iterator.next());
				clientMap_new.put(String.valueOf(clientIndexCounter), clientAddress);
				rmConcEntry = (RMConcClientEntry)concClientEntryMap.get(clientAddress);
				if(rmConcEntry == null){
					concClientEntryMap_new.put(clientAddress, new RMConcClientEntry(clientIndexCounter, clientAddress, clientAddress));
				}else{
					concClientEntryMap_new.put(clientAddress, rmConcEntry);
				}
			}
			concClientEntryMap = concClientEntryMap_new;
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
	public static class RMConcClientEntry {        
		private int rmConcClientIndex;                   
        private String rmConcClientAddress;                 
        private String rmConcClientID;                      
        private long rmConcServPacketsDropped;            
        private long rmConcServRequests;         
        private long rmConcServDupRequests;             
        private long rmConcServResponses;             
        private long rmConcServBadAuthenticators;          
        private long rmConcServMalformedRequests;   
        private long rmConcServNoRecords;         
        private long rmConcServUnknownTypes;            
        
		public RMConcClientEntry(int rmConcClientIndex, String rmConcClientAddress, String rmConcClientID){
			this.rmConcClientIndex = rmConcClientIndex;
			this.rmConcClientAddress = rmConcClientAddress;
			this.rmConcClientID = rmConcClientID;
		}
        public String getRMConcClientAddress() {
			return rmConcClientAddress;
		}
		public String getRMConcClientID() {
			return rmConcClientID;
		}
		public int getRMConcClientIndex() {
			return rmConcClientIndex;
		}
		public long getRMConcServPacketsDropped() {
			return rmConcServPacketsDropped;
		}
		public void incrementRMConcServPacketsDropped() {
			this.rmConcServPacketsDropped++;
		}
		public long getRMConcServRequests() {
			return rmConcServRequests;
		}
		public void incrementRMConcServRequests() {
			this.rmConcServRequests++;
		}
		public long getRMConcServDupRequests() {
			return rmConcServDupRequests;
		}
		public void incrementRMConcServDupRequests() {
			this.rmConcServDupRequests++;
		}
		public long getRMConcServResponses() {
			return rmConcServResponses;
		}
		public void incrementRMConcServResponses() {
			this.rmConcServResponses++;
		}
		public long getRMConcServBadAuthenticators() {
			return rmConcServBadAuthenticators;
		}
		public void incrementRMConcServBadAuthenticators() {
			this.rmConcServBadAuthenticators++;
		}
		public long getRMConcServMalformedRequests() {
			return rmConcServMalformedRequests;
		}
		public void incrementRMConcServMalformedRequests() {
			this.rmConcServMalformedRequests++;
		}
		public long getRMConcServNoRecords() {
			return rmConcServNoRecords;
		}
		public void incrementRMConcServNoRecords() {
			this.rmConcServNoRecords++;
		}
		public long getRMConcServUnknownTypes() {
			return rmConcServUnknownTypes;
		}
		public void incrementRMConcServUnknownTypes() {
			this.rmConcServUnknownTypes++;
		}
	}
}
