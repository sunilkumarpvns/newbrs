package com.elitecore.diameterapi.core.common.session;

import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.imdg.HazelcastImdgInstance;
import com.elitecore.diameterapi.diameter.common.session.InMemorySessionFactory;
import com.elitecore.diameterapi.diameter.common.session.NullSessionFactory;
import com.elitecore.diameterapi.diameter.common.session.imdg.HazelcastSessionFactory;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class SessionFactoryManagerImpl implements SessionFactoryManager {
	private static final String MODULE = "SESSION_FACTORY_MANAGER";
	
	private Map<Long, SessionsFactory> appIdToFactory;
	private IDiameterStackContext diameterStackContext;
	private SessionsFactory inMemorySessionFactory;
	private SessionsFactory hazelCastSessionFactory;
	private SessionsFactory nullSessionFactory;
	private TimeSource timesource;
	
	public SessionFactoryManagerImpl(@Nonnull IDiameterStackContext diameterStackContext) {
		this(diameterStackContext, TimeSource.systemTimeSource());
	}
	
	SessionFactoryManagerImpl(@Nonnull IDiameterStackContext diameterStackContext, TimeSource timesource){
		Preconditions.checkNotNull(diameterStackContext, "Diameter Stack Context is null.");
		this.timesource = timesource;
		this.appIdToFactory = new HashMap<>();
		this.diameterStackContext = diameterStackContext;
		this.nullSessionFactory = new NullSessionFactory(this.timesource); 
		this.inMemorySessionFactory = new InMemorySessionFactory(this.diameterStackContext, this.timesource);
	}
	
	public void register(long appId, SessionFactoryType sessionFactory, Optional<HazelcastImdgInstance> optional) throws InitializationFailedException {
		switch(sessionFactory) {
		case HAZELCAST:   appIdToFactory.put(appId, getOrCreateHazelCastSessionFactory(optional));
			break;
		
		case INMEMORY: appIdToFactory.put(appId, inMemorySessionFactory);
			break;
		
		case NULLSESSION: appIdToFactory.put(appId, nullSessionFactory);
			break;
			
		default: 
				throw new InitializationFailedException("Invalid Session Factory type");
		}
	}

	@Override
	public SessionsFactory getSessionFactory(long appId) {
		if (appIdToFactory.containsKey(appId)) {
			return appIdToFactory.get(appId);
		} else {
		    if(LogManager.getLogger().isDebugLogLevel()){
			   LogManager.getLogger().debug(MODULE, "Using default In-Memory session factory  for Application-Id: " + appId + 
					", Reason: " + appId + " is unregisterd.");
		    }
		}
		return appIdToFactory.getOrDefault(appId, inMemorySessionFactory);
	}

	
	@Override
	public int getSessionCount() {
		return appIdToFactory.values().stream().distinct().mapToInt(sessionsFactory -> sessionsFactory.getSessionCount()).sum();
	}
	
	@Override
	public int removeIdleSession(long sessionTimeOut) {
		return appIdToFactory.entrySet().stream().mapToInt(o -> o.getValue().removeIdleSession(sessionTimeOut)).sum();
	}
	
	@Override
	public boolean hasSession(String sessionId) {
		for (Entry<Long, SessionsFactory> entry : appIdToFactory.entrySet()) {
			if(entry.getValue().hasSession(sessionId)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int removeAllSessions() {
		return appIdToFactory.entrySet().stream().mapToInt(o -> o.getValue().removeAllSessions()).sum();
	}
	
	@Override
	public void release(String sessionId) {
		appIdToFactory.entrySet().forEach(entry -> {
			if(entry.getValue().hasSession(sessionId)) {
				entry.getValue().getOrCreateSession(sessionId).release();
				return;
			}
		});
	}
	
	@Override
	public boolean hasSession(String sessionId, long applicationId) {
		SessionsFactory sessionsFactory = appIdToFactory.get(applicationId);
		if (sessionsFactory != null) {
			return sessionsFactory.hasSession(sessionId);
		}
		return false;
	}
	
	private SessionsFactory getOrCreateHazelCastSessionFactory(Optional<HazelcastImdgInstance> optional) throws InitializationFailedException  {
		if (hazelCastSessionFactory == null){
			if (optional.isPresent()) {
				hazelCastSessionFactory = new HazelcastSessionFactory(optional.get());
			} else {
				throw new InitializationFailedException();
			}
		} 
		return hazelCastSessionFactory;
	}
}
