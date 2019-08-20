package com.elitecore.aaa.util.constants;

public class SessionManagerConstants {

	public static final String TABLE_NAME = "tblmconcurrentusers";
	public static final String START_TIME = "START_TIME";
	public static final String LAST_UPDATED_TIME = "LAST_UPDATED_TIME";
	public static final String SEQUENCE_NAME = "SEQ_TBLMCONCURRENTUSERS";
	public static final String AUTO_SESSION_CLOSE_ENABLED = "false";
	public static final Long SESSION_TIMEOUT = 120L;
	public static final Long SESSION_CLOSE_BATCH_COUNT = 50L;
	public static final Long SESSION_THREAD_SLEEP_TIME = 10L;
	public static final Integer SESSION_CLOSE_ACTION = 3;
	public static final String SESSION_ID_REF_ENTITY = "0:44";
	public static final String GROUPNAME_FIELD = "GROUPNAME";
	public static final String SERVICE_TYPE_FIELD = "NAS_PORT_TYPE";
	public static final String CONCURRENCY_IDENTITY_FIELD = "GROUPNAME";
	public static final Integer BATCH_SIZE = 1000;
	public static final Integer BATCH_UPDATE_INTERVAL = 100;
	public static final Integer DB_QUERY_TIMEOUT = 1;
	public static final Integer SESSION_OVERRIDE_ACTION = 0;
	public static final String SESSION_OVERRIDE_FIELD = "CALLING_STATION_ID";
}
