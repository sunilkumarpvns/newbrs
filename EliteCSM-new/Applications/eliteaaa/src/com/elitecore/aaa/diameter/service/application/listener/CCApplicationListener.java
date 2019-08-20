package com.elitecore.aaa.diameter.service.application.listener;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.aaa.diameter.policies.applicationpolicy.CCAppPolicy;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.DiameterCCServiceConfigurable;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.session.CCAppSession;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.diameterapi.core.common.fsm.IStateTransitionData;
import com.elitecore.diameterapi.core.common.fsm.IStateTransitionDataCode;
import com.elitecore.diameterapi.core.common.fsm.StateEvent;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerEvent;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.PeerDataCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.stack.AppListenerInitializationFaildException;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;
import com.elitecore.diameterapi.diameter.stack.application.sessionrelease.AppDefaultSessionReleaseIndicator;
import com.elitecore.diameterapi.diameter.stack.cc.fsm.BaseCCStateMachine;
import com.elitecore.diameterapi.diameter.stack.cc.session.BaseCCAppSession;

public class CCApplicationListener extends AAAServerApplication {
	
	private static final String MODULE = "CC-APP-LSTNR";

	private boolean isInitialized;
	private DiameterCCServiceConfigurable diameterCCServiceConfiguration;
	private SessionManagementHandler<ApplicationRequest, ApplicationResponse> sessionManagementHandler;
	private final DiameterSessionManager sessionManager;

	public CCApplicationListener(DiameterServiceContext serviceContext,IStackContext stackContext,
			DiameterCCServiceConfigurable diameterCCServiceConfiguration,
			DiameterSessionManager sessionManager, SessionFactoryManager sessionFactoryManager) {
		super(stackContext, serviceContext, sessionFactoryManager, diameterCCServiceConfiguration.getSupportedApplication());
		this.diameterCCServiceConfiguration = diameterCCServiceConfiguration;
		this.sessionManager = sessionManager;
	}

	@Override
	public void init() throws AppListenerInitializationFaildException {
		if(!isInitialized) {
			super.init();
			sessionManagementHandler = new SessionManagementHandler<ApplicationRequest, ApplicationResponse>(sessionManager, getSessionReleaseIndicator());
			registerPlugins(getServiceContext().getServerContext().getDiameterPluginManager().getNameToPluginMap(), diameterCCServiceConfiguration.getInPlugins(), diameterCCServiceConfiguration.getOutPlugins());
			LogManager.getLogger().info(MODULE, "Application Successfully initialized.");
			isInitialized = true;
		}
	}
	

	@Override
	protected void handleApplicationRequest(DiameterSession session, ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse) {
		
		
		BaseCCStateMachine machine = null;
		BaseCCAppSession ccAppSession = (BaseCCAppSession)session.getAppSession();
		
		if(ccAppSession== null){			
			ccAppSession = createCCAppSession(session.getSessionId());
			machine = createCCStateMachine(ccAppSession);
			ccAppSession.setStateMachine(machine);
			session.addAppSession(ccAppSession);
		}		
		machine = (BaseCCStateMachine)ccAppSession.getStateMachine();
		if(machine == null){
			machine = createCCStateMachine(ccAppSession);
			ccAppSession.setStateMachine(machine);
		}
		
		IStateTransitionData stateTransitionData = new IStateTransitionData() {
			Map<IStateTransitionDataCode, Object> data = new HashMap<>();
			public Object getData(IStateTransitionDataCode key) {
				return data.get(key);
			}
			
			public void addObject(IStateTransitionDataCode key, Object value) {
				data.put(key, value);
			}
		};
		
		stateTransitionData.addObject(PeerDataCode.DIAMETER_RECEIVED_PACKET, applicationRequest.getDiameterRequest());
		stateTransitionData.addObject(PeerDataCode.USER_SESSION,session);
		machine.onStateTransitionTrigger(stateTransitionData);
		
		// Allow CC state machine to decide when to send answer and stop template from sending answer
		applicationResponse.setProcessingCompleted(false);
	}
	
	public class EliteCCStateMachine extends BaseCCStateMachine{
		public EliteCCStateMachine(BaseCCAppSession session) {
			super(session);
		}
		
		private DiameterAnswer handleCCRequest(Session session, DiameterRequest request){
			
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Processing CC Application request.");
			}
		
			ApplicationRequest applicationRequest = new ApplicationRequest(request);			
			ApplicationResponse applicationResponse = new ApplicationResponse(request);
			
			CCAppPolicy applicationPolicy = (CCAppPolicy) getServiceContext().selectServicePolicy(applicationRequest);
			if (applicationPolicy == null) {
				LogManager.getLogger().warn( MODULE, "No policy satisfied for the request");
				
				DiameterProcessHelper.rejectResponse(applicationResponse, ResultCode.DIAMETER_RATING_FAILED, DiameterErrorMessageConstants.NO_POLICY_SATISFIED);
				
				return applicationResponse.getDiameterAnswer();
			}
			
			if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info( MODULE, "Applying policy: " 
							+ applicationPolicy.getPolicyName());
				}
			applicationRequest.setApplicationPolicy(applicationPolicy);
			
			applicationPolicy.handleRequest(applicationRequest, applicationResponse, (DiameterSession)session);
			
			if (applicationResponse.isMarkedForDropRequest() == false 
					&& applicationResponse.isProcessingCompleted()
					&& applicationResponse.isFurtherProcessingRequired()) {

				if (applicationPolicy.isSessionManagementEnabled()) {
					sessionManagementHandler.handleSessionManagement(applicationRequest, applicationResponse, 
							fetchSessionStatus(applicationRequest.getDiameterRequest()));
				}
			}
			return applicationResponse.getDiameterAnswer();
		}
		
		@Override
		public DiameterAnswer handleEventRequest(Session session, DiameterRequest request) {			
			return handleCCRequest(session,request); 
		}
		
		@Override
		public DiameterAnswer handleInitialRequest(Session session, DiameterRequest request) {
			return handleCCRequest(session,request);			
		}

		@Override
		public DiameterAnswer handleTerminationRequest(Session session, DiameterRequest request) {			
			return handleCCRequest(session,request);		
		}

		@Override
		public DiameterAnswer handleUpdateRequest(Session session, DiameterRequest request) {			
			return handleCCRequest(session,request);
		}
		
		@Override
		public final void sendAbortSessionRequest(StateEvent event) {
			DiameterSession session = (DiameterSession) event.getStateTransitionData().getData(PeerDataCode.USER_SESSION);
			DiameterRequest request = new DiameterRequest();
			request.setApplicationID(getApplicationEnum()[0].getApplicationId());
			
			IDiameterAVP diameterAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SESSION_ID);
			diameterAvp.setStringValue(session.getSessionId());
			request.addAvp(diameterAvp);
			
			diameterAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.DESTINATION_HOST);
			diameterAvp.setStringValue(peerListener.getHostIdentity());
			request.addAvp(diameterAvp);

			diameterAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.DESTINATION_REALM);
			diameterAvp.setStringValue(peerListener.getRealm());
			request.addAvp(diameterAvp);
			
			DiameterPeerCommunicator peerCommunicator = stackContext.getPeerCommunicator(peerListener.getHostIdentity());
			try {
				peerCommunicator.sendServerInitiatedRequest(session, request, ResponseListener.NO_RESPONSE_LISTENER);
			} catch (CommunicationException e) {
				LogManager.getLogger().warn(MODULE, "Unable to send ASR to peer: " + peerListener.getHostIdentity()
						+ " for session-id: " + session.getSessionId() + " due to reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
			
		}
		
		@Override
		public final void sendCcAnswer(StateEvent event) {
			DiameterRequest request = (DiameterRequest)event.getStateTransitionData().getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);
			DiameterAnswer diameterAnswer = (DiameterAnswer)event.getStateTransitionData().getData(PeerDataCode.DIAMETER_PACKET_TO_SEND);
			try {
				CCApplicationListener.this.sendDiameterAnswer((DiameterSession)event.getStateTransitionData().getData(PeerDataCode.USER_SESSION), 
						request, diameterAnswer);
			} catch (CommunicationException e) {
				LogManager.getLogger().error(MODULE, "Unable to send CCA to peer: " + peerListener.getHostIdentity() 
					+ " for request with HbH-ID=" 
					+ request.getHop_by_hopIdentifier() + "EtE-ID=" 
					+ request.getEnd_to_endIdentifier() + ", Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}

		@Override
		public final void sendErrorMessage(StateEvent event) {
			DiameterRequest request = (DiameterRequest)event.getStateTransitionData().getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);
			 
			DiameterAnswer answer = (DiameterAnswer)event.getStateTransitionData().getData(PeerDataCode.DIAMETER_PACKET_TO_SEND);
			if(answer == null){
				answer = new DiameterAnswer(request);
				event.getStateTransitionData().addObject(PeerDataCode.DIAMETER_PACKET_TO_SEND,answer);
			}
			IDiameterAVP resultCodeAvp = answer.getAVP(DiameterAVPConstants.RESULT_CODE);
			if(resultCodeAvp == null){
				resultCodeAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
				answer.addAvp(resultCodeAvp);		
			}
			resultCodeAvp.setInteger(ResultCode.DIAMETER_UNABLE_TO_COMPLY.code);
			event.getStateTransitionData().addObject(PeerDataCode.PEER_EVENT,DiameterPeerEvent.SendMessage);
			
			try {
				CCApplicationListener.this.sendDiameterAnswer((DiameterSession)event.getStateTransitionData().getData(PeerDataCode.USER_SESSION),
						request, answer);
			} catch (CommunicationException e) {
				LogManager.getLogger().error(MODULE, "Unable to send CCA to peer: " + peerListener.getHostIdentity() 
					+ " for request with HbH-ID=" 
					+ request.getHop_by_hopIdentifier() + "EtE-ID=" 
					+ request.getEnd_to_endIdentifier() + ", Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
						
		}

		private String fetchSessionStatus(DiameterRequest diameterRequest) {
			int commandCode = diameterRequest.getCommandCode();
			if (CommandCode.CREDIT_CONTROL.code == commandCode) {
				IDiameterAVP avp = diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE, true);
				if (avp != null) {
					if (avp.getInteger() == DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST){
						return DiameterAttributeValueConstants.EC_SESSON_STATUS_DELETED;
					} else {
						return DiameterAttributeValueConstants.EC_SESSON_STATUS_ACTIVE;
					}
				}
			}
			return null;
		}

		@Override
		protected String getPeerName() {
			return peerListener.getPeerName();
		}

	}

	private BaseCCStateMachine createCCStateMachine(BaseCCAppSession session) {		
		return new EliteCCStateMachine(session);
	}

	private BaseCCAppSession createCCAppSession(String sessionId) {		
		return new CCAppSession(sessionId);
	}
	
	@Override
	public String getApplicationIdentifier() {
		return "CCApplication";
	}



	@Override
	protected SessionReleaseIndiactor createSessionReleaseIndicator(ApplicationEnum applicationEnum) {
		return new AppDefaultSessionReleaseIndicator();
	}

	@Override
	protected void resumeApplicationRequest(DiameterSession session, ApplicationRequest request,
			ApplicationResponse response) {
		throw new UnsupportedOperationException("Resumption is not supported.");
	}

	@Override
	protected void finalPreResponseProcessing(ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse, ISession session) {

		//No need to process
	}
		
}
