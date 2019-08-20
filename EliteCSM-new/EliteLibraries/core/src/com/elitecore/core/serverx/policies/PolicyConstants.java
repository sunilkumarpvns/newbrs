package com.elitecore.core.serverx.policies;

public class PolicyConstants {
	public static final int SUCCESS = 0;
	public static final int GENERAL_FAILER = 1;
	public static final int POLICY_NOT_FOUND = 2;
	public static final int NULL_REQUEST_PACKET = 3;
	public static final int IGNORE = -1;
	
	
	public static String stringify(int iResult) {
		switch (iResult) {
		case SUCCESS:
			return "SUCCESS";
		case GENERAL_FAILER:
			return "FAIL";
		case POLICY_NOT_FOUND:
			return "POLICY NOT FOUND";
		case NULL_REQUEST_PACKET:
			return "NULL REQUEST PACKET";
		case IGNORE:
			return "IGNORE";
		default:
			return "UNKNOWN RESULT: " + iResult;
		}
	}
}
