package com.elitecore.coreradius.util.mbean.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class ClientData implements Serializable {

	private static final long serialVersionUID = 1L;
	private String strClientIP = null;
	private String strSharedSecret = null;
	private String strVendorName = null;
	private String strVendorID = null;
	private String strVendorType = null;
	private String strTimeZone = null;
	private String strSecurityMode = null;
	private String strRequestExpiryTime = null;
	private String strHaIpAddress = null;
	private String strDhcpIpAddress = null;

	public String getHaIpAddress() {
		return strHaIpAddress;
	}

	public void setHaIpAddress(String haIpAddress) {		
		this.strHaIpAddress = haIpAddress;
	}

	public String getDhcpIpAddress() {
		return strDhcpIpAddress;
	}

	public void setDhcpIpAddress(String strDhcpIpAddress) {		
		this.strDhcpIpAddress = strDhcpIpAddress;		
	}

	public String getRequestExpiryTime() {
		return strRequestExpiryTime;
	}

	public void setRequestExpiryTime(String strRequestExpiryTime) {
		this.strRequestExpiryTime = strRequestExpiryTime;
	}

	public void setClientIP(String clientIP) {
		this.strClientIP = clientIP;
	}

	public String getClientIP() {
		return strClientIP;
	}

	public String getSecurityMode() {
		return strSecurityMode;
	}

	public void setSecurityMode(String strSecurityMode) {
		this.strSecurityMode = strSecurityMode;
	}

	public String getSharedSecret() {
		return strSharedSecret;
	}

	public void setSharedSecret(String strSharedSecret) {
		this.strSharedSecret = strSharedSecret;
	}

	public String getTimeZone() {
		return strTimeZone;
	}

	public void setTimeZone(String strTimeZone) {
		this.strTimeZone = strTimeZone;
	}

	public String getVendorID() {
		return strVendorID;
	}

	public void setVendorID(String strVendorID) {
		this.strVendorID = strVendorID;
	}

	public String getVendorName() {
		return strVendorName;
	}

	public void setVendorName(String strVendorName) {
		this.strVendorName = strVendorName;
	}

	public String getVendorType() {
		return strVendorType;
	}

	public void setVendorType(String strVendorType) {
		this.strVendorType = strVendorType;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Client Ip           : " + strClientIP);
		out.println("Shared Secret       : " + ((strSharedSecret!=null)?strSharedSecret:""));
		out.println("Vendor Name         : " + ((strVendorName!=null)?strVendorName:""));
		out.println("Vendor ID           : " + ((strVendorID!=null)?strVendorID:""));
		out.println("Vendor Type         : " + ((strVendorType!=null)?strVendorType:""));
		out.println("Time Zone           : " + ((strTimeZone!=null)?strTimeZone:""));
		out.println("Security Mode       : " + ((strSecurityMode!=null)?strSecurityMode:""));
		out.println("Request Expiry Time : " + ((strRequestExpiryTime!=null)?strRequestExpiryTime:""));
		out.println("DHCP Ip Address     : " + ((strDhcpIpAddress!=null)?strDhcpIpAddress:""));
		out.println("HA Ip Address       : " + ((strHaIpAddress!=null)?strHaIpAddress:""));
		
		return stringBuffer.toString();
	}

}
