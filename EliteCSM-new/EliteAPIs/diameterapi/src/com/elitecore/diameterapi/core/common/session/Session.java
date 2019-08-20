package com.elitecore.diameterapi.core.common.session;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

public abstract class Session implements ISession {
	public static final String SESSION_RELEASE_KEY = "SESSION_RELEASE_KEY";
	private String sessionId;
	private long creationTime;
	private final SessionEventListener sessionEventListener;
	private final ConcurrentHashMap<String, Object> parameters;
	private long lastAccessedTime;
	private volatile SessionState state;
	private AppSession appSession;
	private TimeSource timeSource;

	public Session(String strSessionId, SessionEventListener sessionEventListener) {
		this(strSessionId, sessionEventListener, TimeSource.systemTimeSource());
	}
	
	public Session(String strSessionId, SessionEventListener sessionEventListener, TimeSource timeSource) {
		this.sessionId = strSessionId;
		this.timeSource = timeSource;
		this.sessionEventListener = sessionEventListener;
		this.creationTime = timeSource.currentTimeInMillis();
		this.lastAccessedTime = creationTime;	
		this.parameters = new ConcurrentHashMap<String, Object>(8, CommonConstants.DEFAULT_LOAD_FACTOR, 4);
		this.state = SessionState.Ok; 
	}

	public String getSessionId() {
		return sessionId;
	}
	
	protected void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public long getCreationTime() {
		return this.creationTime;
	}
	
	protected void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	/**
	 * Storing or fetching any data/parameter from the session causes the access time to be refreshed.
	 */
	public long getLastAccessedTime() {
		return this.lastAccessedTime;
	}
	
	protected void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}
	
	protected SessionState getSessionState() {
		return this.state;
	}
	
	protected void setSessionState(SessionState sessionState) {
		this.state = sessionState;
	}

	protected boolean isReleased() {
		return state == SessionState.Released;
	}
	
	@Override
	public void release() {
		if(state == SessionState.Released){
			if (getLogger().isLogLevel(LogLevel.INFO))
				getLogger().info("SESSION", "Can not remove Session: " + sessionId + " as session is already removed");
			return;
		}
		
		this.state = SessionState.Released;

		sessionEventListener.removeSession(this);

		if (getLogger().isLogLevel(LogLevel.INFO))
			getLogger().info("SESSION", "Session: " + sessionId + " created on: "
					+new Timestamp(creationTime) +" and last updated on: "+new Timestamp(lastAccessedTime)+" is removed");
		
	}

	public void addAppSession(AppSession appSession) {
		touch();
		this.appSession = appSession;
	}
	
	public AppSession getAppSession() {
		touch();
		return appSession;
	}
	
	public Object setParameter(String key, Object parameterValue){
		touch();
		return parameters.put(key, parameterValue);
	}
	
	public Object getParameter(String key){
		touch();
		return parameters.get(key);
	}

	@Override
	public Object removeParameter(String key){
		touch();
		return parameters.remove(key);
	}
	
	private void touch() {
		lastAccessedTime = timeSource.currentTimeInMillis(); 
	}
	
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Session Id: " + sessionId);
		out.println("Session create-time: " + creationTime);
		out.println("Session Last accessed time: " + lastAccessedTime);
		out.close();
		return stringBuffer.toString();
	}
	
	protected Map<String,Object> getParameters() {
		return this.parameters;
	}
	
	@Override
	public void update(ValueProvider noValueProvider) {
		if (isReleased() == false) {
			sessionEventListener.update(this);
		}
	}
}
