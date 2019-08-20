package com.elitecore.nvsmx.system.util.migrate;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.elitecore.corenetvertex.constants.CommonConstants;

public class SubscriberImportResult {
	
	private static final String NA = "NA";
	private static final String FAIL = "FAIL";
	private static final String SUCCESS = "SUCCESS";
	private static final String DATE_FORMAT = "dd-MMM-yyyy HH:mm:ss:SSS";
	
	private int index;
	private String subscriberId;
	private boolean isSuccess;
	private String message;
	
	public SubscriberImportResult(int index) {
		this.index = index;
		this.subscriberId = NA;
		this.message = NA;
		this.isSuccess = false;
	}
	
	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}
	
	public String getSubscriberId() {
		return subscriberId;
	}
	
	public void setResult(boolean result) {
		this.isSuccess = result;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public boolean isSuccess() {
		return isSuccess;
	}

	public String getCDRRecord() {
		return new StringBuilder().append(new SimpleDateFormat(DATE_FORMAT).format(new Date())).append(CommonConstants.COMMA)
				.append(index).append(CommonConstants.COMMA)
				.append(subscriberId).append(CommonConstants.COMMA)
				.append(isSuccess ? SUCCESS : FAIL).append(CommonConstants.COMMA)
				.append(message).toString();
	}
}
