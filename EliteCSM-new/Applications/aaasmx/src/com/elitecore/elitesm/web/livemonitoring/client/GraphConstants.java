package com.elitecore.elitesm.web.livemonitoring.client;

public interface GraphConstants {

	public static int MEMORY_USAGE_DATA_INDEX = 1;
	
	public static int THREAD_STATISTIC_INDEX = 1;
	public static int PEAK_THREAD_STATISTIC_INDEX = 2;
	
	public static int AVG_RESPONSE_TIME_DATA_INDEX = 3;
	public static int TOTAL_REQUEST_DATA_INDEX = 4;
	public static int ACTIVE_THREAD_DATA_INDEX = 2;
	public static int TIMESTAMP_DATA_INDEX = 0;
	
	
	public static int IDX_AUTH_RESPONSETIME_TOTAL_REPSONSE_TIME = 1;
	public static int IDX_AUTH_RESPONSETIME_QUEUE_TIME = 2;
	public static int IDX_AUTH_RESPONSETIME_RMCOMMUNICATION_TIME = 3;
	
	public static int IDX_AUTH_ERRORS_BAD_AUTHENTICATORS = 1;
	public static int IDX_AUTH_ERRORS_DUPLICATE = 2;
	public static int IDX_AUTH_ERRORS_MALFORMED = 3;
	public static int IDX_AUTH_ERRORS_INVALID =4;
	public static int IDX_AUTH_ERRORS_UNKNOWN =5;
	public static int IDX_AUTH_ERRORS_DROPPED =6;
	
	public static int IDX_AUTH_SUMMARY_ACCESS_REQUEST=1;
	public static int IDX_AUTH_SUMMARY_ACCESS_ACCEPT=2;
	public static int IDX_AUTH_SUMMARY_ACCESS_REJECT=3;
	public static int IDX_AUTH_SUMMARY_ACCESS_CHALLENGE=4;
	public static int IDX_AUTH_SUMMARY_DROPPED=5;
	
	public static int IDX_AUTH_REJECT_USER_NOT_FOUND=1;
	public static int IDX_AUTH_REJECT_INVALID_PASSWORD=2;
	public static int IDX_AUTH_REJECT_INVALID_CHAP_PASSWORD=3;
	public static int IDX_AUTH_REJECT_INVALID_MSCHAPV1_PASSWORD=4;
	public static int IDX_AUTH_REJECT_INVALID_MSCHAPV2_PASSWORD=5;
	public static int IDX_AUTH_REJECT_INVALID_DIGEST_PASSWORD=6;
	public static int IDX_AUTH_REJECT_EAP_FAILURE=7;
	public static int IDX_AUTH_REJECT_AUTHENTICATION_FAILED=8;
	public static int IDX_AUTH_REJECT_ACCOUNT_NOT_ACTIVE=9;
	public static int IDX_AUTH_REJECT_ACCOUNT_EXPIRED=10;
	public static int IDX_AUTH_REJECT_CREDIT_LIMIT_EXCEEDED=11;
	public static int IDX_AUTH_REJECT_DIGEST_FAILURE=12;
	public static int IDX_AUTH_REJECT_RMCOMM_TIMEOUT=13;
	
	public static int IDX_ACCT_RESPONSETIME_TOTAL_REPSONSE_TIME = 1;
	public static int IDX_ACCT_RESPONSETIME_QUEUE_TIME = 2;
	public static int IDX_ACCT_RESPONSETIME_RMCOMMUNICATION_TIME = 3;
	
	public static int IDX_ACCT_ERRORS_BAD_AUTHENTICATORS = 1;
	public static int IDX_ACCT_ERRORS_DUPLICATE = 2;
	public static int IDX_ACCT_ERRORS_MALFORMED = 3;
	public static int IDX_ACCT_ERRORS_INVALID = 4;
	public static int IDX_ACCT_ERRORS_UNKNOWN =5;
	public static int IDX_ACCT_ERRORS_DROPPED =6;
	
	public static int IDX_ACCT_SUMMARY_START=1;
	public static int IDX_ACCT_SUMMARY_STOP=2;
	public static int IDX_ACCT_SUMMARY_INTRIM=3;
	public static int IDX_ACCT_SUMMARY_REQUEST=4;
	public static int IDX_ACCT_SUMMARY_DROPPED=5;
	
	
	
	
	
}
