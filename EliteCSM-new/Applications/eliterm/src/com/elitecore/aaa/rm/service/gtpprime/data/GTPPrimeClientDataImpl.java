/**
 * 
 */
package com.elitecore.aaa.rm.service.gtpprime.data;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author dhaval.jobanputra
 *
 */
public class GTPPrimeClientDataImpl implements GTPPrimeClientData {

	private String clientIP;
	private int clientPort;
	private int echoRequestInterval;
	private boolean nodeAliveReq;
	private long reqExpiryTime;
	private int requestRetry;
	private String redirectionIP;
	private String fileName;
	private boolean isSequenceRequired;
	private int minSequenceRange;
	private int maxSequenceRange;
	private String fileLocation;
	private int rollingType;
	private long rollingUnit;
	private String sequenceRange;

	public GTPPrimeClientDataImpl() {
		// required By Jaxb.
	}

	@XmlElement(name ="sequence-range",type = String.class)
	public String getSequenceRange(){
		return sequenceRange;
	}
	public void setSequenceRange(String sequenceRange){
		this.sequenceRange = sequenceRange;
	}
	@Override
	@XmlElement(name = "client-ip",type = String.class)
	public String getClientIP() {
		return clientIP;
	}

	@Override
	@XmlElement(name = "client-port",type = int.class)
	public int getClientPort() {
		return clientPort;
	}

	@Override
	@XmlElement(name = "echo-request",type = int.class)
	public int getEchoRequestInterval() {
		return echoRequestInterval;
	}

	@Override
	@XmlElement(name = "node-alive-request",type = boolean.class)
	public boolean getNodeAliveRequest() {
		return nodeAliveReq;
	}

	@Override
	@XmlElement(name = "request-expiry-time",type = long.class)
	public long getRequestExpiryTime() {
		return reqExpiryTime;
	}

	@Override
	@XmlElement(name = "file-location",type = String.class)
	public String getFileLocation() {
		return fileLocation;
	}

	@Override
	@XmlElement(name = "file-name",type = String.class)
	public String getFileName() {
		return fileName;
	}

	@Override
	@XmlElement(name = "rolling-type",type = int.class)
	public int getRollingType() {
		return rollingType;
	}

	@Override
	@XmlElement(name ="rolling-unit", type =long.class)
	public long getRollingUnit() {
		return rollingUnit;
	}
	@Override
	@XmlElement(name = "redirection-ip",type = String.class)
	public String getRedirectionIP() {
		return redirectionIP;
	}

	@Override
	@XmlElement(name = "request-retry",type = int.class)
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
	public void setEchoRequestInterval(int value) {
		echoRequestInterval = value;
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
		out.println("Echo request: " + echoRequestInterval);
		out.println("Initiate Node Alive request: " + nodeAliveReq);
		out.println("Request expiry timer: " + reqExpiryTime);
		out.println("Request Retry: " + requestRetry);
		out.println("Redirection IP: " + redirectionIP);
		out.println("File Name: " + fileName);
		out.println("File sequence Enabled: " + isSequenceRequired);
		out.println("Sequence Range: " + minSequenceRange + "-" + maxSequenceRange);
		out.println("File Location: " + fileLocation);
		out.println("File rolling- Type: " + rollingType + " Unit: " + rollingUnit);
		return stringBuffer.toString();

	}

	@Override
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;

	}

	@Override
	public void setRedirectionIP(String redirectionIP) {
		this.redirectionIP = redirectionIP;
	}

	@Override
	public void setRequestRetry(int requestRetry) {
		this.requestRetry = requestRetry;
	}

	@Override
	public void setRollingType(int rollingType) {
		this.rollingType = rollingType;
	}

	@Override
	public void setRollingUnit(int rollingUnit) {
		this.rollingUnit = rollingUnit;
	}

	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;		
	}

	@Override
	@XmlElement(name = "file-sequence",type = boolean.class)
	public boolean getIsFileSequenceRequired() {
		return isSequenceRequired;
	}

	@Override
	public void setIsFileSequenceRequired(boolean value) {
		isSequenceRequired = value;
	}

	@Override
	@XmlTransient
	public int getMaxSequenceRange() {
		return maxSequenceRange;
	}

	@Override
	@XmlTransient
	public int getMinSequenceRange() {
		return minSequenceRange;
	}

	@Override
	public void setMaxSequenceRange(int value) {
		maxSequenceRange = value;
	}

	@Override
	public void setMinSequenceRange(int value) {
		minSequenceRange = value;
	}

}
