package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationHandler {
	
	private String handlerName;
	private List<String> selectedAuthMethodTypes = new ArrayList<String>();
	private String authHandlersString;
	private String eapConfigId;
	private String digestConfigId;
	private String userName;
	private String userNameResponseAttribs;
	private String userNameExpression;
	private int orderNumber;
	private String isAdditional;
	private String isHandlerEnabled = "false";
	
	public List<String> getSelectedAuthMethodTypes() {
		return selectedAuthMethodTypes;
	}
	public void setSelectedAuthMethodTypes(List<String> selectedAuthMethodTypes) {
		this.selectedAuthMethodTypes = selectedAuthMethodTypes;
	}
	public String getEapConfigId() {
		return eapConfigId;
	}
	public void setEapConfigId(String eapConfigId) {
		this.eapConfigId = eapConfigId;
	}
	public String getDigestConfigId() {
		return digestConfigId;
	}
	public void setDigestConfigId(String digestConfigId) {
		this.digestConfigId = digestConfigId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserNameResponseAttribs() {
		return userNameResponseAttribs;
	}
	public void setUserNameResponseAttribs(String userNameResponseAttribs) {
		this.userNameResponseAttribs = userNameResponseAttribs;
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
	public String getAuthHandlersString() {
		return authHandlersString;
	}
	public void setAuthHandlersString(String authHandlersString) {
		this.authHandlersString = authHandlersString;
		String[] authHandles = authHandlersString.split(",");
		for(String auth : authHandles) {
			selectedAuthMethodTypes.add(auth);
		}
	}
	@Override
	public String toString() {
		return "----------------AuthenticationHandler------------------------\n  selectedAuthMethodTypes = "
				+ selectedAuthMethodTypes
				+ "\n  eapConfigId = "
				+ eapConfigId
				+ "\n  digestConfigId = "
				+ digestConfigId
				+ "\n  userName = "
				+ userName
				+ "\n  userNameResponseAttribs = "
				+ userNameResponseAttribs
				+ "\n  orderNumber = "
				+ orderNumber
				+ "\n  isAdditional = "
				+ isAdditional
				+ "\n----------------AuthenticationHandler-------------------------\n";
	}
	public String getUserNameExpression() {
		return userNameExpression;
	}
	public void setUserNameExpression(String userNameExpression) {
		this.userNameExpression = userNameExpression;
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
