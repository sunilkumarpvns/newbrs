package com.elitecore.corenetvertex.constants;

public enum AFSignallingProtocol {
	NO_INFORMATION(0, "NO_INFORMATION"),
	SIP(1, "SIP");
	
	public final int val;
	public final String strVal;

	AFSignallingProtocol(int val, String strVal) {
		this.val = val;
		this.strVal = strVal;
	}
	
	public static final AFSignallingProtocol fromLongVal(long val) {
		
		if(val == 1) {
			return SIP;
		} 
		
		return NO_INFORMATION;
	}
	
	
	
}
