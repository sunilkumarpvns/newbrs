package com.elitecore.netvertexsm.util.constants;

import java.io.File;

public class ConfigConstant {
    
    public static final String COMMON = "COMMON";
    public static final String DATE_FORMAT = "DATE_FORMAT";
    public static final String SHORT_DATE_FORMAT = "SHORT_DATE_FORMAT";  
    public static final String PAGE_WIDTH = "PAGE_WIDTH";
    public static final String RRD_GRAPH_DATA = "RRD_GRAPH_DATA";
    public static final String DATASOURCE = "datasource";
    public static final String ENTITYNAME = "OPEN_DB_AUTH_DRIVER_DS";
    public static final String AUTHENTICATION_SERVICE = "AUTH_SERVICE";
    public static final String PROFILE ="PROFILE";
    public static final String LAST_PROFILE="LAST_PROFILE";
    public static final String MISC_CONFIG_FILE_LOCATION="config"+File.separator+"misc-config.properties";
    public static final String DATABASE_CONFIG_FILE_LOCATION="/WEB-INF/database.properties";
    
    public static final String DAILY = "0";
    public static final String WEEKLY = "1";
    public static final String MONTHLY = "2";
    public static final String YEARLY = "3";
    
    public static final String PAGELEFTSPACE = "7";
    
    public static final long DAILY_START = 86400;
    public static final long WEEKLY_START = 604800;
    public static final long MONTHLY_START = 2600640; 
    public static final long YEARLY_START = 31557600;
    public static final String REFRESH_TIME = "30";
    
    /****************************Start Business Model Alias List*****************************************/
    public static final String RADIUS = "RADIUS";
    public static final String RESOURCE_MANAGER = "RESOURCE_MANAGER";
    public static final String MEDIATION = "MEDIATION";
    public static final String SYSTEM = "SYSTEM";
    public static final String SERVER_MODEL = "SERVER_MODEL";
    /****************************End Business Model Alias List*****************************************/
    
    
    
    /****************************Start Business Module Alias List*****************************************/
    
    //PCRF SERVICE POLICY
    public static final String CREATE_PCRF_POLICY ="CREATE_PCRF_POLICY";
    public static final String SEARCH_PCRF_POLICY ="SEARCH_PCRF_POLICY";
    public static final String UPDATE_PCRF_POLICY ="UPDATE_PCRF_POLICY";
    public static final String DELETE_PCRF_POLICY ="DELETE_PCRF_POLICY";
    public static final String VIEW_PCRF_POLICY ="VIEW_PCRF_POLICY";
    public static final String MANAGE_ORDER_PCRF_POLICY ="MANAGE_ORDER_PCRF_POLICY";
    public static final String CHANGE_PCRF_POLICY_STATUS="CHANGE_PCRF_POLICY_STATUS";
    
    //Gateway
    public static final String CREATE_GATEWAY ="CREATE_GATEWAY";
    public static final String SEARCH_GATEWAY ="SEARCH_GATEWAY";
    public static final String UPDATE_GATEWAY ="UPDATE_GATEWAY";
    public static final String DELETE_GATEWAY ="DELETE_GATEWAY";
    public static final String VIEW_GATEWAY ="VIEW_GATEWAY";
    
    //Gateway Profile
    public static final String CREATE_GATEWAY_PROFILE ="CREATE_GATEWAY_PROFILE";
    public static final String SEARCH_GATEWAY_PROFILE ="SEARCH_GATEWAY_PROFILE";
    public static final String UPDATE_GATEWAY_PROFILE ="UPDATE_GATEWAY_PROFILE";
    public static final String DELETE_GATEWAY_PROFILE ="DELETE_GATEWAY_PROFILE";
    public static final String VIEW_GATEWAY_PROFILE ="VIEW_GATEWAY_PROFILE";
    
    //PACKET MAPPING
    public static final String CREATE_PACKET_MAPPING ="CREATE_PACKET_MAPPING";
    public static final String SEARCH_PACKET_MAPPING ="SEARCH_PACKET_MAPPING";
    public static final String UPDATE_PACKET_MAPPING ="UPDATE_PACKET_MAPPING";
    public static final String DELETE_PACKET_MAPPING ="DELETE_PACKET_MAPPING";
    public static final String VIEW_PACKET_MAPPING ="VIEW_PACKET_MAPPING";
    
    
    //LDAP DATASOURCE
    public static final String CREATE_LDAP_DATASOURCE ="CREATE_LDAP_DATASOURCE";
    public static final String SEARCH_LDAP_DATASOURCE ="SEARCH_LDAP_DATASOURCE";
    public static final String UPDATE_LDAP_DATASOURCE ="UPDATE_LDAP_DATASOURCE";
    public static final String DELETE_LDAP_DATASOURCE ="DELETE_LDAP_DATASOURCE";
    public static final String VIEW_LDAP_DATASOURCE ="VIEW_LDAP_DATASOURCE";
    
    //DATABASE DATASOURCE
    public static final String CREATE_DATABASE_DATASOURCE ="CREATE_DATABASE_DATASOURCE";
    public static final String SEARCH_DATABASE_DATASOURCE ="SEARCH_DATABASE_DATASOURCE";
    public static final String UPDATE_DATABASE_DATASOURCE ="UPDATE_DATABASE_DATASOURCE";
    public static final String DELETE_DATABASE_DATASOURCE ="DELETE_DATABASE_DATASOURCE";
    public static final String VIEW_DATABASE_DATASOURCE ="VIEW_DATABASE_DATASOURCE";
    
    //RADIUS
    public static final String RADIUS_POLICY ="RADIUS_POLICY";
    public static final String ACCESS_POLICY = "ACCESS_POLICY";
    public static final String DICTIONARY_MANAGEMENT = "DICTIONARY_MANAGEMENT";
    public static final String LDAP_DATASOURCE = "LDAP_DATASOURCE";
    public static final String DATABASE_DATASOURCE = "DATABASE_DATASOURCE";
    public static final String CREATE_BWLIST_ACTION = "CREATE_BWLIST_ACTION";
    public static final String UTILITIES = "UTILITIES";
    public static final String BLACKLIST_CANDIDATES="BLACKLIST_CANDIDATES";
    //RESOURCE_MANAGER
    public static final String ACTIVE_SESSION_MANAGEMENT = "ACTIVE_SESSION_MANAGEMENT";
    public static final String CONCURRENT_POLICY = "CONCURRENT_POLICY";
    public static final String IP_POOL = "IP_POOL";

    //MEDIATION
    public static final String PROCESSING_RULES = "PROCESSING_RULES";

    //SYSTEM
    public static final String STAFF ="STAFF";
    public static final String DEVICE ="DEVICE";
    public static final String POLICY ="POLICY";
    public static final String SYSTEM_PARAMETER = "SYSTEM_PARAMETER";

    //SERVER_MODEL
    public static final String SERVER = "SERVER";
    
     /****************************End Business Module Alias List*****************************************/
    
    
    
    /****************************Start Sub-Business Module Alias List*****************************************/
    
    //RADIUS_POLICY
    public static final String CREATE_RADIUS_POLICY = "CREATE_RADIUS_POLICY";
    public static final String SEARCH_RADIUS_POLICY = "SEARCH_RADIUS_POLICY";
    
    //ACCESS_POLICY
    public static final String CREATE_ACCESS_POLICY = "CREATE_ACCESS_POLICY";
    public static final String SEARCH_ACCESS_POLICY = "SEARCH_ACCESS_POLICY";
    
    
    //DICTIONARY_MANAGEMENT
    public static final String LIST_DICTIONARY = "LIST_DICTIONARY";
    
    //LDAP_DATASOURCE
    public static final String LIST_LDAP_DATASOURCE = "LIST_LDAP_DATASOURCE";
    
    //DATABASE_DATASOURCE
    public static final String LIST_DATABASE_DATASOURCE = "LIST_DATABASE_DATASOURCE";

    //ACTIVE_SESSION_MANAGEMENT
    public static final String SEARCH_ACTIVE_SESSION = "SEARCH_ACTIVE_SESSION";
    
    //CONCURRENT_POLICY
    public static final String CREATE_CONCURRENT_LOGIN_POLICY = "CREATE_CONCURRENT_LOGIN_POLICY";
    public static final String SEARCH_CONCURRENT_LOGIN_POLICY = "SEARCH_CONCURRENT_LOGIN_POLICY";
    
    //IP_POOL
    public static final String CREATE_IP_POOL = "CREATE_IP_POOL";
    public static final String SEARCH_IP_POOL = "SEARCH_IP_POOL";
    
    //PROCESSING_RULES
    public static final String LIST_PROCESSING_RULE = "LIST_PROCESSING_RULE";
  
    
    
    //STAFF
    public static final String CREATE_STAFF = "CREATE_STAFF";
    public static final String SEARCH_STAFF = "SEARCH_STAFF";
  
    //SYSTEM_PARAMETER
    public static final String LIST_SYSTEM_PARAMETER = "LIST_SYSTEM_PARAMETER";
    
    //DEVICE
    public static final String NEW_DEVICE = "NEW_DEVICE";
    public static final String SEARCH_DEVICE = "SEARCH_DEVICE";       
    public static final String NEW_PROFILE = "NEW_PROFILE";
    public static final String SEARCH_PROFILE = "SEARCH_PROFILE";
    
    //POLICY
    public static final String CREATE_POLICY = "CREATE_POLICY";
    public static final String SEARCH_POLICY = "SEARCH_POLICY";
    
    //SERVER
    public static final String LIST_SERVERS = "LIST_SERVERS";
    public static final String CREATE_SERVER_INSTANCE = "CREATE_SERVER_INSTANCE";
    
    //BLACKLIST CANDIDATES
    public static final String CREATE_BLACKLIST_CANDIDATES = "CREATE_BLACKLIST_CANDIDATES";
    public static final String SEARCH_BLACKLIST_CANDIDATES = "SEARCH_BLACKLIST_CANDIDATES";
    public static final String UPLOAD_BLACKLIST_CANDIDATES = "UPLOAD_BLACKLIST_CANDIDATES";
    
       
    /****************************End Sub-Business Module Alias List*****************************************/
    
    /****************************Start Sub-Business Module Alias List*****************************************/
    // CREATE_RADIUS_POLICY
    public static final String CREATE_RADIUS_POLICY_ACTION = "CREATE_RADIUS_POLICY";
    
    // SEARCH_RADIUS_POLICY
    //[View Radius Policy Missing.]
    public static final String CHANGE_RADIUS_POLICY_STATUS = "CHANGE_RADIUS_POLICY_STATUS ";
    public static final String SEARCH_RADIUS_POLICY_ACTION = "SEARCH_RADIUS_POLICY";
    public static final String DELETE_RADIUS_POLICY_ACTION = "DELETE_RADIUS_POLICY_ACTION";
    public static final String UPDATE_RADIUS_POLICY_ACTION = "UPDATE_RADIUS_POLICY_ACTION";

    //CREATE_ACCESS_POLICY
    public static final String CREATE_ACCESS_POLICY_ACTION = "CREATE_ACCESS_POLICY_ACTION";
    
    
    //SEARCH_ACCESS_POLICY
    //[View Access Policy Missing.]
    public static final String SEARCH_ACCESS_POLICY_ACTION = "SEARCH_ACCESS_POLICY_ACTION";
    public static final String DELETE_ACCESS_POLICY_ACTION = "DELETE_ACCESS_POLICY_ACTION";
    public static final String CHANGE_ACCESS_POLICY_STATUS_ACTION = "CHANGE_ACCESS_POLICY_STATUS_ACTION";
    public static final String UPDATE_ACCESS_POLICY_ACTION = "UPDATE_ACCESS_POLICY_ACTION";
  
    //LIST_DICTIONARY
    public static final String CREATE_DICTIONARY_ACTION = "CREATE_DICTIONARY_ACTION";
    public static final String LIST_DICTIONARY_ACTION = "LIST_DICTIONARY_ACTION";
    public static final String DELETE_DICTIONARY_ACTION = "DELETE_DICTIONARY_ACTION";
    public static final String UPDATE_DICTIONARY_ACTION = "UPDATE_DICTIONARY_ACTION";
    public static final String VIEW_DICTIONARY_ACTION = "VIEW_DICTIONARY_ACTION";
    public static final String CHANGE_DICTIONARY_STATUS_ACTION = "CHANGE_DICTIONARY_STATUS_ACTION";
  
    //CREATE_LDAP_DATASOURCE
    
    
    //LIST_LDAP_DATASOURCE
    public static final String CHANGE_LDAP_DATASOURCE_STATUS_ACTION     = "CHANGE_LDAP_DATASOURCE_STATUS_ACTION";   
    public static final String LIST_LDAP_DATASOURCE_ACTION              = "LIST_LDAP_DATASOURCE_ACTION";
    public static final String DELETE_LDAP_DATASOURCE_ACTION            = "DELETE_LDAP_DATASOURCE_ACTION";
    public static final String UPDATE_LDAP_DATASOURCE_ACTION            = "UPDATE_LDAP_DATASOURCE_ACTION";
    public static final String LIST_LDAP_DATASOURCE_SCHEMA_ACTION       = "LIST_LDAP_DATASOURCE_SCHEMA_ACTION";
    public static final String UPDATE_LDAP_DATASOURCE_SCHEMA_ACTION     = "UPDATE_LDAP_DATASOURCE_SCHEMA_ACTION";
    public static final String CREATE_LDAP_DATASOURCE_FIELD_ACTION      = "CREATE_LDAP_DATASOURCE_FIELD_ACTION";
    public static final String SEARCH_LDAP_DATASOURCE_FIELD_ACTION      = "SEARCH_LDAP_DATASOURCE_FIELD_ACTION";
    public static final String DELETE_LDAP_DATASOURCE_FIELD_ACTION      = "DELETE_LDAP_DATASOURCE_FIELD_ACTION";
   
    //CREATE_DATABASE_DATASOURCE
    
    
    //LIST_DATABASE_DATASOURCE
    public static final String CREATE_DATABASE_DATASOURCE_ACTION_ACTION = "CREATE_DATABASE_DATASOURCE_ACTION_ACTION";
    public static final String CHANGE_DATABASE_DATASOURCE_STATUS_ACTION = "CHANGE_DATABASE_DATASOURCE_STATUS_ACTION";
    public static final String LIST_DATABASE_DATASOURCE_ACTION          = "LIST_DATABASE_DATASOURCE_ACTION";
    public static final String DELETE_DATABASE_DATASOURCE_ACTION        = "DELETE_DATABASE_DATASOURCE_ACTION";
    public static final String UPDATE_DATABASE_DATASOURCE_ACTION        = "UPDATE_DATABASE_DATASOURCE_ACTION";
    public static final String LIST_DATABASE_DATASOURCE_SCHEMA_ACTION   = "LIST_DATABASE_DATASOURCE_SCHEMA_ACTION";
    public static final String UPDATE_DATABASE_DATASOURCE_SCHEMA_ACTION = "UPDATE_DATABASE_DATASOURCE_SCHEMA_ACTION";
    public static final String CREATE_DATABASE_DATASOURCE_FIELD_ACTION  = "CREATE_DATABASE_DATASOURCE_FIELD_ACTION";
    public static final String SEARCH_DATABASE_DATASOURCE_FIELD_ACTION  = "SEARCH_DATABASE_DATASOURCE_FIELD_ACTION";
    public static final String UPDATE_DATABASE_DATASOURCE_FIELD_ACTION  = "UPDATE_DATABASE_DATASOURCE_FIELD_ACTION";
    public static final String DELETE_DATABASE_DATASOURCE_FIELD_ACTION  = "DELETE_DATABASE_DATASOURCE_FIELD_ACTION"; 
    
    //BLACKLIST CANDIDATES
    public static final String CREATE_BLACKLIST_CANDIDATES_ACTION               = "CREATE_BLACKLIST_CANDIDATES_ACTION";
    public static final String UPLOAD_BLACKLIST_CANDIDATES_ACTION               = "UPLOAD_BLACKLIST_CANDIDATES_ACTION";
    public static final String SEARCH_BLACKLIST_CANDIDATES_ACTION               = "SEARCH_BLACKLIST_CANDIDATES_ACTION";
    public static final String DELETE_BLACKLIST_CANDIDATES_ACTION               = "DELETE_BLACKLIST_CANDIDATES_ACTION";
    public static final String CHANGE_BLACKLIST_CANDIDATES_STATUS_ACTION        = "CHANGE_BLACKLIST_CANDIDATES_STATUS_ACTION";
 
    //UTILITIES
    public static final String VERIFY_PASSWORD_ACTION  = "VERIFY_PASSWORD_ACTION";
       
    //USER_STATISTICS
    public static final String SEARCH_USER_STATISTICS_ACTION = "SEARCH_USER_STATISTICS_ACTION";
    
    //CREATE_CONCURRENT_LOGIN_POLICY   
    public static final String CREATE_CONCURRENT_LOGIN_POLICY_ACTION  = "CREATE_CONCURRENT_LOGIN_POLICY_ACTION";
    
    //SEARCH_CONCURRENT_LOGIN_POLICY
    public static final String CHANGE_CONCURRENT_LOGIN_POLICY_STATUS_ACTION = "CHANGE_CONCURRENT_LOGIN_POLICY_STATUS_ACTION";
    public static final String SEARCH_CONCURRENT_LOGIN_POLICY_ACTION = "SEARCH_CONCURRENT_LOGIN_POLICY_ACTION";
    public static final String DELETE_CONCURRENT_LOGIN_POLICY_ACTION = "DELETE_CONCURRENT_LOGIN_POLICY_ACTION";
    public static final String UPDATE_CONCURRENT_LOGIN_POLICY_ACTION = "UPDATE_CONCURRENT_LOGIN_POLICY_ACTION";
    public static final String UPDATE_CONCURRENT_POLICY_ATTRIBUTE_DETAIL_ACTION = "UPDATE_CONCURRENT_POLICY_ATTRIBUTE_DETAIL_ACTION";
    
    //CREATE_IP_POOL
    public static final String CREATE_IP_POOL_ACTION = "CREATE_IP_POOL_ACTION";
    
    //SEARCH_IP_POOL
    public static final String SEARCH_IP_POOL_ACTION = "SEARCH_IP_POOL_ACTION";
    public static final String CHANGE_IP_POOL_STATUS_ACTION = "CHANGE_IP_POOL_STATUS_ACTION";
    public static final String DELETE_IP_POOL_ACTION = "DELETE_IP_POOL_ACTION";
    
    //CREATE SESSION_MANAGER
    public static final String CREATE_SESSION_MANAGER_ACTION="CREATE_SESSION_MANAGER_ACTION";
    
    //LIST_PROCESSING_RULE
    public static final String LIST_PROCESSING_RULES_ACTION = "LIST_PROCESSING_RULES_ACTION";
    
    //NEW_DEVICE
    public static final String NEW_DEVICE_ACTION = "NEW_DEVICE_ACTION";
    
    //CREATE_STAFF
    public static final String CREATE_STAFF_ACTION = "CREATE_STAFF_ACTION";
    
    //SEARCH_STAFF
    public static final String SEARCH_STAFF_ACTION = "SEARCH_STAFF_ACTION";
    public static final String CHANGE_STAFF_STATUS_ACTION = "CHANGE_STAFF_STATUS_ACTION";
    public static final String DELETE_STAFF_ACTION = "DELETE_STAFF_ACTION";
  
    
    //LIST_SYSTEM_PARAMETER
    public static final String LIST_SYSTEM_PARAMETER_ACTION = "LIST_SYSTEM_PARAMETER_ACTION";
    
    
    //CREATE_SERVER_INSTANCE
    public static final String CREATE_SERVER_INSTANCE_ACTION = "CREATE_SERVER_INSTANCE_ACTION";
          
    //LIST_SERVERS
    public static final String SEARCH_SERVER_INSTANCE_ACTION = "SEARCH_SERVER_INSTANCE_ACTION";
    public static final String DELETE_SERVER_INSTANCE_ACTION = "DELETE_SERVER_INSTANCE_ACTION";
    public static final String CHANGE_SERVER_INSTANCE_ACTION = "CHANGE_SERVER_INSTANCE_ACTION";
    
    public static final String UPDATE_NET_SERVER_INSTANCE_BASIC_DETAIL_ACTION      = "UPDATE_NET_SERVER_INSTANCE_BASIC_DETAIL_ACTION";
    
    public static final String SYNCHRONIZE_NET_DICTIONARY_ACTION                   = "SYNCHRONIZE_NET_DICTIONARY_ACTION";
    
    public static final String VIEW_GRAPH_ACTION                                   = "VIEW_GRAPH_ACTION";                                
    public static final String VIEW_SERVER_ACTION                                  = "VIEW_SERVER_ACTION";                               
    public static final String VIEW_NETWORK_TREE_ACTION                            = "VIEW_NETWORK_TREE_ACTION";
    public static final String UPLOAD_LICENCE_ACTION                               = "UPLOAD_LICENCE_ACTION";                            
    public static final String UPDATE_SERVER_ACTION                                = "UPDATE_SERVER_ACTION";                             
    public static final String SYNCHRONIZE_SERVER_ACTION                           = "SYNCHRONIZE_SERVER_ACTION";                        
    public static final String START_NET_SERVER_ACTION                             = "START_NET_SERVER_ACTION";                          
    public static final String SIGNAL_SOFT_RESTART_ACTION                          = "SIGNAL_SOFT_RESTART_ACTION";                       
    public static final String SIGNAL_SERVICE_STARTUP_ACTION                       = "SIGNAL_SERVICE_STARTUP_ACTION";                    
    public static final String SIGNAL_SERVICE_SHUTDOWN_ACTION                      = "SIGNAL_SERVICE_SHUTDOWN_ACTION";                   
    public static final String SIGNAL_SERVER_SHUTDOWN_ACTION                       = "SIGNAL_SERVER_SHUTDOWN_ACTION";                    
    public static final String SIGNAL_SERVER_RELOAD_CACHE_ACTION                   = "SIGNAL_SERVER_RELOAD_CACHE_ACTION";                
    public static final String SIGNAL_SERVER_RELOAD_CONFIGURATION_ACTION           = "SIGNAL_SERVER_RELOAD_CONFIGURATION_ACTION";        
    public static final String SERVER_ENVIRONMENT_ACTION                           = "SERVER_ENVIRONMENT_ACTION";                        
    public static final String EXPORT_SERVER_ACTION                                = "EXPORT_SERVER_ACTION";                             
    public static final String IMPORT_SERVER_ACTION                                = "IMPORT_SERVER_ACTION";                             
    public static final String EXPORT_SERVICE_ACTION                               = "EXPORT_SERVICE_ACTION";                            
    public static final String IMPORT_SERVICE_ACTION                               = "IMPORT_SERVICE_ACTION";                            
    public static final String EXPORT_DRIVER_ACTION                                = "EXPORT_DRIVER_ACTION";                             
    public static final String IMPORT_DRIVER_ACTION                                = "IMPORT_DRIVER_ACTION";                             
    public static final String SIGNAL_SERVICE_RELOAD_CACHE_ACTION                  = "SIGNAL_SERVICE_RELOAD_CACHE_ACTION";               
    public static final String ADD_ATTRIBUTE_DETAIL_ACTION                         = "ADD_ATTRIBUTE_DETAIL_ACTION";                      
    public static final String SIGNAL_SERVICE_RELOAD_CONFIGURATION_ACTION          = "SIGNAL_SERVICE_RELOAD_CONFIGURATION_ACTION";       
    public static final String LIST_NET_SERVER_INSTANCE_ACTION                     = "LIST_NET_SERVER_INSTANCE_ACTION";                  
    public static final String LIST_NET_SERVER_CONFIGURATION_ACTION                = "LIST_NET_SERVER_CONFIGURATION_ACTION";             
    public static final String NET_SERVER_RESTART_ACTION                           = "NET_SERVER_RESTART_ACTION";                        
    public static final String LIST_USERFILE_DATASOURCE_ACTION                     = "LIST_USERFILE_DATASOURCE_ACTION";                  
    public static final String SYNCHRONIZE_NET_SERVER_CONFIGURATION_ACTION         = "SYNCHRONIZE_NET_SERVER_CONFIGURATION_ACTION";      
    public static final String SYNCHRONIZE_BACK_NET_SERVER_CONFIGURATION_ACTION    = "SYNCHRONIZE_BACK_NET_SERVER_CONFIGURATION_ACTION"; 
    public static final String VIEW_CSV_FILES_ACTION                               = "VIEW_CSV_FILES_ACTION";                            
    public static final String VIEW_SUPPORTED_RFC_ACTION                           = "VIEW_SUPPORTED_RFC_ACTION";                        
    public static final String VIEW_LOG_REPORT_ACTION                              = "VIEW_LOG_REPORT_ACTION";                           
    public static final String VIEW_SERVER_CLIENTS_DETAIL_ACTION                   = "VIEW_SERVER_CLIENTS_DETAIL_ACTION";                
    public static final String VIEW_SERVER_DETAIL_ACTION                           = "VIEW_SERVER_DETAIL_ACTION";                        
    public static final String VIEW_RELOAD_CACHE_ACTION                            = "VIEW_RELOAD_CACHE_ACTION";
    public static final String UPLOAD_LICENSE_ACTION                               = "UPLOAD_LICENSE_ACTION";                            
    public static final String DOWNLOAD_LICENSE_PUBLIC_KEY_ACTION                  = "DOWNLOAD_LICENSE_PUBLIC_KEY_ACTION";               
    public static final String UPDATE_NET_SERVER_ADMIN_INTERFACE_DETAIL_ACTION     = "UPDATE_NET_SERVER_ADMIN_INTERFACE_DETAIL_ACTION";  
    public static final String REMOVE_NET_SERVER_SERVICE_INSTANCE_ACTION           = "REMOVE_NET_SERVER_SERVICE_INSTANCE_ACTION";        
    public static final String ADD_NET_SERVER_SERVICE_INSTANCE_ACTION              = "ADD_NET_SERVER_SERVICE_INSTANCE_ACTION";           
       
    
    public static final String CREATE_USERFILE_DATASOURCE_ACTION                   = "CREATE_USERFILE_DATASOURCE_ACTION";
    public static final String UPDATE_USERFILE_DATASOURCE_ACTION                   = "UPDATE_USERFILE_DATASOURCE_ACTION";
    public static final String DELETE_USERFILE_DATASOURCE_ACTION                   = "DELETE_USERFILE_DATASOURCE_ACTION";                
    public static final String VIEW_CONFIGURATION_ACTION                           = "VIEW_CONFIGURATION_ACTION";
    public static final String UPDATE_CONFIGURATION_ACTION                         = "UPDATE_CONFIGURATION_ACTION";
    public static final String UPDATE_DRIVER_CONFIGURATION_ACTION                  = "UPDATE_DRIVER_CONFIGURATION_ACTION";               
    public static final String UPDATE_SUBDRIVER_CONFIGURATION_ACTION               = "UPDATE_SUBDRIVER_CONFIGURATION_ACTION";
    public static final String UPDATE_SERVER_CONFIGURATION_ACTION                  = "UPDATE_SERVER_CONFIGURATION_ACTION";
    public static final String UPDATE_SERVICE_BASIC_DETAIL_ACTION                  = "UPDATE_SERVICE_BASIC_DETAIL_ACTION";               
    public static final String UPDATE_SERVICE_CONFIGURATION_ACTION                 = "UPDATE_SERVICE_CONFIGURATION_ACTION";     
    public static final String UPDATE_SUBSERVICE_CONFIGURATION_ACTION              = "UPDATE_SUBSERVICE_CONFIGURATION_ACTION";
    public static final String LIST_SERVICE_CONFIGURATION_ACTION                   = "LIST_SERVICE_CONFIGURATION_ACTION";                
    public static final String SYNCHRONIZE_SERVICE_CONFIGURATION_ACTION            = "SYNCHRONIZE_SERVICE_CONFIGURATION_ACTION";         
    public static final String SYNCHRONIZE_BACK_SERVICE_CONFIGURATION_ACTION       = "SYNCHRONIZE_BACK_SERVICE_CONFIGURATION_ACTION";
    public static final String MANAGE_LIVE_SERVER_DICTIONARIES_ACTION              = "MANAGE_LIVE_SERVER_DICTIONARIES_ACTION";         
    public static final String SYNCHRONIZE_SERVER_VERSION_ACTION                   = "SYNCHRONIZE_SERVER_VERSION_ACTION";
    public static final String VIEW_CONFIGURED_SERVICES_ACTION                     = "VIEW_CONFIGURED_SERVICES_ACTION";
    public static final String SEARCH_CDR_FILE_ACTION                              = "SEARCH_CDR_FILE_ACTION";

    
    
    // DATABASE DATASOURCE 
    public static final String CREATE_DATABASE_DS_ACTION                           = "CREATE_DATABASE_DS_ACTION";
    public static final String SEARCH_DATABASE_DS_ACTION                           = "SEARCH_DATABASE_DS_ACTION";
    public static final String CHANGE_DATABASE_DS_STATUS_ACTION                    = "CHANGE_DATABASE_DS_STATUS_ACTION";                
    public static final String UPDATE_DATABASE_DS_ACTION                           = "UPDATE_DATABASE_DS_ACTION";
    public static final String VIEW_DATABASE_DS                                    = "VIEW_DATABASE_DS";
    public static final String DELETE_DATABASE_DS_ACTION						   = "DELETE_DATABASE_DS_ACTION";
    
    
    // SESSION MANAGER
    public static final String SEARCH_SESSION_MANAGER_ACTION                       = "SEARCH_SESSION_MANAGER_ACTION";
    
    // DIGEST CONFIG
    public static final String CREATE_DIGEST_CONFIG_ACTION = "CREATE_DIGEST_CONFIG_ACTION";
    
    
    //AUTHSERVICE POLICY
    public static final String CREATE_AUTH_POLICY_ACTION = "CREATE_AUTH_POLICY_ACTION";
    public static final String SEARCH_AUTH_POLICY_ACTION="SEARCH_AUTH_POLICY_ACTION";
    public static final String VIEW_AUTH_POLICY_ACTION="VIEW_AUTH_POLICY_ACTION";
    
    public static final String UPDATE_AUTH_POLICY_BASIC_DETAIL_ACTION="UPDATE_AUTH_POLICY_BASIC_DETAIL_ACTION";
    public static final String UPDATE_AUTH_POLICY_AUTHENTICATE_PARAMS_ACTION="UPDATE_AUTH_POLICY_AUTHENTICATE_PARAMS_ACTION";
    public static final String UPDATE_AUTH_POLICY_AUTHORIZATION_PARAMS_ACTION="UPDATE_AUTH_POLICY_AUTHORIZATION_PARAMS_ACTION";
    public static final String UPDATE_AUTH_POLICY_DRIVER_ACTION="UPDATE_AUTH_POLICY_DRIVER_ACTION";
    public static final String UPDATE_AUTH_POLICY_ESI_ACTION="UPDATE_AUTH_POLICY_ESI_ACTION";
    public static final String UPDATE_AUTH_POLICY_ADDITIONAL_PROC_ACTION="UPDATE_AUTH_POLICY_ADDITIONAL_PROC_ACTION";
    
    //ACCTSERVICE POLICY
    public static final String CREATE_ACCT_POLICY_ACTION = "CREATE_ACCT_POLICY_ACTION";
    public static final String SEARCH_ACCT_POLICY_ACTION="SEARCH_ACCT_POLICY_ACTION";
    
    /****************************End Sub-Business Module Alias List*****************************************/
    //Unidentified.
    public static final String DEFAULT = "DEFAULT";
    public static final String RADIUS_TEST = "RADIUS_TEST";
    public static final String LIST_RADIUS_PACKET = "LIST_RADIUS_PACKET";
    public static final String CREATE_RADIUS_PACKET = "CREATE_RADIUS_PACKET";
    public static final String VIEW_RADIUS_PACKET = "VIEW_RADIUS_PACKET";
    public static final String DELETE_RADIUS_PACKET = "DELETE_RADIUS_PACKET";
    public static final String SEARCH_CDR_FILE = "SEARCH_CDR_FILE";
    public static final String STAFF_AUDIT = "STAFF_AUDIT";
	
  //Alert
	public static final String ALERT_CONFIGURATION="ALERT_CONFIGURATION";
	public static final String CREATE_ALERT_LISTENER = "CREATE_ALERT_LISTENER";                                                             
	public static final String SEARCH_ALERT_LISTENER = "SEARCH_ALERT_LISTENER";                                                             
	public static final String UPDATE_ALERT_LISTENER = "UPDATE_ALERT_LISTENER";                                                           
	public static final String DELETE_ALERT_LISTENER = "DELETE_ALERT_LISTENER";                                                             
	public static final String VIEW_ALERT_LISTENER = "VIEW_ALERT_LISTENER"; 
	
    
    
    
	public static final String DOWNLOAD_LICENSE_ACTION = "DOWNLOAD_LICENSE_ACTION";
    
    public static final String VIEW_SESSION_MANAGER ="VIEW_SESSION_MANAGER";
    
	public static final String CLOSE_SESSION = "CLOSE_SESSION";
	public static final String CLOSE_ALL_SESSION="CLOSE_ALL_SESSION";
	public static final String DOWNLOAD_ACTIVE_SESSION="DOWNLOAD_ACTIVE_SESSION";
	public static final String PURGE_ALL_SESSION="PURGE_ALL_SESSION";
	public static final String PURGE_CLOSED_SESSION="PURGE_CLOSED_SESSION";
	
	public static final String CREATE_DIAMETER_DICTIONARY = "CREATE_DIAMETER_DICTIONARY";
	public static final String DELETE_DIAMETER_DICTIONARY = "DELETE_DIAMETER_DICTIONARY";
	public static final String LIST_DIAMETER_DICTIONARY = "LIST_DIAMETER_DICTIONARY";
	public static final String CHANGE_DIAMETER_DICTIONARY_STATUS = "CHANGE_DIAMETER_DICTIONARY_STATUS";
	
	
	
	// BI/CEA
	public static final String BICEA = "BICEA";
	public static final String BICEA_TEMPLATE = "BICEA_TEMPLATE";
	public static final String CREATE_BICEA_TEMPLATE="CREATE_BICEA_TEMPLATE";
	public static final String SEARCH_BICEA_TEMPLATE="SEARCH_BICEA_TEMPLATE";
	public static final String UPDATE_BICEA_TEMPLATE="UPDATE_BICEA_TEMPLATE";
	public static final String UPLOAD_BICEA_CSV="UPLOAD_BICEA_CSV";
	public static final String DELETE_BICEA_TEMPLATE="DELETE_BICEA_TEMPLATE";
	public static final String VIEW_BICEA_TEMPLATE="VIEW_BICEA_TEMPLATE";	
	
	//Web sevice Config
	public static final String WEB_SERVICE="WEB_SERVICE";
	public static final String SUBSCRIBER_PROFILE_WEB_SERVICE_CONFIG="SUBSCRIBER_PROFILE_WEB_SERVICE_CONFIG";
	public static final String UPDATE_SUBSCRIBER_PROFILE_WSCONFIG="UPDATE_SUBSCRIBER_PROFILE_WSCONFIG";
	
	//Web Service Method Access 
	public static final String ADD_SUBSCRIBER = "ADD_SUBSCRIBER";
	public static final String UPDATE_SUBSCRIBER = "UPDATE_SUBSCRIBER";
	public static final String DELETE_SUBSCRIBER = "DELETE_SUBSCRIBER";

	//Device Management 
	public static final String DEVICE_MANAGEMENT = "DEVICE_MANAGEMENT";
	public static final String CREATE_DEVICE_MANAGEMENT = "CREATE_DEVICE_MANAGEMENT";
	public static final String SEARCH_DEVICE_MANAGEMENT = "SEARCH_DEVICE_MANAGEMENT";
	public static final String UPDATE_DEVICE_MANAGEMENT = "UPDATE_DEVICE_MANAGEMENT";
	public static final String VIEW_DEVICE_MANAGEMENT = "VIEW_DEVICE_MANAGEMENT";
	public static final String DELETE_DEVICE_MANAGEMENT = "DELETE_DEVICE_MANAGEMENT";
	public static final String UPLOAD_DEVICE_MANAGEMENT = "UPLOAD_DEVICE_MANAGEMENT";
	public static final String EXPORT_DEVICE_MANAGEMENT = "EXPORT_DEVICE_MANAGEMENT";

	
	/****************************************Routing Mangament***********************************/
	// MCCMNC Code Management
	public static final String CREATE_NETWORK = "CREATE_NETWORK";
	public static final String VIEW_NETWORK="VIEW_NETWORK";
	public static final String UPDATE_NETWORK="UPDATE_NETWORK";
	public static final String DELETE_NETWORK="DELETE_NETWORK";
	public static final String SEARCH_NETWORK="SEARCH_NETWORK";
	
	//	MCCMNC Group Management
	public static final String CREATE_MCCMNC_GROUP= "CREATE_MCCMNC_GROUP";
	public static final String VIEW_MCCMNC_GROUP="VIEW_MCCMNC_GROUP";
	public static final String UPDATE_MCCMNC_GROUP="UPDATE_MCCMNC_GROUP";
	public static final String DELETE_MCCMNC_GROUP="DELETE_MCCMNC_GROUP";
	public static final String SEARCH_MCCMNC_GROUP="SEARCH_MCCMNC_GROUP";
	
	// Routing Table Management
	public static final String CREATE_ROUTING_ENTRY= "CREATE_ROUTING_ENTRY";
	public static final String UPDATE_ROUTING_ENTRY="UPDATE_ROUTING_ENTRY";
	public static final String DELETE_ROUTING_ENTRY="DELETE_ROUTING_ENTRY";
	public static final String VIEW_ROUTING_ENTRY="VIEW_ROUTING_ENTRY";
	public static final String SEARCH_ROUTING_ENTRY="SEARCH_ROUTING_ENTRY";
	public static final String MANAGE_ROUTING_ENTRY_ORDER="MANAGE_ROUTING_ENTRY_ORDER";
	
	
	/**********************************Customized Menu Management *****************************/
	public static final String SEARCH_CUSTOMIZED_MENU= "SEARCH_CUSTOMIZED_MENU";
	public static final String CREATE_CUSTOMIZED_MENU= "CREATE_CUSTOMIZED_MENU";
	public static final String UPDATE_CUSTOMIZED_MENU= "UPDATE_CUSTOMIZED_MENU";
	public static final String DELETE_CUSTOMIZED_MENU= "DELETE_CUSTOMIZED_MENU";
	public static final String VIEW_CUSTOMIZED_MENU= "VIEW_CUSTOMIZED_MENU";
	
	/*******************AcessGroupAction******************************/
	public static final String CREATE_ROLE_ACTION= "CREATE_ROLE_ACTION";
	public static final String UPDATE_ROLE_ACTION= "UPDATE_ROLE_ACTION";
	public static final String LIST_ROLE_ACTION="LIST_ROLE_ACTION";
	public static final String DELETE_ROLE_ACTION="DELETE_ROLE_ACTION";
	public static final String VIEW_ROLE_ACTION="VIEW_ROLE_ACTION";
	
	
	public static final String CHANGE_PASSWORD_ACTION="CHANGE_PASSWORD_ACTION";
	
	public static final String UPDATE_STAFF_ACCESS_GROUP_ACTION = "UPDATE_STAFF_ACCESS_GROUP_ACTION";
	public static final String UPDATE_STAFF_BASIC_DETAIL_ACTION = "UPDATE_STAFF_BASIC_DETAIL_ACTION";
	public static final String UPDATE_STAFF_PASSWORD_ACTION="UPDATE_STAFF_PASSWORD_ACTION";
	public static final String UPDATE_STAFF_STATUS_ACTION = "UPDATE_STAFF_STATUS_ACTION";
	public static final String UPDATE_STAFF_USER_NAME_ACTION = "UPDATE_STAFF_USER_NAME_ACTION";
	public static final String VIEW_STAFF = "VIEW_STAFF";
    public static final String UPDATE_STAFF = "UPDATE_STAFF";
	
	public static final String UPDATE_SYSTEM_PARAMETER_ACTION ="UPDATE_SYSTEM_PARAMETER_ACTION";
	public static final String VIEW_SYSTEM_PARAMETERS_ACTION = "VIEW_SYSTEM_PARAMETERS_ACTION";
	
	public static final String CREATE_ESI_RADIUS_ACTION = "CREATE_ESI_RADIUS_ACTION";
	public static final String SEARCH_ESIRADIUS_ACTION = "SEARCH_ESIRADIUS_ACTION";
	
	//Radius Dictionary
	public static final String CREATE_RADIUS_DICTIONARY	= "CREATE_RADIUS_DICTIONARY";
	public static final String LIST_RADIUS_DICTIONARY 	= "LIST_RADIUS_DICTIONARY";
	public static final String DELETE_RADIUS_DICTIONARY = "DELETE_RADIUS_DICTIONARY";
	public static final String UPDATE_RADIUS_DICTIONARY = "UPDATE_RADIUS_DICTIONARY";
	public static final String VIEW_RADIUS_DICTIONARY 	= "VIEW_RADIUS_DICTIONARY";
	public static final String CHANGE_RADIUS_DICTIONARY_STATUS = "CHANGE_RADIUS_DICTIONARY_STATUS";

	//session manager		
	public static final String UPDATE_SESSION_MANAGER = "UPDATE_SESSION_MANAGER";
	
	
	//SP Interface
	public static final String CREATE_SP_INTERFACE	= "CREATE_SP_INTERFACE";
	public static final String DELETE_SP_INTERFACE	= "DELETE_SP_INTERFACE";
	public static final String SEARCH_SP_INTERFACE	= "SEARCH_SP_INTERFACE";
	public static final String UPDATE_SP_INTERFACE	= "UPDATE_SP_INTERFACE";
	public static final String VIEW_SP_INTERFACE	= "VIEW_SP_INTERFACE";
			
			
	public static final String MANAGE_ACTIVE_SESSION_ACTION = "MANAGE_ACTIVE_SESSION_ACTION";
	public static final String SEARCH_ACTIVE_SESSION_ACTION = "SEARCH_ACTIVE_SESSION_ACTION";
	
	public static final String SEARCH_CERTIFICATE 		  = "SEARCH_CERTIFICATE";
	public static final String INIT_SEARCH_CERTIFICATE 	  = "INIT_SEARCH_CERTIFICATE";
	
	//Certificate
	public static final String CERTIFICATE = "CERTIFICATE";
	
	//Server Certificate
	public static final String CREATE_SERVER_CERTIFICATE = "CREATE_SERVER_CERTIFICATE";
	public static final String UPDATE_SERVER_CERTIFICATE = "UPDATE_SERVER_CERTIFICATE";
	public static final String DELETE_SERVER_CERTIFICATE = "DELETE_SERVER_CERTIFICATE";
	public static final String SEARCH_SERVER_CERTIFICATE = "SEARCH_SERVER_CERTIFICATE";
	
	//Trusted Certificate
	public static final String CREATE_TRUSTED_CERTIFICATE = "CREATE_TRUSTED_CERTIFICATE";
	public static final String UPDATE_TRUSTED_CERTIFICATE = "UPDATE_TRUSTED_CERTIFICATE";
	public static final String DELETE_TRUSTED_CERTIFICATE = "DELETE_TRUSTED_CERTIFICATE";
	public static final String SEARCH_TRUSTED_CERTIFICATE = "SEARCH_TRUSTED_CERTIFICATE";

	//CRL Certificate
	public static final String CREATE_CERTIFICATE_REVOCATION_LIST = "CREATE_CERTIFICATE_REVOCATION_LIST";
	public static final String UPDATE_CERTIFICATE_REVOCATION_LIST = "UPDATE_CERTIFICATE_REVOCATION_LIST";
	public static final String DELETE_CERTIFICATE_REVOCATION_LIST = "DELETE_CERTIFICATE_REVOCATION_LIST";
	public static final String SEARCH_CERTIFICATE_REVOCATION_LIST = "SEARCH_CERTIFICATE_REVOCATION_LIST";

	public static final String SEARCH_AREA = "SEARCH_AREA";
	public static final String CREATE_AREA = "CREATE_AREA";
	public static final String UPDATE_AREA = "UPDATE_AREA";
	public static final String DELETE_AREA = "DELETE_AREA";
	public static final String VIEW_AREA = "VIEW_AREA";
	
	public static final String SEARCH_CITY = "SEARCH_CITY";
	public static final String CREATE_CITY = "CREATE_CITY";
	public static final String UPDATE_CITY = "UPDATE_CITY";
	public static final String DELETE_CITY = "DELETE_CITY";
	public static final String VIEW_CITY = "VIEW_CITY";
		
	public static final String SEARCH_REGION = "SEARCH_REGION";
	public static final String CREATE_REGION = "CREATE_REGION";
	public static final String UPDATE_REGION = "UPDATE_REGION";
	public static final String DELETE_REGION = "DELETE_REGION";
	public static final String VIEW_REGION = "VIEW_REGION";
        
	
	public static final String CREATE_DRIVER_ACTION= "CREATE_DRIVER_ACTION";
	public static final String UPDATE_DRIVER_ACTION= "UPDATE_DRIVER_ACTION";
	public static final String DELETE_DRIVER_ACTION= "DELETE_DRIVER_ACTION";
	public static final String SEARCH_DRIVER_ACTION= "SEARCH_DRIVER_ACTION";
	public static final String VIEW_DRIVER_ACTION  = "VIEW_DRIVER_ACTION";
	
	
	
	public static final String NETVERTEX_SERVER_DB = "NetvertexServerDB";
	
	//SEND PACKET TYPE
	public static final String DIAMETER = "DIAMETER";

	
	// DDF
	public static final String UPDATE_DDF = "UPDATE_DDF";
	public static final String VIEW_DDF = "VIEW_DDF";
	
	//GROUP INFORMATION
		public static final String GROUP_INFORMATION_MANAGEMENT = "GROUP_MANAGEMENT";
		public static final String CREATE_GROUP_INFORMATION = "CREATE_GROUP_ACTION";
		public static final String UPDATE_GROUP_INFORMATION = "UPDATE_GROUP_ACTION";
		public static final String LIST_GROUP_INFORMATION = "LIST_GROUP_ACTION";
		public static final String DELETE_GROUP_INFORMATION = "DELETE_GROUP_ACTION";
	
	
		
}
