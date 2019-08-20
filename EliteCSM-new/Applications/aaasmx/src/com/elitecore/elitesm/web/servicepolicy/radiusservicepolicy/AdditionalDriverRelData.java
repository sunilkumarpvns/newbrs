package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

public class AdditionalDriverRelData {
	private String driverInstanceId;
	private Long orderNumber;
	
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@Override
	public String toString() {
		return "----------------AdditionalDriverRelData------------------------\n  driverInstanceId = "
				+ driverInstanceId
				+ "\n  orderNumber = "
				+ orderNumber
				+ "\n----------------AdditionalDriverRelData-------------------------\n";
	}
}
