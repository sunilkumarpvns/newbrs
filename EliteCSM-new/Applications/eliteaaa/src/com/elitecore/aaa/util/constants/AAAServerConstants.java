package com.elitecore.aaa.util.constants;

import com.elitecore.commons.base.Strings;

public class AAAServerConstants {
	
	public static final String RAD_AUTH_SERVICE_ID = "RAD_AUTH";
	public static final String RAD_ACCT_SERVICE_ID = "RAD_ACCT";
	public static final String RAD_DYNAUTH_SERVICE_ID = "RAD_DYNAUTH";
	public static final String RAD_COMM_MANAGER = "RAD_COMM_MANAGER";
	public static final String LDAP_COMM_MANAGER = "LDAP_COMM_MANAGER"; 
	public static final String DIA_NAS_SERVICE_ID = "DIA_NAS";
	public static final String DIA_EAP_SERVICE_ID = "DIA_EAP";
	public static final String DIA_CC_SERVICE_ID = "DIA_CC";
	public static final String DIA_TGPP_SERVER_SERVICE_ID = "3GPP_AAA_SERVER";
	public static final String DIAMETER_STACK = "DIAMETER_STACK";
	public static final String SNMP_SERVICE_ID = "SNMP";
	public static final String WEBSERVICE_SERVICE_ID = "WEB_SERVICE";
	public static final String RM_CONCURRENT_LOGIN_SERVICE_ID = "RM_CONCURRENT_LOGIN";
	public static final String RM_PREPAID_CHARGING_SERVICE_ID = "RM_PREPAID_CHARGING";
	public static final String RM_CHARGING_SERVICE_ID = "RM_CHARGING";
	public static final String GTP_SERVICE_ID = "GTP_PRIME_SER";
	public static final String RDR_SERVICE_ID ="RDR_SER";
	public static final String RM_IPPOOL_SERVICE_ID = "RM_IPPOOL";
	public static final String CUI_KEY = "CUI_KEY";
	public static final String UNSTRIPPED_CUI ="Unstripped-CUI";
	public static final String DIAMETER = "Diameter";
	public static final String RADIUS = "Radius";
	public static final String DRIVER_INSTANCE_ID = "Driver-Instance-Id";
	
	/* Authorization specific parameters */
	public static final String CUSTOMER_REPLY_ITEM = "CUSTOMER_REPLY_ITEM";
	public static final String REJECT_ON_CHECK_ITEM_NOT_FOUND = "REJECT_ON_CHECK_ITEM_NOT_FOUND";
	public static final String REJECT_ON_REJECT_ITEM_NOT_FOUND = "REJECT_ON_REJECT_ITEM_NOT_FOUND";
	public static final String CONTINUE_ON_POLICY_NOT_FOUND = "REJECT_ON_POLICY_NOT_FOUND";
	public static final int SESSION_TIMEOUT_DISABLED = -1;
	public static final long POLICY_DEFAULT_SESSION_TIMEOUT_IN_SECS = 0;
	public static final String SESSION_TIMEOUT = "SESSION_TIMEOUT";
	public static final String WIMAX_ENABLED = "WIMAX_ENABLED";
	
	public static final String SATISFIED_POLICIES = "SATISFIED_POLICIES";
	public static final String SATISFIED_HOTLINE_POLICIES = "SATISFIED_HOTLINE_POLICIES";
	public static final String SATISFIED_CLIENT_POLICIES = "SATISFIED_CLIENT_POLICIES";
	public static final String DYNAMIC_CHECK_ITEM_DRIVER_INSTANCE_ID = "Dynamic-check-item-Driver-Instance-Id";
	public static final String SELECTED_SERVICE_POLICY = "SELECTED-SERVICE-POLICY";
	public static final String ATTRIBUTE_FROM_REQUEST = "$REQ:";
	public static final String ATTRIBUTE_FROM_RESPONSE = "$RES:";
	public static final String SYSTEM_PATH = "system";
	
	// Following two constants are used in initializing diameter peer 
	public static final String ROUTING  = "-ROUTING-";
	public static final String ALL  = "-ALL-";
	
	// This has been added basically to use in Alert generation while user not found. Value with this key in
	// ServiceRequest would be containing user identity for which driver tried to fetch the profile
	public static final String USER_IDENTITY = "UserIdentity";
	
	public static final String DN = "DN";
	public static final String SELECTED_AUTHENTICATION_METHOD = "SELECTED_AUTHENTICATION_METHOD";

	// RADIUS webservice Error-Message 
	public static final String AUTH_SERVICE_IS_NOT_RUNNING    = "Authentication service is not running";
	public static final String ACCT_SERVICE_IS_NOT_RUNNING    = "Accouting service is not running";
	public static final String DYNAUTH_SERVICE_IS_NOT_RUNNING = "DynAuth service is not running";
	public static final String INVALID_PACKET_TYPE_RECEIVED   = "Invalid packet type received";
	public static final String UNKNOWN_CLIENT_127_0_0_1       = "Unknown Client 127.0.0.1";
	
	
	/** Diameter Constants **/
	public static String SYS_ORIGIN_STATE_ID_FILE = "_sys.originstateid";
	
	/** Enterprice OID for AAA and RM Server */
	public final static String AAA_ENTERPRISE_OID = "1.3.6.1.4.1.21067.1";
	public final static String RM_ENTERPRISE_OID = "1.3.6.1.4.1.21067.3";
	
	/*
	 * Constants for Miscellaneous configuration
	 */
	public static final String DUMMY_RATING = "dummy-rating";
	
	public static final int DEFAULT_DUPLICATE_REQUEST_QUEUE_PURGE_INTERVAL = 15;

	/*
	 * CUI and Username related configuration constants
	 */
	public static final String NONE ="None";
	public static final String CUI = "CUI";
	public static final String PROFILE_CUI = "Profile-CUI";
	public static final String GROUP = "Group";
	public static final String AUTHENTICATED_IDENTITY = "Authenticated-Identity";
	public static final String REQUEST = "Request";
	public static final String ADVANCED = "Advanced";
	public static final String USERNAME_ADDED = "USERNAME_ADDED";
	public static final String CUI_ADDED = "CUI_ADDED";
	
	
	// Session management
	public static final String PROTOCOL_TYPE_FIELD = "PROTOCOLTYPE";
	public static final String CONCURRENCY_ID_FIELD = "CONCUSERID";
	public static final String SESSION_STATUS = "SESSION_STATUS";
	public static final String SESSION_STATUS_INACTIVE = "INACTIVE";
	public static final String SESSION_STATUS_ACTIVE = "ACTIVE";
	public static final String SESSION_STATUS_DELETED = "DELETED";
	public enum ProtocolType {
		RADIUS,
		DIAMETER;

		public static ProtocolType from(String stringValue) {
			if (Strings.isNullOrBlank(stringValue)) {
				return DIAMETER;
			}
			if (RADIUS.name().equalsIgnoreCase(stringValue.trim())) {
				return RADIUS;
			}
			return DIAMETER;
		}
	}

	//RADIUS service's default ports
	public static final int DEFAULT_RAD_AUTH_PORT = 1812;
	public static final int DEFAULT_RAD_ACCT_PORT = 1813;
	public static final int DEFAULT_RM_CHARGING_SERVICE_PORT = 1924;
	public static final int DEFAULT_RAD_DYNAUTH_PORT = 3799;
	public static final int DEFAULT_RM_CONCURRENT_LOGIN_SERVICE_PORT = 1920;
	public static final int DEFAULT_RM_IP_POOL_SERVICE_PORT = 1811;
	public static final int DEFAULT_RM_PREPAID_CHARGING_PORT = 1923;
	public static final int DEFAULT_SNMP_SERVICE_PORT = 161;
	public static final int DEFAULT_GTP_PRIME_SERVICE_PORT = 3386;
	public static final String DIAMETER_TO_RADIUS_SECRET = "internal_secret";
	public static final String MSK_REVALIDATION_TIME = "MSK_REVALIDATION_TIME";
	
	public static final int DEFAULT_QUEUE_SIZE = 3000;
	public static final int MAX_QUEUE_SIZE = 50000;
	
	// Diameter Duplicate Peer Connection Policy
	public static class DuplicatePeerConnectionPolicyConstants {
		public static final String DISCARD_OLD = "DiscardOld";
		public static final String DEFAULT = "Default";	
	}
}
