package com.elitecore.corenetvertex.constants;

import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;

import java.util.concurrent.TimeUnit;

public class CommonConstants {

    public static final String GATE_STATUS_OPEN = "OPEN";
    public static final String GATE_STATUS_CLOSE = "CLOSE";

    public static final String ADD_ON_ID = "ADD-ON-ID";
    public static final int BATCH_SIZE_MIN = 10;

    public static final String DEFAULT_SERVER_INSTANCE_VALUE = "-1";

    public static final String DOUBLE_QUOTES = "\"";
    public static final int SCHEDULER_QUERY_TIMEOUT_SEC = 60;
	public static final long RESET_DAY_INTERVAL = 2*24*60*60*1000l;
	public static final int ZERO_VALUE = 0;

    public static final int QUERY_TIMEOUT_ERRORCODE = 1013;
    public static final int DUPLICATE_ENTRY_ERRORCODE = 1;
    public static final int QUERY_TIMEOUT_DEFAULT = 1;
    public static final int MAX_QUERY_TIMEOUT_COUNT_DEFAULT = 200;
    public static final char SINGLE_QUOTE = '\'';
    public static final char BACKSLASH = '\\';
    public static final char ASTERISK = '*';
    public static final char OPENING_BRACES = '{';
	public static final char CLOSING_BRACES = '}';
    public static final char OPENING_PARENTHESES = '(';
    public static final char CLOSING_PARENTHESES = ')';
    public static final char NUL = '\0'; // NUL repesentation in char
    public static final char SPACE = 0x20; // hexdecimal value of space
    public static final char TILDE = 0x7E;
    public static final char QUESTION_MARK = '?';
    public static final char COMMA = ',';
	public static final char PIPE = '|';
	public static final char DASH = '-';
	public static final char DOT = '.';
	public static final char SEMICOLON = ';';
    public static final String USAGE_KEY_SEPARATOR = ":";
    public static final char COLON = ':';
	public static final int MIN_POSITIVE_INTEGER = 1;
	public static final String NOT_APPLICABLE = "NA";
	
	//change value whenever size of ID field of service table
	public static final String ALL_SERVICE_ID = "DATA_SERVICE_TYPE_1";
    public static final long ALL_SERVICE_IDENTIFIER = 1;
	public static final String ALL_SERVICE_NAME = "All-Service";
    public static final long DEFAULT_RATING_GROUP_IDENTIFIER = 0;
	public static final String STATUS_DELETED = "DELETED";
	public static final String DEFAULT_GROUP = "DefaultGroup";
	public static final String DEFAULT_GROUP_ID = "GROUP_1";
	public static final String DEFAULT_PREFIX_LIST_MASTER_ID = "DEFAULT_PREFIX_LIST_MASTER_1";
	public static final String VOICE_SERVICE_ALIAS = "VOICE";
	public static final String DATA_SERVICE_ALIAS = "DATA";
	
	public static final int DEFAULT_BATCH_QUERY_TIMEOUT = 10;
	public static final int DEFAULT_BATCH_SIZE = 100;
	public static final int MAX_BATCH_SIZE = 2000;
	
	public static final String STATUS_ACTIVE = "ACTIVE";
	public static final String STATUS_INACTIVE = "INACTIVE";

	public static final String ALL_IP_ADDRESS = "0.0.0.0";
	
	/*------------- System Parameter-------------*/
	public static final String DATE_FORMAT = "DATE_FORMAT";
	public static final String SHORT_DATE_FORMAT = "SHORT_DATE_FORMAT";
	public static final String DEFAULT_DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
	public static final String DEFAULT_SHORT_DATE_FORMAT = "MM/dd/yyyy";
	public static final char EQUAL = '=';
	public static final char HASH = '#';
	public static final char AT_SIGN = '@';

	
	public static final long UNSIGNED32_MAX_VALUE = 4294967295l;
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	public static final long FUTURE_DATE = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(20*365l);

	public static final int PRIMARY_INSTANCE = 1;
	public static final int SECONDARY_INSATNCE = 0;
	public static final String FAIL = "fail";
	public static final String REPLACE = "replace";
	public static final String SUCCESS = "SUCCESS";
	public static final Integer MIN_INTERIM_INTERVAL = 1;
	public static final Integer MAX_INTERIM_INTERVAL = 2880;

	public static final String XML = ".xml";
	public static final String TEXT_XML_TYPE = "text/xml";
	public static final String FORWARD_SLASH="/";
	public static final String UNDERSCORE="_";



	/*----------------------- Concurrent Hash Map --------------------------*/
	public static final int CONCURRENCY_LEVEL = 50;
	public static final float LOAD_FACTOR = 0.75f;
	public static final int INITIAL_CAPACITY = 10000;


	public static final Splitter COMMA_SPLITTER = Strings.splitter(COMMA).trimTokens();
	public static final Splitter PIPE_SPLITTER = Strings.splitter(PIPE).trimTokens();
	public static final int DEFAULT_GROUP_MAX_GLOBAL_PKGS = 20;
	public static final int GROUP_WISE_MAX_GLOBAL_PKGS = 4;

	public static final String CRBN = "CRBN";

	public static final String UTF_8 = "UTF-8";

	public static final String EMPTY_STRING = "";
	public static final String NULL_STRING = "null";
	public static final String UNDEFINED_STRING = "undefined";
	public static final String MONITORING_KEY = "Monitoring Key";

	public static final String ENABLED = "Enabled";
	public static final long TEN_MINUTES = TimeUnit.MINUTES.toMillis(10);
	public static final String DEFAULT_TIMESTAMP_FORMAT = "dd-MMM-yy hh.mm.ss.S a";
	public static final long QUOTA_UNLIMITED_SPECIAL_SYMBOL = -1;
	public static final long QUOTA_UNLIMITED = 999999999999999999L;
	public static final long  QUOTA_UNDEFINED = -999999999999999999L;

	public static final long  UNLIMITED_QCF_QUOTA = 999999999999999999L;

	public static final String ALL_MONETARY_SERVICE = "DATA";
	public static final String ALL_SERVICE_DISPLAY_VALUE = "All Service";
	public static final String MONEY_DATA_SERVICE = "DATA";

	public static final String CSV_TYPE = "csv";

	/*------------ Web-Service --------------------------*/
	public static final String SERVER_INSTANCE_WS_CONTEXT_PATH = "/server-instance";
	public static final String ADMIN_USER_NAME = "admin";
	public static final String ADMIN_STAFF_ID = "STAFF_1";


	public static final int DEFAULT_LDAP_PORT = 389;
	public static final int DEFAULT_MAPPING_COUNT = 30;
	public static final int MIN_QUERY_TIMEOUT_IN_SEC = 1;
	public static final int MAX_QUERY_TIMEOUT_IN_SEC = 10;
	public static final String RATING_GROUP_QUOTA_TOPUP_ID_1 = "RATING_GROUP_QUOTA_TOPUP_ID_1";
	public static final String QUOTA_TOPUP_RATING_GROUP_NAME = "QUOTA_TOPUP_RATING_GROUP";

	/*------------ EDR Specific Operations --------------------------*/
	public static final String SUBSCRIBE_ADDON_PRODUCT_OFFER = "Subscribe AddOn Product Offer";
	public static final String UPDATE_ADDON_PRODUCT_OFFER = "Update AddOn Product Offer Subscription";
	public static final String SUBSCRIBE_TOPUP = "Subscribe TopUp";
	public static final String SUBSCRIBE_BOD = "Subscribe BOD";
	public static final String UPDATE_FNF_GROUP = "Update FnF Group";
	public static final String UPDATE_TOPUP = "Update TopUp Subscription";
	public static final String ADD_MONETARY_BALANCE = "Add Monetary Balance";
	public static final String UPDATE_MONETARY_BALANCE = "Update Monetary Balance";
	public static final String RECHARGE_MONETARY_BALANCE = "Recharge Monetary Balance";
	public static final String FLEXI_RECHARGE_MONETARY_BALANCE = "Flexi-Recharge Monetary Balance";
	public static final String UPDATE_CREDIT_LIMIT = "Update Credit Limit";
	public static final String DIRECT_DEBIT_BALANCE = "Direct Debit Balance";
	public static final String CREATE_SUBSCRIBER = "Create Subscriber";
	public static final String UPDATE_SUBSCRIBER = "Update Subscriber";
	public static final String DELETE_SUBSCRIBER = "Delete Subscriber";
	public static final String RESTORE_SUBSCRIBER = "Restore Subscriber";
	public static final String PURGE_SUBSCRIBER = "Purge Subscriber";
	public static final String IMPORT_SUBSCRIBER = "Import Subscriber";
	public static final String ADD_EXTERNAL_ALTERNATE_ID = "Add External Alternate Identity";
	public static final String UPDATE_EXTERNAL_ALTERNATE_ID_EDR = "Update External Alternate Identity";
	public static final String REMOVE_EXTERNAL_ALTERNATE_ID = "Remove External Alternate Identity";
	public static final int ONE_DAY = 1440;
	public static final String CHANGE_BILL_DAY = "Change Bill Day";


	public static final String UNLIMITED = "UNLIMITED";
	public static final String RNC = "RnC";
    public static final String VERSION_LABLE_KEY_SEPERATOR = "--";
    public static final long SLAB_UNLIMITED = -1;
    public static final int RATE_PRECESION = 6;
    public static final String VOLTDB = "voltdb";
	public static final int DEFAULT_SUBSCRIPTION_PRIORITY = 100;
	public static final int MIN_SUBSCRIPTION_PRIORITY = 1;
	public static final int MAX_SUBSCRIPTION_PRIORITY = 99999;
	public static final String STRIP_PREFIX = "+";

	/*------------ Monetary --------------------------*/
	public static final String MAX_MONETARY_VALUE = "999999999.999999"; //Do not remove. Being used in JSP
	public static final String MONETARY_BALANCE_PRECISION = "6"; //Do not remove. Being used in JSP
	public static final double MONETARY_VALUE_LIMIT = 999999999.999999D;

	public static final long MILLISECONDS_FOR_24_HOUR = TimeUnit.HOURS.toMillis(24);
    public static final String PERECNTILE = "%";
    public static final int MAX_EXTERNAL_ALTERNATE_ID_LIMIT = 10;

	public static final String DEFAULT_VERSION="Default Version";
	public static final int MAX_AUTO_SUBSCRIPTION_COUNT = 10;
	public static final String TRUE = "true";
	public static final String FALSE = "false";

	private CommonConstants(){
	}
}
