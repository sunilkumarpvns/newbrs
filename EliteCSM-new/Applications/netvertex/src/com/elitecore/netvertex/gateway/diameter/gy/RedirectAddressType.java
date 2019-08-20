package com.elitecore.netvertex.gateway.diameter.gy;

public enum RedirectAddressType {

	IPV4Address(0),
	IPV6Address(1),
	URL(2),
	SIPURI(3);
	
	public final int val;
	
	RedirectAddressType(int val) {
		this.val = val;
	}
	
}
