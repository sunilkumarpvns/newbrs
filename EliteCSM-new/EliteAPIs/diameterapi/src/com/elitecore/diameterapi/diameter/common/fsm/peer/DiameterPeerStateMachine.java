package com.elitecore.diameterapi.diameter.common.fsm.peer;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.commons.util.MalformedNAIException;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseSingleExecutionAsyncTask;
import com.elitecore.diameterapi.core.common.fsm.BaseStateMachineContext;
import com.elitecore.diameterapi.core.common.fsm.IStateMachineContext;
import com.elitecore.diameterapi.core.common.fsm.IStateMachineListener;
import com.elitecore.diameterapi.core.common.fsm.IStateTransitionData;
import com.elitecore.diameterapi.core.common.fsm.IStateTransitionDataCode;
import com.elitecore.diameterapi.core.common.fsm.State;
import com.elitecore.diameterapi.core.common.fsm.StateEvent;
import com.elitecore.diameterapi.core.common.fsm.StateMachine;
import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;
import com.elitecore.diameterapi.core.common.fsm.enums.StateEnum;
import com.elitecore.diameterapi.core.common.fsm.exception.UnhandledTransitionException;
import com.elitecore.diameterapi.core.common.peer.IPeerListener;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.router.exception.RoutingFailedException;
import com.elitecore.diameterapi.core.common.session.SessionsFactory;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.Stack;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.diameter.common.explicitrouting.ExplicitRoutingHandler;
import com.elitecore.diameterapi.diameter.common.explicitrouting.exception.ExplicitRoutingFailedException;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerEvent;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.fsm.peer.states.PeerStateClosed;
import com.elitecore.diameterapi.diameter.common.fsm.peer.states.PeerStateClosing;
import com.elitecore.diameterapi.diameter.common.fsm.peer.states.PeerStateIOpen;
import com.elitecore.diameterapi.diameter.common.fsm.peer.states.PeerStateROpen;
import com.elitecore.diameterapi.diameter.common.fsm.peer.states.PeerStateWaitConnAck;
import com.elitecore.diameterapi.diameter.common.fsm.peer.states.PeerStateWaitConnAckElect;
import com.elitecore.diameterapi.diameter.common.fsm.peer.states.PeerStateWaitICEA;
import com.elitecore.diameterapi.diameter.common.fsm.peer.states.PeerStateWaitReturn;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeer;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeer.OverloadHandler;
import com.elitecore.diameterapi.diameter.common.routerx.DiameterRouter;
import com.elitecore.diameterapi.diameter.common.session.DiameterAppMessageHandler;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.session.UnsupportedApplicationException;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.InbandSecurityId;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.CommonConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DisconnectionCause;
import com.elitecore.diameterapi.diameter.common.util.constant.PeerDataCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.EndToEndPool;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.HopByHopPool;
import com.elitecore.diameterapi.diameter.stack.DuplicateDetectionHandler;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;
import com.elitecore.diameterapi.diameter.stack.application.sessionrelease.SessionReleaseIndicatorFactory;

public abstract class DiameterPeerStateMachine extends StateMachine implements
		IPeerAtomicActionsExecutor {

	private static final String MODULE = "DIAMETER-STATE-MACHINE";
	protected static final char[] padding = new char[512];
	private AtomicLong numberOfConnectionAttempts =  new AtomicLong(0);
	private SessionFactoryManager sessionFactoryManager;
	private DiameterPeer peer;
	private IDiameterStackContext stackContext;
	private DiameterRouter diameterRouter;
	private DiameterAppMessageHandler appMessageHandler;
	private ExplicitRoutingHandler explicitRoutingHandler;
	private OverloadHandler overloadHandler;
	
	private int originStateId = -1;
	private DuplicateDetectionHandler duplicateMessageHandler;
	private SessionReleaseIndiactor sessionReleaseIndiactor;
	
	private final @Nonnull TimeSource timesource;
	private volatile DuplicateConnectionPolicy duplicateConnectionPolicy;
	
	static {
		Arrays.fill(padding, (char)0x80);
	}
	
	protected DiameterPeerStateMachine(DiameterPeer peer, DiameterRouter diameterRouter, SessionFactoryManager sessionFactoryManager, DiameterAppMessageHandler appMessageHandler, IDiameterStackContext stackContext, ExplicitRoutingHandler explicitRoutingHandler, OverloadHandler overloadHandler, DuplicateDetectionHandler duplicateMessageHandler, @Nonnull TimeSource timesource) {
		this(peer, stackContext, diameterRouter, sessionFactoryManager, appMessageHandler, StateEnum.UNKNOWN, explicitRoutingHandler, overloadHandler, duplicateMessageHandler, timesource);
	}

	public DiameterPeerStateMachine(DiameterPeer peer, 
			IDiameterStackContext stackContext, 
			DiameterRouter diameterRouter, 
			SessionFactoryManager sessionFactoryManager, 
			DiameterAppMessageHandler appMessageHandler, 
			IStateEnum stateEnum, 
			ExplicitRoutingHandler explicitRoutingHandler,
			OverloadHandler overloadHandler, 
			DuplicateDetectionHandler duplicateMessageHandler, @Nonnull TimeSource timesource) {
		super(stateEnum);
		this.peer = peer;
		this.stackContext = stackContext;
		this.diameterRouter = diameterRouter;
		this.appMessageHandler = appMessageHandler;
		this.explicitRoutingHandler = explicitRoutingHandler;
		this.overloadHandler = overloadHandler;
		this.duplicateMessageHandler = duplicateMessageHandler;
		this.timesource = timesource;
		this.sessionReleaseIndiactor = SessionReleaseIndicatorFactory.getDefaultSessionReleaseIndiactor();
		this.duplicateConnectionPolicy = createDuplicateConnectionPolicy();
		this.sessionFactoryManager = sessionFactoryManager;
	}

	private DuplicateConnectionPolicy createDuplicateConnectionPolicy() {
		switch (peer.getPeerData().getDuplicateConnectionPolicyType()) {
		case DISCARD_OLD:
			return new DiscardOldPolicy();
		case DEFAULT:
		default:
			return new DefaultPolicy();
		}
	}

	@Override
	protected StateEvent createStateEvent(IStateTransitionData transitionData) {

		StateEvent stateEvent = null;

		DiameterPeerEvent peerEvent = (DiameterPeerEvent) transitionData.getData(PeerDataCode.PEER_EVENT);

		DiameterPacket diameterPacket = (DiameterPacket) transitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);

		if (peerEvent != null) {
			DiameterPeerState nextState = (DiameterPeerState) currentState.getNextState(peerEvent);
			if (nextState != null) {
				stateEvent = new StateEvent(currentState, peerEvent, nextState,transitionData);
				return stateEvent;
			}
		} else if (diameterPacket != null) {
			try {
				stateEvent = fetchCurrentState().getStateEvent(transitionData);
			} catch (Exception e) {
				LogManager.getLogger().trace(MODULE, e);
			}
			if (stateEvent != null) {
				return stateEvent;
			}
		}
		if (LogManager.getLogger().isWarnLogLevel()) {
			LogManager.getLogger().warn(MODULE, "Peer: " + getPeerName() + 
					", Event Can't decided..." + currentState + 
						" : " + peerEvent);
		}
		return null;
	}
	@Override
	public boolean stop() {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Peer: " + getPeerName() + ", Shutdown is called, generating Stop event.");
		}
		IStateTransitionData transitionData = new IStateTransitionData() {
			Map<IStateTransitionDataCode, Object> data = new HashMap<IStateTransitionDataCode, Object>();
			public Object getData(IStateTransitionDataCode key) {
				return data.get(key);
			}
			
			public void addObject(IStateTransitionDataCode key, Object value) {
				data.put(key, value);
			}
		};
		transitionData.addObject(PeerDataCode.PEER_EVENT,  DiameterPeerEvent.Stop);
		try{
			this.onStateTransitionTrigger(transitionData);
		}catch(UnhandledTransitionException e){
		}
		return true;
	}
	public void atomicActionCleanup(StateEvent event, ConnectionEvents connEvent) {
		if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
			LogManager.getLogger().warn(MODULE, "Received NAck for "+ peer.getPeerName() +", cleanup done");
		peer.closeConnection(connEvent);
	}

	public void atomicActionError(StateEvent event, ConnectionEvents connEvent) {
		peer.closeConnection(connEvent);
	}

	public void atomicActionIDisc(StateEvent event) {
		if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
			LogManager.getLogger().warn(MODULE, "Closing connection with the peer " + peer.getPeerName());
	}

	/**
	 * 
	 */
	public void atomicActionProcess(StateEvent event) {
		IStateTransitionData stateTransitionData = event.getStateTransitionData();
		DiameterPacket diameterPacket = (DiameterPacket) stateTransitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);
		DiameterSession diameterSession = null;
		SessionsFactory diameterSessionFactory = sessionFactoryManager.getSessionFactory(diameterPacket.getApplicationID());
		if (diameterSessionFactory.hasSession(diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID))) {
			diameterSession = (DiameterSession)diameterSessionFactory.getOrCreateSession(diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID));
		}
		
		if(diameterPacket.isRequest()){
			
			DiameterRequest diameterRequest = (DiameterRequest) diameterPacket;
			if (diameterSession == null) {
				if(stackContext.isOverLoad(diameterRequest)){
					overloadHandler.handle(diameterRequest);
					return;
				}
				
				diameterSession = (DiameterSession) diameterSessionFactory.getOrCreateSession(diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
			
			if (peer.getPeerData().isReTransmissionCompliant() && 
					duplicateMessageHandler.isDuplicate(diameterRequest)){
				
				DiameterAnswer diameterAnswer = duplicateMessageHandler.storeIfAbsent(diameterRequest);
				stackContext.updateDuplicatePacketStatistics(diameterRequest, peer.getHostIdentity());
				if(diameterAnswer != null) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Diameter Answer available for Duplicate Request, Responding Duplicate Request with HbH-ID=" + diameterAnswer.getHop_by_hopIdentifier());
					}
					peer.sendDiameterAnswer(diameterAnswer);
				}
				return;
			}
			
		} else {
			if (diameterSession == null) {
				// below log should be at warn level because creating new session when answer is received
				LogManager.getLogger().warn(MODULE, "Session-id=" + diameterPacket.getSessionID() + " for answer not found. Creating new.");
				diameterSession = (DiameterSession) diameterSessionFactory.getOrCreateSession(diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID));
			}
		}
		
		try{
			if(stackContext.isEREnabled())
				explicitRoutingHandler.handle(diameterPacket);
			
			if(diameterPacket.isRequest()) {				
				submitRequestToDiameterRouter(stateTransitionData,peer,diameterSession,diameterPacket.getAsDiameterRequest());
			} else {
				submitAnswerToDiameterRouter(stateTransitionData,peer, diameterPacket.getAsDiameterAnswer(), diameterSession);
			}
			diameterSession.update(ValueProvider.NO_VALUE_PROVIDER);
		}catch (MalformedNAIException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Peer: " + getPeerName() + ", " +e.getMessage()+", so skipping further local processing");
			ignoreTrace(e);
			DiameterAnswer answerPacket = new DiameterAnswer((DiameterRequest)diameterPacket, ResultCode.DIAMETER_INVALID_AVP_VALUE);
			DiameterUtility.addFailedAVP(answerPacket, diameterPacket.getAVP(DiameterAVPConstants.USER_NAME));
			peer.sendDiameterAnswer(answerPacket);
			if(sessionReleaseIndiactor.isEligible(answerPacket)) {
				diameterSession.release();
			}
		} catch (RoutingFailedException e) { 
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Peer: " + getPeerName() + ", " +e.getMessage()+", so skipping further local processing");
			ignoreTrace(e);
			DiameterAnswer answerPacket = new DiameterAnswer((DiameterRequest)diameterPacket, e.getResultCode());
			if (Strings.isNullOrBlank(e.getLocalizedMessage()) == false) {
				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, answerPacket, e.getLocalizedMessage());
			}
			peer.sendDiameterAnswer(answerPacket);
			RoutingActions routingAction = e.getRoutingAction();
			if(routingAction != null){
				stackContext.updateRealmOutputStatistics(answerPacket, 
						peer.getRealm(), routingAction);
			}
			if(sessionReleaseIndiactor.isEligible(answerPacket)) {
				diameterSession.release();
			}
		} catch (ExplicitRoutingFailedException e) { 
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "For Peer: " + getPeerName() + ", " +e.getMessage()+", so skipping further local processing");
			ignoreTrace(e);
			if(diameterPacket.isResponse())
				return;
			DiameterAnswer answerPacket = new DiameterAnswer((DiameterRequest)diameterPacket, e.getResultCode());
			peer.sendDiameterAnswer(answerPacket);
			if(sessionReleaseIndiactor.isEligible(answerPacket)) {
				diameterSession.release();
			}
		} catch (UnsupportedApplicationException e) { 
			ignoreTrace(e);
			if (diameterPacket.isResponse()) {
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "Dropping Response, Reason: Application: " + e.getUnsupportedApplicationId() + 
							" is not supported.");
				}
				stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, peer.getHostIdentity());
				return;
			}
			
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Application: " + e.getUnsupportedApplicationId() + 
						" is not supported. Sending " + ResultCode.DIAMETER_APPLICATION_UNSUPPORTED + " to Peer: " + getPeerName());
			}
			DiameterAnswer answerPacket = new DiameterAnswer((DiameterRequest)diameterPacket, ResultCode.DIAMETER_APPLICATION_UNSUPPORTED);
			peer.sendDiameterAnswer(answerPacket);
			stackContext.updateRealmOutputStatistics(answerPacket, 
					peer.getRealm(), RoutingActions.LOCAL);
			if(sessionReleaseIndiactor.isEligible(answerPacket)) {
				diameterSession.release();
			}
		} 
	}

	private void submitRequestToDiameterRouter(IStateTransitionData stateTransitionData,
			DiameterPeer peer, 
			DiameterSession diameterSession,
			DiameterRequest diameterRequest) throws MalformedNAIException, RoutingFailedException, UnsupportedApplicationException{
		RoutingActions action = diameterRouter.processDiameterRequest(diameterRequest, diameterSession);

		if (RoutingActions.LOCAL == action) {
			if (stackContext.isNAIEnabled() && isNAIRequest(diameterRequest)) {
				submitRequestToDiameterRouter(stateTransitionData, peer, diameterSession, diameterRequest);
			} else {
				stackContext.updateRealmInputStatistics(diameterRequest, 
						peer.getRealmName(), RoutingActions.LOCAL);
				appMessageHandler.handleReceivedRequest(diameterRequest, diameterSession);
			}
		}
	}
	
	private void submitAnswerToDiameterRouter(IStateTransitionData stateTransitionData,
			DiameterPeer peer, DiameterAnswer diameterAnswer,
			DiameterSession session){
		
		//TODO UPDATE INPUT STATISTICS IN RESPECTIVE LISTENER
		//stackContext.updateRealmInputStatistics(diameterAnswer, peer.getRealmName(), RoutingActions.LOCAL);
		ResponseListener responseListener = (ResponseListener) stateTransitionData.getData(PeerDataCode.RESPONSE_LISTENER);
		responseListener.responseReceived(diameterAnswer, peer.getHostIdentity(), session);
	}
	private boolean isNAIRequest(DiameterPacket diameterPacket) throws MalformedNAIException{
		boolean isNAIRequest=false;

		IDiameterAVP userName = diameterPacket.getAVP(DiameterAVPConstants.USER_NAME);
		IDiameterAVP destinationRealm = diameterPacket.getAVP(DiameterAVPConstants.DESTINATION_REALM);
		if(userName != null){
			String userNameStr = userName.getStringValue();
			if(DiameterUtility.isValidUserAccordingToABNF(userNameStr)){
				if(DiameterUtility.isNAIDecorated(userNameStr)){
					if(DiameterUtility.isValidForProxy(userNameStr)){
						String proxyRealm = DiameterUtility.getProxyRealm(userNameStr);
						if(DiameterUtility.isValidRealmAccordingToABNF(proxyRealm)){
							if(stackContext.isValidNAIRealm(proxyRealm)){
								isNAIRequest=true;
								String transformedNAI = DiameterUtility.transformNAI(userNameStr);
								if(destinationRealm==null){
									destinationRealm = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.DESTINATION_REALM);
									diameterPacket.addAvp(destinationRealm);
								}
								userName.setStringValue(transformedNAI);
								destinationRealm.setStringValue(proxyRealm);
								stripNAIDecoration(diameterPacket,userName);
							}
						}else {
							throw new MalformedNAIException("Improper Proxy-Realm :"+proxyRealm+ " is not according to RFC 4282");
						}
					}	
				}else {
					stripNAIDecoration(diameterPacket,userName);
				}
			}else {
				throw new MalformedNAIException("Improper UserName "+userNameStr+" is not according to RFC 4282");
			}	
		}
		return isNAIRequest;
	}

	private void stripNAIDecoration(DiameterPacket diameterPacket,IDiameterAVP userNameAvp) {
		IDiameterAVP naiDecorationAttr = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_NAI_DECORATION);
		if(naiDecorationAttr!=null){
			String userName = userNameAvp.getStringValue();
			int indexOfOpeningCurlyBrace = userName.indexOf('{');
			int indexOfClosingCurlyBrace = userName.indexOf('}');
			if(indexOfClosingCurlyBrace != -1 && indexOfOpeningCurlyBrace == 0 && indexOfClosingCurlyBrace < userName.length()-1 && indexOfClosingCurlyBrace-indexOfOpeningCurlyBrace > 1){
				userName = userName.substring(indexOfClosingCurlyBrace + 1);
				naiDecorationAttr.setStringValue(userName.substring(indexOfOpeningCurlyBrace+1,indexOfClosingCurlyBrace));
				userNameAvp.setStringValue(userName);
				diameterPacket.addInfoAvp(naiDecorationAttr);
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Elitecore-NAI-Decoration AVP not found in Dictionary. Skipping stripping of NAI Decoration");
			}
		}	
		
	}

	public ResultCode atomicActionProcessCEA(StateEvent event) {
		DiameterPacket receivedPacket = (DiameterPacket) event.getStateTransitionData().getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);
		int resultCode = ResultCode.DIAMETER_SUCCESS.getCode();

		IDiameterAVP resultCodeAVP = receivedPacket.getAVP(DiameterAVPConstants.RESULT_CODE);
		if (resultCodeAVP != null){
			resultCode = (int) resultCodeAVP.getInteger(); 
		} else {
			LogManager.getLogger().error(MODULE, "Result-Code AVP is not present in CEA. Considering " + ResultCode.fromCode(resultCode));
		}
		if(resultCode == ResultCode.DIAMETER_SUCCESS.code){
			setPeerInitConnection(true);
		}else if (resultCode > 2999){
			if(ResultCodeCategory.getResultCodeCategory(resultCode) == ResultCodeCategory.RC5XXX){
				if(peer.getPeerData().isInitConnection()){
					setPeerInitConnection(false);
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Initiate Connection with Peer: " + getPeerName() + 
						" disabled, Reason: CEA with Result-code= " + ResultCode.fromCode((resultCode)) + " received.");
					}
				}
			}
			LogManager.getLogger().error(MODULE, "Peer: " + getPeerName() + ", CEA received with Result code " + ResultCode.fromCode((resultCode)) + ". Closing connection.");
			peer.closeConnection(ConnectionEvents.CONNECTION_BREAK);
			Stack.generateAlert(StackAlertSeverity.CRITICAL, 
					DiameterStackAlerts.DIAMETER_PEER_DOWN, MODULE, 
					"CEA received with Result-code: " + ResultCode.fromCode((resultCode)) + ", current state: " + currentState.toString() + " for peer: " + peer.getPeerName(), 0, 
					peer.getPeerName() + "(" + ResultCode.fromCode((resultCode)) + ", " + currentState.toString() + ")");
			
			return ResultCode.fromCode(resultCode);
		}

		ArrayList<IDiameterAVP> hostAddressAvpList = receivedPacket.getAVPList(DiameterAVPConstants.HOST_IP_ADDRESS);
		if (hostAddressAvpList != null) {
			List<String>hostIpAddresses = new ArrayList<String>();
			for(IDiameterAVP hostAddressAvp : hostAddressAvpList){				
				hostIpAddresses.add(hostAddressAvp.getStringValue());
			}
			if(hostIpAddresses != null && hostIpAddresses.size() > 0){
				peer.setHostIPAddress(hostIpAddresses);
			}			
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Host not found");
		}
		
		ArrayList<IDiameterAVP> inbandSecurities = receivedPacket.getAVPList(DiameterAVPConstants.INBAND_SECURITY_ID);
		if (inbandSecurities != null && inbandSecurities.size() > 0) {
			for(IDiameterAVP inbandSecurity : inbandSecurities){
				InbandSecurityId inbandSecurityId = InbandSecurityId.fromCode((int) inbandSecurity.getInteger());
				peer.addRemoteSecurityId(inbandSecurityId);
			}
		}

		peer.setProductName(receivedPacket.getAVP(DiameterAVPConstants.PRODUCT_NAME).getStringValue());

		peer.setVendorId((int) receivedPacket.getAVP(DiameterAVPConstants.VENDOR_ID).getInteger());

		peer.addRemoteApplication(receivedPacket);
		
		ArrayList<IDiameterAVP> supportedVendorIds = receivedPacket.getAVPList(DiameterAVPConstants.SUPPORTED_VENDOR_ID);
		if (supportedVendorIds != null && supportedVendorIds.size() > 0) {
			for(IDiameterAVP supportedVendorId : supportedVendorIds){
				peer.addSupportedVendor(supportedVendorId.getInteger());
			}
		}

		String originHost = receivedPacket.getAVPValue(DiameterAVPConstants.ORIGIN_HOST);
		if (originHost != null){
			if (!originHost.equals(peer.getHostIdentity())){
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Origin Host Received in CEA does not matches for peer: " + peer.getPeerName());
			}
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Origin Host is not received in CEA for peer: " + peer.getPeerName());
		}


		return ResultCode.fromCode(resultCode);

	}

	/**
	 * 
	 * 1)get Origin host & isValidPeer ? String strOriginHost =
	 * reqPacket.getAVP(DiameterAVPConstants.ORIGIN_HOST).getOctetString();
	 * if(PeersManager.getInstance().isValidPeer(strOriginHost)) else return
	 * UNKNOWN_PEER
	 * 
	 * 2) setRealmName and add HostAddresses to peerData.
	 * peerData.setRealmName(reqPacket
	 * .getAVP(DiameterAVPConstants.ORIGIN_REALM).getOctetString());
	 * 
	 * 3) setProductName and VendorID
	 * 
	 * 4) then get list of auth applications and register it to peerData
	 * 
	 * 5) then get list of acct applications and register it to peerData
	 * 
	 * 6) then get list of Vendor Specific applications and register it to
	 * peerData
	 * 
	 * 7) then get list of CommonApplications and register it to peerData if not
	 * found then return ResultCode.DIAMETER_NO_COMMON_APPLICATION;
	 * 
	 * 8) return ResultCode.DIAMETER_SUCCESS;
	 */

	public ResultCode atomicActionProcessCER(StateEvent event) {
		IStateTransitionData stateTransitionData = event.getStateTransitionData();
		DiameterRequest diameterPacket = (DiameterRequest) stateTransitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);

		NetworkConnectionHandler connectionHandler = ((NetworkConnectionHandler) stateTransitionData.getData(PeerDataCode.CONNECTION));
		
		ArrayList<IDiameterAVP> hostAddressAvpList = diameterPacket.getAVPList(DiameterAVPConstants.HOST_IP_ADDRESS);
		List<String>hostIpAddresses = new ArrayList<String>();
		if (hostAddressAvpList != null) {
			for(IDiameterAVP hostAddressAvp : hostAddressAvpList){				
				hostIpAddresses.add(hostAddressAvp.getStringValue());
			}
			if(hostIpAddresses != null && hostIpAddresses.size() > 0){
				peer.setHostIPAddress(hostIpAddresses);
			}			
		}		
		
		// peer.setRealmName(reqPacket.getAVP(DiameterAVPConstants.ORIGIN_REALM).getOctetString());
		// TODO we need to be defensive about these AVPs in case these are mandatory. If not found then MISSING_AVP
		peer.setProductName(diameterPacket.getAVP(DiameterAVPConstants.PRODUCT_NAME).getStringValue());
		peer.setVendorId((int) diameterPacket.getAVP(DiameterAVPConstants.VENDOR_ID).getInteger());

		if (!peer.isPeerConnected())
			peer.setConnectionListener(connectionHandler);
		else {
			LogManager.getLogger().warn(MODULE, "Rejecting new Connection with peer: " + getPeerName() +" because peer is already connected");
			connectionHandler.closeConnection(ConnectionEvents.REJECT_CONNECTION);
		}

		// setDiameterAvpList(reqPacket.getAVPList(DiameterAVPConstants.AUTH_APPLICATION_ID));

		if(peer.isSessionCleanUpOnCER()){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Releasing sessions for peer: " + peer.getPeerName() + ".Reason: session clean up on CER is enabled");
			releasePeerSessions(diameterPacket);
		}else{
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Session clean up on CER is disabled for peer: " + peer.getPeerName());
			
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Checking Origin-State-Id AVP value for Releasing sessions for peer: " + peer.getPeerName());
			// Session CleanUp On Receiving different Value of Origin-State-Id In CER	
			IDiameterAVP origin_stateAVP = diameterPacket.getAVP(DiameterAVPConstants.ORIGIN_STATE_ID);
			if(origin_stateAVP != null){
				int originStateID = (int)origin_stateAVP.getInteger();
				if(this.originStateId == -1){
					setOriginStateID(originStateID);
				} else if(originStateID != this.originStateId){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Releasing sessions for peer: " + peer.getPeerName() + ". Reason: Value of Origin-State-Id AVP is changed from " + getOriginStateId() + " to " + originStateID);
					setOriginStateID(originStateID);
					releasePeerSessions(diameterPacket);
				}
			} else {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Releasing sessions for peer: " + peer.getPeerName() + " skipped. Reason: Origin-State-Id AVP not found from CER");
			}
				
		}
		peer.addRemoteApplication(diameterPacket);
		
		
		
		if (peer.getCommonApplications().isEmpty()) {
			return ResultCode.DIAMETER_NO_COMMON_APPLICATION;
		}
		
		//Handling of Inband-Security-Id AVP

		ArrayList<IDiameterAVP> inbandSecurities = diameterPacket.getAVPList(DiameterAVPConstants.INBAND_SECURITY_ID);
		if (inbandSecurities != null && inbandSecurities.size() > 0) {
			for(IDiameterAVP inbandSecurity : inbandSecurities){
				InbandSecurityId inbandSecurityId = InbandSecurityId.fromCode((int) inbandSecurity.getInteger());
				peer.addRemoteSecurityId(inbandSecurityId);
			}
		} else {
			//if no AVP found it means Peer has "no inband security"
			peer.addRemoteSecurityId(InbandSecurityId.NO_INBAND_SECURITY);
		}
		
		
		
		Set<InbandSecurityId> commonSecurity = peer.getCommonSecurityIds();
		if(commonSecurity.isEmpty() && peer.getPeerData().getSecurityStandard() != SecurityStandard.RFC_6733){
			return ResultCode.DIAMETER_NO_COMMON_SECURITY;
		}		
		
		

		if (diameterPacket.getAVP(DiameterAVPConstants.FIRMWARE_REVISION) != null) {
			peer.setFirmwareRevision((int) diameterPacket.getAVP(DiameterAVPConstants.FIRMWARE_REVISION).getInteger());
		}

		ArrayList<IDiameterAVP> supportedVendorIds = diameterPacket.getAVPList(DiameterAVPConstants.SUPPORTED_VENDOR_ID);
		if (supportedVendorIds != null && supportedVendorIds.size() > 0) {
			for(IDiameterAVP supportedVendorId : supportedVendorIds){
				peer.addSupportedVendor(supportedVendorId.getInteger());
			}
		}
		
	
		return ResultCode.DIAMETER_SUCCESS;
	}

	public void atomicActionProcessDWA(StateEvent event) {
		//pcbStateMachine.onReceive(event.getStateTransitionData());
	}

	public void atomicActionProcessDWR(StateEvent event) {
		//pcbStateMachine.onReceive(event.getStateTransitionData());
	}

	public boolean atomicActionRAccept(StateEvent event) {
		numberOfConnectionAttempts.set(0);
		return true;
	}

	public void atomicActionRDisc(StateEvent event) {
		if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
			LogManager.getLogger().warn(MODULE, "Closing Connection with the peer :: "
			+ peer.getPeerName());

		//this.pcbStateMachine.onConnectionDown();
		// Session skiped.

		/*
		 * peerData.getSessionsManager().removeAllSessions();
		 * peerData.closeConnectionContext(); peerData.resetConnectionConetxt();
		 */

	}

	public void atomicActionRReject(@Nullable NetworkConnectionHandler connection) {
		if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
			LogManager.getLogger().error(MODULE, "Peer: " + getPeerName() + ", Executing RReject Action");
		
		Stack.generateAlert(StackAlertSeverity.ERROR, DiameterStackAlerts.PEER_CONNECTION_REJECTED, MODULE, 
				"Multiple Connection Request rejected for Peer: " + getPeerName(), 0,
				getPeerName());
		
		peer.recordMessageTxCountAndConnectionTime();
		if (connection != null) {
			connection.closeConnection(ConnectionEvents.REJECT_CONNECTION);
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Peer: " + getPeerName() + ", Failed closing Connection. Reason: Connection not available ");
		}
	}

	@Override
	public void atomicActionRSndCEA(StateEvent event) {
		atomicActionRSndCEA(event, ResultCode.DIAMETER_SUCCESS);
	}

	public void atomicActionRSndCEA(StateEvent event, ResultCode resultCode) {
		sendCEA(event, resultCode);
	}

	private void sendCEA(StateEvent event, ResultCode resultCode) {
		try {

			IStateTransitionData stateTransitionData = event.getStateTransitionData();
			DiameterPacket diameterPacket = (DiameterPacket) stateTransitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);

			DiameterAnswer diameterAnswer = new DiameterAnswer((DiameterRequest) diameterPacket, resultCode);

			IDiameterAVP originStateIDAvp = diameterPacket.getAVP(DiameterAVPConstants.ORIGIN_STATE_ID);
			if(originStateIDAvp != null && originStateIDAvp.getInteger() != 0){

				IDiameterAVP originStateIdAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.ORIGIN_STATE_ID);
				originStateIdAVP.setStringValue(String.valueOf(Parameter.getInstance().getOriginStateId()));
				diameterAnswer.addAvp(originStateIdAVP);
			}
			
			IDiameterAVP hostAddress = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.HOST_IP_ADDRESS);
			hostAddress.setStringValue(peer.getLocalBoundAddress());
			diameterAnswer.addAvp(hostAddress);

			IDiameterAVP vendorId = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.VENDOR_ID);
			vendorId.setInteger(Parameter.getInstance().getVendorId());
			diameterAnswer.addAvp(vendorId);

			IDiameterAVP productName = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.PRODUCT_NAME);
			productName.setStringValue(Parameter.getInstance().getProductName());
			diameterAnswer.addAvp(productName);
			
			Set<InbandSecurityId> securityIds = peer.getCommonSecurityIds();
			
			if(securityIds != null && securityIds.size() > 0){
				for(InbandSecurityId inbandSecurityId : securityIds){
					IDiameterAVP inbandSecurityAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.INBAND_SECURITY_ID);
					if(inbandSecurityAVP == null){
						break;
					}
					inbandSecurityAVP.setInteger(inbandSecurityId.getCode());
					diameterAnswer.addAvp(inbandSecurityAVP);
				}	
			}
			

			Set<ApplicationEnum> commonApplications = peer.getCommonApplications();

			if (commonApplications != null && !commonApplications.isEmpty()) {
				
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Total "+ commonApplications.size() + " common applications found for " +
					"peer = " + diameterPacket.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
				
				List<IDiameterAVP> diameterAVPs = createApplicationIdAVPs(commonApplications);
				if(diameterAVPs != null && !diameterAVPs.isEmpty()){
					diameterAnswer.addAvps(diameterAVPs);
				} else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "No diameter AVPs created for common Application");
				}

				List<String> tempVendorIds = DiameterDictionary.getInstance().getVendorIDs();
				tempVendorIds.remove("0");
				List<String> vendorIds = new ArrayList<String>(tempVendorIds);

				if (vendorIds != null) {
					vendorIds.retainAll(peer.getSupportedVendors());
				}

				if (vendorIds != null && vendorIds.size() > 0) {
					for(String vendorIdStr : vendorIds){
						IDiameterAVP supportedVendorId = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SUPPORTED_VENDOR_ID);
						supportedVendorId.setInteger(Integer.parseInt(vendorIdStr));
						diameterAnswer.addAvp(supportedVendorId);
					}
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "No common application found for " +
					"peer = " + diameterPacket.getAVPValue(DiameterAVPConstants.ORIGIN_HOST));
			}

			if(resultCode == ResultCode.DIAMETER_SUCCESS){
				setPeerInitConnection(true);
			} else if (resultCode.category == ResultCodeCategory.RC3XXX) {
				diameterAnswer.setErrorBit();
			}
			addAdditionalAVPs(peer.getAdditionalCERAvps(), diameterAnswer);
			sendBasePacket(diameterAnswer);
		} catch (IOException ioExc) {
			LogManager.getLogger().error(MODULE, "Diameter Packet sending failed for Peer: " + peer.getPeerName() + ". Reason: " + ioExc.getMessage());
		}
	}

	public void atomicActionISndCEA(StateEvent event) {
		sendCEA(event, ResultCode.DIAMETER_SUCCESS);
	}

	public void atomicActionSndCER(StateEvent event) {
		numberOfConnectionAttempts.set(0);
		IStateTransitionData stateTransitionData = event
				.getStateTransitionData();
		NetworkConnectionHandler connectionListener = (NetworkConnectionHandler) stateTransitionData
				.getData(PeerDataCode.CONNECTION);
		if (!peer.isPeerConnected())
			peer.setConnectionListener(connectionListener);

		DiameterRequest diameterRequest = new DiameterRequest();

		diameterRequest.setCommandCode(257);
		diameterRequest.setApplicationID(0);
		diameterRequest.setHop_by_hopIdentifier(HopByHopPool.get());
		diameterRequest.setEnd_to_endIdentifier(EndToEndPool.get());

		diameterRequest.addAvp(DiameterAVPConstants.HOST_IP_ADDRESS, peer.getLocalBoundAddress());
		diameterRequest.addAvp(DiameterAVPConstants.VENDOR_ID, String.valueOf(Parameter.getInstance().getVendorId()));
		diameterRequest.addAvp(DiameterAVPConstants.PRODUCT_NAME, Parameter.getInstance().getProductName());
		List<String> supportedVendors = DiameterDictionary.getInstance().getVendorIDs();
		//As per RFC 6733, The value of SUPPORTED_VENDOR_ID AVP MUST NOT be set to zero, 
		//i.e. every peer supports BASE Vendor and should not be sent in SUPPORTED_VENDOR_ID AVP.
		supportedVendors.remove("0");
		diameterRequest.addAvp(DiameterAVPConstants.SUPPORTED_VENDOR_ID, supportedVendors);
		diameterRequest.addAvp(DiameterAVPConstants.ORIGIN_STATE_ID, String.valueOf(Parameter.getInstance().getOriginStateId()));
		
		peer.addSecurityAVP(diameterRequest);
		
		Set<ApplicationEnum> localApplicationIdentifiers = peer.getApplications();
		List<IDiameterAVP> diameterAVPs = createApplicationIdAVPs(localApplicationIdentifiers);
		
		if(diameterAVPs != null && !diameterAVPs.isEmpty()){
			diameterRequest.addAvps(diameterAVPs);
		}
		
		List<IDiameterAVP> cerAvps = peer.getAdditionalCERAvps();
		addAdditionalAVPs(cerAvps, diameterRequest);
		
		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Sending CER to " + peer.getPeerName()+ diameterRequest);
		try {
			sendBasePacket(diameterRequest);
			
		} catch (IOException ioExc) { 
			LogManager.getLogger().error(MODULE, "Diameter Packet sending failed for Peer: " + peer.getPeerName() + ". Reason: " + ioExc.getMessage());
			ignoreTrace(ioExc);
		}

		if(peer.isSessionCleanUpOnCER()){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Releasing sessions for peer: " + peer.getPeerName() + ".Reason: session clean up on CER is enabled");			
				
				releasePeerSessions(diameterRequest);
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Releasing sessions for peer: " + peer.getPeerName() + " Skipped.Reason: session clean up on CER is disabled");
	}
	
	}


	public void atomicActionSndConnReq(StateEvent event) {
		numberOfConnectionAttempts.incrementAndGet();
		stackContext.getNetworkConnector(peer.getTransportProtocol()).openConnection(peer);
	}
	
	public  long getTimeoutConnectionAttempts(){
		return this.numberOfConnectionAttempts.get();
	}
	public void atomicActionRSndDPA(StateEvent event) {

		IStateTransitionData stateTransitionData = event
				.getStateTransitionData();

		DiameterRequest diameterRequest = (DiameterRequest) stateTransitionData
				.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);

		IDiameterAVP resultCode = DiameterDictionary.getInstance()
				.getAttribute(DiameterAVPConstants.RESULT_CODE);
		resultCode.setInteger(ResultCode.DIAMETER_SUCCESS.code);
		
		diameterAnswer.addAvp(resultCode);
		addAdditionalAVPs(peer.getAdditionalDPRAvps(), diameterAnswer);
		try {
			sendBasePacket(diameterAnswer);
		} catch (IOException e) {
			throw new UnhandledTransitionException(e);
		}
		if(peer.isSessionCleanUpOnDPR()){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Releasing sessions for peer: " + peer.getPeerName() + ".Reason: session clean up on DPR is enabled");			
				
			releasePeerSessions(diameterRequest);	
		}else{
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Releasing sessions for peer: " + peer.getPeerName() + " Skipped.Reason: session clean up on DPR is disabled");
		}
		
		
	}

	public void atomicActionRSndDPR(StateEvent event ,DiameterPeerEvent peerEvent) {
		DiameterRequest diameterRequest = new DiameterRequest();
		IStateTransitionData stateTransitionData = event.getStateTransitionData();
		diameterRequest.setCommandCode(CommandCode.DISCONNECT_PEER.code);
		diameterRequest.setRequestBit();
		IDiameterAVP disconnectionCauseAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.DISCONNECT_CAUSE);

		if (stateTransitionData != null){
			String reason = (String) stateTransitionData.getData(PeerDataCode.DISCONNECT_REASON);
			if (CommonConstants.MALFORMED_PACKET.equals(reason)){
				disconnectionCauseAVP.setInteger(DisconnectionCause.BUSY.code);
			} else if(peerEvent == DiameterPeerEvent.Stop){
				disconnectionCauseAVP.setInteger(DisconnectionCause.REBOOTING.code);
			} else{
				disconnectionCauseAVP.setInteger(DisconnectionCause.BUSY.code);
			} 
		} else {
			if(peerEvent == DiameterPeerEvent.Stop){
				disconnectionCauseAVP.setInteger(DisconnectionCause.REBOOTING.code);
			}else{
				disconnectionCauseAVP.setInteger(DisconnectionCause.BUSY.code);
			}
		}
		ArrayList<IDiameterAVP> avpList = new ArrayList<IDiameterAVP>();
		avpList.add(disconnectionCauseAVP);

		diameterRequest.addAvps(avpList);
		diameterRequest.setHop_by_hopIdentifier(HopByHopPool.get());
		diameterRequest.setEnd_to_endIdentifier(EndToEndPool.get());
		
		List<IDiameterAVP> additionalDPRAvps = peer.getAdditionalDPRAvps();
		addAdditionalAVPs(additionalDPRAvps, diameterRequest);
		try {
			sendBasePacket(diameterRequest);
		} catch (IOException e) {
			LogManager.getLogger().error(MODULE, "Error in sending DPR to Peer: " + getPeerName());
		}
		
		if(peer.isSessionCleanUpOnDPR()){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Releasing sessions for peer: " + peer.getPeerName() + ".Reason: session clean up on DPR is enabled");			
				
			releasePeerSessions(diameterRequest);
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Releasing sessions for peer: " + peer.getPeerName() + " Skipped.Reason: session clean up on DPR is disabled");
			}

	}

	@Override
	public void atomicActionISndDWA(StateEvent event) {
		IStateTransitionData stateTransitionData = event
				.getStateTransitionData();

		DiameterRequest diameterRequest = (DiameterRequest) stateTransitionData
				.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);

		IDiameterAVP resultCode = DiameterDictionary.getInstance()
				.getAttribute(DiameterAVPConstants.RESULT_CODE);
		resultCode.setInteger(ResultCode.DIAMETER_SUCCESS.code);
		
		diameterAnswer.addAvp(resultCode);
		addAdditionalAVPs(peer.getAdditionalDWRAvps(), diameterAnswer);
		try {			
			sendBasePacket(diameterAnswer);
		} catch (IOException e) {
			throw new UnhandledTransitionException(e);
		}
	}

	public void atomicActionRSndDWA(StateEvent event) {
		IStateTransitionData stateTransitionData = event
				.getStateTransitionData();

		DiameterRequest diameterRequest = (DiameterRequest) stateTransitionData
				.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);

		IDiameterAVP resultCode = DiameterDictionary.getInstance()
				.getAttribute(DiameterAVPConstants.RESULT_CODE);
		resultCode.setInteger(ResultCode.DIAMETER_SUCCESS.code);

		ArrayList<IDiameterAVP> avpList = new ArrayList<IDiameterAVP>();
		avpList.add(resultCode);
		diameterAnswer.addAvps(avpList);
		addAdditionalAVPs(peer.getAdditionalDWRAvps(), diameterAnswer);
		try {
			sendBasePacket(diameterAnswer);
		} catch (IOException e) {
			throw new UnhandledTransitionException(e);
		}
	}

	public void atomicActionSndMessage(StateEvent event) {
		try {
			IStateTransitionData stateTransitionData = event
					.getStateTransitionData();

			DiameterPacket diameterPacket = (DiameterPacket) stateTransitionData
					.getData(PeerDataCode.DIAMETER_PACKET_TO_SEND);
			if (diameterPacket.isRequest()) {
				ResponseListener listener = (ResponseListener) stateTransitionData.getData(PeerDataCode.RESPONSE_LISTENER);
				sendRequest(diameterPacket.getAsDiameterRequest(), listener);
			} else {
				if (peer.getPeerData().isReTransmissionCompliant()) {
					duplicateMessageHandler.decorate((DiameterAnswer) diameterPacket);
				}
				sendAnswer(diameterPacket.getAsDiameterAnswer());
			}
		} catch (IOException e) {
			throw new UnhandledTransitionException(e);
		}
	}

	protected List<State> createStates() {
		
		List<State> result = new ArrayList<State>();
		DiameterPeerStateMachineContext diameterPeerStateMachineContext = (DiameterPeerStateMachineContext)getStateMachineContext();
		result.add(DiameterPeerState.Closed.ordinal(),new PeerStateClosed(this, diameterPeerStateMachineContext));
		result.add(DiameterPeerState.Wait_Conn_Ack.ordinal(),new PeerStateWaitConnAck(this, diameterPeerStateMachineContext));
		result.add(DiameterPeerState.Wait_I_CEA.ordinal(), new PeerStateWaitICEA(this, diameterPeerStateMachineContext));
		result.add(DiameterPeerState.Elect.ordinal(), new PeerStateWaitConnAckElect(this, diameterPeerStateMachineContext));
		result.add(DiameterPeerState.Wait_Returns.ordinal(), new PeerStateWaitReturn(this, diameterPeerStateMachineContext));
		result.add(DiameterPeerState.R_Open.ordinal(),new PeerStateROpen(this, diameterPeerStateMachineContext));
		result.add(DiameterPeerState.I_Open.ordinal(), new PeerStateIOpen(this, diameterPeerStateMachineContext));
		result.add(DiameterPeerState.Closing.ordinal(),new PeerStateClosing(this, diameterPeerStateMachineContext));
		result.add(DiameterPeerState.Wait_Conn_Ack_Elect.ordinal(), new PeerStateWaitConnAck(this, diameterPeerStateMachineContext));
		
		return result; 

	}

	public void act() {
	}

	@Override
	public void atomicActionISndDPA(StateEvent event) {
		IStateTransitionData stateTransitionData = event.getStateTransitionData();

		DiameterRequest diameterRequest = (DiameterRequest) stateTransitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);

		IDiameterAVP resultCode = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
		resultCode.setInteger(ResultCode.DIAMETER_SUCCESS.code);

		ArrayList<IDiameterAVP> avpList = new ArrayList<IDiameterAVP>();
		avpList.add(resultCode);

		diameterAnswer.addAvps(avpList);
		addAdditionalAVPs(peer.getAdditionalDPRAvps(), diameterAnswer);
		try {
			sendBasePacket(diameterAnswer);
		} catch (IOException e) {
			throw new UnhandledTransitionException(e);
		}
		if(peer.isSessionCleanUpOnDPR()){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Releasing sessions for peer: " + peer.getPeerName() + ".Reason: session clean up on DPR is enabled");			
				
			releasePeerSessions(diameterRequest);	
		}else{
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Releasing sessions for peer: " + peer.getPeerName() + " Skipped.Reason: session clean up on DPR is disabled");
		}
	}

	@Override
	public void atomicActionISndDPR(StateEvent event ,DiameterPeerEvent diameterPeerEvent) {
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setCommandCode(CommandCode.DISCONNECT_PEER.code);
		IDiameterAVP disconnectionCauseAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.DISCONNECT_CAUSE);
		
		IStateTransitionData stateTransitionData = event.getStateTransitionData();
		if (stateTransitionData != null){
			String reason = (String) stateTransitionData.getData(PeerDataCode.DISCONNECT_REASON);
			if (CommonConstants.MALFORMED_PACKET.equals(reason)){
				disconnectionCauseAVP.setInteger(DisconnectionCause.BUSY.code);
			} else if(diameterPeerEvent == DiameterPeerEvent.Stop){
				disconnectionCauseAVP.setInteger(DisconnectionCause.REBOOTING.code);
			} else{
				disconnectionCauseAVP.setInteger(DisconnectionCause.BUSY.code);
			} 
		} else {
			if(diameterPeerEvent == DiameterPeerEvent.Stop){
				disconnectionCauseAVP.setInteger(DisconnectionCause.REBOOTING.code);
			}else{
				disconnectionCauseAVP.setInteger(DisconnectionCause.BUSY.code);
			}
		}

		ArrayList<IDiameterAVP> avpList = new ArrayList<IDiameterAVP>();
		avpList.add(disconnectionCauseAVP);

		diameterRequest.addAvps(avpList);
		
		List<IDiameterAVP> additionalDPRAvps = peer.getAdditionalDPRAvps();
		addAdditionalAVPs(additionalDPRAvps, diameterRequest);
		
		diameterRequest.setHop_by_hopIdentifier(HopByHopPool.get());
		diameterRequest.setEnd_to_endIdentifier(EndToEndPool.get());
		try {
			sendBasePacket(diameterRequest);
		} catch (IOException e) {
			LogManager.getLogger().error(MODULE, "Error in sending DPR to Peer: " + getPeerName());
		}
		
		if(peer.isSessionCleanUpOnDPR()){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Releasing sessions for peer: " + peer.getPeerName() + ".Reason: session clean up on DPR is enabled");			
				
			releasePeerSessions(diameterRequest);
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Releasing sessions for peer: " + peer.getPeerName() + " Skipped.Reason: session clean up on DPR is disabled");
			}


	}
	
	/**
	 * 5.6.4.  The Election Process
	 * <p/>
	 * The election is performed on the responder.  The responder compares
	 * the Origin-Host received in the CER sent by its peer with its own
	 * Origin-Host.  If the local Diameter entity's Origin-Host is higher
	 * than the peer's, a Win-Election event is issued locally.
	 * <p/>
	 * The comparison proceeds by considering the shorter OctetString to be
	 * padded with zeros so that it length is the same as the length of the
	 * longer, then performing an octet-by-octet unsigned comparison with
	 * the first octet being most significant.  Any remaining octets are
	 * assumed to have value 0x80.
	 *
	 * @param event
	 */
	public final void atomicActionElect(StateEvent event) {
		String localHostName = Parameter.getInstance().getOwnDiameterIdentity();
		StringBuilder localHost = new StringBuilder(localHostName);
		StringBuilder remoteHost = new StringBuilder(peer.getHostIdentity());
		int lengthDiff = localHost.length() - remoteHost.length();
		if (lengthDiff < 0) {
			localHost.append(padding, 0, -lengthDiff);
		}
		else if (lengthDiff > 0) {
			remoteHost.append(padding, 0, lengthDiff);
		}
		/*int compare = localHost.toString().compareTo(remoteHost.toString());
		if (compare > 0) {
			event.setNextStateEntryEvent(DiameterPeerEvent.WinElection);
		}else if (compare == 0) {
			event.setNextStateEntryEvent(DiameterPeerEvent.Stop);
		}else {
			event.setNextStateEntryEvent(DiameterPeerEvent.Stop);
		}
*/	}


	
	class DiameterPeerStateMachineContext extends BaseStateMachineContext implements IPeerStateMachineContext{

		public DiameterPeerStateMachineContext() {

		}

		public IPeerListener getPeerListener() {
			return peer;
		}
	}


	@Override
	public void onConnectionUp() {
		this.peer.onConnectionUp();
		
	}
	
	@Override
	protected String getKey() {
		return MODULE;
	}

	@Override
	protected IStateMachineContext createStateMachineContext() {
		return new DiameterPeerStateMachineContext();
	}
	
	public class DiameterPeerStateMachineListener implements IStateMachineListener {

		@Override
		public void stateSwitched(IStateEnum oldState, IStateEnum newState) {
			if(newState == DiameterPeerState.R_Open || newState == DiameterPeerState.I_Open) {
				Stack.generateAlert(StackAlertSeverity.INFO, 
						DiameterStackAlerts.DIAMETER_PEER_UP, 
						MODULE, 
						peer.getPeerName() + " is up, current state: " + currentState.toString(), 0, peer.getPeerName() + "(" + currentState.toString() + ")");
			}else if (newState == DiameterPeerState.Closed) {
				Stack.generateAlert(StackAlertSeverity.CRITICAL, 
						DiameterStackAlerts.DIAMETER_PEER_DOWN, 
						MODULE, 
						peer.getPeerName() + " is down, current state: " + currentState.toString(), 0, peer.getPeerName()+ "(" + currentState.toString() + ")");
			}
		}
		
	}


	@Override
	public IStateMachineListener getStateMachineListener() {
		return new DiameterPeerStateMachineListener();
	}
	private IStateTransitionData getStateTransitionData() {
		IStateTransitionData stateTransitionData = new IStateTransitionData() {
			Map<IStateTransitionDataCode, Object> data = new HashMap<IStateTransitionDataCode, Object>();
			public Object getData(IStateTransitionDataCode key) {
				return data.get(key);
			}
			
			public void addObject(IStateTransitionDataCode key, Object value) {
				data.put(key, value);
			}
		};

		return stateTransitionData;
	}
	
	public void startTimeoutEventTimer() {
		stackContext.scheduleSingleExecutionTask(new PeerTimeoutTask());
	}
	
	protected List<IDiameterAVP> createApplicationIdAVPs(Set<ApplicationEnum> diameterApplicationIdentifiers){
		List<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();
		for(ApplicationEnum applicationIdentifier : diameterApplicationIdentifiers){
			if(applicationIdentifier.getVendorId()<=0) {
				switch(applicationIdentifier.getApplicationType()){
				case ACCT:
					IDiameterAVP acctAppIdAvp = createAcctApplicationAVP(applicationIdentifier.getApplicationId());
					if(acctAppIdAvp != null)
						diameterAVPs.add(acctAppIdAvp);
					break;
				case AUTH:
					IDiameterAVP authAppIdAvp = createAuthApplicationAVP(applicationIdentifier.getApplicationId());
					if(authAppIdAvp != null)
						diameterAVPs.add(authAppIdAvp);
					break;
				case BOTH:
					IDiameterAVP appIdAvp = createAuthApplicationAVP(applicationIdentifier.getApplicationId());
					if(appIdAvp != null)
						diameterAVPs.add(appIdAvp);
					appIdAvp = createAcctApplicationAVP(applicationIdentifier.getApplicationId());
					if(appIdAvp != null)
						diameterAVPs.add(appIdAvp);
					break;
				}
			}else {
				
				AvpGrouped vendorSpeAppId = (AvpGrouped)DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.VENDOR_SPECIFIC_APPLICATION_ID);
				if(vendorSpeAppId ==null){
					LogManager.getLogger().warn(MODULE, "Skipping Capability Exchange for Vendor Id(" 
					+ applicationIdentifier.getVendorId() + "). Reason: " 
					+ DiameterAVPConstants.VENDOR_SPECIFIC_APPLICATION_ID_STR+ " AVP not found from Diameter Dictionary");
					continue;
				}
				IDiameterAVP vendorId = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.VENDOR_ID);
				if(vendorId == null){
					LogManager.getLogger().warn(MODULE, "Skipping Capability Exchange for Vendor Id(" 
					+ applicationIdentifier.getVendorId()+ "). Reason: " 
					+ DiameterAVPConstants.VENDOR_ID+ " AVP not found from Diameter Dictionary");
					continue;
				}
				vendorId.setStringValue(String.valueOf(applicationIdentifier.getVendorId()));
				vendorSpeAppId.addSubAvp(vendorId);
				
				// Add Vendor-Specific-Application-Id if Auth or Acct Application ID greater then ZERO 
				switch(applicationIdentifier.getApplicationType()){
				
				case ACCT:
					IDiameterAVP authIDAvp = createAcctApplicationAVP(applicationIdentifier.getApplicationId());
					if(authIDAvp != null)
						vendorSpeAppId.addSubAvp(authIDAvp);
					if(vendorSpeAppId.getGroupedAvp().size() > 1)
						diameterAVPs.add(vendorSpeAppId);
					break;
					
				case AUTH:
					IDiameterAVP acctIDAvp = createAuthApplicationAVP(applicationIdentifier.getApplicationId());
					if(acctIDAvp != null)
						vendorSpeAppId.addSubAvp(acctIDAvp);
					if(vendorSpeAppId.getGroupedAvp().size() > 1)
						diameterAVPs.add(vendorSpeAppId);
					break;
					
				case BOTH:
					
					AvpGrouped vendorSpeAppId2 = null;
					try {
						vendorSpeAppId2 = (AvpGrouped) vendorSpeAppId.clone();
					} catch (CloneNotSupportedException e) {
						LogManager.getLogger().trace(MODULE, e);
						break;
					}
					
					IDiameterAVP appIDAvp = createAuthApplicationAVP(applicationIdentifier.getApplicationId());
					if(appIDAvp != null)
						vendorSpeAppId.addSubAvp(appIDAvp);
						diameterAVPs.add(vendorSpeAppId);

					appIDAvp = createAcctApplicationAVP(applicationIdentifier.getApplicationId());
					if(appIDAvp != null)
						vendorSpeAppId2.addSubAvp(appIDAvp);
						diameterAVPs.add(vendorSpeAppId2);
					break;
				}
			}
		}
		return diameterAVPs;
	}
	
	private IDiameterAVP createAuthApplicationAVP(long authAppIdVal){
		if(authAppIdVal >= 0){
			IDiameterAVP authAppId = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.AUTH_APPLICATION_ID);
			if(authAppId != null){
				authAppId.setStringValue(String.valueOf(authAppIdVal));
				return authAppId;
			}else{
				LogManager.getLogger().debug(MODULE, "Error in creating " + DiameterAVPConstants.AUTH_APPLICATION_ID  + ". Reason: " + DiameterAVPConstants.AUTH_APPLICATION_ID + " AVP not found from Diameter Dictionary");
			}
		}else{
			LogManager.getLogger().debug(MODULE, "Error in creating " + DiameterAVPConstants.AUTH_APPLICATION_ID  + ". Reason: " + DiameterAVPConstants.AUTH_APPLICATION_ID + " must be greater then ZERO");
		}
		return null;
	}
	
	private IDiameterAVP createAcctApplicationAVP(long acctAppIdVal){
		if(acctAppIdVal >= 0){
			IDiameterAVP acctAppId = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.ACCT_APPLICATION_ID);
			if(acctAppId != null){
				acctAppId.setStringValue(String.valueOf(acctAppIdVal));
				return acctAppId;
			}else{
				LogManager.getLogger().debug(MODULE, "Error in creating " + DiameterAVPConstants.ACCT_APPLICATION_ID  + ". Reason: " + DiameterAVPConstants.ACCT_APPLICATION_ID + " AVP not found from Diameter Dictionary");
			}
		}else{
			LogManager.getLogger().debug(MODULE, "Error in creating " + DiameterAVPConstants.ACCT_APPLICATION_ID  + ". Reason: " + DiameterAVPConstants.ACCT_APPLICATION_ID+ " must be greater then ZERO");
		}
		
		return null;
	}
	

	 
	
	private class PeerTimeoutTask extends BaseSingleExecutionAsyncTask{
		
		private IStateEnum state;
		
		public PeerTimeoutTask() {
			this.state = currentState();
		}
		@Override
		public void execute(AsyncTaskContext context) {
			try {
				if(this.state == currentState()) {
					IStateTransitionData stateTransitionData = getStateTransitionData();
					stateTransitionData.addObject(PeerDataCode.PEER_EVENT, DiameterPeerEvent.Timeout);
					onStateTransitionTrigger(stateTransitionData);
				}else {
					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Peer: " + getPeerName() + ", Old State : " + state + " : New State : " + currentState() + " not going to execute time-out task");
				}
			}catch(Exception e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Peer: " + getPeerName() + ", " + e.getMessage());
			}
		}

		@Override
		public long getInitialDelay() {
			return peer.getPeerTimeout()/1000;
		}
	}
	
	@Override
    public void onConnectionDown() {
        this.peer.onConnectionDown();

    }
	
	public abstract void sendBasePacket(DiameterPacket basePacket) throws IOException;
	
	public abstract void sendRequest(DiameterRequest diameterRequest, ResponseListener listener) throws IOException;
	public abstract void sendAnswer(DiameterAnswer diameterAnswer) throws IOException;

	
	public void releasePeerSessions(DiameterRequest baseRequest){
		long sessionCount = stackContext.releasePeerSessions(baseRequest); 
		if (LogManager.getLogger().isWarnLogLevel()) {
			LogManager.getLogger().warn(MODULE, "Removed "+ sessionCount +" Sessions for HostIdenity :"+peer.getPeerName());
		}
		
		
	}

	// Setter and Getter Methods for Origin-State-Id.
	private void setOriginStateID(int originStateId){
		this.originStateId = originStateId;
	}
	private int getOriginStateId(){
		return originStateId;
	}
	public abstract void addAdditionalAVPs(List<IDiameterAVP> additionalAvps,DiameterPacket diameterPacket);
	protected abstract void setPeerInitConnection(boolean peerInitConnection);
	protected abstract void triggerDWR();
	
	@Override
	public final void atomicActionProcessDuplicateConnection(StateEvent stateEvent) {
		
		NetworkConnectionHandler duplicateConnection = (NetworkConnectionHandler) (stateEvent.getStateTransitionData().getData(PeerDataCode.CONNECTION));
		if (duplicateConnection == null) {
			LogManager.getLogger().warn(MODULE, "Duplicate Connection handler is null for peer: " + peer.getPeerName());
			return;
		}
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Duplicate connection request arrived for Peer: " + 
					getPeerName());
		}
		
		try {
			duplicateConnectionPolicy.checkAcceptable(duplicateConnection);
			
			atomicActionError(stateEvent, ConnectionEvents.FORCE_CLOSE);
			ResultCode resultCode = ResultCode.DIAMETER_UNABLE_TO_COMPLY;
			
			if (atomicActionRAccept(stateEvent)) {						
				resultCode = atomicActionProcessCER(stateEvent);
			}
			atomicActionRSndCEA(stateEvent,resultCode);
			
		} catch (UnhandledTransitionException ex) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, ex.getMessage());
			}
			LogManager.getLogger().trace(ex);
			
			atomicActionRReject((NetworkConnectionHandler) (stateEvent.getStateTransitionData().getData(PeerDataCode.CONNECTION)));
		}
	}

	private interface DuplicateConnectionPolicy {
		void checkAcceptable(NetworkConnectionHandler duplicateConnection);
	}
	
	private class DefaultPolicy implements DuplicateConnectionPolicy {

		@Override
		public void checkAcceptable(NetworkConnectionHandler duplicateConnection) {
		if (isEligibleForAcceptingDuplicateConnection() == false) {

				throw new UnhandledTransitionException("Rejecting duplicate connection request of Peer: " + 
						getPeerName() + ", Reason: Request arrived in less than 3000ms.");
			}

		if (peer.getWatchdogInterval() > 0) {

			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "DWR triggered for Peer: " + getPeerName());
			}
			triggerDWR();

				throw new UnhandledTransitionException("Rejecting duplicate connection request of Peer: " 
						+ getPeerName());
		}

		if (peer.isTrafficObservedOnCurrentConnection()) {
		
				throw new UnhandledTransitionException("Connection request from IP-Address: " 
						+ duplicateConnection.getSourceIpAddress() + ":" + duplicateConnection.getSourcePort() 
						+ " is not acceptable, Reason: Traffic is observed from old Connection");
		}
	}
	
	private boolean isEligibleForAcceptingDuplicateConnection() {
			return timesource.currentTimeInMillis() - peer.getPeerLastConnectionRequestTime() > 
			DiameterPeer.DUPLICATE_CONNECTION_ALLOWED_INTERVAL;
	}
	}

	private class DiscardOldPolicy implements DuplicateConnectionPolicy {
	
		@Override
		public void checkAcceptable(NetworkConnectionHandler duplicateConnection) {
			if (peer.isTrafficObservedOnCurrentConnection()) {
				throw new UnhandledTransitionException("Connection request from IP-Address: " 
						+ duplicateConnection.getSourceIpAddress() + ":" + duplicateConnection.getSourcePort() 
						+ " is not acceptable, Reason: Traffic is observed from old Connection");
}
			LogManager.getLogger().trace(MODULE, "Discard Old policy applied successfully for peer: " + peer.getPeerName());
		}
	}


	public void reload() {
		this.duplicateConnectionPolicy = createDuplicateConnectionPolicy();
	}
}
