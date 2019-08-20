package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.util.List;

public class CDRGeneration {

	private String handlerName;
	private List<CDRGenerationDetails> cdrGenerationDetailsList; 
	private int orderNumber;
	private String isAdditional;
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
	public List<CDRGenerationDetails> getCdrGenerationDetailsList() {
		return cdrGenerationDetailsList;
	}
	public void setCdrGenerationDetailsList(List<CDRGenerationDetails> cdrGenerationDetailsList) {
		this.cdrGenerationDetailsList = cdrGenerationDetailsList;
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
