package com.elitecore.netvertex.core.constant;

/**
 * The Class MediationServiceConstants.This class contains all the constants related to Mediation Services.
 * @author Elitecore.
 */
public class OfflineConstants {

	/** The Constant for default file batch size. */
	public static final int DEFAULT_FILE_BATCH_SIZE = 100;

	/** The Constant for default time interval. */
	public static final int DEFAULT_TIME_INTERVAL = 1000 * 60;

	/** The Constant for default record batch size. */
	public static final int DEFAULT_RECORD_BATCH_SIZE = 5000;

	/** The Constant for default minimum thread size. */
	public static final int DEFAULT_MIN_THREAD_SIZE = 1;

	/** The Constant default maximum thread size. */
	public static final int DEFAULT_MAX_THREAD_SIZE = 1;

	/** The Constant for archived. */
	public static final String ARCHIVED = "archived";

	/** The Constant for error. */
	public static final String ERROR = "error";

	/**Constant for Malform */
	public static final String MALFORM = "malform";

	/** The Constant for input. */
	public static final String INPUT = "input";

	/** The Constant for collected. */
	public static final String COLLECTED = "collected";

	/** The Constant for filter. */
	public static final String FILTER = "filtered";

	/** The Constant for clone. */
	public static final String CLONE = "cloned";

	/** The Constant for invalid. */
	public static final String INVALID = "invalid";

	/** The Constant for duplicate. */
	public static final String DUPLICATE = "duplicate";

	/** The Constant for collection root. */
	public static final String COLLECTION_ROOT = "COLLECTION_ROOT";

	/** The Constant for processing root. */
	public static final String PROCESSING_ROOT = "PROCESSING_ROOT";

	/** The Constant for distribution root. */
	public static final String DISTRIBUTION_ROOT = "DISTRIBUTION_ROOT";

	/** The Constant for system log input directory root. */
	public static final String SYSLOG_INPUT_DIR_ROOT = "SYSLOG_INPUT_DIR_ROOT";

	/** The Constant for system log output directory root. */
	public static final String SYSLOG_OUTPUT_DIR_ROOT = "SYSLOG_OUTPUT_DIR_ROOT";

	/** The Constant for default queue size. */
	public static final int DEFAULT_QUEUE_SIZE = 1000;

	/** The Constant for error message. */
	public static final String ERROR_MESSAGE = "ERROR";

	/** The Constant for success message. */
	public static final String SUCCESS_MESSAGE = "SUCCESS";

	/** The Constant for partially success message. */
	public static final String PARTIALLY_SUCCESS_MESSAGE = "PARTIALLY_SUCCESS";

	/** The Constant for clone message. */
	public static final String CLONE_MESSAGE = "CLONE";

	/** The Constant for filter message. */
	public static final String FILTER_MESSAGE = "FILTER";

	/** The Constant for invalid message. */
	public static final String INVALID_MESSAGE = "INVALID";

	/** The Constant for duplicate message. */
	public static final String DUPLICATE_MESSAGE = "DUPLICATE";

	/** The Constant for failed message. */
	public static final String FAILED_MESSAGE = "FAILED";

	/** The Constant for collected message. */
	public static final String COLLECTED_MESSAGE = "COLLECTED";

	/** The Constant for main service. */
	public static final String MAIN_SERVICE = "MAIN";

	/** The Constant for processing error parameter. */
	public static final String PARSING_ERROR_PARAM = "PAR_ERR";
	
	/** The Constant for processing filter parameter. */
	public static final String PROCESSING_FILTER_PARAM = "PRP_FIL";

	/** The Constant for processing error parameter. */
	public static final String PROCESSING_ERROR_PARAM = "PRP_ERR";

	/** The Constant for processing duplicate parameter. */
	public static final String PROCESSING_DUPLICATE_PARAM = "PRP_DUP";

	/** The Constant for processing invalid parameter. */
	public static final String PROCESSING_INVALID_PARAM = "PRP_INV";

	/** The Constant for distribution error parameter. */
	public static final String DISTRIBUTION_ERROR_PARAM = "DRP_ERR";

	/** The Constant PROCESSING_REPROCESSING_PARAM. */
	public static final String PROCESSING_REPROCESSING_PARAM = "PRP";

	/** The Constant for invalid directory path. */
	public static final String INVALID_DIR_PATH = "INVALID_DIR_PATH";

	/** The Constant for error record directory path. */
	public static final String ERROR_RECORD_DIR_PATH = "ERROR_RECORD_DIR_PATH";

	/** The Constant for error file directory path. */
	public static final String ERROR_FILE_DIR_PATH = "ERROR_FILE_DIR_PATH";

	/** The Constant for duplicate directory path. */
	public static final String DUPLICATE_DIR_PATH = "DUPLICATE_DIR_PATH";

	/** The Constant for valid directory path. */
	public static final String VALID_DIR_PATH = "VALID_DIR_PATH";

	/** The Constant for filter directory path. */
	public static final String FILTER_DIR_PATH = "FILTER_DIR_PATH";

	public static final String ARCHIVE_DIR_PATH = "ARCHIVE_DIR_PATH";

	/** The Constant for default rule id. */
	public static final String DEFAULT_RULE_ID = "DEFAULT_RULE_ID";

	/** The Constant for default rule group id. */
	public static final String DEFAULT_RULE_GROUP_ID = "DEFAULT_RULE_GROUP_ID";

	/** The Constant for prepared statement file statistics. */
	public static final String PREPAREDSTATEMENT_FILESTAT = "PREPAREDSTATEMENT_FILESTAT";

	/** The Constant for prepared statement file statistics detail. */
	public static final String PREPAREDSTATEMENT_FILESTATDETAIL = "PREPAREDSTATEMENT_FILESTATDETAIL";

	/** The Constant for prepared statement raw input cdr. */
	public static final String PREPAREDSTATEMENT_RAWINPUTCDR = "PREPAREDSTATEMENT_RAWINPUTCDR";

	/** The Constant for date format without time. */
	public static final String DATEFORMAT_WITHOUTTIME = "dd/MM/yyyy";

	/** The Constant for date format with hour_only. */
	public static final String DATEFORMAT_WITH_HOUR_ONLY = "dd/MM/yyyy HH:00:00";

	/** The Constant for date format with hour minute only. */
	public static final String DATEFORMAT_WITH_HOUR_MINUTE_ONLY = "dd/MM/yyyy HH:mm:00";

	/** The Constant for cdr date summary. */
	public static final String CDR_DATE_SUMMARY = "CDR_DATE_SUMMARY";

	/** The Constant for duplicate check key root. */
	public static final String DUPLICATE_CHECK_KEY_ROOT = "DUPLICATE_CHECK_KEY_ROOT";

	/** The Constant for duplicate check key. */
	public static final String DUPLICATE_CHECK_KEY = "DUPLICATE_CHECK_KEY";

	/** The Constant for duplicate check type across file. */
	public static final String DUPLICATE_CHECK_TYPE_ACROSS_FILE = "ACROSS-FILE";

	/** The Constant for file name list from database. */
	public static final String FILE_NAME_LIST_FROM_DB = "FILE_NAME_LIST_FROM_DB";

	/** The Constant for file name list of column in process. */
	public static final String FILE_NAME_LIST_OF_COL_INP = "FILE_NAME_LIST_OF_COL_INP";

	/** The Constant for common network element. */
	public static final String COMMON_NETWORK_ELEMENT = "NA";

	/** The Constant for duplicate check interval type day. */
	public static final String DUPLICATE_CHECK_INTERVAL_TYPE_DAY = "DAY";

	/** The Constant for duplicate check interval type hour. */
	public static final String DUPLICATE_CHECK_INTERVAL_TYPE_HOUR = "HOUR";

	/** The Constant for duplicate check interval type minute. */
	public static final String DUPLICATE_CHECK_INTERVAL_TYPE_MINUTE = "MINUTE";

	/** The Constant for consolidation success reason for statistics detail. */
	public static final String CONSOLIDATION_SUCCESS_REASON_FOR_STAT_DETAIL = "CONSOLIDATION_SUCCESS";

	/** The Constant for parsing success reason for statistics detail. */
	public static final String PARSING_SUCCESS_REASON_FOR_STAT_DETAIL = "PARSING_SUCCESS";

	/** The Constant for processing success reason for statistics detail. */
	public static final String PROCESSING_SUCCESS_REASON_FOR_STAT_DETAIL = "PROCESSING_SUCCESS";

	/** The Constant for processing error reason for statistics detail. */
	public static final String PROCESSING_ERROR_REASON_FOR_STAT_DETAIL = "PROCESSING_ERROR";

	public static final String PROCESSING_FILE_ERROR_REASON_FOR_STAT_DETAIL = "PROCESSING_FILE_ERROR";
	
	/** The Constant for processing filter reason for statistics detail. */
	public static final String PROCESSING_FILTER_REASON_FOR_STAT_DETAIL = "PROCESSING_FILTER";

	/** The Constant for processing clone reason for statistics detail. */
	public static final String PROCESSING_CLONE_REASON_FOR_STAT_DETAIL = "PROCESSING_CLONE";

	/** The Constant for processing invalid reason for statistics detail. */
	public static final String PROCESSING_INVALID_REASON_FOR_STAT_DETAIL = "PROCESSING_INVALID";

	/** The Constant for processing duplicate reason for statistics detail. */
	public static final String PROCESSING_DUPLICATE_REASON_FOR_STAT_DETAIL = "PROCESSING_DUPLICATE";

	/** The Constant for distribution success reason for statistics detail. */
	public static final String DISTRIBUTION_SUCCESS_REASON_FOR_STAT_DETAIL = "DISTRIBUTION_SUCCESS";

	/** The Constant for distribution error reason for statistics detail. */
	public static final String DISTRIBUTION_ERROR_REASON_FOR_STAT_DETAIL = "DISTRIBUTION_ERROR";

	/** The Constant for rule id. */
	public static final String RULE_ID = "RULE_ID";

	/** The Constant for rule group name. */
	public static final String RULE_GROUP_NAME = "RULE_GROUP_NAME";

	/** The Constant for rule wise. */
	public static final String RULE_WISE = "rulewise";

	/** The Constant for group wise. */
	public static final String GROUP_WISE = "groupwise";

	/** The Constant for default driver id. */
	public static final String DEFAULT_DRIVER_ID = "DEFAULT_DRIVER_ID";

	/** The Constant for global sequence separator. */
	public static final String GLOBLE_SEQ_SEPERATOR = "||";

	/** The Constant MERGE_FILE_NAME. */
	public static final String MERGE_FILE_NAME = "MERGE_FILE_NAME";
	
	/** The Constant MERGE_FILE_NAME. */
	public static final String LIST_OF_FILE_NAME_FOR_PLUGINS = "LIST_OF_FILE_NAME_FOR_PLUGINS";

	/** The Constant for timesten data source name. */
	public static final String TIMESTEN_DATASOURCE_NAME = "TIMESTEN_DATASOURCE_NAME";

	/** The Constant CSV. */
	public static final String CSV = ".csv";
	
	public static final String JSON = ".json";

	/** The Constant GZ. */
	public static final String GZ = ".gz";

	/** The schedule type daily. */
	public static final String SCHEDULE_DAILY = "DAILY";

	/** The schedule type weekly. */
	public static final String SCHEDULE_WEEKLY = "WEEKLY";

	/** The schedule type monthly. */
	public static final String SCHEDULE_MONTHLY = "MONTHLY";

	/** The Constant SOURCE_PATH_MAP. */
	public static final String SOURCE_PATH_MAP = "SOURCE_PATH_MAP";

	/** The Constant DESTINATION_PATH. */
	public static final String DESTINATION_PATH = "DESTINATION_PATH";

	/** The Constant FILE. */
	public static final String FILE = "FILE";

	/** The Constant RECORD. */
	public static final String RECORD = "RECORD";

	/** The Constant File Grouping Type Day Basis. */
	public static final String FILEGROUPINGTYPEDAILY = "Day";

	public static final String FILE_STATISTICS_ENABLED = "DB_FILE_STATISTICS";

	/** The Constant FILE_RANGE. */
	public static final String FILE_RANGE = "FILE_RANGE";

	/** The Constant FILE_SEQUENCE_ORDER. */
	public static final String FILE_SEQUENCE_ORDER = "FILE_SEQUENCE_ORDER";

	public static final String IS_FILE_COMPLETED = "IS_FILE_COMPLETED";

	public static final String SEARCH_PARAMTER_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final String DEFAULT_PARTITION = "1.1.1.1"; //NOSONAR

	public static final String IPV6_DATA_TYPE = "ipv6";
	
	public static final String IPV6_TO_IPV4 = "ipv6toipv4";
	
	public static final String HOUR_DATE_FORMAT = "yyyyMMddHH";
	
	public static final String IPLOG_PARSE_FILE_DATE_FORMAT = "ddMMyyyyHH";
	
	public static final String FILENAME = "FILENAME";
	
	public static final String SOURCEPATH = "SOURCEPATH";
	
	public static final String STATUSMESSAGE = "STATUSMESSAGE";
	
	public static final String DUPICATECHECKKEY = "DUPICATECHECKKEY";
	
	public static final String SERVERINSTANCEID = "SERVERINSTANCEID";
	
	public static final String SERVICEINSTANCEID = "SERVICEINSTANCEID";
	
	public static final String SERIALNUMBER = "SERIALNUMBER";
	
	public static final String SOURCELOGICALUNITNAME = "SOURCELOGICALUNITNAME";

	public static final String BATCHID = "BATCHID";
	
	public static final String DRIVERID = "DRIVERID";
	
	public static final String PATH = "PATH";
	
	public static final String TIMESTAMP = "TIMESTAMP";
	
	public static final String FILE_SEQUENCE_COUNTER = "FILE_SEQUENCE_COUNTER";
	
	public static final String FILE_STATISTICS_TABLE = "TBLMEDCDRFILESTATISTICS";
	
	public static final String DATA_CONSOLIDATION_STAT_TABLE = "TBLMEDDATACONSSTATISTICS";
	
	public static final String FILEID = "FILEID";
	
	public static final String AGGREGATION_SUMMARY_TABLE = "TBLMAGGREGATIONSUMMARY";
	
	public static final String DB_BASED = "DB-BASED";
	
	public static final String QUERY_BASED = "Query-Based";
	
	public static final String DEVICENAME = "DEVICENAME";
	
	public static final String DEFAULT = "DEFAULT";

	public static final String HEADER_SUMMARY = "HEADER_SUMMARY";
}
