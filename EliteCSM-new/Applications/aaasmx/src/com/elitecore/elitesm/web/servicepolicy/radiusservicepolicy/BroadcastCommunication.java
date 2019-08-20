package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.util.List;

public class BroadcastCommunication {
	
	private String handlerName;
	private int orderNumber;
	private String isAdditional;
	private List<BroadcastCommunicationData> broadcastCommunicationList;
	private String isHandlerEnabled = "false";
	
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
	public List<BroadcastCommunicationData> getBroadcastCommunicationList() {
		return broadcastCommunicationList;
	}
	public void setBroadcastCommunicationList(
			List<BroadcastCommunicationData> broadcastCommunicationList) {
		this.broadcastCommunicationList = broadcastCommunicationList;
	}
	
	@Override
	public String toString() {
		return "----------------BroadcastCommunication------------------------\n  orderNumber = "
				+ orderNumber
				+ "\n  isAdditional = "
				+ isAdditional
				+ "\n  broadcastCommunicationList = "
				+ broadcastCommunicationList
				+ "\n----------------BroadcastCommunication-------------------------\n";
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
