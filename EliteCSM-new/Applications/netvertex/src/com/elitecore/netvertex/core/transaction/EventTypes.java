package com.elitecore.netvertex.core.transaction;

public class EventTypes {
	
	public static final String AAR_RCVD = "AAR_RCVD";
	
	/************  Events for New Service Request  ********/	 
	public static final String SUCCESS_RES_RCVD = "SUCCESS-RES-RCVD";
	public static final String FAILED_RES_RCVD = "FAILED-RES-RCVD";
	public static final String RULE_INSTALL_SUCCESS = "RULE-INSTALL-SUCCESS"; 
	public static final String RULE_INSTALL_FAIL = "RULE-INSTALL-FAIL";
	public static final String PCRF_RES_RCVD = "RES-RCVD";
	
	/************  Events for Service Remove  ********/
	public static final String STOP_SERVICE_ACK_RCVD = "STOP-SERVICE-ACK-RCVD";
	public static final String RULE_REMOVED = "RULE-REMOVED"; 
	
	/************  Events for Session Start *************/
	public static final String SESSION_START = "SESSION-START";
	
	/************  Events for Session Stop *************/
	public static final String SESSION_STOP = "SESSION-STOP";
	
	/************ Event When CCR-U arrives with Event-Trigger AVP ***************/
	public static final String SESSION_UPDATE = "SESSION-UPDATE";
	
	/************* Event When ASR Received ****************/
	public static final String ASA_RCVD = "ASA-RCVD";
	
	/************* Event for Session Termination *******************************/
	public static final String SESSION_TERMINATION = "SESSION-TERMINATION";
	
	/************ Event when RAA Received *************************************/
	public static final String RAA_RCVD = "RAR-RCVD";

	/************ Event for Time-Base, BOD and Package Change  *************************************/
	public static final String SESSION_RE_AUTH = "SESSION-RE-AUTH";
	
	/************ Event for Time-Base  *************************************/
	public static final String SESSION_DISCONNECT = "SESSION-DISCONNECT";
	
	public static final String ROUTE_REQUEST = "ROUTE_REQUEST";

	/************ Event for Spending-Status-Notification(SN) Request received for Sy Application ***************/
	public static final String SNR_RCVD = "SNR-RCVD";
	
	public static final String EVENT_REQUEST_RCVD = "EVENT-REQUEST-RCVD";
	
}
