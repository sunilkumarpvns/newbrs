package com.elitecore.nvsmx.ws.sessionmanagement.blmanager;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.session.SessionInformation;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.policydesigner.model.subscriber.SubscriberDAO;
import com.elitecore.nvsmx.remotecommunications.*;
import com.elitecore.nvsmx.remotecommunications.data.HTTPMethodType;
import com.elitecore.nvsmx.remotecommunications.exception.CommunicationException;
import com.elitecore.nvsmx.system.groovy.GroovyManager;
import com.elitecore.nvsmx.system.groovy.SessionManagementWsScript;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextWebServiceInterceptor;
import com.elitecore.nvsmx.ws.interceptor.WebServiceInterceptor;
import com.elitecore.nvsmx.ws.interceptor.WebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatisticsManager;
import com.elitecore.nvsmx.ws.sessionmanagement.data.SessionData;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionQueryByIPRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionQueryRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionReAuthByCoreSessionIdRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionReAuthBySubscriberIdRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.response.SessionQueryResponse;
import com.elitecore.nvsmx.ws.sessionmanagement.response.SessionReAuthResponse;
import com.elitecore.nvsmx.ws.util.ReAuthUtil;
import com.elitecore.nvsmx.ws.util.UpdateActions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class SessionWSBLManager {

	private static final String MODULE = "SESS-WSBL-MNGR";
	private final EndPointManager endPointManager;
	private final RMIGroupManager rmiGroupManager;


	private StaffData adminStaff;
	private List<WebServiceInterceptor> interceptors = new ArrayList<WebServiceInterceptor>();



	public SessionWSBLManager(WebServiceStatisticsManager webServiceStatisticsManager,
							  EndPointManager endPointManager,
							  RMIGroupManager rmiGroupManager) {
		this.endPointManager = endPointManager;
		this.rmiGroupManager = rmiGroupManager;
		adminStaff = new StaffData();
		adminStaff.setUserName("admin");
		interceptors.add(webServiceStatisticsManager);
		interceptors.add(DiagnosticContextWebServiceInterceptor.getInstance());
	}


	public SessionQueryResponse getSessionsBySubscriberId(SessionQueryRequest request) {

		applyRequestInterceptors(request);
		
		List<SessionManagementWsScript> groovyScripts = GroovyManager.getInstance().getSessionManagementWsScripts();
		for (SessionManagementWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preListSessionsBySubscriberId(request);
			} catch (Exception e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		SessionQueryResponse response = doGetSessionsBySubscriberId(request);
		
		for (SessionManagementWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postListSessionsByIP(request, response);
			} catch (Exception e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		applyResponseInterceptors(response);
		return response;
	}

	private SessionQueryResponse doGetSessionsBySubscriberId(SessionQueryRequest request) {
		String subscriberId = request.getSubscriberId();
		
		if (Strings.isNullOrBlank(subscriberId)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE,"SubscriberId not received");
			}
			
			String alternateId = request.getAlternateId();
			
			if (Strings.isNullOrBlank(alternateId)) {
				getLogger().error(MODULE, "Unable to fetch core sessions. Reason: Identity parameter missing");
				return new SessionQueryResponse(ResultCode.INPUT_PARAMETER_MISSING.code, 
						ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
			
			try {
				subscriberId = SubscriberDAO.getInstance().getSubscriberIdByAlternateId(alternateId, adminStaff);
			} catch (OperationFailedException e) {
				getLogger().error(MODULE, "Unable to fetch sessions by alternateId:" + alternateId + ". Reason: " + e.getMessage());
				if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
				}
				return new SessionQueryResponse(e.getErrorCode().code, 
						e.getErrorCode().name + ". Reason: " +  e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			} 
			
			if (Strings.isNullOrBlank(subscriberId)) {
				getLogger().error(MODULE, "Unable to fetch sessions by alternateId:" + alternateId + ". Reason: SubscriberId not found with alternate Id: " + alternateId);
				return new SessionQueryResponse(ResultCode.NOT_FOUND.code, 
						ResultCode.NOT_FOUND.name + ". Reason: SubscriberId not found with alternate Id: " + alternateId, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		}
		
		List<com.elitecore.nvsmx.ws.sessionmanagement.data.SessionData> sessionDatas;
		try {

			RemoteMethod remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
					RemoteMethodConstant.SESSIONS_BY_SUBSCRIBER_IDENTITY_FROM_CACHE,
					subscriberId, HTTPMethodType.GET);
			BroadCastCompletionResult<Collection<SessionInformation>> broadcast = RemoteMessageCommunicator.broadcast(endPointManager.getAllNetvertexEndPoint(), remoteMethod);


			Collection<RMIResponse<Collection<SessionInformation>>> broadCastResult = broadcast.filter(RMIResponsePredicates.NOT_NULL_AND_NOT_EMPTY_RMI_RESPONSE).getAll(3, TimeUnit.SECONDS);

			final String sessionType = request.getSessionType();

			SessionTypePredicate sessionTypePredicate = SessionTypePredicate.create(sessionType);

			SessionInformationResultAccumulator sessionSessionInformationResultAccumulator = new SessionInformationResultAccumulator(sessionTypePredicate);
			sessionSessionInformationResultAccumulator.accumulate(broadCastResult);
			Collection<SessionInformation> sessions = sessionSessionInformationResultAccumulator.getSessionInformations();

			if (Collectionz.isNullOrEmpty(sessions)) {
				remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
						RemoteMethodConstant.SESSIONS_BY_SUBSCRIBER_IDENTITY,
						subscriberId, HTTPMethodType.GET);
				RMIResponse<Collection<SessionInformation>> rmiResponse = RemoteMessageCommunicator.callSync(rmiGroupManager.getNetvertexInstanceRMIGroups(), remoteMethod, RMIResponsePredicates.NOT_NULL_AND_NOT_EMPTY_RMI_RESPONSE);
				if(rmiResponse !=null ){
					if (rmiResponse.isErrorOccurred()) {
						throw rmiResponse.getError();
					} else {
						sessionSessionInformationResultAccumulator = new SessionInformationResultAccumulator(sessionTypePredicate);
						sessionSessionInformationResultAccumulator.accumulate(rmiResponse);
						sessions = sessionSessionInformationResultAccumulator.getSessionInformations();
					}
				}
			}

			if (Collectionz.isNullOrEmpty(sessions)) {
				  
				if(getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "No session found with subscriberId: " + subscriberId);
				}
				return new SessionQueryResponse(ResultCode.NOT_FOUND.code, ResultCode.NOT_FOUND.name 
						+ ". Reason: No session found with subscriberId: " + subscriberId, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}

			sessionDatas = createWSSpecificSessionData(sessions, subscriberId);
		} catch (CommunicationException e) {
			getLogger().error(MODULE, "Error while fetching core sessions for subscriber(" + subscriberId + "). Reason:" + e.getMessage());
			getLogger().trace(MODULE, e);
			return new SessionQueryResponse(404, "NOT FOUND" + ". Reason: " + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while fetching core sessions for subscriber(" + subscriberId + "). Reason:" + e.getMessage());
			getLogger().trace(MODULE, e);
			return new SessionQueryResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name
					 + ". Reason: " + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		
		return new SessionQueryResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, sessionDatas, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
	}

	private ArrayList<SessionData> createWSSpecificSessionData(Collection<SessionInformation> sessionDatas, String sessionIPOrSubscriberId) {

		ArrayList<com.elitecore.nvsmx.ws.sessionmanagement.data.SessionData> wsSessionDatas = new ArrayList<com.elitecore.nvsmx.ws.sessionmanagement.data.SessionData>();

		for (SessionInformation sessionData : sessionDatas) {
			wsSessionDatas.add(SessionData.from(sessionData, sessionIPOrSubscriberId));
		}

		return wsSessionDatas;
	}

	public SessionQueryResponse getSessionsBySessionIP(SessionQueryByIPRequest request) {

		applyRequestInterceptors(request);
		List<SessionManagementWsScript> groovyScripts = GroovyManager.getInstance().getSessionManagementWsScripts();
		for (SessionManagementWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preListSessionsByIP(request);
			} catch (Exception e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		SessionQueryResponse response = doGetSessionsByIP(request);
		
		for (SessionManagementWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postListSessionsByIP(request, response);
			} catch (Exception e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}
		
		applyResponseInterceptors(response);
		return response;
	}


	private static boolean isIPv4(String sessionIP) {
		return sessionIP.contains(".");
	}

	private SessionQueryResponse doGetSessionsByIP(SessionQueryByIPRequest request) {
		final String sessionIP = request.getSessionIP();
		if (Strings.isNullOrBlank(sessionIP)) {
			getLogger().error(MODULE, "Unable to fetch core sessions with sessionIP: " + sessionIP + ". Reason: SessionIP parameter missing");
			
			return new SessionQueryResponse(ResultCode.INPUT_PARAMETER_MISSING.code, 
					ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: SessionIP parameter missing", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}

		List<com.elitecore.nvsmx.ws.sessionmanagement.data.SessionData> sessionDatas;
		try {

			RemoteMethod remoteMethod = null;
			boolean isIPv4 = isIPv4(sessionIP);
			if (isIPv4) {
				remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
						RemoteMethodConstant.SESSIONS_BY_SESSION_IPV4_FROM_CACHE,
						request.getSessionIP(), HTTPMethodType.GET);
			} else {
				remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
						RemoteMethodConstant.SESSIONS_BY_SESSION_IPV6_FROM_CACHE,
						request.getSessionIP(), HTTPMethodType.GET);
			}

			BroadCastCompletionResult<Collection<SessionInformation>> broadcast = RemoteMessageCommunicator.broadcast(endPointManager.getAllNetvertexEndPoint(), remoteMethod);


			Collection<RMIResponse<Collection<SessionInformation>>> broadCastResult = broadcast.filter(RMIResponsePredicates.NOT_NULL_AND_NOT_EMPTY_RMI_RESPONSE).getAll(3, TimeUnit.SECONDS);

			final String sessionType = request.getSessionType();
			SessionTypePredicate sessionTypePredicate = SessionTypePredicate.create(sessionType);

			SessionInformationResultAccumulator sessionSessionInformationResultAccumulator = new SessionInformationResultAccumulator(sessionTypePredicate);
			sessionSessionInformationResultAccumulator.accumulate(broadCastResult);
			Collection<SessionInformation> sessions = sessionSessionInformationResultAccumulator.getSessionInformations();


			if (Collectionz.isNullOrEmpty(sessions)) {
				if(isIPv4){
					remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
							RemoteMethodConstant.SESSIONS_BY_SESSION_IPV4,
							sessionIP, HTTPMethodType.GET);
				}else{
					remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
							RemoteMethodConstant.SESSIONS_BY_SESSION_IPV6,
							sessionIP, HTTPMethodType.GET);
				}
				RMIResponse<Collection<SessionInformation>> objectRMIResponse = RemoteMessageCommunicator.callSync(rmiGroupManager.getNetvertexInstanceRMIGroups(), remoteMethod, RMIResponsePredicates.NOT_NULL_AND_NOT_EMPTY_RMI_RESPONSE);
				if(objectRMIResponse != null) {
					if (objectRMIResponse.isErrorOccurred()) {
						throw objectRMIResponse.getError();
					} else {
						sessionSessionInformationResultAccumulator = new SessionInformationResultAccumulator(sessionTypePredicate);
						sessionSessionInformationResultAccumulator.accumulate(objectRMIResponse);
						sessions = sessionSessionInformationResultAccumulator.getSessionInformations();
					}
				}
			}


			if (Collectionz.isNullOrEmpty(sessions) == true) {

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "No session found with sessionIP: " + sessionIP);
				}
				return new SessionQueryResponse(ResultCode.NOT_FOUND.code, ResultCode.NOT_FOUND.name
						+ ". Reason: No session found with sessionIP: " + sessionIP, null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}

			if (Strings.isNullOrBlank(sessionType) == false && Collectionz.isNullOrEmpty(sessions) == true) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "No session found with sessionIP: " + sessionIP + ", and session type: " + request.getSessionType());
				}
				return new SessionQueryResponse(ResultCode.NOT_FOUND.code, ResultCode.NOT_FOUND.name
						+ ". Reason: No session found with sessionIP: " + sessionIP + ", and session type: " + request.getSessionType(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());

			}


			sessionDatas = createWSSpecificSessionData(sessions, sessionIP);
		} catch (CommunicationException e) {
			getLogger().error(MODULE, "Error while fetching core sessions for sessionIP(" + sessionIP + "). Reason:" + e.getMessage());
			getLogger().trace(MODULE, e);
			return new SessionQueryResponse(404, "NOT FOUND" + ". Reason: " + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while fetching core sessions for sessionIP(" + sessionIP + "). Reason:" + e.getMessage());
			getLogger().trace(MODULE, e);
			return new SessionQueryResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name
					 + ". Reason: " + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		
		return new SessionQueryResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, sessionDatas, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
	}

	public SessionReAuthResponse reAuthSessionsBySubscriberId(
			SessionReAuthBySubscriberIdRequest request) {

		applyRequestInterceptors(request);
		List<SessionManagementWsScript> groovyScripts = GroovyManager.getInstance().getSessionManagementWsScripts();
		for (SessionManagementWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preReAuthBySubscriberId(request);
			} catch (Exception e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		SessionReAuthResponse response = doReAuthSessionsBySubscriberId(request);
		
		for (SessionManagementWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postReAuthBySubscriberId(request, response);
			} catch (Exception e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private SessionReAuthResponse doReAuthSessionsBySubscriberId(
			SessionReAuthBySubscriberIdRequest request) {
		String subscriberId = request.getSubscriberId();
		
		if (Strings.isNullOrBlank(subscriberId)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE,"SubscriberId not received");
			}
			
			String alternateId = request.getAlternateId();
			
			if (Strings.isNullOrBlank(alternateId)) {
				getLogger().error(MODULE, "Unable to re-auth session(s). Reason: Identity parameter missing");
				return new SessionReAuthResponse(ResultCode.INPUT_PARAMETER_MISSING.code, 
						ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Identity parameter missing", null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
			
			try {
				subscriberId = SubscriberDAO.getInstance().getSubscriberIdByAlternateId(alternateId, adminStaff);
			} catch (OperationFailedException e) {
				getLogger().error(MODULE, "Unable to fetch sessions by alternateId:" + alternateId + ". Reason: " + e.getMessage());
				if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
				}
				return new SessionReAuthResponse(e.getErrorCode().code, 
						e.getErrorCode().name + ". Reason: " +  e.getMessage(), null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			} 
			
			if (Strings.isNullOrBlank(subscriberId)) {
				getLogger().error(MODULE, "Unable to re-auth session(s) by alternateId:" + alternateId + ". Reason: SubscriberId not found with alternate Id: " + alternateId);
				return new SessionReAuthResponse(ResultCode.NOT_FOUND.code, 
						ResultCode.NOT_FOUND.name + ". Reason: Subscriber not found", null, null, request.getWebServiceName(), request.getWebServiceMethodName());
			}
		} else {
			subscriberId = SubscriberDAO.getInstance().getStrippedSubscriberIdentity(subscriberId);
		}
		
		try {
			ReAuthUtil.doReAuthBySubscriberId(subscriberId, UpdateActions.RE_AUTH_SESSION.val());
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Unable to re-auth session(s) by subscriber Id:" + subscriberId + ". Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
			getLogger().trace(MODULE, e);
			}
			return new SessionReAuthResponse(e.getErrorCode().code, 
					e.getErrorCode().name + ". Unable to re-auth session(s) by subscriber Id:" + subscriberId + ". Reason: " + e.getMessage()
					, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		
		return new SessionReAuthResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
	}

	public SessionReAuthResponse reAuthSessionsByCoreSessionId(
			SessionReAuthByCoreSessionIdRequest request) {
		
		applyRequestInterceptors(request);
		List<SessionManagementWsScript> groovyScripts = GroovyManager.getInstance().getSessionManagementWsScripts();
		for (SessionManagementWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preReAuthByCoreSessionId(request);
			} catch (Exception e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		SessionReAuthResponse response = doReAuthSessionsByCoreSessionId(request);
		
		for (SessionManagementWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postReAuthByCoreSessionId(request, response);
			} catch (Exception e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;
	}

	private SessionReAuthResponse doReAuthSessionsByCoreSessionId(
			SessionReAuthByCoreSessionIdRequest request) {
		
		String coreSessionId = request.getCoreSessionId();
		
		if (Strings.isNullOrBlank(coreSessionId)) {
			
			getLogger().debug(MODULE,"Unable to perform re-auth. Reason: Core session id not received");
			return new SessionReAuthResponse(ResultCode.INPUT_PARAMETER_MISSING.code, 
					ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Core session id not received", null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
			
		try {
			ReAuthUtil.doReAuthByCoreSessionId(coreSessionId);
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Unable to re-auth session(s) by core session Id:" + coreSessionId + ". Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
			getLogger().trace(MODULE, e);
			}
			return new SessionReAuthResponse(e.getErrorCode().code, 
					e.getErrorCode().name + ". Unable to re-auth session(s) by core session Id:" + coreSessionId + ". Reason: " + e.getMessage()
					, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
		
		return new SessionReAuthResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
	}


	private void applyRequestInterceptors(WebServiceRequest request) {
		for(WebServiceInterceptor interceptor : interceptors){
			interceptor.requestReceived(request);
		}
	}
	
	private void applyResponseInterceptors(WebServiceResponse response) {
		for(WebServiceInterceptor interceptor : interceptors){
			interceptor.responseReceived(response);
		}
	}
}
