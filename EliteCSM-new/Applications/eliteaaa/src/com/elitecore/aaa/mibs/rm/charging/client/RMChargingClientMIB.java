package com.elitecore.aaa.mibs.rm.charging.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.conf.RadESConfiguration.RadESTypeConstants;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;

public class RMChargingClientMIB {
	
	private AAAServerContext serverContext;
	private static Map<String, RMChargingServerEntry> chargingServerNameToCounterMap;
	private static Map<String, String> chargingServerNameToAddressMap;
	private static boolean isInitialize;
	private static long DEFAULT_CNT_VALUE = 0;
	
	public RMChargingClientMIB(AAAServerContext serverContext) {
		this.serverContext = serverContext;
		chargingServerNameToCounterMap = new HashMap<String, RMChargingClientMIB.RMChargingServerEntry>();
		chargingServerNameToAddressMap = new HashMap<String, String>();
	}
	
	public void init(){
		if(!isInitialize){
			List<DefaultExternalSystemData> chargingGWList = serverContext.getServerConfiguration().getRadESConfiguration().getESListByType(RadESTypeConstants.CHARGING_GATEWAY.type);
			
			if(chargingGWList != null && !(chargingGWList.isEmpty())){

				for (DefaultExternalSystemData chargingGW : chargingGWList) {
				
					String chargingGWName = chargingGW.getName();
					String chargingGWAddress = chargingGW.getIPAddress().getHostAddress();
					if(chargingGWAddress != null && !(chargingGWAddress.trim().isEmpty())){
						chargingServerNameToAddressMap.put(chargingGWName,chargingGWAddress);
						chargingServerNameToCounterMap.put(chargingGWName,new RMChargingServerEntry(chargingGWName, chargingGWAddress));
					}
				}
			}
			isInitialize = true;
		}
	}
	
	public static long getRmChargingRequest(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			return rmChargingServerEntry.getRmChargingRequest();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrRmChargingRequest(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			rmChargingServerEntry.incrRmChargingRequest();
		}
	}

	public static long getRmChargingResponses(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			return rmChargingServerEntry.getRmChargingResponses();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrRmChargingResponses(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			rmChargingServerEntry.incrRmChargingResponses();
		}
	}

	public static long getRmChargingRequestDropped(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			return rmChargingServerEntry.getRmChargingRequestDropped();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrRmChargingRequestDropped(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			rmChargingServerEntry.incrRmChargingRequestDropped();
		}
	}

	public static long getRmChargingUnknownRequestType(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			return rmChargingServerEntry.getRmChargingUnknownRequestType();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrRmChargingUnknownRequestType(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			rmChargingServerEntry.incrRmChargingUnknownRequestType();
		}
	}

	public static long getRmChargingRequestTimeout(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			return rmChargingServerEntry.getRmChargingRequestTimeout();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrRmChargingRequestTimeout(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			rmChargingServerEntry.incrRmChargingRequestTimeout();
		}
	}
	
	public static long getRmChargingRequestRetransmission(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			return rmChargingServerEntry.getRmChargingRequestRetransmission();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrRmChargingRequestRetransmission(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			rmChargingServerEntry.incrRmChargingRequestRetransmission();
		}
	}

	public static long getRmChargingAccessRequest(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			return rmChargingServerEntry.getRmChargingAccessRequest();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrRmChargingAccessRequest(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			rmChargingServerEntry.incrRmChargingAccessRequest();
		}
	}

	public static long getRmChargingAccessAccept(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			return rmChargingServerEntry.getRmChargingAccessAccept();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrRmChargingAccessAccept(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			rmChargingServerEntry.incrRmChargingAccessAccept();
		}
	}

	public static long getRmChargingAccessReject(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			return rmChargingServerEntry.getRmChargingAccessReject();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrRmChargingAccessReject(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			rmChargingServerEntry.incrRmChargingAccessReject();
		}
	}

	public static long getRmChargingAcctRequest(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			return rmChargingServerEntry.getRmChargingAcctRequest();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrRmChargingAcctRequest(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			rmChargingServerEntry.incrRmChargingAcctRequest();
		}
	}

	public static long getRmChargingAcctStartRequest(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			return rmChargingServerEntry.getRmChargingAcctStartRequest();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrRmChargingAcctStartRequest(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			rmChargingServerEntry.incrRmChargingAcctStartRequest();
		}
	}

	public static long getRmChargingAcctUpdateRequest(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			return rmChargingServerEntry.getRmChargingAcctUpdateRequest();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrRmChargingAcctUpdateRequest(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			rmChargingServerEntry.incrRmChargingAcctUpdateRequest();
		}
	}

	public static long getRmChargingAcctStopRequest(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			return rmChargingServerEntry.getRmChargingAcctStopRequest();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrRmChargingAcctStopRequest(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			rmChargingServerEntry.incrRmChargingAcctStopRequest();
		}
	}

	public static long getRmChargingAcctResponse(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			return rmChargingServerEntry.getRmChargingAcctResponse();
		}
		return DEFAULT_CNT_VALUE;
	}

	public static void incrRmChargingAcctResponse(String serverName){
		RMChargingServerEntry rmChargingServerEntry = chargingServerNameToCounterMap.get(serverName);
		if(rmChargingServerEntry != null){
			rmChargingServerEntry.incrRmChargingAcctResponse();
		}
	}
	
	public static String getSummary(String esiName) {
		if(chargingServerNameToCounterMap != null && chargingServerNameToCounterMap.get(esiName)!=null){
			return chargingServerNameToCounterMap.get(esiName).toString();
		}else {
			return "ESI Not Found";
		}
	}
	
	public static Map<String, String> getChargingServerNameToAddressMap() {
		return chargingServerNameToAddressMap;
	}
	
	private class RMChargingServerEntry{
		
		private String rmChargingServerName;
		private String rmChargingServerAddress;
		
		private AtomicLong rmChargingRequest = new AtomicLong(0);
		private AtomicLong rmChargingResponses = new AtomicLong(0);
		private AtomicLong rmChargingRequestDropped = new AtomicLong(0);
		private AtomicLong rmChargingUnknownRequestType = new AtomicLong(0);
		private AtomicLong rmChargingRequestTimeout = new AtomicLong(0);
		private AtomicLong rmChargingRequestRetransmission = new AtomicLong(0);
		private AtomicLong rmChargingAccessRequest = new AtomicLong(0);
		private AtomicLong rmChargingAccessAccept = new AtomicLong(0);
		private AtomicLong rmChargingAccessReject = new AtomicLong(0);
		private AtomicLong rmChargingAcctRequest = new AtomicLong(0);
		private AtomicLong rmChargingAcctStartRequest = new AtomicLong(0);
		private AtomicLong rmChargingAcctUpdateRequest = new AtomicLong(0);
		private AtomicLong rmChargingAcctStopRequest = new AtomicLong(0);
		private AtomicLong rmChargingAcctResponse = new AtomicLong(0);
		
		public RMChargingServerEntry(String rmChargingServerName,String rmChargingServerAddress) {
			this.rmChargingServerName = rmChargingServerName;
			this.rmChargingServerAddress = rmChargingServerAddress;
		}

		public long getRmChargingRequest() {
			return rmChargingRequest.get();
		}

		public void incrRmChargingRequest() {
			this.rmChargingRequest.incrementAndGet();
		}

		public long getRmChargingResponses() {
			return rmChargingResponses.get();
		}

		public void incrRmChargingResponses() {
			this.rmChargingResponses.incrementAndGet();
		}

		public long getRmChargingRequestDropped() {
			return rmChargingRequestDropped.get();
		}

		public void incrRmChargingRequestDropped() {
			this.rmChargingRequestDropped.incrementAndGet();
		}

		public long getRmChargingUnknownRequestType() {
			return rmChargingUnknownRequestType.get();
		}

		public void incrRmChargingUnknownRequestType() {
			this.rmChargingUnknownRequestType.incrementAndGet();
		}

		public long getRmChargingRequestTimeout() {
			return rmChargingRequestTimeout.get();
		}

		public void incrRmChargingRequestTimeout() {
			this.rmChargingRequestTimeout.incrementAndGet();
		}

		public long getRmChargingRequestRetransmission() {
			return rmChargingRequestRetransmission.get();
		}
		
		public void incrRmChargingRequestRetransmission() {
			rmChargingRequestRetransmission.incrementAndGet();
		}
		
		public long getRmChargingAccessRequest() {
			return rmChargingAccessRequest.get();
		}

		public void incrRmChargingAccessRequest() {
			this.rmChargingAccessRequest.incrementAndGet();
		}

		public long getRmChargingAccessAccept() {
			return rmChargingAccessAccept.get();
		}

		public void incrRmChargingAccessAccept() {
			this.rmChargingAccessAccept.incrementAndGet();
		}

		public long getRmChargingAccessReject() {
			return rmChargingAccessReject.get();
		}

		public void incrRmChargingAccessReject() {
			this.rmChargingAccessReject.incrementAndGet();
		}

		public long getRmChargingAcctRequest() {
			return rmChargingAcctRequest.get();
		}

		public void incrRmChargingAcctRequest() {
			this.rmChargingAcctRequest.incrementAndGet();
		}

		public long getRmChargingAcctStartRequest() {
			return rmChargingAcctStartRequest.get();
		}

		public void incrRmChargingAcctStartRequest() {
			this.rmChargingAcctStartRequest.incrementAndGet();
		}

		public long getRmChargingAcctUpdateRequest() {
			return rmChargingAcctUpdateRequest.get();
		}

		public void incrRmChargingAcctUpdateRequest() {
			this.rmChargingAcctUpdateRequest.incrementAndGet();
		}

		public long getRmChargingAcctStopRequest() {
			return rmChargingAcctStopRequest.get();
		}

		public void incrRmChargingAcctStopRequest() {
			this.rmChargingAcctStopRequest.incrementAndGet();
		}

		public long getRmChargingAcctResponse() {
			return rmChargingAcctResponse.get();
		}

		public void incrRmChargingAcctResponse() {
			this.rmChargingAcctResponse.incrementAndGet();
		}
		
		@Override
		public String toString() {
			StringBuilder responseBuilder = new StringBuilder();
			
			responseBuilder.append("\n    Request Summary Of ESI : " + rmChargingServerName);
			responseBuilder.append("\n----------------------------------------------------------------");
			responseBuilder.append("\nrmChargingIPAddress                :"+ this.rmChargingServerAddress);
			responseBuilder.append("\nrmChargingRequest                  :"+ this.rmChargingRequest);
			responseBuilder.append("\nrmChargingResponses                :"+ this.rmChargingResponses);
			responseBuilder.append("\nrmChargingRequestDropped           :"+ this.rmChargingRequestDropped);
			responseBuilder.append("\nrmChargingUnknownRequestType       :"+ this.rmChargingUnknownRequestType);
			responseBuilder.append("\nrmChargingAccessRequest            :"+ this.rmChargingAccessRequest);
			responseBuilder.append("\nrmChargingAccessAccept             :"+ this.rmChargingAccessAccept);
			responseBuilder.append("\nrmChargingAccessReject             :"+ this.rmChargingAccessReject);
			responseBuilder.append("\nrmChargingAcctRequest              :"+ this.rmChargingAcctRequest);
			responseBuilder.append("\nrmChargingAcctStartRequest         :"+ this.rmChargingAcctStartRequest);
			responseBuilder.append("\nrmChargingAcctUpdateRequest        :"+ this.rmChargingAcctUpdateRequest);
			responseBuilder.append("\nrmChargingAcctStopRequest          :"+ this.rmChargingAcctStopRequest);
			responseBuilder.append("\nrmChargingAcctResponse             :"+ this.rmChargingAcctStopRequest);
			responseBuilder.append("\n------------------------------------------");
			return responseBuilder.toString();
		}
	}
}
