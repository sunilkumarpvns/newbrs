package com.elitecore.netvertexsm.ws.cxfws.session;

import com.elitecore.commons.base.Strings;
import com.elitecore.netvertexsm.blmanager.servermgr.sessionmgr.SessionBLManager;
import com.elitecore.netvertexsm.ws.cxfws.session.blmanager.SessionMgmtBlManager;
import com.elitecore.netvertexsm.ws.cxfws.session.response.SessionResponse;
import com.elitecore.netvertexsm.ws.logger.Logger;

/**
 * @author kirpalsinh.raj
 *
 */
public class SessionManagementWS implements ISessionManagementWS {

	private static final String MODULE = "SESSION-MANAGEMENT-WS";
    private SessionBLManager sessionBlManager;
    private SessionMgmtBlManager sessionMgmtBlManager;
	
	public SessionManagementWS() {
		sessionBlManager = new SessionBLManager();
		sessionMgmtBlManager = new SessionMgmtBlManager();
	}

	@Override
	public SessionResponse wsGetSessionByIP(String sessionIP, String sessionType) {
		Logger.logInfo(MODULE, "Fetching session for sessionIP: " + sessionIP + 
				(Strings.isNullOrBlank(sessionType) == false ? " and SessionType: " + sessionType : ""));
		SessionResponse response  = sessionBlManager.getSessionByIP(sessionIP, sessionType); 
		return response;
	}

	@Override
	
	public SessionResponse wsReAuthBySessionByIP(String sessionIP) {
     Logger.logInfo(MODULE, "calling wsReAuthBySessionByIP for session ip: " +sessionIP);
		return sessionMgmtBlManager.reAuthBySessionByIP(sessionIP);
	}

	@Override
	public SessionResponse wsReAuthBySubscriberIdentity(String subscriberIdentity) {
		 Logger.logInfo(MODULE, "calling wsReAuthBySubscriberIdentity for subscriberIdentity: " +subscriberIdentity);
		return sessionMgmtBlManager.reAuthBySubscriberIdentity(subscriberIdentity);
	}

	@Override
	public SessionResponse wsReAuthByCoreSessionId(String coreSessionId) {
		 Logger.logInfo(MODULE, "calling wsReAuthByCoreSessionId for coreSessionId: " +coreSessionId);
		 return sessionMgmtBlManager.reAuthByCoreSessionId(coreSessionId);
	}
 }