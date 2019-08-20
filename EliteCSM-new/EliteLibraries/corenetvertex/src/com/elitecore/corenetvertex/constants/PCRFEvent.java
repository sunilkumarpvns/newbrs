package com.elitecore.corenetvertex.constants;

import java.util.HashMap;
import java.util.Map;

public enum PCRFEvent {
	
	UNKNOWN("UNKNOWN"),
	SESSION_START("SESSION_START"),
 	SESSION_UPDATE("SESSION_UPDATE"),
 	SESSION_STOP("SESSION_STOP"),
 	SESSION_RESET("SESSION_RESET"),
 	AUTHORIZE("AUTHORIZE"),
 	REAUTHORIZE("REAUTHORIZE"),
 	AUTHENTICATE("AUTHENTICATE"),
 	USAGE_REPORT("USAGE_REPORT"),
 	GATEWAY_REBOOT("GATEWAY_REBOOT"),
	
	ACCOUNT_EXPIRED("ACCOUNT_EXPIRED"),
	ADDON_EXPIRED("ADDON_EXPIRED"),
	BOD_START("BOD_START"),
	BOD_EXPIRED("BOD_EXPIRED"),
	QUOTA_MANAGEMENT("QUOTA_MANAGEMENT"),
	DIRECT_DEBITING("DIRECT_DEBITING"),
	REFUND_ACCOUNT("REFUND_ACCOUNT"),
	;


	private static final Map<String,PCRFEvent> VAL_TO_PCRFSERVICE_EVENT;

	protected static final PCRFEvent[] VALUES = values();
	public final String val;

	private PCRFEvent(String event) {
		val = event;
	}
	

	
	static {
		VAL_TO_PCRFSERVICE_EVENT = new HashMap<String,PCRFEvent>(15,1);
		for (PCRFEvent event : VALUES)
			VAL_TO_PCRFSERVICE_EVENT.put(event.val, event);
	}
	
	public static PCRFEvent fromValue(String eventVal) {
		PCRFEvent pcrfServiceEvent = VAL_TO_PCRFSERVICE_EVENT.get(eventVal);
	    return pcrfServiceEvent != null ? pcrfServiceEvent : UNKNOWN;
	}

}

