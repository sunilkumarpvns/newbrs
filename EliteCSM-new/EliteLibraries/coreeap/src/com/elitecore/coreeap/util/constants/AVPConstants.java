package com.elitecore.coreeap.util.constants;

public class AVPConstants {
	public static final int ACCT_INTERIM_INTERVAL         	= 85 ;
	public static final int ACCOUNTING_REALTIME_REQUIRED  	=483 ;
	public static final int ACCT_MULTI_SESSION_ID         	= 50 ;
	public static final int ACCOUNTING_RECORD_NUMBER      	=485 ;
	public static final int ACCOUNTING_RECORD_TYPE        	=480 ;
	public static final int ACCOUNTING_SESSION_ID         	= 44 ;
	public static final int ACCOUNTING_SUB_SESSION_ID     	=287 ;
	public static final int ACCT_APPLICATION_ID           	=259 ;
	public static final int AUTH_APPLICATION_ID           	=258 ;
	public static final int AUTH_REQUEST_TYPE             	=274 ;
	public static final int AUTHORIZATION_LIFETIME        	=291 ;
	public static final int AUTH_GRACE_PERIOD             	=276 ;
	public static final int AUTH_SESSION_STATE            	=277 ;
	public static final int RE_AUTH_REQUEST_TYPE          	=285 ;
	public static final int CLASS                         	= 25 ;
	public static final int DESTINATION_HOST              	=293 ;
	public static final int DESTINATION_REALM             	=283 ;
	public static final int DISCONNECT_CAUSE              	=273 ;
	public static final int E2E_SEQUENCE_AVP              	=300 ;
	public static final int ERROR_MESSAGE                 	=281 ;
	public static final int ERROR_REPORTING_HOST          	=294 ;
	public static final int EVENT_TIMESTAMP               	= 55 ;
	public static final int EXPERIMENTAL_RESULT           	=297 ;
	public static final int EXPERIMENTAL_RESULT_CODE      	=298 ;
	public static final int FAILED_AVP                    	=279 ;
	public static final int FIRMWARE_REVISION             	=267 ;
	public static final int HOST_IP_ADDRESS               	=257 ;
	public static final int INBAND_SECURITY_ID            	=299 ;
	public static final int MULTI_ROUND_TIME_OUT          	=272 ;
	public static final int ORIGIN_HOST                   	=264 ;
	public static final int ORIGIN_REALM                  	=296 ;
	public static final int ORIGIN_STATE_ID               	=278 ;
	public static final int PRODUCT_NAME                  	=269 ;
	public static final int PROXY_HOST                    	=280 ;
	public static final int PROXY_INFO                    	=284 ;
	public static final int PROXY_STATE                   	= 33 ;
	public static final int REDIRECT_HOST                 	=292 ;
	public static final int REDIRECT_HOST_USAGE           	=261 ;
	public static final int REDIRECT_MAX_CACHE_TIME       	=262 ;
	public static final int RESULT_CODE                   	=268 ;
	public static final int ROUTE_RECORD                  	=282 ;
	public static final int SESSION_ID                    	=263 ;
	public static final int SESSION_TIMEOUT               	= 27 ;
	public static final int SESSION_BINDING               	=270 ;
	public static final int SESSION_SERVER_FAILOVER       	=271 ;
	public static final int SUPPORTED_VENDOR_ID           	=265 ;
	public static final int TERMINATION_CAUSE             	=295 ;
	public static final int USER_NAME                     	=  1 ;
	public static final int VENDOR_ID                     	=266 ;
	public static final int VENDOR_SPECIFIC_APPLICATION_ID	=260 ;
	public static final int CC_CORRELATION_ID 			= 411; 
	public static final int CC_INPUT_OCTETS   			= 412; 
	public static final int CC_MONEY          			= 413; 
	public static final int CC_OUTPUT_OCTETS  			= 414; 
	public static final int CC_REQUEST_NUMBER 			= 415; 
	public static final int CC_REQUEST_TYPE   			= 416; 
	public static final int CC_SERVICE_SPECIFIC_UNITS		= 417; 
	public static final int CC_SESSION_FAILOVER    			= 418; 	    
	public static final int CC_SUB_SESSION_ID 			= 419; 
	public static final int CC_TIME           			= 420; 
	public static final int CC_TOTAL_OCTETS   			= 421; 
	public static final int CC_UNIT_TYPE      			= 454; 
	public static final int CHECK_BALANCE_RESULT   			= 422;     
	public static final int COST_INFORMATION  			= 423; 
	public static final int COST_UNIT         			= 424; 
	public static final int CREDIT_CONTROL    			= 426; 
	public static final int CREDIT_CONTROL_FAILURE_HANDLING	= 427; 
	public static final int CURRENCY_CODE     			= 425; 
	public static final int DIRECT_DEBITING_FAILURE_HANDLING	= 428; 
	public static final int EXPONENT          			= 429; 
	public static final int FINAL_UNIT_ACTION 			= 449; 
	public static final int FINAL_UNIT_INDICATION  			= 430; 
	public static final int GRANTED_SERVICE_UNIT  			= 431; 	    
	public static final int G_S_U_POOL_IDENTIFIER  			= 453;     
	public static final int G_S_U_POOL_REFERENCE   			= 457;     
	public static final int MULTIPLE_SERVICES_CREDIT_CONTROL	= 456; 
	public static final int MULTIPLE_SERVICES_INDICATOR		= 455;  
	public static final int RATING_GROUP      			= 432; 
	public static final int REDIRECT_ADDRESS_TYPE  			= 433;     
	public static final int REDIRECT_SERVER   			= 434; 
	public static final int REDIRECT_SERVER_ADDRESS			= 435;     
	public static final int REQUESTED_ACTION  			= 436; 
	public static final int REQUESTED_SERVICE_UNIT 			= 437;     
	public static final int RESTRICTION_FILTER_RULE			= 438; 
	public static final int SERVICE_CONTEXT_ID   			= 461; 	    
	public static final int SERVICE_IDENTIFIER     			= 439; 	    
	public static final int SERVICE_PARAMETER_INFO 			= 440;     
	public static final int SERVICE_PARAMETER_TYPE			= 441; 
	public static final int SERVICE_PARAMETER_VALUE			= 442; 
	public static final int SUBSCRIPTION_ID   			= 443; 
	public static final int SUBSCRIPTION_ID_DATA   			= 444;     
	public static final int SUBSCRIPTION_ID_TYPE   			= 450;     
	public static final int TARIFF_CHANGE_USAGE   			= 452; 	    
	public static final int TARIFF_TIME_CHANGE     			= 451; 	    
	public static final int UNIT_VALUE        			= 445; 
	public static final int USED_SERVICE_UNIT 			= 446; 
	public static final int USER_EQUIPMENT_INFO    			= 458; 	    
	public static final int USER_EQUIPMENT_INFO_TYPE		= 459;     
	public static final int USER_EQUIPMENT_INFO_VALUE		= 460; 
	public static final int VALUE_DIGITS      			= 447; 
	public static final int VALIDITY_TIME     			= 448; 

	//---------------------------------------------------------------------------
	//Diameter NAS Service AVP constants
	//---------------------------------------------------------------------------
	
	public static final int USER_PASSWORD    			=  2;
	public static final int PASSWORD_RETRY   			= 75;
	public static final int PROMPT           			= 76;
	public static final int CHAP_AUTH        			=402;
	public static final int CHAP_ALGORITHM   			=403;
	public static final int CHAP_IDENT       			=404;
	public static final int CHAP_RESPONSE    			=405;
	public static final int CHAP_CHALLENGE   			= 60;
	public static final int ARAP_PASSWORD    			= 70;
	public static final int ARAP_CHALLENGE_RESPONSE  	= 84;       			   
	public static final int ARAP_SECURITY    			= 73;
	public static final int ARAP_SECURITY_DATA 			= 74;	

	
	public static final int ACCOUNTING_INPUT_OCTETS     = 363;
	public static final int ACCOUNTING_OUTPUT_OCTETS    = 364;
	public static final int ACCOUNTING_INPUT_PACKETS    = 365;
	public static final int ACCOUNTING_OUTPUT_PACKETS   = 366;
	public static final int ACCT_SESSION_TIME			=  46;
	public static final int ACCT_AUTHENTIC   			=  45;
	public static final int ACOUNTING_AUTH_METHOD  		= 406;
	public static final int ACCT_DELAY_TIME  			=  41;
	public static final int ACCT_LINK_COUNT  			=  51;
	public static final int ACCT_TUNNEL_CONNECTION     	=  68;
	public static final int ACCT_TUNNEL_PACKETS_LOST    =  86;

	//-----------------------------------------------------------------------------
	// EAP Service AVP Constants
	//-----------------------------------------------------------------------------
	public static final int EAP_PAYLOAD					= 462 ; 
	public static final int EAP_REISSUED_PAYLOAD		= 463 ;
	public static final int EAP_MASTER_SESSION_KEY		= 464 ;
	public static final int EAP_KEY_NAME				= 102 ;
	public static final int ACCOUNTING_EAP_AUTH_METHOD	= 465 ;
	
	//-----------------------------------------------------------------------------
	//Following are the  Constants for (String)Base Diameter Attribute Name
	//-----------------------------------------------------------------------------
	public static final String ACCT_INTERIM_INTERVAL_STR         	="Acct-Interim-Interval"          ;
	public static final String ACCOUNTING_REALTIME_REQUIRED_STR  	="Accounting-Realtime-Required"   ;
	public static final String ACCT_MULTI_SESSION_ID_STR         	="Acct-Multi-Session-Id"          ;
	public static final String ACCOUNTING_RECORD_NUMBER_STR      	="Accounting-Record-Number"       ;
	public static final String ACCOUNTING_RECORD_TYPE_STR        	="Accounting-Record-Type"         ;
	public static final String ACCOUNTING_SESSION_ID_STR         	="Accounting-Session-Id"          ;
	public static final String ACCOUNTING_SUB_SESSION_ID_STR     	="Accounting-Sub-Session-Id"      ;
	public static final String ACCT_APPLICATION_ID_STR           	="Acct-Application-Id"            ;
	public static final String AUTH_APPLICATION_ID_STR           	="Auth-Application-Id"            ;
	public static final String AUTH_REQUEST_TYPE_STR             	="Auth-Request-Type"              ;
	public static final String AUTHORIZATION_LIFETIME_STR        	="Authorization-Lifetime"         ;
	public static final String AUTH_GRACE_PERIOD_STR             	="Auth-Grace-Period"              ;
	public static final String AUTH_SESSION_STATE_STR            	="Auth-Session-State"             ;
	public static final String RE_AUTH_REQUEST_TYPE_STR          	="Re-Auth-Request-Type"           ;
	public static final String CLASS_STR                         	="Class"                          ;
	public static final String DESTINATION_HOST_STR              	="Destination-Host"               ;
	public static final String DESTINATION_REALM_STR             	="Destination-Realm"              ;
	public static final String DISCONNECT_CAUSE_STR              	="Disconnect-Cause"               ;
	public static final String E2E_SEQUENCE_AVP_STR              	="E2E-Sequence AVP"               ;
	public static final String ERROR_MESSAGE_STR                 	="Error-Message"                  ;
	public static final String ERROR_REPORTING_HOST_STR          	="Error-Reporting-Host"           ;
	public static final String EVENT_TIMESTAMP_STR               	="Event-Timestamp"                ;
	public static final String EXPERIMENTAL_RESULT_STR           	="Experimental-Result"            ;
	public static final String EXPERIMENTAL_RESULT_CODE_STR      	="Experimental-Result-Code"       ;
	public static final String FAILED_AVP_STR                    	="Failed-AVP"                     ;
	public static final String FIRMWARE_REVISION_STR             	="Firmware-Revision"              ;
	public static final String HOST_IP_ADDRESS_STR               	="Host-IP-Address"                ;
	public static final String INBAND_SECURITY_ID_STR            	="Inband-Security-Id"             ;
	public static final String MULTI_ROUND_TIME_OUT_STR          	="Multi-Round-Time-Out"           ;
	public static final String ORIGIN_HOST_STR                   	="Origin-Host"                    ;
	public static final String ORIGIN_REALM_STR                  	="Origin-Realm"                   ;
	public static final String ORIGIN_STATE_ID_STR               	="Origin-State-Id"                ;
	public static final String PRODUCT_NAME_STR                  	="Product-Name"                   ;
	public static final String PROXY_HOST_STR                    	="Proxy-Host"                     ;
	public static final String PROXY_INFO_STR                    	="Proxy-Info"                     ;
	public static final String PROXY_STATE_STR                   	="Proxy-State"                    ;
	public static final String REDIRECT_HOST_STR                 	="Redirect-Host"                  ;
	public static final String REDIRECT_HOST_USAGE_STR           	="Redirect-Host-Usage"            ;
	public static final String REDIRECT_MAX_CACHE_TIME_STR       	="Redirect-Max-Cache-Time"        ;
	public static final String RESULT_CODE_STR                   	="Result-Code"                    ;
	public static final String ROUTE_RECORD_STR                  	="Route-Record"                   ;
	public static final String SESSION_ID_STR                    	="Session-Id"                     ;
	public static final String SESSION_TIMEOUT_STR               	="Session-Timeout"                ;
	public static final String SESSION_BINDING_STR               	="Session-Binding"                ;
	public static final String SESSION_SERVER_FAILOVER_STR       	="Session-Server-Failover"        ;
	public static final String SUPPORTED_VENDOR_ID_STR           	="Supported-Vendor-Id"            ;
	public static final String TERMINATION_CAUSE_STR             	="Termination-Cause"              ;
	public static final String USER_NAME_STR                     	="User-Name"                      ;
	public static final String VENDOR_ID_STR                     	="Vendor-Id"                      ;
	public static final String VENDOR_SPECIFIC_APPLICATION_ID_STR	="Vendor-Specific-Application-Id" ;
	
	public static final String USER_PASSWORD_STR    		="User-Password"; //NOSONAR - Reason: Credentials should not be hard-coded          
	public static final String PASSWORD_RETRY_STR   		="Password-Retry"; //NOSONAR - Reason: Credentials should not be hard-coded       
	public static final String PROMPT_STR           		="Prompt";                 
	public static final String CHAP_AUTH_STR        		="CHAP-Auth";              
	public static final String CHAP_ALGORITHM_STR   		="CHAP-Algorithm";         
	public static final String CHAP_IDENT_STR       		="CHAP-Ident";             
	public static final String CHAP_RESPONSE_STR    		="CHAP-Response";          
	public static final String CHAP_CHALLENGE_STR   		="CHAP-Challenge";         
	public static final String ARAP_PASSWORD_STR    		="ARAP-Password"; //NOSONAR - Reason: Credentials should not be hard-coded         
	public static final String ARAP_CHALLENGE_RESPONSE_STR  ="ARAP-Challenge-Response";
	public static final String ARAP_SECURITY_STR    		="ARAP-Security";          
	public static final String ARAP_SECURITY_DATA_STR 		="ARAP-Security-Data";	   
	
	public static final String ACCOUNTING_INPUT_OCTETS_STR   		= "Accounting-Input-Octets";
	public static final String ACCOUNTING_OUTPUT_OCTETS_STR   		= "Accounting-Output-Octets";
	public static final String ACCOUNTING_INPUT_PACKETS_STR   		= "Accounting-Input-Packets";
	public static final String ACCOUNTING_OUTPUT_PACKETS_STR  		= "Accounting-Output-Packets";
	public static final String ACCT_SESSION_TIME_STR				= "Acct-Session-Time";
	public static final String ACCT_AUTHENTIC_STR   				= "Acct-Authentic";
	public static final String ACOUNTING_AUTH_METHOD_STR  			= "Acounting-Auth-Method";
	public static final String ACCT_DELAY_TIME_STR  				= "Acct-Delay-Time";
	public static final String ACCT_LINK_COUNT_STR  				= "Acct-Link-Count";
	public static final String ACCT_TUNNEL_CONNECTION_STR     		= "Acct-Tunnel-Connection";
	public static final String ACCT_TUNNEL_PACKETS_LOST_STR   		= "Acct-Tunnel-Packets-Lost";

	public static final String CC_CORRELATION_ID_STR		= "CC-Correlation-Id";
	public static final String CC_INPUT_OCTETS_STR   		= "CC-Input-Octets";
	public static final String CC_MONEY_STR          		= "CC-Money";
	public static final String CC_OUTPUT_OCTETS_STR  		= "CC-Output-Octets";
	public static final String CC_REQUEST_NUMBER_STR 		= "CC-Request-Number";
	public static final String CC_REQUEST_TYPE_STR   		= "CC-Request-Type";
	public static final String CC_SERVICE_SPECIFIC_UNITS_STR	= "CC-Service-Specific-Units";
	public static final String CC_SESSION_FAILOVER_STR    		= "CC-Session-Failover";    
	public static final String CC_SUB_SESSION_ID_STR 		= "CC-Sub-Session-Id";
	public static final String CC_TIME_STR           		= "CC-Time";
	public static final String CC_TOTAL_OCTETS_STR   		= "CC-Total-Octets";
	public static final String CC_UNIT_TYPE_STR      		= "CC-Unit-Type";
	public static final String CHECK_BALANCE_RESULT_STR   		= "Check-Balance-Result";   
	public static final String COST_INFORMATION_STR  		= "Cost-Information";
	public static final String COST_UNIT_STR         		= "Cost-Unit";
	public static final String CREDIT_CONTROL_STR    		= "Credit-Control";
	public static final String CREDIT_CONTROL_FAILURE_HANDLING_STR	= "Credit-Control- Failure-Handling";
	public static final String CURRENCY_CODE_STR     		= "Currency-Code";
	public static final String DIRECT_DEBITING_FAILURE_HANDLING_STR	= "Direct-Debiting-Failure-Handling";
	public static final String EXPONENT_STR          		= "Exponent";
	public static final String FINAL_UNIT_ACTION_STR 		= "Final-Unit-Action";
	public static final String FINAL_UNIT_INDICATION_STR		= "Final-Unit-Indication";
	public static final String GRANTED_SERVICE_UNIT_STR  		= "Granted-Service-Unit";    
	public static final String G_S_U_POOL_IDENTIFIER_STR  		= "G-S-U-Pool-Identifier";   
	public static final String G_S_U_POOL_REFERENCE_STR   		= "G-S-U-Pool-Reference";   
	public static final String MULTIPLE_SERVICES_CREDIT_CONTROL_STR	= "Multiple-Services-Credit-Control";
	public static final String MULTIPLE_SERVICES_INDICATOR_STR	= "Multiple-Services-Indicator";
	public static final String RATING_GROUP_STR      		= "Rating-Group";
	public static final String REDIRECT_ADDRESS_TYPE_STR  		= "Redirect-Address-Type";   
	public static final String REDIRECT_SERVER_STR   		= "Redirect-Server";
	public static final String REDIRECT_SERVER_ADDRESS_STR		= "Redirect-Server-Address";
	public static final String REQUESTED_ACTION_STR  		= "Requested-Action";
	public static final String REQUESTED_SERVICE_UNIT_STR		= "Requested-Service-Unit";
	public static final String RESTRICTION_FILTER_RULE_STR		= "Restriction-Filter-Rule";
	public static final String SERVICE_CONTEXT_ID_STR 		= "Service-Context-Id";    
	public static final String SERVICE_IDENTIFIER_STR 		= "Service-Identifier";    
	public static final String SERVICE_PARAMETER_INFO_STR		= "Service-Parameter-Info";
	public static final String SERVICE_PARAMETER_TYPE_STR		= "Service-Parameter-Type";
	public static final String SERVICE_PARAMETER_VALUE_STR		= "Service-Parameter-Value";
	public static final String SUBSCRIPTION_ID_STR   		= "Subscription-Id";
	public static final String SUBSCRIPTION_ID_DATA_STR   		= "Subscription-Id-Data";   
	public static final String SUBSCRIPTION_ID_TYPE_STR   		= "Subscription-Id-Type";   
	public static final String TARIFF_CHANGE_USAGE_STR   		= "Tariff-Change-Usage";    
	public static final String TARIFF_TIME_CHANGE_STR     		= "Tariff-Time-Change";    
	public static final String UNIT_VALUE_STR        		= "Unit-Value";
	public static final String USED_SERVICE_UNIT_STR 		= "Used-Service-Unit";
	public static final String USER_EQUIPMENT_INFO_STR    		= "User-Equipment-Info";    
	public static final String USER_EQUIPMENT_INFO_TYPE_STR		= "User-Equipment-Info-Type";
	public static final String USER_EQUIPMENT_INFO_VALUE_STR	= "User-Equipment-Info-Value";
	public static final String VALUE_DIGITS_STR      		= "Value-Digits";
	
	//-----------------------------------------------------------------------------
	// EAP Service AVP Constants Name
	//-----------------------------------------------------------------------------
	public static final String EAP_PAYLOAD_STR					= "Eap-Payload" ; 
	public static final String EAP_REISSUED_PAYLOAD_STR			= "EAP-Reissued-Payload";
	public static final String EAP_MASTER_SESSION_KEY_STR		= "EAP-Master-Session-Key";
	public static final String EAP_KEY_NAME_STR					= "EAP-Key-Name";
	public static final String ACCOUNTING_EAP_AUTH_METHOD_STR	= "Accounting-EAP-Auth-Method";
}


