package com.elitecore.test.dependecy.diameter.packet;

import com.elitecore.test.dependecy.diameter.DiameterConstants;

import java.util.HashMap;
import java.util.Map;

public enum ResultCode {

	/************** 1XXX **************************/
	DIAMETER_MULTI_ROUND_AUTH(1001, ResultCodeCategory.RC1XXX, 0),
	
	
	/************** 2XXX **************************/
	DIAMETER_SUCCESS(2001, ResultCodeCategory.RC2XXX, 0),
	DIAMETER_LIMITED_SUCCESS(2002, ResultCodeCategory.RC2XXX, 0),
	DIAMETER_FIRST_REGISTRATION(2003, ResultCodeCategory.RC2XXX, 0),					//RFC 4740
	DIAMETER_SUBSEQUENT_REGISTRATION(2004, ResultCodeCategory.RC2XXX, 0),				//RFC 4740
	DIAMETER_UNREGISTERED_SERVICE(2005, ResultCodeCategory.RC2XXX, 0),				//RFC 4740
	DIAMETER_SUCCESS_SERVER_NAME_NOT_STORED(2006, ResultCodeCategory.RC2XXX, 0),		//RFC 4740
	DIAMETER_SERVER_SELECTION(2007, ResultCodeCategory.RC2XXX, 0),					//RFC 4740
	DIAMETER_SUCCESS_AUTH_SENT_SERVER_NOT_STORED(2008, ResultCodeCategory.RC2XXX, 0),	//RFC 4740
	
	/************** 3XXX **************************/
	DIAMETER_COMMAND_UNSUPPORTED(3001, ResultCodeCategory.RC3XXX, 0),
	DIAMETER_UNABLE_TO_DELIVER(3002, ResultCodeCategory.RC3XXX, 0),
	DIAMETER_REALM_NOT_SERVED(3003, ResultCodeCategory.RC3XXX, 0),
	DIAMETER_TOO_BUSY(3004, ResultCodeCategory.RC3XXX, 0),
	DIAMETER_LOOP_DETECTED(3005, ResultCodeCategory.RC3XXX, 0),
	DIAMETER_REDIRECT_INDICATION(3006, ResultCodeCategory.RC3XXX, 0),
	DIAMETER_APPLICATION_UNSUPPORTED(3007, ResultCodeCategory.RC3XXX, 0),
	DIAMETER_INVALID_HDR_BITS(3008, ResultCodeCategory.RC3XXX, 0),
	DIAMETER_INVALID_AVP_BITS(3009, ResultCodeCategory.RC3XXX, 0),
	DIAMETER_UNKNOWN_PEER(3010, ResultCodeCategory.RC3XXX, 0),
	DIAMETER_INVALID_PROXY_PATH_STACK(3501, ResultCodeCategory.RC3XXX, 0),		       // [RFC6159]
	
	/************** 4XXX **************************/
	DIAMETER_AUTHENTICATION_REJECTED(4001, ResultCodeCategory.RC4XXX, 0),
	DIAMETER_OUT_OF_SPACE(4002, ResultCodeCategory.RC4XXX, 0),
	ELECTION_LOST(4003, ResultCodeCategory.RC4XXX, 0),
	DIAMETER_ERROR_MIP_REPLY_FAILURE(4005, ResultCodeCategory.RC4XXX, 0),
	DIAMETER_ERROR_HA_NOT_AVAILABLE(4006, ResultCodeCategory.RC4XXX, 0),
	DIAMETER_ERROR_BAD_KEY(4007, ResultCodeCategory.RC4XXX, 0),
	DIAMETER_ERROR_MIP_FILTER_NOT_SUPPORTED(4008, ResultCodeCategory.RC4XXX, 0),
	DIAMETER_END_USER_SERVICE_DENIED(4010, ResultCodeCategory.RC4XXX, 0),
	DIAMETER_CREDIT_CONTROL_NOT_APPLICABLE(4011, ResultCodeCategory.RC4XXX, 0),
	DIAMETER_CREDIT_LIMIT_REACHED(4012, ResultCodeCategory.RC4XXX, 0),
	DIAMETER_USER_NAME_REQUIRED(4013, ResultCodeCategory.RC4XXX, 0),
	DIAMETER_ER_NOT_AVAILABLE(4501, ResultCodeCategory.RC4XXX, 0),		       // [RFC6159]
	
	/* Elitecore VendorId(21067) Result Codes */
	DIAMETER_PEER_NOT_FOUND(4998, ResultCodeCategory.RC4XXX,  DiameterConstants.VENDOR_ELITECORE_ID),
	DIAMETER_REQUEST_TIMEOUT(4999, ResultCodeCategory.RC4XXX,  DiameterConstants.VENDOR_ELITECORE_ID), 
	
	/************** 5XXX **************************/
	DIAMETER_AVP_UNSUPPORTED(5001, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_UNKNOWN_SESSION_ID(5002, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_AUTHORIZATION_REJECTED(5003, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_INVALID_AVP_VALUE(5004, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_MISSING_AVP(5005, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_RESOURCES_EXCEEDED(5006, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_CONTRADICTING_AVPS(5007, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_AVP_NOT_ALLOWED(5008, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_AVP_OCCURS_TOO_MANY_TIMES(5009, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_NO_COMMON_APPLICATION(5010, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_UNSUPPORTED_VERSION(5011, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_UNABLE_TO_COMPLY(5012, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_INVALID_BIT_IN_HEADER(5013, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_INVALID_AVP_LENGTH(5014, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_INVALID_MESSAGE_LENGTH(5015, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_INVALID_AVP_BIT_COMBO(5016, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_NO_COMMON_SECURITY(5017, ResultCodeCategory.RC5XXX, 0),
	DIAMETER_RADIUS_AVP_UNTRANSLATABLE(5018, ResultCodeCategory.RC5XXX, 0),			// [RFC4849]
	DIAMETER_ERROR_NO_FOREIGN_HA_SERVICE(5024, ResultCodeCategory.RC5XXX, 0),			// [RFC4004]
	DIAMETER_ERROR_END_TO_END_MIP_KEY_ENCRYPTION(5025, ResultCodeCategory.RC5XXX, 0),	// [RFC4004]
	DIAMETER_USER_UNKNOWN(5030, ResultCodeCategory.RC5XXX, 0),						// [RFC4006]
	DIAMETER_RATING_FAILED(5031, ResultCodeCategory.RC5XXX, 0),						// [RFC4006]
	DIAMETER_ERROR_USER_UNKNOWN(5032, ResultCodeCategory.RC5XXX, 0),					// [RFC4740]
	DIAMETER_ERROR_IDENTITIES_DONT_MATCH(5033, ResultCodeCategory.RC5XXX, 0),			// [RFC4740]
	DIAMETER_ERROR_IDENTITY_NOT_REGISTERED(5034, ResultCodeCategory.RC5XXX, 0),		// [RFC4740]
	DIAMETER_ERROR_ROAMING_NOT_ALLOWED(5035, ResultCodeCategory.RC5XXX, 0),			// [RFC4740]
	DIAMETER_ERROR_IDENTITY_ALREADY_REGISTERED(5036, ResultCodeCategory.RC5XXX, 0),	// [RFC4740]
	DIAMETER_ERROR_AUTH_SCHEME_NOT_SUPPORTED(5037, ResultCodeCategory.RC5XXX, 0),		// [RFC4740]
	DIAMETER_ERROR_IN_ASSIGNMENT_TYPE(5038, ResultCodeCategory.RC5XXX, 0),			// [RFC4740]
	DIAMETER_ERROR_TOO_MUCH_DATA(5039, ResultCodeCategory.RC5XXX, 0),					// [RFC4740]
	DIAMETER_ERROR_NOT_SUPPORTED_USER_DATA(5040, ResultCodeCategory.RC5XXX, 0);		// [RFC4740]
	
	
	private static final Map<Integer,ResultCode> map;
	public static final ResultCode[] VALUES = values();

	static {
		map = new HashMap<Integer,ResultCode>();
		for (ResultCode type : VALUES) {
			map.put(type.code, type);
		}
	}

	public final int code;
	public final ResultCodeCategory category;
	public final long vendorId;

	private ResultCode(int code, ResultCodeCategory category, long vendorId) {
		this.code = code;
		this.category = category;
		this.vendorId = vendorId;
	}

	public int getCode() {
		return code;
	}

	public static boolean isValid(int value) {
		return map.containsKey(value);
	}

	public static ResultCode fromCode(int value) {
		return map.get(value);
	}

	public static void main(String args[]) {
		System.out.println(ResultCode.DIAMETER_SUCCESS);
		System.out.println(isValid(ResultCode.DIAMETER_SUCCESS.getCode()));
	}
}