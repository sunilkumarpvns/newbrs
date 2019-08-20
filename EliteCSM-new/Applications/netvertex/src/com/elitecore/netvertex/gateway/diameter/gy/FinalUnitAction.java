package com.elitecore.netvertex.gateway.diameter.gy;

public enum FinalUnitAction {
	TERMINATE(0),
	REDIRECT(1),
	RESTRICT_ACCESS(2);
	
	public final int val;
	
	FinalUnitAction(int val) {
		this.val = val;
	}
}
