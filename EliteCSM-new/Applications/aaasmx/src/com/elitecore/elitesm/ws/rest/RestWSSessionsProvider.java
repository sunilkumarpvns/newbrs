package com.elitecore.elitesm.ws.rest;

import javax.ws.rs.Path;

import org.springframework.stereotype.Service;

@Service("session")
public class RestWSSessionsProvider {

	private SessionProvider sessionProvider;
	
	public RestWSSessionsProvider() {
		sessionProvider = new SessionProvider();
	}
	
	@Path("")
	public SessionProvider getSessionProvider() {
		return sessionProvider;
	}
}