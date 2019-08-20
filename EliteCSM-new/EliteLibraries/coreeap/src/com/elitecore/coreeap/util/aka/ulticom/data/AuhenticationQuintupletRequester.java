package com.elitecore.coreeap.util.aka.ulticom.data;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.sim.ulticom.UlticomCommunicator.UlticomResponseListener;

public class AuhenticationQuintupletRequester {

	private String akaQuintet = "";
	private static final String MODULE = "AUTH-QUINTUPLETREQUESTOR";
	private UlticomResponseListener responseListener;
	private boolean isFailure = false;
	private int requestorID;

	public native int requestQuintuplet(String imsi, long requestTimeout, int requesterID, int driverID);
	
	public AuhenticationQuintupletRequester(int requestorIDsForAKA) {
		this.requestorID = requestorIDsForAKA;
	}
	
	public void onSuccess(){
		isFailure = false;
		if (akaQuintet.trim().length() > 0)
			responseListener.onSuccess(akaQuintet.substring(0, akaQuintet.length() - 1));
		else 
			responseListener.onSuccess(akaQuintet);
		close();
	}

	public void onFailure(String reason){
		isFailure = true;
		responseListener.onFailure(reason);
	}
	
	/**
	 * If native implementation detects timeout from HLR, it will invoke onTimeout() method 
	 */			
	public void onTimeout(){
		responseListener.onTimeout();
	}

	public void addQuintuplet(String strRand,String strAutn,String strXres, String strCk,String strIk){
		LogManager.getLogger().info(MODULE, "Quintuplet Received : Rand = "+strRand+ " , Xres = "+ strXres +" , Autn = " + strAutn + " , Ck = " + strCk + " , Ik = " + strIk);
		akaQuintet +=  strRand + "," + strXres + "," + strCk + "," + strIk + "," + strAutn + ":"; 
	}		
	public void registerListener(UlticomResponseListener responseListener){
		this.responseListener = responseListener;
	}
	
	public void deregisterListener(){
		this.responseListener = null;
	}
	
	public void log(String logMsg){
		LogManager.getLogger().info(MODULE, logMsg);
	}
	public boolean isFailure() {
		return isFailure;
	}
	public void resetFailure() {
		isFailure = false;
	}
	public void close(){
		 akaQuintet = "";
		 deregisterListener();
	}
	public int getRequestorID(){
		return requestorID;
	}
}
