package com.elitecore.coreeap.util.sim.ulticom;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.aka.ulticom.data.AuhenticationQuintupletRequester;
import com.elitecore.coreeap.util.constants.UlticomResultCodeConstants;
import com.elitecore.coreeap.util.sim.ulticom.data.AuthGateWayTripletRequestor;

public class UlticomCommunicator {
	public static final String MODULE = "ULTICOM-COMMUNICATOR";
	public static final int SIM=1;
	public static final int AKA=2;
	private int initilizedTripletRequestorCount=0;
	private int initilizedQuinteltRequestorCount=0;
	private boolean isTripletRequestorInitialized = false;
	private boolean isQuintetRequestorInitialized = false;
	private static boolean isLibraryLoaded = false;
	static {
		try {
			System.loadLibrary("UlticomRequestorlib");
			isLibraryLoaded = true;
		} catch (UnsatisfiedLinkError err){
			System.out.println("Error while loading libUlticomRequestorlib.so");
			isLibraryLoaded  = false;
		}
		
		ulticomCommunicatorInstance = new UlticomCommunicator();
	}
	private static int requestorIDsForSIM = 0;
	private static int requestorIDsForAKA = 0;
	private static UlticomCommunicator ulticomCommunicatorInstance;
	
	private ArrayBlockingQueue<AuthGateWayTripletRequestor> mapGwTripletRequestorQueue ;
	private ArrayBlockingQueue<AuhenticationQuintupletRequester> mapGwQuintetRequestorQueue ;
	
	private native int initAuthGWConnection(int logLevel, String remoteHost, String localHost, int driverID);
	public native void terminateAuthGWConnection(String remoteHost, String localHost, int driverID);
	public native void shutdownSctpAuthConnection(int driverID);
	
	public static UlticomCommunicator getInstance() {
		return ulticomCommunicatorInstance;
	}

	public boolean init(int noOfConnections, int logLevel, String remoteHost, String localHost, int driverID){
		if (!isLibraryLoaded){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Native Library for Triplet requestor is not loaded so Triplet requestor connection pool will not be initialized");
				LogManager.getLogger().warn(MODULE, "Check LD_LIBRARY_PATH");
			}
			return false;
		}
		
		// Initializing TCP connection with HLR through MAP Gateway
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Initializing connection with Auth Gateway with driver ID: " + driverID + ", Remote Host: " + remoteHost + ", Local Host: " + localHost);
		if (initAuthGWConnection(logLevel, remoteHost, localHost, driverID) != 1){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Failed to initialize Auth Gateway Gateway connection.");
			return false;
		}
		
		if (noOfConnections > 500){
			noOfConnections = 500;
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Maximum 500 connections are allowed for signalware");
		}
		
		// Initializing queue for triplet requesters
		AuthGateWayTripletRequestor tripletRequestor;
		mapGwTripletRequestorQueue = new ArrayBlockingQueue<AuthGateWayTripletRequestor>(noOfConnections);
		
		for ( int i=0 ; i< noOfConnections ; i++){
			tripletRequestor = new AuthGateWayTripletRequestor(requestorIDsForSIM++);
				mapGwTripletRequestorQueue.offer(tripletRequestor);
			}
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Initialized " + mapGwTripletRequestorQueue.size() + " Map Gateway Requestors for triplet.");
		initilizedTripletRequestorCount = mapGwTripletRequestorQueue.size();
		isTripletRequestorInitialized = true;
	
		// Initializing queue for quintet requesters
		AuhenticationQuintupletRequester quintetRequestor;
		mapGwQuintetRequestorQueue = new ArrayBlockingQueue<AuhenticationQuintupletRequester>(noOfConnections);
		for ( int i=0 ; i< noOfConnections ; i++){
			quintetRequestor = new AuhenticationQuintupletRequester(requestorIDsForAKA++);
				mapGwQuintetRequestorQueue.offer(quintetRequestor);
			}
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Initialized " + mapGwQuintetRequestorQueue.size() + " Map Gateway Quintet Requestors.");
		initilizedQuinteltRequestorCount = mapGwQuintetRequestorQueue.size();
		isQuintetRequestorInitialized = true;
		
		return true;
	}
	
	public void getTriplets(String imsi,int numberOfTriplets, UlticomResponseListener responseListener, long requestTimeout, int driverID){
		if (!isTripletRequestorInitialized){
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Request for triplets is received without inititalization");
				return;
			}
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Request for " + numberOfTriplets + " triplets for IMSI : " + imsi + ", Using Driver ID: " + driverID);
		AuthGateWayTripletRequestor requestor = null;
		try {
			requestor = mapGwTripletRequestorQueue.poll( 100 , TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Connection to MAP GW Could not be availed to fetch triplets for IMSI: "+ imsi);
		}
		if (requestor != null ) {
			responseListener = createWrappedResponseListener(numberOfTriplets, responseListener, requestor);
			requestor.registerListener(responseListener);
			int resultCode = UlticomResultCodeConstants.ULCM_SUCCESS.resultCode;
			while((resultCode == UlticomResultCodeConstants.ULCM_SUCCESS.resultCode) 
					&& (requestor.tripletsReceived() < numberOfTriplets)) {
				resultCode = requestor.requestTriplets(imsi, numberOfTriplets - requestor.tripletsReceived(), requestTimeout, requestor.getRequestorID(), driverID);
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, requestor.tripletsReceived() + "/" + numberOfTriplets + " triplets received.");
				}
			}

			if (resultCode != 0) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Request triplet: IMSI: " + imsi + " Response code: " + resultCode + " Name: " + UlticomResultCodeConstants.getName(resultCode) + " Message: "+ UlticomResultCodeConstants.getName(resultCode).getMessage());
				}
				UlticomResultCodeConstants result = UlticomResultCodeConstants.getName(resultCode);
				if (result.needDriverDead()){
					responseListener.markDriverDead();
				}
				if (result.isFailure()) {
					responseListener.onFailure(result.message);
				}
			}
			requestor.close();
			mapGwTripletRequestorQueue.offer(requestor);
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Connection to MAP GW Could not be availed to fetch triplets for IMSI: "+ imsi+ " in: 100 ms.");
			responseListener.onFailure("Connection to MAP GW Could not be availed to fetch triplets for IMSI: " + imsi );
		}
	}
	
	private UlticomResponseListener createWrappedResponseListener(
			final int numberOfTriplets,
			final UlticomResponseListener responseListener,
			final AuthGateWayTripletRequestor requestor) {
		return new UlticomResponseListener() {
			
			@Override
			public void onTimeout() {
				responseListener.onTimeout();
			}
			
			@Override
			public void onSuccess(String authVectors) {
				if (requestor.tripletsReceived() >= numberOfTriplets) {
					responseListener.onSuccess(authVectors);
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Yet not received all triplets so requesting again");
					}
				}
			}
			
			@Override
			public void onFailure(String reason) {
				responseListener.onFailure(reason);
			}
			
			@Override
			public void markDriverDead() {
				responseListener.markDriverDead();
			}
		};
	}

	
	public void getQuintuplet(String imsi, UlticomResponseListener responseListener, long requestTimeout, int driverID){
		if (!isQuintetRequestorInitialized){
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Request for quintets is received without inititalization");
				return;
			}
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Request for QuinTuplet for IMSI : " + imsi + ", Using Driver ID: " + driverID);
		AuhenticationQuintupletRequester requestor = null;
		try {
			requestor = mapGwQuintetRequestorQueue.poll( 100 , TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Connection to MAP GW Could not be availed to fetch Quintet for IMSI: "+ imsi+ " in: 100 ms.");
		}
		if (requestor != null ){
			//Remaining timeout Given to HLR communication (Configured Request Timeout - Time taken to avail connection from mapGwRequestorQueue)
			requestor.registerListener(responseListener);
			int resultCode = requestor.requestQuintuplet(imsi, requestTimeout, requestor.getRequestorID(), driverID);
			if (resultCode != 0){
				UlticomResultCodeConstants result = UlticomResultCodeConstants.getName(resultCode);
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Request Quintuplet: IMSI: " + imsi + " Response code: " 
							+ resultCode + " Name: " + result.name() + " Message: "+ result.getMessage());
				}
				if (result.needDriverDead()){
					responseListener.markDriverDead();
				}
				if (result.isFailure()) {
					responseListener.onFailure(result.message);
				}
			}
			//			requestor.close();
			mapGwQuintetRequestorQueue.offer(requestor);
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Connection to MAP GW Could not be availed to fetch Quintet for IMSI: "+ imsi+ " in: 100 ms.");
			responseListener.onFailure("Connection to MAP GW Could not be availed to fetch Quintet for IMSI: "+ imsi);
		}

	}
	
	public boolean isAlive(int mapMethod){
		boolean isAlive = false;
		if(mapMethod==SIM){			
			if(mapGwTripletRequestorQueue!=null){				
				if(mapGwTripletRequestorQueue.size() > 0 || getActiveConnection(SIM)>0 || isTripletRequestorInitialized)
					isAlive = true;		
			}	
		}else if(mapMethod==AKA){
				if(mapGwQuintetRequestorQueue!=null){				
					if(mapGwQuintetRequestorQueue.size() > 0 || getActiveConnection(AKA)>0 || isQuintetRequestorInitialized)
						isAlive = true;			
				}			
			}
			return isAlive;
		}
		
	public int getPoolSize(int mapMethod){
		if(mapMethod==SIM){
			return initilizedTripletRequestorCount;
		}else if(mapMethod==AKA){
			return initilizedQuinteltRequestorCount;
		}
		return 0;
	}
	
	public int getActiveConnection(int mapMethod)
	{
		if(mapMethod==SIM){
			if(mapGwTripletRequestorQueue!=null){
				return initilizedTripletRequestorCount-mapGwTripletRequestorQueue.size();
			}else{
				return initilizedTripletRequestorCount;
			}
		}else if(mapMethod==AKA){
			if(mapGwQuintetRequestorQueue!=null){
				return initilizedQuinteltRequestorCount-mapGwTripletRequestorQueue.size();
			}else{
				return initilizedQuinteltRequestorCount;
			}
		}
		return 0;
	}
	
	public int getAvailable(int mapMethod)
	{
		if(mapMethod==SIM){
			if(mapGwTripletRequestorQueue!=null){
				return mapGwTripletRequestorQueue.size();
			}
		}else if(mapMethod==AKA){
			if(mapGwQuintetRequestorQueue!=null){
				return mapGwTripletRequestorQueue.size();
			}
		}
		return 0;
	}

	public interface UlticomResponseListener{
		public void onSuccess(String authVectors);
		public void onTimeout();
		public void onFailure(String reason);
		public void markDriverDead();
	}

	public void stop(String remoteHost, String localHost, int driverID) {
		terminateAuthGWConnection(remoteHost, localHost, driverID);
}
	
	public void log(String strMessage){
		LogManager.getLogger().trace(MODULE, strMessage);
	}
	
	public void onFailure(String reason){
		LogManager.getLogger().warn(MODULE, reason);
	}
}
