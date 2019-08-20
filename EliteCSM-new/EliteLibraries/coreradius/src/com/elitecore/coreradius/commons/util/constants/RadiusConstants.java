package com.elitecore.coreradius.commons.util.constants;

public class RadiusConstants {



  /* PACKET TYPE CODES */
  public static final int ACCESS_REQUEST_MESSAGE = 1;
  public static final int ACCESS_ACCEPT_MESSAGE = 2;
  public static final int ACCESS_REJECT_MESSAGE = 3;
  public static final int ACCOUNTING_REQUEST_MESSAGE = 4;
  public static final int ACCOUNTING_RESPONSE_MESSAGE = 5;
  public static final int ACCESS_CHALLENGE_MESSAGE = 11;
  public static final int STATUS_SERVER_MESSAGE = 12;
  public static final int STATUS_CLIENT_MESSAGE = 13;

  public static final int DISCONNECTION_REQUEST_MESSAGE = 40;
  public static final int DISCONNECTION_ACK_MESSAGE = 41;
  public static final int DISCONNECTION_NAK_MESSAGE = 42;
  public static final int COA_REQUEST_MESSAGE = 43;
  public static final int COA_ACK_MESSAGE = 44;
  public static final int COA_NAK_MESSAGE = 45;

  public static final int IP_ADDRESS_ALLOCATE_MESSAGE = 50;
  public static final int IP_ADDRESS_RELEASE_MESSAGE = 51;
  public static final int IP_UPDATE_MESSAGE = 52;
  
  public static final int RESOURCE_FREE_REQUEST = 21;
  public static final int RESOURCE_FREE_RESPONSE = 22;
  public static final int RESOURCE_QUERY_REQUEST = 23;
  public static final int RESOURCE_QUERY_RESPONSE = 24;
  public static final int RESOURCE_REAUTHORIZE_REQUEST=25;
  public static final int ACCOUNTING_MESSAGE = 10;
  
  /* RFC2882 Support for NAS Reboot Request/Response */
  public static final int NAS_REBOOT_REQUEST = 26;
  public static final int NAS_REBOOT_RESPONSE = 27;

  @Deprecated
  public static final int TEST_MESSAGE = 254;
  
  public static final int TIMEOUT_MESSAGE = 253;
  public static final int NO_RM_COMMUNICATION_MESSAGE = 252;

  public static String DICTIONARY_DIRECTORY = "dictionary";
  public static String RADIUS_DICTIONARY_DIRECTORY = "radius";
  public static String DIAMETER_DICTIONARY_DIRECTORY = "diameter";
  public static String CSV_DIRECTORY = "csvfiles";
  public static String DATA_DIRECTORY = "data";

  public static final String RM_STATUS = "STATUS";
  public static final String RM_MESSAGE_OPERATION_FAILURE = "OPERATION_FAILURE";


  /* VENDOR ID Constants */

  public static final long STANDARD_VENDOR_ID = 0;
  public static final long ELITECORE_VENDOR_ID = 21067;

  public static final long WIMAX_VENDOR_ID = 24757;
  public static final String WIMAX_VENDOR_NAME = "WIMAX_VENDOR_NAME";
  public static final long MICROSOFT_VENDOR_ID = 311;
  public static final String MICROSOFT_VENDOR_NAME = "MICROSOFT_VENDOR_NAME";
  
  public static final long ERICSSON_VENDOR_ID = 193;
  public static final String ERICSSON_VENDOR_ID_STR = "ERICSSON_NAME";

  public static final long CISCO_VENDOR_ID = 9;
  
  public static final long VENDOR_3GPP2_ID = 5535;
  public static final String VENDOR_3GPP2_ID_STR = "5535";
  public static final String VENDOR_3GPP2_NAME = "VENDOR_3GPP2_NAME";
  
  public static final long VENDOR_3GPP_ID = 10415;
  public static final String VENDOR_3GPP_ID_STR = "10415";
  public static final String VENDOR_3GPP_NAME = "VENDOR_3GPP_NAME";
  
  public static final long VENDOR_ZTE_ID = 3902;
  public static final String VENDOR_ZTE_ID_STR = "3902";
  public static final String VENDOR_ZTE_NAME = "VENDOR_ZTE_NAME";

  /*
   * Wimax PPAQ sub attribute Update-Reason value Threshold Reached(3)
   */
  public static final int PPAQ_THRESHOLD_REACHED = 3;

  
  
  /*
   *  RequestContext Constants
   * This constants are used to set the respective values for the response packet 
   * in the RequestContext .
   */
  
  public static final int REQUEST_CONTEXT_ACCESS_ACCEPT = 1;
  
  public static final int REQUEST_CONTEXT_ACCESS_REJECT = 2;
  
  public static final int REQUEST_CONTEXT_ACCOUNTING_SUCCESS = 3;
  
  public static final int REQUEST_CONTEXT_ACCOUNTING_FAILURE = 4;
  
  public static final String HOTLINE_APPLICABLE = "HOTLINE_APPLICABLE";

  public static final Integer NORMAL_AUTHORIZATION_PROCESS = 0;
  
  public static final Integer POLICY_BASED_HOTLINING = 1;
  
  public static final Integer PROFILE_BASED_HOTLINING = 2;

  public static final String CUI = "0:89=";
  
  public static final String USER_IDENTITY ="USER_IDENTITY";
  public static final String DEFAULT_IDENTITY_ATTR_STR ="0:1";
  
  
  
  	public static final String RM_POLICY="RM_POLICY";
	public static final String PROXY_POLICY="PROXY_POLICY";
	public static final String BROADCASTING_POLICY="BROADCASTING_POLICY";
	
	public static final String RADIUS_AUTHORIZATION_POLICY = "RADIUS_AUTHORIZATION_POLICY";

	public static final String ACCESS_POLICY="ACCESS_POLICY";
	public static final String RADIUS_POLICY="RADIUS_POLICY";

	public static final String SUCCESS="SUCCESS";
	public static final String FAILED = "FAILED";
	public static final String PARTIAL_SUCCESS="PARTIAL SUCCESS";
	
	public static final String AUTH_SERVICE_STATISTICS = "AuthServiceStatistics";
	public static final String ACCT_SERVICE_STATISTICS = "AcctServiceStatistics";
	public static final String SERVER_STATISTICS = "ServerStatistics";
	
	
	public static final int SESSION_CLOSE_ACTION_NONE=3;
	public static final int SESSION_CLOSE_ACTION_GENERATE_DISCONNECT=1;
	public static final int SESSION_CLOSE_ACTION_GENERATE_STOP=2;
	public static final int SESSION_CLOSE_ACTION_GENERATE_DM_AND_STOP=4;

	public static final int SESSION_OVERRIDE_ACTION_NONE=3;
	public static final int SESSION_OVERRIDE_ACTION_GENERATE_DISCONNECT=1;
	public static final int SESSION_OVERRIDE_ACTION_GENERATE_STOP=2;
	public static final int SESSION_OVERRIDE_ACTION_GENERATE_DM_AND_STOP=4;
	
	public static final int STATUS_SERVER_MESSAGE_RESERVED_ID = 0;
	
	public static final String NAS_COMMUNICATOR_SELECTED = "NAS_COMMUNICATOR_SELECTED";
}
