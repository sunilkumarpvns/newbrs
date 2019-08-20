package com.elitecore.aaa.radius.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.config.Password;

@XmlType(propOrder={})
public class FileTransferDetail{
	
	private String allocatingProtocol="Local";
	private String ipAddress;
	private int port;
	private String destinationLocation;
	private String usrName;
	private Password password;
	private String postOperation="Archive";
	private String archiveLocations;
	private int failOverTime=3;
	
	public FileTransferDetail() {
		//Required by JAXB
		password = new Password();
	}
	
	public void setAllocatingProtocol(String allocatingProtocol) {
		this.allocatingProtocol = allocatingProtocol;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void setDestinationLocation(String destinationLocation) {
		this.destinationLocation = destinationLocation;
	}
	public void setUsrName(String usrName) {
		this.usrName = usrName;
	}
	public void setPassword(String password) {
		this.password.setPassword(password);
	}
	public void setPostOperation(String postOperation) {
		this.postOperation = postOperation;
	}
	public void setArchiveLocations(String archiveLocations) {
		this.archiveLocations = archiveLocations;
	}
	public void setFailOverTime(int failOverTime) {
		this.failOverTime = failOverTime;
	}
	@XmlElement(name ="allocation-protocol",type =String.class,defaultValue ="Local")
	public String getAllocatingProtocol() {
		return this.allocatingProtocol;
	}

	@XmlElement(name ="ip-address",type =String.class)
	public String getIpAddress() {
		return this.ipAddress;
	}

	@XmlElement(name ="port",type =int.class)
	public int getPort() {
		return this.port;
	}

	@XmlElement(name ="destination-location",type =String.class)
	public String getDestinationLocation() {
		return this.destinationLocation;
	}

	@XmlElement(name ="user-name",type =String.class)
	public String getUsrName() {
		return this.usrName;
	}

	@XmlElement(name ="password",type =String.class)
	public String getPassword() {
		return this.password.getPassword();
	}
	
	public String getPlainTextPassword(){
		return this.password.getPlainTextPassword();
	}

	@XmlElement(name ="post-operation",type =String.class,defaultValue ="Archive")
	public String getPostOperation() {
		return this.postOperation;
	}

	@XmlElement(name ="archive-locations",type =String.class)
	public String getArchiveLocations() {
		return this.archiveLocations;
	}

	@XmlElement(name ="failover-time",type =int.class,defaultValue ="3")
	public int getFailOverTime() {
		return this.failOverTime;
	}

}