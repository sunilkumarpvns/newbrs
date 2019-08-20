package com.elitecore.aaa.ws.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import com.elitecore.aaa.core.server.AAAServerContext;

/**
 * This data provider provides live radius sessions details from hazelcast in memory data grid.
 * @author chirag i. prajapati
 *
 */
public class RadiusSessionDataProvider {

	private AAAServerContext serverContext;

	public RadiusSessionDataProvider(AAAServerContext serverContext) {
		this.serverContext = serverContext;
	}

	public SessionData getAllSessionUsingKey(String key, String value) {
		
		SessionData sessionData = new SessionData();
		Collection<SessionDetails> sessionDetails = new ArrayList<>();
		
		SessionDetails session1 = new SessionDetails();
		session1.setCreatedTime(System.currentTimeMillis());
		session1.setSessionId(UUID.randomUUID().toString());
		session1.setFrammedIpAddress("127.0.0.1");
		session1.setLastAccessedTime(System.currentTimeMillis() + 5);
		sessionDetails.add(session1);
		
		
		sessionData.setSession(sessionDetails);
		return sessionData;
	}

	public int getTotalCount() {
		return 0;
	}

}
