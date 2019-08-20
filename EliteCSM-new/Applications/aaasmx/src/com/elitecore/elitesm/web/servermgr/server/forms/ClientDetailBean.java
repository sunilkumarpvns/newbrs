package com.elitecore.elitesm.web.servermgr.server.forms;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData;

public class ClientDetailBean {
	
	private String clientIP;
	private String sharedSecret;
	private String requestExpiryTime;
	private RadiusClientProfileData clientProfileData;
	
	
	
	public String getClientIP() {
		return clientIP;
	}



	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}



	public String getSharedSecret() {
		return sharedSecret;
	}



	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}



	public String getRequestExpiryTime() {
		return requestExpiryTime;
	}



	public void setRequestExpiryTime(String requestExpiryTime) {
		this.requestExpiryTime = requestExpiryTime;
	}



	public RadiusClientProfileData getClientProfileData() {
		return clientProfileData;
	}



	public void setClientProfileData(RadiusClientProfileData clientProfileData) {
		this.clientProfileData = clientProfileData;
	}



	@Override
    public String toString() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println();
        writer.println("------------------ ClientDetailBean ---------------");
        writer.println("clientIP                 =" +clientIP);
        writer.println("sharedSecret             =" +sharedSecret);
        writer.println("requestExpiryTime        =" +requestExpiryTime);
        writer.println("----------------------------------------------------");
        return out.toString();  
	}

}
