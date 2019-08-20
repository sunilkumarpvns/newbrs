package com.elitecore.diameterapi.diameter.common.session;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionEventListener;
import com.elitecore.diameterapi.core.common.session.SessionsFactory;

public class NullSessionFactory implements SessionsFactory {

	private static final String NULL_SESSION_ID = "NULL_SESSION_ID";
	private NullDiameterSession nullDiameterSession;
	public NullSessionFactory(TimeSource timesource) {
		this.nullDiameterSession = new NullDiameterSession(NULL_SESSION_ID, new SessionEventListener() {
			
			@Override
			public void update(Session session) {
				//no-op
			}
			
			@Override
			public boolean removeSession(Session session) {
				return false;
			}
		}, timesource);
	}
	
	@Override
	public boolean removeSession(Session session) {
		return false;
	}

	@Override
	public void update(Session session) {
		// no-op
		
	}

	@Override
	public boolean hasSession(String sessionId) {
		return false;
	}

	@Override
	public int removeAllSessions() {
		return 0;
	}

	@Override
	public long removeAllSessions(String sessionReleaseKey) {
		return 0;
	}

	@Override
	public boolean removeSession(String sessionId) {
		return false;
	}

	@Override
	public int getSessionCount() {
		return 0;
	}

	@Override
	public int removeIdleSession(long idleTime) {
		return 0;
	}

	@Override
	public ISession readOnlySession(String sessionId) {
		return getOrCreateSession(sessionId);
	}

	@Override
	public Session getOrCreateSession(String sessionId) {
		return nullDiameterSession;
	}
}
