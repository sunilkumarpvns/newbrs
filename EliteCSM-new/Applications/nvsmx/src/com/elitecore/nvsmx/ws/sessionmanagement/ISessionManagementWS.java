package com.elitecore.nvsmx.ws.sessionmanagement;

import javax.jws.WebMethod;
import javax.jws.WebParam;

import com.elitecore.nvsmx.ws.sessionmanagement.response.SessionQueryResponse;
import com.elitecore.nvsmx.ws.sessionmanagement.response.SessionReAuthResponse;

/**
 * 
 * @author Jay Trivedi
 *
 */
public interface ISessionManagementWS {

	public static final String WS_REAUTH_SESSIONS_BY_CORE_SESSION_ID 	 = "wsReauthSessionsByCoreSessionId";
	public static final String WS_REAUTH_SESSIONS_BY_SUBSCRIBER_IDENTITY = "wsReauthSessionsBySubscriberIdentity";
	public static final String WS_GET_SESSION_BY_SUBSCRIBER_IDENTITY 	 = "wsGetSessionsBySubscriberIdentity"; 
	public static final String WS_GET_SESSION_BY_IP 					 = "wsGetSessionsByIP";
	
	@WebMethod( operationName = WS_GET_SESSION_BY_SUBSCRIBER_IDENTITY )
	public SessionQueryResponse wsGetSessionsBySubscriberIdentity(
			@WebParam(name="subscriberId")String subscriberId, 
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="sessionType")String sessionType,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2
			);
	
	@WebMethod( operationName = WS_GET_SESSION_BY_IP )
	public SessionQueryResponse wsGetSessionsByIP(
			@WebParam(name="sessionIP")String sessionIP,			
			@WebParam(name="sessionType")String sessionType,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2
			);
	
	@WebMethod(operationName=WS_REAUTH_SESSIONS_BY_SUBSCRIBER_IDENTITY)
	public SessionReAuthResponse wsReauthSessionsBySubscriberIdentity(
			@WebParam(name="subscriberId")String subscriberId, 
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2
			);
	
	@WebMethod(operationName=WS_REAUTH_SESSIONS_BY_CORE_SESSION_ID)
	public SessionReAuthResponse wsReauthSessionsByCoreSessionId(
			@WebParam(name="coreSessionId")String coreSessionId,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2
			);
}
