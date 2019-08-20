package com.elitecore.aaa.core.server.axixserver;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class WebServiceConfiguration {
	
	public static final String SERVER_CERTIFICATE_PROFILE_ID_NOT_SET_STR = "-1";
	
	private String ipAddress="0.0.0.0";
	private int port=8080;
	private String httpsIPAddress;
	private int httpsPort;
	private int maxSession=10;
	private int threadPoolSize=10;
	private boolean isEnabled;
	private String serviceAddress ="0.0.0.0:8080";
	private String httpsServiceAddress;
	private String serverCertificateProfileId = SERVER_CERTIFICATE_PROFILE_ID_NOT_SET_STR;

	public  WebServiceConfiguration() {
		//required by Jaxb.
	}
	
	@XmlTransient
	public String getHttpsIPAddress() {
		return httpsIPAddress;
	}

	public void setHttpsIPAddress(String httpsIPAddress) {
		this.httpsIPAddress = httpsIPAddress;
	}

	@XmlTransient
	public int getHttpsPort() {
		return httpsPort;
	}

	public void setHttpsPort(int httpsPort) {
		this.httpsPort = httpsPort;
	}

	@XmlElement(name = "service-address",type = String.class,defaultValue ="0.0.0.0:8080")
	public String getServiceAddress() {
		return serviceAddress;
	}
	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}
	
	@XmlElement(name = "https-service-address",type = String.class)
	public String getHttpsServiceAddress() {
		return httpsServiceAddress;
	}
	public void setHttpsServiceAddress(String httpServiceAddress) {
		this.httpsServiceAddress = httpServiceAddress;
	}

	@XmlTransient
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress){
		this.ipAddress = ipAddress;
	}
	
	@XmlTransient
	public int getPort() {
		return port;
	}
	public void setPort(int port){
		this.port = port;
	}
	
	@XmlElement(name = "max-session",type = int.class,defaultValue ="10")
	public int getMaxSession() {
		return maxSession;
	}
	public void setMaxSession(int maxSession){
		this.maxSession = maxSession;
	}

	@XmlElement(name = "thread-pool-size",type = int.class,defaultValue ="10")
	public int getThreadPoolSize() {
		return threadPoolSize;
	}
	public void setThreadPoolSize(int threadPoolSize){
		this.threadPoolSize = threadPoolSize;
	}
	@XmlElement(name = "web-service-enabled", type = boolean.class, defaultValue = "false")
	public boolean getIsEnabled() {
		return this.isEnabled;
	}
	public void setIsEnabled(boolean isEnabled){
		this.isEnabled = isEnabled;
	}
	
	@XmlElement(name = "server-certificate-id", type = String.class, defaultValue = SERVER_CERTIFICATE_PROFILE_ID_NOT_SET_STR)
	public String getServerCertificateProfileId() {
		return serverCertificateProfileId;
	}
	
	public void setServerCertificateProfileId(String serverCertificateProfileId) {
		this.serverCertificateProfileId = serverCertificateProfileId;
	}
}