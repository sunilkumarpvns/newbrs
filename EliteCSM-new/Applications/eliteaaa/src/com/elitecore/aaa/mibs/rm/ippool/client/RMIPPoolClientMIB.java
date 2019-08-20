package com.elitecore.aaa.mibs.rm.ippool.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.conf.RadESConfiguration.RadESTypeConstants;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;

public class RMIPPoolClientMIB {
	
	private static AtomicLong ipAddressTotalRequest;
	private static AtomicLong ipAddressTotalResponse;
	private static AtomicLong ipAddressRequestTimeout;
	private static AtomicLong ipAddressRequestRetransmission;
	private static AtomicLong ipAddressDiscoverRequest;
	private static AtomicLong ipAddressOfferResponse;
	private static AtomicLong ipAddressDeclineResponse;
	private static AtomicLong ipAddressAllocationRequest;
	private static AtomicLong ipaddressAllocationResponse;
	private static AtomicLong ipAddressReleaseRequest; 
	private static AtomicLong ipAddressUpdateRequest; 
	
	private AAAServerContext serverContext;
	private static Map<String, String> ipPoolServerNameToAddressMap; 
	private static Map<String, RMIPPoolServerEntry> ipPoolServerNameToCountersMap;

	private static boolean isInitialized;
	
	public RMIPPoolClientMIB(AAAServerContext serverContext){
		this.serverContext = serverContext;
		ipAddressTotalRequest = new AtomicLong(0);
		ipAddressTotalResponse = new AtomicLong(0);
		ipAddressRequestTimeout = new AtomicLong(0);
		ipAddressRequestRetransmission = new AtomicLong(0);
		ipAddressDiscoverRequest = new AtomicLong(0);
		ipAddressOfferResponse = new AtomicLong(0);
		ipAddressDeclineResponse = new AtomicLong(0);
		ipAddressAllocationRequest = new AtomicLong(0);
		ipaddressAllocationResponse = new AtomicLong(0);
		ipAddressReleaseRequest = new AtomicLong(0);
		ipAddressUpdateRequest = new AtomicLong(0);
		
		ipPoolServerNameToAddressMap = new HashMap<String, String>();
		ipPoolServerNameToCountersMap = new HashMap<String, RMIPPoolClientMIB.RMIPPoolServerEntry>();
	}
	
	/**
	 * get the ippoolserver Esi from configuration 
	 * and make the entry for each ip-pool Esi into
	 * hashmap
	 */
	
	public void init(){
		if(!isInitialized) {
			List<DefaultExternalSystemData> ipPoolESIList = serverContext.getServerConfiguration().getRadESConfiguration().getESListByType(RadESTypeConstants.IP_POOL_SERVER.type);
			
			if(ipPoolESIList != null && !(ipPoolESIList.isEmpty())){
			
				for (DefaultExternalSystemData ipPoolServer : ipPoolESIList) {
					String ipPoolServerName = ipPoolServer.getName();
					String ipPoolServerAddress = ipPoolServer.getIPAddress().getHostAddress();
					if(ipPoolServerAddress != null && !(ipPoolServerAddress.isEmpty())){
						ipPoolServerNameToAddressMap.put(ipPoolServerName, ipPoolServerAddress);
						ipPoolServerNameToCountersMap.put(ipPoolServerName, new RMIPPoolServerEntry(ipPoolServerName,ipPoolServerAddress));
					}
				}
			}
			isInitialized = true;
		}
	}
	
	private class RMIPPoolServerEntry{
		
		private String ipPoolServerAddress;
		private String ipPoolServerName;
		private AtomicLong ipAddressTotalResponse;
		private AtomicLong ipAddressTotalRequest;
		private AtomicLong ipAddressRequestTimeout;
		private AtomicLong ipAddressRequestRetransmission;
		private AtomicLong ipAddressDiscoverRequest;
		private AtomicLong ipAddressOfferResponse;
		private AtomicLong ipAddressDeclineResponse;
		private AtomicLong ipAddressAllocationRequest;
		private AtomicLong ipaddressAllocationResponse;
		private AtomicLong ipAddressReleaseRequest;
		private AtomicLong ipAddressUpdateRequest; 

		public RMIPPoolServerEntry(String ipPoolServerName,String ipPoolServerAddress){
			
			this.ipPoolServerName = ipPoolServerName;
			this.ipPoolServerAddress = ipPoolServerAddress;
			ipAddressTotalResponse = new AtomicLong(0);
			ipAddressTotalRequest  = new AtomicLong(0); 
			ipAddressRequestTimeout = new AtomicLong(0);
			ipAddressRequestRetransmission = new AtomicLong(0);
			ipAddressDiscoverRequest = new AtomicLong(0);
			ipAddressOfferResponse = new AtomicLong(0);
			ipAddressDeclineResponse = new AtomicLong(0);
			ipAddressAllocationRequest = new AtomicLong(0);
			ipaddressAllocationResponse = new AtomicLong(0);
			ipAddressReleaseRequest = new AtomicLong(0);
			ipAddressUpdateRequest = new AtomicLong(0); 
		}

		public long getIpAddressTotalRequest() {
			return ipAddressTotalRequest.get();
		}
		
		public void incrIpAddressTotalRequest(){
			ipAddressTotalRequest.incrementAndGet();
		}
		
		public long getIpAddressTotalResponse(){
			return ipAddressTotalResponse.get();
		}
		
		public void incrIpAddressTotalResponse(){
			ipAddressTotalResponse.incrementAndGet();
		}
		
		public long getIpAddressRequestTimeout(){
			return ipAddressRequestTimeout.get(); 
		}
		
		public void incrIpAddressRequestTimeout(){
			ipAddressRequestTimeout.incrementAndGet();
		}
		
		public long getIpAddressRequestRetransmission(){
			return ipAddressRequestRetransmission.get();
		}
		
		public void incrIpAddressRequestRetransmission(){
			ipAddressRequestRetransmission.incrementAndGet();
		}
		
		public long getIpAddressDiscoverRequest(){
			return ipAddressDiscoverRequest.get();
		}
		
		public void incrIpAddressDiscoverRequest(){
			ipAddressDiscoverRequest.incrementAndGet();
		}
		
		public long getIpAddressOfferResponse(){
			return ipAddressOfferResponse.get();
		}
		
		public void incrIpAddressOfferResponse(){
			ipAddressOfferResponse.incrementAndGet();
		}
		
		public long getIpAddressDeclineResponse(){
			return ipAddressDeclineResponse.get();
		}
		
		public void incrIpAddressDeclineResponse(){
			ipAddressDeclineResponse.incrementAndGet(); 
		}
		
		public long getIpAddressAllocationRequest(){
			return ipAddressAllocationRequest.get();
		}
		
		public void incrIpAddressAllocationRequest(){
			ipAddressAllocationRequest.incrementAndGet();
		}
		
		public long getIpaddressAllocationResponse(){
			return ipaddressAllocationResponse.get();
		}
		
		public void incrIpaddressAllocationResponse(){
			ipaddressAllocationResponse.incrementAndGet();
		}
		
		public long getIpAddressReleaseRequest(){
			return ipAddressReleaseRequest.get();
		}
		
		public void incrIpAddressReleaseRequest(){
			ipAddressReleaseRequest.incrementAndGet();
		}
		
		public long getIpAddressUpdateRequest(){
			return ipAddressUpdateRequest.get();
		}
		
		public void incrIpAddressUpdateRequest(){
			ipAddressUpdateRequest.incrementAndGet();
		}

		@Override
		public String toString() {
			StringBuilder responseBuilder = new StringBuilder();
			
			
			responseBuilder.append("\n    Request Summary Of ESI : " + ipPoolServerName);
			responseBuilder.append("\n----------------------------------------------------------------");
			responseBuilder.append("\nIP Address                      :" + this.ipPoolServerAddress);
			responseBuilder.append("\nTotal Requests                  :" + this.getIpAddressTotalRequest());
			responseBuilder.append("\nTotal Response                  :" + this.getIpAddressTotalResponse());
			responseBuilder.append("\nRequest Timeout                 :" + this.getIpAddressRequestTimeout());
			responseBuilder.append("\nRequest Retransmission          :" + this.getIpAddressRequestRetransmission());
			responseBuilder.append("\nDiscover Request                :" + this.getIpAddressDiscoverRequest());
			responseBuilder.append("\nOffer Responses                 :" + this.getIpAddressOfferResponse());
			responseBuilder.append("\nDecline Responses               :" + this.getIpAddressDeclineResponse());
			responseBuilder.append("\nAllocation Requests             :" + this.getIpAddressAllocationRequest());
			responseBuilder.append("\nAllocation Response             :" + this.getIpaddressAllocationResponse());
			responseBuilder.append("\nUpdate Request                  :" + this.getIpAddressUpdateRequest());
			responseBuilder.append("\nRelease Request                 :" + this.getIpAddressReleaseRequest());
			responseBuilder.append("\n----------------------------------------------------------------");
			return responseBuilder.toString();
		}
	}
	
	// Total Request Counter
	
	public static long getIpAddressTotalRequest() {
		return ipAddressTotalRequest.get();
	}
	
	public static long getIpAddressTotalRequest(String serverAddress){
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null ){
			return serverEntry.getIpAddressTotalRequest();
		}
		return 0;
	}
	
	public static void incrIpAddressTotalRequest(String serverAddress){
		ipAddressTotalRequest.incrementAndGet();
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null )
			serverEntry.incrIpAddressTotalRequest();
	}
	
	//Total Response Counter
	
	public static long getIpAddressTotalResponse(){
		return ipAddressTotalResponse.get();
	}
	
	public static long getIpAddressTotalResponse(String serverAddress){
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null ){
			return serverEntry.getIpAddressTotalResponse();
		}
		return 0;
	}
	
	public static void incrIpAddressTotalResponse(String serverAddress){
		ipAddressTotalResponse.incrementAndGet();
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null )
			serverEntry.incrIpAddressTotalResponse();
	}

	// Request Time out Counter
	
	public static long getIpAddressRequestTimeout(){
		return ipAddressRequestTimeout.get(); 
	}
	
	public static long getIpAddressRequestTimeout(String serverAddress){
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null ){
			return serverEntry.getIpAddressRequestTimeout();
		}
		return 0; 
	}
	
	public static void incrIpAddressRequestTimeout(String serverAddress){
		ipAddressRequestTimeout.incrementAndGet();
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null )
			serverEntry.incrIpAddressRequestTimeout();
	}
	
	
	// Request Retransmission Counter
	
	public static long getIpAddressRequestRetransmission(){
		return ipAddressRequestRetransmission.get();
	}
	
	public static long getIpAddressRequestRetransmission(String serverAddress){
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null ){
			return serverEntry.getIpAddressRequestRetransmission();
		}
		return 0;
	}
	
	public static void incrIpAddressRequestRetransmission(String serverAddress){
		ipAddressRequestRetransmission.incrementAndGet();
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null )
			serverEntry.incrIpAddressRequestRetransmission();
	}
	
	// Request Discover Counter
	
	public static long getIpAddressDiscoverRequest(){
		return ipAddressDiscoverRequest.get();
	}
	
	public static long getIpAddressDiscoverRequest(String serverAddress){
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null ){
			return serverEntry.getIpAddressDiscoverRequest();
		}
		return 0;
	}
	
	public static void incrIpAddressDiscoverRequest(String serverAddress){
		ipAddressDiscoverRequest.incrementAndGet();
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null )
			serverEntry.incrIpAddressDiscoverRequest();
	}
	
	// Offer Response Counter
	
	public static long getIpAddressOfferResponse(){
		return ipAddressOfferResponse.get();
	}
	
	public static long getIpAddressOfferResponse(String serverAddress){
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null ){
			return serverEntry.getIpAddressOfferResponse();
		}
		return 0;
	}
	
	public static void incrIpAddressOfferResponse(String serverAddress){
		ipAddressOfferResponse.incrementAndGet();
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null )
			serverEntry.incrIpAddressOfferResponse();
	}
	
	// Decline Response Counter
	
	public static long getIpAddressDeclineResponse(){
		return ipAddressDeclineResponse.get();
	}
	
	public static long getIpAddressDeclineResponse(String serverAddress){
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null ){
			return serverEntry.getIpAddressDeclineResponse();
		}
		return 0;
	}
	
	public static void incrIpAddressDeclineResponse(String serverAddress){
		ipAddressDeclineResponse.incrementAndGet();
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null )
			serverEntry.incrIpAddressDeclineResponse();
	}
	
	// Allocation Request Counter
	
	public static long getIpAddressAllocationRequest(){
		return ipAddressAllocationRequest.get();
	}
	
	public static long getIpAddressAllocationRequest(String serverAddress){
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null ){
			return serverEntry.getIpAddressAllocationRequest();
		}
		return 0;
	}
	
	public static void incrIpAddressAllocationRequest(String serverAddress){
		ipAddressAllocationRequest.incrementAndGet();
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null )
			serverEntry.incrIpAddressAllocationRequest();
	}
	
	// Allocation Response Counter
	
	public static long getIpaddressAllocationResponse(){
		return ipaddressAllocationResponse.get();
	}
	
	public static long getIpaddressAllocationResponse(String serverAddress){
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null ){
			return serverEntry.getIpaddressAllocationResponse();
		}
		return 0;
	}
	
	public static void incrIpaddressAllocationResponse(String serverAddress){
		ipaddressAllocationResponse.incrementAndGet();
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null )
			serverEntry.incrIpaddressAllocationResponse();
	}
	
	// Release Request Counter
	
	public static long getIpAddressReleaseRequest(){
		return ipAddressReleaseRequest.get();
	}
	
	public static long getIpAddressReleaseRequest(String serverAddress){
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null ){
			return serverEntry.getIpAddressReleaseRequest();
		}
		return 0;
	}
	
	public static void incrIpAddressReleaseRequest(String serverAddress){
		ipAddressReleaseRequest.incrementAndGet();
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null )
			serverEntry.incrIpAddressReleaseRequest();
	}
	
	// Update Request Counter
	
	public static long getIpAddressUpdateRequest(){
		return ipAddressUpdateRequest.get();
	}
	
	public static long getIpAddressUpdateRequest(String serverAddress){
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null ){
			return serverEntry.getIpAddressUpdateRequest();
		}
		return 0;
	}

	public static void incrIpAddressUpdateRequest(String serverAddress){
		ipAddressUpdateRequest.incrementAndGet();
		RMIPPoolServerEntry serverEntry = ipPoolServerNameToCountersMap.get(serverAddress);
		if(serverEntry != null )
			serverEntry.incrIpAddressUpdateRequest();
	}

	public static String getSummary(String esiName) {
		if(ipPoolServerNameToCountersMap!=null && ipPoolServerNameToCountersMap.get(esiName)!=null){
			return ipPoolServerNameToCountersMap.get(esiName).toString();
		}else {
			return "ESI Not Found";
		}
	}

	public static Map<String, String> getIpPoolServerNameToAddressMap() {
		return ipPoolServerNameToAddressMap;
	}
}