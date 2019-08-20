package com.elitecore.core.commons.util.db.errorcodes;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Malav Desai
 * 
 */
public enum OracleErrorCodes {
	ORA_17002(17002, "Io exception", true),
	ORA_17410(17410, "No more data to read from socket",true),
	ORA_1033(1033, "ORACLE initialization or shutdown in progress", true),
	ORA_1034(1034, "ORACLE not available", true),
	ORA_3113(3113, "end-of-file on communication channel", true),
	ORA_1089(1089, "immediate shutdown in progress - no operations are permitted", true),
	ORA_17447(17447, "OALL8 is in an inconsistent state", false),
	ORA_12514(12514, "TNS:listener does not currently know of service requested in connect descriptor", true),
	ORA_12528(12528, "TNS:listener: all appropriate instances are blocking new connections", true),
	
	/**
	 * Cause of this error: 
	 * The listener received a request to establish a connection to a database or other service. 
	 * The connect descriptor received by the listener specified a SID for an instance (usually a database instance) 
	 * that either has not yet dynamically registered with the listener or has not been statically configured for the listener. 
	 * This may be a temporary condition such as after the listener has started, but before the database instance has registered with the listener.
	 * 
	 * Reference: http://ora-12505.ora-code.com/
	 */
	ORA_12505(12505, "TNS:listener does not currently know of SID given in connect descriptor", true),
	
	/**
	 * Cause of this error:
	 * An invalid username or password was entered in an attempt to log on to Oracle. 
	 * The username and password must be the same as was specified in a GRANT CONNECT statement. 
	 * If the username and password are entered together, the format is: username/password.
	 * 
	 * Reference: http://ora-01017.ora-code.com/
	 */
	ORA_01017(1017, "invalid username/password; logon denied", true)
	;
	
	public final int errorCode;
	public final String vendorErrorMessage;
	public final boolean isDBDownError;
	
	private static Map<Integer, OracleErrorCodes> errorCodeToErrorEnumMap;
	
	static{
		errorCodeToErrorEnumMap = new HashMap<Integer, OracleErrorCodes>();
		for (OracleErrorCodes code : values()) {
			errorCodeToErrorEnumMap.put(code.errorCode, code);
		}
	}
	
	private OracleErrorCodes(int errorCode, String vendorErrorMessage, boolean isDBDownError) {
		this.errorCode = errorCode;
		this.vendorErrorMessage = vendorErrorMessage;
		this.isDBDownError = isDBDownError;
	}
	
	public static boolean isDBDownErrorCode(int errorCode){
		OracleErrorCodes code = errorCodeToErrorEnumMap.get(errorCode);
		if(code != null){
			return code.isDBDownError;
		}else {
			return false;
		}
	}
	
	public static String getVendorErrorMessageFromErrorCode(int errorCode){
		OracleErrorCodes code = errorCodeToErrorEnumMap.get(errorCode);
		String message = null;
		if(code != null){
			message = code.vendorErrorMessage;
		}
		return message;
	}
	
	public static OracleErrorCodes fromErrorCode(int errorCode){
		return errorCodeToErrorEnumMap.get(errorCode);
	}
}