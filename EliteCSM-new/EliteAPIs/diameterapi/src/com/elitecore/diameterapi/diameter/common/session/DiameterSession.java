package com.elitecore.diameterapi.diameter.common.session;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionEventListener;

/**
 * 
 * @author harsh.patel
 * @author narendra.pathai
 */
public class DiameterSession extends Session {

	public DiameterSession(String sessionId, SessionEventListener eventListener) {
		this(sessionId, eventListener, TimeSource.systemTimeSource());
	}
	
	public DiameterSession(String sessionId, SessionEventListener eventListener, TimeSource timeSource) {
		super(sessionId, eventListener, timeSource);
	}
}