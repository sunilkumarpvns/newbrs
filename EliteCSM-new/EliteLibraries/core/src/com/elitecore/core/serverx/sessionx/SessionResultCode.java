package com.elitecore.core.serverx.sessionx;

public enum SessionResultCode {

	FAILURE(-1),
	SUCCESS(1);
	
	public final int code;
	
	private SessionResultCode(int code) {
		this.code = code;
	}
}
