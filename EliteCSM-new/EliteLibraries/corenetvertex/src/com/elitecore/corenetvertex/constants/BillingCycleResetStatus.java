package com.elitecore.corenetvertex.constants;

public enum BillingCycleResetStatus {

	PENDING("P"),
	SUCCESS("S"),
	;
	
	private String val;
	
	private BillingCycleResetStatus(String val) {
		this.val = val;
	}
	
	public String getVal() {
		return val;
	}
}
