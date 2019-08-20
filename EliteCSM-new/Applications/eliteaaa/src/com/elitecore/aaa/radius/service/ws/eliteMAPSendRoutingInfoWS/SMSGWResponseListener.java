package com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS;

public class SMSGWResponseListener{
	
	private boolean isFailure = false;
	private String imsi = "";
	
	public void setIMSI(String imsi){
		this.imsi = imsi;
	}
	public void onSuccess(){
		resetFailure();
	}
	public void onFailure() {
		isFailure = true;
	}
	public boolean isFailure() {
		return isFailure;
	}
	public String getImsi(){
		return imsi;
	}
	public void resetFailure(){
		isFailure = false;
	}
}
