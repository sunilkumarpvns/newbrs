package com.elitecore.coreradius.commons.util.constants;

public class RadiusAttributeValuesConstants {

    // Service-Type or User Types
    public static final int LOGIN                       =  1;
    public static final int FRAMED                      =  2;
    public static final int CALLBACK_LOGIN              =  3;
    public static final int CALLBACK_FRAMED             =  4;
    public static final int OUTBOUND                    =  5;
    public static final int ADMINISTRATIVE              =  6;
    public static final int NAS_PROMPT                  =  7;
    public static final int AUTHENTICATE_ONLY           =  8;
    public static final int CALLBACK_NAS_PROMPT         =  9;
    public static final int Call_CHECK                  = 10;
    public static final int CALLBACK_ADMINISTRATIVE     = 11;
    public static final int VOICE                       = 12;
    public static final int FAX                         = 13;
    public static final int MODEM_RELAY                 = 14;
    public static final int IAPP_REGISTER               = 15;
    public static final int IAPP_AP_CHECK = 16;
    public static final int AUTHORIZE_ONLY = 17;

    // Framed-Protocol
    public static final int PPP                 = 1;
    public static final int SLIP                = 2;
    public static final int ARAP                = 3;
    public static final int GANDALF_SLML        = 4;
    public static final int XYLOGICS_PROPRIETARY_IPX_SLIP   = 5;
    public static final int X75_SYNCHRONOUS     = 6;

    // Framed-Routing
    public static final int NONE                = 0;
    public static final int BROADCAST           = 1;
    public static final int LISTEN              = 2;
    public static final int BROADCAST_LISTEN    = 3;

    // Framed-Compression
    //public static final int None                  = 0;
    public static final int VJ_TCP_IP_HEADER_COMPRESSION    = 1;
    public static final int IPX_HEADER_COMPRESSION          = 2;
    public static final int STAC_LZS_COMPRESSION            = 3;

    // Login-Service
    public static final int TELNET          = 0;
    public static final int RLOGIN          = 1;
    public static final int TCP_CLEAR       = 2;
    public static final int PORTMASTER      = 3;
    public static final int LAT             = 4;
    public static final int X25_PAD         = 5;
    public static final int X25_T3POS       = 7;
    public static final int TCP_CLEAR_QUIET = 8;
    
    //TCP-Login-Port
    public static final int TCP_TELNET		= 23 ;
    public static final int TCP_RLOGIN		= 513;
    public static final int TCP_RSH			= 514;
    
    // Termination-Action
    public static final int DEFAULT         = 0;
    public static final int RADIUS_REQUEST  = 1;
    //Acct-Status-Type
    public static final int START			= 1 ;  
    public static final int STOP			= 2 ;		
    public static final int INTERIM_UPDATE		= 3 ;		
    public static final int ALIVE			= 3 ;	
    public static final int ACCOUNTING_ON		= 7 ;
    public static final int ACCOUNTING_OFF		= 8 ;  
    public static final int TUNNEL_START		= 9 ; 
    public static final int TUNNEL_STOP		= 10; 
    public static final int TUNNEL_REJECT		= 11; 
    public static final int TUNNEL_LINK_START	= 12; 
    public static final int TUNNEL_LINK_STOP	= 13; 
    public static final int TUNNEL_LINK_REJECT	= 14; 
    public static final int FAILED			= 15; 
    
    //Authentication Type
    
    public static final int RADIUS			= 1;
    public static final int LOCAL			= 2;
    public static final int REMOTE			= 3;
    public static final int DIAMETER		= 4;

    // NAS-PORT-TYPE
    public static final int ASYNC           =  0;
    public static final int SYNC            =  1;
    public static final int ISDN_SYNC       =  2;
    public static final int ISDN_ASYNC_V120 =  3;
    public static final int ISDN_ASYNC_V110 =  4;
    public static final int VIRTUAL         =  5;
    public static final int PIAFS           =  6;
    public static final int HDLC_CLEAR_CHANNEL  =  7;
    public static final int X25             =  8;
    public static final int X75             =  9;
    public static final int G3_FAX          = 10;
    public static final int SDSL            = 11;
    public static final int ADSL_CAP        = 12;
    public static final int ADSL_DMT        = 13;
    public static final int IDSL            = 14;
    public static final int ETHERNET        = 15;
    public static final int XDSL            = 16;
    public static final int CABLE           = 17;
    public static final int WIRELESS_OTHER  = 18;
    public static final int WIRELESS_IEEE_802_11    = 19;
    public static final int TOKEN_RING = 20;
    public static final int FDDI = 21;
    public static final int WIRELESS_CDMA2000 = 22;
    public static final int WIRELESS_UTMS = 23;
    public static final int WIRELESS_1X_EV = 24;
    public static final int IAAP = 25;
    public static final int FTTP = 26;

    //termination cause
    public static final int  USER_REQUEST              = 1  ;  
    public static final int  LOST_CARRIER              = 2  ;  
    public static final int  LOST_SERVICE              = 3  ;  
    public static final int  IDLE_TIMEOUT              = 4  ;  
    public static final int  SESSION_TIMEOUT           = 5  ;  
    public static final int  ADMIN_RESET               = 6  ;  
    public static final int  ADMIN_REBOOT              = 7  ;  
    public static final int  PORT_ERROR                = 8  ;  
    public static final int  NAS_ERROR                 = 9  ;  
    public static final int  NAS_REQUEST               = 10 ;  
    public static final int  NAS_REBOOT                = 11 ;  
    public static final int  PORT_UNNEEDED             = 12 ;  
    public static final int  PORT_PREEMPTED            = 13 ;  
    public static final int  PORT_SUSPENDED            = 14 ;  
    public static final int  SERVICE_UNAVAILABLE       = 15 ;  
    public static final int  CALLBACK                  = 16 ;  
    public static final int  USER_ERROR                = 17 ;  
    public static final int  HOST_REQUEST              = 18 ;  
    public static final int  SUPPLICANT_RESTART	       = 19 ; 
    public static final int  REAUTHENTICATION_FAILURE  = 20 ;  
    public static final int  PORT_REINIT		       = 21 ;
    public static final int  PORT_DISABLED	      	   = 22 ;       

    public static final int IP				= 1 ;
    public static final int PPTP			= 1 ;
    public static final int L2F 			= 2 ;
    public static final int L2TP 			= 3 ;
    public static final int ATMP			= 4 ;
    public static final int VTP				= 5 ;
    public static final int AH 				= 6 ;
    public static final int IP_IP 			= 7 ;
    public static final int MIN_IP_IP		= 8 ;
    public static final int ESP				= 9 ;
    public static final int GRE				= 10; 
    public static final int DVS				= 11; 
    public static final int IP_IN_IP_TUNNELING	= 12; 
    public static final int VLAN			= 13; 	
    
    //Prompt
    public static final int NO_ECHO		= 0;	
    public static final int ECHO		= 1;
    
    //error cause type
    public static final int RESIDUAL_CONTEXT_REMOVED	= 201; 
    public static final int INVALID_EAP_PACKET		= 202; 
    public static final int UNSUPPORTED_ATTRIBUTE		= 401; 
    public static final int MISSING_ATTRIBUTE		= 402; 
    public static final int NAS_IDENTIFICATION_MISMATCH	= 403; 
    public static final int INVALID_REQUEST			= 404; 
    public static final int UNSUPPORTED_SERVICE		= 405; 
    public static final int UNSUPPORTED_EXTENSION		= 406; 
    public static final int ADMINISTRATIVELY_PROHIBITED	= 501; 
    public static final int PROXY_REQUEST_NOT_ROUTABLE	= 502; 
    public static final int SESSION_CONTEXT_NOT_FOUND	= 503; 
    public static final int SESSION_CONTEXT_NOT_REMOVABLE	= 504; 
    public static final int PROXY_PROCESSING_ERROR		= 505; 
    public static final int RESOURCES_UNAVAILABLE		= 506; 
    public static final int REQUEST_INITIATED		= 507; 

    
    /* ******************    Constant Attribute Types  *************************/
    /* *****************  Attributes and sub attributes for SIP ***************/

     // SIP DIGEST AUTH - draft-sterman-aaa-sip-00
     public static final int DIGEST_RESPONSE         = 206;
     public static final int DIGEST_ATTRIBUTE        = 207;


     // SIP DIGEST AUTH - draft-sterman-aaa-sip-00
     public static final int SIP_REALM                   = 1;
     public static final int SIP_NONCE                   = 2;
     public static final int SIP_METHOD                  = 3;
     public static final int SIP_URI                     = 4;
     public static final int SIP_QOP                     = 5;
     public static final int SIP_ALGORITHM               = 6;
     public static final int SIP_BODY_DIGEST             = 7;
     public static final int SIP_CNONCE                  = 8;
     public static final int SIP_NONCE_COUNT             = 9;
     public static final int SIP_USER_NAME               = 10;
    
     //-----------------------------------------------------------------------------
     //Constants for String Value of Radius Attributes
     //-----------------------------------------------------------------------------
     //service type
     public static final String LOGIN_USER_STR		    	= "Login-User";
     public static final String FRAMED_USER_STR		    	= "Framed-User";
     public static final String CALLBACK_LOGIN_USER_STR	    = "Callback-Login-User";
     public static final String CALLBACK_FRAMED_USER_STR    = "Callback-Framed-User";
     public static final String OUTBOUND_USER_STR	        = "Outbound-User";
     public static final String ADMINISTRATIVE_USER_STR	    = "Administrative-User";
     public static final String NAS_PROMPT_USER_STR	        = "NAS-Prompt-User";
     public static final String AUTHENTICATE_ONLY_STR	    = "Authenticate-Only";
     public static final String CALLBACK_NAS_PROMPT_STR	    = "Callback-NAS-Prompt";
     public static final String CALL_CHECK_STR		        = "Call-Check";
     public static final String CALLBACK_ADMINISTRATIVE_STR = "Callback-Administrative";
     public static final String VOICE_STR		            = "Voice";
     public static final String FAX_STR			    		= "Fax";
     public static final String MODEM_RELAY_STR		    	= "Modem-Relay";
     public static final String IAPP_REGISTER_STR	        = "IAPP-Register";
     public static final String IAPP_AP_CHECK_STR	        = "IAPP-AP-Check";
     public static final String AUTHORIZE_ONLY_STR			= "Authorize-Only";
     
     //framed protocol
     public static final String PPP_STR			    		= "PPP";
     public static final String SLIP_STR			    	= "SLIP";
     public static final String ARAP_STR			    	= "ARAP";
     public static final String GANDALF_SLML_STR		    = "Gandalf-SLML";
     public static final String XYLOGICS_IPX_SLIP_STR	    = "Xylogics-IPX-SLIP";
     public static final String X75_SYNCHRONOUS_STR	        = "X.75-Synchronous";
     public static final String GPRS_PDP_CONTEXT_STR	    = "GPRS-PDP-Context";
     
     //Framed Routing Values    
     public static final String NONE_STR		            = "None";
     public static final String BROADCAST_STR	            = "Broadcast";
     public static final String LISTEN_STR		            = "Listen";
     public static final String BROADCAST_LISTEN_STR        = "Broadcast-Listen";

     //Framed Compression Types	
     //public static final String NONE_STR		            = "None";	
     public static final String VAN_JACOBSON_TCP_IP_STR	    = "Van-Jacobson-TCP-IP";
     public static final String IPX_HEADER_COMPRESSION_STR	= "IPX-Header-Compression";
     public static final String STAC_LZS_STR		        = "Stac-LZS";
     
     //Login-Service
     public static final String TELNET_STR		            = "Telnet";
     public static final String RLOGIN_STR		            = "Rlogin";
     public static final String TCP_CLEAR_STR	            = "TCP-Clear";
     public static final String PORTMASTER_STR	            = "PortMaster";
     public static final String LAT_STR		            	= "LAT";
     public static final String X25_PAD_STR		            = "X25-PAD";
     public static final String X25_T3POS_STR	            = "X25-T3POS";
     public static final String TCP_CLEAR_QUIET_STR	        = "TCP-Clear-Quiet";
     
     //TCP-Login-Port
     public static final String TCP_TELNET_STR				= "Telnet"  ;
     public static final String TCP_RLOGIN_STR				= "Rlogin"  ;
     public static final String TCP_RSH_STR					= "Rsh"	;

     
     //Termination-Action
     public static final String DEFAULT_STR					= "Default";
     public static final String RADIUS_REQUEST_STR  		= "RADIUS-Request";  
     
     //ACCT-Status-Type
     public static final String START_STR 					= "Start";
	 public static final String STOP_STR 					= "Stop";
	 public static final String INTERIM_UPDATE_STR 			= "Interim-Update";
 	 public static final String ALIVE_STR					= "Alive";
	 public static final String ACCOUNTING_ON_STR			= "Accounting-On";
	 public static final String ACCOUNTING_OFF_STR 			= "Accounting-Off";
	 public static final String TUNNEL_START_STR 			= "Tunnel-Start";
	 public static final String TUNNEL_STOP_STR 			= "Tunnel-Stop";
	 public static final String TUNNEL_REJECT_STR 			= "Tunnel-Reject";
	 public static final String TUNNEL_LINK_START_STR 		= "Tunnel-Link-Start";
	 public static final String TUNNEL_LINK_STOP_STR 		= "Tunnel-Link-Stop";
	 public static final String TUNNEL_LINK_REJECT_STR 		= "Tunnel-Link-Reject";
	 public static final String FAILED_STR 					= "Failed";
     
	 //Authentication Type
	 public static final String RADIUS_STR					= "RADIUS";	
	 public static final String LOCAL_STR					= "Local";
	 public static final String REMOTE_STR					= "Remote";	
	 public static final String DIAMETER_STR				= "Diameter";
	 
     // NAS-PORT-TYPE
     public static final String ASYNC_STR			 		= "Async";	
     public static final String SYNC_STR			 		= "Sync";	
     public static final String ISDN_STR			 		= "ISDN";	
     public static final String ISDN_V120_STR				= "ISDN-V120";
     public static final String ISDN_V110_STR				= "ISDN-V110";
     public static final String VIRTUAL_STR			 		= "Virtual";	
     public static final String PIAFS_STR			 		= "PIAFS";
     public static final String HDLC_CLEAR_CHANNEL_STR	 	= "HDLC-Clear-Channel";
     public static final String X25_STR			 			= "X.25";	
     public static final String X75_STR			 			= "X.75";	
     public static final String G3_FAX_STR			 		= "G.3-Fax";	
     public static final String SDSL_STR			 		= "SDSL";
     public static final String ADSL_CAP_STR		 		= "ADSL-CAP";
     public static final String ADSL_DMT_STR		 		= "ADSL-DMT";
     public static final String IDSL_STR			 		= "IDSL";
     public static final String ETHERNET_STR		 		= "Ethernet";
     public static final String XDSL_STR			 		= "xDSL";
     public static final String CABLE_STR			 		= "Cable";
     public static final String WIRELESS_OTHER_STR		 	= "Wireless-Other";	
     public static final String WIRELESS_802_11_STR		 	= "Wireless-802.11";	
     public static final String TOKEN_RING_STR		 		= "Token-Ring";
     public static final String FDDI_STR			 		= "FDDI";
     public static final String WIRELESS_CDMA2000_STR		= "Wireless-CDMA2000";
     public static final String WIRELESS_UMTS_STR		 	= "Wireless-UMTS";
     public static final String WIRELESS_1X_EV_STR		 	= "Wireless-1X-EV";
     public static final String IAPP_STR			 		= "IAPP";
     public static final String FTTP_STR					= "FTTP";
     
     //termination cause
     public static final String  USER_REQUEST_STR             = "User-Request";
     public static final String  LOST_CARRIER_STR             = "Lost-Carrier";
     public static final String  LOST_SERVICE_STR             = "Lost-Service";
     public static final String  IDLE_TIMEOUT_STR             = "Idle-Timeout";
     public static final String  SESSION_TIMEOUT_STR          = "Session-Timeout";
     public static final String  ADMIN_RESET_STR              = "Admin-Reset";
     public static final String  ADMIN_REBOOT_STR             = "Admin-Reboot";
     public static final String  PORT_ERROR_STR               = "Port-Error";
     public static final String  NAS_ERROR_STR                = "NAS-Error";
     public static final String  NAS_REQUEST_STR              = "NAS-Request";
     public static final String  NAS_REBOOT_STR               = "NAS-Reboot";
     public static final String  PORT_UNNEEDED_STR            = "Port-Unneeded";
     public static final String  PORT_PREEMPTED_STR           = "Port-Preempted";
     public static final String  PORT_SUSPENDED_STR           = "Port-Suspended";
     public static final String  SERVICE_UNAVAILABLE_STR      = "Service-Unavailable";
     public static final String  CALLBACK_STR                 = "Callback";
     public static final String  USER_ERROR_STR               = "User-Error";
     public static final String  HOST_REQUEST_STR             = "Host-Request";
     public static final String  SUPPLICANT_RESTART_STR	      = "Supplicant-Restart";
     public static final String  REAUTHENTICATION_FAILURE_STR = "Reauthentication-Failure";
     public static final String  PORT_REINIT_STR		      = "Port-Reinit";
     public static final String  PORT_DISABLED_STR	          = "Port-Disabled";
     
     
     public static final String IP_STR		= "IP";
     public static final String PPTP_STR		= "PPTP";
     public static final String L2F_STR 		= "L2F";
     public static final String L2TP_STR 		= "L2TP";
     public static final String ATMP_STR		= "ATMP";
     public static final String VTP_STR		= "VTP";
     public static final String AH_STR 		= "AH";
     public static final String IP_IP_STR 		= "IP-IP";
     public static final String MIN_IP_IP_STR	= "MIN-IP-IP";
     public static final String ESP_STR		= "ESP";
     public static final String GRE_STR		= "GRE";
     public static final String DVS_STR		= "DVS";
     public static final String IP_IN_IP_TUNNELING_STR	= "IP-in-IP-Tunneling";
     public static final String VLAN_STR		= "VLAN";
     
     //Prompt
     public static final String NO_ECHO_STR = "No-Echo";	
     public static final String ECHO_STR	= "Echo";
     
     //error cause type
     public static final String RESIDUAL_CONTEXT_REMOVED_STR	       	= "Residual-Context-Removed";
     public static final String INVALID_EAP_PACKET_STR	       	= "Invalid-EAP-Packet";
     public static final String UNSUPPORTED_ATTRIBUTE_STR		= "Unsupported-Attribute";	
     public static final String MISSING_ATTRIBUTE_STR		= "Missing-Attribute";	
     public static final String NAS_IDENTIFICATION_MISMATCH_STR	       	= "NAS-Identification-Mismatch";
     public static final String INVALID_REQUEST_STR			= "Invalid-Request";		
     public static final String UNSUPPORTED_SERVICE_STR		= "Unsupported-Service";	
     public static final String UNSUPPORTED_EXTENSION_STR		= "Unsupported-Extension";		
     public static final String ADMINISTRATIVELY_PROHIBITED_STR	= "Administratively-Prohibited";	
     public static final String PROXY_REQUEST_NOT_ROUTABLE_STR	= "Proxy-Request-Not-Routable";	
     public static final String SESSION_CONTEXT_NOT_FOUND_STR		= "Session-Context-Not-Found";	
     public static final String SESSION_CONTEXT_NOT_REMOVABLE_STR		= "Session-Context-Not-Removable";	
     public static final String PROXY_PROCESSING_ERROR_STR		= "Proxy-Processing-Error";		
     public static final String RESOURCES_UNAVAILABLE_STR		= "Resources-Unavailable";		
     public static final String REQUEST_INITIATED_STR		= "Request-Initiated";	
 
     //sip
     public static final String DIGEST_RESPONSE_STR			= "Digest-Response";
     public static final String DIGEST_ATTRIBUTES_STR       = "Digest-Attributes";
     
     public static final int VOLUME = 1;
     public static final int DURATION = 2;
     public static final int VOLUME_DURATION = 3;
}
