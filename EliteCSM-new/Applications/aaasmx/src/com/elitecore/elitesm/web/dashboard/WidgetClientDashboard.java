package com.elitecore.elitesm.web.dashboard;

import java.util.List;

import com.elitecore.elitesm.web.dashboard.json.WidgetJSONData;
public class WidgetClientDashboard {
	
	private String layout="layout2";
	private List<WidgetJSONData> data;
	
	public String getLayout() {
		return layout;
	}
	public void setLayout(String layout) {
		this.layout = layout;
	}
	public List<WidgetJSONData> getData() {
		return data;
	}
	public void setData(List<WidgetJSONData> data) {
		this.data = data;
	}
	
}
