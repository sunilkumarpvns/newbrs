package com.elitecore.aaa.rm.service.chargingservice;

import java.util.concurrent.atomic.AtomicLong;


public class RMChargingServiceMIBCounters {
	
	public AtomicLong rmChargingServUpTime = new AtomicLong(0);
	public AtomicLong rmChargingServResetTime = new AtomicLong(0);	
	public AtomicLong rmChargingServTotalRequests = new AtomicLong(0);
	public AtomicLong rmChargingServTotalResponses = new AtomicLong(0);
	public AtomicLong rmChargingServTotalInvalidRequests = new AtomicLong(0);
	public AtomicLong rmChargingServTotalDupRequests = new AtomicLong(0);
	public AtomicLong rmChargingServTotalMalformedRequests = new AtomicLong(0);
	public AtomicLong rmChargingServTotalBadAuthenticators = new AtomicLong(0);
	public AtomicLong rmChargingServTotalPacketsDropped = new AtomicLong(0);
	public AtomicLong rmChargingServTotalNoRecords = new AtomicLong(0);
	public AtomicLong rmChargingServTotalUnknownTypes = new AtomicLong(0);
	
	public AtomicLong rmChargingServTotalAccessRequest = new AtomicLong(0);
	public AtomicLong rmChargingServTotalAccessAccept = new AtomicLong(0);
	public AtomicLong rmChargingServTotalAccessRejects = new AtomicLong(0);
	public AtomicLong rmChargingServTotalAcctRequest = new AtomicLong(0);
	public AtomicLong rmChargingServTotalAcctResponse = new AtomicLong(0);
	public AtomicLong rmChargingServTotalAcctStartRequest = new AtomicLong(0);
	public AtomicLong rmChargingServTotalAcctStopRequest = new AtomicLong(0);
	public AtomicLong rmChargingServTotalAcctUpdateRequest = new AtomicLong(0);
	
	public void resetCounter(){
		rmChargingServTotalRequests.set(0);
		rmChargingServTotalInvalidRequests.set(0);
		rmChargingServTotalDupRequests.set(0);
		rmChargingServTotalResponses.set(0);
		rmChargingServTotalMalformedRequests.set(0);
		rmChargingServTotalAccessRejects.set(0);
		rmChargingServTotalBadAuthenticators.set(0);
		rmChargingServTotalPacketsDropped.set(0); 
		rmChargingServTotalNoRecords.set(0);
		rmChargingServTotalUnknownTypes.set(0);
		
		rmChargingServTotalAccessRequest.set(0);
		rmChargingServTotalAccessAccept.set(0);
		rmChargingServTotalAcctRequest.set(0); 
		rmChargingServTotalAcctResponse.set(0);
		rmChargingServTotalAcctStartRequest.set(0);  
		rmChargingServTotalAcctUpdateRequest.set(0);  
		rmChargingServTotalAcctStopRequest.set(0);
	}
	
	public static class RMChargingClientEntry {        
		private int rmChargingClientIndex;                   
        private String rmChargingClientAddress;                 
        private String rmChargingClientID;                      
        private AtomicLong rmChargingServPacketsDropped = new AtomicLong(0);            
        private AtomicLong rmChargingServRequests = new AtomicLong(0);         
        private AtomicLong rmChargingServDupRequests = new AtomicLong(0);             
        private AtomicLong rmChargingServResponses = new AtomicLong(0);             
        private AtomicLong rmChargingServBadAuthenticators = new AtomicLong(0);          
        private AtomicLong rmChargingServMalformedRequests = new AtomicLong(0);   
        private AtomicLong rmChargingServNoRecords = new AtomicLong(0);         
        private AtomicLong rmChargingServUnknownTypes = new AtomicLong(0);            
        
        private AtomicLong rmChargingServAccessRequest = new AtomicLong(0);
    	private AtomicLong rmChargingServAccessAccept = new AtomicLong(0);
    	private AtomicLong rmChargingServAccessReject = new AtomicLong(0);
    	private AtomicLong rmChargingServAcctRequest = new AtomicLong(0);
    	private AtomicLong rmChargingServAcctResponse = new AtomicLong(0);
    	private AtomicLong rmChargingServAcctStartRequest = new AtomicLong(0);
    	private AtomicLong rmChargingServAcctStopRequest = new AtomicLong(0);
    	private AtomicLong rmChargingServAcctUpdateRequest = new AtomicLong(0);
    	
		public RMChargingClientEntry(int rmChargingClientIndex, String rmChargingClientAddress, String rmChargingClientID){
			this.rmChargingClientIndex = rmChargingClientIndex;
			this.rmChargingClientAddress = rmChargingClientAddress;
			this.rmChargingClientID = rmChargingClientID;
		}
		
        public String getRMChargingClientAddress() {
			return rmChargingClientAddress;
		}
		
        public String getRMChargingClientID() {
			return rmChargingClientID;
		}
		
        public int getRMChargingClientIndex() {
			return rmChargingClientIndex;
		}
		
        public long getRMChargingServPacketsDropped() {
			return rmChargingServPacketsDropped.get();
		}
		
        public void incrementRMChargingServPacketsDropped() {
			this.rmChargingServPacketsDropped.incrementAndGet();
		}
		
        public long getRMChargingServRequests() {
			return rmChargingServRequests.get();
		}
		
        public void incrementRMChargingServRequests() {
			this.rmChargingServRequests.incrementAndGet();
		}
		
        public long getRMChargingServDupRequests() {
			return rmChargingServDupRequests.get();
		}
		
        public void incrementRMChargingServDupRequests() {
			this.rmChargingServDupRequests.incrementAndGet();
		}
		
        public long getRMChargingServResponses() {
			return rmChargingServResponses.get();
		}
		
        public void incrementRMChargingServResponses() {
			this.rmChargingServResponses.incrementAndGet();
		}
		
        public long getRMChargingServBadAuthenticators() {
			return rmChargingServBadAuthenticators.get();
		}
		
        public void incrementRMChargingServBadAuthenticators() {
			this.rmChargingServBadAuthenticators.incrementAndGet();
		}
		
        public long getRMChargingServMalformedRequests() {
			return rmChargingServMalformedRequests.get();
		}
		
        public void incrementRMChargingServMalformedRequests() {
			this.rmChargingServMalformedRequests.incrementAndGet();
		}
		
        public long getRMChargingServNoRecords() {
			return rmChargingServNoRecords.get();
		}
		
        public void incrementRMChargingServNoRecords() {
			this.rmChargingServNoRecords.incrementAndGet();
		}
		
        public long getRMChargingServUnknownTypes() {
			return rmChargingServUnknownTypes.get();
		}
		
        public void incrementRMChargingServUnknownTypes() {
			this.rmChargingServUnknownTypes.incrementAndGet();
		}
		
        public long getRmChargingServAccessRequest() {
			return rmChargingServAccessRequest.get();
		}

		public void incrementRmChargingServAccessRequest() {
			this.rmChargingServAccessRequest.incrementAndGet();
		}

		public long getRmChargingServAccessAccept() {
			return rmChargingServAccessAccept.get();
		}
		
		public void incrementRmChargingServAccessAccept() {
			this.rmChargingServAccessAccept.incrementAndGet();
		}

		public long getRmChargingServAccessReject() {
			return rmChargingServAccessReject.get();
		}
		
		public void incrementRmChargingServAccessReject() {
			this.rmChargingServAccessReject.incrementAndGet();
		}
		
		public long getRmChargingServAcctRequest() {
			return rmChargingServAcctRequest.get();
		}
		
		public void incrementRmChargingServAcctRequest() {
			this.rmChargingServAcctRequest.incrementAndGet();
		}

		public long getRmChargingServAcctResponse() {
			return rmChargingServAcctResponse.get();
		}

		public void incrementRmChargingServAcctResponse() {
			this.rmChargingServAcctResponse.incrementAndGet();
		}

		public long getRmChargingServAcctStartRequest() {
			return rmChargingServAcctStartRequest.get();
		}

		public void incrementRmChargingServAcctStartRequest() {
			this.rmChargingServAcctStartRequest.incrementAndGet();
		}

		public long getRmChargingServAcctStopRequest() {
			return rmChargingServAcctStopRequest.get();
		}

		public void incrementRmChargingServAcctStopRequest() {
			this.rmChargingServAcctStopRequest.incrementAndGet();
		}

		public long getRmChargingServAcctUpdateRequest() {
			return rmChargingServAcctUpdateRequest.get();
		}

		public void incrementRmChargingServAcctUpdateRequest() {
			this.rmChargingServAcctUpdateRequest.incrementAndGet();
		}

		public void resetCounter() {
			rmChargingServPacketsDropped.set(0); 
			rmChargingServRequests.set(0);
			rmChargingServDupRequests.set(0);
			rmChargingServResponses.set(0);
			rmChargingServBadAuthenticators.set(0);
			rmChargingServMalformedRequests.set(0);
			rmChargingServNoRecords.set(0);
			rmChargingServUnknownTypes.set(0);
			
			rmChargingServAccessRequest.set(0);    
			rmChargingServAccessAccept.set(0);  
			rmChargingServAccessReject.set(0);
			rmChargingServAcctRequest.set(0);      
			rmChargingServAcctResponse.set(0);     
			rmChargingServAcctStartRequest.set(0); 
			rmChargingServAcctUpdateRequest.set(0);
			rmChargingServAcctStopRequest.set(0);  
		}
	}
}
