package com.elitecore.core.notification.sms.http;


public class SMSHTTPConfiguration {

	private String serviceURL;
	private String sender;
	private String userName;
	private String password;
	private String deliveryURL;
	private boolean isEnabled;
	private String isFlash;
	private String param1;
	private String param2;
	private String param3;
	
	public SMSHTTPConfiguration(String serviceURL, String sender, String userName, String password, String deliveryURL, boolean isEnabled, 
			String isFlash, String param1, String param2, String param3) {
		this.serviceURL = serviceURL;
		this.sender = sender;
		this.userName = userName;
		this.password = password;
		this.deliveryURL = deliveryURL;
		this.isEnabled = isEnabled;
		this.isFlash = isFlash;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
	}

	public String getServiceURL() {
		return serviceURL;
	}

	public String getSender() {
		return sender;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getDeliveryURL() {
		return deliveryURL;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public String isFlash() {
		return isFlash;
	}

	public String getParam1() {
		return param1;
	}

	public String getParam2() {
		return param2;
	}

	public String getParam3() {
		return param3;
	}
	
}
