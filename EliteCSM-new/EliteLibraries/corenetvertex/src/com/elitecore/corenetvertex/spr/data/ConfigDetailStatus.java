package com.elitecore.corenetvertex.spr.data;

public enum ConfigDetailStatus {
	SUCCESS("SUCCESS"), 
	FAILURE("FAILURE")
	;
	
	public final String status;
	private ConfigDetailStatus(String status) {
		this.status=status;
	}
}
