package com.elitecore.corenetvertex.constants;

import java.util.HashMap;
import java.util.Map;



public enum PolicyStatus {
	SUCCESS("SUCCESS"), 
	PARTIAL_SUCCESS("PARTIAL SUCCESS"), 
	FAILURE("FAILURE"),
	LAST_KNOWN_GOOD("LAST KNOWN GOOD"),
	UNKNOWN("UNKNOWN");
	
	public final String status;

	public String getStatus() {
		return status;
	}

	private PolicyStatus(String status) {
		this.status=status;
	}
	private static final Map<String,PolicyStatus> policyStatusMap;;

	
	static {
		policyStatusMap = new HashMap<String,PolicyStatus>(4,1);
		for (PolicyStatus status : values()){
			policyStatusMap.put(status.status, status);
		}
	}
	
	public static PolicyStatus fromValue(String statusVal) {
		PolicyStatus policyStatus = policyStatusMap.get(statusVal);
	    return (policyStatus != null ? policyStatus : UNKNOWN);
	}

	


}
