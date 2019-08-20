package com.elitecore.diameterapi.diameter.common.routerx.agent;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.common.router.exception.RoutingFailedException;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.peers.PeerSelector;
import com.elitecore.diameterapi.diameter.common.routerx.RouterContext;
import com.elitecore.diameterapi.diameter.common.routerx.RoutingEntry;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;
import com.elitecore.diameterapi.diameter.stack.application.sessionrelease.SessionReleaseIndicatorFactory;

/**
 * Redirect agents do not relay messages, and only return an
 * answer with the information necessary for Diameter agents to
 * communicate directly.
 */

 /*
  *    +------+   			 --------->        +-------+
  *    | Dia  |    			1. Request         | Elite |
  *    | Node |                                | DRD   |
  *    |      |    2. Redirect Notification    |       |
  *    +------+    			<---------         +-------+
  *    
  *				Redirecting Diameter Request
  */
public class RedirectAgent extends DiameterAgent {

	private static final String MODULE = "REDIRECT-AGNT";
	private SessionReleaseIndiactor sessionReleaseIndiactor;

	public RedirectAgent(RouterContext diameterRouterContext) {
		super(diameterRouterContext);
		sessionReleaseIndiactor = SessionReleaseIndicatorFactory.getDefaultSessionReleaseIndiactor();
	}

	@Override
	public void routeRequest(DiameterRequest diameterRequest,
			DiameterSession diameterSession, RoutingEntry routingEntry) throws RoutingFailedException {
		
		String sessionId = diameterSession.getSessionId();
		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Redirecting Diameter Request with Session-Id=" + sessionId);
		
		PeerSelector peerSelector = routingEntry.getPeerSelector();
		if(peerSelector == null){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER + 
				" for Session-ID=" + sessionId + ", Reason: No Redirect Peer Group found.");
			
			throw new RoutingFailedException(ResultCode.DIAMETER_UNABLE_TO_DELIVER, RoutingActions.REDIRECT, 
					DiameterErrorMessageConstants.REDIRECT_PEER_NOT_FOUND);
		}
		
		String redirectHost = peerSelector.selectNextPeer(diameterRequest);
		String secondaryRedirectHost = peerSelector.selectSecondaryPeer(diameterRequest, redirectHost);
		
		if(redirectHost == null){
			if(secondaryRedirectHost == null){
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER + 
					" for Session-ID=" + sessionId + ", Reason: No redirection host found.");
				
				throw new RoutingFailedException(ResultCode.DIAMETER_UNABLE_TO_DELIVER, RoutingActions.REDIRECT, 
						DiameterErrorMessageConstants.REDIRECT_PEER_NOT_FOUND);
			}else{
				redirectHost = secondaryRedirectHost;
				secondaryRedirectHost = null;
			}
		}
		boolean bRedirectHostAdded = false;
		DiameterAnswer answer = new DiameterAnswer(diameterRequest, ResultCode.DIAMETER_REDIRECT_INDICATION);

		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Redirection host " + redirectHost + 
			" selected for session-ID="+ sessionId);
		IDiameterAVP redirectHostAVP = getRedirectionHostAVP(redirectHost);
		if (redirectHostAVP != null) {
			answer.addAvp(redirectHostAVP);
			bRedirectHostAdded = true;
		}
		if (secondaryRedirectHost != null) {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Redirection host " + secondaryRedirectHost + 
				" selected for session-ID="+ sessionId);
			
			IDiameterAVP secRedirectHostAVP = getRedirectionHostAVP(secondaryRedirectHost);
			if (redirectHostAVP != null) {
				answer.addAvp(secRedirectHostAVP);
				bRedirectHostAdded = true;
			}
		}
		if (bRedirectHostAdded == false) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Sending " + ResultCode.DIAMETER_UNABLE_TO_COMPLY + 
				" for Session-ID=" + sessionId + ", Reason: No redirection host could be resolved.");
			
			throw new RoutingFailedException(ResultCode.DIAMETER_UNABLE_TO_COMPLY, RoutingActions.REDIRECT, 
					"No redirection host could be resolved for Session-ID=" + sessionId);
		}
		String translationMapp = routingEntry.getTranslationMapping();
		if(translationMapp != null && translationMapp.trim().length() > 0){
			answer = buildAnswer(translationMapp, answer, diameterSession, diameterRequest);
		}

		try {
			sendAnswer(diameterSession, diameterRequest, answer, diameterRequest.getRequestingHost(), routingEntry.getRoutingAction());
		} catch (CommunicationException e) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Dropping Diameter Answer with Session-ID=" + 
						diameterSession.getSessionId() + ", Reason: "  + e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
			routerContext.updateDiameterStatsPacketDroppedStatistics(
					answer, diameterRequest.getRequestingHost(),
					diameterRequest.getPeerData().getRealmName(),
					routingEntry.getRoutingAction());
		}
		
		if(sessionReleaseIndiactor.isEligible(answer)) {
			diameterSession.release();
		}
	}

	private IDiameterAVP getRedirectionHostAVP(String redirectHostName) {
		PeerData redirectPeerData = routerContext.getPeerData(redirectHostName);
		if(redirectPeerData == null){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Peer configuration for Redirect host " + 
				redirectHostName + " not found.");
			return null;
		}
		String redirectHostVal = redirectPeerData.getURI();

		if(redirectHostVal == null){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "DiameterURI not found for Peer " + redirectHostName);
			return null;
		}

		IDiameterAVP redirectHostAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.REDIRECT_HOST);
		if(redirectHostAVP != null){
			redirectHostAVP.setStringValue(redirectHostVal);
		}else{
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, DiameterAVPConstants.REDIRECT_HOST + " AVP not found in Diameter Dictionary");
		}
		return redirectHostAVP;
	}
	
	private DiameterAnswer buildAnswer(String translatorName, DiameterAnswer diameterAnswer, 
			DiameterSession diameterSession, DiameterRequest originRequest) throws RoutingFailedException{
		
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Translating packet before sending Redirect Indication using translation policy: " 
			+ translatorName);
		
		TranslatorParams translatorParams = new TranslatorParamsImpl(diameterAnswer, diameterAnswer, originRequest, null);
		translatorParams.setParam(TranslatorConstants.DIAMETER_SESSION, diameterSession);

		try {
			TranslationAgent.getInstance().translate(translatorName, translatorParams, diameterAnswer.isRequest());
			diameterAnswer = (DiameterAnswer) translatorParams.getParam(TranslatorConstants.TO_PACKET);
		} catch (TranslationFailedException e) {
			LogManager.getLogger().trace(MODULE, e);
			throw new RoutingFailedException(RoutingActions.REDIRECT, 
					DiameterErrorMessageConstants.translationFailed(translatorName));
		}
		return diameterAnswer;
	}
}
