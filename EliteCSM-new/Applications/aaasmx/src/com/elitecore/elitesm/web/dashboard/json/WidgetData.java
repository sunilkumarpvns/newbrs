package com.elitecore.elitesm.web.dashboard.json;

import java.util.Comparator;
import java.util.List;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.web.dashboard.widget.configuration.WidgetConfigData;

public class WidgetData extends BaseData implements Comparator<WidgetData>{
	private String widgetId;
	private String widgetTemplateId;
	private String dashboardId;
	private String orderId;
	
	private WidgetTemplateData widgteTemplateData;
	private WidgetOrderData widgetOrderData;
	private List<WidgetConfigData> widgetConfigDataList;
	
	public WidgetTemplateData getWidgteTemplateData() {
		return widgteTemplateData;
	}
	public void setWidgteTemplateData(WidgetTemplateData widgteTemplateData) {
		this.widgteTemplateData = widgteTemplateData;
	}
	public WidgetOrderData getWidgetOrderData() {
		return widgetOrderData;
	}
	public void setWidgetOrderData(WidgetOrderData widgetOrderData) {
		this.widgetOrderData = widgetOrderData;
	}
	public String getWidgetId() {
		return widgetId;
	}
	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}
	public String getWidgetTemplateId() {
		return widgetTemplateId;
	}
	public void setWidgetTemplateId(String widgetTemplateId) {
		this.widgetTemplateId = widgetTemplateId;
	}
	public String getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(String dashboardId) {
		this.dashboardId = dashboardId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	@Override
	public int compare(WidgetData o1, WidgetData o2) {
		if(o1.widgetOrderData.getOrderNumber() > o2.widgetOrderData.getOrderNumber()){
			return 1;
		}else{
			return -1;
		}
	}
	
	public List<WidgetConfigData> getWidgetConfigDataList() {
		return widgetConfigDataList;
	}
	public void setWidgetConfigDataList(List<WidgetConfigData> widgetConfigDataList) {
		this.widgetConfigDataList = widgetConfigDataList;
	}
}
