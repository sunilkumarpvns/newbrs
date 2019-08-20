package com.elitecore.elitesm.web.history;

import java.util.List;

public class DatabaseHistoryData {
	
	private String name;
	private String lastupdatetime;
	private String userName;
	private List<HistoryData> historyData;
	private String systemAuditId;
	private String ipAddress;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLastupdatetime() {
		return lastupdatetime;
	}
	public void setLastupdatetime(String lastupdatetime) {
		this.lastupdatetime = lastupdatetime;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public List<HistoryData> getHistoryData() {
		return historyData;
	}
	public void setHistoryData(List<HistoryData> historyData) {
		this.historyData = historyData;
	}
	public String getSystemAuditId() {
		return systemAuditId;
	}
	public void setSystemAuditId(String systemAuditId) {
		this.systemAuditId = systemAuditId;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
}
