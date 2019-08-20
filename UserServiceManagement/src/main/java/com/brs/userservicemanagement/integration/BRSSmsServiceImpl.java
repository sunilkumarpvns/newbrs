package com.brs.userservicemanagement.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BRSSmsServiceImpl 
implements BRSSmsService{
	@Autowired
private RestTemplate restTemplate;
	@Override
	public String sendSms(String mobile, String sms) {
	String apiKey="4BmsOTbTaNU-Kl6iHlAapryfZoZpOPkrzNW44IfwUp";
	String sender="TXTLCL";
	String url="https://api.textlocal.in/send/?apiKey="+apiKey+"&sender="+sender+"&message="+sms+"&numbers="+mobile;
	  String jsonString=restTemplate.getForObject(url,String.class);
		

		
		

	  return jsonString;

	}

}
