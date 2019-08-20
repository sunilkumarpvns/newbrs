package com.elitecore.diameterapi.diameter.common.session.imdg;

import java.util.concurrent.TimeUnit;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.imdg.EventListner;
import com.elitecore.core.imdg.HazelcastImdgInstance;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionsFactory;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;

/**
 * 
 * @author malav
 *
 */
public class HazelcastSessionFactory implements SessionsFactory {

	@VisibleForTesting
	protected IMap<String, Session> sessionIdToSessionMap;
	
	public HazelcastSessionFactory(HazelcastImdgInstance hazelcastImdgServer){

		MapIndexConfig mapIndexConfig = new MapIndexConfig();
		mapIndexConfig.setAttribute("lastAccessedTime");
		mapIndexConfig.setOrdered(false);
		
		MapConfig mapConfig = new MapConfig("session");
		mapConfig.addMapIndexConfig(mapIndexConfig);
		
		hazelcastImdgServer.addMapConfig(mapConfig, new EventListner() {
			
			@Override
			public void onStartUp(HazelcastInstance hazelcastInstance) {
				sessionIdToSessionMap = hazelcastInstance.getMap("session");
			}
		});
		
		
		hazelcastImdgServer.addSerializableFactory(
				HazelcastSessionSerializationFactory.FACTORY_ID, new HazelcastSessionSerializationFactory(this));
	}
	
	@Override
	public boolean removeSession(Session session) {
		return removeSession(session.getSessionId());
	}

	@Override
	public int removeAllSessions() {
		int totalSession = sessionIdToSessionMap.size();
		sessionIdToSessionMap.clear();
		return totalSession - sessionIdToSessionMap.size();
	}

	@Override
	public long removeAllSessions(String sessionReleaseKey) {
		// TODO malav use predicate to do this
		return 0;
	}

	@Override
	public boolean removeSession(String sessionId) {
		sessionIdToSessionMap.delete(sessionId);
		unlock(sessionId);
		return true;
	}

	@Override
	public int getSessionCount() {
		return sessionIdToSessionMap.size();
	}

	@Override
	public int removeIdleSession(long idleTime) {
		int totalSessions = sessionIdToSessionMap.size();
		long lastAcceptableTime = System.currentTimeMillis() - idleTime;
		sessionIdToSessionMap.removeAll(new SqlPredicate("lastAccessedTime <= " + lastAcceptableTime));
		return totalSessions - sessionIdToSessionMap.size();
	}

	@Override
	public Session getOrCreateSession(String sessionId) {
		lock(sessionId);
		Session session = sessionIdToSessionMap.get(sessionId);
		if (session == null) {
			session = new HazelcastSession(sessionId, this);
		}
		return session;
	}

	@Override
	public void update(Session session) {
		String sessionId = session.getSessionId();
		sessionIdToSessionMap.set(sessionId, session);
		unlock(sessionId);
	}

	private void lock(String key) {
		sessionIdToSessionMap.lock(key, 1, TimeUnit.SECONDS);
	}

	private void unlock(String key) {
		sessionIdToSessionMap.unlock(key);
	}

	@Override
	public ISession readOnlySession(String sessionId) {
		ISession session = null;
		if (hasSession(sessionId)) {
			session = createReadOnlySession(sessionIdToSessionMap.get(sessionId));
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

	@Override
	public boolean hasSession(String sessionId) {
		return sessionIdToSessionMap.containsKey(sessionId);
	}

}
