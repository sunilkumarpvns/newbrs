package com.elitecore.coreradius.commons.util.constants;

public class RadiusAttributeConstants {

	public static final int USER_NAME       		= 1;
	public static final int USER_PASSWORD   		= 2;
	public static final int CHAP_PASSWORD			= 3;
	public static final int NAS_IP_ADDRESS			= 4;
	public static final int NAS_PORT			    = 5;
	public static final int SERVICE_TYPE			= 6;
	public static final int FRAMED_PROTOCOL			= 7;
	public static final int FRAMED_IP_ADDRESS		= 8;
	public static final int FRAMED_IP_NETMASK		= 9;
	public static final int FRAMED_ROUTING			= 10;
	public static final int FILTER_ID			    = 11;
	public static final int FRAMED_MTU			    = 12;
	public static final int FRAMED_COMPRESSION		= 13;
	public static final int LOGIN_IP_HOST			= 14;
	public static final int LOGIN_SERVICE			= 15;
	public static final int LOGIN_TCP_PORT			= 16;
								//17      (unassigned)
	public static final int REPLY_MESSAGE			= 18;
	public static final int CALLBACK_NUMBER			= 19;
	public static final int CALLBACK_ID			    = 20;
								//21      (unassigned)
	public static final int FRAMED_ROUTE			= 22;
	public static final int FRAMED_IPX_NETWORK		= 23;
	public static final int STATE				    = 24;
	public static final int CLASS				    = 25;
	public static final int VENDOR_SPECIFIC			= 26;
	public static final int SESSION_TIMEOUT			= 27;
	public static final int IDLE_TIMEOUT			= 28;
	public static final int TERMINATION_ACTION		= 29;
	public static final int CALLED_STATION_ID		= 30;
	public static final int CALLING_STATION_ID		= 31;
	public static final int NAS_IDENTIFIER			= 32;
	public static final int PROXY_STATE			    = 33;
	public static final int LOGIN_LAT_SERVICE		= 34;
	public static final int LOGIN_LAT_NODE			= 35;
	public static final int LOGIN_LAT_GROUP			= 36;
	public static final int FRAMED_APPLETALK_LINK		= 37;
	public static final int FRAMED_APPLETALK_NETWORK	= 38;
	public static final int FRAMED_APPLETALK_ZONE		= 39;

	//40_59   (reserved for accounting)
	public static final int ACCT_STATUS_TYPE		= 40;
	public static final int ACCT_DELAY_TIME			= 41;
	public static final int ACCT_INPUT_OCTETS		= 42;
	public static final int ACCT_OUTPUT_OCTETS		= 43;
	public static final int ACCT_SESSION_ID		    = 44;
	public static final int ACCT_AUTHENTIC			= 45;
	public static final int ACCT_SESSION_TIME		= 46;
	public static final int ACCT_INPUT_PACKETS		= 47;
	public static final int ACCT_OUTPUT_PACKETS		= 48;
	public static final int ACCT_TERMINATE_CAUSE	= 49;
	public static final int ACCT_MULTI_SESSION_ID	= 50;
	public static final int ACCT_LINK_COUNT		    = 51;
	public static final int ACCT_INPUT_GIGAWORDS	= 52;
	public static final int ACCT_OUTPUT_GIGAWORDS	= 53;
	public static final int EVENT_TIMESTAMP		    = 55;

	public static final int EGRESS_VLANID		= 56;
	public static final int INGRESS_FILTERS		= 57;
	public static final int EGRESS_VLAN_NAME	= 58;
	public static final int USER_PRIORITY_TABLE	= 59;
	
	public static final int CHAP_CHALLENGE		= 60;
	public static final int NAS_PORT_TYPE		= 61;
	public static final int PORT_LIMIT			= 62;
	public static final int LOGIN_LAT_PORT		= 63;
	public static final int TUNNEL_TYPE		        = 64;
	public static final int TUNNEL_MEDIUM_TYPE	    = 65;
	public static final int TUNNEL_CLIENT_ENDPOINT	= 66;
	public static final int TUNNEL_SERVER_ENDPOINT	= 67;
	public static final int ACCT_TUNNEL_CONNECTION	= 68;
	public static final int TUNNEL_PASSWORD		= 69;
	public static final int ARAP_PASSWORD		= 70;
	public static final int ARAP_FEATURES		= 71;
	public static final int ARAP_ZONE_ACCESS	= 72;
	public static final int ARAP_SECURITY		= 73;
	public static final int ARAP_SECURITY_DATA	= 74;
	public static final int PASSWORD_RETRY		= 75;
	public static final int PROMPT				= 76;
	public static final int CONNECT_INFO		= 77;
	public static final int CONFIGURATION_TOKEN	= 78;
	public static final int EAP_MESSAGE			= 79;
	public static final int MESSAGE_AUTHENTICATOR	  = 80;
	public static final int TUNNEL_PRIVATE_GROUP_ID	  = 81;
	public static final int TUNNEL_ASSIGNMENT_ID	  = 82;
	public static final int TUNNEL_PREFERENCE		  = 83;
	public static final int ARAP_CHALLENGE_RESPONSE	  = 84;
	public static final int ACCT_INTERIM_INTERVAL	  = 85;
	public static final int ACCT_TUNNEL_PACKETS_LOST  = 86;
	public static final int NAS_PORT_ID			= 87;
	public static final int FRAMED_POOL			= 88;
	public static final int CUI		= 89;
	public static final int TUNNEL_CLIENT_AUTH_ID      =90 ;  
	public static final int TUNNEL_SERVER_AUTH_ID      =91 ;
	
//RFC-4849 Nas-Filter-Rule
	public static final int NAS_FILTER_RULE			   =92 ;
	public static final int ORIGINATING_LINE_INFO 	   =94;
	public static final int NAS_IPV6_ADDRESS           =95 ; 
	public static final int FRAMED_INTERFACE_ID        =96 ; 
	public static final int FRAMED_IPV6_PREFIX         =97 ; 
	public static final int LOGIN_IPV6_HOST            =98 ; 
	public static final int FRAMED_IPV6_ROUTE          =99 ; 
	public static final int FRAMED_IPV6_POOL           =100 ;
	public static final int ERROR_CAUSE                =101 ;


//	Digest Authentication Attributes ( RFC- 4590,5090)	
	public static final int DIGEST_RESPONSE = 103;
	public static final int DIGEST_REALM = 104;
	public static final int DIGEST_NONCE = 105;
	public static final int DIGEST_RESPONSE_AUTH = 106;
	public static final int DIGEST_NEXTNONCE = 107;
	public static final int DIGEST_METHOD = 108;
	public static final int DIGEST_URI = 109;
	public static final int DIGEST_QOP = 110;
	public static final int DIGEST_ALGORITHM = 111;
	public static final int DIGEST_ENTITY_BODY_HASH = 112;
	public static final int DIGEST_CNONCE = 113;
	public static final int DIGEST_NONCE_COUNT = 114;
	public static final int DIGEST_USERNAME = 115;
	public static final int DIGEST_OPAQUE = 116;
	public static final int DIGEST_AUTH_PARAM = 117;
	public static final int DIGEST_AKA_AUTS = 118;
	public static final int DIGEST_DOMAIN = 119;
	public static final int DIGEST_STALE = 120;
	public static final int DIGEST_HA1 = 121;
	public static final int SIP_AOR = 122;
	
	public static final int DRAFT_DIGEST_RESPONSE            =206 ;
	public static final int DRAFT_DIGEST_ATTRIBUTES          =207 ; 
	
	
	// VENDOR SPECIFIC ATTRIBUTES 
	// VENDOR ID 21067 Elitecore Technologies Ltd.
	
	public static final int ELITE_24_ONLINE_AVPAPIR 	    = 0;
	public static final int ELITE_TEKELEC_AVPAIR 		    = 1;
	public static final int ELITE_CYBEROAM_AVPAIR 	        = 2;
	public static final int ELITE_IP_POOL_AVPAIR		    = 3;
	public static final int ELITE_RATING_AVPAIR             = 4;             
	public static final int ELITE_RESOURCE_MANAGER_AVPAIR   = 5;
	public static final int ELITE_PROFILE_ACCOUNT_STATUS	= 111;
	public static final int ELITE_PROFILE_ACCOUNT_TYPE	 	= 112;
	public static final int ELITE_PROFILE_CREDIT_LIMIT 		= 113;
	public static final int ELITE_PROFILE_USER_GROUP		= 114;
	public static final int ELITE_PROFILE_EXPIRY_DATE 		= 115;
	public static final int ELITE_PROFILE_IDENTITY 			= 116;
	public static final int ELITE_PROFILE_AVPAIR 			= 117;
	public static final int ELITE_GRACE_TYPE				= 123;
	public static final int ELITE_GROUP_BANDWIDTH			= 130;
	public static final int ELITE_PROFILE_IMSI			    = 146;
	public static final int ELITE_PROFILE_MEID		 	    = 147;
	public static final int ELITE_PROFILE_MSISDN			= 148;
	public static final int ELITE_PROFILE_MDN			    = 149;
	public static final int ELITE_PROFILE_IMEI			    = 150;
	public static final int ELITE_NAI_DECORATION 			= 204;
	public static final int ELITE_SATISFIED_ESI				= 205;
	public static final int ELITE_REQUESTER_ID				= 206;
	
	public static final int ELITE_EAP_CODE 		= 211;
	public static final int ELITE_EAP_METHOD 	= 212;

	public static final int ELITE_PARAM1					    = 1;
	public static final int ELITE_PARAM2					    = 2;
	public static final int ELITE_PARAM3					    = 3;
	public static final int ELITE_PARAM4					    = 4;
	public static final int ELITE_PARAM5					    = 5;
	public static final int FRAMED_IPv6_ADDRESS					= 4;
	public static final int ELITE_SRC_ADDR					    = 118;
	public static final int ELITE_DST_ADDR					    = 119;
	public static final int ELITE_SRC_PROXY_ADDR			    = 120;
	public static final int ELITE_DST_PROXY_ADDR			    = 121;
	public static final int ELITE_VENDOR_TYPE				    = 122;
	public static final int HOTLINE_REASON					    = 124;
	public static final int ELITE_PROFILE_USERNAME			    = 125;
	public static final int ELITE_AUTHENTICATED_USER_ID		    = 126;
	public static final int ELITE_PPAC_AVPAIR				    = 131;
	public static final int ELITE_PPAQ_AVPAIR				    = 132;
	public static final int ELITE_PTS_AVPAIR				    = 133;
	public static final int ELITE_PACKET_TYPE				    = 127;
	public static final int ELITE_RESPONSE_TIME				    = 128;
	public static final int ELITE_SATISFIED_POLICIES		    = 129;
	public static final int ELITE_SATISFIED_HOTLINE_POLICIES    = 142;
	public static final int ELITE_SATISFIED_SERVICE_POLICY	    = 141;
	public static final int ELITE_SERVER_INSTANCE_ID		    = 143;
	public static final int ELITE_CONCURRENT_LOGIN_POLICY_NAME	= 144;
	public static final int ELITE_FRAMED_POOL_NAME              = 145;
	
	public static final int ELITE_GEO_LOCATION              	= 155;
	public static final int ELITE_DEVICE_NAME              		= 156;
	public static final int ELITE_DEVICE_VENDOR              	= 157;
	public static final int ELITE_DEVICE_PORT              		= 158;
	public static final int ELITE_DEVICE_VLAN              		= 159;
	
	public static final int ELITE_TIME_QUOTA_THRESHOLD          = 203;
	
	public static final int EC_SERVER_NAME						= 195;
	public static final int EC_DOMAIN_NAME						= 196;
	public static final int EC_SOFTWARE_VERSION					= 197;
	public static final int EC_SOFTWARE_REVISION				= 198;
	
	public static final int EC_DTA_MSCC 						= 134;
	public static final int EC_DTA_MSCC_TIME				    = 1;
	public static final int EC_DTA_MSCC_IN_OCTETS			    = 2;
	public static final int EC_DTA_MSCC_OUT_OCTETS			    = 3;
	public static final int EC_DTA_MSCC_TOTAL_OCTETS		    = 4;
	public static final int EC_DTA_MSCC_MONEY				    = 5;
	
	//-----------------------------------------------------------------------------
	//Following are the  Constants for String Attribute Name 
	//VENDOR ID 21067 Elitecore Technologies Ltd.
	//-----------------------------------------------------------------------------
	public static final String ELITE_24_ONLINE_AVPAPIR_STR 	     = "24Online-AVPair";
	public static final String ELITE_TEKELEC_AVPAIR_STR 		 = "Tekelec-AVPair";
	public static final String ELITE_CYBEROAM_AVPAIR_STR 	     = "Cyberoam-AVPair";
	public static final String ELITE_IP_POOL_AVPAIR_STR		     = "IP-Pool-AVPair";
	public static final String ELITE_RATING_AVPAIR_STR           = "Rating_AVPair";
	public static final String ELITE_RESOURCE_MANAGER_AVPAIR_STR = "Resource-Manager-AVPair";	
	public static final String ELITE_PROFILE_ACCOUNT_STATUS_STR	 = "Profile-Account-Status";
	public static final String ELITE_PROFILE_ACCOUNT_TYPE_STR 	 = "Profile-Account-Type";
	public static final String ELITE_PROFILE_CREDIT_LIMIT_STR 	 = "Profile-Credit-Limit";
	public static final String ELITE_PROFILE_USER_GROUP_STR 	 = "Profile-User-Group";
	public static final String ELITE_PROFILE_EXPIRY_DATE_STR 	 = "Profile-Expiry-Date";
	public static final String ELITE_PROFILE_IDENTITY_STR 		 = "Profile-Identity";
	public static final String ELITE_PROFILE_AVPAIR_STR 		 = "Profile-AVPair";
	public static final String ELITE_PROFILE_IMSI_STR			 = "Profile-IMSI";
	public static final String ELITE_PROFILE_MEID_STR		 	 = "Profile-MEID";
	public static final String ELITE_PROFILE_MSISDN_STR		     = "Profile-MSISDN";
	public static final String ELITE_PROFILE_MDN_STR		     = "Profile-MDN";
	public static final String ELITE_PROFILE_IMEI_STR	         = "Profile-IMEI";
	public static final String ELITE_NAI_DECORATION_STR 		 = "NAI-Decoration";
	public static final String ELITE_SATISFIED_ESI_STR  		 = "Satisfied-ESI";
	public static final String ELITE_EAP_ATTRIBUTE_STR 			 = "EAP-Attribute";
	
	//sub-type
	public static final String ELITE_PARAM1_STR					 = "Param1";
	public static final String ELITE_PARAM2_STR					 = "Param2";
	public static final String ELITE_PARAM3_STR					 = "Param3";
	public static final String ELITE_SRC_ADDR_STR			 	 = "Src-Addr";
	public static final String ELITE_DST_ADDR_STR				 = "Dst-Addr";
	public static final String ELITE_SRC_PROXY_ADDR_STR			 = "Src-Proxy-Addr";
	public static final String ELITE_DST_PROXY_ADDR_STR			 = "Dst-Proxy-Addr";
	public static final String ELITE_VENDOR_TYPE_STR			 = "Vendor-Type";
	public static final String ELITE_GRACE_TYPE_STR      		 = "Grace-Type";
	public static final String HOTLINE_REASON_STR				 = "Hotline-Reason";
	public static final String ELITE_PROFILE_USERNAME_STR		 = "Profile-User-Name";
	public static final String ELITE_AUTHENTICATED_USER_ID_STR	 = "Authenticated-User-Identity";	
	public static final String ELITE_PPAC_AVPAIR_STR			 = "PPAC-AVPair";
	public static final String ELITE_PPAQ_AVPAIR_STR			 = "PPAQ-AVPair";
	public static final String ELITE_PTS_AVPAIR_STR				 = "PTS-AVPair";
	public static final String ELITE_PACKET_TYPE_STR			 = "Packet-Type";
	public static final String ELITE_RESPONSE_TIME_STR			 = "Response-Time";
	public static final String ELITE_SATISFIED_POLICIES_STR	 	 = "Satisfied-Policies";
	public static final String ELITE_SATISFIED_SERVICE_POLICY_STR= "Satisfied-Service-Policy";
	public static final String ELITE_SERVER_INSTNACE_ID_STR		 = "Server-Instance-Id";
	public static final String ELITE_CONCURRENT_LOGIN_POLICY_NAME_STR = "Concurrent-Login-Policy-Name";
	public static final String ELITE_FRAMED_POOL_NAME_STR = "Framed-Pool-Name";
	public static final String ELITE_EAP_CODE_STR  				 = "EAP-Code";
	public static final String ELITE_EAP_METHOD_STR  			 = "EAP-Method";
	
	public static final String EC_SERVER_NAME_STR				= "EC-Server-Name";
	public static final String EC_DOMAIN_NAME_STR				= "EC-Domain-Name";
	public static final String EC_SOFTWARE_VERSION_STR			= "EC-Software-Version";
	public static final String EC_SOFTWARE_REVISION_STR			= "EC-Software-Revision";
	
	//	 VENDOR ID 311 Microsoft
	public static final int MS_MPPE_SEND_KEY = 16;
	public static final int MS_MPPE_RECV_KEY = 17;
	public static final int MSCHAP_CHALLENGE = 11;
	public static final int MSCHAP_RESPONSE = 1;
	public static final int MSCHAP2_RESPONSE = 25;
	public static final int MSCHAP2_SUCCESS = 26;
	
	//	-----------------------------------------------------------------------------
	//Following are the  Constants for String Attribute Name 
	//VENDOR ID 311 Microsoft
	//-----------------------------------------------------------------------------
	public static final String MS_MPPE_SEND_KEY_STR = "MS_MPPE_Send_Key";
	public static final String MS_MPPE_RECV_KEY_STR = "MS_MPPE_Recv_Key";
	public static final String MSCHAP_CHALLENGE_STR = "MSCHAP_Challenge";
	public static final String MSCHAP_RESPONSE_STR = "MSCHAP_Response";
	public static final String MSCHAP2_RESPONSE_STR = "MSCHAP2_Response";
	
	//-----------------------------------------------------------------------------
	//Following are the  Constants for (String)Standard Radius Attribute Name
	//-----------------------------------------------------------------------------
	public static final String USER_NAME_STR                = "User-Name";
	public static final String USER_PASSWORD_STR            = "User-Password"; //NOSONAR - Reason: Credentials should not be hard-coded 
	public static final String CHAP_PASSWORD_STR            = "CHAP-Password"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String NAS_IP_ADDRESS_STR           = "NAS-IP-Address"; 
	public static final String NAS_PORT_STR                 = "NAS-Port"; 
	public static final String SERVICE_TYPE_STR             = "Service-Type";
	public static final String FRAMED_PROTOCOL_STR          = "Framed-Protocol"; 
	public static final String FRAMED_IP_ADDRESS_STR        = "Framed-IP-Address"; 
	public static final String FRAMED_IP_NETMASK_STR        = "Framed-IP-Netmask"; 
	public static final String FRAMED_ROUTING_STR           = "Framed-Routing";
	public static final String FILTER_ID_STR                = "Filter-Id"; 
	public static final String FRAMED_MTU_STR               = "Framed-MTU"; 
	public static final String FRAMED_COMPRESSION_STR       = "Framed-Compression"; 
	public static final String LOGIN_IP_HOST_STR            = "Login-IP-Host";
	public static final String LOGIN_SERVICE_STR            = "Login-Service";
	public static final String LOGIN_TCP_PORT_STR           = "Login-TCP-Port";
	public static final String REPLY_MESSAGE_STR            = "Reply-Message";
	public static final String CALLBACK_NUMBER_STR          = "Callback-Number";
	public static final String CALLBACK_ID_STR              = "Callback-Id"; 
	public static final String FRAMED_ROUTE_STR             = "Framed-Route";
	public static final String FRAMED_IPX_NETWORK_STR       = "Framed-IPX-Network";
	public static final String STATE_STR                    = "State";
	public static final String CLASS_STR                    = "Class";
	public static final String VENDOR_SPECIFIC_STR          = "Vendor-Specific";
	public static final String SESSION_TIMEOUT_STR          = "Session-Timeout";
	public static final String IDLE_TIMEOUT_STR             = "Idle-Timeout";
	public static final String TERMINATION_ACTION_STR       = "Termination-Action";
	public static final String CALLED_STATION_ID_STR        = "Called-Station-Id";
	public static final String CALLING_STATION_ID_STR       = "Calling-Station-Id";
	public static final String NAS_IDENTIFIER_STR           = "NAS-Identifier";
	public static final String PROXY_STATE_STR              = "Proxy-State";
	public static final String LOGIN_LAT_SERVICE_STR        = "Login-LAT-Service";
	public static final String LOGIN_LAT_NODE_STR           = "Login-LAT-Node";
	public static final String LOGIN_LAT_GROUP_STR          = "Login-LAT-Group";
	public static final String FRAMED_APPLETALK_LINK_STR    = "Framed-AppleTalk-Link";
	public static final String FRAMED_APPLETALK_NETWORK_STR = "Framed-AppleTalk-Network";
	public static final String FRAMED_APPLETALK_ZONE_STR    = "Framed-AppleTalk-Zone";
	public static final String ACCT_STATUS_TYPE_STR         = "Acct-Status-Type";
	public static final String ACCT_DELAY_TIME_STR          = "Acct-Delay-Time";
	public static final String ACCT_INPUT_OCTETS_STR        = "Acct-Input-Octets";
	public static final String ACCT_OUTPUT_OCTETS_STR       = "Acct-Output-Octets";
	public static final String ACCT_SESSION_ID_STR          = "Acct-Session-Id";
	public static final String ACCT_AUTHENTIC_STR           = "Acct-Authentic";
	public static final String ACCT_SESSION_TIME_STR        = "Acct-Session-Time";
	public static final String ACCT_INPUT_PACKETS_STR       = "Acct-Input-Packets";
	public static final String ACCT_OUTPUT_PACKETS_STR      = "Acct-Output-Packets";
	public static final String ACCT_TERMINATE_CAUSE_STR     = "Acct-Terminate-Cause";
	public static final String ACCT_MULTI_SESSION_ID_STR    = "Acct-Multi-Session-Id";
	public static final String ACCT_LINK_COUNT_STR          = "Acct-Link-Count";
	public static final String ACCT_INPUT_GIGAWORDS_STR     = "Acct-Input-Gigawords";
	public static final String ACCT_OUTPUT_GIGAWORDS_STR    = "Acct-Output-Gigawords";
	public static final String EVENT_TIMESTAMP_STR          = "Event-Timestamp";
	public static final String EGRESS_VLANID_STR			= "Egress-VLANID";
	public static final String INGRESS_FILTERS_STR			= "Ingress-Filters";
	public static final String EGRESS_VLAN_NAME_STR			= "Egress-VLAN-Name";
	public static final String USER_PRIORITY_TABLE_STR		= "User-Priority-Table";
	public static final String CHAP_CHALLENGE_STR           = "CHAP-Challenge";
	public static final String NAS_PORT_TYPE_STR            = "NAS-Port-Type";
	public static final String PORT_LIMIT_STR               = "Port-Limit";
	public static final String LOGIN_LAT_PORT_STR           = "Login-LAT-Port";
	public static final String TUNNEL_TYPE_STR			    = "Tunnel-Type";
	public static final String TUNNEL_MEDIUM_TYPE_STR		= "Tunnel-Medium-Type";
	public static final String TUNNEL_CLIENT_ENDPOINT_STR	= "Tunnel-Client-Endpoint";
	public static final String TUNNEL_SERVER_ENDPOINT_STR	= "Tunnel-Server-Endpoint";
	public static final String ACCT_TUNNEL_CONNECTION_STR	= "Acct-Tunnel-Connection";
	public static final String TUNNEL_PASSWORD_STR  		= "Tunnel-Password"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String ARAP_PASSWORD_STR            = "ARAP-Password"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String ARAP_FEATURES_STR            = "ARAP-Features";
	public static final String ARAP_ZONE_ACCESS_STR         = "ARAP-Zone-Access";
	public static final String ARAP_SECURITY_STR            = "ARAP-Security";
	public static final String ARAP_SECURITY_DATA_STR       = "ARAP-Security-Data";
	public static final String PASSWORD_RETRY_STR           = "Password-Retry"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String PROMPT_STR                   = "Prompt";
	public static final String CONNECT_INFO_STR             = "Connect-Info";
	public static final String CONFIGURATION_TOKEN_STR      = "Configuration-Token";
	public static final String EAP_MESSAGE_STR              = "EAP-Message";
	public static final String TUNNEL_PRIVATE_GROUP_ID_STR	="Tunnel-Private-Group-ID";
	public static final String TUNNEL_ASSIGNMENT_ID_STR	    ="Tunnel-Assignment-ID";
	public static final String TUNNEL_PREFERENCE_STR	    ="Tunnel-Preference";
	public static final String MESSAGE_AUTHENTICATOR_STR    = "Message-Authenticator";
	public static final String ARAP_CHALLENGE_RESPONSE_STR  = "ARAP-Challenge-Response";
	public static final String ACCT_INTERIM_INTERVAL_STR    = "Acct-Interim-Interval";
	public static final String ACCT_TUNNEL_PACKETS_LOST_STR = "Acct-Tunnel-Packets-Lost";
	public static final String NAS_PORT_ID_STR              = "NAS-Port-Id";
	public static final String FRAMED_POOL_STR              = "Framed-Pool";
	public static final String TUNNEL_CLIENT_AUTH_ID_STR    = "Tunnel-Client-Auth-ID"; 
	public static final String TUNNEL_SERVER_AUTH_ID_STR    = "Tunnel-Server-Auth-ID";
	
	//This is the attribute added based on the draft RADIUS attributes for IPv6 Access Networks
	//this attribute is still in the draft stage so there is no id assigned to it
	public static final String FRAMED_IPv6_ADDRESS_STR = "Framed-IPv6-Address";
// RFC-4849 Nas Filter Rule	
	public static final String NAS_FILTER_RULE_STR			= "NAS-Filter-Rule";
	public static final String NAS_IPV6_ADDRESS_STR         = "NAS-IPv6-Address";
	public static final String FRAMED_INTERFACE_ID_STR      = "Framed-Interface-Id";
	public static final String FRAMED_IPV6_PREFIX_STR       = "Framed-IPv6-Prefix";
	public static final String LOGIN_IPV6_HOST_STR          = "Login-IPv6-Host";
	public static final String FRAMED_IPV6_ROUTE_STR        = "Framed-IPv6-Route";
	public static final String FRAMED_IPV6_POOL_STR         = "Framed-IPv6-Pool";
	public static final String ERROR_CAUSE_STR              = "Error-Cause";   
	public static final String CUI_STR = "CUI";  
	public static final String CUI_ADDED = "CUI_ADDED";
	public static final String ELITECORE_VSA_ADDED = "VSA_ADDED";
	public static final String USERNAME_ADDED = "USERNAME_ADDED";

//	Digest Authentication Attributes ( RFC- 4590,5090)	
	public static final String DIGEST_RESPONSE_STR = "Digest-Response";
	public static final String DIGEST_REALM_STR = "Digest-Realm";
	public static final String DIGEST_NONCE_STR = "Digest-Nonce";
	public static final String DIGEST_RESPONSE_AUTH_STR = "Digest-Response-Auth";
	public static final String DIGEST_NEXTNONCE_STR = "Digest-Nextnonce";
	public static final String DIGEST_METHOD_STR = "Digest-Method";
	public static final String DIGEST_URI_STR = "Digest-URI";
	public static final String DIGEST_QOP_STR = "Digest-Qop";
	public static final String DIGEST_ALGORITHM_STR = "Digest-Algorithm";
	public static final String DIGEST_ENTITY_BODY_HASH_STR = "Digest-Entity-Body-Hash";
	public static final String DIGEST_CNONCE_STR = "Digest-CNonce";
	public static final String DIGEST_NONCE_COUNT_STR = "Digest-Nonce-Count";
	public static final String DIGEST_USERNAME_STR = "Digest-Username";
	public static final String DIGEST_OPAQUE_STR = "Digest-Opaque";
	public static final String DIGEST_AUTH_PARAM_STR = "Digest-Auth-Param";
	public static final String DIGEST_AKA_AUTS_STR = "Digest-AKA-Auts";
	public static final String DIGEST_DOMAIN_STR = "Digest-Domain";
	public static final String DIGEST_STALE_STR = "Digest-Stale";
	public static final String DIGEST_HA1_STR = "Digest-HA1";
	public static final String SIP_AOR_STR = "SIP-AOR";
	
	//Cisco specific Attribute	
	public static final String CISCO_H323_CLASS_STR = "h323-Class";
	public static final int CISCO_H323_CLASS = 211;
	public static final int CISCO_SSG_SERVICE_INFO=251;
	public static final int CISCO_SSG_CONTROL_INFO=253;
	public static final int CISCO_COMMAND_CODE=252;


        //Error messages
      public static final String ERROR_MESSAGE_201 =  "Residual Session Context Removed";
      public static final String ERROR_MESSAGE_202 =  "Invalid EAP Packet (Ignored)";
      public static final String ERROR_MESSAGE_401 =  "Unsupported Attribute";
      public static final String ERROR_MESSAGE_402 =  "Missing Attribute";
      public static final String ERROR_MESSAGE_403 =  "NAS Identification Mismatch";
      public static final String ERROR_MESSAGE_404 =  "Invalid Request";
      public static final String ERROR_MESSAGE_405 =  "Unsupported Service";
      public static final String ERROR_MESSAGE_406 =  "Unsupported Extension";
      public static final String ERROR_MESSAGE_407 =  "Invalid Attribute Value";
      public static final String ERROR_MESSAGE_501 =  "Administratively Prohibited";
      public static final String ERROR_MESSAGE_502 =  "Request Not Routable (Proxy)";
      public static final String ERROR_MESSAGE_503 =  "Session Context Not Found";
      public static final String ERROR_MESSAGE_504 =  "Session Context Not Removable";
      public static final String ERROR_MESSAGE_505 =  "Other Proxy Processing Error";
      public static final String ERROR_MESSAGE_506 =  "Resources Unavailable";
      public static final String ERROR_MESSAGE_507 =  "Request Initiated";
      public static final String ERROR_MESSAGE_508 =  "Multiple Session Selection Unsupported";
      
      public static final String EC_DTA_MSCC_STR 						= "EC-DTA-MSCC";
      public static final String EC_DTA_MSCC_TIME_STR				    = "EC-DTA-MSCC-Time";
      public static final String EC_DTA_MSCC_IN_OCTETS_STR			    = "EC-DTA-MSCC-In-Octets";
      public static final String EC_DTA_MSCC_OUT_OCTETS_STR				= "EC-DTA-MSCC-Out-Octets";
      public static final String EC_DTA_MSCC_TOTAL_OCTETS_STR		    = "EC-DTA-MSCC-Total-Octets";
      public static final String EC_DTA_MSCC_MONEY_STR				    = "EC-DTA-MSCC-Money";
      
      //3gpp2 Attributes
      public static final int WM_3G_PREPAID_ACCT_QUOTA = 90;
      public static final int WM_3G_PREPAID_ACCT_CAPABILITY = 91;
      public static final int WM_3G_VOLUME_QUOTA = 2;
      public static final int WM_3G_VOLUME_THRESHOLD = 4;
      public static final int WM_3G_DURATION_QUOTA = 6;
      public static final int WM_3G_DURATION_THRESHOLD = 7;
      
      public static final long THREEGPP2_CORRELATION_ID = 44;
      
}
