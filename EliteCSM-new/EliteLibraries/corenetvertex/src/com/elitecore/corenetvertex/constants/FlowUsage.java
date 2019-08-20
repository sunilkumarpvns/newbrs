package com.elitecore.corenetvertex.constants;

public enum FlowUsage {
	
	NO_INFORMATION(0, "NO_INFORMATION"),
	RTCP(1, "RTCP"),
	AF_SIGNALLING(2, "AF_SIGNALLING");

	public final int val;
	public final String strVal;

	FlowUsage(int flowUsagNumber, String strVal) {
		this.val = flowUsagNumber;
		this.strVal = strVal;	
	}
	
	public static FlowUsage fromLongVal(long val) {
		if(val ==0) {
			return NO_INFORMATION;
		} else if(val == 1) {
			return RTCP;
		} else if(val == 2) {
			return AF_SIGNALLING;
		}
		
		return NO_INFORMATION;
	}
}
