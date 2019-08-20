/**
 * 
 */
package com.elitecore.aaa.rm.data;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author dhaval.jobanputra
 *
 */
public class GTPPrimeClientDataImpl implements GTPPrimeClientData {

	private String clientIP;
	private int clientPort;
	private boolean echoReq;
	private boolean nodeAliveReq;
	private int reqExpiryTime;
	private int requestRetry;
	private String RedirectionIP;
	private String fileLocation;
	private int rollingType;
	private long rollingUnit;
	private int maxRolledUnit;
	private boolean compressRolledUnit;

	public GTPPrimeClientDataImpl() {
		super();
	}

	@Override
	public String getClientIP() {
		return clientIP;
	}

	@Override
	public int getClientPort() {
		return clientPort;
	}

	@Override
	public boolean getEchoRequest() {
		return echoReq;
	}

	@Override
	public boolean getNodeAliveRequest() {
		return nodeAliveReq;
	}

	@Override
	public int getRequestExpiryTime() {
		return reqExpiryTime;
	}

	@Override
	public boolean getCompressRolledUnit() {
		return compressRolledUnit;
	}

	@Override
	public String getFileLocation() {
		return fileLocation;
	}

	@Override
	public int getMaxRolledUnit() {
		return maxRolledUnit;
	}

	@Override
	public int getRollingType() {
		return rollingType;
	}

	@Override
	public long getRollingUnit() {
		return rollingUnit;
	}
	@Override
	public String getRedirectionIP() {
		return RedirectionIP;
	}

	@Override
	public int getRequestRetry() {
		return requestRetry;
	}
	@Override
	public void setClientIP(String ip) {
		clientIP = ip;
	}

	@Override
	public void setClientPort(int port) {
		clientPort = port;
	}

	@Override
	public void setEchoRequest(boolean value) {
		echoReq = value;
	}

	@Override
	public void setNodeAliveRequest(boolean value) {
		nodeAliveReq = value;
	}

	@Override
	public void setRequestExpiryTime(int time) {
		reqExpiryTime = time;
	}

	@Override
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Client Adress: " + clientIP + ":"+ clientPort);
		out.println("Initiate echo request: " + echoReq + "Node Alive request: " + nodeAliveReq);
		out.println("Request expiry timer: " + reqExpiryTime);
		out.println("Request Retry: " + requestRetry);
		out.println("Redirection IP: " + RedirectionIP);
		out.println("File Location: " + fileLocation);
		out.println("File rolling- Type: " + rollingType + " Unit: " + rollingUnit);
		out.println("Max Rolled Unit: " + maxRolledUnit);
		out.println("Compress Rolled unit: " + compressRolledUnit);
		return stringBuffer.toString();

	}

	@Override
	public void setCompressRolledUnit(boolean value) {
		this.compressRolledUnit = value;

	}

	@Override
	public void setFileLocation(String loc) {
		this.fileLocation = loc;		
	}

	@Override
	public void setMaxRolledUnit(int unit) {
		this.maxRolledUnit = unit;
	}

	@Override
	public void setRedirectionIP(String ip) {
		this.RedirectionIP = ip;
	}

	@Override
	public void setRequestRetry(int value) {
		this.requestRetry = value;
	}

	@Override
	public void setRollingType(int type) {
		this.rollingType = type;
	}

	@Override
	public void setRollingUnit(long unit) {
		this.rollingUnit = unit;
	}
}
