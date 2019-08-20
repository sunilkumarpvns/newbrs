package com.elitecore.diameterapi.diameter.common.session;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.diameterapi.core.common.session.AppSession;
import com.elitecore.diameterapi.core.common.session.SessionEventListener;
import com.elitecore.diameterapi.core.common.session.SessionState;

public class NullDiameterSession extends DiameterSession {


	private static final String NULL_DIAMETER_SESSION = "Null Diameter Session";

	public NullDiameterSession(String sessionId, SessionEventListener eventListener, TimeSource timeSource) {
		super(sessionId, eventListener, timeSource);
	}


	@Override
	public long getCreationTime() {
		return 0l;
	}

	@Override
	protected void setCreationTime(long creationTime) {
		// no-operation
	}

	@Override
	public long getLastAccessedTime() {
		return 0l;
	}

	@Override
	protected void setLastAccessedTime(long lastAccessedTime) {
		// no-operation
	}

	@Override
	protected SessionState getSessionState() {
		return SessionState.Ok;
	}

	@Override
	protected void setSessionState(SessionState sessionState) {
		// no-operation
	}

	@Override
	protected boolean isReleased() {
		return false;
	}

	@Override
	public void release() {
		// no-operation
	}

	@Override
	public void addAppSession(AppSession appSession) {
		// no-operation
	}

	@Override
	public AppSession getAppSession() {
		return null;
	}

	@Override
	public Object setParameter(String key, Object parameterValue) {
		return null;
	}

	@Override
	public Object getParameter(String key) {
		return null;
	}

	@Override
	public Object removeParameter(String key) {
		return null;
	}

	@Override
	public String toString() {
		return NULL_DIAMETER_SESSION;
	}

	@Override
	protected Map<String, Object> getParameters() {
		return new HashMap<>();
	}
}
