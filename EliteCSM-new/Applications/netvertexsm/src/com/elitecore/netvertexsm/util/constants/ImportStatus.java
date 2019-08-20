package com.elitecore.netvertexsm.util.constants;

import java.util.HashMap;
import java.util.Map;

public enum ImportStatus {
	SUCCESS("SUCCESS"), 
	PARTIAL_IMPORT("PARTIAL IMPORT"), 
	FAIL("FAIL"),
	SKIP("SKIP");
	
	public final String status;

	public String getStatus() {
		return status;
	}

	private ImportStatus(String status) {
		this.status=status;
	}
	private static final Map<String,ImportStatus> policyStatusMap;;

	
	static {
		policyStatusMap = new HashMap<String,ImportStatus>(4,1);
		for (ImportStatus status : values()){
			policyStatusMap.put(status.status, status);
		}
	}
	
	public static ImportStatus fromValue(String statusVal) {
		return policyStatusMap.get(statusVal);
	}
	

}
