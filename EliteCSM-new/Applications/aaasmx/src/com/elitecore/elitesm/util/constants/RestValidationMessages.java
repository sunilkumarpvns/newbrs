package com.elitecore.elitesm.util.constants;

public class RestValidationMessages {
	public static final String NAME_NOT_FOUND_QUERY_PARAMETER = "name parameter not found in url";
	public static final String NAME_INVALID = "Not a valid name. Valid characters : A-Z, a-z, 0-9, ., -, _";
	public static final String NAME_REGEX = "^[a-zA-Z0-9-_.]*$";
	
	public static final String REGEX_MOY = "([1-9]|1[0-2])(([-|,]{1})([1-9]|1[0-2]))*|^$";
	public static final String REGEX_DOM = "([1-9]|1[0-9]|2[0-9]|3[0-1])(([-|,]{1})([1-9]|1[0-9]|2[0-9]|3[0-1]))*|^$";
	public static final String REGEX_DOW = "([1-7])(([-|,]{1})([1-7]))*|^$";
	public static final String REGEX_TIME_PERIOD = "(0?[0-9]|1[0-9]|2[0-3])((:(0?[0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])){0,2})(([-|,]{1})(0?[0-9]|1[0-9]|2[0-3])((:(0?[0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])){0,2}))*|^$";
	
	public static final String MESSAGE_MOY = "Invalid Input, Month of Year must be between 1 to 12. Examples: 1) 3,5,9  2) 1-4,5-8 3) 2,4,6-8";
	public static final String MESSAGE_DOM = "Invalid Input, Day of month must be between 1 to 31. Examples: 1) 3,15,29  2) 1-4,15-18 3) 2,4,16-19";
	public static final String MESSAGE_DOW = "Invalid Input, Day of week must be between 1 to 7. Examples: 1) 3,5,7  2) 1-4,6-7 3) 2,3,5-7";
	public static final String MESSAGE_TIME_PERIOD = "Invalid Input, Timeperiod must be in hh[:mm[:ss]][-hh[:mm[:ss]]] format, limit for hh is 0-23 and for mm and ss is 0-59 ";

	public static final String IPV4_IPV6_REGEX = "((^\\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\\s*$)|(^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$))|(^\\s*((?=.{1,255}$)(?=.*[A-Za-z].*)[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?(?:\\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?)*)\\s*$)";
	
	public static final String IPV4_REGEX = "(^\\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\\s*$)";
	public static final String REGEX_NUMERIC = "^-?\\d\\d*$";
	public static final String REGEX_NUMERIC_POSITIVE = "^?\\d\\d*$";
	public static final String REGEX_VALUE = "^[\\x20-\\x7e]*$";
	public static final String REGEX_SECUIRTY_STANDARD = "RFC 6733|RFC 3588 Dynamic|RFC 3588 TLS|NONE";
	public static final String REGEX_ROLLING_TYPE = "Size-Based|Time-Based|1|2";
	public static final String NONE = "NONE";
	public static final String NONE_WITH_HYPHEN = "-None-";
	public static final String TIME_BASED = "Time-Based";
	public static final String SIZE_BASED = "Size-Based";
	public static final String TIME_BASED_ROLLING_TYPE = "1";
	public static final String SIZE_BASED_ROLLING_TYPE = "2";
	public static final String REGEX_TLS_VERSION = "^$|TLSv1|TLSv1.1|TLSv1.2" ;
	public static final String REGEX_TRUE_FALSE = "true|false|True|False|TRUE|FALSE";
	public static final String REGEX_LOG_LEVEL = "OFF|WARN|ERROR|INFO|FATAL|DEBUG|TRACE|ALL" ; 
	
	public static final String PARAMETER_REGEX = "(^-?\\d+$)";
	public static final String PARAMETER_DIGIT_REGEX =  "(^\\d+$)";
	public static final String PARAMETER_ERR_MESSAGE = "Please enter valid ";
	public static final String INVALID_VALUE = "Invalid Value";
	
	/* EliteCSM Server's Wimax Configuration */
	public static final String REGEX_ACC_CAPABILITIES = "Flow-based accounting|IP-session-based accounting";
	public static final String REGEX_NOFITICATION_CAPABILITIES = "Not Required|Required";
	
	public static final String FILE_NAME_NOT_FOUND_QUERY_PARAMETER = "filename parameter not found in url";
	
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	
	public static final String TRUE_FALSE_WITH_EMPTY = "^$|true|false|True|False|TRUE|FALSE";
	public static final String BOOLEAN_REGEX = "true|false";
	public static final String REGEX_EAP_SIMCONFIG_METHODTYPES = "-NONE-|BASE16|BASE32|BASE64|ELITECRYPT|BASIC_ALPHA_1";
	public static final String REGEX_ENABLE_DISABLE = "ENABLE|DISABLE";
	
	public static final long INVALID_VALUE_LONG = -1;
	
	public static final String ACTIVE = "ACTIVE";
	public static final String INACTIVE = "INACTIVE";
	
	public static final String ROLLING_TYPE_REGEX = "^$|Time-Based|Size-Based";
	public static final String LOGLEVEL_REGEX = "^$|OFF|WARN|ERROR|INFO|FATAL|DEBUG|TRACE|ALL";

	public static final String INVALID = "INVALID";
	public static final String COMMAND_NOT_FOUND_IN_REQUEST_BODY = "Command not found in request body.it must be specified for executing command";
	public static final String REMOVE_EXTRA_LINES = "\\r?\\n";
	
	/* Script Types */
	public static final String DRIVER_SCRIPT = "DRIVER_SCRIPT";
	public static final String TRANSLATION_MAPPING_SCRIPT = "TRANSLATION_MAPPING_SCRIPT";
	public static final String EXTERNAL_RADIUS_SCRIPT = "EXTERNAL_RADIUS_SCRIPT";
	public static final String DIAMETER_ROUTER_SCRIPT = "DIAMETER_ROUTER_SCRIPT";
	
}
