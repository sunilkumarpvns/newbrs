
/**
 * 
 */
package com.elitecore.aaa.rm.service.gtpprime.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.core.conf.RMServerConfiguration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.rm.service.gtpprime.data.GTPPrimeClientDataImpl;

/**
 * @author dhaval.jobanputra
 *
 */
public class GTPPrimeServiceCounters {

	public AtomicLong gtpPrimeTotalRequestReceived;
	public AtomicLong gtpPrimeTotalEchoRequestReceived;
	public AtomicLong gtpPrimeTotalNodeAliveRequestReceived;
	public AtomicLong gtpPrimeTotalRedirectionRequestReceived;
	public AtomicLong gtpPrimeTotalDataRecordTransferRequestReceived;
	public AtomicLong gtpPrimeTotalMalformedRequestPacketReceived;

	public AtomicLong gtpPrimeTotalRedirectionResponseSent;
	public AtomicLong gtpPrimeTotalEchoResponseSent;
	public AtomicLong gtpPrimeTotalNodeAliveResponseSent;
	public AtomicLong gtpPrimeTotalDataRecordTransferResponseSent;
	public AtomicLong gtpPrimeTotalVersionNotSupportedResponseSent;
	public AtomicLong gtpPrimeTotalInvalidClientRequestReceived;
	public Date gtpPrimeServiceUpTime;
	public AtomicLong gtpPrimeTotalDroppedRequest;
	public AtomicLong gtpPrimeTotalEchoRequestDropped;
	public AtomicLong gtpPrimeTotalNodeAliveRequestDropped;
	public AtomicLong gtpPrimeTotalRedirectionRequestDropped;
	public AtomicLong gtpPrimeTotalDataRecordTransferRequestDropped;
	public AtomicLong gtpPrimeTotalDataRecordTransferFailureResponseSent;
	public AtomicLong gtpPrimeTotalRedirectionFailureResponseSent;
	
	public AtomicLong gtpPrimeTotalEchoRequestRetry;
	public AtomicLong gtpPrimeTotalNodeAliveRequestRetry;
	public AtomicLong gtpPrimeTotalEchoRequestSent;
	public AtomicLong gtpPrimeTotalEchoResponseReceived;
	public AtomicLong gtpPrimeTotalNodeAliveRequestSent;
	public AtomicLong gtpPrimeTotalNodeAliveResponseReceived;
	public AtomicLong gtpPrimeTotalMalformedEchoResponseReceived;
	public AtomicLong gtpPrimeTotalMalformedNodeAliveResponseReceived;
	
	public boolean isInitialized;

	private AAAServerContext context;
	public Map <String, GTPPrimeClientCounters> gtpClientCounterMap ;
	public Map <String, String>clientMap ;
	
	

	public GTPPrimeServiceCounters( AAAServerContext context){
		this.context = context;
		gtpPrimeTotalDroppedRequest = new AtomicLong(0);
		gtpPrimeTotalInvalidClientRequestReceived = new AtomicLong(0);
		gtpPrimeServiceUpTime = new Date();
		gtpPrimeTotalRequestReceived = new AtomicLong(0);
		gtpPrimeTotalEchoRequestReceived = new AtomicLong(0);
		gtpPrimeTotalNodeAliveRequestReceived = new AtomicLong(0);
		gtpPrimeTotalRedirectionRequestReceived = new AtomicLong(0);
		gtpPrimeTotalDataRecordTransferRequestReceived = new AtomicLong(0);
		gtpPrimeTotalRedirectionResponseSent = new AtomicLong(0);
		gtpPrimeTotalEchoResponseSent = new AtomicLong(0);
		gtpPrimeTotalNodeAliveResponseSent = new AtomicLong(0);
		gtpPrimeTotalDataRecordTransferResponseSent = new AtomicLong(0);
		gtpPrimeTotalVersionNotSupportedResponseSent = new AtomicLong(0);
		gtpPrimeTotalMalformedRequestPacketReceived = new AtomicLong(0);
		gtpPrimeTotalEchoRequestDropped = new AtomicLong(0);
		gtpPrimeTotalNodeAliveRequestDropped = new AtomicLong(0);
		gtpPrimeTotalRedirectionRequestDropped = new AtomicLong(0);
		gtpPrimeTotalDataRecordTransferRequestDropped = new AtomicLong(0);
		gtpPrimeTotalDataRecordTransferFailureResponseSent = new AtomicLong(0);
		gtpPrimeTotalRedirectionFailureResponseSent = new AtomicLong(0);
		gtpPrimeTotalEchoRequestSent = new AtomicLong(0);
		gtpPrimeTotalEchoResponseReceived = new AtomicLong(0);
		gtpPrimeTotalNodeAliveRequestSent = new AtomicLong(0);
		gtpPrimeTotalNodeAliveResponseReceived = new AtomicLong(0);
		gtpPrimeTotalEchoRequestRetry = new AtomicLong(0);
		gtpPrimeTotalNodeAliveRequestRetry = new AtomicLong(0);
		gtpPrimeTotalMalformedEchoResponseReceived = new AtomicLong(0);
		gtpPrimeTotalMalformedNodeAliveResponseReceived = new AtomicLong(0);
		gtpClientCounterMap = new HashMap<String, GTPPrimeClientCounters>();
		clientMap = new LinkedHashMap<String, String>();
		isInitialized = false;
	}

	public void init(){
		if (!isInitialized){
			gtpPrimeServiceUpTime = new Date();
			List<GTPPrimeClientDataImpl> clientList = new ArrayList<GTPPrimeClientDataImpl>();	
			clientList = ((RMServerConfiguration)context.getServerConfiguration()).getGTPPrimeConfiguration().getClientList();
			int listLength = clientList.size();
			String clientAddr=null;
			for ( int i=0 ; i<listLength ; i++ ){
				clientAddr = clientList.get(i).getClientIP().trim();
				if (clientAddr != null && clientAddr.length() > 0){
					clientMap.put(String.valueOf(i+1), clientAddr);
					gtpClientCounterMap.put(clientAddr, new GTPPrimeClientCounters((i+1), clientAddr ));
				}
			}
			isInitialized = true;
		}
	}

	public void reset(){
		isInitialized = false;
		gtpPrimeTotalEchoRequestSent.set(0);
		gtpPrimeTotalEchoResponseReceived.set(0);
		gtpPrimeTotalNodeAliveRequestSent.set(0);
		gtpPrimeTotalNodeAliveResponseReceived.set(0);
		gtpPrimeTotalEchoRequestDropped.set(0);
		gtpPrimeTotalNodeAliveRequestDropped.set(0);
		gtpPrimeTotalRedirectionRequestDropped.set(0);
		gtpPrimeTotalDataRecordTransferRequestDropped.set(0);
		gtpPrimeTotalDataRecordTransferFailureResponseSent.set(0);
		gtpPrimeTotalRedirectionFailureResponseSent.set(0);
		gtpPrimeTotalMalformedRequestPacketReceived.set(0);
		gtpPrimeTotalDroppedRequest.set(0);
		gtpPrimeTotalEchoRequestReceived.set(0);
		gtpPrimeTotalRequestReceived.set(0);
		gtpPrimeTotalNodeAliveRequestReceived.set(0);
		gtpPrimeTotalRedirectionRequestReceived.set(0);
		gtpPrimeTotalDataRecordTransferRequestReceived.set(0);
		gtpPrimeTotalRedirectionResponseSent.set(0);
		gtpPrimeTotalEchoResponseSent.set(0);
		gtpPrimeTotalNodeAliveResponseSent.set(0);
		gtpPrimeTotalDataRecordTransferResponseSent.set(0);
		gtpPrimeTotalVersionNotSupportedResponseSent.set(0);
		gtpPrimeTotalInvalidClientRequestReceived.set(0);
		gtpPrimeTotalEchoRequestRetry.set(0);
		gtpPrimeTotalNodeAliveRequestRetry.set(0);
		gtpPrimeTotalMalformedEchoResponseReceived.set(0);
		gtpPrimeTotalMalformedNodeAliveResponseReceived.set(0);
		gtpClientCounterMap = new HashMap<String, GTPPrimeClientCounters>();
		clientMap = new LinkedHashMap<String, String>();
	}

	public void reInitialize(){
		List<GTPPrimeClientDataImpl> clientList = new ArrayList<GTPPrimeClientDataImpl>();	
		Map <String, GTPPrimeClientCounters> localGTPClientCounterMap = new HashMap<String, GTPPrimeClientCounters>();
		Map <String, String>localClientMap = new HashMap<String, String>();

		clientList = ((RMServerConfiguration)context.getServerConfiguration()).getGTPPrimeConfiguration().getClientList();
		int listLength = clientList.size();
		String clientAddr=null;
		for ( int i=0 ; i<listLength ; i++ ){
			clientAddr = clientList.get(i).getClientIP().trim();
			if (clientAddr != null && clientAddr.length() > 0){
				localClientMap.put(String.valueOf(i+1), clientAddr);
				GTPPrimeClientCounters gtpClientCounter;
				gtpClientCounter = gtpClientCounterMap.get(clientAddr);
				if (gtpClientCounter == null){
					localGTPClientCounterMap.put(clientAddr, new GTPPrimeClientCounters((i+1), clientAddr ));	
				}
				else{
					localGTPClientCounterMap.put(clientAddr, gtpClientCounter);
				}

			}
		}
		clientMap = localClientMap;
		gtpClientCounterMap = localGTPClientCounterMap;
		isInitialized = true;
	}

	public static class GTPPrimeClientCounters{

		private int gtpClientIndex;
		private String gtpClientAddress;
		private long gtpPrimeEchoRequestReceived;
		private long gtpPrimeNodeAliveRequestReceived;
		private long gtpPrimeRedirectionRequestReceived;
		private long gtpPrimeDataRecordTransferRequestReceived;
		private long gtpPrimeDroppedRequest;
		private long gtpPrimeMalformedRequestPacketReceived;

		private long gtpPrimeRedirectionResponseSent;
		private long gtpPrimeEchoResponseSent;
		private long gtpPrimeNodeAliveResponseSent;
		private long gtpPrimeDataRecordTransferResponseSent;
		private long gtpPrimeVersionNotSupportedResponseSent;
		
		public long gtpPrimeEchoRequestDropped;
		public long gtpPrimeNodeAliveRequestDropped;
		public long gtpPrimeRedirectionRequestDropped;
		public long gtpPrimeDataRecordTransferRequestDropped;
		public long gtpPrimeDataRecordTransferFailureResponseSent;
		private long gtpPrimeRedirectionFailureResponseSent;
		
		public long gtpPrimeEchoRequestSent;
		public long gtpPrimeEchoResponseReceived;
		public long gtpPrimeNodeAliveRequestSent;
		public long gtpPrimeNodeAliveResponseReceived;
		public long gtpPrimeEchoRequestRetry;
		public long gtpPrimeNodeAliveRequestRetry;
		public long gtpPrimeMalformedEchoResponseReceived;
		public long gtpPrimeMalformedNodeAliveResponseReceived;

		public GTPPrimeClientCounters(int gtpClientIndex, String gtpClientAddress ) {
			this.gtpClientIndex = gtpClientIndex;
			this.gtpClientAddress = gtpClientAddress;

			gtpPrimeEchoRequestReceived = 0;
			gtpPrimeNodeAliveRequestReceived = 0;
			gtpPrimeRedirectionRequestReceived = 0;
			gtpPrimeDataRecordTransferRequestReceived = 0;
			gtpPrimeDroppedRequest = 0;
			gtpPrimeMalformedRequestPacketReceived = 0;

			gtpPrimeRedirectionResponseSent = 0;
			gtpPrimeEchoResponseSent = 0;
			gtpPrimeNodeAliveResponseSent = 0;
			gtpPrimeDataRecordTransferResponseSent = 0;
			gtpPrimeVersionNotSupportedResponseSent = 0;
			
			gtpPrimeEchoRequestDropped = 0;
			gtpPrimeNodeAliveRequestDropped = 0;
			gtpPrimeRedirectionRequestDropped = 0;
			gtpPrimeDataRecordTransferRequestDropped = 0;
			
			gtpPrimeDataRecordTransferFailureResponseSent = 0;
			gtpPrimeRedirectionFailureResponseSent = 0;
			
			gtpPrimeEchoRequestSent = 0;
			gtpPrimeEchoResponseReceived = 0;
			gtpPrimeNodeAliveRequestSent = 0;
			gtpPrimeNodeAliveResponseReceived = 0;
			gtpPrimeEchoRequestRetry = 0;
			gtpPrimeNodeAliveRequestRetry = 0;
			gtpPrimeMalformedEchoResponseReceived = 0;
			gtpPrimeMalformedNodeAliveResponseReceived = 0;
		}

		public int getGTPClientIndex() {
			return gtpClientIndex;
		}

		public String getGTPClientAddress() {
			return gtpClientAddress;
		}
		
		public Long getGTPPrimeMalformedEchoResponseReceived() {
			return gtpPrimeMalformedEchoResponseReceived;
		}

		public void incrementGTPPrimeMalformedEchoResponseReceived() {
			this.gtpPrimeMalformedEchoResponseReceived++;
		}
		
		public Long getGTPPrimeMalformedNodeAliveResponseReceived() {
			return gtpPrimeMalformedNodeAliveResponseReceived;
		}

		public void incrementGTPPrimeMalformedNodeAliveResponseReceived() {
			this.gtpPrimeMalformedNodeAliveResponseReceived++;
		}
		
		public Long getGTPPrimeEchoRequestRetry() {
			return gtpPrimeEchoRequestRetry;
		}

		public void incrementGTPPrimeEchoRequestRetry() {
			this.gtpPrimeEchoRequestRetry++;
		}

		public Long getGTPPrimeNodeAliveRequestRetry() {
			return gtpPrimeNodeAliveRequestRetry;
		}

		public void incrementGTPPrimeNodeAliveRequestRetry() {
			this.gtpPrimeNodeAliveRequestRetry++;
		}

		
		public Long getGTPPrimeEchoRequestSent() {
			return gtpPrimeEchoRequestSent;
		}

		public void incrementGTPPrimeEchoRequestSent() {
			this.gtpPrimeEchoRequestSent++;
		}
		
		public Long getGTPPrimeEchoResponseReceived() {
			return gtpPrimeEchoResponseReceived;
		}

		public void incrementGTPPrimeEchoResponseReceived() {
			this.gtpPrimeEchoResponseReceived++;
		}
		
		public Long getGTPPrimeNodeAliveResponseReceived() {
			return gtpPrimeNodeAliveResponseReceived;
		}

		public void incrementGTPPrimeNodeAliveResponseReceived() {
			this.gtpPrimeNodeAliveResponseReceived++;
		}
		
		public Long getGTPPrimeNodeAliveRequestSent() {
			return gtpPrimeNodeAliveRequestSent;
		}

		public void incrementGTPPrimeNodeAliveRequestSent() {
			this.gtpPrimeNodeAliveRequestSent++;
		}

		public Long getGTPPrimeEchoRequestReceived() {
			return gtpPrimeEchoRequestReceived;
		}

		public void incrementGTPPrimeEchoRequestReceived() {
			this.gtpPrimeEchoRequestReceived++;
		}

		public Long getGTPPrimeNodeAliveRequestReceived() {
			return gtpPrimeNodeAliveRequestReceived;
		}

		public void incrementGTPPrimeNodeAliveRequestReceived() {
			this.gtpPrimeNodeAliveRequestReceived++;
		}

		public Long getGTPPrimeRedirectionRequestReceived() {
			return gtpPrimeRedirectionRequestReceived;
		}

		public void incrementGTPPrimeRedirectionRequestReceived() {
			this.gtpPrimeRedirectionRequestReceived++;
		}

		public Long getGTPPrimeDataRecordTransferRequestReceived() {
			return gtpPrimeDataRecordTransferRequestReceived;
		}

		public void incrementGTPPrimeDataRecordTransferRequestReceived() {
			this.gtpPrimeDataRecordTransferRequestReceived++;
		}

		public Long getGTPPrimeRedirectionResponseSent() {
			return gtpPrimeRedirectionResponseSent;
		}

		public void incrementGTPPrimeRedirectionResponseSent() {
			this.gtpPrimeRedirectionResponseSent++;
		}

		public Long getGTPPrimeEchoResponseSent() {
			return gtpPrimeEchoResponseSent;
		}

		public void incrementGTPPrimeEchoResponseSent() {
			this.gtpPrimeEchoResponseSent++;
		}

		public Long getGTPPrimeNodeAliveResponseSent() {
			return gtpPrimeNodeAliveResponseSent;
		}

		public void incrementGTPPrimeNodeAliveResponseSent() {
			this.gtpPrimeNodeAliveResponseSent++;
		}

		public Long getGTPPrimeDataRecordTransferResponseSent() {
			return gtpPrimeDataRecordTransferResponseSent;
		}

		public void incrementGTPPrimeDataRecordTransferResponseSent() {
			this.gtpPrimeDataRecordTransferResponseSent++;
		}

		public Long getGTPPrimeVersionNotSupportedResponseSent() {
			return gtpPrimeVersionNotSupportedResponseSent;
		}

		public void incrementGTPPrimeVersionNotSupportedResponseSent() {
			this.gtpPrimeVersionNotSupportedResponseSent++;
		}

		public void incrementGTPPrimeDroppedRequestReceived() {
			this.gtpPrimeDroppedRequest++;
		}

		public long getGTPPrimeDroppedRequest() {
			return gtpPrimeDroppedRequest;
		}

		public void incrementGTPPrimeMalformedRequestPacketReceived() {
			gtpPrimeMalformedRequestPacketReceived++;			
		}
		public long getGTPPrimeMalformedRequestPacketReceived() {
			return gtpPrimeMalformedRequestPacketReceived;			
		}

		public void incrementGTPPrimeEchoRequestDropped() {
			gtpPrimeEchoRequestDropped++;
				
		}

		public void incrementGTPPrimeNodeAliveRequestDropped() {
			gtpPrimeNodeAliveRequestDropped++;
			
		}

		public void incrementGTPPrimeRedirectionRequestDropped() {
			gtpPrimeRedirectionRequestDropped++;
		}

		public void incrementGTPPrimeDataRecordTransferRequestDropped() {
			gtpPrimeDataRecordTransferRequestDropped++;				
		}
		
		public void incrementGTPPrimeDataRecordTransferFailureResponseSent() {
			gtpPrimeDataRecordTransferFailureResponseSent++;
		}


		public void incrementGTPPrimeRedirectionFailureResponseSent() {
			gtpPrimeRedirectionFailureResponseSent++;
		}
		
		public long getGTPPrimeRedirectionFailureResponseSent() {
			return gtpPrimeRedirectionFailureResponseSent;
		}
		
		public long getGTPPrimeDataRecordTransferFailureResponseSent() {
			return gtpPrimeDataRecordTransferFailureResponseSent;
		}
		
		public long getGtpPrimeEchoRequestDropped() {
			return gtpPrimeEchoRequestDropped;
		}

		public long getGtpPrimeNodeAliveRequestDropped() {
			return gtpPrimeNodeAliveRequestDropped;
		}

		public long getGtpPrimeRedirectionRequestDropped() {
			return gtpPrimeRedirectionRequestDropped;
		}

		public long getGtpPrimeDataRecordTransferRequestDropped() {
			return gtpPrimeDataRecordTransferRequestDropped;
		}
	}
}
