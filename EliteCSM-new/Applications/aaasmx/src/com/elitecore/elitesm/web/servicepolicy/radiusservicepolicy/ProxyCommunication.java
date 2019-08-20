package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.util.List;

public class ProxyCommunication {
	
	private String handlerName;
	private int orderNumber;
	private String isAdditional;
	private List<ProxyCommunicationData> proxyCommunicationList;
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
	public List<ProxyCommunicationData> getProxyCommunicationList() {
		return proxyCommunicationList;
	}
	public void setProxyCommunicationList(
			List<ProxyCommunicationData> proxyCommunicationList) {
		this.proxyCommunicationList = proxyCommunicationList;
	}
	
	@Override
	public String toString() {
		return "----------------ProxyCommunication------------------------\n  orderNumber = "
				+ orderNumber
				+ "\n  isAdditional = "
				+ isAdditional
				+ "\n  proxyCommunicationList = "
				+ proxyCommunicationList
				+ "\n----------------ProxyCommunication-------------------------\n";
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
