package com.elitecore.core.util.mbean.data.live;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;


public class EliteNetServerDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String identification;
	private String version;
	private Timestamp serverStartUpTime;	
	private Timestamp serverReloadTime;	
	private Timestamp cacheReloadTime;	
	private Timestamp softRestartTime;
	private List serviceSummaryList; //NOSONAR
	private List internalTasks;	//NOSONAR
	
	public String getIdentification() {
		return identification;
	}
	public void setIdentification(String identification) {
		this.identification = identification;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Timestamp getCacheReloadTime() {
		return cacheReloadTime;
	}
	public void setCacheReloadTime(Timestamp cacheReloadTime) {
		this.cacheReloadTime = cacheReloadTime;
	}
	public Timestamp getServerReloadTime() {
		return serverReloadTime;
	}
	public void setServerReloadTime(Timestamp serverReloadTime) {
		this.serverReloadTime = serverReloadTime;
	}
	public Timestamp getServerStartUpTime() {
		return serverStartUpTime;
	}
	public void setServerStartUpTime(Timestamp serverStartUpTime) {
		this.serverStartUpTime = serverStartUpTime;
	}
	public List getServiceSummaryList() {
		return serviceSummaryList;
	}
	public void setServiceSummaryList(List serviceSummaryList) {
		this.serviceSummaryList = serviceSummaryList;
	}
	public Timestamp getSoftRestartTime() {
		return softRestartTime;
	}
	public void setSoftRestartTime(Timestamp softRestartTime) {
		this.softRestartTime = softRestartTime;
	}
	public List getInternalTasks() {
		return internalTasks;
	}
	public void setInternalTasks(List internalTasks) {
		this.internalTasks = internalTasks;
	}	
}
