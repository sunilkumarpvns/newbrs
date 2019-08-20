package com.elitecore.nvsmx.ws.sessionmanagement.restws;

import com.elitecore.corenetvertex.util.StringUtil;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.RMIGroupManager;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatisticsManager;
import com.elitecore.nvsmx.ws.sessionmanagement.ISessionManagementWS;
import com.elitecore.nvsmx.ws.sessionmanagement.blmanager.SessionWSBLManager;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionQueryByIPRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionQueryRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionReAuthByCoreSessionIdRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.request.SessionReAuthBySubscriberIdRequest;
import com.elitecore.nvsmx.ws.sessionmanagement.response.SessionQueryResponse;
import com.elitecore.nvsmx.ws.sessionmanagement.response.SessionReAuthResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Created by aditya on 2/8/17.
 */
@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
public class RESTSessionManagementWS implements ISessionManagementWS {

    private static final String MODULE = "REST-SESS-MGMT-WS";
    public static final String WEB_SERVICE_NAME = RESTSessionManagementWS.class.getSimpleName();
    private SessionWSBLManager sessionWSBLManager;

    public RESTSessionManagementWS() {
        sessionWSBLManager = new SessionWSBLManager(WebServiceStatisticsManager.getInstance(), EndPointManager.getInstance(), RMIGroupManager.getInstance());
    }

    @Override
    @GET
    @Path("/search/byIdentity")
    public  SessionQueryResponse wsGetSessionsBySubscriberIdentity(
            @QueryParam(value="subscriberIdentity")String subscriberId,
            @QueryParam(value="alternateId")String alternateId,
            @QueryParam(value = "sessionType")String sessionType,
            @QueryParam(value="parameter1")String parameter1,
            @QueryParam(value="parameter2")String parameter2) {

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
    @GET
    @Path("/search/bySessionIP")
    public SessionQueryResponse wsGetSessionsByIP(
            @QueryParam(value = "sessionIP")String sessionIP,
            @QueryParam(value="sessionType")String sessionType,
            @QueryParam(value="parameter1")String parameter1,
            @QueryParam(value="parameter2")String parameter2) {

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
    @GET
    @Path("/reAuth/byIdentity")
    public SessionReAuthResponse wsReauthSessionsBySubscriberIdentity(
            @QueryParam(value = "subscriberIdentity") String subscriberId,
            @QueryParam(value = "alternateId") String alternateId,
            @QueryParam(value = "parameter1") String parameter1,
            @QueryParam(value = "parameter2") String parameter2) {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsReAuthSessionsBySubscriberIdentity");
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
    @GET
    @Path("/reAuth/byCoreSessionId")
    public SessionReAuthResponse wsReauthSessionsByCoreSessionId(
            @QueryParam(value= "coreSessionId") String coreSessionId,
            @QueryParam(value= "parameter1") String parameter1,
            @QueryParam(value= "parameter2") String parameter2) {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Called wsReAuthSessionsByCoreSessionId");
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