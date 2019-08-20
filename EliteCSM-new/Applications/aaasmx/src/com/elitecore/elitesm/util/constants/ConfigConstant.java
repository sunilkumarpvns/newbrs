package com.elitecore.elitesm.util.constants;


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
    public static final String MISC_CONFIG_FILE_LOCATION="/WEB-INF/misc-config.properties";
    public static final String DATABASE_CONFIG_FILE_LOCATION="/WEB-INF/database.properties";
    public static final String ESAPI_PROPERTIES="/WEB-INF/ESAPI.properties";
    public static final String VALIDATION_PROPERTIES="/WEB-INF/validation.properties";
    public static final String JSONFILEPATH="/jsp/dashboardwidgets/jsonfiles/";
    public static final String JSONCONFIGFILEPATH="/jsp/dashboardwidgets/categoriesfiles/";
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
    public static final String DIAMETER = "DIAMETER";
    public static final String RM = "RM";
    /****************************End Business Model Alias List*****************************************/
    
    
    
    /****************************Start Business Module Alias List*****************************************/
    
    //RADIUS
    public static final String RADIUS_POLICY ="RADIUS_POLICY";
    public static final String ACCESS_POLICY = "ACCESS_POLICY";
    public static final String DICTIONARY_MANAGEMENT = "DICTIONARY_MANAGEMENT";
    public static final String CREATE_BWLIST_ACTION = "CREATE_BWLIST_ACTION";
    public static final String UTILITIES = "UTILITIES";
    public static final String BLACKLIST_CANDIDATES="BLACKLIST_CANDIDATES";
    public static final String RADIUS_ROUTING_TABLE="RADIUS_ROUTING_TABLE";
    //DIAMETER
    public static final String DIAMETER_DICTIONARY_MANAGEMENT = "DIAMETER_DICTIONARY_MANAGEMENT";
    //RESOURCE_MANAGER
    public static final String ACTIVE_SESSION_MANAGEMENT = "ACTIVE_SESSION_MANAGEMENT";
    public static final String CONCURRENT_POLICY = "CONCURRENT_POLICY";
    public static final String IP_POOL = "IP_POOL";

    //MEDIATION
    public static final String PROCESSING_RULES = "PROCESSING_RULES";

    //SYSTEM
    public static final String STAFF ="STAFF";
    public static final String SYSTEM_PARAMETER = "SYSTEM_PARAMETER";
    public static final String LICENSE = "LICENSE";
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
                                                     
    
    //DIAMETER_DICTIONARY_MANAGEMENT
    public static final String LIST_DIAMETER_DICTIONARY = "LIST_DIAMETER_DICTIONARY";
    public static final String CREATE_DIAMETER_DICTIONARY = "CREATE_DIAMETER_DICTIONARY";
    
    //LDAP_DATASOURCE
    public static final String LIST_LDAP_DATASOURCE = "LIST_LDAP_DATASOURCE";
    
    //DATABASE_DATASOURCE
    public static final String LIST_DATABASE_DATASOURCE = "LIST_DATABASE_DATASOURCE";

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
    public static final String CREATE_RADIUS_POLICY_ACTION = "CREATE_RADIUS_POLICY_ACTION";
    
    // SEARCH_RADIUS_POLICY
    public static final String CHANGE_RADIUS_POLICY_STATUS = "CHANGE_RADIUS_POLICY_STATUS_ACTION";
    public static final String SEARCH_RADIUS_POLICY_ACTION = "SEARCH_RADIUS_POLICY_ACTION";
    public static final String DELETE_RADIUS_POLICY_ACTION = "DELETE_RADIUS_POLICY_ACTION";
    public static final String UPDATE_RADIUS_POLICY_ACTION = "UPDATE_RADIUS_POLICY_ACTION";
    public static final String VIEW_RADIUS_POLICY_ACTION="VIEW_RADIUS_POLICY_ACTION";
    
    //CREATE_ACCESS_POLICY
    public static final String CREATE_ACCESS_POLICY_ACTION = "CREATE_ACCESS_POLICY_ACTION";
    
    
    //SEARCH_ACCESS_POLICY
    public static final String SEARCH_ACCESS_POLICY_ACTION = "SEARCH_ACCESS_POLICY_ACTION";
    public static final String DELETE_ACCESS_POLICY_ACTION = "DELETE_ACCESS_POLICY_ACTION";
    public static final String CHANGE_ACCESS_POLICY_STATUS_ACTION = "CHANGE_ACCESS_POLICY_ACTION";
    public static final String UPDATE_ACCESS_POLICY_ACTION = "UPDATE_ACCESS_POLICY_ACTION";
    public static final String VIEW_ACCESS_POLICY_ACTION="VIEW_ACCESS_POLICY_ACTION";
  
    //LIST_DICTIONARY
    public static final String CREATE_DICTIONARY_ACTION = "CREATE_DICTIONARY_ACTION";
    public static final String LIST_DICTIONARY_ACTION = "LIST_DICTIONARY_ACTION";
    public static final String DELETE_DICTIONARY_ACTION = "DELETE_DICTIONARY_ACTION";
    public static final String UPDATE_DICTIONARY_ACTION = "UPDATE_DICTIONARY_ACTION";
    public static final String VIEW_DICTIONARY_ACTION = "VIEW_DICTIONARY_ACTION";
    public static final String CHANGE_DICTIONARY_STATUS_ACTION = "CHANGE_DICTIONARY_STATUS_ACTION";
    public static final String DOWNLOAD_DICTIONARY="DOWNLOAD_DICTIONARY";
    
    
    ////LIST_DIAMETER_DICTIONARY
    public static final String CREATE_DIAMETER_DICTIONARY_ACTION = "CREATE_DIAMETER_DICTIONARY_ACTION";
    public static final String LIST_DIAMETER_DICTIONARY_ACTION = "LIST_DIAMETER_DICTIONARY_ACTION";
    public static final String DELETE_DIAMETER_DICTIONARY_ACTION = "DELETE_DIAMETER_DICTIONARY_ACTION";
    public static final String UPDATE_DIAMETER_DICTIONARY_ACTION = "UPDATE_DIAMETER_DICTIONARY_ACTION";
    public static final String VIEW_DIAMETER_DICTIONARY_ACTION = "VIEW_DIAMETER_DICTIONARY_ACTION";
    public static final String CHANGE_DIAMETER_DICTIONARY_STATUS_ACTION = "CHANGE_DIAMETER_DICTIONARY_STATUS_ACTION";
    public static final String DOWNLOAD_DIAMETER_DICTIONARY="DOWNLOAD_DIAMETER_DICTIONARY";
    
  
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
    
    
    //BLACKLIST CANDIDATES
	public static final String BLACKLIST_CANDIDATES_ACTION           		    = "BLACKLIST_CANDIDATES_ACTION";
    public static final String CREATE_BLACKLIST_CANDIDATES_ACTION               = "CREATE_BLACKLIST_CANDIDATES_ACTION";
    public static final String UPLOAD_BLACKLIST_CANDIDATES_ACTION               = "UPLOAD_BLACKLIST_CANDIDATES_ACTION";
    public static final String SEARCH_BLACKLIST_CANDIDATES_ACTION               = "SEARCH_BLACKLIST_CANDIDATES_ACTION";
    public static final String DELETE_BLACKLIST_CANDIDATES_ACTION               = "DELETE_BLACKLIST_CANDIDATES_ACTION";
    public static final String CHANGE_BLACKLIST_CANDIDATES_STATUS_ACTION        = "CHANGE_BLACKLIST_CANDIDATES_STATUS_ACTION";
	public static final String UPDATE_BLACKLIST_CANDIDATES_ACTION 				= "UPDATE_BLACKLIST_CANDIDATES_ACTION";

 
    //UTILITIES
    public static final String VERIFY_PASSWORD_ACTION  = "VERIFY_PASSWORD_ACTION";
       
    //USER_STATISTICS
    public static final String SEARCH_USER_STATISTICS_ACTION = "SEARCH_USER_STATISTICS_ACTION";
    
    //CREATE_CONCURRENT_LOGIN_POLICY   
    public static final String CREATE_CONCURRENT_LOGIN_POLICY_ACTION  = "CREATE_CONCURRENT_LOGIN_POLICY_ACTION";
    
    //SEARCH_CONCURRENT_LOGIN_POLICY
    public static final String CHANGE_CONCURRENT_LOGIN_POLICY_STATUS_ACTION = "CHANGE_CONCURRENT_LOGIN_POLICY_STATUS";
    public static final String SEARCH_CONCURRENT_LOGIN_POLICY_ACTION = "SEARCH_CONCURRENT_LOGIN_POLICY_ACTION";
    public static final String DELETE_CONCURRENT_LOGIN_POLICY_ACTION = "DELETE_CONCURRENT_LOGIN_POLICY_ACTION";
    public static final String UPDATE_CONCURRENT_LOGIN_POLICY_ACTION = "UPDATE_CONCURRENT_LOGIN_POLICY_ACTION";
    public static final String UPDATE_CONCURRENT_POLICY_ATTRIBUTE_DETAIL_ACTION = "UPDATE_CONCURRENT_POLICY_ATTRIBUTE_DETAIL_ACTION";
    public static final String VIEW_CONCURRENT_LOGIN_POLICY_ACTION="VIEW_CONCURRENT_LOGIN_POLICY_ACTION";
    
    //CREATE_IP_POOL
    public static final String CREATE_IP_POOL_ACTION = "CREATE_IP_POOL_ACTION";
    
    //SEARCH_IP_POOL
    public static final String SEARCH_IP_POOL_ACTION = "SEARCH_IP_POOL_ACTION";
    public static final String CHANGE_IP_POOL_STATUS_ACTION = "CHANGE_IP_POOL_STATUS_ACTION";
    public static final String DELETE_IP_POOL_ACTION = "DELETE_IP_POOL_ACTION";
    public static final String VIEW_IP_POOL_ACTION="VIEW_IP_POOL";
    public static final String UPDATE_IP_POOL_ACTION="UPDATE_IP_POOL_BASIC_DETAIL_ACTION";
    public static final String SEARCH_IP_ADDRESS="SEARCH_IP_ADDRESS_ACTION";
    public static final String DELETE_IP_ADDRESS="REMOVE_IP_ADDRESS_ACTION";
    
    //LIST_PROCESSING_RULE
    public static final String LIST_PROCESSING_RULES_ACTION = "LIST_PROCESSING_RULES_ACTION";
    
    
    //CREATE_STAFF
    public static final String CREATE_STAFF_ACTION = "CREATE_STAFF_ACTION";
    
    //SEARCH_STAFF
    public static final String SEARCH_STAFF_ACTION = "SEARCH_STAFF_ACTION";
    public static final String CHANGE_STAFF_STATUS_ACTION = "CHANGE_STAFF_STATUS_ACTION";
    public static final String DELETE_STAFF_ACTION = "DELETE_STAFF_ACTION";
    public static final String VIEW_STAFF_ACTION="VIEW_STAFF_ACTION";
    public static final String UPDATE_STAFF_ACTION="UPDATE_STAFF_ACTION";
    public static final String UPDATE_STAFF_ACCESSGROUP="UPDATE_STAFF_ACCESS_GROUP_ACTION";
    public static final String UPDATE_STAFF_PASSWORD="UPDATE_STAFF_PASSWORD_ACTION";
    public static final String UPDATE_STAFF_USERNAME="UPDATE_STAFF_USER_NAME_ACTION";
    
    public static final String ACCESS_GROUP_ACTION = "ACCESS_GROUP_ACTION";
    public static final String UPDATE_ACCESS_GROUP_ACTION="UPDATE_ACCESS_GROUP_ACTION";
    public static final String DELETE_ACCESS_GROUP_ACTION="DELETE_ACCESS_GROUP_ACTION";
    public static final String CREATE_ACCESS_GROUP_ACTION="CREATE_ACCESS_GROUP_ACTION";
    public static final String LIST_ACCESS_GROUP_ACTION="LIST_ACCESS_GROUP_ACTION";
    public static final String VIEW_ACCESS_GROUP_ACTION="VIEW_ACCESS_GROUP_ACTION";
    
    //LIST_SYSTEM_PARAMETER
    public static final String LIST_SYSTEM_PARAMETER_ACTION = "LIST_SYSTEM_PARAMETER_ACTION";
    public static final String UPDATE_SYSTEM_PARAMETER_ACTION="UPDATE_SYSTEM_PARAMETER_ACTION";
    
    
    //CREATE_SERVER_INSTANCE
    public static final String CREATE_SERVER_INSTANCE_ACTION = "CREATE_SERVER_INSTANCE_ACTION";
          
    //LIST_SERVERS
    public static final String SEARCH_SERVER_INSTANCE_ACTION = "SEARCH_SERVER_INSTANCE_ACTION";
    public static final String DELETE_SERVER_INSTANCE_ACTION = "DELETE_SERVER_INSTANCE_ACTION";
    public static final String CHANGE_SERVER_INSTANCE_ACTION = "CHANGE_SERVER_INSTANCE_ACTION";
    
    public static final String UPDATE_NET_SERVER_INSTANCE_BASIC_DETAIL_ACTION      = "UPDATE_NET_SERVER_INSTANCE_BASIC_DETAIL_ACTION";
    
    
    public static final String VIEW_SERVER_GRAPH                                   = "VIEW_SERVER_GRAPH";                                
    public static final String VIEW_SERVER_ACTION                                  = "VIEW_SERVER_ACTION";                               
    public static final String UPLOAD_LICENCE_ACTION                               = "UPLOAD_LICENCE_ACTION";                            
    public static final String UPDATE_SERVER_ACTION                                = "UPDATE_SERVER_ACTION";                             
    public static final String SYNCHRONIZE_SERVER_ACTION                           = "SYNCHRONIZE_SERVER_ACTION";                        
    public static final String START_NET_SERVER_ACTION                             = "START_NET_SERVER_ACTION";                          
    public static final String SIGNAL_SOFT_RESTART_ACTION                          = "SIGNAL_SOFT_RESTART_ACTION";                       
    public static final String SIGNAL_SERVICE_STARTUP_ACTION                       = "SIGNAL_SERVICE_STARTUP_ACTION";                    
    public static final String SIGNAL_SERVICE_SHUTDOWN_ACTION                      = "SIGNAL_SERVICE_SHUTDOWN_ACTION";                   
    public static final String SIGNAL_SERVER_SHUTDOWN_ACTION                       = "SIGNAL_SERVER_SHUTDOWN_ACTION";                    
    public static final String SIGNAL_SERVER_RELOAD_CACHE_ACTION                   = "SIGNAL_SERVER_RELOAD_CACHE_ACTION";                
    public static final String RELOAD_CONFIGURATION_ACTION           			   = "RELOAD_CONFIGURATION_ACTION";  
    public static final String RELOAD_CACHE_ACTION           			           = "RELOAD_CACHE_ACTION";    
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
    public static final String CONFIGURED_CLIENTS                   			   = "CONFIGURED_CLIENTS_ACTION";                
    public static final String VIEW_SERVER_DETAIL_ACTION                           = "VIEW_SERVER_DETAIL_ACTION";                        
    public static final String VIEW_RELOAD_CACHE_ACTION                            = "VIEW_RELOAD_CACHE_ACTION";
    public static final String UPLOAD_LICENSE_ACTION                               = "UPLOAD_LICENSE_ACTION";                            
    public static final String DOWNLOAD_LICENSE_PUBLIC_KEY_ACTION                  = "DOWNLOAD_LICENSE_PUBLIC_KEY_ACTION";               
    public static final String UPDATE_NET_SERVER_ADMIN_INTERFACE_DETAIL_ACTION     = "UPDATE_NET_SERVER_ADMIN_INTERFACE_DETAIL_ACTION";  
    public static final String REMOVE_NET_SERVER_SERVICE_INSTANCE_ACTION           = "REMOVE_NET_SERVER_SERVICE_INSTANCE_ACTION";        
    public static final String ADD_NET_SERVER_SERVICE_INSTANCE_ACTION              = "ADD_NET_SERVER_SERVICE_INSTANCE_ACTION";           
    public static final String CLI_ACTION                                          = "CLI_ACTION";   
    
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
    
    public static final String SYNCHRONIZE_NET_DICTIONARY_ACTION                   = "SYNCHRONIZE_NET_DICTIONARY_ACTION";
    public static final String DOWNLOAD_LOG 									   = "DOWNLOAD_LOG" ;

    
    /****************************End Sub-Business Module Alias List*****************************************/
    //Unidentified.
    public static final String DEFAULT = "DEFAULT";
    public static final String RADIUS_TEST = "RADIUS_TEST";
    public static final String LIST_RADIUS_PACKET = "LIST_RADIUS_PACKET";
    public static final String CREATE_RADIUS_PACKET = "CREATE_RADIUS_PACKET";
    public static final String UPDATE_RADIUS_PACKET = "UPDATE_RADIUS_PACKET";
    public static final String VIEW_RADIUS_PACKET = "VIEW_RADIUS_PACKET";
    public static final String DELETE_RADIUS_PACKET = "DELETE_RADIUS_PACKET";
    public static final String SEARCH_CDR_FILE = "SEARCH_CDR_FILE";
    public static final String STAFF_AUDIT_ACTION = "STAFF_AUDIT";
	
	public static final String DOWNLOAD_LICENSE_ACTION = "DOWNLOAD_LICENSE_ACTION";
    
	//Radius Service Policy
	public static final String AAA_SERVICE_POLICY ="AAA_SERVICE_POLICY";
	public static final String CREATE_RADIUS_SERVICE_POLICY="CREATE_RADIUS_SERVICE_POLICY";
	public static final String SEARCH_RADIUS_SERVICE_POLICY="SEARCH_RADIUS_SERVICE_POLICY";
	public static final String MANAGE_ORDER_RADIUS_SERVICE_POLICY="MANAGE_ORDER_RADIUS_SERVICE_POLICY";
	public static final String UPDATE_RADIUS_SERVICE_POLICY = "UPDATE_RADIUS_SERVICE_POLICY";
	public static final String UPDATE_RADIUS_SERVICE_POLICY_BASIC_DETAILS="UPDATE_RADIUS_SERVICE_POLICY_BASIC_DETAILS";
	public static final String UPDATE_RADIUS_SERVICE_POLICY_AUTH_SERVICE_FLOW="UPDATE_RADIUS_SERVICE_POLICY_AUTH_SERVICE_FLOW";
	public static final String UPDATE_RADIUS_SERVICE_POLICY_ACCT_SERVICE_FLOW="UPDATE_RADIUS_SERVICE_POLICY_ACCT_SERVICE_FLOW";
	public static final String UPDATE_RADIUS_SERVICE_POLICY_STATUS="UPDATE_RADIUS_SERVICE_POLICY_STATUS";
	public static final String DELETE_RADIUS_SERVICE_POLICY="DELETE_RADIUS_SERVICE_POLICY";
	public static final String VIEW_RADIUS_SERVICE_POLICY="VIEW_RADIUS_SERVICE_POLICY";
	public static final String MANAGE_RADIUS_SERVICE_POLICY_ORDER="MANAGE_RADIUS_SERVICE_POLICY_ORDER";

	
	//DynAuth Service Policy
	public static final String CREATE_DYNAUTH_POLICY = "CREATE_DYNAUTH_POLICY";                                                                 
	public static final String SEARCH_DYNAUTH_POLICY = "SEARCH_DYNAUTH_POLICY";                                                                 
	public static final String UPDATE_DYNAUTH_POLICY_BASIC_DETAIL = "UPDATE_DYNAUTH_POLICY_BASIC_DETAIL";    
	public static final String DELETE_DYNAUTH_POLICY = "DELETE_DYNAUTH_POLICY";                                                                 
	public static final String VIEW_DYNAUTH_POLICY = "VIEW_DYNAUTH_POLICY";
	public static final String UPDATE_DYNAUTH_POLICY_STATUS = "UPDATE_DYNAUTH_POLICY_STATUS";   
	public static final String MANAGE_DYNAUTH_POLICY_ORDER = "MANAGE_DYNAUTH_POLICY_ORDER"; 
	
	//Charging Policy
	
	public static final String CREATE_CG_POLICY="CREATE_CG_POLICY";
	public static final String SEARCH_CG_POLICY = "SEARCH_CG_POLICY";         
	public static final String DELETE_CG_POLICY = "DELETE_CG_POLICY"; 
	public static final String VIEW_CG_POLICY = "VIEW_CG_POLICY"; 
	public static final String UPDATE_CG_POLICY_STATUS = "UPDATE_CG_POLICY_STATUS";   
	public static final String MANAGE_CG_POLICY_ORDER = "MANAGE_CG_POLICY_ORDER";
	public static final String UPDATE_CG_POLICY = "UPDATE_CG_POLICY"; 
	
	//Session Manager
	public static final String SESSION_MANAGER="SESSION_MANAGER";
	public static final String CREATE_SESSION_MANAGER = "CREATE_SESSION_MANAGER";                                                         
	public static final String UPDATE_SESSION_MANAGER = "UPDATE_SESSION_MANAGER";                                                         
	public static final String VIEW_SESSION_MANAGER = "VIEW_SESSION_MANAGER";                                                             
	public static final String DELETE_SESSION_MANAGER = "DELETE_SESSION_MANAGER";                                                         
	public static final String SEARCH_SESSION_MANAGER = "SEARCH_SESSION_MANAGER";
	public static final String UPDATE_SESSION_MANAGER_BASIC_DETAILS="UPDATE_SESSION_MANAGER_BASIC_DETAILS";

	public static final String CLOSE_SESSION = "CLOSE_SESSION";
	public static final String CLOSE_ALL_SESSION="CLOSE_ALL_SESSION";
	public static final String DOWNLOAD_ACTIVE_SESSION="DOWNLOAD_ACTIVE_SESSION";
	public static final String PURGE_ALL_SESSION="PURGE_ALL_SESSION";
	public static final String PURGE_CLOSED_SESSION="PURGE_CLOSED_SESSION";
	public static final String SEARCH_ACTIVE_SESSION="SEARCH_ACTIVE_SESSION";
	
	//Driver
	public static final String SERVICE_DRIVERS="SERVICE_DRIVERS";
	public static final String CREATE_DRIVER = "CREATE_DRIVER";                                                             
	public static final String SEARCH_DRIVER = "SEARCH_DRIVER";                                                         
	public static final String UPDATE_DRIVER = "UPDATE_DRIVER";                                                         
	public static final String VIEW_DRIVER = "VIEW_DRIVER";                                                             
	public static final String DELETE_DRIVER = "DELETE_DRIVER";                                                     

	//Digest Configuration
	public static final String DIGEST_CONFIGURATION="DIGEST_CONFIGURATION";
	public static final String CREATE_DIGEST_CONFIGURATION = "CREATE_DIGEST_CONFIGURATION";                                               
	public static final String SEARCH_DIGEST_CONFIGURATION = "SEARCH_DIGEST_CONFIGURATION";                                               
	public static final String UPDATE_DIGEST_CONFIGURATION = "UPDATE_DIGEST_CONFIGURATION";                                               
	public static final String VIEW_DIGEST_CONFIGURATION = "VIEW_DIGEST_CONFIGURATION";                                                   
	public static final String DELETE_DIGEST_CONFIGURATION = "DELETE_DIGEST_CONFIGURATION";                                               
	
	//EAP Configuration
	public static final String EAP_CONFIGURATION="EAP_CONFIGURATION";
	public static final String CREATE_EAP_CONFIGURATION = "CREATE_EAP_CONFIGURATION";                                                     
	public static final String SEARCH_EAP_CONFIGURATION = "SEARCH_EAP_CONFIGURATION";                                                     
	public static final String UPDATE_EAP_CONFIGURATION = "UPDATE_EAP_CONFIGURATION";                                                     
	public static final String VIEW_EAP_CONFIGURATION = "VIEW_EAP_CONFIGURATION";                                                         
	public static final String DELETE_EAP_CONFIGURATION = "DELETE_EAP_CONFIGURATION";    
	
	//Grace Policy
	public static final String GRACE_POLICY="GRACE_POLICY";
	public static final String CREATE_GRACE_POLICY = "CREATE_GRACE_POLICY";                                                               
	public static final String LIST_GRACE_POLICY = "LIST_GRACE_POLICY";                                                                   
	public static final String UPDATE_GRACE_POLICY = "UPDATE_GRACE_POLICY";                                                               
	public static final String DELETE_GRACE_POLICY = "DELETE_GRACE_POLICY"; 
	
	//Alert
	public static final String ALERT_CONFIGURATION="ALERT_CONFIGURATION";
	public static final String CREATE_ALERT_LISTENER = "CREATE_ALERT_LISTENER";                                                             
	public static final String SEARCH_ALERT_LISTENER = "SEARCH_ALERT_LISTENER";                                                             
	public static final String UPDATE_ALERT_LISTENER = "UPDATE_ALERT_LISTENER";                                                           
	public static final String DELETE_ALERT_LISTENER = "DELETE_ALERT_LISTENER";                                                             
	public static final String VIEW_ALERT_LISTENER = "VIEW_ALERT_LISTENER";                                                             
	
	//Trusted Client Profile
	public static final String TRUSTED_CLIENT_PROFILE="TRUSTED_CLIENT_PROFILE";
	public static final String CREATE_CLIENT_PROFILE = "CREATE_CLIENT_PROFILE";                                                           
	public static final String SEARCH_CLIENT_PROFILE = "SEARCH_CLIENT_PROFILE";                                                           
	public static final String UPDATE_CLIENT_PROFILE = "UPDATE_CLIENT_PROFILE";                                                           
	public static final String DELETE_CLIENT_PROFILE = "DELETE_CLIENT_PROFILE";                                                           
	public static final String VIEW_CLIENT_PROFILE = "VIEW_CLIENT_PROFILE";      
	
	//External System
	public static final String EXTERNAL_SYSTEM="EXTERNAL_SYSTEM";
	public static final String EXTENDED_RADIUS="EXTENDED_RADIUS";
	public static final String CREATE_EXTERNAL_SYSTEM = "CREATE_EXTERNAL_SYSTEM";                                                         
	public static final String SEARCH_EXTERNAL_SYSTEM = "SEARCH_EXTERNAL_SYSTEM";                                                         
	public static final String UPDATE_EXTERNAL_SYSTEM = "UPDATE_EXTERNAL_SYSTEM";                                                         
	public static final String DELETE_EXTERNAL_SYSTEM = "DELETE_EXTERNAL_SYSTEM";                                                         
	public static final String VIEW_EXTERNAL_SYSTEM = "VIEW_EXTERNAL_SYSTEM";     
	
	//LDAP
	public static final String LDAP_DATASOURCE="LDAP_DATASOURCE";
	public static final String CREATE_LDAP_DATASOURCE = "CREATE_LDAP_DATASOURCE";                                                         
	public static final String SEARCH_LDAP_DATASOURCE = "SEARCH_LDAP_DATASOURCE";                                                         
	public static final String UPDATE_LDAP_DATASOURCE = "UPDATE_LDAP_DATASOURCE";                                                         
	public static final String DELETE_LDAP_DATASOURCE = "DELETE_LDAP_DATASOURCE";                                                         
	public static final String VIEW_LDAP_DATASOURCE = "VIEW_LDAP_DATASOURCE";
	public static final String SHOW_ALL_DATASOURCE="SHOW_ALL_DATASOURCE";
	
	//Database Datasource
	public static final String DATABASE_DATASOURCE="DATABASE_DATASOURCE";
	public static final String CREATE_DATABASE_DATASOURCE = "CREATE_DATABASE_DATASOURCE";                                                 
	public static final String SEARCH_DATABASE_DATASOURCE = "SEARCH_DATABASE_DATASOURCE";                                                 
	public static final String UPDATE_DATABASE_DATASOURCE = "UPDATE_DATABASE_DATASOURCE";                                                 
	public static final String DELETE_DATABASE_DATASOURCE = "DELETE_DATABASE_DATASOURCE";
	public static final String VIEW_DATABASE_DATASOURCE = "VIEW_DATABASE_DATASOURCE"; 
	
	//Web sevice Config
	public static final String WEB_SERVICE="WEB_SERVICE";
	public static final String SUBSCRIBER_PROFILE_WEB_SERVICE_CONFIG="SUBSCRIBER_PROFILE_WEB_SERVICE_CONFIG";
	public static final String UPDATE_SUBSCRIBER_PROFILE_WSCONFIG="UPDATE_SUBSCRIBER_PROFILE_WSCONFIG";
	public static final String SESSION_MANAGEMENT_WEB_SERVICE_CONFIG="SESSION_MANAGEMENT_WEB_SERVICE_CONFIG";
	public static final String UPDATE_SESSION_MANAGEMENT_WSCONFIG="UPDATE_SESSION_MANAGEMENT_WSCONFIG";
	
	//Diameter NAS Service Policy
	public static final String CREATE_NAS_SERVICE_POLICY        = "CREATE_NAS_SERVICE_POLICY";
	public static final String SEARCH_NAS_SERVICE_POLICY        = "SEARCH_NAS_SERVICE_POLICY";
	public static final String DELETE_NAS_SERVICE_POLICY        = "DELETE_NAS_SERVICE_POLICY";
	public static final String UPDATE_NAS_SERVICE_POLICY_STATUS = "UPDATE_NAS_SERVICE_POLICY_STATUS";
	public static final String UPDATE_NAS_SERVICE_POLICY_AUTHORIZATION_PARAMS= "UPDATE_NAS_SERVICE_POLICY_AUTHORIZATION_PARAMS";
	public static final String UPDATE_NAS_SERVICE_POLICY_ACCOUNTING_PARAMS= "UPDATE_NAS_SERVICE_POLICY_ACCOUNTING_PARAMS";
	public static final String UPDATE_NAS_SERVICE_POLICY_AUTHENTICATE_PARAMS= "UPDATE_NAS_SERVICE_POLICY_AUTHENTICATE_PARAMS";
	public static final String UPDATE_NAS_SERVICE_POLICY        = "UPDATE_NAS_SERVICE_POLICY";
	public static final String VIEW_NAS_SERVICE_POLICY          = "VIEW_NAS_SERVICE_POLICY";
	public static final String MANAGE_NAS_SERVICE_POLICY_ORDER  = "MANAGE_NAS_SERVICE_POLICY_ORDER";
	
	//Translation Mapping Configuration
	public static final String TRANSLATION_MAPPING_CONFIG = "TRANSLATION_MAPPING_CONFIG";
	public static final String CREATE_TRANSLATION_MAPPING_CONFIG = "CREATE_TRANSLATION_MAPPING_CONFIG";
	public static final String SEARCH_TRANSLATION_MAPPING_CONFIG = "SEARCH_TRANSLATION_MAPPING_CONFIG";
	public static final String DELETE_TRANSLATION_MAPPING_CONFIG = "DELETE_TRANSLATION_MAPPING_CONFIG";
	public static final String UPDATE_TRANSLATION_MAPPING_CONFIG = "UPDATE_TRANSLATION_MAPPING_CONFIG";
	public static final String VIEW_TRANSLATION_MAPPING_CONFIG = "VIEW_TRANSLATION_MAPPING_CONFIG";
	
	
	//Diameter Credit Control Service Policy
	public static final String CREATE_CREDIT_CONTROL_SERVICE_POLICY = "CREATE_CREDIT_CONTROL_SERVICE_POLICY";
	public static final String SEARCH_CREDIT_CONTROL_SERVICE_POLICY = "SEARCH_CREDIT_CONTROL_SERVICE_POLICY";
	public static final String DELETE_CREDIT_CONTROL_SERVICE_POLICY = "DELETE_CREDIT_CONTROL_SERVICE_POLICY";
	public static final String UPDATE_CREDIT_CONTROL_SERVICE_POLICY_STATUS = "UPDATE_CREDIT_CONTROL_SERVICE_POLICY_STATUS";
	public static final String UPDATE_CREDIT_CONTROL_SERVICE_POLICY = "UPDATE_CREDIT_CONTROL_SERVICE_POLICY";
	public static final String VIEW_CREDIT_CONTROL_SERVICE_POLICY = "VIEW_CREDIT_CONTROL_SERVICE_POLICY";
	public static final String MANAGE_CREDIT_CONTROL_SERVICE_POLICY_ORDER = "MANAGE_CREDIT_CONTROL_SERVICE_POLICY_ORDER";
	
	
	//Diameter EAP Service Policy
	public static final String CREATE_DIAMETER_EAP_POLICY = "CREATE_DIAMETER_EAP_POLICY";
	public static final String SEARCH_DIAMETER_EAP_POLICY = "SEARCH_DIAMETER_EAP_POLICY";
	public static final String DELETE_DIAMETER_EAP_POLICY = "DELETE_DIAMETER_EAP_POLICY";
	public static final String UPDATE_DIAMETER_EAP_POLICY = "UPDATE_DIAMETER_EAP_POLICY";
	public static final String UPDATE_DIAMETER_EAP_AUTHENTICATION_PARAMETER="UPDATE_DIAMETER_EAP_AUTHENTICATION_PARAMETER";
	public static final String UPDATE_DIAMETER_EAP_AUTHORIZATION_PARAMETER="UPDATE_DIAMETER_EAP_AUTHORIZATION_PARAMETER";
	public static final String UPDATE_DIAMETER_EAP_PROFILE_DRIVER="UPDATE_DIAMETER_EAP_PROFILE_DRIVER";
	public static final String UPDATE_DIAMETER_EAP_POLICY_STATUS = "UPDATE_DIAMETER_EAP_POLICY_STATUS";
	public static final String VIEW_DIAMETER_EAP_POLICY = "VIEW_DIAMETER_EAP_POLICY";
	public static final String MANAGE_DIAMETER_EAP_POLICY_ORDER = "MANAGE_DIAMETER_EAP_POLICY_ORDER";
	
	//Diameter Policy
	public static final String AUTHORIZATION_POLICY = "AUTHORIZATION_POLICY";
	public static final String CREATE_AUTHORIZATION_POLICY = "CREATE_AUTHORIZATION_POLICY";
	public static final String SEARCH_AUTHORIZATION_POLICY = "SEARCH_AUTHORIZATION_POLICY";
	public static final String UPDATE_AUTHORIZATION_POLICY_BASIC_DETAIL = "UPDATE_AUTHORIZATION_POLICY_BASIC_DETAIL";
	public static final String UPDATE_AUTHORIZATION_POLICY_STATUS = "UPDATE_AUTHORIZATION_POLICY_STATUS";
	public static final String DELETE_AUTHORIZATION_POLICY = "DELETE_AUTHORIZATION_POLICY";
	public static final String VIEW_AUTHORIZATION_POLICY = "VIEW_AUTHORIZATION_POLICY";
	
	//Web Service Method Access 
	public static final String FIND_BY_FRAMED_IP_ADDRESS = "FIND_BY_FRAMED_IP_ADDRESS";
	public static final String FIND_BY_USER = "FIND_BY_USER";
	public static final String FIND_BY_SERVICE = "FIND_BY_SERVICE";
	public static final String FIND_BY_USERIDENTITY = "FIND_BY_USERIDENTITY";
	public static final String FIND_BY_ATTRIBUTE = "FIND_BY_ATTRIBUTE";
	public static final String ADD_SUBSCRIBER = "ADD_SUBSCRIBER";
	public static final String UPDATE_SUBSCRIBER = "UPDATE_SUBSCRIBER";
	public static final String DELETE_SUBSCRIBER = "DELETE_SUBSCRIBER";
	
	//Diameter Peer Profile
	public static final String DIAMETER_PEER_PROFILE = "DIAMETER_PEER_PROFILE";
	public static final String CREATE_DIAMETER_PEER_PROFILE = "CREATE_DIAMETER_PEER_PROFILE";
	public static final String SEARCH_DIAMETER_PEER_PROFILE = "SEARCH_DIAMETER_PEER_PROFILE";  
	public static final String UPDATE_DIAMETER_PEER_PROFILE = "UPDATE_DIAMETER_PEER_PROFILE";
	public static final String VIEW_DIAMETER_PEER_PROFILE = "VIEW_DIAMETER_PEER_PROFILE";
	public static final String DELETE_DIAMETER_PEER_PROFILE = "DELETE_DIAMETER_PEER_PROFILE";
	public static final String SHOW_ALL_DIAMETER_PEER_PROFILE="SHOW_ALL_DIAMETER_PEER_PROFILE";
	
	//Diameter Peer   
	public static final String DIAMETER_PEER = "DIAMETER_PEER";
	public static final String CREATE_DIAMETER_PEER = "CREATE_DIAMETER_PEER";
	public static final String SEARCH_DIAMETER_PEER = "SEARCH_DIAMETER_PEER"; 
	public static final String UPDATE_DIAMETER_PEER = "UPDATE_DIAMETER_PEER"; 
	public static final String VIEW_DIAMETER_PEER = "VIEW_DIAMETER_PEER"; 
	public static final String DELETE_DIAMETER_PEER = "DELETE_DIAMETER_PEER";
	public static final String SHOW_ALL_DIAMETER_PEER="SHOW_ALL_DIAMETER_PEER";
	
	//Diameter Routing Table
	public static final String DIAMETER_ROUTING_TABLE = "DIAMETER_ROUTING_TABLE";
	public static final String CREATE_DIAMETER_ROUTING_TABLE = "CREATE_DIAMETER_ROUTING_TABLE";
	public static final String SEARCH_DIAMETER_ROUTING_TABLE = "SEARCH_DIAMETER_ROUTING_TABLE"; 
	public static final String UPDATE_DIAMETER_ROUTING_TABLE = "UPDATE_DIAMETER_ROUTING_TABLE"; 
	public static final String DELETE_DIAMETER_ROUTING_TABLE = "DELETE_DIAMETER_ROUTING_TABLE"; 
	public static final String VIEW_DIAMETER_ROUTING_TABLE = "VIEW_DIAMETER_ROUTING_TABLE";
	public static final String SHOWALL_DIAMETER_ROUTING_TABLE = "SHOWALL_DIAMETER_ROUTING_TABLE";
	public static final String LIST_DIAMETER_ROUTING_ENTRIES="LIST_DIAMETER_ROUTING_ENTRIES";
	public static final String MANAGE_DIAMETER_ROUTING_TABLE_ORDER = "MANAGE_DIAMETER_ROUTING_TABLE_ORDER";  

	//Diameter Session Manager
	public static final String DIAMETER_SESSION_MANAGER="DIAMETER_SESSION_MANAGER";
	public static final String SEARCH_DIAMETER_SESSION_MANAGER="SEARCH_DIAMETER_SESSION_MANAGER";
	public static final String CREATE_DIAMETER_SESSION_MANAGER="CREATE_DIAMETER_SESSION_MANAGER";
	public static final String DELETE_DIAMETER_SESSION_MANAGER="DELETE_DIAMETER_SESSION_MANAGER";
	public static final String UPDATE_DIAMETER_SESSION_MANAGER="UPDATE_DIAMETER_SESSION_MANAGER";
	public static final String VIEW_DIAMETER_SESSION_MANAGER="VIEW_DIAMETER_SESSION_MANAGER";
	
	// Subscribe Profile Management Action 
	public static final String SUBSCRIBE_PROFILE = "SUBSCRIBE_PROFILE";
	public static final String UPDATE_FIELD_NAME = "UPDATE_FIELD_NAME";
	public static final String UPDATE_VALUE_POOL = "UPDATE_VALUE_POOL";
	public static final String SEARCH_SUBSCRIBE_PROFILE = "SEARCH_SUBSCRIBE_PROFILE";
	public static final String ADD_SUBSCRIBE_PROFILE = "ADD_SUBSCRIBE_PROFILE";
	public static final String UPDATE_SUBSCRIBE_PROFILE = "UPDATE_SUBSCRIBE_PROFILE";
	public static final String VIEW_SUBSCRIBE_PROFILE = "VIEW_SUBSCRIBE_PROFILE";
	public static final String DELETE_SUBSCRIBE_PROFILE = "DELETE_SUBSCRIBE_PROFILE";
	
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
	
	// View Exception Error
	public static final String VIEW_ERROR_DETAIL = "VIEW_ERROR_DETAIL_ACTION";
	
	public static final String MANAGE_DASHBOARD_ORDER ="MANAGE_DASHBOARD_ORDER";
	
	public static final String MANAGE_DASHBOARD_ACTION="MANAGE_DASHBOARD_ACTION";
	public static final String CREATE_DASHBOARD_ACTION = "CREATE_DASHBOARD_ACTION";
	public static final String ADMINISTRATIVE_PERMISSION_ACTION="ADMINISTRATIVE_PERMISSION_ACTION";
	public static final String UPDATE_DASHBOARD_ACTION = "UPDATE_DASHBOARD_ACTION";
	
	//Copy Packet Translation Mapping Configuration
	public static final String COPY_PACKET_TRANSLATION_MAPPING_CONFIG = "COPY_PACKET_TRANSLATION_MAPPING_CONFIG";
	public static final String CREATE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG = "CREATE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG";
	public static final String SEARCH_COPY_PACKET_TRANSLATION_MAPPING_CONFIG = "SEARCH_COPY_PACKET_TRANSLATION_MAPPING_CONFIG";
	public static final String DELETE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG = "DELETE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG";
	public static final String UPDATE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG = "UPDATE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG";
	public static final String VIEW_COPY_PACKET_TRANSLATION_MAPPING_CONFIG = "VIEW_COPY_PACKET_TRANSLATION_MAPPING_CONFIG";
	public static final String TRANSLATION_MAPPING = "T";
	public static final String COPY_PACKET_MAPPING = "C";


	//Constant for Session Manager
	public static final String IGNORE = "IGNORE";
	public static final String REJECT = "REJECT";
	public static final String DROP = "DROP";

	//Constant for Session Stop Action
	public static final String DELETE = "DELETE";
	public static final String UPDATE = "UPDATE";

	// Restful Web Service Access
	public static final String ROLE_WEBSERVICE = "ROLE_WEBSERVICE";

	//Priority  Table
	public static final String PRIORITY_TABLE = "PRIORITY_TABLE";
	public static final String UPDATE_PRIORITY_TABLE = "UPDATE_PRIORITY_TABLE";
	public static final String SEARCH_PRIORITY_TABLE = "SEARCH_PRIORITY_TABLE";
	
	// DASHBOARD ESI TYPE CONSTANT
	public static final long AUTH_ESI_TYPE=1;
	public static final long ACCT_ESI_TYPE=2;
	public static final long NAS_ESI_TYPE=7;
	public static final String CUSTOM = "Custom";
	
	//Subscriber Routing Table
	public static final String SUBSCRIBER_ROUTING_TABLE="SUBSCRIBER_ROUTING_TABLE";
	public static final String SEARCH_SUBSCRIBER_ROUTING_TABLE= "SEARCH_SUBSCRIBER_ROUTING_TABLE";
	public static final String CREATE_IMSI_BASED_ROUTING_TABLE = "CREATE_IMSI_BASED_ROUTING_TABLE";
	public static final String SEARCH_IMSI_BASED_ROUTING_TABLE = "SEARCH_IMSI_BASED_ROUTING_TABLE"; 
	public static final String UPDATE_IMSI_BASED_ROUTING_TABLE = "UPDATE_IMSI_BASED_ROUTING_TABLE"; 
	public static final String DELETE_IMSI_BASED_ROUTING_TABLE = "DELETE_IMSI_BASED_ROUTING_TABLE"; 
	public static final String VIEW_IMSI_BASED_ROUTING_TABLE = "VIEW_IMSI_BASED_ROUTING_TABLE";
	public static final String CREATE_MSISDN_BASED_ROUTING_TABLE="CREATE_MSISDN_BASED_ROUTING_TABLE";
	public static final String SEARCH_MSISDN_BASED_ROUTING_TABLE="SEARCH_MSISDN_BASED_ROUTING_TABLE";
	public static final String UPDATE_MSISDN_BASED_ROUTING_TABLE="UPDATE_MSISDN_BASED_ROUTING_TABLE";
	public static final String DELETE_MSISDN_BASED_ROUTING_TABLE="DELETE_MSISDN_BASED_ROUTING_TABLE";
	public static final String VIEW_MSISDN_BASED_ROUTING_TABLE="VIEW_MSISDN_BASED_ROUTING_TABLE";

	//Radius Policy Group
	public static final String RADIUS_POLICY_GROUP="RADIUS_POLICY_GROUP";
	public static final String CREATE_RADIUS_POLICY_GROUP="CREATE_RADIUS_POLICY_GROUP";
	public static final String SEARCH_RADIUS_POLICY_GROUP="SEARCH_RADIUS_POLICY_GROUP";
	public static final String UPDATE_RADIUS_POLICY_GROUP="UPDATE_RADIUS_POLICY_GROUP";
	public static final String DELETE_RADIUS_POLICY_GROUP="DELETE_RADIUS_POLICY_GROUP";
	public static final String VIEW_RADIUS_POLICY_GROUP="VIEW_RADIUS_POLICY_GROUP";
	
	//Diameter Policy Group
	public static final String DIAMETER_POLICY_GROUP="DIAMETER_POLICY_GROUP";
	public static final String CREATE_DIAMETER_POLICY_GROUP="CREATE_DIAMETER_POLICY_GROUP";
	public static final String SEARCH_DIAMETER_POLICY_GROUP="SEARCH_DIAMETER_POLICY_GROUP";
	public static final String UPDATE_DIAMETER_POLICY_GROUP="UPDATE_DIAMETER_POLICY_GROUP";
	public static final String DELETE_DIAMETER_POLICY_GROUP="DELETE_DIAMETER_POLICY_GROUP";
	public static final String VIEW_DIAMETER_POLICY_GROUP="VIEW_DIAMETER_POLICY_GROUP";
	
	//Diameter Concurrency
	public static final String DIAMETER_CONCURRENCY="DIAMETER_CONCURRENCY";
	public static final String CREATE_DIAMETER_CONCURRENCY="CREATE_DIAMETER_CONCURRENCY";
	public static final String SEARCH_DIAMETER_CONCURRENCY="SEARCH_DIAMETER_CONCURRENCY";
	public static final String UPDATE_DIAMETER_CONCURRENCY="UPDATE_DIAMETER_CONCURRENCY";
	public static final String DELETE_DIAMETER_CONCURRENCY="DELETE_DIAMETER_CONCURRENCY";
	public static final String VIEW_DIAMETER_CONCURRENCY="VIEW_DIAMETER_CONCURRENCY";
	
	//Search Session
	public static final String SEARCH_SESSION="SEARCH_SESSION";

	//System parameters
	public static final String LOGIN_BLOCK_INTERVAL="LOGIN_BLOCK_INTERVAL";
	public static final String LOGIN_BLOCK_ATTEMPT="LOGIN_BLOCK_ATTEMPT";
	public static final String CONCURRENT_LOGIN_LIMIT="CONCURRENT_LOGIN_LIMIT";
	public static final String SESSION_IDLE_TIME="SESSION_IDLE_TIME";
	public static final String TGPP_COMMAND_CODE_LIMIT = "TGPP_COMMAND_CODE_LIMIT";
	
	//Plugin
	public static final String PLUGIN        = "PLUGIN";
	public static final String CREATE_PLUGIN = "CREATE_PLUGIN";                                                             
	public static final String SEARCH_PLUGIN = "SEARCH_PLUGIN";                                                         
	public static final String UPDATE_PLUGIN = "UPDATE_PLUGIN";                                                         
	public static final String VIEW_PLUGIN   = "VIEW_PLUGIN";                                                             
	public static final String DELETE_PLUGIN = "DELETE_PLUGIN";  
	public static final String CHANGE_PLUGIN_STATUS = "CHANGE_PLUGIN_STATUS";   
	
	//Script
	public static final String SCRIPT	= 	"SCRIPT";
	public static final String CREATE_SCRIPT = "CREATE_SCRIPT";                                                             
	public static final String SEARCH_SCRIPT = "SEARCH_SCRIPT";                                                         
	public static final String UPDATE_SCRIPT = "UPDATE_SCRIPT";                                                         
	public static final String VIEW_SCRIPT   = "VIEW_SCRIPT";                                                             
	public static final String DELETE_SCRIPT = "DELETE_SCRIPT";  
	public static final String CHANGE_SCRIPT_STATUS = "CHANGE_SCRIPT_STATUS";
	
	//Diameter Peer Group
	public static final String DIAMETER_PEER_GROUP 		  = "DIAMETER_PEER_GROUP";
	public static final String CREATE_DIAMETER_PEER_GROUP = "CREATE_DIAMETER_PEER_GROUP";
	public static final String SEARCH_DIAMETER_PEER_GROUP = "SEARCH_DIAMETER_PEER_GROUP";
	public static final String UPDATE_DIAMETER_PEER_GROUP = "UPDATE_DIAMETER_PEER_GROUP"; 
	public static final String VIEW_DIAMETER_PEER_GROUP   = "VIEW_DIAMETER_PEER_GROUP";
	public static final String DELETE_DIAMETER_PEER_GROUP = "DELETE_DIAMETER_PEER_GROUP";
	
	//TGPP AAA Service Policy
	public static final String MANAGE_TGPP_AAA_SERVICE_POLICY_ORDER = "MANAGE_TGPP_AAA_SERVICE_POLICY_ORDER";	
	public static final String CREATE_TGPP_AAA_SERVICE_POLICY = "CREATE_TGPP_AAA_SERVICE_POLICY";
	public static final String UPDATE_TGPP_AAA_SERVICE_POLICY = "UPDATE_TGPP_AAA_SERVICE_POLICY";
	public static final String VIEW_TGPP_AAA_SERVICE_POLICY = "VIEW_TGPP_AAA_SERVICE_POLICY";
	public static final String SEARCH_TGPP_AAA_SERVICE_POLICY = "SEARCH_TGPP_AAA_SERVICE_POLICY";
	public static final String DELETE_TGPP_AAA_SERVICE_POLICY = "DELETE_TGPP_AAA_SERVICE_POLICY";

	
	public static final String[] restVersions = {"v1","v2"};
	//Radius ESI GROUP
	public static final String RADIUS_ESI_GROUP = "RADIUS_ESI_GROUP";
	public static final String CREATE_RADIUS_ESI_GROUP = "CREATE_RADIUS_ESI_GROUP";
	public static final String UPDATE_RADIUS_ESI_GROUP = "UPDATE_RADIUS_ESI_GROUP";
	public static final String VIEW_RADIUS_ESI_GROUP = "VIEW_RADIUS_ESI_GROUP";
	public static final String SEARCH_RADIUS_ESI_GROUP = "SEARCH_RADIUS_ESI_GROUP";
	public static final String DELETE_RADIUS_ESI_GROUP = "DELETE_RADIUS_ESI_GROUP";
	
	//License
	public static final String VIEW_LICENSE = "VIEW_LICENSE";
	public static final String DOWNLOAD_KEY = "DOWNLOAD_KEY";
	public static final String DOWNLOAD_LICENSE = "DOWNLOAD_LICENSE";
	public static final String UPGRADE_LICENSE = "UPGRADE_LICENSE";
	public static final String DELETE_LICENSE = "DELETE_LICENSE";
	public static final String DEREGISTER_INSTANCE = "DEREGISTER_INSTANCE";
	
	public static final boolean IS_AUDIT_ENABLED = true;
	public static final boolean IS_AUDIT_DISABLED = false;

	//Regex Name pattern for validate any module name
	public static final String NAME_PATTERN = "[a-zA-Z0-9.\\-_]*";
	public static final String LICENCE_LOCATION = "license";
	public static final String LICENCE_FILE_NAME = "local_node.lic";
	public static final String PUB_KEY = ".pubkey";
	public static final Long INVALID_VALUE = -1L;
	
	// ELITECSM DEFAULT SETUP
	public static final String SYSTEM_STARTUP_SETUP = "SYSTEM_STARTUP_SETUP";
	public static final String DATABASE_CONNECTION_FAILED = "databaseConnectionFailed";
	public static final String TABLE_DOES_NOT_EXIST = "tableDoesNotExist";
	public static final String SUCCESS = "success";
	
	public static final String ORACLE = "oracle";
	public static final String POSTGRESQL = "postgresql";
	
	public static final String HYPHEN= "-";
	
	// In-Memory Data Grid
	public static final String INMEMORYDATAGRID	= 	"INMEMORYDATAGRID";
	public static final String CONFIGURE = "CONFIGURE";
	public static final String UPDATE_INMEMORYDATAGRID = "UPDATE_INMEMORYDATAGRID";  
	public static final String VIEW_INMEMORYDATAGRID = "VIEW_INMEMORYDATAGRID";
	
	public static final String BLACKLIST_CANDIDATES_LABEL = "Blacklist Candidates";
	
	//Database Properties
	public static final String DATABASEPROPERTIES = "DATABASEPROPERTIES";
	public static final String UPDATE_DATABASEPROPERTIESFILE = "UPDATE_DATABASEPROPERTIESFILE";

	//Correlated Radius
    public static final String CORRELATED_RADIUS = "CORRELATED_RADIUS";
    public static final String SEARCH_CORRELATED_RADIUS = "SEARCH_CORRELATED_RADIUS";
    public static final String CREATE_CORRELATED_RADIUS = "CREATE_CORRELATED_RADIUS";
    public static final String UPDATE_CORRELATED_RADIUS = "UPDATE_CORRELATED_RADIUS";
    public static final String DELETE_CORRELATED_RADIUS = "DELETE_CORRELATED_RADIUS";
    public static final String VIEW_CORRELATED_RADIUS = "VIEW_CORRELATED_RADIUS";

	
}

