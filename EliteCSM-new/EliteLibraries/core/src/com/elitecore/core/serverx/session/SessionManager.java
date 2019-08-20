package com.elitecore.core.serverx.session;

import java.util.List;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.session.data.UserSession;

public abstract class SessionManager {
	
	
	public void init() throws InitializationFailedException {
		
	}
	
	public abstract UserSession locateUserSessionBySessionId(String sessionId) ;
	
	/**
	 * 
	 * @param sessionId
	 * @return
	 */
	public abstract UserSession locateLatestUserSessionByUserId(String sessionId);
	
	public abstract List<UserSession> locateAllUserSessionByUserId(String sessionId);
	
	
	public abstract void createUserSession(UserSession userSession) ;

	public abstract void updateUserSession(UserSession userSession) ;
	
	public abstract void removeUserSession(UserSession userSession) ;
	
}
