package com.elitecore.corenetvertex.constants;

public enum PCRFKeyType {
	REQUEST(1),
	RESPONSE(2),
	RULE(3),
	PCC_RULE(4),
	SUBSCRIBER_PROFILE(5),
	IMS_RULE(6)
	;
	
	public final int val;

	private PCRFKeyType(int val) {
		this.val = val;
	}

	public int getVal() {
		return val;
	}
}
