package com.elitecore.aaa.radius.service.auth.constant;

public class AuthReplyMessageConstant {

	//Authentication Success Constants
	public static final String AUTHENTICATION_SUCCESS = "Authentication Success";
	//Authentication Failure Constants
	public static final String INVALID_PASSWORD = "Invalid Password"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String USER_NOT_FOUND = "User not found";
	public static final String INVALID_CHAP_PASSWORD = "Invalid CHAP-Password"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String INVALID_MSCHAPv1_PASSWORD = "Invalid MSCHAPv1-Password"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String INVALID_MSCHAPv2_PASSWORD = "Invalid MSCHAPv2-Password"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String EAP_FAILURE = "EAP-Failure";
	public static final String AUTHENTICATION_FAILED = "Authentication Failed";	
	public static final String DIGEST_FAILURE = "Digest Failure";	
	public static final String INVALID_DIGEST_PASSWORD = "Invalid Digest Password";//NOSONAR - Reason: Credentials should not be hard-coded
	public static final String INVALID_PACKETTYPE_FOR_DIGEST_RESPONSE_PACKET = "Invalid packet type for Digest response packet";
	public static final String ACCOUNT_IS_BLACKLISTED = "Account is blacklisted";

	//Invalid Password Constants, except Wrong Password entered by user.
	public static final String AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD = "Authentication failed due to Invalid password"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String AUTHENTICATION_FAILED_DUE_TO_INVALID_ENCRYPTION_TYPE = "Authentication failed due to Invalid encryption type";
	public static final String AUTHENTICATION_FAILED_DUE_TO_UNSUPPORTED_ENCRYPTION = "Authentication failed due to Unsupported encryption";
	public static final String AUTHENTICATION_FAILED_DUE_TO_IMPROPER_PASSWORD_ENCRYPTION_FORMAT = "Authentication failed due to Improper password encryption format"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String AUTHENTICATION_FAILED_DUE_TO_EXCEPTION_IN_DECRYPTION = "Authentication failed due to exception in decryption";
	public static final String AUTHENTICATION_FAILED_DUE_TO_EXCEPTION_IN_ENCRYPTION = "Authentication failed due to exception in encryption";
	
	//Authorization Failure constants
	public static final String AUTHORIZATION_FAILED = "Authorization Failed";
	public static final String ACCOUNT_NOT_ACTIVE = "Account is not active";
	public static final String ACCOUNT_EXPIRED = "Account Expired";
	public static final String CREDIT_LIMIT_EXCEEDED = "Account Credit Limit Exceeded";
	public static final String MAC_VALIDATION_FAILED_FOR_SESSION_ID = "MAC Validation Failed for Session-Id: " ;
	public static final String INVALID_NAS_PORT_TYPE = "Invalid NAS Port Type";
	public static final String NAS_PORT_TYPE_NOT_FOUND = "NAS Port Type not found";
	public static final String INVALID_CALLING_STATION_ID = "Invalid Calling Station Id";
	public static final String CALLING_STATION_ID_NOT_FOUND = "Calling Station Id not found";
	public static final String INVALID_CALLED_STATION_ID = "Invalid Called Station Id";
	public static final String CALLED_STATION_ID_NOT_FOUND = "Called Station Id not found";
	
	//Service Policy level failure constants
	public static final String NO_POLICY_SATISFIED = "No Policy Satisfied";
	public static final String PACKET_VALIDATION_FAILED = "Packet validation failed";
	public static final String NO_DEFAULTVALUE_FOUND_FOR_RESPONSE_ATTRIBUTE_ID = "No Default value found for Response Attribute ID: ";	
	public static final String UNSUPPORTED_AUTHENTICATION_METHOD = "Unsupported Authentication method";
	
	
	//RM Process Failure Constants
	public static final String RATING_PROCESS_FAILED = "Rating process Failed";
	public static final String IP_POOL_OPERATION_FAILED= "IP Pool operation Failed";
	
	//RM communication Success Constants
	public static final String IP_SUCCESS = "IP Success";

	//RM Communication Failure Constants	
	public static final String OPERATION_FAILD_FOR_IPPOOL_COMMUNICATION = "Operation failed for IPPool Communication";
	public static final String TIMEOUT_RESPONSE_RECEIVED_FOR_IPPOOL_COMMUNICATION = "Timeout Response received for IPPool Communication";
	public static final String OPERATION_FAILED_FOR_CG_COMMUNICATION = "Operation failed for CG Communication";
	public static final String TIMEOUT_RESPONSE_RECEIVED_FOR_CG_COMMUNICATION = "Timeout Response received for CG Communication";
	public static final String OPERATION_FAILED_FOR_CONC_COMMUNICATION = "Operation failed for Conc Communication";
	public static final String TIMEOUT_RESPONSE_RECEIVED_FOR_CONC_COMMUNICATION = "Timeout Response received for Conc Communication";
	public static final String OPERATION_FAILED_FOR_PREPAID_COMMUNICATION = "Operation failed for Prepaid Communication";
	public static final String TIMEOUT_RESPONSE_RECEIVED_FOR_PREPAID_COMMUNICATION = "Timeout Response received for Prepaid Communication";
	
	//Proxy constants
	public static final String TIMEOUT_RESPONSE_RECEIVED_FOR_PROXY_COMMUNICATION = "Timeout Response received for proxy Communication";

	//Dummy Rating Failure Constants
	public static final String INITIAL_RESERVATION_NOT_AVAILABLE = "Initial Reservation not Available";
	
	
	//Concurrency Session Manager constants
	public static final String MAX_LOGIN_LIMIT_REACHED = "Max Login Limit Reached";
	public static final String CONCURRENCY_FAILED = "Concurrency Failed";
	public static final String CONCURRENCY_FAILED_DUE_TO_DBOPERATION_FAILURE = "Concurrency Failed due to DB Operation Failure";
	public static final String SESSION_MANAGER_OPERATION_FAILED = "Session Manager Operation Failed";
	
	public static final String HOTLINE_SUCCESS = "Hotline Success";
	
	//WiMAX constants
	public static final String WIMAX_SESSION_NOT_FOUND_FOR_HA = "WiMAX session mandatory to process HA request cannot be located";
	public static final String WIMAX_PROCESSING_FAILED = "WiMAX processing failed";
}
