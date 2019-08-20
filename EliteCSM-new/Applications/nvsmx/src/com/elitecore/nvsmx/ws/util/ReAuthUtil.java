package com.elitecore.nvsmx.ws.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.JMXConstant;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.session.SessionInformation;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.SessionReAuthUtil;
import com.elitecore.nvsmx.policydesigner.model.subscriber.SubscriberDAO;
import com.elitecore.nvsmx.remotecommunications.BroadCastCompletionResult;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.RMIGroupManager;
import com.elitecore.nvsmx.remotecommunications.RMIResponse;
import com.elitecore.nvsmx.remotecommunications.RMIResponsePredicates;
import com.elitecore.nvsmx.remotecommunications.RemoteMessageCommunicator;
import com.elitecore.nvsmx.remotecommunications.RemoteMethod;
import com.elitecore.nvsmx.remotecommunications.RemoteMethodConstant;
import com.elitecore.nvsmx.remotecommunications.SessionInformationResultAccumulator;
import com.elitecore.nvsmx.remotecommunications.data.HTTPMethodType;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.ws.sessionmanagement.blmanager.SessionUtil;
import org.apache.commons.lang.StringUtils;


import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.nvsmx.ws.util.SessionInformationComparator.getSessionInformationComparatorBasedOnCreationTime;

/**
 * @author Jay Trivedi
 */

public class ReAuthUtil {

    private ReAuthUtil(){}
    private static final String MODULE = "REAUTH";
    private static final Predicate<RMIResponse<String>> SUCCESS_FAIL_RESULT_PREDICATE = input -> {
        if (input.isErrorOccurred()) {
            return false;
        }
        String response = input.getResponse();
        return JMXConstant.SUCCESS.equalsIgnoreCase(response) || JMXConstant.PARTIAL_SUCCESS.equalsIgnoreCase(response) || JMXConstant.OPERATION_NOT_SUPPORTED.equalsIgnoreCase(response);
    };
    private static final Predicate<SessionInformation> RE_AUTHORIZABLE_SESSION_TYPE_PREDICATE = sessionInformation -> {

            if (sessionInformation == null) {
                return false;
            }

            return (SessionTypeConstant.GX.val.equals(sessionInformation.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()))
					|| SessionTypeConstant.CISCO_GX.val.equals(sessionInformation.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()))
					|| SessionTypeConstant.RADIUS.val.equals(sessionInformation.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()))
					|| SessionTypeConstant.GY.val.equals(sessionInformation.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal())));
    };
    public static final String FOR_ID = " for Id: ";
    public static final String FAILED_FOR_ID = " failed for Id: ";


    public static void doReAuthBySubscriberId(String subscriberId, Integer updateAction) throws OperationFailedException {

        if (LogManager.getLogger().isInfoLogLevel()) {
            LogManager.getLogger().info(MODULE, "Received value for 'updateAction' is  " + (updateAction == null ? "invalid" : updateAction));
        }

        UpdateActions updtActn = isReauthRequired(updateAction);
        subscriberId = SubscriberDAO.getInstance().getStrippedSubscriberIdentity(subscriberId);
        if (LogManager.getLogger().isInfoLogLevel()) {
            LogManager.getLogger().info(MODULE, "Performing " + updtActn.label() + " for subscriberId: " + subscriberId);
        }

        if (updtActn == UpdateActions.NO_ACTION) {
            return;
        }

        RemoteMethod remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
                RemoteMethodConstant.SESSIONS_BY_SUBSCRIBER_IDENTITY_FROM_CACHE,
                subscriberId, HTTPMethodType.GET);
        BroadCastCompletionResult<Collection<SessionInformation>> broadcast = RemoteMessageCommunicator.broadcast(EndPointManager.getInstance().getAllNetvertexEndPoint(), remoteMethod);

        Collection<RMIResponse<Collection<SessionInformation>>> broadCastResult = broadcast.filter(RMIResponsePredicates.NOT_NULL_AND_NOT_EMPTY_RMI_RESPONSE).getAll(3, TimeUnit.SECONDS);

        SessionInformationResultAccumulator broadCastSessionInformationResultAccumulator = new SessionInformationResultAccumulator(RE_AUTHORIZABLE_SESSION_TYPE_PREDICATE);
        broadCastSessionInformationResultAccumulator.accumulate(broadCastResult);
        Map<SessionInformation, RMIResponse<Collection<SessionInformation>>> sessionInformationToRMIResponse = broadCastSessionInformationResultAccumulator.getSessionInformationToRMIResponse();
        correlateSessions(sessionInformationToRMIResponse);
        RMIResponse<String> response = null;
        if (updtActn == UpdateActions.RE_AUTH_SESSION) {
            response = performReauthSessionAndGetRMIResponse(subscriberId, sessionInformationToRMIResponse, response);
        } else if (updtActn == UpdateActions.DISCONNECT_SESSION) {
            response = performDisconnectSessionAndGetRMIResponse(subscriberId, sessionInformationToRMIResponse, response);
        } else {
            throw new OperationFailedException("Update action:" + updateAction + " not supported", ResultCode.OPERATION_NOT_SUPPORTED);
        }

        String result = JMXConstant.SESSION_NOT_FOUND;
        if (response != null) {
            result = response.getResponse();
        }

        processResult(subscriberId, result, updtActn.label());
    }

	private static void correlateSessions(Map<SessionInformation, RMIResponse<Collection<SessionInformation>>> sessionInformationToRMIResponse) {
		Set<String> pcrfSessions = new HashSet<>(8);
		Set<String> ocsSessions = new HashSet<>(8);

		Set<Entry<SessionInformation, RMIResponse<Collection<SessionInformation>>>> sessions = sessionInformationToRMIResponse.entrySet();
		for (Entry<SessionInformation, RMIResponse<Collection<SessionInformation>>> session : sessions) {
			SessionInformation sessionInformation = session.getKey();
			SessionTypeConstant sessionTypeConstant = SessionTypeConstant.fromValue(sessionInformation.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()));
			String ipAddress = getIpAddress(sessionInformation);

			if (SessionReAuthUtil.isPCRFSession(sessionTypeConstant)) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Adding session: " + sessionInformation.getValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal())
							+ " in PCRF re-auth list");
				}

				pcrfSessions.add(ipAddress);

				/* When IP Address is not there in session, do not correlate sessions.
				 * ReAuth will be generated for both sessions.
				 */
				if (ipAddress == null) {
					continue;
				}

				if (ocsSessions.contains(ipAddress)) {
					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Removing session: " + sessionInformation.getValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal())
								+ " from OCS re-auth list. Reason: PCRF Session found for same IP Address: " + ipAddress);
					}
					ocsSessions.remove(ipAddress);
					sessionInformationToRMIResponse.remove(sessionInformation);
				}
			} else if (SessionReAuthUtil.isOCSSession(sessionTypeConstant)) {

				if (ipAddress == null || pcrfSessions.contains(ipAddress) == false) {
					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Adding session: " + sessionInformation.getValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal())
								+ " in OCS re-auth list");
					}
					ocsSessions.add(ipAddress);
				} else {
					sessionInformationToRMIResponse.remove(sessionInformation);
				}
			}
		}
	}

	private static String getIpAddress(SessionInformation sessionInformation) {
		String ipAddress = sessionInformation.getValue(PCRFKeyConstants.CS_SESSION_IPV4.val);
		if (StringUtils.isEmpty(ipAddress)) {
			ipAddress = sessionInformation.getValue(PCRFKeyConstants.CS_SESSION_IPV6.val);
		}
		return ipAddress;
	}

	private static RMIResponse<String> performDisconnectSessionAndGetRMIResponse(String subscriberId, Map<SessionInformation, RMIResponse<Collection<SessionInformation>>> sessionInformationToRMIResponse, RMIResponse<String> response) {
        RMIResponse<String> responseLocal = response;
        if (Maps.isNullOrEmpty(sessionInformationToRMIResponse)) {
            responseLocal = RemoteMessageCommunicator.callSync(RMIGroupManager.getInstance().getNetvertexInstanceRMIGroups(), new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH, RemoteMethodConstant.SESSIONS_DISCONNECT_BY_CORE_SUBSCRIBER_IDENTITY, subscriberId, HTTPMethodType.GET), SUCCESS_FAIL_RESULT_PREDICATE);
        } else {
            for (Map.Entry<SessionInformation, RMIResponse<Collection<SessionInformation>>> sessionData : sessionInformationToRMIResponse.entrySet()) {
                responseLocal =  RemoteMessageCommunicator.callSync(new RemoteMethod(
                                RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
                                RemoteMethodConstant.SESSIONS_DISCONNECT_BY_CORE_SESSION_ID,
                                sessionData.getKey().getValue(PCRFKeyConstants.CS_CORESESSION_ID.val),
                                HTTPMethodType.GET),
                        RMIGroupManager.getInstance().getRMIGroupFromServerCode(sessionData.getValue().getInstanceData().getId()), sessionData.getValue().getInstanceData().getId());
            }
        }
        return responseLocal;
    }

    private static RMIResponse<String> performReauthSessionAndGetRMIResponse(String subscriberId, Map<SessionInformation, RMIResponse<Collection<SessionInformation>>> sessionInformationToRMIResponse, RMIResponse<String> response) {
        RMIResponse<String> responseLocal = response;
        if (Maps.isNullOrEmpty(sessionInformationToRMIResponse)) {
            responseLocal = RemoteMessageCommunicator.callSync(RMIGroupManager.getInstance().getNetvertexInstanceRMIGroups(), new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH, RemoteMethodConstant.RE_AUTH_BY_SUBSCRIBER_IDENTITY, subscriberId, HTTPMethodType.GET), SUCCESS_FAIL_RESULT_PREDICATE);
        } else {
            for (Map.Entry<SessionInformation, RMIResponse<Collection<SessionInformation>>> sessionData : sessionInformationToRMIResponse.entrySet()) {
                responseLocal = RemoteMessageCommunicator.callSync(new RemoteMethod(
                                RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
                                RemoteMethodConstant.RE_AUTH_BY_CORE_SESSION_ID,
                                sessionData.getKey().getValue(PCRFKeyConstants.CS_CORESESSION_ID.val),
                                HTTPMethodType.GET),
                        RMIGroupManager.getInstance().getRMIGroupFromServerCode(sessionData.getValue().getInstanceData().getId()), sessionData.getValue().getInstanceData().getId());
            }
        }
        return responseLocal;
    }


    public static UpdateActions isReauthRequired(Integer updateAction) {

        if (UpdateActions.fromValue(updateAction) == null) {
            UpdateActions sysParamUpdateAction = SystemParameterDAO.getAutoReauthEnable();
            if (LogManager.getLogger().isWarnLogLevel()) {
                LogManager.getLogger().warn(MODULE, "Invalid value received for WS 'updateAction' Parameter. Taking system Parameter 'Update Action for Ws' value: " + sysParamUpdateAction);
            }
            return sysParamUpdateAction;
        }
        return UpdateActions.fromValue(updateAction);
    }

    public static void doReAuthByCoreSessionId(String coreSessionId) throws OperationFailedException {

        RMIResponse<String> response;
        RemoteMethod remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
                RemoteMethodConstant.SESSIONS_BY_CORE_SESSION_ID_FROM_CACHE,
                coreSessionId, HTTPMethodType.GET);
        BroadCastCompletionResult<SessionInformation> broadcast = RemoteMessageCommunicator.broadcast(EndPointManager.getInstance().getAllNetvertexEndPoint(), remoteMethod);

        RMIResponse<SessionInformation> sessionDataResponse = broadcast.filter(SessionUtil.RMI_RESPONSE_ERROR_OR_NULL_PREDICATE).sort(getSessionInformationComparatorBasedOnCreationTime()).getFirst(3, TimeUnit.SECONDS);

        if (sessionDataResponse != null) {
            response = RemoteMessageCommunicator.callSync(new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH, RemoteMethodConstant.RE_AUTH_BY_CORE_SESSION_ID, coreSessionId, HTTPMethodType.GET),
                    RMIGroupManager.getInstance().getRMIGroupFromServerCode(sessionDataResponse.getInstanceData().getId()), sessionDataResponse.getInstanceData().getId());
        } else {
            response = RemoteMessageCommunicator.callSync(RMIGroupManager.getInstance().getNetvertexInstanceRMIGroups(), new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH, RemoteMethodConstant.RE_AUTH_BY_CORE_SESSION_ID, coreSessionId, HTTPMethodType.GET), SUCCESS_FAIL_RESULT_PREDICATE);
        }

        String result = JMXConstant.SESSION_NOT_FOUND;
        if (response != null) {
            result = response.getResponse();
        }
        processResult(coreSessionId, result, UpdateActions.RE_AUTH_SESSION.label());
    }

    public static void doReAuthByCoreSessionId(String coreSessionId,String instanceId) throws  OperationFailedException{

        RMIResponse<String> response = RemoteMessageCommunicator.callSync(RMIGroupManager.getInstance().getNetvertexInstanceRMIGroups(), new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH, RemoteMethodConstant.RE_AUTH_BY_CORE_SESSION_ID, coreSessionId, HTTPMethodType.GET), SUCCESS_FAIL_RESULT_PREDICATE);


        String result = JMXConstant.SESSION_NOT_FOUND;
        if (response != null) {
            result = response.getResponse();
        }
        processResult(coreSessionId, result, UpdateActions.RE_AUTH_SESSION.label());
    }

    private static void processResult(String coreSessionOrSubscriberId, String result, String updateActionLabel) throws OperationFailedException {
        if (JMXConstant.SUCCESS.equalsIgnoreCase(result)) {
            if (LogManager.getLogger().isInfoLogLevel()) {
                LogManager.getLogger().info(MODULE, updateActionLabel + " performed successfully for Id: " + coreSessionOrSubscriberId);
            }
        } else if (JMXConstant.PARTIAL_SUCCESS.equalsIgnoreCase(result)) {
            if (LogManager.getLogger().isInfoLogLevel()) {
                LogManager.getLogger().info(MODULE, "Partial success while performing " + updateActionLabel + FOR_ID + coreSessionOrSubscriberId);
            }
            throw new OperationFailedException("Partial success while performing " + updateActionLabel + FOR_ID + coreSessionOrSubscriberId);
        } else if (JMXConstant.INVALID_INPUT_PARAMETER.equalsIgnoreCase(result)) {
            if (LogManager.getLogger().isInfoLogLevel()) {
                LogManager.getLogger().info(MODULE, "Invalid input parameter found while performing " + updateActionLabel + FOR_ID + coreSessionOrSubscriberId);
            }
            throw new OperationFailedException("Invalid input parameter found while performing " + updateActionLabel + FOR_ID + coreSessionOrSubscriberId, ResultCode.INVALID_INPUT_PARAMETER);
        } else if (JMXConstant.OPERATION_NOT_SUPPORTED.equalsIgnoreCase(result)) {
            if (LogManager.getLogger().isInfoLogLevel()) {
                LogManager.getLogger().info(MODULE, updateActionLabel + " service unavailable for Id: " + coreSessionOrSubscriberId);
            }
            throw new OperationFailedException(updateActionLabel + " service unavailable for Id: " + coreSessionOrSubscriberId, ResultCode.OPERATION_NOT_SUPPORTED);
        } else if (JMXConstant.SESSION_NOT_FOUND.equalsIgnoreCase(result)) {
            if (LogManager.getLogger().isInfoLogLevel()) {
                LogManager.getLogger().info(MODULE, "Session not found while performing " + updateActionLabel + FOR_ID + coreSessionOrSubscriberId);
            }
            throw new OperationFailedException("Session not found while performing " + updateActionLabel + FOR_ID + coreSessionOrSubscriberId, ResultCode.NOT_FOUND);
        } else if (JMXConstant.INTERNAL_ERROR.equalsIgnoreCase(result)) {
            if (LogManager.getLogger().isInfoLogLevel()) {
                LogManager.getLogger().info(MODULE, "Internal error while performing " + updateActionLabel + FOR_ID + coreSessionOrSubscriberId);
            }
            throw new OperationFailedException("Internal error while performing " + updateActionLabel + FOR_ID + coreSessionOrSubscriberId);
        } else {
            if (LogManager.getLogger().isInfoLogLevel()) {
                LogManager.getLogger().info(MODULE, updateActionLabel + FAILED_FOR_ID + coreSessionOrSubscriberId);
            }
            throw new OperationFailedException(updateActionLabel + FAILED_FOR_ID + coreSessionOrSubscriberId);
        }
    }

}
