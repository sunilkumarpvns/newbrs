package com.elitecore.classicrating.base;

public interface IBaseConstant {

	/* ERROR RESPONSE CODES */

	public static final int GENERAL_ERROR = -1;

	public static final int APP_CONTEXT_NOT_DEFINED_ERROR = -2;

	public static final int MANDATORY_PARAMETER_MISSING_ERROR = -3;

	public static final int CUSTOMER_NOT_FOUND_ERROR = -4;

	public static final int INSUFFICIENT_BALANCE_ERROR = -5;

	public static final int INVALID_INPUT_DATA_ERROR = -6;

	public static final int NO_RATE_DEFINED_ERROR = -7;
	
	public static final int RATING_ERROR = -8;
        
        public static int PACKAGE_NOT_FOUND_ERROR=-9;
	
	public static final String RATING_ERROR_MSG  = "Unable to Process Rating Request.";

	/* SUCCESS RESPONSE CODE */

	public static final int SUCCESS = 1;

	public static final int AUTHORIZATION_SUCCESS = 2;

	public static final int ACCOUNTING_SUCCESS = 3;

	public static final String ANY = "ANY";

	/* SLAB NAMES */

	public static final String SLAB_1 = "SLAB1";

	public static final String SLAB_2 = "SLAB2";

	public static final String SLAB_3 = "SLAB3";

	public static final double MINIMUM_BALANCE = 0.0;

	public static final String ACCT_REQUEST = "ACCT-REQUEST";

	public static final String AUTH_REQUEST = "AUTH-REQUEST";

	public static final String PREPAID = "PREPAID";

	public static final String CUSTOMER_TYPE = "CUSTOMER-TYPE";

	public static final String POSTPAID = "POSTPAID";

	public static final String VOIP = "VOIP";

	public static final int PULSE = 60;

	public static final String DATA = "DATA";

	public static final String ACCESS_SERVICE = "ACCESS";

	public static final String ACCESS_SERVICE_ALIAS = "ACCESS SERVICE";

	public static final String CALL_DIRECTION = "CALL-DIRECTION";

	public static final String OUTGOING = "OUTGOING";

	public static final String INCOMING = "INCOMING";

	public static final String USERID = "USER-ID";

	public static final String CALLING_STATION_ID = "CALLING-STATION-ID";

	public static final String CALLED_STATION_ID = "CALLED-STATION-ID";

	public static final String INCLUDE_RING_TIME = "INCLUDE-RING-TIME";

	public static final String MAX_SESSION_TIME = "MAX-SESSION-TIME";

	public static final String MAX_SESSION_TIME_VOLUME = "MAX-SESSION-TIME-VOLUME";

	/* REQUEST response PARAMETERS */
	public static final String MAX_SESSION_TIME_ATTRIBUTE = "MAX-SESSION-TIME";

	public static final String SESSION_TIME = "SESSION-TIME";

	public static final String SESSION_CONNECT_TIME = "SESSION-CONNECT-TIME";

	public static final String SESSION_DISCONNECT_TIME = "SESSION-DISCONNECT-TIME";

	public static final String FRAMED_IP_ADDRESS = "FRAMED-IP-ADDRESS";

	public static final String NAS_IP_ADDRESS = "NAS-IP-ADDRESS";

	public static final String ACCT_SESSION_ID = "ACCT-SESSION-ID";

	public static final String SERVICE_TYPE = "SERVICE-TYPE";

	public static final String NAS_IDENTIFIER = "NAS-IDENTIFIER";

	public static final String ACCT_SESSION_TIME = "ACCT-SESSION-TIME";

	public static final String ACCT_STATUS_TYPE = "ACCT-STATUS-TYPE";

	public static final String NAS_PORT_ID = "NAS-PORT-ID";

	public static final String EVENT_TIMESTAMP = "EVENT-TIMESTAMP";

	public static final String DIALLED_DIGITS = "DIALLED-DIGITS";

	public static final String COST = "COST";

	public static final String BALANCE = "BALANCE";

	/* Rating Types */

	public static final String HOUR_BASE = "H";

	/* Rating Types */

	/* Rating Stream */

	public static final String RETAIL = "RETAIL";

	/* Rating Stream */

	public static final String VOLUME_BASE = "VOLUME-BASE";

	public static final String TIME_BASE = "TIME-BASE";

	/* Rate Card Types */

	public static final String RATING_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

	public static final String UDR_DATE_FORMAT = "dd/MM/yyyy";

	public static final int SECONDS_OF_DAY = 86400;

	public static final int TOTAL_MINUTES_OF_WEEK = 10080;

	public static final int SECONDS_OF_HOUR = 3600;

	// public static final int MINUTES_OF_DAY = 1440;

	public static final int SECONDS_OF_MINUTE = 60;

	public static final int MB_CONST = 1024 * 1024;

	public static final long GB_CONST = 1024 * 1024 * 1024;

	/* Rated CDR */

	public static final String OPEARTION_TYPE = "OPERATION-TYPE";

	public static final String RATED_CDR = "RATED-CDR";

	public static final String ACCOUNTED_COST = "ACCOUNTED-COST";

	public static final String TRANS_TYPE = "TRANS-TYPE";

	public static final String ACCOUNTED_DATA_TRANSFER = "ACCOUNTED-DATA-TRANSFER";

	public static final String ACCOUNTED_TIME = "ACCOUNTED-TIME";

	public static final String CALL_START = "CALL-START";

	public static final String CALL_END = "CALL-END";

	public static final String EVENT_TYPE = "EVENT-TYPE";

	public static final String EVENT = "EVENT";

	public static final String ACCT_INPUT_OCTETS = "ACCT-INPUT-OCTETS";

	public static final String ACCT_OUTPUT_OCTETS = "ACCT-OUTPUT-OCTETS";

	public static final String DURATION = "DURATION";

	/* Package Types */

	public static final String FLAT_PACKAGE = "FLAT";

	public static final String USAGE_BASED_PACKAGE = "USAGE-BASED";

	public static final String STEP_WISE_PACKAGE = "STEP-WISE";

	public static final String INDIVIDUAL = "INDIVIDUAL";

	public static final String SEGMENTED = "SEGMENTED";

	public static final String MANDATORY_MSG = " is a mandatory parameter";

	// public static final String SERVICE_TYPE = "SERVICE-TYPE";

	public static final String MAX_SESSION_VOLUME = "MAX-SESSION-VOLUME";

	public static final String AUTHORISATION = "AUTHORISATION";

	public static final int FALSE = -1;

	public static final int TRUE = 1;

	public static final String ACCOUNTING = "ACCOUNTING";
	
	public static String MAX_EVENTS = "MAX-EVENTS";

	public static String REQUEST_TYPE = "REQUEST-TYPE";
	
	public static String MB = "MB";
	
	public static String SEC = "SEC";
        

}
