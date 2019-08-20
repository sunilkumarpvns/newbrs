package com.elitecore.aaa.rm.service.ippool;

/**
 * class defines the Counter for 
 * IPPoolService.
 * @author elitecore
 */

import java.util.concurrent.atomic.AtomicLong;

public class RMIPPoolServiceMIBCounters {
	
	public AtomicLong rmIPPoolServUpTime = new AtomicLong(0);
	public AtomicLong rmIPPoolServResetTime = new AtomicLong(0);	
	public AtomicLong rmIPPoolServTotalRequests = new AtomicLong(0);
	public AtomicLong rmIPPoolServTotalInvalidRequests = new AtomicLong(0);
	public AtomicLong rmIPPoolServTotalDupRequests = new AtomicLong(0);
	public AtomicLong rmIPPoolServTotalResponses = new AtomicLong(0);
	public AtomicLong rmIPPoolServTotalMalformedRequests = new AtomicLong(0);
	public AtomicLong rmIPPoolServTotalBadAuthenticators = new AtomicLong(0);
	public AtomicLong rmIPPoolServTotalPacketsDropped = new AtomicLong(0);
	public AtomicLong rmIPPoolServTotalNoRecords = new AtomicLong(0);
	public AtomicLong rmIPPoolServTotalUnknownTypes = new AtomicLong(0);
	
	public AtomicLong ipAddressDiscoverTotalRequest = new AtomicLong(0);
	public AtomicLong ipAddressOfferTotalResponse = new AtomicLong(0);
	public AtomicLong ipAddressDeclineTotalResponse = new AtomicLong(0);
	public AtomicLong ipAddressTotalAllocationRequest = new AtomicLong(0);
	public AtomicLong ipAddressTotalReleaseRequest = new AtomicLong(0);
	public AtomicLong ipAddressTotalUpdateRequest = new AtomicLong(0);
	
	public void resetCounter(){
		rmIPPoolServTotalRequests.set(0);
		rmIPPoolServTotalInvalidRequests.set(0); 
		rmIPPoolServTotalDupRequests.set(0);
		rmIPPoolServTotalResponses.set(0);
		rmIPPoolServTotalMalformedRequests.set(0);
		rmIPPoolServTotalBadAuthenticators.set(0);
		rmIPPoolServTotalPacketsDropped.set(0);
		rmIPPoolServTotalNoRecords.set(0);
		rmIPPoolServTotalUnknownTypes.set(0);
		ipAddressDiscoverTotalRequest.set(0);
		ipAddressOfferTotalResponse.set(0);
		ipAddressDeclineTotalResponse.set(0);
		ipAddressTotalAllocationRequest.set(0);
		ipAddressTotalReleaseRequest.set(0);
		ipAddressTotalUpdateRequest.set(0);
	}
	
	public class RMIPPoolClientEntry {       
		private int rmIpPoolClientIndex;                   
        private String rmIpPoolClientAddress;                 
        private String rmIpPoolClientID;                      
        
        private AtomicLong rmIpPoolServPacketsDropped = new AtomicLong(0);            
        private AtomicLong rmIpPoolServRequests = new AtomicLong(0);         
        private AtomicLong rmIpPoolServDupRequests = new AtomicLong(0);
        private AtomicLong rmIpPoolServResponses = new AtomicLong(0);             
        private AtomicLong rmIpPoolServBadAuthenticators = new AtomicLong(0);          
        private AtomicLong rmIpPoolServMalformedRequests = new AtomicLong(0);   
        private AtomicLong rmIpPoolServNoRecords = new AtomicLong(0);         
        private AtomicLong rmIpPoolServUnknownTypes = new AtomicLong(0);
        
        private AtomicLong rmIPPoolServInvalidRequests = new AtomicLong(0);
    	private AtomicLong ipAddressDiscoverRequest = new AtomicLong(0);
    	private AtomicLong ipAddressOfferResponse = new AtomicLong(0);
    	private AtomicLong ipAddressDeclineResponse = new AtomicLong(0);
    	private AtomicLong ipAddressAllocationRequest = new AtomicLong(0);
    	private AtomicLong ipAddressReleaseRequest = new AtomicLong(0);
    	private AtomicLong ipAddressUpdateRequest = new AtomicLong(0);
        
		public RMIPPoolClientEntry(int rmIpPoolClientIndex, String rmIpPoolClientAddress, String rmIpPoolClientID){
			this.rmIpPoolClientIndex = rmIpPoolClientIndex;
			this.rmIpPoolClientAddress = rmIpPoolClientAddress;
			this.rmIpPoolClientID = rmIpPoolClientID;
		}
    
		public String getRMIPPoolClientAddress() {
			return rmIpPoolClientAddress;
		}
		public String getRMIPPoolClientID() {
			return rmIpPoolClientID;
		}
		public int getRMIPPoolClientIndex() {
			return rmIpPoolClientIndex;
		}
		public long getRMIPPoolServPacketsDropped() {
			return rmIpPoolServPacketsDropped.get();
		}
		public void incrementRMIPPoolServPacketsDropped() {
			rmIpPoolServPacketsDropped.incrementAndGet();
		}
		public long getRMIPPoolServRequests() {
			return rmIpPoolServRequests.get();
		}
		public void incrementRMIPPoolServRequests() {
			rmIpPoolServRequests.incrementAndGet();
		}
		public long getRMIPPoolServDupRequests() {
			return rmIpPoolServDupRequests.get();
		}
		public void incrementRMIPPoolServDupRequests() {
			rmIpPoolServDupRequests.incrementAndGet();
		}
		public long getRMIPPoolServResponses() {
			return rmIpPoolServResponses.get();
		}
		public void incrementRMIPPoolServResponses() {
			rmIpPoolServResponses.incrementAndGet();
		}
		public long getRMIPPoolServBadAuthenticators() {
			return rmIpPoolServBadAuthenticators.get();
		}
		public void incrementRMIPPoolServBadAuthenticators() {
			rmIpPoolServBadAuthenticators.incrementAndGet();
		}
		public long getRMIPPoolServMalformedRequests() {
			return rmIpPoolServMalformedRequests.get();
		}
		public void incrementRMIPPoolServMalformedRequests() {
			rmIpPoolServMalformedRequests.incrementAndGet();
		}
		public long getRMIPPoolServNoRecords() {
			return rmIpPoolServNoRecords.get();
		}
		public void incrementRMIPPoolServNoRecords() {
			rmIpPoolServNoRecords.incrementAndGet();
		}
		public long getRMIPPoolServUnknownTypes() {
			return rmIpPoolServUnknownTypes.get();
		}
		public void incrementRMIPPoolServUnknownTypes() {
			rmIpPoolServUnknownTypes.incrementAndGet();
		}
		
		public void incrementRMIPPoolServInvalidTypes(){
			rmIPPoolServInvalidRequests.incrementAndGet();
		}
		public long getRMIPPoolServInvalidTypes(){
			return rmIPPoolServInvalidRequests.get();
		}
		
		public void incrementIPAddressDiscoverRequest(){
			ipAddressDiscoverRequest.incrementAndGet();
		}
		public long getIPAddressDiscoverRequest(){
			return ipAddressDiscoverRequest.get();
		}
		
		public void incrementIPAddressOfferResponse(){
			ipAddressOfferResponse.incrementAndGet();
		}
		public long getIPAddressOfferResponse(){
			return ipAddressOfferResponse.get();
		}
		
		public void incrementIPAddressDeclineResponse(){
			ipAddressDeclineResponse.incrementAndGet();
		}
		public long getIPAddressDeclineResponse(){
			return ipAddressDeclineResponse.get();
		}
		
		public void incrementIPAddressAllocationRequest(){
			ipAddressAllocationRequest.incrementAndGet();
		}
		public long getIPAddressAllocationRequest(){
			return ipAddressAllocationRequest.get();
		}
		
		public void incrementIPAddressReleaseRequest(){
			ipAddressReleaseRequest.incrementAndGet();
		}
		public long getIPAddressReleaseRequest(){
			return ipAddressReleaseRequest.get();
		}
		
		public void incrementIPAddressUpdateRequest(){
			ipAddressUpdateRequest.incrementAndGet();
		}
		public long getIPAddressUpdateRequest(){
			return ipAddressUpdateRequest.get();
		}
		
		public void resetCounter(){
			rmIpPoolServRequests.set(0);
			rmIpPoolServResponses.set(0);
			rmIPPoolServInvalidRequests.set(0);
			rmIpPoolServUnknownTypes.set(0);
			rmIpPoolServMalformedRequests.set(0);
			rmIpPoolServBadAuthenticators.set(0);
			rmIpPoolServNoRecords.set(0);
			rmIpPoolServPacketsDropped.set(0);
			rmIpPoolServDupRequests.set(0);
			ipAddressDiscoverRequest.set(0);
			ipAddressOfferResponse.set(0); 
			ipAddressDeclineResponse.set(0);
			ipAddressAllocationRequest.set(0); 
			ipAddressReleaseRequest.set(0);
			ipAddressUpdateRequest.set(0); 
		}
 	}
}