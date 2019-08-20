package com.elitecore.aaa.radius.session;

import java.util.Set;

import com.elitecore.core.serverx.sessionx.inmemory.ISession;

public interface SessionsFactory extends SessionEventListener {

	public boolean hasSession(String sessionId);

	public int removeAllSessions();

	public long removeAllSessions(String sessionReleaseKey);

	public boolean removeSession(String sessionId);

	public int getSessionCount();

	public int removeIdleSession(long idleTime);

	public ISession getOrCreateSession(String sessionId);
	
	public Set<String> search(String attributeName, String attributeValue);
	
	public ISession readOnlySession(String sessionId);
	
	public static final SessionsFactory NO_SESSION_FACTORY = new SessionsFactory() {


		@Override
		public boolean removeSession(ISession session) {
			return false;
		}

		@Override
		public boolean removeSession(String sessionId) {
			return false;
		}

		@Override
		public int removeIdleSession(long idleTime) {
			return 0;
		}

		@Override
		public long removeAllSessions(String sessionReleaseKey) {
			return 0;
		}

		@Override
		public int removeAllSessions() {
			return 0;
		}

		@Override
		public boolean hasSession(String sessionId) {
			return false;
		}

		@Override
		public int getSessionCount() {
			return 0;
		}

		@Override
		public ISession getOrCreateSession(String sessionId) {
			return ISession.NO_SESSION;
		}

		@Override
		public Set<String> search(String attributeName, String attributeValue) {
			return null;
		}

		@Override
		public ISession readOnlySession(String sessionId) {
			return ISession.NO_SESSION;
		}

		@Override
		public void update(ISession session) {
			// NO OP			
		}

	};

}
