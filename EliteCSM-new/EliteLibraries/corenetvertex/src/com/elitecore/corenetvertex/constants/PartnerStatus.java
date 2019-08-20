package com.elitecore.corenetvertex.constants;

public enum PartnerStatus {
	REGISTERED("Registered"),
	ACTIVE("Active"),
	SUSPENDED("Suspended"),
	TERMINATE("Terminated");


	private String val;

	private PartnerStatus(String val) {
		this.val = val;
	}

	public String getName() {
		return val;
	}
}