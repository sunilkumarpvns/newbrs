package com.elitecore.netvertexsm.datamanager.servermgr.alert.data;

public class AlertTrapListenerData extends BaseAlertListener{
	private long trapListenerId;
	private String trapServer;
	private String trapVersion;
	private String community;
	private String advanceTrap;
	private Byte snmpRequestType;
	private Integer timeout;
	private Byte retryCount;
	
	public String getAdvanceTrap() {
		return advanceTrap;
	}
	public void setAdvanceTrap(String advanceTrap) {
		this.advanceTrap = advanceTrap;
	}
	public long getTrapListenerId() {
		return trapListenerId;
	}
	public void setTrapListenerId(long trapListenerId) {
		this.trapListenerId = trapListenerId;
	}
	public String getTrapServer() {
		return trapServer;
	}
	public void setTrapServer(String trapServer) {
		this.trapServer = trapServer;
	}
	public String getTrapVersion() {
		return trapVersion;
	}
	public void setTrapVersion(String trapVersion) {
		this.trapVersion = trapVersion;
	}
	public String getCommunity() {
		return community;
	}
	public void setCommunity(String community) {
		this.community = community;
	}
	public Byte getSnmpRequestType() {
		return snmpRequestType;
	}
	public void setSnmpRequestType(Byte snmpRequestType) {
		this.snmpRequestType = snmpRequestType;
	}
	public Byte getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(Byte retryCount) {
		this.retryCount = retryCount;
	}
	public Integer getTimeout() {
		return timeout;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
 
 
}
