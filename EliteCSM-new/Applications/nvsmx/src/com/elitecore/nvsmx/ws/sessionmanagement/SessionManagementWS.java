package com.elitecore.nvsmx.ws.sessionmanagement;

import static com.elitecore.commons.logging.LogManager.getLogger;

import javax.jws.WebMethod;
import javax.jws.WebParam;

import com.elitecore.corenetvertex.util.StringUtil;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.RMIGroupManager;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatisticsManager;
import com.elitecore.nvsmx.ws.sessionmanagement.blmanager.SessionWSBLManager;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionQueryByIPRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionQueryRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionReAuthByCoreSessionIdRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionReAuthBySubscriberIdRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.response.SessionQueryResponse;
import com.elitecore.nvsmx.ws.sessionmanagement.response.SessionReAuthResponse;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class SessionManagementWS implements ISessionManagementWS{

	public static final String WEB_SERVICE_NAME = SessionManagementWS.class.getSimpleName();
	private static final String MODULE = "SESS-MGMT-WS";
	private SessionWSBLManager sessionWSBLManager;
	
	public SessionManagementWS() {
		sessionWSBLManager = new SessionWSBLManager(WebServiceStatisticsManager.getInstance(),EndPointManager.getInstance(),RMIGroupManager.getInstance());
	}
	
	@Override
	@WebMethod(operationName = WS_GET_SESSION_BY_SUBSCRIBER_IDENTITY)
	public  SessionQueryResponse wsGetSessionsBySubscriberIdentity(
			@WebParam(name="subscriberId")String subscriberId, 
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="sessionType")String sessionType,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2) {
	
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsGetSessionsBySubscriberIdentity");
		}
		
		subscriberId = StringUtil.trimParameter(subscriberId);
		alternateId = StringUtil.trimParameter(alternateId);
		sessionType = StringUtil.trimParameter(sessionType);
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		
		if (getLogger().isDebugLogLevel()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Subscriber Id: ");
			stringBuilder.append(subscriberId);
			stringBuilder.append(", Alternate Id: ");
			stringBuilder.append(alternateId);
			stringBuilder.append(", Session Type: ");
			stringBuilder.append(sessionType);
			stringBuilder.append(", Parameter1: ");
			stringBuilder.append(parameter1);
			stringBuilder.append(", Parameter2: ");
			stringBuilder.append(parameter2);
			getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
		}
		return sessionWSBLManager.getSessionsBySubscriberId(new SessionQueryRequest(subscriberId, alternateId, sessionType, parameter1, parameter2, WEB_SERVICE_NAME, WS_GET_SESSION_BY_SUBSCRIBER_IDENTITY));
	}

	@Override
	@WebMethod(operationName = WS_GET_SESSION_BY_IP)
	public SessionQueryResponse wsGetSessionsByIP(
			@WebParam(name="sessionIP")String sessionIP,			
			@WebParam(name="sessionType")String sessionType,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2) {
	
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsGetSessionsByIP");
		}
		
		sessionIP = StringUtil.trimParameter(sessionIP);
		sessionType = StringUtil.trimParameter(sessionType);
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		
		if (getLogger().isDebugLogLevel()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Session IP: ");
			stringBuilder.append(sessionIP);
			stringBuilder.append(", Session Type: ");
			stringBuilder.append(sessionType);
			stringBuilder.append(", Parameter1: ");
			stringBuilder.append(parameter1);
			stringBuilder.append(", Parameter2: ");
			stringBuilder.append(parameter2);
			getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
		}
		return sessionWSBLManager.getSessionsBySessionIP(new SessionQueryByIPRequest(sessionIP, sessionType, parameter1, parameter2, WEB_SERVICE_NAME, WS_GET_SESSION_BY_IP ));
	}

	@Override
	@WebMethod(operationName = WS_REAUTH_SESSIONS_BY_SUBSCRIBER_IDENTITY)
	public SessionReAuthResponse wsReauthSessionsBySubscriberIdentity(
			@WebParam(name = "subscriberId") String subscriberId,
			@WebParam(name = "alternateId") String alternateId,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsReauthSessionsBySubscriberIdentity");
		}
		
		subscriberId = StringUtil.trimParameter(subscriberId);
		alternateId = StringUtil.trimParameter(alternateId);
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		
		if (getLogger().isDebugLogLevel()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Subscriber Id: ");
			stringBuilder.append(subscriberId);
			stringBuilder.append(", Alternate Id: ");
			stringBuilder.append(alternateId);
			stringBuilder.append(", Parameter1: ");
			stringBuilder.append(parameter1);
			stringBuilder.append(", Parameter2: ");
			stringBuilder.append(parameter2);
			getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
		}
		
		return sessionWSBLManager.reAuthSessionsBySubscriberId(new SessionReAuthBySubscriberIdRequest(subscriberId, alternateId, parameter1, parameter2, WEB_SERVICE_NAME, WS_REAUTH_SESSIONS_BY_SUBSCRIBER_IDENTITY));
	}

	@Override
	@WebMethod(operationName = WS_REAUTH_SESSIONS_BY_CORE_SESSION_ID)
	public SessionReAuthResponse wsReauthSessionsByCoreSessionId(
			@WebParam(name = "coreSessionId") String coreSessionId,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsReauthSessionsByCoreSessionId");
		}
		
		coreSessionId = StringUtil.trimParameter(coreSessionId);
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		
		if (getLogger().isDebugLogLevel()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Core Session Id: ");
			stringBuilder.append(coreSessionId);
			stringBuilder.append(", Parameter1: ");
			stringBuilder.append(parameter1);
			stringBuilder.append(", Parameter2: ");
			stringBuilder.append(parameter2);
			getLogger().debug(MODULE, "Request Parameters: " + stringBuilder.toString());
		}
		
		return sessionWSBLManager.reAuthSessionsByCoreSessionId(new SessionReAuthByCoreSessionIdRequest(coreSessionId, parameter1, parameter2, WEB_SERVICE_NAME, WS_REAUTH_SESSIONS_BY_CORE_SESSION_ID));
	}

}
