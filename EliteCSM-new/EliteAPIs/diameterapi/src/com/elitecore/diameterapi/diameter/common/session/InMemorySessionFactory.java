/**
 * 
 */
package com.elitecore.diameterapi.diameter.common.session;

import java.util.concurrent.ConcurrentHashMap;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionsFactory;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;

/**
 * 
 * @author pulindani
 * @author narendra.pathai
 * @author malav
 *
 */
/*
 * This is In-memory session factory.
 * The current code own by this class was previously divided into two class
 * 1) Abstract super class for this class
 * 2) this class only has the create method  
 */
public class InMemorySessionFactory implements SessionsFactory {
	
	private static final String MODULE = "SESSN-FACTORY";
	private TimeSource timeSource;
	protected final ConcurrentHashMap<String, Session> sessionIdToSession;

	public InMemorySessionFactory(IDiameterStackContext diameterStackContext, TimeSource timeSource) {
		this.timeSource = timeSource;
		sessionIdToSession = new ConcurrentHashMap<String,Session>(1024, CommonConstants.DEFAULT_LOAD_FACTOR, diameterStackContext.getMaxWorkerThreads());
		
	}

	@Override
	public int removeAllSessions() {
		int removedSessionCount = 0;

		for (Session session : sessionIdToSession.values()) {
			session.release();
			removedSessionCount++;
		}
		if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
			LogManager.getLogger().warn(MODULE, "Removed All Sessions. No of Sessions removed: " + removedSessionCount);
		return removedSessionCount;
	}
	
	@Override
	public long removeAllSessions(String sessionReleaseKey) {
		if (sessionReleaseKey == null) {
			return 0;
		}

		long releaseCnt = 0;
		for (Session session : sessionIdToSession.values()) {
			if (sessionReleaseKey.equals(session.getParameter(Session.SESSION_RELEASE_KEY))) {
				session.release();
				releaseCnt++;
			}
		}
		return releaseCnt;
	}
	
	@Override
	public boolean removeSession(Session session) {
		return removeSession(session.getSessionId());
	}
	
	@Override
	public boolean removeSession(String sessionId) {
		return sessionIdToSession.remove(sessionId) != null;
	}
	
	@Override
	public int getSessionCount(){
		return sessionIdToSession.size();
	}
	
	@Override
	public int removeIdleSession(long idleTime){
		
		if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
			LogManager.getLogger().warn(MODULE, "Session Cleanup Started. Sessions idle for " + (idleTime/1000) + " Sec. will be released.");
		
		int noOfSessions = 0;
		LogManager.getLogger().warn(MODULE, "Total no of active Sessions are: " + sessionIdToSession.size());
		Long currentTimeMs = timeSource.currentTimeInMillis();
		
		for(Session sessionData : sessionIdToSession.values()){
			long sessionElapsedTime =  currentTimeMs - sessionData.getLastAccessedTime();
			if(sessionElapsedTime > idleTime){
				sessionData.release();
				noOfSessions++;
			}
		}
		if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
			LogManager.getLogger().warn(MODULE, "Number of sessions removed older than "+ (idleTime/1000) +" Sec. = "+noOfSessions);
		}
		

		return noOfSessions;
	}
	
	@Override
	public Session getOrCreateSession(String sessionId) {
		Session session = sessionIdToSession.get(sessionId);
		if (session == null) {
			session = new DiameterSession(sessionId, this, timeSource);
			sessionIdToSession.put(sessionId, session);
		}
		return session;
	}

	@Override
	public void update(Session session) {
		// Not Required
	}

	@Override
	public boolean hasSession(String sessionId) {
		return sessionIdToSession.containsKey(sessionId);
	}

	@Override
	public ISession readOnlySession(String sessionId) {
		ISession session = null;
		if (hasSession(sessionId)) {
			session = createReadOnlySession(sessionIdToSession.get(sessionId));
		}
		return session;
	}

	private ISession createReadOnlySession(final Session session) {
		return new ISession() {
			
			@Override
			public Object setParameter(String key, Object parameterValue) {
				throw new UnsupportedOperationException("Cannot set parameters using read only session");
			}

			@Override
			public Object removeParameter(String key) {
				throw new UnsupportedOperationException("Cannot remove parameters using read only session");
			}

			@Override
			public void release() {
				throw new UnsupportedOperationException("Cannot release using read only session");
			}

			@Override
			public String getSessionId() {
				return session.getSessionId();
			}

			@Override
			public Object getParameter(String str) {
				return session.getParameter(str);
			}

			@Override
			public long getLastAccessedTime() {
				return session.getLastAccessedTime();
			}

			@Override
			public long getCreationTime() {
				return session.getCreationTime();
			}

			@Override
			public void update(ValueProvider valueProvider) {
				throw new UnsupportedOperationException("Cannot update using read only session");
			}
		};
	}

}
