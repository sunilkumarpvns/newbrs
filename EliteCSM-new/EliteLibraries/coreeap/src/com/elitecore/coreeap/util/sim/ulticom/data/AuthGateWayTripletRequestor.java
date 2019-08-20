package com.elitecore.coreeap.util.sim.ulticom.data;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.sim.ulticom.UlticomCommunicator.UlticomResponseListener;

public class AuthGateWayTripletRequestor {
	private UlticomResponseListener responseListener;
	private String gsmTriplets = "";
	private static final String MODULE = "AUTHGATEWAY-TRIPLETREQUESTOR";
	private boolean isFailure = false;
	private int requestorID;
	private int noOfTriplets = 0;
	
	public native int requestTriplets(String imsi,int numberOfTriplets, long requestTimeout, int requesterID, int driverID);
	
	public AuthGateWayTripletRequestor(int reqID){
		this.requestorID = reqID;
	}
	/**
	 * native implementation will receive the triplets,after receiving the triplets native implementaion will invoke OnSuccess() method.
	 */
	public void onSuccess(){
		isFailure = false;
		if (gsmTriplets.trim().length() > 0)
			responseListener.onSuccess(gsmTriplets.substring(0, gsmTriplets.length() - 1));
		else 
			responseListener.onSuccess(gsmTriplets);
	}
	/**
	 * If native implementation will not receive any triplets from Authgateway, then native implementaion will invoke OnFailure() method with failure reason.
	 */		
	public void onFailure(String reason){
		isFailure = true;
		responseListener.onFailure(reason);
	}
	

	public void addTriplets(String strRand,String strSres,String strKc){			
		LogManager.getLogger().info(MODULE, "Triplet Received : Rand = "+strRand+ " , Sres = "+ strSres +" , Kc = " + strKc);
		noOfTriplets++;
		gsmTriplets += strRand + "," + strSres + "," + strKc + ":";			
	}		
	
	public int tripletsReceived() {
		return this.noOfTriplets;
	}
	
	public void registerListener(UlticomResponseListener responseListener){
		this.responseListener = responseListener;
	}
	
	public void deregisterListener(){
		this.responseListener = null;
	}
	
	public boolean isFailure() {
		return isFailure;
	}
	public void resetFailure() {
		isFailure = false;
	}
	public void log(String logMsg){
		LogManager.getLogger().info(MODULE, logMsg);
	}
	
	public void close(){
		gsmTriplets = "";
		noOfTriplets  = 0;
		deregisterListener();
	}
	public int getRequestorID(){
		return requestorID;
	}
	
	/**
	 * If native implementation detects timeout from HLR, it will invoke onTimeout() method 
	 */			
	public void onTimeout(){
		responseListener.onTimeout();
	}
}
