package com.elitecore.diameterapi.core.common.session;

import com.elitecore.core.serverx.sessionx.inmemory.ISession;

/**
 * @author malav
 */
public interface SessionsFactory extends SessionEventListener {

	boolean hasSession(String sessionId);

	int removeAllSessions();

	long removeAllSessions(String sessionReleaseKey);

	boolean removeSession(String sessionId);

	int getSessionCount();

	int removeIdleSession(long idleTime);

	ISession readOnlySession(String sessionId);

	Session getOrCreateSession(String sessionId);
}
