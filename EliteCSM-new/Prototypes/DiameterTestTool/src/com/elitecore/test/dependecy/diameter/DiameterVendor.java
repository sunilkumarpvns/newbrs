package com.elitecore.test.dependecy.diameter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class DiameterVendor {
	
	private int vendorId;
	private String vendorName;
	private Map<Integer, String> applicationMap;
	
	public DiameterVendor(int vendorId, String vendorName, int appId, String appName) {
		this.vendorId = vendorId;
		this.vendorName = vendorName;
		applicationMap = new HashMap <Integer, String>();
		applicationMap.put(appId, appName);
	}

	public int getLVendorId() {
		return vendorId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void addApplication(int applicationId, String strApplicationName) {
		applicationMap.put(applicationId, strApplicationName);
	}
	
	@Override
	public String toString(){
		StringWriter stringWriter = new StringWriter(); 
		PrintWriter out = new PrintWriter ( stringWriter);
		out.println("Vendor: " + vendorId + ":" + vendorName);
		out.println("APPMAP: " + applicationMap.toString());
		return stringWriter.toString();
	}

}
