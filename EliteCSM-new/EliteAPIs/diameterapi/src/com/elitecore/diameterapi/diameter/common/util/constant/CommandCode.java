package com.elitecore.diameterapi.diameter.common.util.constant;

import java.util.HashMap;
import java.util.Map;

public enum CommandCode {
	
	/*
	 * In case of adding new command code, Please check isEligibleToRemoveSession() method of Session.java 
	 * and isEligibleToRemoveSession() method of ApplicationListener for providing case for releasing session.
	 */
	
	//Diameter-base command
	CAPABILITIES_EXCHANGE(257,"CE"),
	DEVICE_WATCHDOG(280,"DW"),
	DISCONNECT_PEER(282,"DP"),
	
	//Application specific command code
	AUTHENTICATION_AUTHORIZATION(265,"AA"),
	CREDIT_CONTROL(272,"CC"),  
	SESSION_TERMINATION(275,"ST"),
	ACCOUNTING(271,"AC"),
	DIAMETER_EAP(268,"DE"),
	
	//Server initiated Application specific command code
	ABORT_SESSION(274,"AS", true),
	RE_AUTHORIZATION(258,"RA", true),
	SPENDING_LIMIT(8388635 , "SL"),
	SPENDING_STATUS_NOTIFICATION(8388636, "SN", true),
	
	//for HSS Integration
	USER_AUTHORIZATION(300, "UA"),
	SERVER_ASSIGNMENT(301, "SA"),
	LOCATION_INFO(302, "LI"),
	MULTIMEDIA_AUTHENTICATION(303, "MA"),
	REGISTRATION_TERMINATION(304, "RT", true),
	PUSH_PROFILE(305, "PP", true),
	USER_DATA(306, "UD"),
	PROFILE_UPDATE(307, "PU"),
	SUBSCRIBE_NOTIFICATIONS(308, "SBN"),
	PUSH_NOTIFICATION(309, "PN", true),
	BOOTSTRAPPING_INFO(310, "BI"),
	MESSAGE_PROCESS(311, "MP"),
	
	UNIMPLEMENTED(0,"UN");

	private static final Map<Integer,CommandCode> map;
	public static final CommandCode[] VALUES = values();

	static {
		map = new HashMap<Integer,CommandCode>();
		for (CommandCode type : VALUES) {
			map.put(type.code, type);
		}
	}

	public final int code;
	public final String displayName;
	public final boolean isServerInitiated;
	
	CommandCode(int code,String displayName) {
		this(code, displayName, false);
	}
	
	CommandCode(int code, String displayName, boolean isServerInitiated) {
		this.code = code;
		this.displayName = displayName;
		this.isServerInitiated = isServerInitiated;
	}

	public int getCode() {
		return code;
	}

	public static boolean isValid(int value) {
		return map.containsKey(value);
	}

	public static String fromCode(int value) {
		
		CommandCode commandCode = map.get(value);
		
		if(commandCode != null)
			return commandCode.name();
		
		return String.valueOf(value);
	}
	
	public static String getDisplayName(int value) {
		
		CommandCode commandCode = map.get(value);
		
		if(commandCode != null)
			return commandCode.displayName;
		
		return String.valueOf(value);
	}
	
	public static CommandCode getCommandCode(int intCommandCode){
		CommandCode commandCode = map.get(intCommandCode); 
		if(commandCode!=null)
			return commandCode;
		return UNIMPLEMENTED;
	}
	
	public static CommandCode fromDisplayName(String displayName) {
		CommandCode[] commandCodes = values();
		if (commandCodes != null) {
			for (int i=0 ; i<commandCodes.length ; i++) {
				if (commandCodes[i].displayName.equalsIgnoreCase(displayName)) {
					return commandCodes[i];
				}
			}
		}
		return null;
	}
	
	
}