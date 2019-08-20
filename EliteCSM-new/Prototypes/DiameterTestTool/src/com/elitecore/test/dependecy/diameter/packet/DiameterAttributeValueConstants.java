package com.elitecore.test.dependecy.diameter.packet;


public class DiameterAttributeValueConstants {
	 //Accounting-Realtime-Required
	 public static final int DELIVER_AND_GRANT             = 1;
	 public static int GRANT_AND_STORE               = 2;
	 public static int GRANT_AND_LOSE                = 3;
	 
	 //Accounting-Record-Type
	 public static final int EVENT_RECORD                  = 1;
	 public static final int START_RECORD                  = 2;
	 public static final int INTERIM_RECORD                = 3;
	 public static final int STOP_RECORD                   = 4;
	 
	 //Auth-Request-Type
	 public static final int AUTHENTICATE_ONLY             = 1;
	 public static final int AUTHORIZE_ONLY                = 2;
	 public static final int AUTHORIZE_AUTHENTICATE        = 3;
	 
	 //Auth-Session-State
	 public static final int STATE_MAINTAINED              = 0;
	 public static final int NO_STATE_MAINTAINED           = 1;
	 
	 //Re-Auth-Request-Type
	 public static final int RE_AUTH_AUTHORIZE_ONLY        = 0;
	 public static final int RE_AUTH_AUTHORIZE_AUTHENTICATE= 1;
	 
	 //Disconnect-Cause
	 public static final int REBOOTING                     = 0;
	 public static final int BUSY                          = 1;
	 public static final int DO_NOT_WANT_TO_TALK_TO_YOU    = 2;
	 
	 //Inband-Security-Id
	 public static final int NO_INBAND_SECURITY            = 0;
	 public static final int TLS                           = 1;
	 
	 //Redirect-Host-Usage
	 public static final int DONT_CACHE                    = 0;
	 public static final int ALL_SESSION                   = 1;
	 public static final int ALL_REALM                     = 2;
	 public static final int REALM_AND_APPLICATION         = 3;
	 public static final int ALL_APPLICATION               = 4;
	 public static final int ALL_HOST                      = 5;
	 public static final int ALL_USER                      = 6;
	 
	 //Session-Binding
	 public static final int RE_AUTH                       = 1;
	 public static final int STR                           = 2;
	 public static final int ACCOUNTING                    = 4;
	 
	 //Session-Server-Failover
	 public static final int REFUSE_SERVICE                = 0;
	 public static final int TRY_AGAIN                     = 1;
	 public static final int ALLOW_SERVICE                 = 2;
	 public static final int TRY_AGAIN_ALLOW_SERVICE       = 3;
	 
	 //Termination-Cause
	 public static final int DIAMETER_LOGOUT               = 1;
	 public static final int DIAMETER_SERVICE_NOT_PROVIDED = 2;
	 public static final int DIAMETER_BAD_ANSWER           = 3;
	 public static final int DIAMETER_ADMINISTRATIVE       = 4;
	 public static final int DIAMETER_LINK_BROKEN          = 5;
	 public static final int DIAMETER_AUTH_EXPIRED         = 6;
	 public static final int DIAMETER_USER_MOVED           = 7;
	 public static final int DIAMETER_SESSION_TIMEOUT      = 8;
	 
	 //CC-Request-Type
	 public static final int DIAMETER_INITIAL_REQUEST		=1;
	 public static final int DIAMETER_UPDATE_REQUEST		=2;
	 public static final int DIAMETER_TERMINATION_REQUEST 	=3;
	 public static final int DIAMETER_EVENT_REQUEST		=4;
	 public static final String DIAMETER_INITIAL_REQUEST_STR		="1";
	 public static final String DIAMETER_UPDATE_REQUEST_STR			="2";
	 public static final String DIAMETER_TERMINATION_REQUEST_STR 	="3";
	 public static final String DIAMETER_EVENT_REQUEST_STR			="4";
	 

	 
	 
	 //CC-Session-Failover
	 public static final int DIAMETER_FAILOVER_NOT_SUPPORTED = 0;
	 public static final int DIAMETER_FAILOVER_SUPPORTED = 1;
	 
	 //CC-Unit-Type
	 public static final int DIAMETER_TIME = 0;
	 public static final int DIAMETER_MONEY = 1;
	 public static final int DIAMETER_TOTAL_OCTETS=2;
	 public static final int DIAMETER_INPUT_OCTETS=3;
	 public static final int DIAMETER_OUTPUT_OCTETS=4;
	 public static final int DIAMETER_SERVICE_SPECIFIC_UNITS=5;
	 
	 //Check-Balance-Result
	 public static final int DIAMETER_ENOUGH_CREDIT=0; 
	 public static final int DIAMETER_NO_CREDIT=1;
	 
	 //426  Credit-Control     
	 public static final int DIAMETER_CREDIT_AUTHORIZATION=0; 
	 public static final int DIAMETER_RE_AUTHORIZATION=1;
	 
	 //427  Credit-Control-Failure-Handling     
	 public static final int DIAMETER_TERMINATE = 0; 
	 public static final int DIAMETER_CONTINUE = 1; 
	 public static final int DIAMETER_RETRY_AND_TERMINATE=2; 

	 //428  Direct-Debiting-Failure-Handling     
	 public static final int DIAMETER_TERMINATE_OR_BUFFER=0; 

	 //449  Final-Unit-Action      	
	 public static final int DIAMETER_REDIRECT=1; 
	 public static final int DIAMETER_RESTRICT_ACCESS=2; 			

	 //455  Multiple-Services-Indicator     
	 public static final int DIAMETER_DIRECT_DEBITING = 0; 
	 public static final int DIAMETER_REFUND_ACCOUNT = 1; 
	 public static final int DIAMETER_CHECK_BALANCE =2; 
	 public static final int DIAMETER_PRICE_ENQUIRY=3; 			

	 //433  Redirect-Address-      	
	 public static final int DIAMETER_IPV4_ADDRESS=0; 
	 public static final int DIAMETER_IPV6_ADDRESS=1; 
	 public static final int DIAMETER_URL=3; 
	 public static final int DIAMETER_SIP_URI=4; 			

	 //450  Subscription--     
	 public static final int DIAMETER_END_USER_E164 = 0; 
	 public static final int DIAMETER_END_USER_IMSI = 1; 
	 public static final int DIAMETER_END_USER_SIP_URI = 2; 
	 public static final int DIAMETER_END_USER_NAI = 3; 			
	 public static final int DIAMETER_END_USER_PRIVATE = 4; 			

	 //452  Tariff-Change-Usage     
	 public static final int DIAMETER_UNIT_BEFORE_TARIFF_CHANGE = 0; 
	 public static final int DIAMETER_UNIT_AFTER_TARIFF_CHANGE = 1; 
	 public static final int DIAMETER_UNIT_INDETERMINATE = 2; 

	 //459  User-Equipment-Info- no    
	 public static final int DIAMETER_IMEISV = 0; 
	 public static final int DIAMETER_MAC = 1; 
	 public static final int DIAMETER_EUI64=2;
	 public static final int DIAMETER_MODIFIED_EUI64=3; 

	 //500 Abort-Cause
	 public static final int TGPP_ABORT_CAUSE_BEARER_RELEASED = 0;
	 public static final int TGPP_ABORT_CAUSE_INSUFFICIENT_SERVER_RESOURCES = 1;
	 public static final int TGPP_ABORT_CAUSE_INSUFFICIENT_BEARER_RESOURCES = 2;
	 
	 //10415:1032 RAT-Type
	 public static final int TGPP_RAT_TYPE_WLAN = 0;
	 public static final int TGPP_RAT_TYPE_VIRTUAL = 1;
	 public static final int TGPP_RAT_TYPE_UTRAN = 1000;
	 public static final int TGPP_RAT_TYPE_GERAN = 1001;
	 public static final int TGPP_RAT_TYPE_GAN = 1002;
	 public static final int TGPP_RAT_TYPE_HSPA_EVO= 1003;
	 public static final int TGPP_RAT_TYPE_EUTRAN= 1004;
	 public static final int TGPP_RAT_TYPE_CDMA2000_1X= 2000;
	 public static final int TGPP_RAT_TYPE_HRPD= 2001;
	 public static final int TGPP_RAT_TYPE_UMB= 2002;
	 public static final int TGPP_RAT_TYPE_EHRPD= 2003;
	 
	 
	 //10415:1027 IP_CAN_TYPE
	 public static final int TGPP_IP_CAN_TYPE_3GPP_GPRS = 0;
	 public static final int TGPP_IP_CAN_TYPE_DOCSIS = 1;
	 public static final int TGPP_IP_CAN_TYPE_XDSL = 2;
	 public static final int TGPP_IP_CAN_TYPE_WIMAX = 3;
	 public static final int TGPP_IP_CAN_TYPE_3GPP2 = 4 ;
	 public static final int TGPP_IP_CAN_TYPE_3GPP_EPS = 5 ;
	 public static final int TGPP_IP_CAN_TYPE_NON_3GPP_EPS = 6;
	
	 
	 //1021 Bearer-Operation
	 public static final int TGPP_BEARER_OPERATION_TERMINATION = 0;
	 public static final int TGPP_BEARER_OPERATION_ESTABLISHMENT = 1;
	 public static final int TGPP_BEARER_OPERATION_MODIFICATION = 2;
	 
	 //2203 Sub-Session Operation
	 public static final int TGPP_SUBSESSION_OPERATION_TERMINATION = 0;
	 public static final int TGPP_SUBSESSION_OPERATION_ESTABLISHMENT = 1;
	 public static final int TGPP_SUBSESSION_OPERATION_MODIFICATION = 2;
	 
	 //1019 Pcc-Rule-Status
	 public static final int TGPP_PCC_RULE_STATUS_ACTIVE = 0;
	 public static final int TGPP_PCC_RULE_STATUS_INACTIVE = 1;
	 public static final int TGPP_PCC_RULE_STATUS_TEMPORARILY_INACTIVE = 2;
	 
	 //1006 Event-Trigger
	 public static final int TGPP_OUT_OF_CREDIT = 15;
	 public static final String TGPP_EVENT_TRIGGER_USAGE_REPORT_STR = "USAGE_REPORT";
	 
	 //10415:511 Flow-Status
	 public static final int TGPP_FLOW_STATUS_ENABLED_UPLINK	= 0;
	 public static final int TGPP_FLOW_STATUS_ENABLED_DOWNLINK	= 1;
	 public static final int TGPP_FLOW_STATUS_ENABLED	= 2;
	 public static final int TGPP_FLOW_STATUS_DISABLED	= 3;
	 public static final int TGPP_FLOW_STATUS_REMOVED	= 4;
	
	 //0:459 User-Equipment-Info-Type
	 public static final int IMEISV			= 0;
 	 public static final int MAC			= 1;
	 public static final int EUI64			= 2;
	 public static final int MODIFIED_EUI64	= 3;
	
	//0:450 Subscription-Id-Type
	 public static final int END_USER_E164		= 0;
	 public static final int END_USER_IMSI		= 1;
	 public static final int END_USER_SIP_URI	= 2;
	 public static final int END_USER_NAI		= 3;
	 public static final int END_USER_PRIVATE	= 4;
	 
	 
	 //10415:1068 Usage-Metering-Level
	 public static final int TGPP_USAGE_METERING_SESSION_LEVEL 	= 0;
	 public static final int TGPP_USAGE_METERING_PCC_RULE_LEVEL 	= 1;
	 
	//10415:2904 SL-Request-Type
	 public static final int TGPP_SL_REQUET_TYPE_INITIAL_REQUEST 	= 0;
	 public static final int TGPP_SL_REQUET_TYPE_INTERMEDIATE_REQUEST 	= 1;
	 
	 //21067:65629 EC_SESSION_ACTION
	 public static final int EC_SESSION_INSERT_ACTION = 1;
	 public static final int EC_SESSION_DELETE_ACTION = 2;
	 public static final int EC_SESSION_UPDATE_ACTION = 3;
		
		
}
