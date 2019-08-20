package com.elitecore.diameterapi.diameter.common.routerx.agent;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.router.exception.RoutingFailedException;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.peers.PeerSelector;
import com.elitecore.diameterapi.diameter.common.routerx.RouterContext;
import com.elitecore.diameterapi.diameter.common.routerx.RoutingEntry;
import com.elitecore.diameterapi.diameter.common.routerx.failure.FailureActionResult;
import com.elitecore.diameterapi.diameter.common.routerx.failure.RoutingFailureAction;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.HopByHopPool;
import com.elitecore.diameterapi.diameter.stack.IDiameterSessionManager;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;
import com.elitecore.diameterapi.diameter.stack.application.sessionrelease.AppDefaultSessionReleaseIndicator;
/**
 * 
 * Relay agents route Diameter messages using the
 * Diameter routing table.
 * 
 * <pre>
 *      +------+    --------->     +-------+     --------->    +------+
 *      | Dia  |    1. Request     | Elite |     2. Request    | Dia  |
 *      | Node |                   |  DRA  |                   | Node |
 *      |      |    4. Answer      |       |     3. Answer     |      |
 *      +------+    <---------     +-------+     <---------    +------+
 *     example.net                example.net                example.com
 * </pre>
 *     
 * @author monica.lulla
 *
 */
public class RelayAgent extends DiameterAgent {

	
	private static final String MODULE = "RELAY-AGNT";
	protected SessionReleaseIndiactor sessionReleaseIndiactor;
	private IDiameterSessionManager diameterSessionManager;

	public RelayAgent(RouterContext routerContext,
			IDiameterSessionManager diameterSessionManager) {
		super(routerContext);
		this.sessionReleaseIndiactor = new AppDefaultSessionReleaseIndicator();
		this.diameterSessionManager = diameterSessionManager;
	}

	@Override
	public void routeRequest(final DiameterRequest originRequest, final DiameterSession diameterSession, 
			final RoutingEntry routingEntry) throws RoutingFailedException {

		String sessionId = diameterSession.getSessionId();
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Routing initiated for Diameter Request with Session-Id = " + sessionId);
		}

		diameterSession.setParameter(DiameterAVPConstants.ORIGIN_HOST, originRequest.getRequestingHost());
		diameterSession.setParameter(ROUTING_ENTRY, routingEntry.getRoutingEntryName());
		
		locateSession(originRequest);
		
		final DiameterRequest destinationRequest = buildRequest(diameterSession, originRequest, routingEntry);
		/*
		 * SessionId AVP is added just to remove translation fail scenario
		 * 
		 */
		if (destinationRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) == null) {
			IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
			diameterAVP.setStringValue(sessionId);
			destinationRequest.addAvp(diameterAVP);
		}
		
		
		String destPeerName;
		
		if (destinationRequest.getParameter(TranslatorConstants.DUMMY_MAPPING) != null) {
			destPeerName = routerContext.getVirtualRoutingPeerName();
		} else {
			destPeerName = selectDestinationPeer(originRequest, routingEntry, diameterSession);
		}

		if(destPeerName == null){
			//TODO PROPER LOGGING -- HARSH PATEL
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Doing failover Sending "+ ResultCode.DIAMETER_PEER_NOT_FOUND +
				", Reason: Destination Peer not found for Session-Id=" + sessionId);
			DiameterAnswer diameterAnswer = new DiameterAnswer(originRequest,ResultCode.DIAMETER_PEER_NOT_FOUND);
			routeAnswer(diameterSession, originRequest, destinationRequest, diameterAnswer, routingEntry, null);
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Peer: " + destPeerName + " selected for Session-ID=" + sessionId);



			diameterSession.setParameter(DiameterAVPConstants.DESTINATION_HOST, destPeerName);
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Routing Session updated for Session-ID=" + sessionId);
		
			
			addRouteRecoredAVP(originRequest, destinationRequest);
			
			postRequestProcessing(destinationRequest);
			
			finalRequestProcessing(diameterSession, originRequest, destinationRequest, destPeerName, routingEntry);
			
			try {

				sendClientInitiatedRequest(diameterSession,destinationRequest, new RelayAgentResponseListener(routingEntry, originRequest, destinationRequest),
						destPeerName, routingEntry.getRoutingAction());
			
				
			} catch (CommunicationException e) { 
				/*
				 * This is observed when there is Error in Sending Request to Peer,
				 * Possible Reasons: IO Error while sending Packet
				 * 
				 * In that case Sending, Peer Not Found, So as take necessary Failover Actions 
				 * for this.
				 */
				getLogger().trace(MODULE, e);
				throw new RoutingFailedException(ResultCode.DIAMETER_PEER_NOT_FOUND, routingEntry.getRoutingAction(), e.getMessage());
			}
		}

		
	}

	protected void postRequestProcessing(DiameterRequest destinationRequest) {
		
	}

	/**
	 * Behavior:<br />
	 * 
	 * 	 Select Dest-Host in DESTINATION_HOST AVP if available <br />
	 *  <b>></b> Select Stateful Peer if Routing Session available <br />
	 *  <b>></b> Select Next Peer from Group in Round Robin Fashion.
	 * 
	 * @param originRequest
	 * @param routingEntry
	 * @param routingSession
	 * @return destination host name to which request is to be forwarded
	 * 
	 */
	protected String selectDestinationPeer(DiameterRequest originRequest,
			RoutingEntry routingEntry, DiameterSession routingSession) {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Selecting Destination Peer for Session-Id=" + 
					originRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
		}
		
		DiameterPeerCommunicator destPeerComm = null;
		


		if (routingEntry.isStatefulRoutingEnabled()) {
			String destHostId = (String) routingSession.getParameter(DiameterAVPConstants.DESTINATION_HOST);
			if(destHostId != null) {
				destPeerComm = routerContext.getPeerCommunicator(destHostId);

				if (destPeerComm.isAlive()) {

					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Session's Destination host: " 
								+ destHostId + " selected for Session-ID = " 
								+ originRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
					}
					return destPeerComm.getName();
				}
				
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Peer: " + destHostId
							+ " for Session-ID = " + originRequest.getAVPValue(DiameterAVPConstants.SESSION_ID)
							+ " is Down.");
				}
				return null;
			}
		}
	
		
		String destHostId = originRequest.getAVPValue(DiameterAVPConstants.DESTINATION_HOST);
		if (destHostId != null){

			destPeerComm = routerContext.getPeerCommunicator(destHostId);
			if(destPeerComm != null){

				if(destPeerComm.isAlive()){

					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Packet's Destination host: " + destHostId + 
						" selected for Session-ID=" +
						originRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
					return destPeerComm.getName();
				}

				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Peer: " + destHostId + 
					" for Session-ID=" + originRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) +
					" is Down.");
				return null;
			}
		}

		PeerSelector peerSelector = routingEntry.getPeerSelector();
		if(peerSelector != null)
			return peerSelector.selectNextPeer(originRequest);

		return null;
	}
	
	private class RelayAgentResponseListener implements ResponseListener {

		private RoutingEntry routingEntry;
		private DiameterRequest originRequest;
		private DiameterRequest destinationRequest;

		public RelayAgentResponseListener(RoutingEntry routingEntry,
				DiameterRequest originRequest, DiameterRequest destinationRequest) {
			super();
			this.routingEntry = routingEntry;
			this.originRequest = originRequest;
			this.destinationRequest = destinationRequest;
		}

		@Override
		public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession diameterSession) {
			routerContext.updateRealmInputStatistics(diameterAnswer, 
					routerContext.getPeerData(hostIdentity).getRealmName(),
					routingEntry.getRoutingAction());
			routeAnswer(diameterSession, originRequest, destinationRequest, diameterAnswer, routingEntry, hostIdentity);
		}

		@Override
		public void requestTimedout(String hostIdentity, DiameterSession diameterSession) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Request timeout response received from peer: " + hostIdentity);
			}
			DiameterAnswer diameterAnswer = new DiameterAnswer(destinationRequest, ResultCode.DIAMETER_REQUEST_TIMEOUT);
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, 
					diameterAnswer, DiameterErrorMessageConstants.REQUEST_TIMEOUT);
			
			routerContext.updateRealmTimeoutRequestStatistics(destinationRequest, 
					routerContext.getPeerData(hostIdentity).getRealmName(), 
					routingEntry.getRoutingAction());
			
			routeAnswer(diameterSession, originRequest, destinationRequest, diameterAnswer, routingEntry, hostIdentity);
		}

	}

	/**
	 * Caution: Must check Diameter Session and Routing Session for Null before using it. 
	 * 
	 * @param diameterSession
	 * @param routingSession
	 * @param originRequest
	 * @param routingEntry
	 * @return
	 * @throws RoutingFailedException
	 */
	protected DiameterRequest buildRequest (DiameterSession diameterSession, 
			DiameterRequest originRequest, RoutingEntry routingEntry) throws RoutingFailedException {
		try {
			DiameterRequest destReq = (DiameterRequest) originRequest.clone();
			destReq.setHop_by_hopIdentifier(HopByHopPool.get());
			destReq.touch();
			return destReq;
		} catch (CloneNotSupportedException e) {
			LogManager.getLogger().trace(MODULE, e);
			throw new AssertionError(e);
		} 
	}

	private void addRouteRecoredAVP(DiameterRequest originalRequest, DiameterRequest destinationRequest){
		//Adding Route Record AVP
		String requesterHostId = originalRequest.getRequestingHost();
		if (requesterHostId != null && requesterHostId.trim().length() > 0 
				&& (requesterHostId.equalsIgnoreCase(Parameter.getInstance().getOwnDiameterIdentity()) == false)){
			IDiameterAVP routeRecord = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.ROUTE_RECORD);
			routeRecord.setStringValue(requesterHostId);
			destinationRequest.addAvp(routeRecord);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Added Route-Record-AVP " + DiameterAVPConstants.ROUTE_RECORD + " with value: "+ requesterHostId + " in remote request.");
		}
	}
	}
	
	/**
	 * This will perform Final Request processing<br />
	 * 
	 * --> Set HbH for Origin Request<br />
	 * --> Add Route Record AVP<br />
	 * --> Set Routing Entry for Origin Request<br />
	 * --> Set Origin Request<br />
	 * 
	 * @param diameterSession
	 * @param destinationRequest
	 * @param routingEntry
	 * @param destPeerName
	 */
	private void finalRequestProcessing(DiameterSession routingSession, 
			DiameterRequest originRequest,
			DiameterRequest destinationRequest, 
			String remoteHost,
			RoutingEntry routingEntry) {

		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Final Request Processing for Session-Id=" + 
			originRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
		
		routerContext.postRequestRouting(originRequest, destinationRequest, 
				originRequest.getRequestingHost(), 
				remoteHost, routingEntry.getRoutingEntryName());
		
		if (routingEntry.isStatefulRoutingEnabled()) {
			routingSession.setParameter(DiameterAVPConstants.DESTINATION_HOST, remoteHost);
		}
	}
	
	/**
	 * This will perform Final Request processing<br />
	 * 
	 * --> Set HbH for Origin Request<br />
	 * --> Add Route Record AVP<br />
	 * --> Set Routing Entry for Origin Request<br />
	 * --> Set Origin Request<br />
	 * 
	 * @param diameterSession
	 * @param destinationRequest
	 * @param routingEntry
	 * @param destPeerName
	 */
	private void finalServerInitiatedRequestProcessing( 
			DiameterSession session, 
			DiameterRequest originRequest,
			DiameterRequest destinationRequest, 
			RoutingEntry routingEntry) {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Final Request Processing for Session-Id=" + 
					originRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
		}
		destinationRequest.setHop_by_hopIdentifier(HopByHopPool.get());
		routerContext.postRequestRouting(originRequest, 
				destinationRequest, 
				originRequest.getRequestingHost(),
				(String)session.getParameter(DiameterAVPConstants.ORIGIN_HOST), 
				routingEntry.getRoutingEntryName());

	}

	private void routeAnswer(DiameterSession session, 
			DiameterRequest originalRequest, 
			DiameterRequest translatedRequest, 
			DiameterAnswer remoteAnswer, 
			RoutingEntry routingEntry, 
			String hostIdentity){
		
		String sessionId = session.getSessionId();
		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Routing initiated for Diameter Answer with Session-ID=" + sessionId);
		
		routerContext.preAnswerRouting(originalRequest, translatedRequest
				, remoteAnswer, hostIdentity, routingEntry.getRoutingEntryName());
		
		
		IDiameterAVP resultCodeAVP = remoteAnswer.getAVP(DiameterAVPConstants.RESULT_CODE);
		int resultCode = 0;
		if(resultCodeAVP != null) {
			resultCode = (int) resultCodeAVP.getInteger();
		}
		
		if(ResultCodeCategory.getResultCodeCategory(resultCode).isFailureCategory == false 
				|| routingEntry.getFailureAction(resultCode) == null){
			DiameterAnswer translatedAnswer = buildAnswer(originalRequest, remoteAnswer, routingEntry, session, translatedRequest);
			sendAnswerInternal(originalRequest, translatedRequest, remoteAnswer, routingEntry, session, translatedAnswer,hostIdentity);
		}else{
			RoutingFailureAction failureAction = routingEntry.getFailureAction(resultCode);

			
			FailureActionResult failureActionResult = failureAction.process(remoteAnswer, session, originalRequest, translatedRequest, hostIdentity, originalRequest.getRequestingHost());
			
			switch (failureActionResult.getAction()) {
			case DROP:
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Dropping Diameter Answer with HbH-ID=" + remoteAnswer.getHop_by_hopIdentifier());
				
				releaseSession(originalRequest, new DiameterAnswer(originalRequest), session);
				
				routerContext.updateDiameterStatsPacketDroppedStatistics(
						remoteAnswer, 
						originalRequest.getRequestingHost(), 
						originalRequest.getPeerData().getRealmName(), 
						routingEntry.getRoutingAction());
				return;
				
			case SEND_REQUEST_TO_PEER:
				actionOnSendRequestToPeer(remoteAnswer, session, failureActionResult, translatedRequest, originalRequest, routingEntry, hostIdentity);
				break;
				
			case SEND_ANSWER_TO_ORIGINATOR:
				sendAnswerInternal(originalRequest, translatedRequest, remoteAnswer, routingEntry, session, failureActionResult.getDiameterPacket().getAsDiameterAnswer(),hostIdentity);
				break;
			}
		
		}
	}

	private void sendAnswerInternal(DiameterRequest originRequest,
			DiameterRequest remoteRequest,
			DiameterAnswer remoteAnswer, 
			RoutingEntry routingEntry, 
			DiameterSession diameterSession,
			DiameterAnswer finalPacket, String remoteHost) {
		
		postAnswerProcessing((DiameterAnswer) finalPacket);
		boolean releaseSession = sessionReleaseIndiactor.isEligible(finalPacket);
		if(releaseSession == false){
			updateOrSaveSession(originRequest, (DiameterAnswer)finalPacket);
		}
		finalAnswerProcessing(originRequest, remoteAnswer, finalPacket.getAsDiameterAnswer(), 
				diameterSession, remoteRequest, remoteHost, routingEntry);
		try{
			sendAnswer(diameterSession, originRequest, finalPacket, originRequest.getRequestingHost(), routingEntry.getRoutingAction());
		} catch (CommunicationException e) { 
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Dropping Diameter Answer with Session-ID=" + 
												diameterSession.getSessionId() + ", Reason: "  + e.getMessage());
			}
			getLogger().trace(MODULE, e);
			routerContext.updateDiameterStatsPacketDroppedStatistics(
					finalPacket, originRequest.getRequestingHost(),
					originRequest.getPeerData().getRealmName(),
					routingEntry.getRoutingAction());
		}
		if(releaseSession){
			releaseSession(originRequest, (DiameterAnswer) finalPacket, diameterSession);
		}
	}

	private void actionOnSendRequestToPeer(DiameterAnswer remoteAnswer, 
			final DiameterSession diameterSession,
			FailureActionResult failureActionResult, 
			DiameterRequest destinationRequest, 
			final DiameterRequest originalRequest, 
			final RoutingEntry routingEntry,
			String remoteHost) {
		
		final DiameterPacket finalPacket = failureActionResult.getDiameterPacket();
		String destPeerName = failureActionResult.getPeerName();

		diameterSession.setParameter(DiameterAVPConstants.DESTINATION_HOST, destPeerName);
		
		postRequestProcessing(destinationRequest);
		finalRequestProcessing(diameterSession, originalRequest, (DiameterRequest) finalPacket, destPeerName, routingEntry);
		try {
			sendClientInitiatedRequest(diameterSession,finalPacket.getAsDiameterRequest(),
					new RelayAgentResponseListener(routingEntry, originalRequest, destinationRequest),
					destPeerName, routingEntry.getRoutingAction());
		} catch (CommunicationException e) { 
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Not performing Failover for Diameter Answer with Session-ID=" + 
												diameterSession.getSessionId() + ", Reason: "  + e.getMessage());
			}
			getLogger().trace(MODULE, e);
			sendAnswerInternal(originalRequest, destinationRequest, remoteAnswer, routingEntry, diameterSession, remoteAnswer, remoteHost);
		}
	
	}
	
	protected void postAnswerProcessing(DiameterAnswer finalPacket) {
		
	}

	/**
	 * 
	 * @param resultCode
	 * @param diameterAnswer
	 * @return true if we have to follow Redirect Indication from the Peer 
	 */
	private boolean isFollowRedirection(int resultCode, DiameterAnswer diameterAnswer, DiameterSession routingSession) {
		if(resultCode != ResultCode.DIAMETER_REDIRECT_INDICATION.code){
			return false;
		}

		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, ResultCode.DIAMETER_REDIRECT_INDICATION + 
			" received. Checking Redirect behaviour for Session-ID=" + 
			diameterAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID));

		String hostIdentity = diameterAnswer.getAVPValue(DiameterAVPConstants.ORIGIN_HOST);
		PeerData peerData = routerContext.getPeerData(hostIdentity);

		// Peer Data of origin host not found so checking if 
		// the peer, from which we received the answer, is known to us
		
		if(peerData == null){
			
			String destinationHost = (String)routingSession.getParameter(DiameterAVPConstants.DESTINATION_HOST);  
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Peer configuration for Peer: " + hostIdentity + 
				" not found, Obtaining Peer Configuration for Peer: "+ destinationHost);
			
			peerData = routerContext.getPeerData(destinationHost);
		}
		
		if (peerData == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Peer configuration for Peer: " + 
				hostIdentity + " not found, Not following Redirect indication");
			return false;			
		}

		if(peerData.isFollowRedirection() == false){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Follow Redirection for Peer: " + hostIdentity + " is disabled");
			return false;
		} 
		return true;
	}
	
	protected DiameterAnswer buildAnswer(DiameterRequest diameterRequest, 
			DiameterAnswer diameterAnswer,
			RoutingEntry routingEntry,
			DiameterSession routingSession, DiameterRequest translatedRequest) {
		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Building Diameter Answer for Session-Id=" + 
			diameterAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID));

		return diameterAnswer;
	}
	
	/**
	 *  This will perform Final Answer processing<br />
	 * --> Set HbH for Answer<br />
	 * 
	 * @param originRequest
	 * @param diameterAnswer
	 * @param routingSession 
	 * @param translatedReqeuest 
	 * @param routingEntry 
	 */
	private void finalAnswerProcessing(DiameterRequest originRequest, 
			DiameterAnswer originAnwser, 
			DiameterAnswer diameterAnswer, 
			DiameterSession routingSession, 
			DiameterRequest translatedReqeuest,
			String remoteHost,
			RoutingEntry routingEntry) {
		
		diameterAnswer.setHop_by_hopIdentifier(originRequest.getHop_by_hopIdentifier());
		diameterAnswer.setRequestReceivedTime(originRequest.creationTimeMillis());
		IDiameterAVP sessionIdAVP = diameterAnswer.getAVP(DiameterAVPConstants.SESSION_ID);
		String originSessionID = originRequest.getAVPValue(DiameterAVPConstants.SESSION_ID);
		
		if (sessionIdAVP == null) {
			sessionIdAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SESSION_ID);
			sessionIdAVP.setStringValue(originSessionID);
			diameterAnswer.addAvp(sessionIdAVP);
		}else{
			sessionIdAVP.setStringValue(originSessionID);
		}
		
		routerContext.postAnswerRouting(originRequest, translatedReqeuest, 
				originAnwser, diameterAnswer, remoteHost, 
				originRequest.getRequestingHost(),
				routingEntry.getRoutingEntryName());

	}
	
	/**
	 *  This will perform Final Answer processing<br />
	 * --> Set HbH for Answer<br />
	 * 
	 * @param originRequest
	 * @param diameterAnswer
	 * @param routingSession 
	 * @param destinationRequest 
	 */
	private void finalServerInitiatedAnswerProcessing(DiameterRequest originRequest, 
			DiameterAnswer originAnwser, 
			DiameterAnswer diameterAnswer, 
			DiameterSession routingSession, 
			DiameterRequest destinationRequest,
			RoutingEntry routingEntry) {
		
		diameterAnswer.setHop_by_hopIdentifier(originRequest.getHop_by_hopIdentifier());
		diameterAnswer.setRequestReceivedTime(originRequest.creationTimeMillis());
		IDiameterAVP sessionIdAVP = diameterAnswer.getAVP(DiameterAVPConstants.SESSION_ID);
		String originSessionID = originRequest.getAVPValue(DiameterAVPConstants.SESSION_ID);
		
		if (sessionIdAVP == null) {
			sessionIdAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SESSION_ID);
			sessionIdAVP.setStringValue(originSessionID);
			diameterAnswer.addAvp(sessionIdAVP);
		}else{
			sessionIdAVP.setStringValue(originSessionID);
		}
		
		routerContext.postAnswerRouting(originRequest, destinationRequest, 
				originAnwser, diameterAnswer, 
				(String)routingSession.getParameter(DiameterAVPConstants.ORIGIN_HOST), 
				originRequest.getRequestingHost(), 
				routingEntry.getRoutingEntryName());
	}
	
	/**
	 * Routes Server Initiated Request.
	 * @param diameterRequest
	 * @param session
	 * @throws RoutingFailedException 
	 */
	public void routeServerInitiatedRequest(final DiameterRequest originRequest,
			final DiameterSession session) throws RoutingFailedException {

		String sessionId = session.getSessionId();
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Routing initiated for Server Initiated Request with Session-Id=" + sessionId);
		}

		locateSession(originRequest);
		
		final RoutingEntry routingEntry = routerContext.getRoutingEntry((String)session.getParameter(ROUTING_ENTRY));

		final DiameterRequest destinationRequest = buildRequest(null,originRequest,routingEntry);
		/*
		 * SessionId AVP is added just to remove translation fail scenario
		 */
		if (destinationRequest.getAVPValue(DiameterAVPConstants.SESSION_ID) == null) {
			IDiameterAVP diameterAVP = DiameterUtility.createAvp(
					DiameterAVPConstants.SESSION_ID, sessionId);
			destinationRequest.addAvp(diameterAVP);
		}
		
		String destHost;
		if (destinationRequest.getParameter(TranslatorConstants.DUMMY_MAPPING) != null) {
			destHost = routerContext.getVirtualRoutingPeerName();
			
		} else {
			
			destHost = (String) session.getParameter(DiameterAVPConstants.ORIGIN_HOST);
			
			if (destHost == null) {
				throw new RoutingFailedException(ResultCode.DIAMETER_PEER_NOT_FOUND, 
						routingEntry.getRoutingAction(),
						DiameterErrorMessageConstants.DEST_PEER_NOT_FOUND);
			}
			
			DiameterPeerCommunicator destPeerComm = routerContext.getPeerCommunicator(destHost);
			if(destPeerComm.isAlive() == false){

				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "Sending "+ ResultCode.DIAMETER_UNABLE_TO_DELIVER +
							" for Session-Id=" + sessionId + 
							", Reason: Peer: " + destHost + " is Down.");
				}
				throw new RoutingFailedException(ResultCode.DIAMETER_UNABLE_TO_DELIVER, 
						routingEntry.getRoutingAction(),
						DiameterErrorMessageConstants.DEST_PEER_NOT_FOUND);
			}
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Session's Destination host: " + destHost + 
						" selected for Session-ID=" + sessionId);
			}
		}

		try {
			addRouteRecoredAVP(originRequest, destinationRequest);
			postRequestProcessing(destinationRequest);
			finalServerInitiatedRequestProcessing(session, originRequest, destinationRequest, routingEntry);
			sendServerInitiatedRequest(session, destinationRequest, new ResponseListener() {
				
				@Override
				public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession diameterSession) {
					routerContext.updateRealmInputStatistics(diameterAnswer, 
							routerContext.getPeerData(hostIdentity).getRealmName(),
							routingEntry.getRoutingAction());
					routeServerInitiatedAnswer(diameterAnswer, originRequest, destinationRequest, session, routingEntry);
				}
				
				@Override
				public void requestTimedout(String hostIdentity, DiameterSession diameterSession) {

					DiameterAnswer diameterAnswer = new DiameterAnswer(destinationRequest,
							ResultCode.DIAMETER_REQUEST_TIMEOUT);
					DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, diameterAnswer,
							DiameterErrorMessageConstants.REQUEST_TIMEOUT);
					
					routerContext.updateRealmTimeoutRequestStatistics(destinationRequest,
							routerContext.getPeerData(hostIdentity).getRealmName(), routingEntry.getRoutingAction());
					/*
					 * No need to provide diameter session, as Router will fetch
					 * diameterSession from routingSession.
					 */
					routeServerInitiatedAnswer(diameterAnswer, originRequest, destinationRequest, session, routingEntry);

				}
			}, destHost, routingEntry.getRoutingAction());
			
		} catch(CommunicationException e) { 
			getLogger().trace(MODULE, e);
			throw new RoutingFailedException(ResultCode.DIAMETER_UNABLE_TO_COMPLY, 
					routingEntry.getRoutingAction(), e.getMessage());
		}

	}

	/**
	 * Routes Server Initiated Answer
	 * 
	 * @param remoteAnswer
	 * @param diameterSession
	 * @param routingEntry
	 */
	private void routeServerInitiatedAnswer(DiameterAnswer remoteAnswer,
			DiameterRequest originalRequest,
			DiameterRequest remoteRequest,
			DiameterSession diameterSession, RoutingEntry routingEntry) {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Routing initiated for Diameter Answer with Session-ID=" + diameterSession.getSessionId());
		}

		DiameterAnswer originAnswer = null;
		try {
			originAnswer = (DiameterAnswer) remoteAnswer.clone();
		} catch (CloneNotSupportedException e) {
			LogManager.getLogger().trace(MODULE, e);
			originAnswer = remoteAnswer;
		}
		routerContext.preAnswerRouting(originalRequest, remoteRequest
				, originAnswer, originalRequest.getRequestingHost(), routingEntry.getRoutingEntryName());

		
		DiameterAnswer finalAnswer = buildAnswer(originalRequest, remoteAnswer, routingEntry, diameterSession, remoteRequest);
		postAnswerProcessing(finalAnswer);
		boolean releaseSession = sessionReleaseIndiactor.isEligible(finalAnswer);
		if (releaseSession == false) {
			updateOrSaveSession(originalRequest, finalAnswer);
		}
		finalServerInitiatedAnswerProcessing(originalRequest, remoteAnswer, finalAnswer, diameterSession, remoteRequest, routingEntry);
		try {
			sendAnswer(diameterSession, originalRequest, finalAnswer, originalRequest.getRequestingHost(), routingEntry.getRoutingAction());
		} catch (CommunicationException e) {
			LogManager.getLogger().error(MODULE, "Unable to send diameter answer for request with HbH-ID=" 
					+ originalRequest.getHop_by_hopIdentifier() + "EtE-ID=" 
					+ originalRequest.getEnd_to_endIdentifier() + ", Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	protected final void locateSession(DiameterRequest diameterRequest) {
		if (diameterSessionManager == null) {
			return;
		}
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Session location for Diameter Packet with HbH-ID = " 
						+ diameterRequest.getHop_by_hopIdentifier() + " and EtE-ID = " + diameterRequest.getEnd_to_endIdentifier() 
						+ " has started");
		}

		List<SessionData> locatedSessionData = diameterSessionManager.locate(diameterRequest, null);
		if(locatedSessionData != null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, locatedSessionData.size() + " session(s) located.");
			}
		}
		diameterRequest.setLocatedSessionData(locatedSessionData);
	}
	
	protected final void updateOrSaveSession(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer){
		if(diameterSessionManager == null){
			return;
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Session updation/save for Diameter Request with HbH-ID=" 
						+ diameterRequest.getHop_by_hopIdentifier() + " and EtE-ID=" + diameterRequest.getEnd_to_endIdentifier() 
						+ " has started");
		}
		diameterSessionManager.updateOrSave(diameterRequest, diameterAnswer, diameterRequest.getLocatedSessionData());
	}
	
	protected final void releaseSession(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer, DiameterSession diameterSession){
		if(diameterSessionManager != null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Session deletion for Diameter Packet with HbH-ID=" 
							+ diameterRequest.getHop_by_hopIdentifier() + " and EtE-ID=" + diameterRequest.getEnd_to_endIdentifier() 
							+ " has started");
			}
			diameterSessionManager.delete(diameterRequest, diameterAnswer);
		}
		diameterSession.release();
	}
}
