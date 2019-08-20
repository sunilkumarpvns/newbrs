package com.elitecore.netvertex.gateway.diameter.application;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupFactory;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.core.common.peer.group.HighAvailabilityStatus;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.session.ClientApplicationRequestResponseListener;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.stack.application.ClientApplication;
import com.elitecore.diameterapi.mibs.constants.ServiceTypes;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.handler.ApplicationHandler;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.scripts.DiameterGroovyScript;

import javax.annotation.Nullable;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public abstract class NetvertexBaseClientApplication extends ClientApplication implements NetvertexApplication {
    private static final String MODULE = "BASE-DIA-CLIENT-APP";
    protected DiameterGatewayControllerContext context;
    private DiameterPeerGroupFactory groupFactory;

    public NetvertexBaseClientApplication(DiameterPeerGroupFactory groupFactory, DiameterGatewayControllerContext context, final long vendorId, final long applicationId, final Application application) {
        super(context.getStackContext(), new AuthApplication(vendorId, applicationId, application));
        this.groupFactory = groupFactory;

        this.context = context;
    }

    /*
     * Any Change related to this method must be done in same method provided in NetvertexBaseServerApplication
     */
    protected final void applyScriptsForReceivedPacket(DiameterPacket diameterPacket, String gatewayName) {

        List<DiameterGroovyScript> scripts = context.getDiameterGroovyScripts(gatewayName);
        if (scripts == null || scripts.isEmpty()) {
            return;
        }

        for (DiameterGroovyScript script : scripts) {
            try {
                script.postReceived(diameterPacket);
            } catch (Exception ex) {
                LogManager.getLogger().error(MODULE, "Error in executing script \"" + script.getName() + "\" for Diameter-Packet with Session-ID= "
                        + diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID) + " for gateway = " + gatewayName + ". Reason: " + ex.getMessage());
                LogManager.getLogger().trace(MODULE, ex);
            }

        }

    }

    /*
     * Any Change related to this method must be done in same method provided in NetvertexBaseClientApplication
     */
    @Override
    public void preProcess(DiameterSession session, DiameterAnswer diameterPacket) {
        super.preProcess(session, diameterPacket);

        preProcessInternal(diameterPacket);
    }


    public void preProcess(DiameterSession session, DiameterRequest diameterPacket, DiameterPeerGroupParameter parameter, @Nullable String preferredPeer) {

        String sessionID = diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID);

        DiameterGatewayConfiguration configuration = null;
        if (preferredPeer == null) {

            for (String peerName : parameter.getPeers().keySet()) {

                configuration = context.getGatewayConfigurationByName(peerName);

                if (configuration == null) {
                    configuration = context.getGatewayConfigurationByHostId(peerName);
                }
                break;
            }
        } else {
            HighAvailabilityStatus haStatus = (HighAvailabilityStatus) session.getParameter(parameter.getName());
            configuration = context.getGatewayConfigurationByHostId(haStatus.getActivePeer());

            if (configuration == null) {
                configuration = context.getGatewayConfigurationByName(haStatus.getActivePeer());
            }
        }

        if (configuration == null) {
            LogManager.getLogger().error(MODULE, "Unable to execute scripts for Diameter-Packet with Session-ID= " + sessionID + ". Reason: Gateway Configuration not found");
            return;
        }

        List<DiameterGroovyScript> scripts = context.getDiameterGroovyScripts(configuration.getName());
        if (scripts == null || scripts.isEmpty()) {
            return;
        }

        for (DiameterGroovyScript script : scripts) {
            try {
                script.preSend(diameterPacket);
            } catch (Exception ex) {
                LogManager.getLogger().error(MODULE, "Error in executing script \"" + script.getName() + "\" for Diameter-Packet with Session-ID= " +
                        sessionID + " for gateway = " + configuration.getName() + ". Reason: " + ex.getMessage());
                LogManager.getLogger().trace(MODULE, ex);
            }

        }
    }

    /*
     * Any Change related to this method must be done in same method provided in NetvertexBaseServerApplication
     */
    private void preProcessInternal(DiameterAnswer diameterPacket) {


        String sessionID = diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID);

        DiameterGatewayConfiguration configuration = context.getGatewayConfigurationByName(diameterPacket.getInfoAVPValue(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME));
        if (configuration == null) {
            LogManager.getLogger().error(MODULE, "Unable to execute scripts for Diameter-Packet with Session-ID= " + sessionID + ". Reason: Gateway Configuration not found");
            return;
        }

        List<DiameterGroovyScript> scripts = context.getDiameterGroovyScripts(configuration.getName());
        if (scripts == null || scripts.isEmpty()) {
            return;
        }

        for (DiameterGroovyScript script : scripts) {
            try {
                script.preSend(diameterPacket);
            } catch (Exception ex) {
                LogManager.getLogger().error(MODULE, "Error in executing script \"" + script.getName() + "\" for Diameter-Packet with Session-ID= " +
                        sessionID + " for gateway = " + configuration.getName() + ". Reason: " + ex.getMessage());
                LogManager.getLogger().trace(MODULE, ex);
            }

        }
    }

    private class NetVertexBaseClientResponseListener implements ResponseListener {

        private final DiameterRequest diameterRequest;


        public NetVertexBaseClientResponseListener(DiameterRequest diameterRequest) {
            super();
            this.diameterRequest = diameterRequest;
        }

        @Override
        public void requestTimedout(String hostIdentity, DiameterSession session) {

            if (getLogger().isLogLevel(LogLevel.INFO))
                getLogger().info(MODULE, "Timeout Request: " + diameterRequest);


            DiameterGatewayConfiguration gatewayConfiguration = context.getGatewayConfigurationByHostId(hostIdentity);

            if (gatewayConfiguration == null) {
                getLogger().error(MODULE, "Unable to process Timeout request. Reason: Configuration not found");
                return;
            }

            SupportedStandard supportedStandard = gatewayConfiguration.getSupportedStandard();

            ApplicationHandler appHandler = context.getApplicationHandler(getApplicationEnum()[0].getApplication(), supportedStandard);

            if (appHandler == null) {
                getLogger().error(MODULE, "Unable to process Timeout request. Reason : No Application Handler found for Application");
                return;
            }

            appHandler.handleTimeoutRequest(session, diameterRequest);
        }

        @Override
        public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession session) {

            if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
                LogManager.getLogger().info(MODULE, "Received Response: " + diameterAnswer);

            session.setParameter(DiameterAVPConstants.DESTINATION_HOST, diameterAnswer.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));

            DiameterGatewayConfiguration gatewayConfiguration = context.getGatewayConfiguration(diameterAnswer.getInfoAVPValue(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME),
                    diameterAnswer.getInfoAVPValue(DiameterAVPConstants.EC_PROXY_AGENT_NAME));

            if (gatewayConfiguration == null) {
                gatewayConfiguration = context.getGatewayConfigurationByHostId(diameterAnswer.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
            }

            if (gatewayConfiguration == null) {
                LogManager.getLogger().error(MODULE, "Unable to process response. Reason: Gateway configuration not found");
                return;
            }

            applyScriptsForReceivedPacket(diameterAnswer, gatewayConfiguration.getName());

            SupportedStandard supportedStandard = gatewayConfiguration.getSupportedStandard();

            ApplicationHandler appHandler = context.getApplicationHandler(getApplicationEnum()[0].getApplication(), supportedStandard);

            if (appHandler == null) {
                LogManager.getLogger().error(MODULE, "Unable to process request. Reason : No Application Handler found for Application");
                return;
            }

            appHandler.handleReceivedResponse(session, diameterRequest, diameterAnswer);


        }

    }

    @Override
    public void sendRequest(DiameterSession session,
                            DiameterRequest request,
                            DiameterPeerGroupParameter parameter,
                            @Nullable String preferredPeer) throws CommunicationException {

        if (preferredPeer != null) {
            HighAvailabilityStatus haStatus = createHAStatus(parameter, preferredPeer);
            session.setParameter(parameter.getName(), haStatus);
        }

        preProcess(session, request, parameter, preferredPeer);
        groupFactory.getInstance(parameter).sendClientInitiatedRequest(session, request, new ClientApplicationRequestResponseListener(getSessionReleaseIndicator(), new NetVertexBaseClientResponseListener(request)));

    }

    private HighAvailabilityStatus createHAStatus(DiameterPeerGroupParameter parameter, @Nullable String preferredPeer) throws CommunicationException {
        final PeerData preferredPeerData = context.getStackContext().getPeerData(preferredPeer);

        if (parameter.getPeers().containsKey(preferredPeerData.getPeerName())) {
            return new HighAvailabilityStatus(preferredPeerData.getHostIdentity(), preferredPeerData.getSecondaryPeerName(), preferredPeerData.getHostIdentity());
        } else {
            for (String key : parameter.getPeers().keySet()) {
                final PeerData primaryPeerData = context.getStackContext().getPeerData(key);
                if (preferredPeerData == context.getStackContext().getPeerData(primaryPeerData.getSecondaryPeerName())) {
                    return new HighAvailabilityStatus(primaryPeerData.getHostIdentity(), preferredPeerData.getHostIdentity(), preferredPeerData.getHostIdentity());
                }
            }
        }

        throw new CommunicationException("PreferredPeer is not part of group:" + parameter.getName());
    }

    @Override
    public void sendAnswer(DiameterSession session, DiameterRequest request, DiameterAnswer answer) throws CommunicationException {
        sendDiameterAnswer(session, request, answer);
    }

    private static class AuthApplication implements ApplicationEnum {


        private long vendorId;
        private long applicationId;
        private Application application;

        public AuthApplication(long vendorId, long applicationId, Application application) {
            this.vendorId = vendorId;
            this.applicationId = applicationId;
            this.application = application;
        }

        @Override
        public long getVendorId() {
            return vendorId;
        }

        @Override
        public ServiceTypes getApplicationType() {
            return ServiceTypes.AUTH;
        }

        @Override
        public long getApplicationId() {
            return applicationId;
        }

        @Override
        public Application getApplication() {
            return application;
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append(getVendorId())
                    .append(":")
                    .append(getApplicationId())
                    .append(" [").append(getApplication().getDisplayName()).append("]").toString();
        }
    }


}
