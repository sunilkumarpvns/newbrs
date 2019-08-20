package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.util.List;

public class COADMGeneration {
	
	private String handlerName;
	private List<COADMGenerationDetails> coaDMGenerationDetailList; 
	private int orderNumber;
	private String isAdditional;
	private String scheduleAfterInMillis;
	private String isHandlerEnabled = "false";
	
	public List<COADMGenerationDetails> getCoaDMGenerationDetailList() {
		return coaDMGenerationDetailList;
	}
	public void setCoaDMGenerationDetailList(
			List<COADMGenerationDetails> coaDMGenerationDetailList) {
		this.coaDMGenerationDetailList = coaDMGenerationDetailList;
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
	public String getScheduleAfterInMillis() {
		return scheduleAfterInMillis;
	}
	public void setScheduleAfterInMillis(String scheduleAfterInMillis) {
		this.scheduleAfterInMillis = scheduleAfterInMillis;
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
