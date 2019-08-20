package com.elitecore.coreeap.util.constants;

public class AttributeConstants {

		public static final String STANDARD_USER_NAME      = "0:1";
		public static final String STANDARD_EAP_MESSAGE    = "0:79";
		public static final String STANDARD_EAP_PAYLOAD    = "0:462";
		public static final String STANDARD_USER_PASSWORD  = "0:2"; //NOSONAR - Reason: Credentials should not be hard-coded
		public static final String STANDARD_CHAP_PASSWORD  = "0:3"; //NOSONAR - Reason: Credentials should not be hard-coded
		public static final String STANDARD_CHAP_CHALLENGE = "0:60";
		public static final String MICROSOFT_MSCHAP_CHALLENGE = "311:11";
		public static final String MICROSOFT_MSCHAP_RESPONSE  = "311:1";
		public static final String MICROSOFT_MSCHAP2_RESPONSE = "311:25";
		
		public static final int STANDARD_VENDOR_ID		= 0;
		public static final int MICROSOFT_VENDOR_ID 	= 311;
		public static final int ELITECORE_VENDOR_ID 	= 21067;
		
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
		public static final int TUNNEL_CLIENT_AUTH_ID      =90 ;  
		public static final int TUNNEL_SERVER_AUTH_ID      =91 ; 
		public static final int NAS_IPV6_ADDRESS           =95 ; 
		public static final int FRAMED_INTERFACE_ID        =96 ; 
		public static final int FRAMED_IPV6_PREFIX         =97 ; 
		public static final int LOGIN_IPV6_HOST            =98 ; 
		public static final int FRAMED_IPV6_ROUTE          =99 ; 
		public static final int FRAMED_IPV6_POOL           =100 ;
		public static final int ERROR_CAUSE                =101 ;
		public static final int DIGEST_RESPONSE            =206 ;
		public static final int DIGEST_ATTRIBUTES          =207 ; 


		
		// VENDOR SPECIFIC ATTRIBUTES 
		// VENDOR ID 21067 Elitecore Technologies Ltd.
		
		public static final int ELITE_24_ONLINE_AVPAPIR 	= 0;
		public static final int ELITE_TEKELEC_AVPAIR 		= 1;
		public static final int ELITE_CYBEROAM_AVPAIR 	= 2;
		public static final int ELITE_IP_POOL_AVPAIR		= 3;
		public static final int ELITE_RESOURCE_MANAGER_AVPAIR   = 5;
		
		//-----------------------------------------------------------------------------
		//Following are the  Constants for String Attribute Name 
		//VENDOR ID 21067 Elitecore Technologies Ltd.
		//-----------------------------------------------------------------------------
		public static final String ELITE_24_ONLINE_AVPAPIR_STR 	     = "24Online-AVPair";
		public static final String ELITE_TEKELEC_AVPAIR_STR 		 = "Tekelec-AVPair";
		public static final String ELITE_CYBEROAM_AVPAIR_STR 	     = "Cyberoam-AVPair";
		public static final String ELITE_IP_POOL_AVPAIR_STR		     = "IP-Pool-AVPair";
		public static final String ELITE_RESOURCE_MANAGER_AVPAIR_STR = "Resource-Manager-AVPair";
		
		//	 VENDOR ID 311 Microsoft
		public static final int MS_MPPE_SEND_KEY = 16;
		public static final int MS_MPPE_RECV_KEY = 17;
		
		//	-----------------------------------------------------------------------------
		//Following are the  Constants for String Attribute Name 
		//VENDOR ID 311 Microsoft
		//-----------------------------------------------------------------------------
		public static final String MS_MPPE_SEND_KEY_STR = "MS_MPPE_Send_Key";
		public static final String MS_MPPE_RECV_KEY_STR = "MS_MPPE_Recv_Key";
		
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
		public static final String NAS_IPV6_ADDRESS_STR         = "NAS-IPv6-Address";
		public static final String FRAMED_INTERFACE_ID_STR      = "Framed-Interface-Id";
		public static final String FRAMED_IPV6_PREFIX_STR       = "Framed-IPv6-Prefix";
		public static final String LOGIN_IPV6_HOST_STR          = "Login-IPv6-Host";
		public static final String FRAMED_IPV6_ROUTE_STR        = "Framed-IPv6-Route";
		public static final String FRAMED_IPV6_POOL_STR         = "Framed-IPv6-Pool";
		public static final String ERROR_CAUSE_STR              = "Error-Cause";    
		public static final String DIGEST_RESPONSE_STR          = "Digest-Response";    
		public static final String DIGEST_ATTRIBUTES_STR        = "Digest-Attributes";
		public static final String CUI = "CUI";
}
