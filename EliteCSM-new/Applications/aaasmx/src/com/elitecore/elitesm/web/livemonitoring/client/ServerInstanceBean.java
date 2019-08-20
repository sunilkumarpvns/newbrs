package com.elitecore.elitesm.web.livemonitoring.client;

import java.io.Serializable;

public class ServerInstanceBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String netServerId;
	private String serverCode;
	private String serverName;
	private String serverType;
	private String serverIP;
	private String alias;
	
	private int jmxPort;

	public String getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}
	public String getServerCode() {
		return serverCode;
	}
	public void setServerCode(String serverCode) {
		this.serverCode = serverCode;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerType() {
		return serverType;
	}
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	public String getServerIP() {
		return serverIP;
	}
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	public int getJmxPort() {
		return jmxPort;
	}
	public void setJmxPort(int jmxPort) {
		this.jmxPort = jmxPort;
	}
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("\n------------ServerInstanceBean-----------------");
		buffer.append("\nserverIP    =" +serverIP);
		buffer.append("\njmxPort     =" +jmxPort);
		buffer.append("\nnetServerId =" +netServerId);                                     
		buffer.append("\nserverCode  =" +serverCode);                                     
		buffer.append("\nserverName  =" +serverName);                                     
		buffer.append("\nserverType  =" +serverType);
		buffer.append("\nalias       =" +alias);     
		buffer.append("\n----------------------------------------------------");
		
		return buffer.toString();
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
}
