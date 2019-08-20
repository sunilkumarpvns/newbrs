package com.elitecore.elitesm.web.dashboard.json;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;


public class WidgetOrderData extends BaseData{

	private String orderId;
	private String widgetId;
	private Long layout;
	private Long columnNumber;
	private Long orderNumber;
	private String dashboardId;
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getWidgetId() {
		return widgetId;
	}
	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}
	public Long getLayout() {
		return layout;
	}
	public void setLayout(Long layout) {
		this.layout = layout;
	}
	public Long getColumnNumber() {
		return columnNumber;
	}
	public void setColumnNumber(Long columnNumber) {
		this.columnNumber = columnNumber;
	}
	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(String dashboardId) {
		this.dashboardId = dashboardId;
	}
}
