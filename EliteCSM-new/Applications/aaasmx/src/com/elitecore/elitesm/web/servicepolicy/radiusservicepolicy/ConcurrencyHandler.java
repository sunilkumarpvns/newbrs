package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

public class ConcurrencyHandler {
	
	private String handlerName;
	private String ruleset;
	private String sessionManagerId;
	private int orderNumber;
	private String isAdditional;
	private String isHandlerEnabled = "false";
	
	public String getSessionManagerId() {
		return sessionManagerId;
	}
	public void setSessionManagerId(String sessionManagerId) {
		this.sessionManagerId = sessionManagerId;
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
	public String getRuleset() {
		return ruleset;
	}
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}
	public String getIsHandlerEnabled() {
		return isHandlerEnabled;
	}
	public void setIsHandlerEnabled(String isHandlerEnabled) {
		this.isHandlerEnabled = isHandlerEnabled;
	}
	
	@Override
	public String toString() {
		return "ConcurrencyHandler [ruleset=" + ruleset + ", sessionManagerId="
				+ sessionManagerId + ", orderNumber=" + orderNumber
				+ ", isAdditional=" + isAdditional + ", isHandlerEnabled="
				+ isHandlerEnabled + "]";
	}
	public String getHandlerName() {
		return handlerName;
	}
	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}
}
