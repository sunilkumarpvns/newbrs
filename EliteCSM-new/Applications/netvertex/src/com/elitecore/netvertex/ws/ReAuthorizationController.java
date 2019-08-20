package com.elitecore.netvertex.ws;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.JMXConstant;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.netvertex.EliteNetVertexServer;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.session.NetvertexSessionManager;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.session.SessionSortOrder;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ReAuthorizationController {

    private static final String MODULE = "RE-AUTHORIZATION-CONTROLLER";


    private final NetvertexSessionManager sessionManager;
    private final EliteNetVertexServer.NetvertexGatewayController gatewayController;
    private final NetVertexServerContext serverContext;

    public ReAuthorizationController(NetvertexSessionManager sessionManager,
                                      EliteNetVertexServer.NetvertexGatewayController gatewayController,
                                      NetVertexServerContext serverContext) {
        this.sessionManager = sessionManager;
        this.gatewayController = gatewayController;
        this.serverContext = serverContext;
    }

    public String reAuthByCoreSessionId(String coreSessionId) {

        if (Strings.isNullOrBlank(coreSessionId)) {
            getLogger().error(MODULE, "Skipping reauthorization. Reason: Core session id is null");
            return JMXConstant.INVALID_INPUT_PARAMETER;
        }

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Processing reauthorization for core session ID: " + coreSessionId);
        }

        return doReAuthorization(PCRFKeyConstants.CS_CORESESSION_ID, coreSessionId.trim(), true);
    }

    public String reAuthBySubscriberIdentity(String subscriberIdentity) {
        if (Strings.isNullOrBlank(subscriberIdentity)) {
            getLogger().error(MODULE, "Skipping reauthorization. Reason: Subscriber identity is null");
            return JMXConstant.INVALID_INPUT_PARAMETER;
        }

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Processing reauthorization for subscriber ID: " + subscriberIdentity);
        }

			/*
			 * Here, reAuthorization is done on SubscriberIdentity instead of
			 * UserIdentity Field. SubscriberProfile should contain the value of
			 * SubscriberIdentity field on which user is authenticated. If Value
			 * of subscriber identity is not present with subscriber profile,
			 * then RAR will not be sent for that subscriber identity ::
			 * DISCUSSED WITH SUBHASH
			 */
        return doReAuthorization(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY, subscriberIdentity.trim(), true);
    }

    public String reAuthBySessionIPv4(String sessionIP) {

        if (Strings.isNullOrBlank(sessionIP)) {
            getLogger().error(MODULE, "Skipping reauthorization. Reason: Session ip is null");
            return JMXConstant.INVALID_INPUT_PARAMETER;
        }

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Processing reauthorization for Session IP: " + sessionIP);
        }

        return doReAuthorization(PCRFKeyConstants.CS_SESSION_IPV4, sessionIP.trim(), true);
    }

    public String reAuthBySessionIPv6(String sessionIP) {

        if (Strings.isNullOrBlank(sessionIP)) {
            getLogger().error(MODULE, "Skipping reauthorization. Reason: Session ip is null");
            return JMXConstant.INVALID_INPUT_PARAMETER;
        }

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Processing reauthorization for Session IP: " + sessionIP);
        }

        return doReAuthorization(PCRFKeyConstants.CS_SESSION_IPV6, sessionIP.trim(), true);
    }



    public String doReAuthorization(PCRFKeyConstants pcrfKey, String value, boolean forcefuleReAuth) {

        if (serverContext.getServerConfiguration().getMiscellaneousParameterConfiguration().getRAREnabled() == false) {
            if (getLogger().isLogLevel(LogLevel.INFO)) {
                getLogger().info(MODULE, "Re-Authorizing session for " + pcrfKey + " = " + value +
                        " Skipped. Reason: RAR is disabled");
            }

            invalidateCache(pcrfKey, value);

            return JMXConstant.OPERATION_NOT_SUPPORTED;
        }

        if (gatewayController == null) {
            if (getLogger().isLogLevel(LogLevel.INFO)) {
                getLogger().info(MODULE, "Re-Authorizing session for " + pcrfKey + " = " + value +
                        " Skipped. Reason: ALL gateways are disabled");
            }
            return JMXConstant.FAIL;
        }

        if (gatewayController.getDiameterGatewayController() == null && gatewayController.getRadiusGatewayController() == null) {
            if (getLogger().isLogLevel(LogLevel.INFO)) {
                getLogger().info(MODULE, "Re-Authorizing session for " + pcrfKey + " = " + value +
                        " Skipped. Reason: ALL gateways are disabled");
            }

            return JMXConstant.FAIL;
        }

        if (getLogger().isLogLevel(LogLevel.INFO))
            getLogger().info(MODULE, "Re-Authorizing session for " + pcrfKey + " = " + value);

        SessionLocator sessionLocator = sessionManager.getSessionLookup();
        try {
            Criteria criteria = sessionLocator.getCoreSessionCriteria();
            criteria.add(Restrictions.eq(pcrfKey.val, value));
            Iterator<SessionData> sessionIterator = null;
            if (pcrfKey == PCRFKeyConstants.CS_CORESESSION_ID) {
                sessionIterator = sessionByCoreSessionId(value, sessionLocator, criteria);
            } else if (pcrfKey == PCRFKeyConstants.CS_SESSION_IPV4) {
                sessionIterator = filterAndMerge(sessionLocator.getCoreSessionBySessionIPv4FromCache(value), sessionLocator.getCoreSessionList(criteria),pcrfKey.getVal(),value);
            } else if (pcrfKey == PCRFKeyConstants.CS_SESSION_IPV6) {
                sessionIterator = filterAndMerge(sessionLocator.getCoreSessionBySessionIPv6FromCache(value), sessionLocator.getCoreSessionList(criteria),pcrfKey.getVal(),value);
            } else if (pcrfKey == PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY) {
                sessionIterator = filterAndMerge(sessionLocator.getCoreSessionByUserIdentityFromCache(value), sessionLocator.getCoreSessionList(criteria),pcrfKey.getVal(),value);
            }

            if (sessionIterator != null) {

                SessionReAuthProcessor reAuthProcessor = new SessionReAuthProcessor(gatewayController);
                if (getLogger().isLogLevel(LogLevel.DEBUG))
                    getLogger().debug(MODULE, "Core session found for " + pcrfKey + " = " + value);

                int noOfSessionReauthorized = 0;
                int totalSessions = 0;
                List<SessionData> nonReAuthorizableSessions = new ArrayList<>();
                while(sessionIterator.hasNext()) {
                    totalSessions++;
                    SessionData session = sessionIterator.next();
                    String sessionTypeVal = session.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal());

                    if (sessionTypeVal == null) {
                        if (getLogger().isLogLevel(LogLevel.DEBUG)) {
                            getLogger().debug(MODULE, "Skipping Re-Authorization for the Core-Session-ID: "
                                    + session.getValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal()) + ". Reason: Session type not found");
                        }

                        continue;
                    }

                    SessionTypeConstant sessionType = SessionTypeConstant.fromValue(sessionTypeVal);

                    if (sessionType == null || isReAuthorizableSession(sessionType) == false) {
                        getLogger().warn(MODULE, "Skipping Re-Authorization for " + pcrfKey + " = " + value +
                                " Reason:Invalid session-type: " + sessionTypeVal);
                        nonReAuthorizableSessions.add(session);
                        continue;
                    }

                    if (GatewayTypeConstant.RADIUS == sessionType.gatewayType
                            || GatewayTypeConstant.DIAMETER == sessionType.gatewayType) {
                        reAuthProcessor.addSession(session);
                        noOfSessionReauthorized++;
                    } else {
                        nonReAuthorizableSessions.add(session);
                        getLogger().warn(MODULE, "Skipping Re-Authorization for " + pcrfKey + " = " + value +
                                " Reason: Unsupported session type:" + sessionType + " for session re-authorization");
                    }
                }

				reAuthProcessor.reAuth(forcefuleReAuth);
                if(totalSessions == nonReAuthorizableSessions.size()){
                    if (getLogger().isLogLevel(LogLevel.DEBUG)) {
                        getLogger().debug(MODULE, "All the sessions are non re authorizable session so returning "+JMXConstant.OPERATION_NOT_SUPPORTED);
                    }
                    return JMXConstant.OPERATION_NOT_SUPPORTED;
                }

                if (noOfSessionReauthorized == 0) {
                    return JMXConstant.FAIL;
                }

                if (noOfSessionReauthorized < totalSessions) {
                    return JMXConstant.PARTIAL_SUCCESS;
                }

                return JMXConstant.SUCCESS;

            } else {
                if (getLogger().isLogLevel(LogLevel.DEBUG)) {
                    getLogger().debug(MODULE, "Skipping Re-Authorization for " + pcrfKey + " = " + value +
                            " Reason: Core session not found");
                }
                return JMXConstant.SESSION_NOT_FOUND;
            }
        } catch (SessionException e) {
            if (getLogger().isLogLevel(LogLevel.ERROR)) {
                getLogger().error(MODULE, "Unable to Re-Authorize " + pcrfKey + " = " + value + ". Reason : " + e.getMessage());
            }
            getLogger().trace(MODULE, e);
            return JMXConstant.INTERNAL_ERROR;
        }

    }

    private Iterator<SessionData> filterAndMerge(Iterator<SessionData> iteratorFromCache, List<SessionData> sessionsFromDB, String pcrfKey, String value) {
        if (Objects.isNull(iteratorFromCache) && CollectionUtils.isEmpty(sessionsFromDB)) {
            return null;
        }
        if (Objects.isNull(iteratorFromCache) || iteratorFromCache.hasNext() == false) {
            if (getLogger().isLogLevel(LogLevel.INFO)) {
                getLogger().info(MODULE, "Session for " + pcrfKey + " = " + value + " is not found from cache so returning session from DB");
            }
            return sessionsFromDB.iterator();
        }
        if (CollectionUtils.isEmpty(sessionsFromDB)) {
            if (getLogger().isLogLevel(LogLevel.INFO)) {
                getLogger().info(MODULE, "Session for " + pcrfKey + " = " + value + " is not found from DB so returning session from cache");
            }
            return iteratorFromCache;
        }
        Map<String, List<SessionData>> coreSessionIdToSessions = new HashMap<>();
        createCoreSessionIdWiseMap(iteratorFromCache, coreSessionIdToSessions);
        createCoreSessionIdWiseMap(sessionsFromDB.iterator(), coreSessionIdToSessions);
        return sortSessionsAndReturnIterator(coreSessionIdToSessions);
    }

    private Iterator<SessionData> sortSessionsAndReturnIterator(Map<String, List<SessionData>> coreSessionToSessions) {
        List<SessionData> sortedSessions = new ArrayList<>();
        for(Map.Entry<String,List<SessionData>> entry: coreSessionToSessions.entrySet()){
            SessionSortOrder.LAST_UPDATE_TIME_DESCENDING.sort(entry.getValue());
            sortedSessions.add(entry.getValue().get(0));
        }
        return sortedSessions.iterator();
    }

    private void createCoreSessionIdWiseMap(Iterator<SessionData> sessionIterator, Map<String, List<SessionData>> coreSessionIdToSessions) {
        while (sessionIterator.hasNext()){
            SessionData sessionData = sessionIterator.next();
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Session found with core session id: "+sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.val)+" and last update time:" +sessionData.getLastUpdateTime());
            }
            String coreSessionId = sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal());
            List<SessionData> sessions = coreSessionIdToSessions.computeIfAbsent(coreSessionId, s -> new ArrayList<>());
            sessions.add(sessionData);
        }
    }

    private Iterator<SessionData> sessionByCoreSessionId(String value, SessionLocator sessionLocator, Criteria criteria) {
        SessionData sessionFromCache = sessionLocator.getCoreSessionByCoreSessionIDFromCache(value);
        List<SessionData> sessionFromDb = sessionLocator.getCoreSessionList(criteria);
        if (Objects.isNull(sessionFromCache) && CollectionUtils.isEmpty(sessionFromDb)) {
            return null;
        }
        if (CollectionUtils.isEmpty(sessionFromDb)) {
            return createSessionDataIterator(sessionFromCache);
        }
        if (Objects.isNull(sessionFromCache)) {
            SessionData sessionDataFromDB = sessionFromDb.iterator().next();
            return createSessionDataIterator(sessionDataFromDB);
        }
        sessionFromDb.add(sessionFromCache);
        SessionSortOrder.LAST_UPDATE_TIME_DESCENDING.sort(sessionFromDb);
        return createSessionDataIterator(sessionFromDb.iterator().next());
    }

    private Iterator<SessionData> createSessionDataIterator(SessionData sessionData) {
        List<SessionData> sessionsByCoreSessionId = new ArrayList<SessionData>(2);
        sessionsByCoreSessionId.add(sessionData);
        return sessionsByCoreSessionId.iterator();
    }

    private boolean isReAuthorizableSession(SessionTypeConstant sessionType) {
        return SessionTypeConstant.GX == sessionType || SessionTypeConstant.CISCO_GX == sessionType
                || SessionTypeConstant.RADIUS == sessionType || SessionTypeConstant.GY == sessionType;
    }

    private void invalidateCache(PCRFKeyConstants pcrfKey, String value) {

        SessionData sessionData = null;
        Iterator<SessionData> coreSessionIterator = null;
        switch (pcrfKey) {
            case CS_CORESESSION_ID:
                sessionData = sessionManager.getSessionLookup().getCoreSessionByCoreSessionIDFromCache(value);
                break;
            case SUB_SUBSCRIBER_IDENTITY:
                coreSessionIterator = sessionManager.getSessionLookup().getCoreSessionByUserIdentityFromCache(value);
                if (coreSessionIterator != null && coreSessionIterator.hasNext()) {
                    sessionData = coreSessionIterator.next();
                }
                break;
            case CS_SESSION_IPV4:
                coreSessionIterator = sessionManager.getSessionLookup().getCoreSessionBySessionIPv4FromCache(value);
                if (coreSessionIterator != null && coreSessionIterator.hasNext()) {
                    sessionData = coreSessionIterator.next();
                }
                break;
            case CS_SESSION_IPV6:
                coreSessionIterator = sessionManager.getSessionLookup().getCoreSessionBySessionIPv6FromCache(value);
                if (coreSessionIterator != null && coreSessionIterator.hasNext()) {
                    sessionData = coreSessionIterator.next();
                }

                break;
        }

        if(sessionData != null) {
            String subscriberIdentity = sessionData.getValue(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
            if (getLogger().isDebugLogLevel())
                getLogger().debug(MODULE, "Removing SPR cache for subscriber:" + subscriberIdentity);
            CacheAwareDDFTable.getInstance().removeCache(subscriberIdentity);
            sessionManager.removeCache(sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.val));
            if(sessionData.getValue(PCRFKeyConstants.SUB_ALTERNATE_IDENTITY.val) != null){
                CacheAwareDDFTable.getInstance().removeSecondaryCache(sessionData.getValue(PCRFKeyConstants.SUB_ALTERNATE_IDENTITY.val));
            }
        } else {
            if(pcrfKey == PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY) {
                if (getLogger().isDebugLogLevel())
                    getLogger().debug(MODULE, "Removing SPR cache for subscriber:" + value);
                CacheAwareDDFTable.getInstance().removeCache(value);
            } else {
                if (getLogger().isDebugLogLevel())
                    getLogger().debug(MODULE, "Unable to removing SPR cache. Reason: Session not found from cache");
            }
        }


    }
}
