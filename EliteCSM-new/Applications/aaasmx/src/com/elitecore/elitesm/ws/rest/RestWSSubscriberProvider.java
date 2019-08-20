package com.elitecore.elitesm.ws.rest;

import javax.ws.rs.Path;

import org.springframework.stereotype.Service;

@Service("subscriber")
public class RestWSSubscriberProvider{

	private SubscriberProvider subscriberProvider;
	
	public RestWSSubscriberProvider() {
		subscriberProvider = new SubscriberProvider();
	}

	@Path("")
	public SubscriberProvider getSubscriberProvider() {
		return subscriberProvider;
	}
}
