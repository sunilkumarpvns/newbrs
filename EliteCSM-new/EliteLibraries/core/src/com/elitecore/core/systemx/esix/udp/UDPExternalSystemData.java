/**
 * 
 */
package com.elitecore.core.systemx.esix.udp;

import java.net.InetAddress;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.util.url.URLData;

/**
 * @author pulin
 *
 */
public abstract class UDPExternalSystemData {

	public static final int GENERIC_ESI_TYPE = 0;
	private int id;
	
	/* This field is used for ESI UUID*/
	private String uuid;
	
	private InetAddress ipAddress;
	private int port;
	
	private int communicationTimeout = 0;
	
	private int minLocalPort = 10; //the default value changed from 2 to 10
	
	private int expiredRequestLimitCount = 10;
	
	private int retryLimit = 1;
	
	private int statusCheckDuration = 120;
	
	private String name;

	private int esiType = GENERIC_ESI_TYPE ;
	private String strIpAddress;
	
	public UDPExternalSystemData(){
		
	}
	
	public UDPExternalSystemData(String uuid,
								 String targetSystemIPAddress,
								 int expCount,
								 int communicationTimeout,
								 int minLocalPort,
								 int retryLimit,
								 int statusCheckDuration,
								 String name,
								 int esiType) {
		super();
		this.uuid = uuid;
		this.strIpAddress = targetSystemIPAddress;
		this.communicationTimeout = communicationTimeout;
		this.expiredRequestLimitCount = expCount;
		this.minLocalPort = minLocalPort;
		this.retryLimit = retryLimit;
		this.statusCheckDuration = statusCheckDuration;
		this.name = name;
		this.esiType = esiType;
	}
	
	@XmlTransient
	public InetAddress getIPAddress() {
		return ipAddress;
	}
	
	public void setIPAddress(InetAddress ipAddress){
		this.ipAddress = ipAddress;
	}

	@XmlTransient
	public int getPort() {
		return port;
	}
	public void setPort(int port){
		this.port = port;
	}

	@XmlElement(name = "timeout",type = int.class,defaultValue ="0")
	public int getCommunicationTimeout() {
		return communicationTimeout;
	}
	
	public void setCommunicationTimeout(int communicationTimeout){
		this.communicationTimeout = communicationTimeout;
	}

	@XmlElement(name = "minimum-local-port",type = int.class,defaultValue ="10")
	public int getMinLocalPort() {
		return minLocalPort;
	}
	
	public void setMinLocalPort(int localPort){
		this.minLocalPort = localPort;
	}

	@XmlElement(name = "expired-request-limit-count",type = int.class,defaultValue ="10")
	public int getExpiredRequestLimitCount() {
		return expiredRequestLimitCount;
	}
	
	public void setExpiredRequestLimitCount(int expiredReqLimitCount){
		this.expiredRequestLimitCount = expiredReqLimitCount;
	}
	
	@XmlElement(name = "name",type = String.class)
	public String getName() {
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	
	@XmlElement(name = "esi-type",type = int.class,defaultValue ="0")
	public int getEsiType() {
		return esiType;
	}
	
	public void setEsiType(int esiType){
		this.esiType = esiType;
	}
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("Name : ");
		strBuilder.append(name);
		strBuilder.append("\n");
		strBuilder.append("Address : ");
		strBuilder.append(getIPAddress().getHostAddress());
		strBuilder.append(":");
		strBuilder.append(getPort());
		strBuilder.append("\n");
		strBuilder.append("Communication Timeout : ");
		strBuilder.append(getCommunicationTimeout());
		strBuilder.append("\n");
		strBuilder.append("Minimum Local Port : ");
		strBuilder.append(getMinLocalPort());
		strBuilder.append("\n");
		strBuilder.append("Expired Request Limit Count : ");
		strBuilder.append(getExpiredRequestLimitCount());
		strBuilder.append("\n");
		strBuilder.append("Retry Count : ");
		strBuilder.append(getRetryLimit());
		strBuilder.append("\n");
		strBuilder.append("Status Check  Duration : ");
		strBuilder.append(getStatusCheckDuration());
		strBuilder.append("\n");
		strBuilder.append("ESI Type : ");
		strBuilder.append(getEsiType());
		strBuilder.append("\n");
		return strBuilder.toString();
	}

	@XmlTransient
	public int getID() {
		return id;
	}
	public void setID(int id){
		this.id = id;
	}
	
	@XmlElement(name = "id",type = String.class)
	public String getUUID() {
		return uuid;
	}
	
	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public int hashCode() {
		return ipAddress.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		try{

			if(obj == null) {
				return false;
			}

			UDPExternalSystemData externalSystem = (UDPExternalSystemData) obj;

			return ipAddress.equals(externalSystem.getIPAddress())
					&& port == externalSystem.getPort();

		}catch(ClassCastException cce){
			LogManager.ignoreTrace(cce);
			return false;
		}
	}

	/**
	 * Returns Retry Count for Timedout requests
	 */
	@XmlElement(name = "retry-count",type = int.class)
	public int getRetryLimit() {
		return retryLimit;
	}
	
	public void setRetryLimit(int retryLimit){
		this.retryLimit = retryLimit;
	}

	/**
	 * Returns interval in seconds used to scan target system for aliveness
	 * @return interval in seconds 
	 */
	@XmlElement(name = "status-check-duration",type = int.class)
	public int getStatusCheckDuration() {
		return statusCheckDuration;
	}
	
	public void setStatusCheckDuration(int statuCheckDuration){
		this.statusCheckDuration = statuCheckDuration;
	}
	/**
	 * 
	 * @return True If this ES is to be treated as Always Alive else false
	 */
	@XmlElement(name = "is-alive",type = boolean.class)
	public boolean getIsAlwaysAlive(){
		return (expiredRequestLimitCount <= ESCommunicator.ALWAYS_ALIVE);
	}

	@XmlElement(name = "address",type = String.class)
	public String getStringIpAddress(){
		return strIpAddress;
	}
	public void setStringIpAddress(String strIpAddress){
		this.strIpAddress = strIpAddress;
	}
	
	@XmlTransient
	public abstract URLData getURL();
}