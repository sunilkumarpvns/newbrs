package com.elitecore.netvertex.gateway.diameter.application;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupFactory;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionFactoryType;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.stack.AppListenerInitializationFaildException;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;
import com.elitecore.diameterapi.diameter.stack.application.sessionrelease.AppDefaultSessionReleaseIndicator;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.application.handler.ApplicationHandler;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFResponseListner;

import javax.annotation.Nullable;
import java.util.Arrays;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class SyApplication extends NetvertexBaseClientApplication {

	private static final String MODULE = "SY-APP";
	public static final String SESSION_KEY_FOR_COUNTER = "policycounter";


	private boolean isInitialized;
	
	public SyApplication(DiameterPeerGroupFactory groupFactory,DiameterGatewayControllerContext context) {
		this(groupFactory, context,ApplicationIdentifier.TGPP_SY.getVendorId(), ApplicationIdentifier.TGPP_SY.getApplicationId());
	}
	public SyApplication(DiameterPeerGroupFactory groupFactory,DiameterGatewayControllerContext context, long vendorId, long applicationId) {		
		super(groupFactory, context, vendorId, applicationId, Application.TGPP_SY);	
	}
	
	@Override
	public void init() throws AppListenerInitializationFaildException {
		if(!isInitialized) {
			super.init();
			try {
				context.registerSessionFactoryType(ApplicationIdentifier.TGPP_SY.getApplicationId(),
						SessionFactoryType.INMEMORY);
			} catch (InitializationFailedException e) {
				throw new AppListenerInitializationFaildException(e);
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Sy Application("+ Arrays.toString(getApplicationEnum()) +") initialized successfully");
			}
			
			isInitialized = true;
		}
	}
	
	@Override
	protected SessionReleaseIndiactor createSessionReleaseIndicator(ApplicationEnum applicationEnum) {
		return new AppDefaultSessionReleaseIndicator();
	}
	
	public void sendRequest(DiameterRequest diameterRequest,
							PCRFResponse pcrfResponse,
							DiameterPeerGroupParameter diameterPeerGroup,
							@Nullable String peerName,
							PCRFResponseListner responseListener) throws CommunicationException{
		
		int commandCode = diameterRequest.getCommandCode();
		DiameterSession session = null;
		
		String sySessionId = pcrfResponse.getAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val);
		
		if (commandCode == CommandCode.SPENDING_LIMIT.code) {
			if(sySessionId == null){
				if(getLogger().isLogLevel(LogLevel.DEBUG))
					getLogger().debug(MODULE, "Create new Sy session for group: " + diameterPeerGroup.getName() + (peerName != null ? " with Preferred Peer: " + peerName : ""));
				
				session = (DiameterSession) context.getStackContext().generateSession(diameterRequest.getApplicationID());

				sySessionId = session.getSessionId();
			} else {
				session = (DiameterSession) context.getStackContext().getOrCreateSession(sySessionId, diameterRequest.getApplicationID());
			}
			
		} else if(commandCode == CommandCode.SESSION_TERMINATION.code) {

			if(sySessionId == null){
				throw new CommunicationException("Sy session-ID not found in PCRFResponse");
			}
			
			session = (DiameterSession) context.getStackContext().getOrCreateSession(sySessionId, diameterRequest.getApplicationID());
			
		} else {
			throw new CommunicationException("Unsupported PacketType:" +commandCode);
		}
		
		
		

		//add session-Id
		IDiameterAVP diameterAvp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
		diameterAvp.setStringValue(sySessionId);
		diameterRequest.addAvp(diameterAvp);

		
		if(SessionTypeConstant.RX.val.equals(pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val)) == false){
			session.setParameter(PCRFKeyConstants.CS_CORESESSION_ID.val, pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));

		}

		if(responseListener != null){
			session.setParameter(String.valueOf(diameterRequest.getHop_by_hopIdentifier()),responseListener);
		}

		sendRequest(session, diameterRequest, diameterPeerGroup, peerName);

	}


	@Override
	public String getApplicationIdentifier() {
		return "SyApplication";
	}

	@Override
	protected void processApplicationRequest(Session session, DiameterRequest diameterRequest) {
		super.processApplicationRequest(session, diameterRequest);
		if(getLogger().isLogLevel(LogLevel.INFO))
			getLogger().info(MODULE, "Received Sy Request: " + diameterRequest);
		
		DiameterGatewayConfiguration gatewayConfiguration = context.getGatewayConfiguration(diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_ORIGINATOR_PEER_NAME) ,
				diameterRequest.getInfoAVPValue(DiameterAVPConstants.EC_PROXY_AGENT_NAME));
		
		if(gatewayConfiguration == null){
			getLogger().error(MODULE, "Unable to process Sy request. Reason: Configuration not found");
			return;
		}
		
		applyScriptsForReceivedPacket(diameterRequest, gatewayConfiguration.getName());
		
		SupportedStandard supportedStandard = gatewayConfiguration.getSupportedStandard(); 
		
		ApplicationHandler syAppHandler = context.getApplicationHandler(Application.TGPP_SY, supportedStandard);
		
		if(syAppHandler == null){
			getLogger().error(MODULE, "Unable to process Sy request. Reason : No Application Handler found for Sy Application");
			return;
		} 
		
		syAppHandler.handleReceivedRequest(session, diameterRequest);
	}
	
	private class SyResponseListener implements ResponseListener{
		
		private final DiameterRequest diameterRequest;
		private final DiameterGatewayConfiguration gatewayConfiguration;  

		public SyResponseListener(DiameterRequest diameterRequest,
				DiameterGatewayConfiguration gatewayConfiguration) {
			this.diameterRequest = diameterRequest;
			this.gatewayConfiguration = gatewayConfiguration;
		}

		@Override
		public void requestTimedout(String hostIdentity, DiameterSession session) {
			if(getLogger().isLogLevel(LogLevel.INFO))
				getLogger().info(MODULE, "Timeout Sy Request: " + diameterRequest);
			
			SupportedStandard supportedStandard = gatewayConfiguration.getSupportedStandard(); 
			
			ApplicationHandler syAppHandler = context.getApplicationHandler(Application.TGPP_SY, supportedStandard);
			
			if (syAppHandler == null) {
				getLogger().error(MODULE, "Unable to process Timeout Sy request. Reason : No Application Handler found for Sy Application");
				return;
			} 
			
			syAppHandler.handleTimeoutRequest(session, diameterRequest);
		}

		@Override
		public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession session) {

			if(getLogger().isLogLevel(LogLevel.INFO))
				getLogger().info(MODULE, "Received Sy Response: " + diameterAnswer);
			
			applyScriptsForReceivedPacket(diameterAnswer, gatewayConfiguration.getName());
			
			SupportedStandard supportedStandard = gatewayConfiguration.getSupportedStandard(); 
			
			ApplicationHandler syAppHandler = context.getApplicationHandler(Application.TGPP_SY, supportedStandard);
			
			if(syAppHandler == null){
				getLogger().error(MODULE, "Unable to process Sy response. Reason : No Application Handler found for Sy Application");
				return;
			} 
			syAppHandler.handleReceivedResponse(session, diameterRequest, diameterAnswer);
		}
		
	}


}
