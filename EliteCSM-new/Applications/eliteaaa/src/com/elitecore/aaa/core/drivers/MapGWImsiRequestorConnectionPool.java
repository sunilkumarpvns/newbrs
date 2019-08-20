package com.elitecore.aaa.core.drivers;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS.SMSGWResponseListener;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class MapGWImsiRequestorConnectionPool {
	
	private static String MODULE = "IMSI-REQ-CONN-POOL" ;
	private ArrayBlockingQueue<SMSGatewayIMSIRequestor> smsGWImsiReqQueue;
	private long requestTimeout = 1000;
	private String localHost, remoteHost;
	private static boolean isInitialized = false;
	private static boolean isLibraryLoaded = false;
	private static MapGWImsiRequestorConnectionPool imsiRequestorConnectionPool;
	private int requestorIDs = 0;
	
	static {
		try {
			System.loadLibrary("UlticomIMSIRequestorlib");
			isLibraryLoaded = true;
		} catch (UnsatisfiedLinkError err){
			System.out.println("Error while loading libUlticomIMSIRequestorlib.so");
			isLibraryLoaded = false;
		}
	}
	
	private MapGWImsiRequestorConnectionPool(){}
	
	static {
		imsiRequestorConnectionPool = new MapGWImsiRequestorConnectionPool();
	}
	
	public static MapGWImsiRequestorConnectionPool getInstance(){
		return imsiRequestorConnectionPool;
	}
	
	public void init (String localHost , String remoteHost, String requestTimeout){
		if (isInitialized){
			return;
		}
		if (!isLibraryLoaded){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Native Library for IMSI requestor is not loaded so IMSI requestor connection pool will not be initialized.");
				LogManager.getLogger().warn(MODULE, "Check LD_LIBRARY_PATH");
			}
			return;
		}
		try {
			this.requestTimeout = Long.parseLong(requestTimeout);
		} catch (NumberFormatException e){
			if ((LogManager.getLogger().isLogLevel(LogLevel.WARN)))
				LogManager.getLogger().warn(MODULE, "Request timeout configured for IMSI requestor is invalid. Using default value 1000");
			this.requestTimeout = 1000;
		}
		if (localHost == null || localHost.trim().length() == 0){
			if ((LogManager.getLogger().isLogLevel(LogLevel.WARN)))
				LogManager.getLogger().warn(MODULE, "Local Host configuration is invalid for IMSI requestor.");
		}
		
		if (remoteHost == null || remoteHost.trim().length() == 0){
			if ((LogManager.getLogger().isLogLevel(LogLevel.WARN)))
				LogManager.getLogger().warn(MODULE, "Remote Host Configuration is invalid for IMSI requestor.");
		}
		
		if (remoteHost!=null && localHost!=null && remoteHost.trim().length() > 0 && localHost.trim().length() > 0){
			SMSGWResponseListener responseListener;
			SMSGatewayIMSIRequestor imsiRequestor;
			if ((LogManager.getLogger().isLogLevel(LogLevel.INFO)))
				LogManager.getLogger().info(MODULE, "Initializing: 5 Map Gateway IMSI Requestors.");
			smsGWImsiReqQueue = new ArrayBlockingQueue<SMSGatewayIMSIRequestor>(5);

			for ( int i=0 ; i< 5 ; i++){
				responseListener = new SMSGWResponseListener();
				imsiRequestor = new SMSGatewayIMSIRequestor(requestorIDs++);
				imsiRequestor.registerListener(responseListener);
				imsiRequestor.init(remoteHost, localHost, LogManager.getLogger().getCurrentLogLevel());
				imsiRequestor.deregisterListener();
				if (!responseListener.isFailure()){ 
					smsGWImsiReqQueue.offer(imsiRequestor);
				} else {
					if ((LogManager.getLogger().isLogLevel(LogLevel.WARN)))
						LogManager.getLogger().warn(MODULE, "Failed to initialize " + i + "th Map Gateway IMSI Requestor");
					responseListener.resetFailure();
				}
			}
			if ((LogManager.getLogger().isLogLevel(LogLevel.INFO)))
				LogManager.getLogger().info(MODULE, "Initialized " + smsGWImsiReqQueue.size() + " Map Gateway IMSI Requestors.");
			isInitialized = true;
		} else { 
			if ((LogManager.getLogger().isLogLevel(LogLevel.WARN)))
				LogManager.getLogger().warn(MODULE, "Configuration For IMSI requestor is invalid. Not initializing IMSI requestor connection pool.");
		}
	}
	
	public void requestIMSIForMSISDN(String msisdn, SMSGWResponseListener listener) throws Exception{
		if (!isInitialized){
			if ((LogManager.getLogger().isLogLevel(LogLevel.ERROR)))
				LogManager.getLogger().error(MODULE, "IMSI Requestor is not initialized. It must be initialized before requesting IMSI from MAP Gateway");
			return;
		}
	
		SMSGatewayIMSIRequestor imsiRequestor = null;
		long currentTime = System.currentTimeMillis();
		try {
			imsiRequestor = smsGWImsiReqQueue.poll( 100 , TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			if ((LogManager.getLogger().isLogLevel(LogLevel.DEBUG)))
				LogManager.getLogger().debug(MODULE, "Connection to MAP GW Could not be availed in: 100 ms. to fetch IMSI for MSISDN: " + msisdn);
		}
		if (imsiRequestor != null ){
			imsiRequestor.registerListener(listener);
			//Remaining timeout Given to HLR communication (Configured Request Timeout - Time taken to avail connection from smsGWImsiReqQueue)
			imsiRequestor.requestIMSI(msisdn, requestTimeout-(System.currentTimeMillis()-currentTime), imsiRequestor.getRequestorID());
			imsiRequestor.deregisterListener();
			smsGWImsiReqQueue.offer(imsiRequestor);
		} else {
			if ((LogManager.getLogger().isLogLevel(LogLevel.WARN)))
				LogManager.getLogger().warn(MODULE, "Connection to MAP GW Could not be availed in: 100 ms. to fetch IMSI for MSISDN: " + msisdn);
			throw new Exception("Connection to MAP Gateway Not Available");
		}
	}
	
	public void terminate(){
		if (isInitialized){
			SMSGWResponseListener responseListener = new SMSGWResponseListener();;
			if ((LogManager.getLogger().isLogLevel(LogLevel.INFO)))
				LogManager.getLogger().info(MODULE, "Terminating Ulticom IMSI Requestor");
			int len = smsGWImsiReqQueue.size();
			SMSGatewayIMSIRequestor imsiRequestor = null;
			for (int i=0 ; i<len ; i++ ){
				try {
					imsiRequestor = smsGWImsiReqQueue.take(); 
					imsiRequestor.registerListener(responseListener);
					imsiRequestor.terminate(remoteHost, localHost);
					if (responseListener.isFailure()){
						if ((LogManager.getLogger().isLogLevel(LogLevel.DEBUG)))
							LogManager.getLogger().debug(MODULE, "Could not terminate " + (i+1) + "th MAP GW IMSI Requestor connection");
						responseListener.resetFailure();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			isInitialized = false;
		}
	}
}
