package com.elitecore.elitesm.web.plugins;

import java.util.List;

/**
 * @author nayana.rathod
 *
 */
public class GroovyHistroryData {
	
	private String lastUpdatedTime;
	private String userName;
	private String ipAddress;
	private String history;
	private List<GroovyHistoryContent> contentList;
	
	public String getLastUpdatedTime() {
		return lastUpdatedTime;
	}
	public void setLastUpdatedTime(String lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getHistory() {
		return history;
	}
	public void setHistory(String history) {
		this.history = history;
	}
	public List<GroovyHistoryContent> getContent() {
		return contentList;
	}
	public void setContent(List<GroovyHistoryContent> contentList) {
		this.contentList = contentList;
	}
}
