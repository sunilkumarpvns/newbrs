package com.elitecore.netvertex.gateway.file;

public class FileGatewayConstants {

	/** The Constant for regex seperator. */
	public static final String REGEX_FOR_SEPERATOR = "(?:^|R)(\"(?:[^\"]+|\"\")*\"|[^R]*)";

	/** The Constant for replace character. */
	public static final String REPLACE_CHAR = "R";

	/** The Constant for unit name. */
	public static final String UNIT_NAME = "unitname";

	/** The Constant for source unit name. */
	public static final String SOURCE_UNIT_NAME = "sourceunitname";

	/** The Constant for record index. */
	public static final String RECORDINDEX = "recordindex";

	/** The Constant for date. */
	public static final String DATE = "date";

	/** The Constant for attribute list. */
	public static final String ATTRIBUTE_LIST = "ATTRIBUTE_LIST";

	/** The Constant for system date. */
	public static final String SYSDATE = "${SYSDATE}";

	public static final String INPUT_FILE = "INPUT_FILE";

	public static final String PATH_DATA = "PATH_DATA";

	public static final String DEST_DIR_PATH = "DEST_DIR_PATH";

	public static final String IS_COMPRESSED_FILE = "IS_COMPRESSED_FILE";

	public static final String OUTFILE_EXT = "OUTFILE_EXT";

	public static final String ERROR_RECORD_DIR_PATH = "ERROR_RECORD_DIR_PATH";

	public static final String MALFORM_RECORD_DIR_PATH = "MALFORM_RECORD_DIR_PATH";

	public static final String COUNTER_LISTENER = "COUNTER_LISTENER";

	public static final String PLUGGABLE_AGGREGATION = "PLUGGABLE_AGGREGATION";

	public static final String RESULT_MAP = "RESULT_MAP";

	public static final String FILE_DATE = "FILE_DATE";

	public static final String TOTAL_CDR_COUNT = "TOTAL_CDR_COUNT";

	public static final String TOTAL_ERROR_CDR_COUNT = "TOTAL_ERROR_CDR_COUNT";
	
	public static final String ERROR_FILE_SIZE = "ERROR_FILE_SIZE";

	public static final String TOTAL_MALFORM_CDR_COUNT = "TOTAL_MALFORM_CDR_COUNT";
	
	public static final String TOTAL_PACKET_COUNT= "TOTAL_PACKET_COUNT";
	
	public static final String WRITE_FILE_SIZE_DETAILS = "WRITE_FILE_SIZE_DETAILS";

	public static final String FILE_SIZE = "FILE_SIZE";

	public static final String FILE_PATH_NAME = "FILE_PATH_NAME";

	public static final String REPORT_AGGREGATED_DATA = "REPORT_AGGREGATED_DATA";

	public static final String KEY_SEPERATOR = "#";

	public static final String TOTAL_CLONE_CDR_COUNT = "TOTAL_CLONE_CDR_COUNT";

	public static final String CLONE = "CLONE CDR";

	public static final String LIST_OF_NATFLOW_PACKETS = "LIST_OF_NATFLOW_PACKETS";

	public static final String LIST_OF_MALFORM_PACKETS = "LIST_OF_MALFORM_PACKETS";

	/** The Constant TOTAL_SUCCESS_RECORDS. */
	public static final String TOTAL_SUCCESS_RECORDS = "TOTAL_SUCCESS_RECORDS";
	
	/** The Constant FOOTER_SUMMARY. */
	public static final String FOOTER_SUMMARY = "Trailer ID: CRESTELMEDTRL Start-Range = 1 End-Range = ";
	
	public static final String DURATION_IN_MILLIS  = "DURATION_IN_MILLIS";

	public static final String RECORD_TIME = "RECORD_TIME";

	public static final String TOTAL_RECIEVED_PACKETS = "TOTAL_RECIEVED_PACKETS";

	public static final String TOTAL_BUFFERED_PACKETS = "TOTAL_BUFFERED_PACKETS";

	public static final String TOTAL_RECORDS_RECIEVED = "TOTAL_RECORDS_RECIEVED";

	public static final String TOTAL_PACKETS_DROPPED = "TOTAL_PACKETS_DROPPED";

	public static final String TOTAL_PROCCESSED_PACKETS = "TOTAL_PROCCESSED_PACKETS";

	public static final String AVG_TPS = "AVG_TPS";

	public static final String CLIENT_INFO = "CLIENT_INFO";

	public static final String CLIENT_IP = "CLIENT_IP";

	public static final String TOTAL_TEMPLATE_REQUEST = "TOTAL_TEMPLATE_REQUEST";

	public static final String TOTAL_OPTION_TEMPLATE_REQUEST = "TOTAL_OPTION_TEMPLATE_REQUEST";

	public static final String TOTAL_FLOW_REQUEST = "TOTAL_FLOW_REQUEST";

	public static final String TOTAL_MALFORM_REQUEST = "TOTAL_MALFORM_REQUEST";

	public static final String TOTAL_INVALID_REQUEST = "TOTAL_INVALID_REQUEST";

	public static final String TOTAL_BUFFERED_PENDING_TO_WRITE_PACKETS = "TOTAL_BUFFERED_PENDING_TO_WRITE_PACKETS";

	public static final String TOTAL_AUTHENTICATION_REQUESTS = "TOTAL_AUTHENTICATION_REQUESTS";

	public static final String TOTAL_ACCOUNTING_START_REQUESTS = "TOTAL_ACCOUNTING_START_REQUESTS";

	public static final String TOTAL_ACCOUNTING_STOP_REQUESTS = "TOTAL_ACCOUNTING_STOP_REQUESTS";

	public static final String TOTAL_ACCOUNTING_UPDATE_REQUESTS = "TOTAL_ACCOUNTING_UPDATE_REQUESTS";

	public static final String TOTAL_ACCOUNTING_ON_REQUESTS = "TOTAL_ACCOUNTING_ON_REQUESTS";

	public static final String TOTAL_ACCOUNTING_OFF_REQUESTS = "TOTAL_ACCOUNTING_ON_REQUESTS";

	public static final String TOTAL_ACCOUNTING_OTHER_REQUESTS = "TOTAL_ACCOUNTING_OTHER_REQUESTS";

	public static final String TOTAL_AUTHENTICATION_RESPONSES = "TOTAL_AUTHENTICATION_RESPONSES";

	public static final String TOTAL_ACCOUNTING_START_RESPONSES = "TOTAL_ACCOUNTING_START_RESPONSES";

	public static final String TOTAL_ACCOUNTING_STOP_RESPONSES = "TOTAL_ACCOUNTING_STOP_RESPONSES";

	public static final String TOTAL_ACCOUNTING_UPDATE_RESPONSES = "TOTAL_ACCOUNTING_UPDATE_RESPONSES";

	public static final String TOTAL_ACCOUNTING_ON_RESPONSES = "TOTAL_ACCOUNTING_ON_RESPONSES";

	public static final String TOTAL_ACCOUNTING_OFF_RESPONSES = "TOTAL_ACCOUNTING_ON_RESPONSES";

	public static final String TOTAL_ACCOUNTING_OTHER_RESPONSES = "TOTAL_ACCOUNTING_OTHER_RESPONSES";

	public static final String TOTAL_RESPONSES = "TOTAL_RESPONSES";

	public static final String TOTAL_REQUEST = "TOTAL_REQUEST";

	public static final String TOTAL_ECHO_REQUEST = "TOTAL_ECHO_REQUEST";

	public static final String TOTAL_NODE_ALIVE_REQUEST = "TOTAL_NODE_ALIVE_REQUEST";

	public static final String TOTAL_REDIRECTION_REQUEST = "TOTAL_REDIRECTION_REQUEST";

	public static final String TOTAL_DATA_RECORD_TRANSFER_REQUEST = "TOTAL_DATA_RECORD_TRANSFER_REQUEST";

	public static final String TOTAL_DATA_RECORD_TRANSFER_RECORDS = "TOTAL_DATA_RECORD_TRANSFER_RECORDS";

	public static final String TOTAL_REDIRECTION_RESPONSE_SUCCESS = "TOTAL_REDIRECTION_RESPONSE_SUCCESS";

	public static final String TOTAL_REDIRECTION_RESPONSE_FAILURE = "TOTAL_REDIRECTION_RESPONSE_FAILURE";

	public static final String TOTAL_ECHO_RESPONSE = "TOTAL_ECHO_RESPONSE";

	public static final String TOTAL_NODE_ALIVE_RESPONSE = "TOTAL_NODE_ALIVE_RESPONSE";

	public static final String TOTAL_DATA_RECORD_TRANSFER_RESPONSESUCCESS = "TOTAL_DATA_RECORD_TRANSFER_RESPONSESUCCESS";

	public static final String TOTAL_DATA_RECORD_TRANSFER_RESPONSEFAILURE = "TOTAL_DATA_RECORD_TRANSFER_RESPONSEFAILURE";

	public static final String TOTAL_VERSION_NOT_SUPPORTED_RESPONSE = "TOTAL_VERSION_NOT_SUPPORTED_RESPONSE";

	public static final String TOTAL_INVALID_CLIENT_REQUEST = "TOTAL_INVALID_CLIENT_REQUEST";

	public static final String TOTAL_DROPPED_ECHO_REQUEST = "TOTAL_DROPPED_ECHO_REQUEST";

	public static final String TOTAL_DROPPED_NODE_ALIVE_REQUEST = "TOTAL_DROPPED_NODE_ALIVE_REQUEST";

	public static final String TOTAL_DROPPED_REDIRECTION_REQUEST = "TOTAL_DROPPED_REDIRECTION_REQUEST";

	public static final String TOTAL_DROPPED_DATA_TRANSFER_REQUEST = "TOTAL_DROPPED_DATA_TRANSFER_REQUEST";

	public static final String TOTAL_DROPPED_REQUEST = "TOTAL_DROPPED_REQUEST";

	public static final String TOTAL_MALFORMED_REQUEST_PACKET = "TOTAL_MALFORMED_REQUEST_PACKET";

	public static final String SERVICE_UP_TIME = "SERVICE_UP_TIME";

	public static final String AVERAGE_DATA_RECORD_TPS = "AVERAGE_DATA_RECORD_TPS";

	public static final String TOTAL_ECHO_REQUEST_SENT = "TOTAL_ECHO_REQUEST_SENT";

	public static final String TOTAL_ECHO_REQUEST_RETRY = "TOTAL_ECHO_REQUEST_RETRY";

	public static final String TOTAL_NODE_ALIVE_REQUEST_SENT = "TOTAL_NODE_ALIVE_REQUEST_SENT";

	public static final String TOTAL_NODE_ALIVE_REQUEST_RETRY = "TOTAL_NODE_ALIVE_REQUEST_RETRY";

	public static final String TOTAL_ECHO_RESPONSE_RECEIVED = "TOTAL_ECHO_RESPONSE_RECEIVED";

	public static final String TOTAL_MALFORMED_ECHO_RESPONSE_RECEIVED = "TOTAL_MALFORMED_ECHO_RESPONSE_RECEIVED";

	public static final String TOTAL_NODE_ALIVE_RESPONSE_RECEIVED = "TOTAL_NODE_ALIVE_RESPONSE_RECEIVED";

	public static final String TOTAL_MALFORMED_NODE_ALIVE_RESPONSE_RECEIVED = "TOTAL_MALFORMED_NODE_ALIVE_RESPONSE_RECEIVED";

	public static final String TOTAL_FILES = "TOTAL_FILES";

	public static final String TOTAL_MALFORM_RECORDS  = "TOTAL_MALFORM_RECORDS";

	public static final String TOTAL_PARTIALLY_SUCCESS_FILES = "TOTAL_PARTIALLY_SUCCESS_FILES";

	public static final String TOTAL_SUCCESS_FILES = "TOTAL_SUCCESS_FILES";

	public static final String TOTAL_FAILED_FILES = "TOTAL_FAILED_FILES";

	public static final String TOTAL_PENDING_FILES = "TOTAL_PENDING_FILES";

	public static final String TOTAL_INPROCESS_FILES = "TOTAL_INPROCESS_FILES";

	public static final String TOTAL_PACKETS = "TOTAL_PACKETS";

	public static final String TOTAL_FAILED_RECORDS = "TOTAL_FAILED_RECORDS";

	public static final String TOTAL_INVALID_RECORDS = "TOTAL_INVALID_RECORDS";

	public static final String TOTAL_CLONE_RECORDS = "TOTAL_CLONE_RECORDS";

	public static final String TOTAL_DUPLICATE_RECORDS = "TOTAL_DUPLICATE_RECORDS";

	public static final String TOTAL_FILTER_RECORDS = "TOTAL_FILTER_RECORDS";

	public static final String DRIVER_INFO = "DRIVER_INFO";

	public static final String PLUGIN_INFO = "PLUGIN_INFO";

	public static final String FILE_MERGE_STATUS  = "FILE_MERGE_STATUS";

	public static final String CURRENT_TPS = "CURRENT_TPS";
	
	public static final String TOTAL_RECORDS_RECIEVED_LICENSE = "TOTAL_RECORDS_RECIEVED_LICENSE";
	
	public static final String DURATION_IN_MILLIS_LICENSE = "DURATION_IN_MILLIS_LICENSE";

	public static final String FILE_NAME_WITHOUT_EXT = "FILE_NAME_WITHOUT_EXT";

}
