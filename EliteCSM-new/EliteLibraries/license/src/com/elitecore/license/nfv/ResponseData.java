package com.elitecore.license.nfv;

public class ResponseData {
	
	public static final int SUCCESS = 200;
	public static final int ALREADY_ALLOCATED = 201;
	public static final int LICENSE_AUTHENTICITY_CHECK_SUCCESS = 202;
	public static final int LICENSE_AUTHENTICITY_CHECK_FAILURE = 300;
	public static final int LICENSE_LIMIT_REACHED = 301;
	public static final int UNKNOWN_INSTANCE = 404;
	public static final int DB_FAILURE = 500;
	public static final int ENCRYPTION_ERROR = 501;
	public static final int INVALID_LICENSE =502;
	
	
	public static final String LICENSE_AUTHENTICITY_CHECK_SUCCESS_MSG = "License authenticity validated successfully";
	public static final String LICENSE_LIMIT_REACHED_MSG = "Unable to allocate license, Reason: Maximum license instance limit reached";
	public static final String FAILED_TO_CHECK_AVAILABILITY = "Failed to check license availabiliy";
	public static final String LICENSE_AUTHENTICITY_CHECK_FAILURE_MSG = "Possibly license file has been tampered with or this insntance has been deregistered";
	public static final String UNKNOWN_INSTANCE_MSG = "This EliteAAA server is unknown to the SM serving the license allocation request";
	private int messageCode;
	private String message;
	
	public ResponseData() {
		
	}
	
	public ResponseData(int messageCode, String message) {
		this.messageCode = messageCode;
		this.message = message;
	}
	
	public void setMessageCode(int messageCode) {
		this.messageCode = messageCode;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getMessageCode() {
		return messageCode;
	}

	public String getMessage() {
		return message;
	}

}
