package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.util.List;

public class PluginHandler {
	
	private String handlerName;
	private List<PluginDetails> lstPluginDetails;
	private int orderNumber;
	private String isAdditional;
	private String isHandlerEnabled = "false";
	
	public List<PluginDetails> getLstPluginDetails() {
		return lstPluginDetails;
	}
	public void setLstPluginDetails(List<PluginDetails> lstPluginDetails) {
		this.lstPluginDetails = lstPluginDetails;
	}
	public int getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getIsAdditional() {
		return isAdditional;
	}
	public void setIsAdditional(String isAdditional) {
		this.isAdditional = isAdditional;
	}
	public String getIsHandlerEnabled() {
		return isHandlerEnabled;
	}
	public void setIsHandlerEnabled(String isHandlerEnabled) {
		this.isHandlerEnabled = isHandlerEnabled;
	}
	public String getHandlerName() {
		return handlerName;
	}
	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}
}
