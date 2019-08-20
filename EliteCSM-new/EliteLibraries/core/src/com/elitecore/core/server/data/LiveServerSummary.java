package com.elitecore.core.server.data;

import java.util.Date;
import java.util.List;

import com.elitecore.core.commons.scheduler.EliteTaskData;
import com.elitecore.core.services.data.LiveServiceSummary;

public class LiveServerSummary {

	private String id;
	private String name;
	private String version;
	private Date serverStartupTime;
	private Date configurationReloadTime;
	private Date cacheReloadTime;
	private Date softRestartTime;
	private List<LiveServiceSummary> serviceSummaryList;
	private List<EliteTaskData> internalTasks;
	
	public Date getCacheReloadTime() {
		return cacheReloadTime;
	}

	public void setCacheReloadTime(Date cacheReloadTime) {
		this.cacheReloadTime = cacheReloadTime;
	}

	public Date getConfigurationReloadTime() {
		return configurationReloadTime;
	}

	public void setConfigurationReloadTime(Date configurationReloadTime) {
		this.configurationReloadTime = configurationReloadTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getServerStartupTime() {
		return serverStartupTime;
	}

	public void setServerStartupTime(Date serverStartupTime) {
		this.serverStartupTime = serverStartupTime;
	}

	public Date getSoftRestartTime() {
		return softRestartTime;
	}

	public void setSoftRestartTime(Date softRestartTime) {
		this.softRestartTime = softRestartTime;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<LiveServiceSummary> getServiceSummaryList() {
		return serviceSummaryList;
	}

	public void setServiceSummaryList(List<LiveServiceSummary> serviceSummaryList) {
		this.serviceSummaryList = serviceSummaryList;
	}

	public List<EliteTaskData> getInternalTasks() {
		return internalTasks;
	}

	public void setInternalTasks(List<EliteTaskData> internalTasks) {
		this.internalTasks = internalTasks;
	}
}
