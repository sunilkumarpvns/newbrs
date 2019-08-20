package com.elitecore.elitesm.datamanager.dashboard.data;

import java.io.Serializable;
import java.util.Set;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.web.dashboard.json.WidgetData;

public class DashboardData extends BaseData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String dashboardId;
	private String dashboardName;
	private String dashboardDesc;
	private String startFrom;
	private String addShares;
	private String author;
	private Long orderNumber;
	private Set<WidgetData> widgetSet;
	
	public String getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(String dashboardId) {
		this.dashboardId = dashboardId;
	}
	public String getDashboardName() {
		return dashboardName;
	}
	public void setDashboardName(String dashboardName) {
		this.dashboardName = dashboardName;
	}
	public String getDashboardDesc() {
		return dashboardDesc;
	}
	public void setDashboardDesc(String dashboardDesc) {
		this.dashboardDesc = dashboardDesc;
	}
	public String getStartFrom() {
		return startFrom;
	}
	public void setStartFrom(String startFrom) {
		this.startFrom = startFrom;
	}
	public String getAddShares() {
		return addShares;
	}
	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	public void setAddShares(String addShares) {
		this.addShares = addShares;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Set<WidgetData> getWidgetSet() {
		return widgetSet;
	}
	public void setWidgetSet(Set<WidgetData> widgetSet) {
		this.widgetSet = widgetSet;
	}
	
	
}
