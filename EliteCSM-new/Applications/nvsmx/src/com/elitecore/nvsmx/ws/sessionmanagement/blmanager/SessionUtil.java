package com.elitecore.nvsmx.ws.sessionmanagement.blmanager;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.session.SessionInformation;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.elitecore.nvsmx.ws.util.SessionInformationComparator.getSessionInformationComparatorBasedOnCreationTime;

/**
 * Created by aditya on 5/10/17.
 */
public class SessionUtil {


    private static final String MODULE = "SESSION-UTIL";
    private EndPointManager endPointManager;
    private RMIGroupManager rmiGroupManager;
    public static final Predicate<RMIResponse<SessionInformation>> RMI_RESPONSE_ERROR_OR_NULL_PREDICATE = new Predicate<RMIResponse<SessionInformation>>() {
        @Override
        public boolean apply(RMIResponse<SessionInformation> rmiResponse) {
            if (rmiResponse.isErrorOccurred()) {
                return false;
            }
            if (rmiResponse.getResponse() == null) {
                return false;
            }
            return true;
        }
    };


    public SessionUtil(EndPointManager endPointManager, RMIGroupManager rmiGroupManager) {
        this.endPointManager = endPointManager;
        this.rmiGroupManager = rmiGroupManager;
    }

    public @Nullable RMIResponse<SessionInformation> getSessionByCoreSessionId(@Nonnull String coreSessionId) {
        try {
            RemoteMethod remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
                    RemoteMethodConstant.SESSIONS_BY_CORE_SESSION_ID_FROM_CACHE,
                    coreSessionId, HTTPMethodType.GET);
            BroadCastCompletionResult<SessionInformation> broadcast = RemoteMessageCommunicator.broadcast(EndPointManager.getInstance().getAllNetvertexEndPoint(), remoteMethod);
            RMIResponse<SessionInformation> sessionDataResponse = broadcast.filter(RMI_RESPONSE_ERROR_OR_NULL_PREDICATE).sort(getSessionInformationComparatorBasedOnCreationTime()).getFirst(3, TimeUnit.SECONDS);

            if(sessionDataResponse != null ){
                return sessionDataResponse;
            }
            sessionDataResponse =  RemoteMessageCommunicator.callSync(rmiGroupManager.getNetvertexInstanceRMIGroups(), new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH, RemoteMethodConstant.SESSIONS_BY_CORE_SESSION_ID, coreSessionId, HTTPMethodType.GET), RMI_RESPONSE_ERROR_OR_NULL_PREDICATE);
            if(sessionDataResponse == null ){
                if (LogManager.getLogger().isDebugLogLevel()) {
                    LogManager.getLogger().debug(MODULE, "Unable to find session for core session id: "+coreSessionId+" . Reason: No response found from server");
                }
            }
            return sessionDataResponse;
        }catch (Exception e){
            LogManager.getLogger().error(MODULE,"Error while fetching session for coreSessionId: "+coreSessionId);
            LogManager.getLogger().trace(MODULE,e);
        }
       return null;
    }


    public @Nullable Map<SessionInformation, RMIResponse<Collection<SessionInformation>>> getSessionBySessionIP(String sessionIP) {

            RemoteMethod remoteMethod = null;
            boolean isIPv4 = sessionIP.contains(".");
            if (isIPv4) {
                remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
                        RemoteMethodConstant.SESSIONS_BY_SESSION_IPV4_FROM_CACHE,
                        sessionIP, HTTPMethodType.GET);
            } else {
                remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
                        RemoteMethodConstant.SESSIONS_BY_SESSION_IPV6_FROM_CACHE,
                        sessionIP, HTTPMethodType.GET);
            }

            BroadCastCompletionResult<Collection<SessionInformation>> broadcast = null;
            try {
                broadcast = RemoteMessageCommunicator.broadcast(endPointManager.getAllNetvertexEndPoint(), remoteMethod);

                ///Filter Session information based on creation time
                Collection<RMIResponse<Collection<SessionInformation>>> rmiResponses = broadcast.filter(RMIResponsePredicates.NOT_NULL_AND_NOT_EMPTY_RMI_RESPONSE).getAll(3, TimeUnit.SECONDS);
                SessionTypePredicate sessionTypePredicate = SessionTypePredicate.create(null);

                SessionInformationResultAccumulator sessionInformationResultAccumulator  = new SessionInformationResultAccumulator(sessionTypePredicate);
                sessionInformationResultAccumulator.accumulate(rmiResponses);
                Map<SessionInformation, RMIResponse<Collection<SessionInformation>>> sessionInformationToRMIResponse = sessionInformationResultAccumulator.getSessionInformationToRMIResponse();


                if(Maps.isNullOrEmpty(sessionInformationToRMIResponse) == false ){
                   return sessionInformationToRMIResponse;
                }

                if (Collectionz.isNullOrEmpty(rmiResponses)) {
                    if (isIPv4) {
                        remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
                                RemoteMethodConstant.SESSIONS_BY_SESSION_IPV4,
                                sessionIP, HTTPMethodType.GET);
                    } else {
                        remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
                                RemoteMethodConstant.SESSIONS_BY_SESSION_IPV6,
                                sessionIP, HTTPMethodType.GET);
                    }
                    RMIResponse<Collection<SessionInformation>> syncCallRMIResponse = RemoteMessageCommunicator.callSync(rmiGroupManager.getNetvertexInstanceRMIGroups(), remoteMethod, RMIResponsePredicates.NOT_NULL_AND_NOT_EMPTY_RMI_RESPONSE);

                    if(syncCallRMIResponse == null ){
                        if(LogManager.getLogger().isDebugLogLevel()){
                            LogManager.getLogger().debug(MODULE,"Unable to fetch Session Information. Reason.No response found from server");
                        }
                        return null;
                    }

                    if (syncCallRMIResponse.isErrorOccurred()) {
                        throw syncCallRMIResponse.getError();
                    } else {
                        sessionInformationResultAccumulator = new SessionInformationResultAccumulator(sessionTypePredicate);
                        sessionInformationResultAccumulator.accumulate(syncCallRMIResponse);
                        return sessionInformationResultAccumulator.getSessionInformationToRMIResponse();
                    }
                }

            } catch (Exception e) {
                LogManager.getLogger().error(MODULE,"Error while fetching session for sessionIP: "+sessionIP);
                LogManager.getLogger().trace(MODULE, e);
            }
        return null;
    }

    public Map<SessionInformation, RMIResponse<Collection<SessionInformation>>> getSessionBySubscriberIdentity(String subscriberId, @Nullable String sessionType) {
        RemoteMethod remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
                RemoteMethodConstant.SESSIONS_BY_SUBSCRIBER_IDENTITY_FROM_CACHE,
                subscriberId, HTTPMethodType.GET);

        try {

            SessionTypePredicate sessionTypePredicate = SessionTypePredicate.create(sessionType);

            BroadCastCompletionResult<Collection<SessionInformation>> broadcast = RemoteMessageCommunicator.broadcast(EndPointManager.getInstance().getAllNetvertexEndPoint(), remoteMethod);

            ///Filter Session information based on creation time
            Collection<RMIResponse<Collection<SessionInformation>>> broadCastResult = broadcast.filter(RMIResponsePredicates.NOT_NULL_AND_NOT_EMPTY_RMI_RESPONSE).getAll(3, TimeUnit.SECONDS);
            SessionInformationResultAccumulator sessionInformationResultAccumulator = new SessionInformationResultAccumulator(sessionTypePredicate);
            sessionInformationResultAccumulator.accumulate(broadCastResult);
            Map<SessionInformation, RMIResponse<Collection<SessionInformation>>> sessionInformationToRMIResponse = sessionInformationResultAccumulator.getSessionInformationToRMIResponse();

            if(Maps.isNullOrEmpty(sessionInformationToRMIResponse) == false ){
                return sessionInformationToRMIResponse;
            }


            remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH,
                    RemoteMethodConstant.SESSIONS_BY_SUBSCRIBER_IDENTITY,
                    subscriberId, HTTPMethodType.GET);
            RMIResponse<Collection<SessionInformation>> syncCallRMIResponse = RemoteMessageCommunicator.callSync(RMIGroupManager.getInstance().getNetvertexInstanceRMIGroups(), remoteMethod, RMIResponsePredicates.NOT_NULL_AND_NOT_EMPTY_RMI_RESPONSE);

            if(syncCallRMIResponse == null ) {
                if (LogManager.getLogger().isDebugLogLevel()) {
                    LogManager.getLogger().debug(MODULE, "Unable to fetch Session Information. Reason.No response found from server");
                }
              return null;
            }
            if (syncCallRMIResponse.isErrorOccurred()) {
                throw syncCallRMIResponse.getError();
            } else {
                sessionInformationResultAccumulator = new SessionInformationResultAccumulator(sessionTypePredicate);
                sessionInformationResultAccumulator.accumulate(syncCallRMIResponse);
                return sessionInformationResultAccumulator.getSessionInformationToRMIResponse();
            }

        } catch (Exception e) {
            LogManager.getLogger().error(MODULE,"Error while fetching session for subscriberId: "+subscriberId);
            LogManager.getLogger().trace(MODULE,e);
        }
        return null;

    }


}
