package com.elitecore.nvsmx.ws.subscription.data;

public enum AggregationKey {

	BILLING_CYCLE("Billing Cycle"),
	CUSTOM("Custom"),
	WEEKLY("Weekly"),
	DAILY("Daily");

	private String val;
	AggregationKey(String val){
		this.val = val;
	}
	public String getVal() {
		return this.val;
	}
}
