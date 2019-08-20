package com.elitecore.netvertex.ws;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.corenetvertex.constants.JMXConstant;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.netvertex.EliteNetVertexServer;
import com.elitecore.netvertex.core.session.NetvertexSessionManager;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;

import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class SessionDisconnectController {

    private static final String MODULE = "SESSION-DISCONNECT-CONTROLLER";
    private final NetvertexSessionManager sessionManager;
    private final EliteNetVertexServer.NetvertexGatewayController gatewayController;

    public SessionDisconnectController(NetvertexSessionManager sessionManager, EliteNetVertexServer.NetvertexGatewayController gatewayController) {
        this.sessionManager = sessionManager;
        this.gatewayController = gatewayController;
    }


    public String disconnectbyCoreSessionId(String coreSessionId) {
        return sessionDisconnect(PCRFKeyConstants.CS_CORESESSION_ID, coreSessionId, false);
    }

    public String disconnectbySubscriberIdentity(String subscriberIdentity) {
        return sessionDisconnect(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY, subscriberIdentity, true);
    }

    private String sessionDisconnect(PCRFKeyConstants pcrfKey, String value, boolean consideredOnlyCoreSessions) {

        if (getLogger().isInfoLogLevel())
            getLogger().info(MODULE, "Sending session disconnect message for session-ID: " + value);

        SessionLocator sessionLocator = sessionManager.getSessionLookup();
        try {
            Criteria criteria = sessionLocator.getCoreSessionCriteria();
            criteria.add(Restrictions.eq(pcrfKey.val, value));
            List<SessionData> sessions = sessionLocator.getCoreSessionList(criteria);

            if (sessions != null && !sessions.isEmpty()) {

                int noOfDisconnect = 0;
                for (SessionData session : sessions) {
                    if (getLogger().isLogLevel(LogLevel.INFO))
                        getLogger().info(MODULE, "Generating disconnect request");

                    String sessionTypeVal = session.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal());
                    if (sessionTypeVal == null) {
                        if (getLogger().isLogLevel(LogLevel.DEBUG))
                            getLogger().debug(MODULE, "Skipping further processing. Reason: Session type not found");
                        continue;
                    }

                    SessionTypeConstant sessionType = SessionTypeConstant.fromValue(sessionTypeVal);

                    if (consideredOnlyCoreSessions) {
                        if (sessionType == null || SessionTypeConstant.SY == sessionType || SessionTypeConstant.RX == sessionType) {
                            getLogger().warn(MODULE, "Skipping session disconnect for " + pcrfKey + " = " + value +
                                    " Reason:Session-type is " + sessionTypeVal);
                            continue;
                        }
                    }

                    PCRFRequest request = new PCRFRequestImpl();

                    PCRFPacketUtil.buildPCRFRequest(session, request);

                    request.addPCRFEvent(PCRFEvent.SESSION_STOP);
                    if (getLogger().isLogLevel(LogLevel.INFO))
                        getLogger().info(MODULE, "PCRF request created " + request);

                    if (SessionTypeConstant.RADIUS == sessionType) {
                        gatewayController.getRadiusGatewayController().handleSessionDisconnectRequest(request);
                    } else {
                        gatewayController.getDiameterGatewayController().handleSessionDisconnectRequest(request);
                    }

                    noOfDisconnect++;
                }

                if (noOfDisconnect == 0) {
                    return JMXConstant.SESSION_NOT_FOUND;
                } else {
                    return JMXConstant.SUCCESS;
                }
            } else {
                if (getLogger().isLogLevel(LogLevel.INFO))
                    getLogger().info(MODULE, "No session with session-ID: " + value + " present");
                return JMXConstant.SESSION_NOT_FOUND;
            }
        } catch (SessionException e) {
            if (getLogger().isLogLevel(LogLevel.ERROR))
                getLogger().error(MODULE, "Skipping session disconnect request sending. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
            return JMXConstant.SERVICE_UNAVAILABLE;
        }
    }

}
