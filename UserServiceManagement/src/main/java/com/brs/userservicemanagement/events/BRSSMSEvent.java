package com.brs.userservicemanagement.events;

import org.springframework.context.ApplicationEvent;

public class BRSSMSEvent extends ApplicationEvent{
private String mobile;
private String sms;
	public BRSSMSEvent(Object source,String mobile,String sms) {
		super(source);
	this.mobile=mobile;
	this.sms=sms;
	}
	public String getMobile() {
		return mobile;
	}
	public String getSms() {
		return sms;
	}

}
