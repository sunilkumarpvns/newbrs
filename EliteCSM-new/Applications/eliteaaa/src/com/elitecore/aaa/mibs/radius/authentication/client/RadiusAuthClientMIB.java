package com.elitecore.aaa.mibs.radius.authentication.client;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.mibs.radius.authentication.client.autogen.TableRadiusAuthServerTable;
import com.elitecore.aaa.radius.conf.RadESConfiguration;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;

public class RadiusAuthClientMIB {

	private static HashMap<String, RadiusAuthServerEntry> authServerEntryMap;
	
	private static LinkedHashMap<String, String> serverMap;
	
	private AAAServerContext serverContext;
	
	private static long radiusAuthClientInvalidServerAddresses;
	
	//private static long radiusAuthClientIdentifier;
	
	//private static long radiusAuthClinetRoundTripTime = 0;
	
	private static boolean isInitialized;
	//private static boolean isOther = true;
	
	private static TableRadiusAuthServerTable authClientTable;
	
	public RadiusAuthClientMIB(AAAServerContext serverContext) {
		this.serverContext = serverContext;
	}

	public void init() {
		if(!isInitialized) {
			//isOther = false;
			
			authServerEntryMap = new HashMap<String, RadiusAuthServerEntry>();
			
			serverMap = new LinkedHashMap<String, String>();
			
			List<DefaultExternalSystemData> esiList  = serverContext.getServerConfiguration().getRadESConfiguration().getESListByType(RadESConfiguration.RadESTypeConstants.RAD_AUTH_PROXY.type) ;
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
							authServerEntryMap.put(externalSystem.getName(), new RadiusAuthServerEntry(serverIndexCounter, serverAddress,externalSystem.getPort(),externalSystem.getName()));
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
	
	public static long getRadiusAuthClientInvalidServerAddresses() {
		return radiusAuthClientInvalidServerAddresses;
	}
	
	public static void incrementRadiusAuthClientInvalidServerAddresses() {
		radiusAuthClientInvalidServerAddresses++;
	}
	
	public static HashMap<String, String> getServerMap() {
		return serverMap;
	}
	
	public static void setRadiusAuthClientRoundTripTime(String serverAddress, long roundTripTime) {
		RadiusAuthServerEntry authServerEntry = (RadiusAuthServerEntry)authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.setRoundTripTime(roundTripTime);
		}
	}
	
	public static long getRadiusAuthClientRoundTripTime(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = (RadiusAuthServerEntry)authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAuthClientRoundTripTime();
		}
		return 0;
	}
	
	public static long getRadiusAuthClientAccessRequests(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAuthClientAccessRequests();
		}
		return 0;
	}
	
	public static void incrementRadiusClientAuthAccessRequests(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.incrementAuthClientAccessRequests();
		}
	}
	
	public static long getRadiusAuthClientAccessRetransmissions(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAuthClientAccessRetrasmissions();
		}
		return 0;
	}
	
	public static void incrementRadiusClientAuthAccessRetransmissions(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.incrementAuthClientAccessRetrasmissions();
		}
	}
	
	public static long getRadiusAuthClientAccessAccept(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAuthClientAccessAccept();
		}
		return 0;
	}
	
	public static void incrementRadiusAuthClientAccessAccept(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.incrementAuthClientAccessAccept();
		}
	}
	
	public static long getRadiusAuthClientAccessReject(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAuthClientAccessReject();
		}
		return 0;
	}
	
	public static void incrementRadiusAuthClientAccessReject(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.incrementAuthClientAccessReject();
		}
	}

	public static long getRadiusAuthClientAccessChallenge(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAuthClientAccessChalleneges();
		}
		return 0;
	}
	
	public static void incrementRadiusAuthClientAccessChallenges(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.incrementAuthClientAccessChalleneges();
		}
	}
	
	public static long getRadiusAuthClientMalformedAccessResponse(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAuthClientMalformedAccessResponses();
		}
		return 0;
	}
	
	public static void incrementRadiusAuthClientMalformedAccessResponse(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.incrementAuthClientMalformedAccessResponses();
		}
	}
	
	public static long getRadiusAuthClientBadAuthenticators(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAuthClientBadAuthenticators();
		}
		return 0;
	}
	
	public static void incrementRadiusAuthClientBadAuthenticators(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.incrementAuthClientBadAuthenticators();
		}
	}
	
	public static long getRadiusAuthClientPendingRequests(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAuthClientPendingRequests();
		}
		return 0;
	}
	
	public static void setRadiusAuthClientPendingRequests(int intPendingRequest,String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.setAuthClientPendingRequests(intPendingRequest);
		}
	}
	
	public static long getRadiusAuthClientTimeouts(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAuthClientTimeouts();
		}
		return 0;
	}
	
	public static void incrementRadiusAuthClientTimeouts(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.incrementAuthClientTimeouts();
		}
	}
	
	public static long getRadiusAuthClientUnknownTypes(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAuthClientUnknownTypes();
		}
		return 0;
	}
	
	public static void incrementRadiusAuthClientUnknowTypes(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.incrementAuthClientUnknownTypes();
		}
	}
	
	public static long getRadiusAuthClientPacketsDropped(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAuthClientPacketsDropped();
		}
		return 0;
	}
	
	public static void incrementRadiusAuthClientPacketsDropped(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.incrementAuthClientPacketsDropped();
		}
	}
	
	public static void incrementRadiusAuthClientPandingRequests(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.incrementAuthClientPandingRequests();
		}
	}
	
	public static void decrementRadiusAuthClientPandingRequests(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			authServerEntry.decrementAuthClientPandingRequests();
		}
	}
	
	public static int getRadiusAuthClientServerPort(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAuthClientServerPort();
		}
		return 0;
	}

	public static String getRadiusAuthServerAddress(String serverAddress) {
		RadiusAuthServerEntry authServerEntry = authServerEntryMap.get(serverAddress);
		if(authServerEntry != null) {
			return authServerEntry.getAuthServerAddress();
		}
		return "";
	}

	private static class RadiusAuthServerEntry {
		private AtomicInteger radiusAuthServerIndex = new AtomicInteger();
		private String radiusAuthServerIP;
		private AtomicInteger radiusAuthClientServerPort = new AtomicInteger();
		private String name;
		private AtomicLong radiusAuthClientRoundTripTime = new AtomicLong();
		private AtomicLong radiusAuthClientAccessRequests = new AtomicLong();
		private AtomicLong radiusAuthClientAccessRetrasmissions = new AtomicLong();
		private AtomicLong radiusAuthClientAccessAccepts = new AtomicLong();
		private AtomicLong radiusAuthClientAccessRejects = new AtomicLong();
		private AtomicLong radiusAuthClientAccessChallenges = new AtomicLong();
		private AtomicLong radiusAuthClientMalformedAccessResponses = new AtomicLong();
		private AtomicLong radiusAuthClientBadAuthenticators = new AtomicLong();
		private AtomicLong radiusAuthClientPendingRequests = new AtomicLong();
		private AtomicLong radiusAuthClientTimeouts = new AtomicLong();
		private AtomicLong radiusAuthClientUnknownTypes = new AtomicLong();
		private AtomicLong radiusAuthClientPacketsDropped = new AtomicLong();
		
		public RadiusAuthServerEntry(int radiusAuthServerIndex, String radiusAuthServerAddress,int radiusAuthClientServerPort,String name) {
			this.radiusAuthServerIndex.set(radiusAuthServerIndex);
			this.radiusAuthServerIP = radiusAuthServerAddress;
			this.radiusAuthClientServerPort.set(radiusAuthClientServerPort);
			this.name = name;
		}
		
		public int getAuthServerIndex() {
			return radiusAuthServerIndex.get();
		}
		
		public String getAuthServerAddress() {
			return radiusAuthServerIP;
		}
		
		public int getAuthClientServerPort() {
			return radiusAuthClientServerPort.get();
		}
		
		public void setRoundTripTime(long roundTripTime) {
			radiusAuthClientRoundTripTime.set(roundTripTime);
		}

		public long getAuthClientRoundTripTime() {
			return radiusAuthClientRoundTripTime.get();
		}
		
		public long getAuthClientAccessRequests() {
			return radiusAuthClientAccessRequests.get();
		}
		
		public void incrementAuthClientAccessRequests() {
			radiusAuthClientAccessRequests.incrementAndGet();
		}

		
		public long getAuthClientAccessRetrasmissions() {
			return radiusAuthClientAccessRetrasmissions.get();
		}
		
		public void incrementAuthClientAccessRetrasmissions() {
			radiusAuthClientAccessRetrasmissions.incrementAndGet();
		}

		
		public long getAuthClientAccessAccept() {
			return radiusAuthClientAccessAccepts.get();
		}
		
		public void incrementAuthClientAccessAccept() {
			radiusAuthClientAccessAccepts.incrementAndGet();
		}

		
		public long getAuthClientAccessReject() {
			return radiusAuthClientAccessRejects.get();
		}

		public void incrementAuthClientAccessReject() {
			radiusAuthClientAccessRejects.incrementAndGet();		
		}
		
		public long getAuthClientAccessChalleneges() {
			return radiusAuthClientAccessChallenges.get();
		}
		
		public void incrementAuthClientAccessChalleneges() {
			radiusAuthClientAccessChallenges.incrementAndGet();
		}

		public long getAuthClientMalformedAccessResponses() {
			return radiusAuthClientMalformedAccessResponses.get();
		}
		
		public void incrementAuthClientMalformedAccessResponses() {
			radiusAuthClientMalformedAccessResponses.incrementAndGet();
		}

		public long getAuthClientBadAuthenticators() {
			return radiusAuthClientBadAuthenticators.get();
		}

		public void incrementAuthClientBadAuthenticators() {
			radiusAuthClientBadAuthenticators.incrementAndGet();
		}

		public long getAuthClientPendingRequests() {
			return radiusAuthClientPendingRequests.get();
		}
		
		public void incrementAuthClientPandingRequests() {
			radiusAuthClientPendingRequests.incrementAndGet();
		}
		
		public void decrementAuthClientPandingRequests() {
			if(radiusAuthClientPendingRequests.get() > 0) {
				radiusAuthClientPendingRequests.decrementAndGet();
		}
		}
		
		public void setAuthClientPendingRequests(int intPendingRequestCnt) {
			radiusAuthClientPendingRequests.set(intPendingRequestCnt);
		}
		
		public long getAuthClientTimeouts() {
			return radiusAuthClientTimeouts.get();
		}
		
		public void incrementAuthClientTimeouts() {
			radiusAuthClientTimeouts.incrementAndGet();
		}
		
		public long getAuthClientUnknownTypes() {
			return radiusAuthClientUnknownTypes.get();
		}

		public void incrementAuthClientUnknownTypes() {
			radiusAuthClientUnknownTypes.incrementAndGet();
		}
		
		public long getAuthClientPacketsDropped() {
			return radiusAuthClientPacketsDropped.get();
		}
		
		public void incrementAuthClientPacketsDropped() {
			radiusAuthClientPacketsDropped.incrementAndGet();		
		}
		
		@Override
		public String toString() {
			StringBuilder responseBuilder = new StringBuilder();
			
			
			responseBuilder.append("\n    Request Summary Of ESI : "+name);
			responseBuilder.append("\n----------------------------------------------------------------");
			responseBuilder.append("\nIP                              :" + this.radiusAuthServerIP);
			responseBuilder.append("\nPort                            :" + this.radiusAuthClientServerPort);
			responseBuilder.append("\nRound Trip Time                 :" + this.radiusAuthClientRoundTripTime+" ms");
			responseBuilder.append("\nAccess Requests                 :" + this.radiusAuthClientAccessRequests);
			responseBuilder.append("\nAccess Retrasmissions           :" + this.radiusAuthClientAccessRetrasmissions);
			responseBuilder.append("\nAccess Accepts                  :" + this.radiusAuthClientAccessAccepts);
			responseBuilder.append("\nAccess Rejects                  :" + this.radiusAuthClientAccessRejects);
			responseBuilder.append("\nAccess Challenges               :" + this.radiusAuthClientAccessChallenges);
			responseBuilder.append("\nMalformedAccess Responses       :" + this.radiusAuthClientMalformedAccessResponses);
			responseBuilder.append("\nBadAuthenticators Responses     :" + this.radiusAuthClientBadAuthenticators);
			responseBuilder.append("\nPending Requests                :" + this.radiusAuthClientPendingRequests);
			responseBuilder.append("\nTimeouts Requests               :" + this.radiusAuthClientTimeouts);
			responseBuilder.append("\nUnknownTypes Responses          :" + this.radiusAuthClientUnknownTypes);
			responseBuilder.append("\nPackets Dropped                 :" + this.radiusAuthClientPacketsDropped);
			responseBuilder.append("\n----------------------------------------------------------------");
			return responseBuilder.toString();
		}
	}
	
	public static String getSummary(String esiName) {
		if(authServerEntryMap!=null && authServerEntryMap.get(esiName)!=null){
			return authServerEntryMap.get(esiName).toString();
		}else {
			return "ESI Not Found";
		}
	}

	public static TableRadiusAuthServerTable getAuthClientTable() {
		return RadiusAuthClientMIB.authClientTable;
	}
	
	public static void setAuthClientTable(TableRadiusAuthServerTable authClientTable) {
		RadiusAuthClientMIB.authClientTable = authClientTable;
	}
}
