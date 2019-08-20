package com.elitecore.netvertex.gateway.diameter.application;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.session.ServerInitiatedRequestResponseListener;
import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.stack.application.ServerApplication;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.mibs.constants.ServiceTypes;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.handler.ApplicationHandler;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.scripts.DiameterGroovyScript;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.ThreadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public abstract class NetvertexBaseServerApplication extends ServerApplication implements NetvertexApplication {
    private static final String MODULE = "BASE-DIA-SERV-APP";
    protected DiameterGatewayControllerContext context;

    public NetvertexBaseServerApplication(DiameterGatewayControllerContext context, final long vendorId, final long applicationId, final Application application) {
        super(context.getStackContext(), new NetvertexBaseServerApplicationEnum(vendorId, applicationId, application));
        this.context = context;
    }

    /*
     * Any Change related to this method must be done in same method provided in NetvertexBaseClientApplication
     */
    protected final void applyScriptsForReceivedPacket(DiameterPacket diameterPacket, String gatewayName) {

        List<DiameterGroovyScript> scripts = context.getDiameterGroovyScripts(gatewayName);
        if (CollectionUtils.isEmpty(scripts)) {
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

    @Override
    public void preProcess(Session session, DiameterRequest diameterRequest) {
        super.preProcess(session, diameterRequest);
        addContextInformation(diameterRequest);

    }

    protected void addContextInformation(DiameterRequest diameterRequest) {
        addSubscriptionInfomationInContext(diameterRequest);
        addFramedIp(diameterRequest);
        addRequestType(diameterRequest);
    }

    private void addRequestType(DiameterRequest diameterRequest) {
        String requestType = diameterRequest.getAVPValue(DiameterAVPConstants.CC_REQUEST_TYPE);
        if(Objects.nonNull(requestType)) {
            ThreadContext.put(PCRFKeyConstants.REQUEST_TYPE.val,requestType);
        }
    }

    private void addFramedIp(DiameterRequest diameterRequest) {
        String framedIp = diameterRequest.getAVPValue(DiameterAVPConstants.FRAMED_IP_ADDRESS);
        if(Objects.nonNull(framedIp)) {
            ThreadContext.put(PCRFKeyConstants.CS_SESSION_IPV4.val,framedIp);
        }

        String framedIpV6 = diameterRequest.getAVPValue(DiameterAVPConstants.FRAMED_IPV6_PREFIX);
        if(Objects.nonNull(framedIp)) {
            ThreadContext.put(PCRFKeyConstants.CS_SESSION_IPV6.val,framedIpV6);
        }

    }

    private void addSubscriptionInfomationInContext(DiameterRequest diameterRequest) {
        ArrayList<IDiameterAVP> subscriberIDAvps = diameterRequest.getAVPList(DiameterAVPConstants.SUBSCRIPTION_ID);
        if(CollectionUtils.isEmpty(subscriberIDAvps)) {
            return;
        }

        for(int index= 0; subscriberIDAvps.size() > index; index++) {
            AvpGrouped avpGrouped = (AvpGrouped) subscriberIDAvps.get(index);
            IDiameterAVP subscriptionType = avpGrouped.getSubAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
            IDiameterAVP subscriptionValue = avpGrouped.getSubAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
            if(Objects.isNull(subscriptionType) || Objects.isNull(subscriptionValue)) {
                continue;
            }

            long type = subscriptionType.getInteger();
            String pcrfKey;
            if(type == DiameterAttributeValueConstants.DIAMETER_END_USER_E164) {
                pcrfKey = PCRFKeyConstants.SUB_EUI64.val;
            } else if(type == DiameterAttributeValueConstants.DIAMETER_END_USER_IMSI) {
                pcrfKey = PCRFKeyConstants.SUB_IMSI.val;
            } else if(type == DiameterAttributeValueConstants.DIAMETER_END_USER_SIP_URI) {
                pcrfKey = PCRFKeyConstants.SUB_SIP_URL.val;
            } else if(type == DiameterAttributeValueConstants.DIAMETER_END_USER_NAI) {
                pcrfKey = PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_NAI.val;
            } else {
                pcrfKey = subscriptionType.getLogValue();
            }

            ThreadContext.put(pcrfKey,subscriptionValue.getStringValue());
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

    public void preProcess(DiameterRequest diameterPacket) {
        preProcessInternal(diameterPacket);
    }

    private void preProcessInternal(DiameterPacket diameterPacket) {


        String sessionID = diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID);

        DiameterGatewayConfiguration configuration = context.getGatewayConfigurationByName(diameterPacket.getInfoAVPValue(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME));
        if (configuration == null) {
            LogManager.getLogger().error(MODULE, "Unable to execute scripts for Diameter-Packet with Session-ID= " + sessionID + ". Reason: Gateway Configuration not found");
            return;
        }

        List<DiameterGroovyScript> scripts = context.getDiameterGroovyScripts(configuration.getName());
        if (CollectionUtils.isEmpty(scripts)) {
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

    private class ServerApplicationResponseListener implements ResponseListener {

        private final DiameterRequest diameterRequest;

        public ServerApplicationResponseListener(DiameterRequest diameterRequest) {
            super();
            this.diameterRequest = diameterRequest;
        }

        @Override
        public void requestTimedout(String hostIdentity, DiameterSession session) {

            if (getLogger().isLogLevel(LogLevel.INFO))
                getLogger().info(MODULE, "Timeout Request: " + diameterRequest);

            DiameterGatewayConfiguration gatewayConfiguration = context.getGatewayConfigurationByName(diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME));
            if (gatewayConfiguration == null) {
                gatewayConfiguration = context.getGatewayConfigurationByHostId(hostIdentity);
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
                            String preferredPeer) throws CommunicationException {

        preProcess(request);

        if (preferredPeer == null) {
            //FIXME is It required to fetch origin host form session
            throw new CommunicationException("Preferred peer not provided");
        } else {
            sendToPreferredPeer(session, request, preferredPeer);
        }

    }

    private void sendToPreferredPeer(DiameterSession session, DiameterRequest diameterRequest, String preferredPeer) throws CommunicationException {
        final DiameterPeerCommunicator peerCommunicator = context.getStackContext().getPeerCommunicator(preferredPeer);

        if (peerCommunicator == null) {
            throw new CommunicationException("Preferred peer(" + preferredPeer + ") not found");
        }

        peerCommunicator.sendServerInitiatedRequest(session, diameterRequest, new ServerInitiatedRequestResponseListener(new ServerApplicationResponseListener(diameterRequest)));
    }

    @Override
    public void sendAnswer(DiameterSession session, DiameterRequest request, DiameterAnswer answer) throws CommunicationException {
        sendDiameterAnswer(session, request, answer);
    }

    private static class NetvertexBaseServerApplicationEnum implements ApplicationEnum {
        private final long vendorId;
        private final long applicationId;
        private final Application application;

        public NetvertexBaseServerApplicationEnum(long vendorId, long applicationId, Application application) {

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
