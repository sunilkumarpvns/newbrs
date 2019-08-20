package com.elitecore.diameterapi.diameter.common.util;

import java.util.Date;

public class Parameter {

	private String strOwnDiameterIdentity = "eliteaaa.elitecore.com";
	
	private String strOwnDiameterURI = "aaa://localhost:3868";
	
	private String strOwnDiameterRealm = "elitecore.com";
	
	private String strHostIPAddress = "127.0.0.1";
	
	private int intVendorID = 21067;
	
	private String strProductName = "EliteAAA";
	
	public static String PEER_IDENTITY = "PEER_IDENTITY";
	
	private static Parameter parameter = null;
	
	private String routingTableName = "";
	
	private int originStateId;

	private int listeningPort;
	
	private Date stackUpTime;
	
	private Parameter(){
	}

	static {
		parameter = new Parameter();
	}

	public static Parameter getInstance() {
		return parameter;
	}

	public void setOwnDiameterIdentity(String strDiameterIdentity) {
		this.strOwnDiameterIdentity = strDiameterIdentity;
	}
	
	public String getOwnDiameterIdentity() {
		return this.strOwnDiameterIdentity;
	}

	
	public void setOwnDiameterRealm(String strDiameterRealm) {
		this.strOwnDiameterRealm = strDiameterRealm;
	}
	
	public String getOwnDiameterRealm() {
		return this.strOwnDiameterRealm;
	}


	public void setOwnDiameterURI(String strDiameterURI) {
		this.strOwnDiameterURI = strDiameterURI;
	}
	
	public String getOwnDiameterURI() {
		return this.strOwnDiameterURI;
	}
	
	public String getRoutingTableName() {
		return routingTableName;
	}

	public void setHostIPAddress(String strHostIPAddress) {
		this.strHostIPAddress = strHostIPAddress;
	}
	
	public String getHostIPAddress() {
		return this.strHostIPAddress;
	}
	
	public void setVendorId(int intVendorId) {
		this.intVendorID = intVendorId;
	}
	
	public int getVendorId() {
		return this.intVendorID;
	}

	public void setProductName(String strProductName) {
		this.strProductName = strProductName;
	}
	
	public String getProductName() {
		return this.strProductName;
	}
	
	public void setRoutingTableName(String routingTableName) {
		this.routingTableName = routingTableName;
	}
	
	public void setOriginStateId(int originStateId){
		this.originStateId = originStateId;
	}
	public int getOriginStateId(){
		return originStateId;
	}

	public int getHostListeningPort() {
		return listeningPort;
	}

	public void setHostListeningPort(int listeningPort) {
		this.listeningPort = listeningPort;
	}

	public Date getStackUpTime() {
		return stackUpTime;
	}

	public void setStackUpTimeStamp() {
		this.stackUpTime = new Date();
	}
	
}
