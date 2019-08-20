package com.elitecore.corenetvertex.subscriberimport;

public enum InputType {

	
	JSON("-json"),
	CSV_SUBSCRIBER("-csvsubscriber"),
	CSV_SUBSCRIPTION("-csvsubscription");
	
	
	public String option;
	
	private InputType(String option) {
		this.option = option;
	}
	
	public static InputType fromOption(String option) {
		
		if (JSON.option.equalsIgnoreCase(option)) {
			return JSON;
		} else if (CSV_SUBSCRIBER.option.equalsIgnoreCase(option)) {
			return CSV_SUBSCRIBER;
		} else if (CSV_SUBSCRIPTION.option.equalsIgnoreCase(option)) {
			return CSV_SUBSCRIPTION;
		}
		
		return null;
	}
	
}
