package com.elitecore.elitesm.web.core.system.dashboardconfiguration.forms;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;
import com.elitecore.elitesm.web.dashboard.json.WidgetTemplateData;

public class DashboardConfigurationForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String action;
	private List databaseDSList;
	private String databaseId;
	private Long maxTabs=3L;
	private Long maxWebSockets=50L;
	private Long maxConcurrentAccess=2L;
	private Long maxWidgets=7L;
	private String dashboardConfigId;
	private List<WidgetTemplateData> widgetTemplateDataList;
	private List<WidgetTemplateData> customWidgetList;
	
	private String widgetName;
	private String widgetThumbnail;
	private String widgetJsp;
	private String widgetConfJsp;
	private String widgetGroovy;
	private String widgetDesc;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List getDatabaseDSList() {
		return databaseDSList;
	}
	public void setDatabaseDSList(List databaseDSList) {
		this.databaseDSList = databaseDSList;
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
	public String getDashboardConfigId() {
		return dashboardConfigId;
	}
	public void setDashboardConfigId(String dashboardConfigId) {
		this.dashboardConfigId = dashboardConfigId;
	}
	public List<WidgetTemplateData> getWidgetTemplateDataList() {
		return widgetTemplateDataList;
	}
	public void setWidgetTemplateDataList(List<WidgetTemplateData> widgetTemplateDataList) {
		this.widgetTemplateDataList = widgetTemplateDataList;
	}
	public String getWidgetName() {
		return widgetName;
	}
	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}
	public String getWidgetThumbnail() {
		return widgetThumbnail;
	}
	public void setWidgetThumbnail(String widgetThumbnail) {
		this.widgetThumbnail = widgetThumbnail;
	}
	public String getWidgetJsp() {
		return widgetJsp;
	}
	public void setWidgetJsp(String widgetJsp) {
		this.widgetJsp = widgetJsp;
	}
	public String getWidgetConfJsp() {
		return widgetConfJsp;
	}
	public void setWidgetConfJsp(String widgetConfJsp) {
		this.widgetConfJsp = widgetConfJsp;
	}
	public String getWidgetGroovy() {
		return widgetGroovy;
	}
	public void setWidgetGroovy(String widgetGroovy) {
		this.widgetGroovy = widgetGroovy;
	}
	public String getWidgetDesc() {
		return widgetDesc;
	}
	public void setWidgetDesc(String widgetDesc) {
		this.widgetDesc = widgetDesc;
	}
	public List<WidgetTemplateData> getCustomWidgetList() {
		return customWidgetList;
	}
	public void setCustomWidgetList(List<WidgetTemplateData> customWidgetList) {
		this.customWidgetList = customWidgetList;
	}
}
