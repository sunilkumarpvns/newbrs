package com.elitecore.netvertexsm.ws.cxfws.session.blmanager;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.netvertexsm.util.remotecommunications.RemoteMethodInvocator;
import com.elitecore.netvertexsm.ws.cxfws.session.response.SessionResponse;
import com.elitecore.netvertexsm.ws.exception.ResultCodes;
import com.elitecore.netvertexsm.ws.logger.Logger;

public class SessionMgmtBlManager {

	private static final String	MODULE	= "SESS-MGMT-BL-MNGR";

	public SessionResponse reAuthBySessionByIP(String sessionIP) {
		if(Strings.isNullOrBlank(sessionIP) ==  true){
			Logger.logError(MODULE, "Invalid input parameter received. Reason: Session IP not provided");
			SessionResponse  response = new SessionResponse(ResultCodes.INVALID_INPUT_PARAMETER,"Session IP can not be null",null);
			return response;
		}

		String methodName = null;
		if(isIPv4(sessionIP)) {			
			Logger.logDebug(MODULE, "Calling reAuthBySessionByIP for sessionIPv4: " + sessionIP);
			methodName = "reAuthBySessionIPv4";
		} else {
			Logger.logDebug(MODULE, "Calling reAuthBySessionByIP for sessionIPv6: " + sessionIP);
			methodName = "reAuthBySessionIPv6";
		}
		

		Object[] parameter = { sessionIP };
		String[] parameterType = { "java.lang.String" };
		String result = (String) RemoteMethodInvocator.invokeRemoteMethod(MBeanConstants.REAUTHORIZE,methodName, parameter, parameterType);
		return getSessionReponsefromResult(result);
	}

	public SessionResponse reAuthBySubscriberIdentity(String subscriberIdentity) {
		if(Strings.isNullOrBlank(subscriberIdentity) ==  true){
			Logger.logError(MODULE, "Invalid input parameter received. Reason: subscriber Identity not provided");
			SessionResponse  response = new SessionResponse(ResultCodes.INVALID_INPUT_PARAMETER,"subscriber Identity can not be null",null);
			return response;
		}

		Logger.logDebug(MODULE, "Calling reAuthBySubscriberIdentity for subscriberID: " + subscriberIdentity);

		Object[] parameter = { subscriberIdentity };
		String[] parameterType = { "java.lang.String" };
		String result = (String) RemoteMethodInvocator.invokeRemoteMethod(MBeanConstants.REAUTHORIZE,"reAuthBySubscriberIdentity", parameter, parameterType);
		return getSessionReponsefromResult(result);

	}

	public SessionResponse reAuthByCoreSessionId(String coreSessionId) {
		if(Strings.isNullOrBlank(coreSessionId) ==  true){
			Logger.logError(MODULE, "Invalid input parameter received. Reason: coreSessionId not provided");
			SessionResponse  response = new SessionResponse(ResultCodes.INVALID_INPUT_PARAMETER,"Core session Id can not be null",null);
			return response;
		}
		
		Logger.logDebug(MODULE, "Calling reAuthByCoreSessionId for subscriberID: " + coreSessionId);
		
		Object[] parameter = { coreSessionId };
		String[] parameterType = { "java.lang.String" };
		String result = (String) RemoteMethodInvocator.invokeRemoteMethod(MBeanConstants.REAUTHORIZE,"reAuthByCoreSessionId", parameter, parameterType);
		return getSessionReponsefromResult(result);
	}

	private boolean isIPv4(String sessionIP) {
		if(sessionIP.contains(".")) {
			return true;
		} else {
			return false;
		}
	}

	private SessionResponse getSessionReponsefromResult(String result){

		if ("SUCCESS".equalsIgnoreCase(result)) {
			SessionResponse response = new SessionResponse(ResultCodes.SUCCESS, result, null);
			return response;
		}else if ("PARTIAL_SUCCESS".equalsIgnoreCase(result)) {
			SessionResponse response = new SessionResponse(ResultCodes.PARTIAL_SUCCESS, result, null);
			return response;
		} else if ("INVALID_INPUT_PARAMETER".equalsIgnoreCase(result)) {
			SessionResponse response = new SessionResponse(ResultCodes.INPUT_PARAMETER_MISSING, result, null);
			return response;
		}else if("OPERATION_NOT_SUPPORTED".equalsIgnoreCase(result)){
			SessionResponse response = new SessionResponse(ResultCodes.SERVICE_UNAVAILABLE, result, null);
			return response;
		}else if("SESSION_NOT_FOUND".equalsIgnoreCase(result)){
			SessionResponse response = new SessionResponse(ResultCodes.NO_RECORDS_FOUND, result, null);
			return response; 
		}else if("INTERNAL_ERROR".equalsIgnoreCase(result)){
			SessionResponse response = new SessionResponse(ResultCodes.INTERNAL_ERROR, result, null);
			return response; 
		}else {
			SessionResponse response = new SessionResponse(ResultCodes.FAILURE, "FAIL", null);
			return response;
		}
	}
}
