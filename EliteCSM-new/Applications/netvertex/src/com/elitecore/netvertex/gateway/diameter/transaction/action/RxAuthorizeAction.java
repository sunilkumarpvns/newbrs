package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Criterion;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.af.AFSessionRule;
import com.elitecore.netvertex.gateway.diameter.af.MediaComponent;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.transaction.MappingFailedException;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;


/*
 * ===========Transaction use this action====================
 * NewServiceTransaction
 * ServiceRegistrationTransaction
 */
public class RxAuthorizeAction extends ActionHandler {

    private static final String MODULE = "RX-AUTH-ACTN";
    private static final String NAME = "RxAUTHORIZE";
    private static final Comparator<SessionData> SESSION_RULE_CREATION_DATE_BASE_COMPARATOR = Collections.reverseOrder(Comparator.comparing(SessionData::getCreationTime));

    public RxAuthorizeAction(DiameterTransactionContext transactionContext) {
        super(transactionContext);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public TransactionState handle() {

        DiameterRequest diameterRequest = getDiameterRequest();

        DiameterGatewayControllerContext controllerContext = getTransactionContext().getControllerContext();
        DiameterGatewayConfiguration configuration = controllerContext.getGatewayConfiguration(diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME),
                diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_PROXY_AGENT_NAME));

        if (configuration == null) {
            configuration = controllerContext.getGatewayConfigurationByHostId(diameterRequest.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
        }

        PCRFRequest pcrfRequest;

        try {
            pcrfRequest = createPCRFRequest(configuration.getAARMappings(), configuration);

        }catch(MappingFailedException e){

            sendRejectResponse(e.getErrorCode(), configuration);
            LogManager.getLogger().trace(MODULE, e);
            return TransactionState.COMPLETE;
        }

        LogManager.getLogger().debug(MODULE, "PCRF Request for Rx");
        LogManager.getLogger().debug(MODULE, pcrfRequest.toString());

        Set<PCRFEvent> event = pcrfRequest.getPCRFEvents();

        if (event.isEmpty()) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                LogManager.getLogger().debug(MODULE, "No Event is set for this PCRFRequest. so taking " + PCRFEvent.SESSION_START + " and " + PCRFEvent.AUTHENTICATE + " as default");
            pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_START);
            pcrfRequest.addPCRFEvent(PCRFEvent.AUTHENTICATE);
        }

        /*
         * In AAR request, We required to check that AAR request received is First request or not...
         * if AAR request is not first request then we should set event "Session-Update" because we already have Rx session for that session-id.
         * Also First AAR might be Service-Reg. request(policy handling not required) or Authorization request(Policy Handling required)
         *
         * In this Action PCRFRequest contains one of following events
         * 1) Service Registration request
         * 		--> Session-Start and Authenticate
         * 2) Authorization request
         * 		--> Session-Start, Authenticate and Authorize
         * 3) Authorization request with flow-status (Remove, Disable)
         * 		--> Session-Update, Authenticate
         * 3) Session Termination request
         * 		--> Session-Stop
         *
         */


        SessionData gxCoreSession = null;
        /*IF Rx session not found then we should check Gx session only if it is First(not STR) request
         * because RxSession must be found for AAR if it is not first request
         * */

        if (pcrfRequest.isSessionFound() == false) {
            if (event.contains(PCRFEvent.SESSION_START)) {

                List<MediaComponent> mediaComponents = pcrfRequest.getMediaComponents();
                for (int i = 0; i < mediaComponents.size(); i++) {
                    MediaComponent mediaComponent = mediaComponents.get(i);
                    if (mediaComponent.getFlowStatus() == FlowStatus.REMOVED) {
                        mediaComponents.remove(i);
                        continue;
                    }
                }

                LinkedHashMap<String, String> sessionLookupValue = createSessionLookupKeyMap(pcrfRequest);

                if (sessionLookupValue.isEmpty()) {
                    if (getLogger().isDebugLogLevel())
                        getLogger().debug(MODULE, "Unable to find gx session. Reason: value not found for configured session-lookup keys");
                    sendNegResToGateway(pcrfRequest, PCRFKeyValueConstants.RESULT_CODE_MAIN_SESSION_NOT_AVAILABLE.val);
                    return TransactionState.COMPLETE;
                }

                gxCoreSession = locateGxCoreSession(sessionLookupValue);
                if (gxCoreSession == null) {
                    sendNegResToGateway(pcrfRequest, PCRFKeyValueConstants.RESULT_CODE_MAIN_SESSION_NOT_AVAILABLE.val);
                    return TransactionState.COMPLETE;
                }
            } else {
                sendNegResToGateway(pcrfRequest, PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_SESSION_ID.val);
                return TransactionState.COMPLETE;
            }
        } else {
            List<SessionData> locateSessionRule = locateSessionRule(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val));
            String gxCoreSessionId = findGxCoreSessionIdFromSessionRule(pcrfRequest, locateSessionRule);

            if (gxCoreSessionId != null) {
                gxCoreSession = locateGxCoreSessionByCoreSessionId(gxCoreSessionId + ':' + SessionTypeConstant.GX.val);
            } else {

                LinkedHashMap<String, String> sessionLookupKeyValue = createSessionLookupKeyMap(pcrfRequest);

                if (sessionLookupKeyValue.isEmpty()) {
                    if (getLogger().isDebugLogLevel())
                        getLogger().debug(MODULE, "Unable to find gx session. Reason: value not found for configured session-lookup keys");
                    sendNegResToGateway(pcrfRequest, PCRFKeyValueConstants.RESULT_CODE_MAIN_SESSION_NOT_AVAILABLE.val);
                    return TransactionState.COMPLETE;
                }

                gxCoreSession = locateGxCoreSession(sessionLookupKeyValue);
            }


            if (gxCoreSession == null) {
                if(gxCoreSessionId == null) {
                    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                        LogManager.getLogger().debug(MODULE, "No Gx session is found for reauthorization request. so sending negatiove reply for request");
                    }
                    sendNegResToGateway(pcrfRequest, PCRFKeyValueConstants.RESULT_CODE_MAIN_SESSION_NOT_AVAILABLE.val);
                }else{
                    sendSuccessResToGateway(diameterRequest, pcrfRequest);
                }
                return TransactionState.COMPLETE;
            }

            /*
             * If Rx session found and it is AAR (not STR or Service-Registration request) then set "Session-Update" event and locate Gx session
             */
            if (event.contains(PCRFEvent.SESSION_START) && event.contains(PCRFEvent.AUTHORIZE)) {
                event.remove(PCRFEvent.SESSION_START);
                pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_UPDATE);
            }
        }

        setCoreSessionAttribute(pcrfRequest, gxCoreSession);
        getTransactionContext().getTransactionSession().put(SessionKeys.GX_SESSION, gxCoreSession);

        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
            LogManager.getLogger().debug(MODULE, "PCRF Request : " + pcrfRequest);
        }

            /*
             * According to 3gpp TS 29.214
             *
             * The AF may additionally provide preliminary service information not
             * fully negotiated yet (e.g. based on the SDP offer) at an earlier stage.
             * To do so, the AF shall include the Service-Info-Status AVP with the value
             * set to PRELIMINARY SERVICE INFORMATION. Upon receipt of such preliminary
             * service information, the PCRF shall perform an early authorization check
             * of the service information. For GPRS, the PCRF shall not provision PCC
             * rules towards the PCEF unsolicited.
             */
        if (PCRFKeyValueConstants.SERVICE_INFO_STATUS_PRELIMINARY_SERVICE_INFORMATION.val.equalsIgnoreCase(pcrfRequest.getAttribute(PCRFKeyConstants.SERVICE_INFO_STATUS.val))) {
            pcrfRequest.getPCRFEvents().remove(PCRFEvent.SESSION_START);
            pcrfRequest.getPCRFEvents().remove(PCRFEvent.SESSION_STOP);
            pcrfRequest.getPCRFEvents().remove(PCRFEvent.SESSION_UPDATE);
        }


        RequestStatus requestStatus = getTransactionContext().submitPCRFRequest(pcrfRequest);
        if (requestStatus != RequestStatus.SUBMISSION_SUCCESSFUL) {
            sendRejectResponse(pcrfRequest);
            return TransactionState.COMPLETE;
        }

        return TransactionState.WAIT_FOR_AUTH_RES;// Wait
    }

    /**
     * If Rx session found then remove and generate STA with success code.
     * @param diameterRequest
     * @param pcrfRequest
     */
    private void sendSuccessResToGateway(DiameterRequest diameterRequest, PCRFRequest pcrfRequest) {
        PCRFResponse pcrfResponse = new PCRFResponseImpl();
        PCRFPacketUtil.buildPCRFResponse(pcrfRequest, pcrfResponse);

        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
            LogManager.getLogger().debug(MODULE, "PCRF Response:" + pcrfResponse);
        }
        getTransactionContext().getControllerContext().handleSession(pcrfRequest, pcrfResponse);
        pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.val, PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val);
        DiameterAnswer answer = new DiameterAnswer(diameterRequest);
        getTransactionContext().getControllerContext().buildSTA(pcrfResponse, answer);
        getTransactionContext().sendAnswer(answer, diameterRequest);
    }

    private String findGxCoreSessionIdFromSessionRule(PCRFRequest pcrfRequest, List<SessionData> locateSessionRule) {
        String gxCoreSessionId = null;
        if (locateSessionRule != null && locateSessionRule.isEmpty() == false) {
            ArrayList<AFSessionRule> afActivePCCRules = new ArrayList<AFSessionRule>(locateSessionRule.size());

            for (int index = 0; index < locateSessionRule.size(); index++) {
                String gxCoreSessionIdFromSessionRule = locateSessionRule.get(index).getValue(PCRFKeyConstants.CS_CORESESSION_ID.val);

                if (gxCoreSessionId != null) {
                    if (gxCoreSessionId.equals(gxCoreSessionIdFromSessionRule) == false) {
                        continue;
                    }
                } else {
                    gxCoreSessionId = gxCoreSessionIdFromSessionRule;
                }

                afActivePCCRules.add(createAfSessionRule(pcrfRequest, locateSessionRule, index));
            }
            pcrfRequest.setAFActivePCCRule(afActivePCCRules);
        }
        return gxCoreSessionId;
    }

    private AFSessionRule createAfSessionRule(PCRFRequest pcrfRequest, List<SessionData> locateSessionRule, int index) {
        AFSessionRule afSessionRule = AFSessionRule.create(locateSessionRule.get(index));
        if (pcrfRequest.getMediaComponents() != null) {
            for (int i = 0; i < pcrfRequest.getMediaComponents().size(); i++) {
                if (pcrfRequest.getMediaComponents().get(i).belongsToYou(afSessionRule)) {
                    pcrfRequest.getMediaComponents().get(i).addPreviousInstalledRule(afSessionRule);
                }
            }
        }
        return afSessionRule;
    }

    private LinkedHashMap<String, String> createSessionLookupKeyMap(PCRFRequest pcrfRequest) {
        List<String> sessionLookupKey = getTransactionContext().getControllerContext().getGatewayConfigurationByName(pcrfRequest.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val)).getSessionLookupKey();
        LinkedHashMap<String, String> sessionLookupValue = new LinkedHashMap<String, String>();
        for (int i = 0; i < sessionLookupKey.size(); i++) {
            String key = sessionLookupKey.get(i);
            String value = pcrfRequest.getAttribute(key);
            if (value == null) {
                if (getLogger().isDebugLogLevel())
                    getLogger().debug(MODULE, "Skipping session look up key:" + key + " to find gx session. Reason: value not found for session-lookup key:" + key);
                continue;
            }
            sessionLookupValue.put(key, value);
        }
        return sessionLookupValue;
    }


    private List<SessionData> locateSessionRule(String attribute) {
        SessionLocator sessionLocator = getTransactionContext().getControllerContext().getSessionLocator();
        try {
            Criteria criteria = sessionLocator.getSessionRuleCriteria();
            criteria.add(Restrictions.eq(PCRFKeyConstants.CS_AF_SESSION_ID.val, attribute));
            List<SessionData> sessionRules = sessionLocator.getSessionRules(criteria);

            if (Collectionz.isNullOrEmpty(sessionRules) == false && sessionRules.size() > 1) {
                Collections.sort(sessionRules, SESSION_RULE_CREATION_DATE_BASE_COMPARATOR);
            }

            return sessionRules;
        } catch (SessionException e) {
            if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
                LogManager.getLogger().error(MODULE, "Unable to locate session rule. Reason: " + e.getMessage());
            LogManager.getLogger().trace(MODULE, e);
        }
        return null;
    }


    /**
     * locateGxCoreSession locate Gx session based on SessionLookUp key
     *
     * @param sessionLookupValue
     * @return SessionData
     */
    private SessionData locateGxCoreSessionByCoreSessionId(String sessionLookupValue) {
        SessionLocator sessionLocator = getTransactionContext().getControllerContext().getSessionLocator();
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            LogManager.getLogger().debug(MODULE, "Locating Gx by core-session-ID, value=" + sessionLookupValue);


        return sessionLocator.getCoreSessionByCoreSessionID(sessionLookupValue);

    }

    /**
     * locateGxCoreSession locate Gx session based on SessionLookUp key
     * Multiple keys used as "or" condition to find Gx session
     *
     * @return SessionData
     */
    private SessionData locateGxCoreSession(LinkedHashMap<String, String> sessionLookupKeys) {
        SessionLocator sessionLocator = getTransactionContext().getControllerContext().getSessionLocator();


        try {

            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "Locating Gx by key=" + sessionLookupKeys);
            }


            Criteria criteria = sessionLocator.getCoreSessionCriteria();

            criteria.add(createCriteria(sessionLookupKeys));
            List<SessionData> sessionDatas = sessionLocator.getCoreSessionList(criteria);

            SessionData newestSession = null;
            if (Collectionz.isNullOrEmpty(sessionDatas)) {
                return null;
            }

            for (int index = 0; index < sessionDatas.size(); index++) {
                SessionData sessionData = sessionDatas.get(index);
                if (SessionTypeConstant.GX.val.equals(sessionData.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal())) == false) {
                    continue;
                }

                if (newestSession != null) {
                    if (newestSession.getCreationTime().before(sessionData.getCreationTime())) {
                        newestSession = sessionData;
                    }
                } else {
                    newestSession = sessionData;
                }
            }

            return newestSession;

        } catch (SessionException e) {
            if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
                LogManager.getLogger().error(MODULE, "Unable to locate core Session. Reason: " + e.getMessage());
            LogManager.getLogger().trace(MODULE, e);
        }
        return null;
    }

    private Criterion createCriteria(LinkedHashMap<String, String> sessionLookupKeyValue) {

        Criterion criterion = null;
        for (Entry<String, String> sessionLookupKeyValueEntry : sessionLookupKeyValue.entrySet()) {
            if (criterion == null) {
                criterion = Restrictions.eq(sessionLookupKeyValueEntry.getKey(), sessionLookupKeyValueEntry.getValue());
            } else {
                criterion = Restrictions.or(criterion, Restrictions.eq(sessionLookupKeyValueEntry.getKey(), sessionLookupKeyValueEntry.getValue()));
            }
        }

        return criterion;
    }

    /**
     * Sending Response to Rx gateway
     *
     * @param pcrfRequest
     */
    private void sendNegResToGateway(PCRFRequest pcrfRequest, String resultCode) {
        PCRFResponse pcrfResponse = new PCRFResponseImpl();
        PCRFPacketUtil.buildPCRFResponse(pcrfRequest, pcrfResponse);

        DiameterRequest diameterRequest = getDiameterRequest();
        DiameterAnswer answer = new DiameterAnswer(diameterRequest);
        if (resultCode.equals(PCRFKeyValueConstants.RESULT_CODE_MAIN_SESSION_NOT_AVAILABLE.val)) {
            AvpGrouped experimentalResult = (AvpGrouped) DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EXPERIMENTAL_RESULT);

            IDiameterAVP vendoerIdAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.VENDOR_ID);
            vendoerIdAVP.setInteger(ResultCode.DIAMETER_IP_CAN_SESSION_NOT_AVAILABLE.vendorId);
            experimentalResult.addSubAvp(vendoerIdAVP);

            IDiameterAVP experimentalResultCodeAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EXPERIMENTAL_RESULT_CODE);
            experimentalResultCodeAVP.setInteger(ResultCode.DIAMETER_IP_CAN_SESSION_NOT_AVAILABLE.code);
            experimentalResult.addSubAvp(experimentalResultCodeAVP);
            answer.addAvp(experimentalResult);
        } else {
            pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.val, resultCode);
        }
        getTransactionContext().getControllerContext().buildAAA(pcrfResponse, answer);
        getTransactionContext().sendAnswer(answer, diameterRequest);
    }


    /**
     * setCoreSessionAttribute set session attribute to pcrfRequest if it is not found in pcrfRequest
     * if event is service-registration or authorization then
     * session is gx session
     * if event is service-stop or session-termination then
     * session is rx session
     *
     * @param pcrfRequest
     * @param session
     */
    private void setCoreSessionAttribute(PCRFRequest pcrfRequest, SessionData session) {

        PCRFPacketUtil.buildPCRFRequest(session, pcrfRequest, false);

        if (SessionTypeConstant.GX.getVal().equals(session.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()))) {
            pcrfRequest.setAttribute(PCRFKeyConstants.GX_ADDRESS.getVal(), session.getValue(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal()));
            pcrfRequest.setAttribute(PCRFKeyConstants.GX_REALM.getVal(), session.getValue(PCRFKeyConstants.CS_GATEWAY_REALM.getVal()));
            pcrfRequest.setAttribute(PCRFKeyConstants.GX_SESSION_ID.getVal(), session.getValue(PCRFKeyConstants.CS_SESSION_ID.getVal()));
        } else if (SessionTypeConstant.RX.getVal().equals(session.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()))) {
            pcrfRequest.setSessionStartTime(session.getCreationTime());
        }

    }

}
