package com.elitecore.elitesm.web.dashboard.widget.impl;

import com.elitecore.elitesm.web.dashboard.widget.WidgetDataSender;
import com.elitecore.elitesm.web.dashboard.widget.WidgetRequest;


public class WidgetRequestImpl implements WidgetRequest {
	private String socketID;
	private String requestData;
	private WidgetDataSender widgetDataSender;
	
	public WidgetRequestImpl(String id, String requestData, WidgetDataSender widgetDataSender) {
		this.socketID = id;
		this.requestData = requestData;
		this.widgetDataSender = widgetDataSender;
	}
	
	@Override
	public String getSocketID() {
		return socketID;
	}

	@Override
	public String getRequestData() {
		return requestData;
	}

	@Override
	public WidgetDataSender getWidgetDataSender() {
		return widgetDataSender;
	}

	@Override
	public String toString() {
		return "WidgetRequest [id=" + socketID + "\n requestData=" + requestData
				+ "]";
	}


	
	
}
