package com.elitecore.corenetvertex.constants;

public enum SubscriberStatus {
	ACTIVE("Active"),
	INACTIVE("Inactive"),
	DELETED("Deleted");
	
	public String val;

	SubscriberStatus(String val) {
		this.val = val;
	}
	
}