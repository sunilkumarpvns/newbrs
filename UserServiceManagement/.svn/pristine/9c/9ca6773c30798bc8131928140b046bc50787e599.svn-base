package com.brs.userservicemanagement.events;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import com.brs.userservicemanagement.integration.BRSSmsService;

@Service
public class BRSSMSEventListener implements ApplicationListener<BRSSMSEvent> {

	private static final Logger logger = Logger
			.getLogger(BRSSMSEventListener.class);
	@Autowired
	private BRSSmsService smsService;

	@Override
	public void onApplicationEvent(BRSSMSEvent smsEvent) {
		logger.info("Entered into onApplicationEvent() ");
		long startTime=System.currentTimeMillis();
		String jsonString = smsService.sendSms(smsEvent.getMobile(),
				smsEvent.getSms());
		long endTime=System.currentTimeMillis();
		logger.info(jsonString+" [ "+Thread.currentThread().getName()+" ] "+(endTime-startTime));

	}

}
