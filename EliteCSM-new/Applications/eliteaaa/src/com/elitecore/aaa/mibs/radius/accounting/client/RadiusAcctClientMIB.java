package com.elitecore.aaa.mibs.radius.accounting.client;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.mibs.radius.accounting.client.autogen.TableRadiusAccServerTable;
import com.elitecore.aaa.radius.conf.RadESConfiguration;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;

public class RadiusAcctClientMIB {

	private static HashMap<String, RadiusAcctServerEntry> acctServerEntryMap;
	
	private static LinkedHashMap<String, String> serverMap;
	
	private static long radiusAcctClientInvalidServerAddresses;
	
	private AAAServerContext serverContext;
	
	private static boolean isInitialized;

	private static TableRadiusAccServerTable accServerTable;
	//private static boolean isOther = true;
	
	public RadiusAcctClientMIB(AAAServerContext serverContext) {
		this.serverContext = serverContext;
	}

	public  void init() {
		if(!isInitialized) {
			//isOther = false;
			
			acctServerEntryMap = new HashMap<String, RadiusAcctServerEntry>();
			
			serverMap = new LinkedHashMap<String, String>();
			
			List<DefaultExternalSystemData> esiList  = serverContext.getServerConfiguration().getRadESConfiguration().getESListByType(RadESConfiguration.RadESTypeConstants.RAD_ACCT_PROXY.type) ;
			int serverIndexCounter = 0;
			if(esiList!=null){
				int size = esiList.size();
				for(int i=0;i<size;i++){
					DefaultExternalSystemData externalSystem = esiList.get(i);
					if(externalSystem!=null){
						String serverAddress = String.valueOf(externalSystem.getIPAddress().getHostAddress());
						if (serverAddress != null && serverAddress.trim().length() > 0) {
							serverIndexCounter++;
							serverAddress = serverAddress.trim();
							serverMap.put(String.valueOf(serverIndexCounter), externalSystem.getName());
							acctServerEntryMap.put(externalSystem.getName(), new RadiusAcctServerEntry(serverIndexCounter, serverAddress,externalSystem.getPort(),externalSystem.getName()));
						}
					}	
				}
			}
			isInitialized = true;
		}
	}
	
	public String getClientIdentifier() {
		return "EliteRadius Client";
	}
	
	public static long getRadiusAcctClientInvalidServerAddresses() {
		return radiusAcctClientInvalidServerAddresses;
	}
	
	public static void incrementRadiusAcctClientInvalidServerAddresses() {
		radiusAcctClientInvalidServerAddresses++;
	}
	
	public static HashMap<String, String> getServerMap() {
		return serverMap;
	}
	
	public static long getRadiusAcctClientRetransmissions(String serverAddress) {
		RadiusAcctServerEntry acctServerEntry = acctServerEntryMap.get(serverAddress);
		if(acctServerEntry != null) {
			return acctServerEntry.getAcctClientRetrasmissions();
		}
		return 0;
	}
	
	
	public static long getRadiusAcctClientResponses(String serverAddress) {
		RadiusAcctServerEntry acctServerEntry = acctServerEntryMap.get(serverAddress);
		if(acctServerEntry != null) {
			return acctServerEntry.getAcctClientResponses();
		}
		return 0;
	}
	
	public static long getRadiusAcctClientMalformedResponses(String serverAddress) {
		RadiusAcctServerEntry acctServerEntry = acctServerEntryMap.get(serverAddress);
		if(acctServerEntry != null) {
			return acctServerEntry.getAcctClientMalformedResponses();
		}
		return 0;
	}
	
	public static void incrementRadiusAcctClientMalformedAccessResponse(String serverAddress) {
		RadiusAcctServerEntry acctServerEntry = acctServerEntryMap.get(serverAddress);
		if(acctServerEntry != null) {
			acctServerEntry.incrementAcctClientMalformedAccessResponses();
		}
	}
	
	public static void incrementRadiusAcctClientResponses(String serverAddress) {
		RadiusAcctServerEntry acctServerEntry = acctServerEntryMap.get(serverAddress);
		if(acctServerEntry != null) {
			acctServerEntry.incrementAcctClientResponses();
		}
	}
	
	public static long getRadiusAcctClientBadAuthenticators(String serverAddress) {
		RadiusAcctServerEntry acctServerEntry = acctServerEntryMap.get(serverAddress);
		if(acctServerEntry != null) {
			return acctServerEntry.getAcctClientBadAuthenticators();
		}
		return 0;
	}
	
	public static void incrementRadiusAcctClientBadAuthenticators(String serverAddress) {
		RadiusAcctServerEntry acctServerEntry = acctServerEntryMap.get(serverAddress);
		if(acctServerEntry != null) {
			acctServerEntry.incrementAcctClientBadAuthenticators();
		}
	}
	
	public static long getRadiusAcctClientPendingRequests(String serverAddress) {
		RadiusAcctServerEntry acctServerEntry = acctServerEntryMap.get(serverAddress);
		if(acctServerEntry != null) {
			return acctServerEntry.getAcctClientPendingRequests();
		}
		return 0;
	}
	
	public static long getRadiusAcctClientTimeouts(String serverAddress) {
		RadiusAcctServerEntry acctServerEntry = acctServerEntryMap.get(serverAddress);
		if(acctServerEntry != null) {
			return acctServerEntry.getAcctClientTimeouts();
		}
		return 0;
	}
	
	public static void incrementRadiusAcctClientTimeouts(String serverAddress) {
		RadiusAcctServerEntry acctServerEntry = acctServerEntryMap.get(serverAddress);
		if(acctServerEntry != null) {
			acctServerEntry.incrementAcctClientTimeouts();
		}
	}
	
	public static long getRadiusAcctClientUnknownTypes(String serverAddress) {
		RadiusAcctServerEntry acctServerEntry = acctServerEntryMap.get(serverAddress);
		if(acctServerEntry != null) {
			return acctServerEntry.getAcctClientUnknownTypes();
		}
		return 0;
	}
	
	public static void incrementRadiusAcctClientUnknowTypes(String serverAddress) {
		RadiusAcctServerEntry acctServerEntry = acctServerEntryMap.get(serverAddress);
		if(acctServerEntry != null) {
			acctServerEntry.incrementAcctClientUnknownTypes();
		}
	}
	
	public static long getRadiusAcctClientPacketsDropped(String serverAddress) {
		RadiusAcctServerEntry acctServerEntry = acctServerEntryMap.get(serverAddress);
		if(acctServerEntry != null) {
			return acctServerEntry.getAcctClientPacketsDropped();
		}
		return 0;
	}
	
	public static void incrementRadiusAcctClientPacketsDropped(String serverAddress) {
		RadiusAcctServerEntry acctServerEntry = acctServerEntryMap.get(serverAddress);
		if(acctServerEntry != null) {
			acctServerEntry.incrementAcctClientPacketsDropped();
		}
	}
	
	public static void setRadiusAcctClientRoundTripTime(String serverAddress, long roundTripTime) {
		RadiusAcctServerEntry acctServerEntry = acctServerEntryMap.get(serverAddress);
		if(acctServerEntry != null) {
			acctServerEntry.setRoundTripTime(roundTripTime);
		}
	}
	
	public static void incrementRadiusAcctClientPendingRequests(String serverAddress) {
		RadiusAcctServerEntry authServerEntry = acctServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.incrementAcctClientPendingRequests();
		}
	}
	
	public static void decrementRadiusAcctClientPendingRequests(String serverAddress) {
		RadiusAcctServerEntry authServerEntry = acctServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.decrementAcctClientPendingRequests();
		}
	}
	
	
	
	public static void incrementRadiusAccClientRequests(String serverAddress) {
		RadiusAcctServerEntry authServerEntry = acctServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.incrementRadiusAccClientRequests();
		}
	}
	
	public static void incrementRadiusAccClientRetransmissions(String serverAddress) {
		RadiusAcctServerEntry authServerEntry = acctServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.incrementRadiusAccClientRetransmissions();
		}
	}
	
	public static String getRadiusAccServerAddress(String serverAddress) {
		RadiusAcctServerEntry authServerEntry = acctServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAcctServerAddress();
		}
		return "";
	}
	
	public static int getRadiusAccClientServerPortNumber(String serverAddress) {
		RadiusAcctServerEntry authServerEntry = acctServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAcctClientServerPort();
		}
		return 0;
	}
	
	public static long getRadiusAccClientRoundTripTime(String serverAddress) {
		RadiusAcctServerEntry authServerEntry = acctServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAcctClientRoundTripTime();
		}
		return 0;
	}

	private static class RadiusAcctServerEntry {
		
		private AtomicInteger radiusAccServerIndex= new AtomicInteger();                           
		private String radiusAccServerAddress;                         
		private AtomicInteger  radiusAccClientServerPortNumber = new AtomicInteger();                
		private AtomicLong radiusAccClientRoundTripTime = new AtomicLong();               
		private AtomicLong radiusAccClientRequests = new AtomicLong();                        
		private AtomicLong radiusAccClientRetransmissions = new AtomicLong();                 
		private AtomicLong radiusAccClientResponses = new AtomicLong();                       
		private AtomicLong radiusAccClientMalformedResponses = new AtomicLong();              
		private AtomicLong radiusAccClientBadAuthenticators = new AtomicLong();              
		private AtomicLong radiusAccClientPendingRequests = new AtomicLong();               
		private AtomicLong radiusAccClientTimeouts = new AtomicLong();                       
		private AtomicLong radiusAccClientUnknownTypes = new AtomicLong();                    
		private AtomicLong radiusAccClientPacketsDropped = new AtomicLong();
		private String name;
		
		public RadiusAcctServerEntry(int radiusAccServerIndex, String radiusAccServerAddress,int radiusAccClientServerPortNumber,String name) {
			this.radiusAccServerIndex.set(radiusAccServerIndex);
			this.radiusAccServerAddress = radiusAccServerAddress;
			this.radiusAccClientServerPortNumber.set(radiusAccClientServerPortNumber);
			this.name = name;
		}
		
		public void incrementRadiusAccClientRequests() {
			radiusAccClientRequests.incrementAndGet();
		}
		
		public void incrementRadiusAccClientRetransmissions() {
			radiusAccClientRetransmissions.incrementAndGet();
		}
		
		public long getRadiusAccClientRequests() {
			return radiusAccClientRequests.get();
		}

		public int getAcctServerIndex() {
			return radiusAccServerIndex.get();
		}
		
		public String getAcctServerAddress() {
			return radiusAccServerAddress;
		}
		
		public int getAcctClientServerPort() {
			return radiusAccClientServerPortNumber.get();
		}
		
		public long getAcctClientRoundTripTime() {
			return radiusAccClientRoundTripTime.get();
		}
		
		
		public long getAcctClientRetrasmissions() {
			return radiusAccClientRetransmissions.get();
		}
		
	
		
		public long getAcctClientResponses() {
			return radiusAccClientResponses.get();
		}
		
		public void incrementAcctClientResponses() {
			radiusAccClientResponses.incrementAndGet();
		}
		

		public long getAcctClientMalformedResponses() {
			return radiusAccClientMalformedResponses.get();
		}
		
		public void incrementAcctClientMalformedAccessResponses() {
			radiusAccClientMalformedResponses.incrementAndGet();
		}

		public long getAcctClientBadAuthenticators() {
			return radiusAccClientBadAuthenticators.get();
		}

		public void incrementAcctClientBadAuthenticators() {
			radiusAccClientBadAuthenticators.incrementAndGet();
		}

		public long getAcctClientPendingRequests() {
			return radiusAccClientPendingRequests.get();
		}
		
		public long getAcctClientTimeouts() {
			return radiusAccClientTimeouts.get();
		}
		
		public void incrementAcctClientTimeouts() {
			radiusAccClientTimeouts.incrementAndGet();
		}
		
		public long getAcctClientUnknownTypes() {
			return radiusAccClientUnknownTypes.get();
		}

		public void incrementAcctClientUnknownTypes() {
			radiusAccClientUnknownTypes.incrementAndGet();
		}
		
		public long getAcctClientPacketsDropped() {
			return radiusAccClientPacketsDropped.get();
		}
		
		public void incrementAcctClientPacketsDropped() {
			radiusAccClientPacketsDropped.incrementAndGet();		
		}
		
		public void setRoundTripTime(long radiusAccClientRoundTripTime) {
			this.radiusAccClientRoundTripTime.set(radiusAccClientRoundTripTime);
		}
		
		public void incrementAcctClientPendingRequests() {
			radiusAccClientPendingRequests.incrementAndGet();
		}
		
		public void decrementAcctClientPendingRequests() {
			if(radiusAccClientPendingRequests.get() > 0) {
				radiusAccClientPendingRequests.decrementAndGet();
		}
		}
		
		@Override
		public String toString() {
			StringBuilder responseBuilder = new StringBuilder();
			
			responseBuilder.append("\n    Request Summary Of ESI : "+name);
			responseBuilder.append("\n----------------------------------------------------------------");
			responseBuilder.append("\nIP                              :" + this.radiusAccServerAddress);
			responseBuilder.append("\nPort                            :" + this.radiusAccClientServerPortNumber);
			responseBuilder.append("\nRound Trip Time                 :" + this.radiusAccClientRoundTripTime);
			responseBuilder.append("\nAccounting Requests             :" + this.radiusAccClientRequests);
			responseBuilder.append("\nAccounting Retrasmissions       :" + this.radiusAccClientRetransmissions);
			responseBuilder.append("\nAccounting Responses            :" + this.radiusAccClientResponses);
			responseBuilder.append("\nMalformedAccess Responses       :" + this.radiusAccClientMalformedResponses);
			responseBuilder.append("\nBadAuthenticators Responses     :" + this.radiusAccClientBadAuthenticators);
			responseBuilder.append("\nPending Requests                :" + this.radiusAccClientPendingRequests);
			responseBuilder.append("\nTimeouts Requests               :" + this.radiusAccClientTimeouts);
			responseBuilder.append("\nUnknownTypes Responses          :" + this.radiusAccClientUnknownTypes);
			responseBuilder.append("\nPackets Dropped                 :" + this.radiusAccClientPacketsDropped);
			responseBuilder.append("\n----------------------------------------------------------------");
			return responseBuilder.toString();
		}
		
	}

	public static long getRadiusAcctClientRequests(String serverAddress) {
		RadiusAcctServerEntry acctServerEntry = (RadiusAcctServerEntry)acctServerEntryMap.get(serverAddress);
		if(acctServerEntry != null) {
			return acctServerEntry.getRadiusAccClientRequests();
		}
		return 0;
	}
	
	public static String getSummary(String esiName) {
		if(acctServerEntryMap!=null && acctServerEntryMap.get(esiName)!=null){
			return acctServerEntryMap.get(esiName).toString();
		}else {
			return "ESI Not Found";
		}
	}

	public static TableRadiusAccServerTable getAccServerTable() {
		return accServerTable;
	}
	
	public static void setAcctServerTable(TableRadiusAccServerTable accServerTable) {
		RadiusAcctClientMIB.accServerTable = accServerTable;
	}
}
