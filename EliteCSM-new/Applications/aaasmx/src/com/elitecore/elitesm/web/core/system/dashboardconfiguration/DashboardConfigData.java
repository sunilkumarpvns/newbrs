package com.elitecore.elitesm.web.core.system.dashboardconfiguration;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class DashboardConfigData extends BaseData{
	private String dashboardConfigId;
	private String databaseId;
	private Long maxTabs;
	private Long maxWebSockets;
	private Long maxConcurrentAccess;
	private Long maxWidgets;
	private String dataSourceName;
	
	public String getDashboardConfigId() {
		return dashboardConfigId;
	}
	public void setDashboardConfigId(String dashboardConfigId) {
		this.dashboardConfigId = dashboardConfigId;
	}
	public String getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}
	public Long getMaxTabs() {
		return maxTabs;
	}
	public void setMaxTabs(Long maxTabs) {
		this.maxTabs = maxTabs;
	}
	public Long getMaxWebSockets() {
		return maxWebSockets;
	}
	public void setMaxWebSockets(Long maxWebSockets) {
		this.maxWebSockets = maxWebSockets;
	}
	public Long getMaxConcurrentAccess() {
		return maxConcurrentAccess;
	}
	public void setMaxConcurrentAccess(Long maxConcurrentAccess) {
		this.maxConcurrentAccess = maxConcurrentAccess;
	}
	public Long getMaxWidgets() {
		return maxWidgets;
	}
	public void setMaxWidgets(Long maxWidgets) {
		this.maxWidgets = maxWidgets;
	}
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
}
