package com.elitecore.diameterapi.core.common.session;

import java.util.Optional;

import com.elitecore.core.imdg.HazelcastImdgInstance;
import com.elitecore.core.commons.InitializationFailedException;

public interface SessionFactoryManager {
	
	SessionsFactory getSessionFactory(long appId);
	void register(long appId, SessionFactoryType sessionFactoryType, Optional<HazelcastImdgInstance> optional)
			throws InitializationFailedException;
	/**
	 * @return Total number of sessions present in all session factories
	 */
	int getSessionCount();
	int removeIdleSession(long sessionTimeOut);
	boolean hasSession(String sessionId);
	boolean hasSession(String sessionId, long applicationId);
	int removeAllSessions();
	void release(String sessionId);
}
