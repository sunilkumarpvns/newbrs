package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

public class AuthorizationHandler {
	
	private String handlerName;
	private String wimaxEnabled;
	private String threeGPPEnabled;
	private int defaultSessionTimeout;
	private String rejectOnCheckItemNotFound;
	private String rejectOnRejectItemNotFound;
	private String actionOnPolicyNotFound;
	private String gracePolicyId;
	private int orderNumber;
	private String isAdditional;
	private String isHandlerEnabled = "false";
	
	public String getWimaxEnabled() {
		return wimaxEnabled;
	}
	public void setWimaxEnabled(String wimaxEnabled) {
		this.wimaxEnabled = wimaxEnabled;
	}
	public String getThreeGPPEnabled() {
		return threeGPPEnabled;
	}
	public void setThreeGPPEnabled(String threeGPPEnabled) {
		this.threeGPPEnabled = threeGPPEnabled;
	}
	public int getDefaultSessionTimeout() {
		return defaultSessionTimeout;
	}
	public void setDefaultSessionTimeout(int defaultSessionTimeout) {
		this.defaultSessionTimeout = defaultSessionTimeout;
	}
	public String getRejectOnCheckItemNotFound() {
		return rejectOnCheckItemNotFound;
	}
	public void setRejectOnCheckItemNotFound(String rejectOnCheckItemNotFound) {
		this.rejectOnCheckItemNotFound = rejectOnCheckItemNotFound;
	}
	public String getRejectOnRejectItemNotFound() {
		return rejectOnRejectItemNotFound;
	}
	public void setRejectOnRejectItemNotFound(String rejectOnRejectItemNotFound) {
		this.rejectOnRejectItemNotFound = rejectOnRejectItemNotFound;
	}
	public String getActionOnPolicyNotFound() {
		return actionOnPolicyNotFound;
	}
	public void setActionOnPolicyNotFound(String actionOnPolicyNotFound) {
		this.actionOnPolicyNotFound = actionOnPolicyNotFound;
	}
	public String getGracePolicyId() {
		return gracePolicyId;
	}
	public void setGracePolicyId(String gracePolicyId) {
		this.gracePolicyId = gracePolicyId;
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
	
	@Override
	public String toString() {
		return "----------------AuthorizationHandler------------------------\n  wimaxEnabled = "
				+ wimaxEnabled
				+ "\n  threeGPPEnabled = "
				+ threeGPPEnabled
				+ "\n  defaultSessionTimeout = "
				+ defaultSessionTimeout
				+ "\n  rejectOnCheckItemNotFound = "
				+ rejectOnCheckItemNotFound
				+ "\n  rejectOnRejectItemNotFound = "
				+ rejectOnRejectItemNotFound
				+ "\n  actionOnPolicyNotFound = "
				+ actionOnPolicyNotFound
				+ "\n  gracePolicyId = "
				+ gracePolicyId
				+ "\n  orderNumber = "
				+ orderNumber
				+ "\n  isAdditional = "
				+ isAdditional
				+ "\n----------------AuthorizationHandler-------------------------\n";
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