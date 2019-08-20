package com.elitecore.aaa.radius.session.imdg;

import static com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession.LAST_ACCESSED_TIME_INDEX;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.elitecore.aaa.core.conf.impl.ImdgIndexDetail;
import com.elitecore.aaa.core.conf.impl.RadiusSessionFieldMapping;
import com.elitecore.aaa.radius.session.SessionsFactory;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession.IndexField;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.imdg.EventListner;
import com.elitecore.core.imdg.HazelcastImdgInstance;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;

/**                                     
 * 
 * @author soniya
 *
 */
public class HazelcastRadiusSessionFactory implements SessionsFactory {

	private static final String MODULE = "RAD-SESSION-FACTORY";
	private IMap<String, ISession> sessionIdToSessionMap;
	private TimeSource timeSource;
	private List<ImdgIndexDetail> sessionIndexFieldMapping;
	private List<RadiusSessionFieldMapping> sessionDataFieldMapping;


	public HazelcastRadiusSessionFactory(HazelcastImdgInstance hazelcastImdgServer, List<ImdgIndexDetail> sesisonIndexFieldMapping, List<RadiusSessionFieldMapping> sessionDataFieldMapping) {
		this.sessionIndexFieldMapping = sesisonIndexFieldMapping;
		this.sessionDataFieldMapping = sessionDataFieldMapping;

		MapConfig mapConfig = new MapConfig("radiussession");
		mapConfig.addMapIndexConfig(new MapIndexConfig(LAST_ACCESSED_TIME_INDEX, false));
		mapConfig.addMapIndexConfig(new MapIndexConfig(IndexField.INDEX_0.getIndex(),false));
		mapConfig.addMapIndexConfig(new MapIndexConfig(IndexField.INDEX_1.getIndex(),false));
		mapConfig.addMapIndexConfig(new MapIndexConfig(IndexField.INDEX_2.getIndex(),false));
		mapConfig.addMapIndexConfig(new MapIndexConfig(IndexField.INDEX_3.getIndex(),false));
		mapConfig.addMapIndexConfig(new MapIndexConfig(IndexField.INDEX_4.getIndex(),false));
		mapConfig.addMapIndexConfig(new MapIndexConfig(IndexField.INDEX_5.getIndex(),false));


		hazelcastImdgServer.addMapConfig(mapConfig, new EventListner() {

			@Override
			public void onStartUp(HazelcastInstance hazelcastInstance) {
				sessionIdToSessionMap = hazelcastInstance.getMap("radiussession");
			}
		});


		hazelcastImdgServer.addSerializableFactory(
				HazelcastRadiusSessionSerializationFactory.FACTORY_ID, new HazelcastRadiusSessionSerializationFactory(this, sesisonIndexFieldMapping, sessionDataFieldMapping));

		timeSource = TimeSource.systemTimeSource();
	}

	public HazelcastRadiusSessionFactory(HazelcastImdgInstance hazelcastImdgServer, TimeSource timeSource, List<ImdgIndexDetail> radiusFieldMapping, List<RadiusSessionFieldMapping> sessionDataFieldMapping ) {
		this(hazelcastImdgServer,radiusFieldMapping, sessionDataFieldMapping);
		this.timeSource = timeSource;
	}

	@Override
	public boolean removeSession(ISession session) {
		return removeSession(session.getSessionId());
	}

	@Override
	public int removeAllSessions() {
		int totalSession = sessionIdToSessionMap.size();
		sessionIdToSessionMap.clear();
		return totalSession - getSessionCount();
	}

	@Override
	public long removeAllSessions(String sessionReleaseKey) {
		// TODO use predicate to do this
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
		long lastAcceptableTime = timeSource.currentTimeInMillis() - idleTime;
		sessionIdToSessionMap.removeAll(new SqlPredicate("lastAccessedTime <= " + lastAcceptableTime));
		return 0;
	}

	@Override
	public ISession getOrCreateSession(String sessionId) {
		lock(sessionId);
		ISession session = sessionIdToSessionMap.get(sessionId);
		if (session == null) {
			session = new HazelcastRadiusSession(sessionId, timeSource, this, sessionIndexFieldMapping, sessionDataFieldMapping);
			LogManager.getLogger().info(MODULE, "Session is created with session-id: " + sessionId);
		} else {
			LogManager.getLogger().info(MODULE, "Session exists for session id: " + sessionId);
		}
		return session;
	}

	@Override
	public void update(ISession session) {
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
	public Set<String> search(String index, String value) {
		return  sessionIdToSessionMap.keySet(new SqlPredicate(index + " = '" + value + "'"  ));
	}

	@Override
	public ISession readOnlySession(String sessionId) {
		ISession session = null;
		if (hasSession(sessionId)) {
			session = createReadOnlySession(sessionIdToSessionMap.get(sessionId));
		}
		return session;
	}

	private ISession createReadOnlySession(final ISession session) {
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

	public class RadiusSessionCleanupTask extends BaseIntervalBasedTask {

		long sessionTimeOut ;
		long sessionCleanupInterval;

		public RadiusSessionCleanupTask (long sessionTimeOut, long sessionCleanupInterval){
			this.sessionTimeOut = sessionTimeOut;
			this.sessionCleanupInterval = sessionCleanupInterval;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			LogManager.getLogger().info(MODULE, "Executing Radius session cleanup task.");
			removeIdleSession(sessionTimeOut);

		}

		@Override
		public long getInitialDelay(){
			return sessionCleanupInterval;
		}

		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.SECONDS;

		}

		@Override
		public long getInterval() {
			return sessionCleanupInterval;
		}

	}

}
