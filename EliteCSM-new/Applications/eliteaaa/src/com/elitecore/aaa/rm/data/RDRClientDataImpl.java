package com.elitecore.aaa.rm.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class RDRClientDataImpl implements RDRClientData {

	
	String clientIP = "0.0.0.0";
	int clientPort = 3386;
	boolean nodeAliveRequest=false;
	int echoRequestInterval=0;
	int requestExpiryTime=8000;
	int requestRetry = 3;
	String redirectionIP = null;
	String fileName = "defaultFile.rdr";
	String fileLocation ;
	int rollingType = 1;
	int rollingUnit = 5;
	
	public RDRClientDataImpl() {
		// TODO Auto-generated constructor stub
	}
	
	public void setClientIP(String ip) {
		this.clientIP=ip;
	}

	public void setClientPort(int port) {
		this.clientPort=port;
	}
	
	public void setFileLocation(String loc) {
		this.fileLocation = loc;
	}

	public void setRollingType(int type) {
		this.rollingType=type;
	}

	public void setRollingUnit(long unit) {
		this.rollingUnit=(int) unit;
	}

	@XmlElement(name ="client-ip",type =String.class)
	public String getClientIP() {
		return clientIP;
	}

	@Override
	@XmlTransient
	public int getClientPort() {
		return clientPort;
	}

	@Override
	@XmlElement(name ="file-location",type = String.class)
	public String getFileLocation() {
		return fileLocation;
	}

	@Override
	@XmlElement(name ="rolling-type",type =int.class,defaultValue = "1")
	public int getRollingType() {
		return rollingType;
	}

	@Override
	@XmlElement(name ="rolling-unit",type =long.class)
	public long getRollingUnit() {
		return rollingUnit;
	}
	
	public void setFileName(String fileName) {
		this.fileName=fileName;
	}

	@Override
	@XmlElement(name ="file-name",type =String.class,defaultValue = "defaultFile.rdr")
	public String getFileName() {
		return fileName;
	}
}
