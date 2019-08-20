package com.brs.userservicemanagement.events;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class BRSSmsEventPublisher {
	private static Logger logger=Logger.getLogger(BRSSmsEventPublisher.class);
	@Autowired
private ApplicationEventPublisher applicationEventPublisher;
public void sendSmsPublisher(String mobile,String sms){
	BRSSMSEvent brssmsEvent=new BRSSMSEvent(this, mobile, sms);
	  applicationEventPublisher.publishEvent(brssmsEvent);
	}
}





