package com.elitecore.aaa.core.drivers;

import com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS.SMSGWResponseListener;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class SMSGatewayIMSIRequestor {
	
	private static final String MODULE = "NATIVE-SMSGW-IMSI-REQ";
	private String receivedIMSI = ""; 
	SMSGWResponseListener responseListener;
	private int requestorID = 0;

	public native void init(String remoteHost, String localHost, int logLevel);
	public native void requestIMSI(String msisdn, long requestTimeout, int requestorID);
	public native void terminate(String remoteHost, String localHost);
	
	public SMSGatewayIMSIRequestor(int requestorID){
		this.requestorID = requestorID;
	}
		
	public void setIMSI(String imsi){
		if (imsi != null && imsi.trim().length() > 0){
			this.receivedIMSI = imsi;
		}
		responseListener.setIMSI(receivedIMSI);
	}
	
	public void onSuccess(){
		responseListener.onSuccess();
	}
	
	public void onFailure(String error){
		if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
			LogManager.getLogger().error(MODULE, "Error Ocuured while fetching IMSI for MSISDN. Error: " + error);
		responseListener.onFailure();
	}
	
	public String getIMSI(){
		return this.receivedIMSI;
	}
	private void log(String msg){
		if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
			LogManager.getLogger().warn(MODULE, msg);
	}
	public void registerListener(SMSGWResponseListener responseListener) {
		this.responseListener = responseListener;
	}
	public void deregisterListener() {
		responseListener = null;
	}
	public int getRequestorID(){
		return this.requestorID;
	}
}
