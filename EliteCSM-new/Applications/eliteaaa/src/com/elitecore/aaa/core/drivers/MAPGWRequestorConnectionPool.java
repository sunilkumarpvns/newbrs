package com.elitecore.aaa.core.drivers;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.elitecore.aaa.core.drivers.MAPGWAuthDriver.UlticomResponseListener;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.constants.UlticomResultCodeConstants;

public class MAPGWRequestorConnectionPool {
	private static String MODULE = "MAPGW-REQ-CONN-POOL";
	private static boolean isLibraryLoaded = false;
	private static int profileReqIDs = 0;

	static {
		try {
			System.loadLibrary("UlticomProfileRequestorlib");
			isLibraryLoaded = true;
		} catch (UnsatisfiedLinkError err){
			System.out.println("Error while loading libUlticomProfileRequestorlib.so");
			isLibraryLoaded   = false;			
		}
	}
	private native int initAuthGWConnection(int driverID, int logLevel, String remoteHost, String localHost);
	public native void terminateAuthGWConnection(String remoteHost, String localHost, int driverID);
	public native void shutdownSctpAuthConnection(int driverID);
	
	private ArrayBlockingQueue<AuthGatewayProfileRequestor> mapGwRequestorQueue ;
	private int noOfConnections;
	private long requestTimeout;
	private UlticomResponseListener ulticomResponseListener;
	
	public MAPGWRequestorConnectionPool(UlticomResponseListener listener, String remoteHost, String localHost, int noOfConnection, long requesTimeout){
		this.noOfConnections = noOfConnection;
		this.requestTimeout = requesTimeout;
		this.ulticomResponseListener = listener;
	}
	
	public boolean init(int driverID, String remoteHost, String localHost){
		if (!isLibraryLoaded){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Native Library for Profile requestor is not loaded so Profile requestor connection pool will not be initialized");
				LogManager.getLogger().warn(MODULE, "Check LD_LIBRARY_PATH");
			}
			return false;
		}
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Initializing connection with Auth Gateway with driver ID: " + driverID + ", Remote Host: " + remoteHost + ", Local Host: " + localHost);
		if (initAuthGWConnection(driverID, LogManager.getLogger().getCurrentLogLevel(), remoteHost, localHost) != 1){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Failed to initialize MAP Gateway connection so Profile requestor connection pool will not be initialized");
			return false;
		}
		
		AuthGatewayProfileRequestor profileRequestor;
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Initializing: " + noOfConnections + " Map Gateway Profile Requestors.");
		mapGwRequestorQueue = new ArrayBlockingQueue<AuthGatewayProfileRequestor>(noOfConnections);
		
		for ( int i=0 ; i< noOfConnections ; i++){
			profileRequestor = new AuthGatewayProfileRequestor(profileReqIDs++);
				mapGwRequestorQueue.offer(profileRequestor);
			}
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Initialized " + mapGwRequestorQueue.size() + " Map Gateway Profile Requestors.");
		return true;
	}
	
	public void requestProfile(String identityString, UlticomResponseListener listener, int driverID){
		AuthGatewayProfileRequestor profileRequestor = null;
		try {
			profileRequestor = mapGwRequestorQueue.poll( 100 , TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Connection to MAP GW Could not be availed in: 100 ms. Request will be Rejected.");
		}
		if (profileRequestor != null ){
			profileRequestor.registerListener(listener);
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Requesting profile for identity: " + identityString + ", Using requestor: " + profileRequestor.getRequestorID() + ", Driver ID: " + driverID);
			//Remaining timeout Given to HLR communication (Configured Request Timeout - Time taken to avail connection from mapGwRequestorQueue)
			int resultCode = profileRequestor.requestProfile(identityString, requestTimeout,  profileRequestor.getRequestorID(), driverID);
			if (resultCode != 0){
				UlticomResultCodeConstants result = UlticomResultCodeConstants.getName(resultCode);
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Request Profile: Identity: " + identityString + " Response code: " + resultCode + " Name: "
							+ result.name() + " Message: "+ result.getMessage());
				}
				if (result.needDriverDead()){
					listener.markDriverDead();
				}
				if (result.isFailure()) {
					listener.onFailure(result.message);
				}
			}
//			profileRequestor.close();
			mapGwRequestorQueue.offer(profileRequestor);
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Connection to MAP GW Could not be availed in: 100 ms. Request will be Rejected.");
			listener.onFailure("Connection to MAP GW Could not be availed in: 100 ms. Request will be Rejected.");
		}
	}
	
	public void stop(String remoteHost, String localHost, int driverID){
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Terminating MAP Gateway Requestor connection pool for Driver ID: " + driverID + ", Remote Host: " + remoteHost + ", Local Host: " + localHost );
		terminateAuthGWConnection(remoteHost, localHost, driverID);
				}
	
	public void log(String strMessage){
		LogManager.getLogger().trace(MODULE, strMessage);
}
	
	public void onFailure(String reason){
		LogManager.getLogger().warn(MODULE, reason);
	}
}
